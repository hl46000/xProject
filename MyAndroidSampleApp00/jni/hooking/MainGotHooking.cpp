#include <jni.h>
#include <android/log.h>

#include <stdio.h>
#include <pthread.h>
#include "util/util.h"

#include <sys/types.h>
#include <sys/stat.h>
#include <sys/mman.h>
#include <fcntl.h>
#include <time.h>

#include <string.h>
#include <unistd.h>
#include <dlfcn.h>

//#include "hooking/hooking.h"
#include "alog.h"

#define DELAY_TIME		usleep(10);

void init( JNIEnv * env, jobject, jobject appContext );
void HookingFunc( JNIEnv * env, jobject, jobject appContext );
void OpenTestFunc( JNIEnv * env, jobject, jobject appContext );

__attribute__((constructor)) int JNI_OnPreLoad()
{
	LOGT();
	LOGD( "PID[%d] : TID[%d]", getpid(), gettid() );

	return 0;
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
			{ "init", 			"(Landroid/content/Context;)V", (void*) init },
			{ "hooking", 		"(Landroid/content/Context;)V", (void*) HookingFunc }
			//{ "open_test", 		"(Landroid/content/Context;)V", (void*) OpenTestFunc }
	};

	if (env->RegisterNatives( clazz, gMethods, 2 ) < 0 )
	{
		LOGE( "RegisterNatives failed for '%s'", classPath );
		return -1;
	}



	return JNI_VERSION_1_6;
}

void dumpMaps() {
	char temp[512];
	sprintf( temp, "/proc/%d/maps", getpid());
	FILE* fp = fopen( temp, "r");

	if (fp != NULL) {
		while(fgets( temp, 512, fp) != NULL) {
			LOGD( "%s", temp );
		}
		fclose(fp);
	}
}

void HookingFunc( __attribute__((unused)) JNIEnv * env, __attribute__((unused)) jobject obj, __attribute__((unused)) jobject appContext )
{
	LOGT();
}

bool isArt()
{
	FILE * fp = popen( "getprop persist.sys.dalvik.vm.lib", "r" );
	if ( fp != NULL) {
		char buffer[128] = { 0, };
		fread( buffer, 1, 128, fp );
		pclose( fp );

		return strncmp( buffer, "libart.so", strlen("libart.so")) == 0;
	}
	return false;
}

#ifdef __cplusplus
extern "C" {
#endif

static void* (*__original_printf)(const char *format, ...);
static int hook_printf(const char *format, ...) {
	LOGD("hook_printf : [%d] %s", getpid(), format );

	va_list args;
	va_start(args, format);
	int ret = __android_log_vprint(ANDROID_LOG_DEBUG, "TEST_Native", format, args);
	va_end(args);

	return ret;
}



static ssize_t (*__original_read)(int, void *, size_t);
static ssize_t hook_read( int fd, void * buff, size_t len )
{
	ssize_t ret = -1;
	if( __original_read != 0 ) {
		ret = (*__original_read)(fd,buff,len);
	}

	LOGD("hook_read : [%d] %d %dbyte", getpid(), fd, len );
	return ret;
}

static FILE *(*__original_fopen)(const char *, const char *);
static FILE *hook_fopen(const char * a , const char * b)
{
	FILE * ret = NULL;
	if( __original_fopen != 0 ) {
		ret = (*__original_fopen)(a,b);
	}

	LOGD("hook_fopen : [%d] %u %s %s", getpid(), (unsigned)ret, a, b );
	return ret;
}

static void* (*__original_dlopen)(const char*, int);
static void* hook_dlopen(const char* a, int b)
{
	void * ret = NULL;
	if( __original_dlopen != 0 ) {
		ret = (*__original_dlopen)(a,b);
	}

	LOGD("hook_dlopen : [%d] %s %d", getpid(), a, b );
	return ret;
}

static int (*__original_fcntl)(int, int, ...);
static int hook_fcntl( int fd, int cmd, ... )
{
	int ret = -1;
	if( __original_fcntl != 0 ) {
		ret = (*__original_fcntl)(fd, cmd);
	}

	LOGD("hook_fcntl : [%d] %s %d", getpid(), fd, cmd );
	return ret;
}

void* (*__original_mmap)(void*, size_t, int, int, int, off_t);
void* hook_mmap(void* a, size_t b, int c, int d, int e, off_t f)
{
	void * ret = NULL;
	if( __original_mmap != 0 ) {
		ret = (*__original_mmap)(a,b,c,d,e,f);
	}

	LOGD("hook_mmap : [%d] %d %d %d %d %d", getpid(), b, c, d, e, f );
	return ret;
}

#ifdef __cplusplus
}
#endif

static int (*__original_open)( const char*, int );
static int hook_open( char *filename, int flags )
{
	int ret = -1;
	if( __original_open != 0 ) {
		ret = (*__original_open)( filename, flags );
	}

	LOGD("hook_open : [%d] %s %d %d", getpid(), filename, flags, ret );
	return ret;
}


#include "got_hook.h"

#define LIBC_PATH "/system/lib/libc.so"
#define LINKER_PATH "/system/bin/linker"

got_hook hook;
void init( __attribute__((unused)) JNIEnv * env, __attribute__((unused)) jobject obj, __attribute__((unused)) jobject appContext )
{
	LOGT();


	//dumpMaps();
	//__original_fopen 	= hook.hooking( "fopen", 	(unsigned long) fopen, 	(unsigned long) hook_fopen );
	__original_open 	= hook.hooking( "open" , 	open, 	hook_open );
	//__original_read 	= hook.hooking( "read",		(unsigned long) read, 	(unsigned long) hook_read );
	//__original_dlopen 	= hook.hooking( "dlopen",	(unsigned long) dlopen, (unsigned long) hook_dlopen );
	//__original_fcntl	= hook.hooking( "fcntl",	(unsigned long) fcntl, 	(unsigned long) hook_fcntl );
	//__original_mmap		= hook.hooking( "mmap",		(unsigned long) mmap, 	(unsigned long) hook_mmap );
}

