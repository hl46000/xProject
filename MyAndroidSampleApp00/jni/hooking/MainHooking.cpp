#include <jni.h>
#include <android/log.h>

#include <stdio.h>
#include <pthread.h>
#include "util/util.h"

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <time.h>

#include <string.h>
#include <unistd.h>

#include "hooking/hooking.h"
#include "alog.h"

#define DELAY_TIME		usleep(10);

//#ifdef __cplusplus
//extern "C" {
//#endif

void init( JNIEnv * env, jobject, jobject appContext );
void HookingFunc( JNIEnv * env, jobject, jobject appContext );
void OpenTestFunc( JNIEnv * env, jobject, jobject appContext );

hooking hk;

__attribute__((constructor)) int JNI_OnPreLoad()
{
	LOGT();

	LOGD( "PID[%d] : TID[%d]", getpid(), gettid() );
	return 0;
}

#if 0
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
#endif


//int hook_open( char *filename, int access, int permission )
int (*__original_open)( const char*, int, ... );
int hook_open( char *filename, int flags, ... )
{
	int ret = -1;
	if( __original_open != 0 ) {
		ret = (*__original_open)( filename, flags );
	}

	LOGD("hook_open : [%d]%s %d", getpid(), filename, flags );
	return ret;
}

int (*__original_open64)( const char*, int, ... );
int hook_open64( char *filename, int flags, ... )
{
	int ret = -1;
	if( __original_open64 != 0 ) {
		ret = (*__original_open64)( filename, flags );
	}

	LOGD("hook_open64 : [%d]%s %d", getpid(), filename, flags );
	return ret;
}

int (*__original_openat)(int a, const char* b, int c, ...);
int hook_openat(int a, const char* b, int c, ...)
{
	int ret = -1;
	if( __original_openat != 0 ) {
		ret = (*__original_openat)( a,b,c );
	}

	LOGD("hook_openat : [%d] %d %s %d", getpid(), a, b, c );
	return ret;
}

int (*__original_openat64)(int a, const char* b, int c, ...);
int hook_openat64(int a, const char* b, int c, ...)
{
	int ret = -1;
	if( __original_openat64 != 0 ) {
		ret = (*__original_openat64)( a,b,c );
	}

	LOGD("hook_openat64 : [%d] %d %s %d", getpid(), a, b, c );
	return ret;
}


int (*__original_close)(int);
int hook_close( int fd )
{
	int ret = -1;
	if( __original_close != 0 ) {
		ret = (*__original_close)(fd);
	}

	LOGD("hook_close : [%d] %d", getpid(), fd );
	return ret;
}

ssize_t (*__original_read)(int, void *, size_t);
ssize_t hook_read( int fd, void * buff, size_t len )
{
	ssize_t ret = -1;
	if( __original_read != 0 ) {
		ret = (*__original_read)(fd,buff,len);
	}

	LOGD("hook_read : [%d] %d %dbyte", getpid(), fd, len );
	return ret;
}

ssize_t (*__original_pread)(int, void *, size_t, off_t);
ssize_t hook_pread(int a, void * b, size_t c, off_t d)
{
	ssize_t ret = -1;
	if( __original_pread != 0 ) {
		ret = (*__original_pread)( a, b, c, d );
	}

	LOGD("hook_pread : [%d] %d %d %d", getpid(), a, c, d );
	return ret;
}

ssize_t (*__original_pread64)(int, void *, size_t, off64_t);
ssize_t hook_pread64(int a, void * b, size_t c, off64_t d)
{
	ssize_t ret = -1;
	if( __original_pread64 != 0 ) {
		ret = (*__original_pread64)( a, b, c, d );
	}

	LOGD("hook_pread64 : [%d] %d %d %d", getpid(), a, c, d );
	return ret;
}

ssize_t (*__original_write)(int, const void *, size_t);
ssize_t hook_write( int fd, void * buff, size_t len )
{
	ssize_t ret = -1;
	if( __original_write != 0 ) {
		ret = (*__original_write)(fd,buff,len);
	}

	LOGD("hook_write : [%d] %d %dbyte", getpid(), fd, len );
	return ret;
}

int (*__original_creat)(const char*, mode_t);
int hook_creat( const char * fname, mode_t mode )
{
	int ret = -1;
	if( __original_creat != 0 ) {
		ret = (*__original_creat)( fname, mode);
	}

	LOGD("hook_creat : [%d] %s %d", getpid(), fname, mode );
	return ret;
}


