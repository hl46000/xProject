/*
 * apk_hash_generator.h
 *
 *  Created on: 2016. 4. 4.
 *      Author: purehero2
 */

#ifndef UTIL_APK_HASH_GENERATOR_H_
#define UTIL_APK_HASH_GENERATOR_H_

#include <string>

class ApkHashGenerator {
public:
	ApkHashGenerator(){}
	virtual ~ApkHashGenerator(){}

	const char * Generator( const char * filepath );
private :
	std::string hash_string;
};

#endif /* UTIL_APK_HASH_GENERATOR_H_ */
