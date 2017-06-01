#include <jni.h>
#include <android/log.h>

#include <stdio.h>
#include <pthread.h>
#include "util/util.h"

#include "smc_module.h"
#include "util/log.h"


__attribute__((constructor)) int JNI_OnPreLoad()
{
	SMC_START_TAG;

	change_logTag( "SMC_TEST" );
	LOGT();

	LOGD( "PID[%d] : TID[%d]", getpid(), gettid() );


	SMC_END_TAG;
	return 0;
}

// JNI_OnLoad
jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	LOGT();

	SMC_START_TAG;

	JNIEnv * env = NULL;
	jint result = -1;

	if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK)
	{
		LOGE("ERROR: GetEnv failed");
	}
	else
	{
		return JNI_VERSION_1_4;
	}

	SMC_END_TAG;

	return -1;
}