int (*__original_creat64)(const char*, mode_t);
int hook_creat64( const char * fname, mode_t mode )
{
	int ret = -1;
	if( __original_creat64 != 0 ) {
		ret = (*__original_creat64)( fname, mode);
	}

	LOGD("hook_creat64 : [%d] %s %d", getpid(), fname, mode );
	return ret;
}

int (*__original_fcntl)(int, int, ...);
int hook_fcntl( int fd, int cmd, ... )
{
	int ret = -1;
	if( __original_fcntl != 0 ) {
		ret = (*__original_fcntl)(fd, cmd);
	}

	LOGD("hook_fcntl : [%d] %s %d", getpid(), fd, cmd );
	return ret;
}

off_t (*__original_lseek)(int, off_t, int);
off_t hook_lseek( int a, off_t b, int c )
{
	off_t ret = -1;
	if( __original_lseek != 0 ) {
		ret = (*__original_lseek)(a,b,c);
	}

	LOGD("hook_lseek : [%d] %d %d %d", getpid(), a, b, c );
	return ret;
}

off64_t (*__original_lseek64)(int, off64_t, int);
off64_t hook_lseek64( int a, off64_t b, int c )
{
	off64_t ret = -1;
	if( __original_lseek64 != 0 ) {
		ret = (*__original_lseek64)(a,b,c);
	}

	LOGD("hook_lseek64 : [%d] %d %d %d", getpid(), a, b, c );
	return ret;
}

FILE *(*__original_fopen)(const char *, const char *);
FILE *hook_fopen(const char * a , const char * b)
{
	FILE * ret = NULL;
	if( __original_fopen != 0 ) {
		ret = (*__original_fopen)(a,b);
	}

	LOGD("hook_fopen : [%d] %u %s %s", getpid(), (unsigned)ret, a, b );
	return ret;
}

int	 (*__original_fclose)(FILE *);
int hook_fclose( FILE * fp )
{
	int ret = -1;
	if( __original_fclose != 0 ) {
		ret = (*__original_fclose)(fp);
	}

	LOGD("hook_fclose : [%d] %u", getpid(), (unsigned)fp );
	return ret;
}

int	 (*__original_fseek)(FILE *, long, int);
int hook_fseek( FILE * fp, long a, int b )
{
	int ret = -1;
	if( __original_fseek != 0 ) {
		ret = (*__original_fseek)(fp, a, b );
	}

	LOGD("hook_fseek : [%d] %u %d %d", getpid(), (unsigned)fp, a, b );
	return ret;
}

size_t	 (*__original_fread)(void *, size_t, size_t, FILE *);
size_t	 hook_fread(void *a, size_t b, size_t c, FILE * fp)
{
	size_t ret = -1;
	if( __original_fread != 0 ) {
		ret = (*__original_fread)(a,b,c,fp);
	}

	LOGD("hook_fread : [%d] %u %d %d", getpid(), (unsigned)fp, b, c );
	return ret;
}

size_t	 (*__original_fwrite)(void *, size_t, size_t, FILE *);
size_t	 hook_fwrite(void *a, size_t b, size_t c, FILE * fp)
{
	size_t ret = -1;
	if( __original_fwrite != 0 ) {
		ret = (*__original_fwrite)(a,b,c,fp);
	}

	LOGD("hook_fwrite : [%d] %u %d %d", getpid(), (unsigned)fp, b, c );
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

void* (*__original_mmap64)(void*, size_t, int, int, int, off64_t);
void* hook_mmap64(void* a, size_t b, int c, int d, int e, off64_t f)
{
	void * ret = NULL;
	if( __original_mmap64 != 0 ) {
		ret = (*__original_mmap64)(a,b,c,d,e,f);
	}

	LOGD("hook_mmap64 : [%d] %d %d %d %d %d", getpid(), b, c, d, e, f );
	return ret;
}

void* (*__original_dlopen)(const char*, int);
void* hook_dlopen(const char* a, int b)
{
	void * ret = NULL;
	if( __original_dlopen != 0 ) {
		ret = (*__original_dlopen)(a,b);
	}

	LOGD("hook_dlopen : [%d] %s %d", getpid(), a, b );
	return ret;
}

int   (*__original_dlclose)(void*);
int   hook_dlclose(void*  a)
{
	int ret = -1;
	if( __original_dlclose != 0 ) {
		ret = (*__original_dlclose)(a);
	}

	LOGD("hook_dlclose : [%d]", getpid() );
	return ret;
}

void* (*__original_dlsym)(void*, const char*);
void* hook_dlsym(void* a, const char* b)
{
	void * ret = NULL;
	if( __original_dlsym != 0 ) {
		ret = (*__original_dlsym)(a,b);
	}

	LOGD("hook_dlsym : [%d] %s", getpid(), b );
	return ret;
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
			{ "hooking", 		"(Landroid/content/Context;)V", (void*) HookingFunc },
			{ "open_test", 		"(Landroid/content/Context;)V", (void*) OpenTestFunc }
	};

	if (env->RegisterNatives( clazz, gMethods, 3 ) < 0 )
	{
		LOGE( "RegisterNatives failed for '%s'", classPath );
		return -1;
	}

	return JNI_VERSION_1_4;
}

