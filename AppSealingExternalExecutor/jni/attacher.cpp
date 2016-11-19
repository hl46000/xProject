#include <jni.h>
#include <android/log.h>

#include <unistd.h>

// LOG
#define  LOG_TAG	"AppSealing_ATTACHER"
#define  LOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGT()		__android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,"%s:%s",__FILE__,__func__ )

int main( int argc, char * argv[] )
{
	LOGT();
	LOGD( "argc : %d", argc );

	if( argc > 0 )
	{
		for( int i = 0; i < argc; i++ )
		{
			LOGD( "argv[%d] = %s", i, argv[i] );
		}
	}

	return 100;
}
