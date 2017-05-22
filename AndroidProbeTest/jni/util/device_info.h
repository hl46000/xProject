/*
 * device_info.h
 *
 *  Created on: 2015. 10. 2.
 *      Author: purehero2
 */

#ifndef UTIL_DEVICE_INFO_H_
#define UTIL_DEVICE_INFO_H_

#include <jni.h>
#include <string>

class device_info {
public:
	device_info( JNIEnv * _env );
	virtual ~device_info();

	const char * get_model();
	const char * get_serial();
	const char * get_api_level();
	const char * get_platform();
	const char * get_internal_sd_path();
	const char * get_apk_full_path( jobject appContext );
	int get_locale( std::string & str_locale );
protected :
	JNIEnv * env;
	jclass build_class;

	std::string device_name;
	std::string device_serial;
	std::string device_sdk_version;
	std::string device_release_version;
	std::string device_internal_sd_path;
	std::string apk_fullpath;
};

#endif /* UTIL_DEVICE_INFO_H_ */
