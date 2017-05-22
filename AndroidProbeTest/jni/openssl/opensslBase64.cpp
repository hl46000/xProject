//#include "StdAfx.h"
#include "opensslBase64.h"

#include <openssl/sha.h>
#include <openssl/hmac.h>
#include <openssl/evp.h>
#include <openssl/bio.h>
#include <openssl/buffer.h>

openssl_base64::openssl_base64(void){}
openssl_base64::~openssl_base64(void){}

/**
 * @fn			size_t openssl_b64encode( IN const bytes & source, OUT string & target )
 * @brief 		base64 encoding
 * @param		[IN]  source	source data
 * @param		[IN]  target	target buffer
 * @return 		size of target buffer
 * @exception	N/A
 */
size_t	openssl_base64::encode( const bytes & source, string & target )
{
	BIO *bmem, *b64;
	BUF_MEM *bptr;

	b64 = BIO_new(BIO_f_base64());
	BIO_set_flags(b64, BIO_FLAGS_BASE64_NO_NL);
	bmem = BIO_new(BIO_s_mem());
	b64 = BIO_push(b64, bmem);
	while( true )
	{
		if( 0 >= BIO_write(b64, ( unsigned char * ) &source[0], source.size()))
		{
			if( BIO_should_retry(b64)) 
			{
				continue;
			}
		}
		break;
	}
	BIO_flush(b64);
	BIO_get_mem_ptr(b64, &bptr);

	target.assign( bptr->data, bptr->data + bptr->length );
	BIO_free_all(b64);

	return target.size();
}

/**
 * @fn			size_t openssl_b64decode( IN const bytes & source, OUT string & target )
 * @brief 		base64 decoding 
 * @param		[IN]  source	source data
 * @param		[IN]  target	target buffer
 * @return 		size of target buffer
 * @exception	N/A
 */
size_t	openssl_base64::decode( const string & source, bytes & target )
{
	BIO *b64, *bmem;

	char *buffer = (char *)malloc(source.size());
	memset(buffer, 0, source.size());

	b64 = BIO_new(BIO_f_base64());
	BIO_set_flags(b64, BIO_FLAGS_BASE64_NO_NL);

	bmem = BIO_new_mem_buf((void *) source.c_str(), source.size());
	bmem = BIO_push(b64, bmem);

	int nRead = BIO_read(bmem, buffer, source.size());
	
	target.assign( buffer, buffer + nRead );

	BIO_free_all(bmem);
	free( buffer );

	return target.size();
}
