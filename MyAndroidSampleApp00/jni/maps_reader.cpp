/*
 * mapsreader.cpp
 *
 *  Created on: 2017. 1. 26.
 *      Author: purehero
 */

#include "maps_reader.h"
#include <string.h>
#include "alog.h"

maps_reader::maps_reader( int _pid ) : pid( _pid ) {
}

maps_reader::~maps_reader() {
}


int maps_reader::read() {
	char strTemp[2048];
	sprintf( strTemp, "/proc/%d/maps", pid );

	LOGD( "maps_reader => file open '%s'", strTemp );

	maps_infos.clear();

	FILE * mapsFp = fopen ( strTemp, "rb" );
	if( mapsFp != NULL ) {

		mapsInfo info;
		while( fgets( strTemp, sizeof(strTemp), mapsFp ) != NULL ){
			memset( &info, sizeof(mapsInfo), 0 );

			sscanf( strTemp, "%08lx-%08lx %s %08lx %s %d %s", &info.startAddr, &info.endAddr,(char*)&info.permission,&info.unknown1,(char*)&info.unknown2,&info.unknown3,(char*)&info.filename);
			maps_infos.push_back( info );
		}
		fclose(mapsFp);

	} else{
		LOGE( "%s", "maps_reader => file open fail" );
	}

	return maps_infos.size();
}

const mapsInfo * maps_reader::get( int idx ) {
	return &maps_infos[idx];
}

void maps_reader::print( const mapsInfo * pMapsInfo ) {
	if( pMapsInfo != NULL ) {
		LOGI("%08lx-%08lx %s %08lx %s %d %s\n",pMapsInfo->startAddr,pMapsInfo->endAddr,pMapsInfo->permission,pMapsInfo->unknown1,pMapsInfo->unknown2,pMapsInfo->unknown3,pMapsInfo->filename);
	} else {
		for( auto it = maps_infos.cbegin(); it != maps_infos.cend(); ++it ) {
			const mapsInfo * pMapsInfo = &(*it);
			LOGI("%08lx-%08lx %s %08lx %s %d %s\n",pMapsInfo->startAddr,pMapsInfo->endAddr,pMapsInfo->permission,pMapsInfo->unknown1,pMapsInfo->unknown2,pMapsInfo->unknown3,pMapsInfo->filename);
		}
	}
}
