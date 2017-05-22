/*
 * apk_hash_generator.cpp
 *
 *  Created on: 2016. 4. 4.
 *      Author: purehero2
 */

#include <apk_hash_generator.h>

#include "zlib/Unzipper.h"
#include "util/util.h"
#include "util/log.h"

#include "opensslDigest.h"
#include "opensslBase64.h"

#include <map>

int getNameDigestLine( const std::string & strInfo, std::string & strName, std::string & stdDigest )
{
	std::vector<std::string> token = split2( strInfo, (unsigned char * )"\r\n", 2 );
	int len = token.size();

	std::string digest_line = token[ len - 1 ];
	std::string name_line = token[0];

	for( int j = 1; j < len - 1; j++ )
	{
		name_line = trim( name_line ) + trim( token[j] );
	}

	std::vector<std::string> tokName	= split2( name_line,   (unsigned char * )":", 1 );
	std::vector<std::string> tokDigest	= split2( digest_line, (unsigned char * )":", 1 );

	strName.clear();
	strName.assign( tokName[1].begin(), tokName[1].end() );

	stdDigest.clear();
	stdDigest.assign( tokDigest[1].begin(), tokDigest[1].end() );

	return 0;
}

const char * ApkHashGenerator::Generator( const char * filepath )
{
	CUnzipper unZip;
	if( !unZip.OpenZip( filepath ))
	{
		LOGE( "ERROR : APK file open fail! : %s", filepath );
		return "";
	}

	if( !unZip.GotoFirstFile())
	{
		LOGE( "ERROR : APK can't move first file! : %s", filepath );
		unZip.CloseZip();

		return "";
	}

	std::string strManifest = "";
	bool bFound = false;

	UZ_FileInfo info;
	do
	{
		unZip.GetFileInfo( info );
		if( strstr( info.szFileName, "MANIFEST.MF" ) == NULL ) continue;

		if( unZip.OpenCurrentFile())
		{
			char buffer[10241];
			int nBytes = 0;

			while(( nBytes = unZip.ReadCurrentFile( buffer, 10240)) > 0 )
			{
				buffer[nBytes] = '\0';
				strManifest.append( std::string( buffer ));
			}

			unZip.CloseCurrentFile();

			bFound = true;
			break;
		}

	} while( unZip.GotoNextFile());

	unZip.CloseZip();

	// MANIFEST.MF 파일을 찾았으면
	if( bFound )
	{
		const std::string delims = "\r\n\r\n";
		std::vector<std::string> tokens = split2( strManifest, (unsigned char *)&delims[0], delims.length() );
		std::map<std::string,std::string> hashInfos;

		std::string strName, strDigest;

		int nLen = tokens.size() - 1;
		for( int i = 1; i <= nLen; i ++ )
		{
			getNameDigestLine( tokens[i], strName, strDigest );

			LOGD( "NAME => %s\n", strName.c_str() );
			LOGD( "DIGEST => %s\n", strDigest.c_str() );

			if( strstr( strName.c_str(), "MANIFEST.MI" ) != NULL )
			{
				LOGD( "hash skip!!" );
				continue;
			}

			hashInfos.insert( std::pair<std::string,std::string>( strDigest, strName ));
		}

		opensslDigest __DIGEST;
		if( __DIGEST.init( OPENSSL::DIGEST_MD5) == 0 )
		{
			bytes updateBytes;
			for( std::map<std::string,std::string>::iterator it = hashInfos.begin(); it != hashInfos.end(); it++ )
			{
				updateBytes.assign( it->first.c_str(), it->first.c_str() + it->first.size());
				__DIGEST.update( &updateBytes[0], updateBytes.size() );
			}

			bytes apkHash;
			__DIGEST.final(apkHash);

			openssl_base64::encode( apkHash, hash_string );
		}
	}

	return hash_string.c_str();
}
