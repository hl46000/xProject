#include <jni.h>
#include <android/log.h>

// LOG
#define  LOG_TAG	"AppSealing_Executor"
#define  LOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGT()		__android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,"%s:%s",__FILE__,__func__ )

JNIEXPORT
jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
	LOGT();
	LOGD("%s %s", __DATE__, __TIME__ );

    return JNI_VERSION_1_6;
}

JNIEXPORT
void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved)
{
	LOGT();
}
