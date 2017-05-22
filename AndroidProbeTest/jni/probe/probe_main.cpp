#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "probe.h"
#include "util/maps_reader.h"

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include "smc_module.h"

void test()
{
	LOGT();

	char strTemp[64];
	char strBuff[10240];

	sprintf( strTemp, "/proc/%d/maps", getpid());

	int fd = open( strTemp, O_RDONLY );
	if( fd == -1 )
	{
		perror("open");
		return;
	}

	long size = lseek( fd, 0, SEEK_END );
	lseek( fd, 0, SEEK_SET );

	int nRead = read( fd, strBuff, 10240 );

	LOGD( "01. %s file size = %ld bytes %d", strTemp, size, nRead );
	close( fd );

	struct stat stat_buff;
	stat( strTemp, &stat_buff );

	LOGD( "02. %s file size = %lld bytes %lu %llu", strTemp, stat_buff.st_size, stat_buff.st_blksize, stat_buff.st_blocks );


	LOGD( "==========================================================================" );
	char * token = strtok( strBuff, "\r\n");
	while( token != NULL )
	{
		LOGD( "%s", token );
		token = strtok( NULL, "\r\n");
	}
	LOGD( "==========================================================================" );

#if 0
	char * strTemp = "17a510c4-a55e-5e50-0dbba75f-414b5386.dmp.2.0.1_OH13A.dmp";
	//char * strTemp = "17a510c4-a55e-5e50-0dbba75f-414b5386.dmp";
	// 위의 파일명에서 2.0.1_OH13A 값만을 취하는 로직을 구현한다.

	LOGD( "strTemp = %s", strTemp );

	char * strFound = strstr( strTemp, ".dmp." );
	if( strFound != NULL )
	{
		LOGD( "strFound = %s", strFound );
		strFound += 5;

		char strVersion[32]; memset( strVersion, 0, sizeof( strVersion ));
		strncpy( strVersion, strFound, strlen( strFound ) - 4 );

		LOGD( "strVersion = %s", strVersion );
	}
	else
	{
		LOGD( "strNotFound = .dmp." );
	}

	LOGD( "strTemp = %s", strTemp );
#endif
}

int main( int argc, char * argv[] )
{
	LOGT();

	//test();

	SMC_START_TAG( _main );

	if( argc < 2 ) return 0;

	int server_port = atoi( argv[1] );
	LOGD( "server_port : %d", server_port );

	char * pProbe = NULL;
	int nProbeLen = 0;

	maps_reader reader( getpid());
	if( reader.read() > 0 )
	{
		reader.print_all();

		std::vector<maps_info> search_infos;
		if( 0 < reader.search( argv[0], search_infos ))
		{
			for( std::vector<maps_info>::iterator it = search_infos.begin(); it != search_infos.end(); it ++ )
			{
				if( it->permission[0] != 'r' || it->permission[2] != 'x' ) continue;

				pProbe 		= ( char * ) it->startAddr;
				nProbeLen 	= ( int )( it->endAddr - it->startAddr );

				LOGD( "nProbeLen = %d", nProbeLen );

				//reader.print( &(*it) );
				break;
			}
		}
	}

	if( pProbe != NULL )
	{
		probe p;
		p.run( server_port, pProbe, nProbeLen );
	}

	SMC_END_TAG( _main );

	return 0;
}

