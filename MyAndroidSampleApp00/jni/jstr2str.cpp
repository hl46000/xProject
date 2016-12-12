/*
 * jstr2str.cpp
 *
 *  Created on: 2016. 12. 12.
 *      Author: purehero
 */

#include "jstr2str.h"
#include <string.h>

#ifdef __cplusplus
extern "C" {
#endif

jstr2str::jstr2str( JNIEnv * _pEnv, jstring _jstr ) : jstr( _jstr ), pEnv( _pEnv )
{
	pStr = ( char * ) pEnv->GetStringUTFChars( jstr, NULL );
}

jstr2str::~jstr2str()
{
	if( pStr != NULL ) {
		pEnv->ReleaseStringUTFChars( jstr, pStr );
		pStr = NULL;
	}
}

const char * jstr2str::c_str() {
	return pStr;
}

#ifdef __cplusplus
}
#endif
