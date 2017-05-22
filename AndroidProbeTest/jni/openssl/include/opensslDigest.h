/*
 * opensslDigest.h
 *
 *  Created on: 2013. 3. 4.
 *      Author: purehero2
 */

#ifndef OPENSSLDIGEST_H_
#define OPENSSLDIGEST_H_

#include <openssl/evp.h>
#include <openssl/bio.h>

#ifndef _bytes_
#define _bytes_
#include <vector>
typedef std::vector<unsigned char> bytes;
#endif

#ifndef IN
#define IN
#define OUT
#endif

namespace OPENSSL
{
	enum DIGEST
	{
		DIGEST_MD5,
		DIGEST_SHA1,
		DIGEST_SHA256,
		DIGEST_CHECKSUM32,
		DIGEST_CRC32
	};
};

class opensslDigest {
public:
	opensslDigest();
	virtual ~opensslDigest();

	/**
	 * @fn      init
	 * @brief   초기화
	 *
	 * @param   [IN] Covault::DIGEST
	 * @return  0 : 성공, 기타 오류 값
	 */
	int init( IN OPENSSL::DIGEST algorithm );

	/**
	 * @fn      update
	 * @brief   Digest 할 데이터들을 받아들임
	 *
	 * @param   [IN] pData
	 * @param   [IN] size
	 * @return  0 : 성공, 기타 오류 값
	 */
	int update( IN const unsigned char * pData, IN int size );

	/**
	 * @fn      final
	 * @brief   Digest 값을 얻음
	 *
	 * @param   [OUT] data
	 * @return  0 : 성공, 기타 오류 값
	 */
	int final( OUT bytes & data );

	/**
	 * @fn      release
	 * @brief	release
	 *
	 * @param	void
	 * @return  void
	 */
	void release();

protected :
	EVP_MD_CTX				m_ctx;
};

#endif /* OPENSSLDIGEST_H_ */
