#include <jni.h>
#include <android/log.h>

#include <stdio.h>
#include <stdlib.h>

#include <sys/types.h>
#include <unistd.h>

//#include "injection/injectionPLT.h"
#include "injection/injection_detecter.h"
#include "injection/injection_detecter_not_read_maps.h"

#define  LOG_TAG	"Injection_SampleApp"
#define  LOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGT()		__android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,"%s:%s",__FILE__,__func__ )

#define DELAY_TIME		usleep(10);

void CheckInjection( JNIEnv * env, jobject obj )
{
	LOGT();

	//injectionPLT inj;
	//injection_detecter inj;
	injection_detecter_not_read_maps inj;
	inj.init( env );

	//inj.set_log_fp( "/mnt/sdcard/123123.txt" );
	if( inj.get_target_module( getpid()) > 0 ) {
		inj.working();
	}

	inj.release();
}

__attribute__((constructor)) int JNI_OnPreLoad() { return 0; }

// JNI_OnLoad
jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	LOGT();

	JNIEnv * env = NULL;
	jint result = -1;

	if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK)
	{
		LOGE("ERROR: GetEnv failed");
		return result;
	}

	const char * classPath = "com/example/androidprobetest/SimpleMainActivity";
	jclass clazz = env->FindClass( classPath );
	if (clazz == NULL)
	{
		LOGE( "Native registration unable to find class '%s'", classPath );
		return JNI_FALSE;
	}

	JNINativeMethod gMethods[1] = {
			{ "CheckInjection", "()V", (void*) CheckInjection }
	};

	if (env->RegisterNatives( clazz, gMethods, 1 ) < 0 )
	{
		LOGE( "RegisterNatives failed for '%s'", classPath );
		return -1;
	}

	return JNI_VERSION_1_4;
}
