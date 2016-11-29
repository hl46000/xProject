#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>

#define  LOG_TAG	"Sample00"
#define  LOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGT()		__android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,"%s:%s",__FILE__,__func__ )

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
#if 0
	const char * classPath = "com/example/myandroidsampleapp00/NativeLibrary";
	jclass clazz = env->FindClass( classPath );
	if (clazz == NULL)
	{
		LOGE( "Native registration unable to find class '%s'", classPath );
		return JNI_FALSE;
	}

	JNINativeMethod gMethods[4] = {
			{ "nCreateRenderer", 	"()I", 		(void*) android_view_GLES20Canvas_createRenderer },
			{ "nDrawCircle", 		"(IFFFI)V", (void*) android_view_GLES20Canvas_drawCircle }
	};

	if (env->RegisterNatives( clazz, gMethods, 2 ) < 0 )
	{
		LOGE( "RegisterNatives failed for '%s'", classPath );
		return -1;
	}
#endif
	return JNI_VERSION_1_4;
}
