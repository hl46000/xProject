#include <stdio.h>
#include <stdlib.h>

#include "alog.h"
#include "jstr2str.h"
#include "maps_reader.h"

#include <sys/types.h>
#include <unistd.h>

#include <dlfcn.h>
void test_so_filepath()
{
	LOGT();

	char path[1024];
	sprintf( path, "%s", "libattach.so");

	void* handle = dlopen( "libsample01.so", RTLD_LAZY );
	const char* errmsg=dlerror();
	if( handle == NULL )
	{
		if( errmsg != NULL ) {
			LOGE( "%s", errmsg );
		} else {
			LOGE( "errmsg is NULL" );
		}
	} else {
		memcpy( path, handle, 127 );
		LOGE( "success dlopen %s", path );
		dlclose( handle );
	}
}

void init( JNIEnv * env, jobject, jobject appContext )
{
	LOGT();

	test_so_filepath();

	maps_reader maps( getpid() );
	LOGD( "sizeof(mapsInfo) : %d", maps.read());
	maps.print( NULL );

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
		LOGD("SUCCESS: get nativeLibraryDir field value");
	}


	char * strNativeLibraryDir = (char*) env->GetStringUTFChars( nativeLibraryDir, 0 );
	LOGD( "strNativeLibraryDir : %s", strNativeLibraryDir );

	env->ReleaseStringUTFChars( nativeLibraryDir, strNativeLibraryDir );
}



#include <fcntl.h>
void runDex2Oat( JNIEnv * env, jobject, jstring cache_path )
{
	LOGT();

	jstr2str jstr( env, cache_path );


	char cmd_buff[1024];

	//const char * dexFilePath 	= "/sdcard/workTemp/a.apk";
	const char * dexFilePath 	= "/sdcard/workTemp/classes.dex";

	char odexFilePath[260];
	sprintf( odexFilePath, "%s/a.apk@classes.dex", jstr.c_str() );

	//int dexFd 	= open( dexFilePath, 	O_RDONLY );
	int odexFd 	= open( odexFilePath, 	O_WRONLY | O_CREAT | O_TRUNC );

	//sprintf( cmd_buff, "/system/bin/dex2oat --zip-fd=%d --zip-location=%s --oat-fd=%d --oat-location=%s --instruction-set=arm --instruction-set-features=div --compiler-filter=interpret-only --compiler-backend=Quick --runtime-arg -Xms64m --runtime-arg -Xmx512m -j4 --swap-fd=18",
	//sprintf( cmd_buff, "/system/bin/dex2oat --zip-fd=%d --zip-location=%s --oat-fd=%d --oat-location=%s --instruction-set=arm --instruction-set-features=div --compiler-backend=Quick --runtime-arg -Xms64m --runtime-arg -Xmx512m -j4 --swap-fd=18",
	//sprintf( cmd_buff, "/system/bin/dex2oat --zip-fd=%d --zip-location=%s --oat-fd=%d --oat-location=%s --instruction-set=arm --instruction-set-features=div --compiler-filter=verify-none;interpret-only --compiler-backend=Quick --runtime-arg -Xms64m --runtime-arg -Xmx512m -j4 --swap-fd=18",
	//sprintf( cmd_buff, "/system/bin/dex2oat --dex-file=%s --oat-fd=%d --oat-location=%s --instruction-set=arm --instruction-set-features=div --compiler-filter=verify-none --runtime-arg -Xms64m --runtime-arg -Xmx512m -j4 --swap-fd=18",

	// 5.0 �̻� �ɼ�
	//sprintf( cmd_buff, "/system/bin/dex2oat --dex-file=%s --oat-fd=%d --oat-location=%s --instruction-set=arm --compiler-filter=verify-none --runtime-arg -Xms64m --runtime-arg -Xmx512m -j4 --swap-fd=18",

	// 4.4 �ɼ�
	sprintf( cmd_buff, "/system/bin/dex2oat --dex-file=%s --oat-fd=%d --oat-location=%s --instruction-set=arm --runtime-arg -Xms64m --runtime-arg -Xmx512m -j4 --swap-fd=18",
		dexFilePath,
		odexFd, odexFilePath );

	LOGD( "%s", cmd_buff );
	LOGD( "[[ BEGIN ]]" );
	system( cmd_buff );
	LOGD( "[[ END ]]" );

	//close( dexFd );
	close( odexFd );
}

__attribute__((constructor))
void JNI_OnPreLoad()
{
	LOGT();
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
			{ "init", 			"(Landroid/content/Context;)V", 		(void*) init },
			{ "runDex2Oat", 	"(Ljava/lang/String;)V", 				(void*) runDex2Oat }
	};

	if (env->RegisterNatives( clazz, gMethods, 2 ) < 0 )
	{
		LOGE( "RegisterNatives failed for '%s'", classPath );
		return -1;
	}
#endif

	test_so_filepath();

	return JNI_VERSION_1_4;
}
