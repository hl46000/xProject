#include <jni.h>
#include "alog.h"

__attribute__((constructor)) int JNI_OnPreLoad()
{
	LOGT();
	return 0;
}

// JNI_OnLoad
jint JNI_OnLoad(JavaVM* vm,  __attribute__((unused)) void* reserved)
{
	LOGT();

	return JNI_VERSION_1_4;
}

