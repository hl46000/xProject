#include <jni.h>
#include <stdio.h>

#include <sys/syslimits.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/param.h>
#include <sys/stat.h>

#include "alog.h"

__attribute__((constructor)) int JNI_OnPreLoad()
{
	LOGT();
	return 0;
}

void OpenTestFunc( __attribute__((unused)) JNIEnv * env, jobject, __attribute__((unused)) jobject appContext )
{
	LOGT();

#if 1
	int fd = open( "/dev/ashmem", 2 );
	LOGD( "fd : %d", fd );

	if( fd > 0 )
	{
		close( fd );
	}
#endif
}


// JNI_OnLoad
jint JNI_OnLoad(JavaVM* vm,  __attribute__((unused)) void* reserved)
{
	LOGT();

	JNIEnv * env = NULL;
	jint result = -1;

	if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK)
	{
		LOGE("ERROR: GetEnv failed");
		return result;
	}

	const char * classPath = "com/example/hooking/NativeLibrary";
	jclass clazz = env->FindClass( classPath );
	if (clazz == NULL)
	{
		LOGE( "Native registration unable to find class '%s'", classPath );
		return JNI_FALSE;
	}

	JNINativeMethod gMethods[3] = {
			//{ "init", 			"(Landroid/content/Context;)V", (void*) init },
			//{ "hooking", 		"(Landroid/content/Context;)V", (void*) HookingFunc },
			{ "open_test", 		"(Landroid/content/Context;)V", (void*) OpenTestFunc }
	};

	if (env->RegisterNatives( clazz, gMethods, 1 ) < 0 )
	{
		LOGE( "RegisterNatives failed for '%s'", classPath );
		return -1;
	}

	return JNI_VERSION_1_4;
}

