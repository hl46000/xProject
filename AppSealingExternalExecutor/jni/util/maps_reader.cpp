/*
 * mapsreader.cpp
 *
 *  Created on: 2017. 1. 26.
 *      Author: purehero
 */

#include "maps_reader.h"
#include <string.h>

#include <memory>
#include "../jni_helper.h"

maps_reader::maps_reader( int _pid ) : pid( _pid ) {
	LOGD( "mapsInfo struct size : %d", sizeof(mapsInfo));
}

maps_reader::~maps_reader() {
}


int maps_reader::read() {
	//char strTemp[2048];
	std::unique_ptr<char[]> strTemp( new char[2048]);
	sprintf( strTemp.get(), "/proc/%d/maps", pid );

	LOGD( "maps_reader => file open '%s'", strTemp.get() );

	maps_infos.clear();

	FILE * mapsFp = fopen ( strTemp.get(), "rb" );
	if( mapsFp != NULL ) {
		unsigned long unknown1;
		char unknown2[6];
		int unknown3;

		mapsInfo info;
		while( fgets( strTemp.get(), 2048, mapsFp ) != NULL ){
			memset( &info, sizeof(mapsInfo), 0 );

			sscanf( strTemp.get(), "%08lx-%08lx %s %08lx %s %d %s", &info.startAddr, &info.endAddr,(char*)&info.permission,&unknown1,(char*)&unknown2,&unknown3,(char*)&info.filename);
			maps_infos.push_back( info );
		}
		fclose(mapsFp);

	} else{
		LOGE( "%s", "maps_reader => file open fail" );
	}

	return maps_infos.size();
}

const mapsInfo * maps_reader::get( unsigned int idx ) {
	if( idx >= maps_infos.size()) {
		return nullptr;
	}

	try {
		return &maps_infos[idx];
	} catch( std::exception & e ) {
	}
	return nullptr;
}

void maps_reader::print( const mapsInfo * pMapsInfo ) {
	if( pMapsInfo != NULL ) {
		LOGI("%08lx-%08lx %s %s\n",pMapsInfo->startAddr,pMapsInfo->endAddr,pMapsInfo->permission,pMapsInfo->filename);
	} else {
		for( auto it = maps_infos.cbegin(); it != maps_infos.cend(); ++it ) {
			const mapsInfo * pMapsInfo = &(*it);
			LOGI("%08lx-%08lx %s %s\n",pMapsInfo->startAddr,pMapsInfo->endAddr,pMapsInfo->permission,pMapsInfo->filename);
		}
	}
}
