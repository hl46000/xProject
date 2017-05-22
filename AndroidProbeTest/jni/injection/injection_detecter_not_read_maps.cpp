/*
 * injection_detecter_not_read_maps.cpp
 *
 *  Created on: 2015. 10. 6.
 *      Author: purehero2
 */

#include <injection/injection_detecter_not_read_maps.h>

#include <dlfcn.h>
#include <errno.h>
#include <stdlib.h>

#include <sys/stat.h>
#include <sys/types.h>
#include <fcntl.h>
#include <unistd.h>

injection_detecter_not_read_maps::injection_detecter_not_read_maps() {}
injection_detecter_not_read_maps::~injection_detecter_not_read_maps() {}
int injection_detecter_not_read_maps::get_target_module( int pid ) { return 1; }

void injection_detecter_not_read_maps::working()
{
	mkdir( "/mnt/sdcard/sym_infos", 0755 );

	char strTemp[256];
	char filename[128];

	soinfo *si = (struct soinfo *)dlopen( "/system/bin/app_process", RTLD_GLOBAL );
	if( si == NULL ) {
		print_log( "'/system/bin/app_process' dlopen error: %s.", dlerror() );
		return;
	}

	soinfo * do_si = si;
	while( do_si != NULL ) {
		sprintf( strTemp, "/mnt/sdcard/sym_infos/%s", do_si->name );
		log_fp = fopen( strTemp, "wb");
		if( log_fp == NULL ) {
			print_log( "'%s' File create error", strTemp );
			continue;
		}

		print_log( "\r\nTrying to '%s' file %08llx", do_si->name, do_si->base );

		just_do_it( do_si, NULL );
		fclose( log_fp );

		do_si = do_si->next;
	}

	dlclose( si );
}
