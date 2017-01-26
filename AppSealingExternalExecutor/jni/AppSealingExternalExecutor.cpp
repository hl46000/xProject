#include <jni.h>
#include <android/log.h>

#include "alog.h"
#include "util/maps_reader.h"

JNIEXPORT
jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
	LOGT();
	LOGD("%s %s", __DATE__, __TIME__ );

	maps_reader maps( getpid());
	maps.read();
	maps.print( NULL );

    return JNI_VERSION_1_6;
}

JNIEXPORT
void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved)
{
	LOGT();
}