void init( __attribute__((unused)) JNIEnv * env, __attribute__((unused)) jobject obj, __attribute__((unused)) jobject appContext )
{
	LOGT();
}

void HookingFunc( __attribute__((unused)) JNIEnv * env, __attribute__((unused)) jobject obj, __attribute__((unused)) jobject appContext )
{
	LOGT();

	if( hk.get_target_module( getpid() ) > 0 ) {
		//hk.try_hooking( "ctime", (unsigned)hook_ctime );
		//hk.try_hooking( "sleep", (unsigned)hook_sleep );
		//hk.try_hooking( "clock_gettime", (unsigned)hook_clock_gettime );
		//hk.try_hooking( "gettimeofday", (unsigned)hook_gettimeofday );

		__original_open 	= hk.try_hooking( "open", (unsigned)hook_open );
		__original_open64 	= hk.try_hooking( "open64", (unsigned)hook_open64 );
		__original_close 	= hk.try_hooking( "close", (unsigned)hook_close );
		__original_read 	= hk.try_hooking( "read", (unsigned)hook_read );
		__original_write	= hk.try_hooking( "write", (unsigned)hook_write );
		__original_creat	= hk.try_hooking( "creat", (unsigned)hook_creat );
		__original_creat64	= hk.try_hooking( "creat64", (unsigned)hook_creat64 );
		__original_fcntl	= hk.try_hooking( "fcntl", (unsigned)hook_fcntl );
		__original_lseek	= hk.try_hooking( "lseek", (unsigned)hook_lseek );
		__original_lseek64	= hk.try_hooking( "lseek64", (unsigned)hook_lseek64 );

		__original_openat	= hk.try_hooking( "openat", (unsigned)hook_openat );
		__original_openat64	= hk.try_hooking( "openat64", (unsigned)hook_openat64 );
		__original_pread	= hk.try_hooking( "pread", (unsigned)hook_pread );
		__original_pread64	= hk.try_hooking( "pread64", (unsigned)hook_pread64 );

		__original_fopen	= hk.try_hooking( "fopen", (unsigned)hook_fopen );
		__original_fclose	= hk.try_hooking( "fclose", (unsigned)hook_fclose );
		__original_fseek	= hk.try_hooking( "fseek", (unsigned)hook_fseek );
		__original_fread	= hk.try_hooking( "fread", (unsigned)hook_fread );
		__original_fwrite	= hk.try_hooking( "fwrite", (unsigned)hook_fwrite );

		__original_mmap		= hk.try_hooking( "mmap", (unsigned)hook_mmap );
		__original_mmap64	= hk.try_hooking( "mmap64", (unsigned)hook_mmap64 );

		//__original_dlopen	= hk.try_hooking( "dlopen", (unsigned)hook_dlopen );
		//__original_dlclose	= hk.try_hooking( "dlclose", (unsigned)hook_dlclose );
		//__original_dlsym	= hk.try_hooking( "dlsym", (unsigned)hook_dlsym );
	}
}


void OpenTestFunc( __attribute__((unused)) JNIEnv * env, jobject, __attribute__((unused)) jobject appContext )
{
	LOGT();
	int fd = open( "/dev/ashmem", 2 );
	LOGD( "fd : %d", fd );

	if( fd > 0 )
	{
		close( fd );
	}
}

//#ifdef __cplusplus
//}
//#endif
