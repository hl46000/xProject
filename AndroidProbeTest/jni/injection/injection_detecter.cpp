/*
 * injection_detecter.cpp
 *
 *  Created on: 2015. 10. 2.
 *      Author: purehero2
 */

#include <injection/injection_detecter.h>

#include <dlfcn.h>
#include <errno.h>
#include <stdlib.h>

#include <sys/stat.h>
#include <sys/types.h>
#include <fcntl.h>
#include <unistd.h>

#include <map>

#include "util/log.h"

injection_detecter::injection_detecter() {
}

injection_detecter::~injection_detecter() {
}

#if 0
void injection_detecter::print_log( const char *fmt, ... )
{
	char buf[1024] = {0,};
	va_list ap;

	va_start( ap, fmt );
	vsprintf( buf, fmt, ap );
	va_end(ap);

	LOGD( "%s", buf );
}
#endif

void injection_detecter::working()
{
	mkdir( "/mnt/sdcard/sym_infos", 0755 );

	char strTemp[256];
	char filename[128];

	soinfo *si = NULL;
	for( std::vector<maps_info>::iterator it = m_maps_infos.begin(); it != m_maps_infos.end(); it++ ) {
		//if( *((char*)( it->s_add + 1)) != 'E' ) continue;

		strcpy( filename, it->filename );
		int len = strlen( filename );
		for( int i = 0; i < len; i++ ) {
			if( filename[i] == '/' || filename[i] == '\\' || filename[i] == '.' ) {
				filename[i] = '_';
			}
		}
		sprintf( strTemp, "/mnt/sdcard/sym_infos/%s", filename );
		log_fp = fopen( strTemp, "wb");
		if( log_fp == NULL ) {
			print_log( "'%s' File create error", strTemp );
			continue;
		}

		print_log( "\r\nTrying to '%s' file %08llx-%08llx", filename, it->startAddr, it->endAddr );

		if(( si = (struct soinfo *)dlopen( it->filename, 0 /* RTLD_NOLOAD */ )) == NULL) {
			print_log( "'%s' dlopen error: %s.", it->filename, dlerror() );
			continue;
		}

		just_do_it( si, NULL );
		fclose( log_fp );

		dlclose( si );
		si = NULL;
	}
}

