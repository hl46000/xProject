/*
 * jstr2str.cpp
 *
 *  Created on: 2016. 12. 12.
 *      Author: purehero
 */

#include "jstr2str.h"

jstr2str::jstr2str( JNIEnv * _pEnv, jstring _jstr ) : jstr( _jstr ), pEnv( _pEnv )
{
	pStr = ( char * ) pEnv->GetStringUTFChars( jstr, nullptr );
}

jstr2str::~jstr2str()
{
	if( pStr != nullptr ) {
		pEnv->ReleaseStringUTFChars( jstr, pStr );
		pStr = nullptr;
	}
}

const char * jstr2str::c_str() {
	return pStr;
}
