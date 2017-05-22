#include <jni.h>
#include <android/log.h>

#include <stdio.h>
#include <pthread.h>
#include "util/util.h"

#include "hooking/hooking.h"

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <time.h>

#include "hooking/hooking.h"

#define  LOG_TAG	"AppSealing_SampleApp"
#define  LOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGT()		__android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,"%s:%s",__FILE__,__func__ )

#define DELAY_TIME		usleep(10);

#include <time.h>
char * hook_ctime ( const time_t * timer )
{
	LOGT();
	return ctime( timer );
}

int hook_gettimeofday(struct timeval *tv, struct timezone *tz)
{
	//LOGT();
	//tv->tv_usec += 3000;
	int ret = gettimeofday( tv, tz );

	static time_t hook_val = 0;
	static struct timeval o_tv = { 0,0 };
	if( o_tv.tv_sec < tv->tv_sec ) {
		memcpy( &o_tv, tv, sizeof( struct timeval ));
		hook_val += 60;
	}

	tv->tv_sec += hook_val;

	return ret;
}

unsigned int hook_sleep( unsigned int seconds)
{
	LOGD( "Called hook_sleep %d seconds", seconds );
	unsigned int ret = sleep( seconds );

	return ret;
}

int hook_clock_gettime(clockid_t clk_id, struct timespec *tp)
{
	int ret = clock_gettime( clk_id, tp );

	static unsigned int cnt = 0;
	cnt++;

	static time_t o_sec = tp->tv_sec;
	if( o_sec < tp->tv_sec ) {

		LOGD( "Called hook_clock_gettime function : %u count, %d sec", cnt, (int)( tp->tv_sec - o_sec ));

		o_sec = tp->tv_sec;
		cnt = 0;
	}
	return ret;
}

clock_t hook_clock(void)
{
	clock_t ret = clock();
	static clock_t inc_ret = 0;
	inc_ret += CLOCKS_PER_SEC;
	return ret + inc_ret;
}

int hook_open( char *filename, int access, int permission )
{
#if 0
	int len = strlen( filename );
	if( len > 3 && filename[len-3] == 'm' && filename[len-2] == 'e' && && filename[len-1] == 'm' ) {
		LOGE( "mem file open : %s", filename );
	}

	if( len > 4 && filename[len-4] == 'm' && filename[len-3] == 'a' && filename[len-2] == 'p' && && filename[len-1] == 's' ) {
		LOGE( "maps file open : %s", filename );
	}

	return open( filename, access, permission );
#endif
	return 0;
}

void HookingFunc( JNIEnv * env, jobject obj )
{
	LOGT();

	hooking hk;
	if( hk.get_target_module( getpid() ) > 0 ) {
		//hk.try_hooking( "ctime", (unsigned)hook_ctime );
		//hk.try_hooking( "sleep", (unsigned)hook_sleep );
		//hk.try_hooking( "clock_gettime", (unsigned)hook_clock_gettime );
		//hk.try_hooking( "gettimeofday", (unsigned)hook_gettimeofday );

		hk.try_hooking( "open", (unsigned)hook_open );
	}
}

// JNI_OnLoad
jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	LOGT();

	JNIEnv * env = NULL;
	jint result = -1;

	if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK)
	{
		LOGE("ERROR: GetEnv failed");
		return result;
	}

	const char * classPath = "com/example/androidprobetest/SimpleMainActivity";
	jclass clazz = env->FindClass( classPath );
	if (clazz == NULL)
	{
		LOGE( "Native registration unable to find class '%s'", classPath );
		return JNI_FALSE;
	}

	JNINativeMethod gMethods[1] = {
			{ "Hooking", 		"()V", (void*)HookingFunc }
	};

	if (env->RegisterNatives( clazz, gMethods, 1 ) < 0 )
	{
		LOGE( "RegisterNatives failed for '%s'", classPath );
		return -1;
	}

	return JNI_VERSION_1_4;
}


__attribute__((constructor)) int JNI_OnPreLoad()
{
	LOGT();

	LOGD( "PID[%d] : TID[%d]", getpid(), gettid() );
	return 0;
}
