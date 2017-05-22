/*
 * device_info.cpp
 *
 *  Created on: 2015. 10. 2.
 *      Author: purehero2
 */

#include <device_info.h>
#include "util/log.h"

device_info::device_info( JNIEnv * _env )
: env( _env )
{
	build_class = env->FindClass("android/os/Build");
}

device_info::~device_info() {
}

/*
* 단말기의 모델명을 반환 합니다.
*/
const char * device_info::get_model()
{
	jfieldID model_id  = env->GetStaticFieldID(build_class, "MODEL", "Ljava/lang/String;");
	jstring model_obj  = (jstring)env->GetStaticObjectField(build_class, model_id);
	const char * str_model = env->GetStringUTFChars(model_obj, 0);

	device_name = std::string( str_model );
	env->ReleaseStringUTFChars(model_obj, str_model);

	return device_name.c_str();
}

/*
* 단말기의 시리얼번호을 반환 합니다.
*/
const char * device_info::get_serial()
{
	jfieldID serial_id  = env->GetStaticFieldID(build_class, "SERIAL", "Ljava/lang/String;");
	jstring serial_obj  = (jstring)env->GetStaticObjectField(build_class, serial_id);
	const char * str_serial = env->GetStringUTFChars(serial_obj, 0);

	device_serial = std::string( str_serial );
	env->ReleaseStringUTFChars(serial_obj, str_serial);

	return device_serial.c_str();
}

/*
* 단말기의 SDK 버전을 반환 합니다.
*/
const char * device_info::get_api_level()
{
	jclass version_class = env->FindClass( "android/os/Build$VERSION");

	jfieldID sdk_version_id  = env->GetStaticFieldID( version_class, "SDK", "Ljava/lang/String;");
	jstring sdk_version_obj  = (jstring)env->GetStaticObjectField(version_class, sdk_version_id);
	const char * str_sdk_version = env->GetStringUTFChars(sdk_version_obj, 0);

	device_sdk_version = std::string( str_sdk_version );
	env->ReleaseStringUTFChars( sdk_version_obj, str_sdk_version );

	return device_sdk_version.c_str();
}

/*
* 단말기의 안드로이드 버전을 반환 합니다.
*/
const char * device_info::get_platform()
{
	jclass version_class = env->FindClass( "android/os/Build$VERSION");

	jfieldID release_version_id  = env->GetStaticFieldID( version_class, "RELEASE", "Ljava/lang/String;");
	jstring release_version_obj  = (jstring)env->GetStaticObjectField(version_class, release_version_id);
	const char * str_release_version = env->GetStringUTFChars(release_version_obj, 0);

	device_release_version = std::string( str_release_version );
	env->ReleaseStringUTFChars( release_version_obj, str_release_version );

	return device_release_version.c_str();
}


const char * device_info::get_internal_sd_path()
{
	jclass system_class = env->FindClass( "java/lang/System");
	jmethodID method = env->GetStaticMethodID( system_class, "getenv", "(Ljava/lang/String;)Ljava/lang/String;" );

	jstring internal_sd_path = (jstring)env->CallStaticObjectMethod( system_class, method, "EXTERNAL_STORAGE" );
	const char * str_internal_sd_path = env->GetStringUTFChars( internal_sd_path, 0);

	device_internal_sd_path = std::string( str_internal_sd_path );
	env->ReleaseStringUTFChars( internal_sd_path, str_internal_sd_path );

	return device_internal_sd_path.c_str();
}

// 단말기의 locale 정보를 출력해 줍니다. ko_KR <== 요런 식으로( 한국어_한국 )
int device_info::get_locale( std::string & str_locale )
{
	str_locale.clear();

	jclass jc_locale = env->FindClass( "java/util/Locale");
	if( jc_locale == 0) return -1;

	jmethodID jm_get_default = env->GetStaticMethodID( jc_locale, "getDefault","()Ljava/util/Locale;");
	if( jm_get_default == 0) return -2;

	jmethodID jm_toString = env->GetMethodID( jc_locale, "toString", "()Ljava/lang/String;");
	if( jm_toString == NULL) return -4;

	jobject jc_locale_obj = env->CallStaticObjectMethod( jc_locale, jm_get_default );
	if( jc_locale_obj == 0) return -3;

	jstring js_str_locale  = (jstring) env->CallObjectMethod( jc_locale_obj, jm_toString );
	if( js_str_locale == 0 ) return -5;

	const char * tempstr = env->GetStringUTFChars( js_str_locale, NULL );
	str_locale.assign(tempstr);

	env->ReleaseStringUTFChars( js_str_locale, tempstr );
	return 0;
}

const char * device_info::get_apk_full_path( jobject appContext )
{
	jclass cls = env->FindClass("android/content/Context");
	if( cls == NULL )
	{
		LOGE( "ERROR: env->FindClass(\"android/content/Context\")" );
		return NULL;
	}

	jmethodID mid = env->GetMethodID( cls, "getPackageResourcePath", "()Ljava/lang/String;");
	if(mid == NULL)
	{
		LOGE( "ERROR: env->GetMethodID( cls, \"getPackageResourcePath\", \"()Ljava/lang/String;\")" );
		return NULL;
	}

	jobject jobjPath = env->CallObjectMethod( appContext, mid );
	jstring jstrPath = (jstring)jobjPath;

	const char * c_str = env->GetStringUTFChars( jstrPath, 0 );

	apk_fullpath.clear();
	apk_fullpath.assign( c_str, c_str + strlen( c_str ) );

	env->ReleaseStringUTFChars( jstrPath, c_str );

	return apk_fullpath.c_str();
}
