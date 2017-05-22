/*
 * probe.cpp
 *
 *  Created on: 2015. 7. 7.
 *      Author: purehero2
 */

#include "probe.h"
#include "probe_lua.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <netdb.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include <unistd.h>

probe::probe()
{
	LOGT();

#ifdef __i386__
	LOGE( "defined __i386__\n" );
#endif

#ifdef __arm__
	LOGE( "defined __arm__\n" );
#endif

#ifdef _MIPS_ARCH
	LOGE( "defined ___MIPS_ARCH__\n" );
#endif

	lua.init();
}

probe::~probe()
{
	LOGT();

	lua.release();
}

void probe::run( int port, char * pProbe, int len )
{
	LOGT();

	int s_id = connection_to_parent( port );
	if( s_id < 0 ) return;

	if( self_hash_check( s_id, pProbe, len ) < 0 )
	{
		LOGE( "ERROR : probe self hash check");
		return;
	}

	if( execute_lua_script( s_id ) < 0 )
	{
		LOGE( "ERROR : probe execute lua script");
		return;
	}

	close( s_id );
	LOGD( "SUCCESS close socket" );

	LOGD( "SUCCESS : probe run" );
}

int probe::connection_to_parent( int port )
{
	LOGT();

	int sockfd = socket( AF_INET, SOCK_STREAM, 0 );
	if( sockfd < 0 )
	{
		LOGE( "ERROR opening socket" );
		return sockfd;
	}

	struct sockaddr_in server_addr;
	int addr_len = sizeof( server_addr );

	memset( &server_addr, 0, addr_len );
	server_addr.sin_family = AF_INET;
	server_addr.sin_addr.s_addr = inet_addr("127.0.0.1");
	server_addr.sin_port = htons( port );

	if( connect( sockfd, (struct sockaddr *) &server_addr, addr_len ) < 0 )
	{
		LOGE( "ERROR connection socket" );
		return -1;
	}

	return sockfd;
}

int probe::self_hash_check( int sockfd, char * pProbe, int probe_len )
{
	LOGT();

	int n_write, pos = 0;
	int len = probe_len;

	char strSize[10];
	sprintf( strSize, "%10d", len );

	write( sockfd, strSize, 10 );	// 보낸 전체 데이터의 크기를 10byte 문자열로 먼저 보낸다.
	while( len > 0 )
	{
		n_write = ( int ) write( sockfd, ( const void * )( pProbe + pos ), len );
		if( n_write <= 0 ) break;

		LOGD( "Writted : %dbyte", n_write );
		pos += n_write;
		len -= n_write;
	}

	LOGD( "Total writted : (%d/%d)byte", pos, probe_len );
	return pos == probe_len ? pos : -1;
}

int probe::execute_lua_script( int sockfd )
{
	LOGT();

	char recv_buff[16*1024];		// 16Kbyte buff
	int nRead = read( sockfd, recv_buff, 10 );	// 수신할 전체 데이터의 길이를 먼저 받아 온다.
	if( nRead < 1 ) return -1;

	recv_buff[nRead] = 0;
	LOGD( "Recieved size data : %s", recv_buff );

	int nSize = atoi( recv_buff );
	LOGD( "size value : %d", nSize );
	if( nSize < 0 )
	{
		LOGE( "Error : invalid size value" );
		return -10000;
	}

	if( nSize > 16*1024 ) {
		LOGE( "Error : invalid size value" );
		return -10001;
	}

	int pos = 0;
	while( nSize > 0 )
	{
		nRead = read( sockfd, &recv_buff[pos], nSize );
		LOGD( "read bytes : %d", nRead );

		pos   += nRead;
		nSize -= nRead;
	}
	recv_buff[pos] = 0;

	LOGD( "Excute lua script" );

	lua.excute( recv_buff, pos );
	return pos;
}
