#include <jni.h>
#include <android/log.h>

#include "jni_helper.h"
#include "util/maps_reader.h"
#include "util/util.h"

JNIEXPORT
jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
	LOGT();
	LOGD("%s %s", __DATE__, __TIME__ );

	for( int i = 0; i < 100; i++ ) {
		LOGD( "compose_string : %s", compose_string({'1','2','3'}));
	}

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
