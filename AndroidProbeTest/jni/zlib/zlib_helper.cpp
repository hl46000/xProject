#include "zlib_helper.h"

#ifdef __cplusplus
extern "C" {
#endif

//#ifdef OS_ANDROID
//#include "jni_helper.h"
//#define LogPrint			LOGI
//#else
#define LogPrint			printf
//#endif

void zerr( int ret )
{
	LogPrint( "zpipe: ");
	switch (ret) {
	case Z_ERRNO:
		if (ferror(stdin))
		{
			LogPrint("error reading stdin\n");
		}

		if (ferror(stdout))
		{
			LogPrint("error writing stdout\n");
		}
		break;

	case Z_STREAM_ERROR:
		LogPrint("invalid compression level\n");
		break;

	case Z_DATA_ERROR:
		LogPrint("invalid or incomplete deflate data\n");
		break;

	case Z_MEM_ERROR:
		LogPrint("out of memory\n");
		break;

	case Z_VERSION_ERROR:
		LogPrint("zlib version mismatch!\n");
	}
}

bool Compress( unsigned char * inbuf, unsigned int inlen, unsigned char * outbuf, unsigned int &outlen, unsigned int outbuflen )
{
	LogPrint( "zlib_helper : Compress [IN]");

	z_stream z;
	unsigned int flush, status;

	z.zalloc	= Z_NULL;
	z.zfree		= Z_NULL;
	z.opaque	= Z_NULL;

	int ret = deflateInit (&z, Z_BEST_COMPRESSION);
	if( ret != Z_OK )
	{
		zerr( ret );
		LogPrint( ">> Failed: deflateInit. [%s]\n", z.msg );
		return false;
	}

	z.avail_in	= 0;
	z.next_in	= inbuf;
	z.avail_in	= inlen;
	z.next_out	= outbuf;
	z.avail_out	= outbuflen;

	flush = Z_FINISH;

	status = deflate (&z, flush);

	if( status != Z_STREAM_END ) 
	{
		zerr( ( int )status );
		LogPrint( ">> Failed: deflate. [%s]\n", z.msg );
		return false;
	}

	if( deflateEnd (&z) != Z_OK ) 
	{
		LogPrint( ">> Failed: deflateEnd. [%s]\n", z.msg );
		return false;
	}

	outlen = z.total_out;

	LogPrint( "zlib_helper : Compress [OUT]");
	return true;
}

bool DeCompress( unsigned char * inbuf, unsigned int inlen, unsigned char * outbuf, unsigned int &outlen, unsigned int outbuflen )
{
	LogPrint( "zlib_helper : DeCompress [IN]");

	z_stream z;
	unsigned int status;
	int ret;

	z.zalloc	= Z_NULL;
	z.zfree		= Z_NULL;
	z.opaque	= Z_NULL;

	z.next_in	= Z_NULL;
	z.avail_in	= 0;

	ret = inflateInit(&z);
	if( ret != Z_OK )
	{
		zerr( ret );
		LogPrint( ">> Failed: inflateInit. [%s]\n", z.msg );
		return false;
	}

	z.next_in	= inbuf;
	z.avail_in	= inlen;
	z.next_out	= outbuf;
	z.avail_out	= outbuflen;

	status = Z_OK;

	status = inflate(&z, Z_NO_FLUSH);

	if( status != Z_STREAM_END ) 
	{
		zerr( (int) status );
		LogPrint( ">> Failed: inflate. [%s]\n", z.msg );
		return false;
	}

	if( inflateEnd (&z) != Z_OK ) 
	{
		LogPrint( ">> Failed: inflateEnd. [%s]\n", z.msg );
		return false;
	}

	outlen = z.total_out;

	LogPrint( "zlib_helper : DeCompress [OUT]");
	return true;
}


#ifdef __cplusplus
}
#endif
