/*
 * got_hook.cpp
 *
 *  Created on: 2017. 2. 22.
 *      Author: MY
 */

#include "got_hook.h"

 #include <sys/types.h>
#include <unistd.h>

#include "elf_file.h"
#include "../alog.h"
#include "plt_hooking.h"

got_hook::got_hook() {
	// TODO Auto-generated constructor stub

}

got_hook::~got_hook() {
	// TODO Auto-generated destructor stub
}

typedef struct _mapsinfo{
	char permission[5];
	char unknown2[6];
	int unknown3;
	char filename[128];
	unsigned long startAddr,endAddr;
	unsigned long unknown1;
} mapsInfo;

#include <stdlib.h>
bool findMapsInfo( mapsInfo & info, unsigned long funcAddress ) {
	bool ret = false;

	char temp[512];
	sprintf( temp, "/proc/%d/maps", getpid());
	FILE* fp = fopen( temp, "r");
	if (fp != NULL) {
		while(fgets( temp, 512, fp) != NULL) {
			memset( &info, sizeof(mapsInfo), 0 );
			sscanf( temp, "%08lx-%08lx %s %08lx %s %d %s", &info.startAddr, &info.endAddr,(char*)&info.permission,&info.unknown1,(char*)&info.unknown2,&info.unknown3,(char*)&info.filename);

			if( info.startAddr == 0 ) continue;
			if( info.startAddr < funcAddress && funcAddress < info.endAddr ) {
//				LOGD( "Found %08lx-%08lx %s %s", info.startAddr, info.endAddr, info.filename, info.permission );
				ret = true;
				break;
			}
		}
		fclose(fp);
	}
	return ret;
}

void * got_hook::hooking( const char * fname, void * original_function, void * target_function ) {
	LOGT();

	void * ret = try_to_got_hooking( fname, original_function, target_function );
	if( ret != NULL ) return ret;

	ret = try_to_plt_hooking( fname, original_function, target_function );
	if( ret != NULL ) return ret;

	return NULL;
}

void * got_hook::try_to_got_hooking( const char * fname, void * original_function, void * target_function ) {
	mapsInfo info;
	if( !findMapsInfo( info, ( unsigned long ) original_function )) {
		return NULL;
	}

	LOGD( "[GOT HOOK]Found module '%s' -> %s : 0x%08x", fname, info.filename, info.startAddr );

	elf_file elf( info.startAddr, info.filename );
	return elf.hook( original_function, target_function );
}


void * got_hook::try_to_plt_hooking( const char * fname, void * original_function, void * target_function ) {
	//LOGD( "[PLT HOOK]" );

	plt_hooking hk;
	return  ( void * ) hk.try_hooking( fname, (unsigned) target_function );
	//return NULL;
}
