#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>

#define  LOG_TAG	"TEST01"
#define  LOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGT()		__android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,"%s:%s",__FILE__,__func__ )

void init( JNIEnv * env, jobject, jobject appContext );

// JNI_OnLoad
jint JNI_OnLoad( JavaVM* vm, void* )
{
	LOGT();

	JNIEnv * env = NULL;
	jint result = -1;

	if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK)
	{
		LOGE("ERROR: GetEnv failed");
		return result;
	}

	const char * classPath = "com/example/myandroidsampleapp00/NativeLibrary2";
	jclass clazz = env->FindClass( classPath );
	if (clazz == NULL)
	{
		LOGE( "Native registration unable to find class '%s'", classPath );
		return JNI_FALSE;
	}

	JNINativeMethod gMethods[1] = {
			{ "init", 			"(Landroid/content/Context;)V", 		(void*) init }
	};

	if (env->RegisterNatives( clazz, gMethods, 1 ) < 0 )
	{
		LOGE( "RegisterNatives failed for '%s'", classPath );
		return -1;
	}

	return JNI_VERSION_1_4;
}


#include <string>
/*
 * Android 의 Build 정보를 획득하기 위한 함수
 *
 * */
int getDeviceField( JNIEnv* env, const char* name, std::string & ret_value )
{
	jclass cls_context = env->FindClass( "android/os/Build" );
	if ( cls_context == 0  ) {
		LOGE( "ERROR: env->FindClass failed" );
		return -1;
	}

	jfieldID field = env->GetStaticFieldID( cls_context, name, "Ljava/lang/String;" );
	if ( field == 0 ) {
		LOGE( "ERROR: env->GetStaticFieldID" );
		return -1;
	}

	jstring str_value = ( jstring )env->GetStaticObjectField( cls_context, field );
	if ( str_value == 0 ) {
		LOGE( "ERROR: env->GetStaticObjectField" );
		return -1;
	}

	const char * pStr = ( char * ) env->GetStringUTFChars( str_value, NULL );
	ret_value.assign( pStr );
	env->ReleaseStringUTFChars( str_value, pStr );

	LOGD( "getDeviceField : %s => %s", name, ret_value.c_str());

	return 0;
}




/*
 * 파일의 존재 유무를 확인하기 위한 함수
 *
 * return : true ( 유 ), false ( 무 )
 * */
#include <unistd.h>
bool file_exist( const char * filepath )
{
	return access( filepath, 0 ) == 0;
}

/*
 * 실행중인 단말기가 VM 환경인지를 확인 하기 위한 함수
 *
 * return : true ( VM ), false ( No VM )
 * */
bool check_vm( JNIEnv * env )
{
	LOGT();

	// 해당 파일이 존재하면 VM 으로 판단
	if( file_exist( "/sys/devices/platform/hd_power" )) {
		return true;
	}
	if( file_exist( "/sys/bus/ac97" )) {
		return true;
	}

	// 해당 파일이 존재하지 않으면 VM으로 판단.

	// 해당 파일이 특정 문자열이 존재하면 VM으로 판다.
	char line[2048];

	bool bFound = false;
	FILE * fp = NULL;

	fp = fopen( "/proc/partitions", "r");
	if( fp != NULL ) {

		const char * findString = "sda";	// 찾을 문자열
		while( fgets( line, 2047, fp ) != NULL ) {
			if( strstr( line, findString ) != NULL ) {
				bFound = true;
				break;
			}
		}
		fclose( fp );
	}
	if( bFound ) return true;

	bFound = false;
	fp = fopen( "/proc/modules", "r");
	if( fp != NULL ) {
		const char * check_list[] = { 	// 찾을 문자열 들
			"bstmouse",
			"bsttouch",
			"bstkbd",
			"bstgps",
			"bstcmd",
			"bstcamera",
			"bstvideo",
			"bstaudio",
			NULL
		};

		while( fgets( line, 2047, fp ) != NULL ) {
			for( int i = 0; check_list[i] != NULL; i++ ) {
				if( strstr( line, check_list[i] ) != NULL ) {
					bFound = true;
					break;
				}
			}
		}
		fclose( fp );
	}
	if( bFound ) return true;

	// Device 의 Build 정보 획득
	std::string cpu, device, hardware, product;
	getDeviceField( env, "CPU_ABI", cpu );
	getDeviceField( env, "DEVICE", device );
	getDeviceField( env, "HARDWARE", hardware );
	getDeviceField( env, "PRODUCT", product );

	// check ( cpu && hardware && device && product )
	const char * ck_datas[5][4] = {
		{ "x", 	"goldfish", "generic",		"sdk" },
		{ "x86","goldfish",	"generic_x86",	"sdk_x86" },
		{ "x86","andy",		"santos",		"santos" },
		{ "x86","duos",		"native",		"duos" },
		{ NULL,NULL,NULL,NULL }
	};

	bFound = false;
	for( int i = 0; ck_datas[i][0] != NULL; i++ ) {
		const char * _cpu 		= ck_datas[i][0];
		const char * _hardware	= ck_datas[i][1];
		const char * _device	= ck_datas[i][2];
		const char * _product	= ck_datas[i][3];

		if (( strlen(_cpu) 	 	< 2 ? true : cpu.compare( _cpu ) == 0 ) 			&&
			( strlen(_hardware) < 2 ? true : hardware.compare( _hardware ) == 0 ) 	&&
			( strlen(_device) 	< 2 ? true : device.compare( _device ) == 0 ) 		&&
			( strlen(_product)  < 2 ? true : product.compare( _product ) == 0 )) {

			LOGD( "check VM : cpu(%s) && hardware(%s) && device(%s) && product(%s)", _cpu, _hardware, _device, _product );
			return true;
		}
	}

	// check (cpu && ( hardware || device || product ))
	const char * ck_datas2[2][4] = {
		{ "x86","vbox86","vbox86","vbox86" },
		{ NULL,NULL,NULL,NULL }
	};

	bFound = false;
	for( int i = 0; ck_datas2[i][0] != NULL; i++ ) {
		const char * _cpu 		= ck_datas2[i][0];
		const char * _hardware	= ck_datas2[i][1];
		const char * _device	= ck_datas2[i][2];
		const char * _product	= ck_datas2[i][3];

		if (( strlen(_cpu) 	 	 	< 2 ? true : cpu.compare( _cpu ) == 0 ) &&
			( 	( strlen(_hardware) < 2 ? true : hardware.compare( _hardware ) == 0 ) 	||
				( strlen(_device) 	< 2 ? true : device.compare( _device ) == 0 ) 		||
				( strlen(_product)  < 2 ? true : product.compare( _product ) == 0 ))
			) {

			LOGD( "check VM : cpu(%s) && ( hardware(%s) || device(%s) || product(%s))", _cpu, _hardware, _device, _product );
			return true;
		}
	}


	return false;
}

void init( JNIEnv * env, jobject, jobject /*appContext*/ )
{
	LOGT();
	if( check_vm( env )) {
		LOGD("VM");
	} else {
		LOGD("NOT VM");
	}
}
