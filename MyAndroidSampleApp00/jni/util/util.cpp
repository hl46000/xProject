#include <util.h>

#include <dirent.h>
#include <unistd.h>
#include <stdlib.h>

#include <time.h>
#include <algorithm>

#include "alog.h"

#ifndef DELAY_TIME
#define DELAY_TIME		usleep(10);
#endif


int GuidGenerator( JNIEnv *env, std::string & newGuid, const char * seed )
{
	LOGT();

	newGuid.clear();

	jclass _uuidClass = env->FindClass("java/util/UUID");
	jmethodID _mostSignificantBitsMethod = env->GetMethodID(_uuidClass, "getMostSignificantBits", "()J");
	jmethodID _leastSignificantBitsMethod = env->GetMethodID(_uuidClass, "getLeastSignificantBits", "()J");

	jobject javaUuid = NULL;
	if( seed != NULL ) {
		jmethodID _newGuidMethod = env->GetStaticMethodID(_uuidClass, "nameUUIDFromBytes", "([B)Ljava/util/UUID;");

		int seed_len = strlen( seed );
		jbyteArray seed_byte_array = env->NewByteArray( seed_len );
		env->SetByteArrayRegion( seed_byte_array, 0, seed_len, ( const jbyte* ) seed );

		javaUuid = env->CallStaticObjectMethod(_uuidClass, _newGuidMethod, seed_byte_array );
	} else {
		jmethodID _newGuidMethod = env->GetStaticMethodID(_uuidClass, "randomUUID", "()Ljava/util/UUID;");

		javaUuid = env->CallStaticObjectMethod(_uuidClass, _newGuidMethod );
	}

	jlong mostSignificant = env->CallLongMethod(javaUuid, _mostSignificantBitsMethod);
	jlong leastSignificant = env->CallLongMethod(javaUuid, _leastSignificantBitsMethod);

	unsigned char bytes[16] =
	{
		(unsigned char)((mostSignificant >> 56) & 0xFF),
		(unsigned char)((mostSignificant >> 48) & 0xFF),
		(unsigned char)((mostSignificant >> 40) & 0xFF),
		(unsigned char)((mostSignificant >> 32) & 0xFF),
		(unsigned char)((mostSignificant >> 24) & 0xFF),
		(unsigned char)((mostSignificant >> 16) & 0xFF),
		(unsigned char)((mostSignificant >> 8) & 0xFF),
		(unsigned char)((mostSignificant) & 0xFF),
		(unsigned char)((leastSignificant >> 56) & 0xFF),
		(unsigned char)((leastSignificant >> 48) & 0xFF),
		(unsigned char)((leastSignificant >> 40) & 0xFF),
		(unsigned char)((leastSignificant >> 32) & 0xFF),
		(unsigned char)((leastSignificant >> 24) & 0xFF),
		(unsigned char)((leastSignificant >> 16) & 0xFF),
		(unsigned char)((leastSignificant >> 8) & 0xFF),
		(unsigned char)((leastSignificant) & 0xFF)
	};

	char strTemp[64];
	sprintf( strTemp, "%02X%02X%02X%02X-%02X%02X-%02X%02X-%02X%02X-%02X%02X%02X%02X%02X%02X",
			bytes[0],  bytes[1],  bytes[2],  bytes[3],
			bytes[4],  bytes[5],
			bytes[6],  bytes[7],
			bytes[8],  bytes[9],
			bytes[10], bytes[11], bytes[12], bytes[13], bytes[14], bytes[15] );

	newGuid.assign( &strTemp[0], &strTemp[strlen(strTemp)] );
	return 0;
}

int read_thread_ids( int pid, std::vector<int> & TIDs )
{
	char strPath[260];

	struct dirent *file;
	sprintf( strPath, "/proc/%d/task", pid );

	DIR * dir = opendir( strPath );
	if( dir == NULL )
	{
		perror("opendir");
		return -1;
	}

	while(( file = readdir( dir )) != NULL )
	{
		DELAY_TIME;
		if ( strncmp( file->d_name, ".", 1 ) == 0 || strncmp( file->d_name, "..", 2 ) == 0 || file->d_name[0] < '1' || file->d_name[0] > '9' )
		{
			continue;
		}

		int pid = atoi( file->d_name );
		TIDs.push_back( pid );
	}
	closedir( dir );

	return (int) TIDs.size();
}

// trim from start
std::string &ltrim(std::string &s) {
	s.erase(s.begin(), std::find_if(s.begin(), s.end(), std::not1(std::ptr_fun<int, int>(std::isspace))));
	return s;
}

// trim from end
std::string &rtrim(std::string &s) {
	s.erase(std::find_if(s.rbegin(), s.rend(), std::not1(std::ptr_fun<int, int>(std::isspace))).base(), s.end());
	return s;
}

// trim from both ends
std::string &trim(std::string &s) {
	return ltrim(rtrim(s));
}

std::vector<std::string> split2( const std::string& str, unsigned char * separators, int spLen )
{
	std::vector<std::string> result;

	int start = 0;
	unsigned char * pBuff = ( unsigned char * ) &str[0];
	bool bFound = false;

	int nLen = str.length();
	for( int i = 0; i < nLen-spLen; i++ )
	{
		bFound = true;
		for( int j = 0; j < spLen; j++ )
		{
			if( pBuff[i+j] != separators[j] )
			{
				if( j > 0 ) i += j;

				bFound = false;
				break;
			}
		}
		if( bFound )
		{
			std::string tmp( ( char * )&pBuff[start], ( char * )&pBuff[i] );
			result.push_back( trim(tmp) );

			i += spLen;
			start = i;
		}
	}
	if( start < nLen )
	{
		std::string tmp( ( char * )&pBuff[start], ( char * )&pBuff[nLen] );
		result.push_back( trim( tmp ));
	}

	return result;
}
