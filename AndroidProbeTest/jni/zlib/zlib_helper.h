#ifndef ZlibManager_H
#define ZlibManager_H

#if _DEBUG
#pragma comment( lib, "zlib/zlibd.lib")
#else
#pragma comment( lib, "zlib/zlib.lib")
#endif

#include <stdio.h>
#ifdef WIN32
#include <Windows.h>
#endif
#include "zconf.h"
#include "zlib.h"

#ifdef __cplusplus
extern "C" {
#endif

bool Compress( unsigned char * inbuf, unsigned int inlen, unsigned char * outbuf, unsigned int &outlen, unsigned int outbuflen );
bool DeCompress( unsigned char * inbuf, unsigned int inlen, unsigned char * outbuf, unsigned int &outlen, unsigned int outbuflen );

#ifdef __cplusplus
}
#endif

#endif
