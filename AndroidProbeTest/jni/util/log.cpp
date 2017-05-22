/*
 * probelog.cpp
 *
 *  Created on: 2015. 7. 7.
 *      Author: purehero2
 */

#include <string.h>
#include <stdio.h>
#include "log.h"

char logTagBuffer[128] = { 'p','r','o','b','e','_','l','o','g', 0 };

const char * get_logTag()
{
	return logTagBuffer;
}

void change_logTag( const char * logTag )
{
	LOGT();

	strcpy( logTagBuffer, logTag );
}

__attribute__((constructor(101))) void init_logTag()
{
	LOGT();
	//strcpy( logTagBuffer, "SampleApp" );
	strcpy( logTagBuffer, "ash" );
}

void print_address( const char * title, const unsigned char * pAddr, int len )
{

	LOGD( "===============================================================================" );
	if( title != NULL ) LOGD( " '%s' address [%d bytes]", title, len );
	LOGD( "===============================================================================" );

	int i = 0;
	int nCnt = len / 16;
	for( int idx = 0; idx < nCnt; idx++, i+= 16 )
	{
		LOGD( "0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x",
			pAddr[i],   pAddr[i+1],  pAddr[i+2],  pAddr[i+3],  pAddr[i+4],  pAddr[i+5],  pAddr[i+6], pAddr[i+7],
			pAddr[i+8], pAddr[i+9], pAddr[i+10], pAddr[i+11], pAddr[i+12], pAddr[i+13], pAddr[i+14], pAddr[i+15] );
	}

	char strTemp[128], temp[32];

	if( i < len )
	{
		sprintf( strTemp, "0x%02x", pAddr[i++] );
		for( ; i < len; i++ )
		{
			sprintf( temp, " 0x%02x", pAddr[i] );
			strcat( strTemp, temp );
		}

		LOGD( "%s", strTemp );
	}


	LOGD( "===============================================================================" );
}
