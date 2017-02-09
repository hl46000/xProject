#include <jni.h>
#include <android/log.h>

#include "jni_helper.h"
#include "util/maps_reader.h"
#include "util/util.h"

#include <dlfcn.h>

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

	char path[1024];
	sprintf( path, "%s", "libattach.so");

	void* handle = dlopen( NULL, RTLD_LAZY );
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

    return JNI_VERSION_1_6;
}

JNIEXPORT
void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved)
{
	LOGT();
}
