/*
 * opensslDigest.cpp
 *
 *  Created on: 2013. 3. 4.
 *      Author: purehero2
 */

#include "opensslDigest.h"
#include <openssl/evp.h>
#include "../util/log.h"

opensslDigest::opensslDigest()
{
}

opensslDigest::~opensslDigest()
{
	EVP_MD_CTX_cleanup(&m_ctx);
}

/**
 * @fn      init
 * @brief   초기화
 *
 * @param   [IN] Covault::DIGEST
 * @return  0 : 성공, 기타 오류 값
 */
int opensslDigest::init( IN OPENSSL::DIGEST algorithm )
{
	const EVP_MD *md = NULL;
	switch( algorithm )
	{
	case OPENSSL::DIGEST_MD5:
		md = EVP_md5();
        LOGE( "######## OpenSSL Digest : MD5" );
		break;
	case OPENSSL::DIGEST_SHA1:
		md = EVP_sha1();
        LOGE( "######## OpenSSL Digest : SHA1" );
		break;
	case OPENSSL::DIGEST_SHA256 :
		md = EVP_sha256();
        LOGE( "######## OpenSSL Digest : SHA256" );
		break;
	default :
		md = EVP_md5();
        LOGE( "######## OpenSSL Digest : MD5" );
		break;
	}

	EVP_MD_CTX_init(&m_ctx);
	EVP_DigestInit_ex(&m_ctx, md, NULL);

	return 0;
}

/**
 * @fn      update
 * @brief   Digest 할 데이터들을 받아들임
 *
 * @param   [IN] pData
 * @param   [IN] size
 * @return  0 : 성공, 기타 오류 값
 */
int opensslDigest::update( IN const unsigned char * pData, IN int size )
{
	EVP_DigestUpdate(&m_ctx, ( const void * )pData, size );
	return 0;
}

/**
 * @fn      final
 * @brief   Digest 값을 얻음
 *
 * @param   [OUT] data
 * @return  0 : 성공, 기타 오류 값
 */
int opensslDigest::final( OUT bytes & data )
{
	unsigned char digest[EVP_MAX_MD_SIZE];		 /* longest known is SHA512 */
	unsigned int digestlen;

	EVP_DigestFinal_ex( &m_ctx, digest, &digestlen );

	data.clear();
	data.assign( digest, digest + digestlen );
	return 0;
}

/**
 * @fn      release
 * @brief	release
 *
 * @param	void
 * @return  void
 */
void opensslDigest::release()
{
	//delete this;
}

