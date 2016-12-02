#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>

#define  LOG_TAG	"TEST_Native"
#define  LOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGT()		__android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,"%s:%s",__FILE__,__func__ )

void init( JNIEnv * env, jobject, jobject appContext )
{
	LOGT();

	jclass jc_appContext = env->GetObjectClass(appContext);
	if(jc_appContext == NULL) {
		LOGE("ERROR: failed to get appContext");
		return;
	} else {
		LOGD("SUCCESS: get appContext");
	}

	jmethodID jm_getApplicationInfo = env->GetMethodID( jc_appContext, "getApplicationInfo", "()Landroid/content/pm/ApplicationInfo;");
	if( jm_getApplicationInfo == NULL) {
		LOGE("ERROR: failed to get getApplicationInfo method");
		return;
	} else {
		LOGD("SUCCESS: get getApplicationInfo method");
	}

	jobject jo_applicationInfo = ( jclass ) env->CallObjectMethod( appContext, jm_getApplicationInfo );
	if( jo_applicationInfo == NULL ) {
		LOGE("ERROR: failed to get getApplicationInfo object");
		return;
	} else {
		LOGD("SUCCESS: get getApplicationInfo object");
	}

	jclass jc_applicationInfo = env->FindClass( "android/content/pm/ApplicationInfo");
	if( jc_applicationInfo == NULL ) {
		LOGE("ERROR: failed to get getApplicationInfo instance");
		return;
	} else {
		LOGD("SUCCESS: get getApplicationInfo instance");
	}

	jfieldID jf_nativeLibraryDir = env->GetFieldID( jc_applicationInfo, "nativeLibraryDir", "Ljava/lang/String;");
	if( jf_nativeLibraryDir == NULL ) {
		LOGE("ERROR: failed to get nativeLibraryDir field");
		return;
	} else {
		LOGD("SUCCESS: get nativeLibraryDir field");
	}

	jstring nativeLibraryDir = (jstring) env->GetObjectField( jo_applicationInfo, jf_nativeLibraryDir );
	if( nativeLibraryDir == NULL ) {
		LOGE("ERROR: failed to get nativeLibraryDir field value");
		return;
	} else {
		LOGD("SUCCESS: et nativeLibraryDir field value");
	}


	char * strNativeLibraryDir = (char*) env->GetStringUTFChars( nativeLibraryDir, 0 );
	LOGD( "strNativeLibraryDir : %s", strNativeLibraryDir );

	env->ReleaseStringUTFChars( nativeLibraryDir, strNativeLibraryDir );
}

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
#if 1
	const char * classPath = "com/example/myandroidsampleapp00/NativeLibrary";
	jclass clazz = env->FindClass( classPath );
	if (clazz == NULL)
	{
		LOGE( "Native registration unable to find class '%s'", classPath );
		return JNI_FALSE;
	}

	JNINativeMethod gMethods[4] = {
			{ "init", 	"(Landroid/content/Context;)V", 		(void*) init }
	};

	if (env->RegisterNatives( clazz, gMethods, 1 ) < 0 )
	{
		LOGE( "RegisterNatives failed for '%s'", classPath );
		return -1;
	}
#endif
	return JNI_VERSION_1_4;
}
