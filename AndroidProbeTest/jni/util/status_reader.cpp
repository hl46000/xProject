/*
 * status_reader.cpp
 *
 *  Created on: 2015. 8. 27.
 *      Author: purehero2
 */

#include "status_reader.h"
#include "util/log.h"

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdio.h>

#define READ_BUFF_SIZE		1024

status_reader::status_reader( int pID )
: process_id( pID )
{
}

status_reader::~status_reader() {
}

int status_reader::read()
{
	_buff.clear();

	char strTemp[READ_BUFF_SIZE];
	sprintf( strTemp, "/proc/%d/status", process_id );

	int fd = ::open( strTemp, O_RDONLY );
	if( fd == -1 ) return -2;

	int nRead = ::read( fd, strTemp, READ_BUFF_SIZE );
	while( nRead > 0 )
	{
		_buff.insert( _buff.end(), &strTemp[0], &strTemp[nRead] );
		nRead = ::read( fd, strTemp, READ_BUFF_SIZE );
	}
	::close( fd );	// 내용을 모두 읽었으니 파일을 close 합니다.

	return (int)_buff.size();
}

const char * status_reader::search( const char * key )
{
	char * value = strstr(( char * ) &_buff[0], key );
	if( value == NULL )
	{
		LOGI( "Failed to searching field name [%s]", key );
		return NULL;
	}

	return ( const char * )( value + strlen( key ) + 1 );
}
