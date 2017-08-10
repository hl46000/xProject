#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/ptrace.h>
#include <sys/prctl.h>
#include <sys/stat.h>
#include <pthread.h>

#define  LOG_TAG	"TEST02"
#define  LOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGT()		__android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,"%s:%s",__FILE__,__func__ )

void init( JNIEnv * env, jobject, jobject appContext );

void* _main_thread_routine_( void* param );

// JNI_OnLoad
jint JNI_OnLoad( JavaVM* vm, void* )
{
	LOGT();

	JNIEnv * env = NULL;
	jint result = -1;

	if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK)
	{
		LOGE("ERROR: GetEnv failed");
		return result;
	}

	const char * classPath = "com/example/myandroidsampleapp00/NativeLibrary2";
	jclass clazz = env->FindClass( classPath );
	if (clazz == NULL)
	{
		LOGE( "Native registration unable to find class '%s'", classPath );
		return JNI_FALSE;
	}

	JNINativeMethod gMethods[1] = {
			{ "init", 			"(Landroid/content/Context;)V", 		(void*) init }
	};

	if (env->RegisterNatives( clazz, gMethods, 1 ) < 0 )
	{
		LOGE( "RegisterNatives failed for '%s'", classPath );
		return -1;
	}

	return JNI_VERSION_1_4;
}

pid_t main_pid 	= -1;
pid_t child_pid	= -1;
void SIGCHLD_HANDLER( int sig )
{
	LOGD( "SIGCHLD_HANDLER sig : %d", sig );

	pid_t pid;
	int status;

	while(( pid = waitpid( -1, &status, WNOHANG )) > 0 ) sleep( 1 );

	//if( pid == 0 ) return;
	int signo = WSTOPSIG( status );

	if( !WIFEXITED( status )) {
		LOGD( "[%d]status : %d, signo : %d", pid, status, signo );

		ptrace( PTRACE_CONT, pid, 0, signo );
		//_exit( 0 );
	}
}


void init( JNIEnv * /*env*/, jobject, jobject /*appContext*/ )
{
	LOGT();

	main_pid 	= getpid();
	child_pid 	= fork();

	struct sigaction sa;
	sigemptyset(&sa.sa_mask);

	switch( child_pid ) {
	case -1 : break;
	case 0 :	// child process
		setsid();

		sa.sa_handler = SIGCHLD_HANDLER;
		sa.sa_flags = 0;
		sigaction(SIGCHLD, &sa, NULL);

		child_pid = getpid();
		while( ptrace( PTRACE_ATTACH, main_pid, 0, 0 ) > 0 ) sleep(1);
		while( ptrace( PTRACE_CONT, main_pid, 0, 0 ) > 0 ) sleep(1);

		for( int i = 0; i < 15; i++ ) {
			LOGD( "child process : %d, count = %d", child_pid, i );
			sleep( 1 );
		}
		while( ptrace( PTRACE_DETACH, main_pid, 0, 0 ) > 0 ) sleep(1);
		while( ptrace( PTRACE_CONT, main_pid, 0, 0 ) > 0 ) sleep(1);
		sleep( 1 );

		_exit(0);
		break;

	default :	// main process
		sa.sa_handler = SIGCHLD_HANDLER;
		sa.sa_flags = SA_NODEFER | SA_NOCLDWAIT;
		sa.sa_restorer = NULL;
		sigaction(SIGCHLD, &sa, NULL);

		pthread_t threadID;
		pthread_create( &threadID, NULL, _main_thread_routine_, NULL );
		break;
	}
}


void* _main_thread_routine_( void * /*param*/ )
{
	while( ptrace( PTRACE_DETACH, child_pid, 0, 0 ) > 0 ) { sleep( 1 ); }
	while( ptrace( PTRACE_CONT, child_pid, 0, 0 ) > 0 ) { sleep( 1 ); }

	for( int i = 0; i < 15; i++ ) {
		LOGD( "main process : %d, count = %d", main_pid, i );
		sleep( 1 );
	}

	return NULL;
}
