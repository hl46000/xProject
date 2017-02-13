#include <stdio.h>
#include <stdlib.h>

#include "alog.h"
#include "jstr2str.h"
#include "maps_reader.h"

#include <sys/types.h>
#include <unistd.h>

#define PLATFORM_ANDROID 1
#include "AppSealingSecurity_Unreal.h"

#if 1
void smc_tag01_function()
{
	APPSEALING_ENCRYPTION_BEGIN
	LOGT();
	APPSEALING_ENCRYPTION_END
}
#endif

int smc_tag02_function()
{
	LOGT();
	APPSEALING_ENCRYPTION_BEGIN
	int value = 2;
	value ++;
	value += value;
	APPSEALING_ENCRYPTION_END_RETURN(value);
}

void init( JNIEnv * env, jobject, jobject appContext )
{
	LOGT();
	smc_tag01_function();

	int value = smc_tag02_function();
	LOGD( "value = %d", value );
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

	const char * classPath = "com/example/myandroidsampleapp03/NativeLibrary";
	jclass clazz = env->FindClass( classPath );
	if (clazz == NULL)
	{
		LOGE( "Native registration unable to find class '%s'", classPath );
		return JNI_FALSE;
	}

	JNINativeMethod gMethods[4] = {
			{ "init", 			"(Landroid/content/Context;)V", 		(void*) init }
	};

	if (env->RegisterNatives( clazz, gMethods, 1 ) < 0 )
	{
		LOGE( "RegisterNatives failed for '%s'", classPath );
		return -1;
	}

	return JNI_VERSION_1_4;
}
