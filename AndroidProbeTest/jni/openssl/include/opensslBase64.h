/**
* @file		sdrm_base64.h file
* @brief	Samsung DRM base64 file
*
* Copyright 2011 by INKA Entworks, Inc.
*/

#ifndef _INCLUDE_Covault_OPENSSL_BASE64_HEADER_
#define _INCLUDE_Covault_OPENSSL_BASE64_HEADER_

#include <stdio.h>

#include <string>
using namespace std;

#ifndef _bytes_
#define _bytes_
#include <vector>
typedef vector<unsigned char> bytes;
#endif

#pragma once

class openssl_base64
{
public:
	openssl_base64(void);
	~openssl_base64(void);

	/**
	 * @fn			size_t encode( IN const bytes & source, OUT string & target )
	 * @brief 		base64 encoding
	 * @param		[IN]  source	source data
	 * @param		[IN]  target	target buffer
	 * @return 		size of target buffer
	 * @exception	N/A
	 */
	static size_t	encode( const bytes & source,  string & target );
	
	/**
	 * @fn			size_t decode( IN const bytes & source, OUT string & target )
	 * @brief 		base64 decoding 
	 * @param		[IN]  source	source data
	 * @param		[IN]  target	target buffer
	 * @return 		size of target buffer
	 * @exception	N/A
	 */
	static size_t	decode( const string & source, bytes & target );
};

#endif	/* _INCLUDE_Covault_BASE64_HEADER_ */
