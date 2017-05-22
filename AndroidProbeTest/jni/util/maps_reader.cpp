/*
 * mapsreader.cpp
 *
 *  Created on: 2015. 7. 8.
 *      Author: purehero2
 */

#include "maps_reader.h"
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <util/log.h>

#define READ_BUFF_SIZE 			4096	// 4K

maps_reader_impl::maps_reader_impl( int pID )
: process_id( pID )
{
}

maps_reader_impl::~maps_reader_impl()
{
}

int maps_reader_impl::read()
{
	LOGT();
	maps_infos.clear();

	char mapsPath[64];
	sprintf( mapsPath, "/proc/%d/maps", process_id );

	FILE * mapsFp = fopen ( mapsPath, "rb" );
	if( mapsFp == NULL ) return -1;

	maps_info info;

	char buff[1024];
	while( fgets( buff, 1024, mapsFp ) != NULL )
	{
		memset( &info, 0, sizeof( maps_info ));
		sscanf( buff,"%llx-%llx %s %llx %s %d %s",&info.startAddr,&info.endAddr,(char*)&info.permission,&info.unknown1,(char*)&info.unknown2,&info.unknown3,(char*)&info.filename);

		maps_infos.push_back( info );
	}
	fclose( mapsFp );

	return (int) maps_infos.size();
}


const maps_info * maps_reader_impl::search( char * name )
{
	for( std::vector<maps_info>::iterator it = maps_infos.begin(); it != maps_infos.end(); it++ )
	{
		if( strstr( it->filename, name ) != NULL )
		{
			return &(*it);
		}
	}
	return NULL;
}

int maps_reader_impl::search( char * name, std::vector<maps_info> & result )
{
	result.clear();
	for( std::vector<maps_info>::iterator it = maps_infos.begin(); it != maps_infos.end(); it++ )
	{
		if( strstr( it->filename, name ) != NULL )
		{
			result.push_back(*it);
		}
	}
	return (int) result.size();
}

void maps_reader_impl::print( const maps_info * info )
{
	LOGI("%08llx-%08llx %s %llx %s %d %s\n",info->startAddr,info->endAddr,info->permission,info->unknown1,info->unknown2,info->unknown3,info->filename);
}

void maps_reader_impl::print_all()
{
	LOGI( "==============================================================================================" );
	LOGI( " MAPS READER PRINT ALL");
	LOGI( "==============================================================================================" );
	for( std::vector<maps_info>::iterator it = maps_infos.begin(); it != maps_infos.end(); it++ )
	{
		print( &(*it) );
	}
	LOGI( "==============================================================================================" );
}


maps_reader::maps_reader( int pID ) : maps_reader_impl(pID){ }
maps_reader::~maps_reader(){}
int maps_reader::read()
{
	LOGT();

	maps_infos.clear();

	char mapsPath[64];
	sprintf( mapsPath, "/proc/%d/maps", process_id );

	std::vector<char> file_buff;
	char strTemp[READ_BUFF_SIZE];	// 4K

	//LOGI( "trying to read file '%s'", mapsPath );

	// maps 파일을 open 합니다.
	int fd = ::open( mapsPath, O_RDONLY );
	if( fd == -1 )
	{
		LOGI( "Failed to open file '%s'", mapsPath );
		return -1;
	}

	//LOGI( "success open file '%s' %d", mapsPath, fd );

	// maps 파일의 내용을 모두 읽어 옵니다.
	int nRead = ::read( fd, strTemp, READ_BUFF_SIZE );
	while( nRead > 0 )
	{
		//LOGI( "%d byte readed", nRead );

		file_buff.insert( file_buff.end(), &strTemp[0], &strTemp[nRead] );
		nRead = ::read( fd, strTemp, READ_BUFF_SIZE );
	}
	::close( fd );	// 내용을 모두 읽었으니 파일을 close 합니다.

	// 읽어온 maps 파일 내용을 파싱합니다.
	char * token = strtok(( char * ) &file_buff[0], "\r\n" );

	maps_info info;
	while( token != NULL )
	{
		//while( *token == ' ' || *token == '\r' || *token == '\n' ) token++;
		sscanf( token, "%llx-%llx %s %llx %s %d %s",&info.startAddr,&info.endAddr,(char*)&info.permission,&info.unknown1,(char*)&info.unknown2,&info.unknown3,(char*)&info.filename);
		maps_infos.push_back( info );

		//LOGI( "%s",  token );
		//LOGI( "%x-%x",  info.startAddr, info.endAddr );
		token = strtok( NULL, "\r\n" );
	}

	return (int) maps_infos.size();
}
