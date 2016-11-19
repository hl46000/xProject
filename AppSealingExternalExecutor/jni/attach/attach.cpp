#include <jni.h>
#include <android/log.h>
#include <unistd.h>
#include <stdlib.h>
#include <pthread.h>
#include <sys/ptrace.h>
#include <errno.h>
#include <sys/wait.h>

// LOG
#define  LOG_TAG	"AppSealing_Attach"
#define  LOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGT()		__android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,"%s:%s",__FILE__,__func__ )

void * first_thread( void * pParam );
void * second_thread( void * pParam );
void * third_thread( void * pParam );

pid_t process_ids[3] = { 0, 0, 0 };

#define FIRST_PID_IDX		0
#define SECOND_PID_IDX		FIRST_PID_IDX+1
#define THIRD_PID_IDX		SECOND_PID_IDX+1

JNIEXPORT
jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
	LOGT();
	LOGD("%s %s", __DATE__, __TIME__ );

	process_ids[FIRST_PID_IDX] 	= getpid();
	process_ids[SECOND_PID_IDX]	= fork();
	if( process_ids[SECOND_PID_IDX] < 0 ) {

	} else if( process_ids[SECOND_PID_IDX] == 0 ) {
		// child process
		process_ids[SECOND_PID_IDX] = getpid();

		process_ids[THIRD_PID_IDX] = fork();
		if( process_ids[THIRD_PID_IDX] < 0 ) {

		} else if( process_ids[THIRD_PID_IDX] == 0 ) {
			process_ids[THIRD_PID_IDX] = getpid();

			// third process
			// TODO : thread 을 생성해서 first process 에 attach 시킨다.
			LOGD( "third process %d of %d", process_ids[THIRD_PID_IDX], process_ids[SECOND_PID_IDX] );
			pthread_t threadID;
			pthread_create( &threadID, NULL, third_thread, 0 );

		} else {
			// child process
			// TODO : thread 을 생성해서 third process 에 attach 시킨다.
			LOGD( "second process %d", process_ids[SECOND_PID_IDX] );
			pthread_t threadID;
			pthread_create( &threadID, NULL, second_thread, 0 );
		}
	} else {
		LOGD( "first process %d", process_ids[FIRST_PID_IDX] );
		// TODO : thread 을 생성해서 second process 에 attach 시킨다.
		pthread_t threadID;
		pthread_create( &threadID, NULL, first_thread, 0 );
	}

    return JNI_VERSION_1_6;
}

void * first_thread( void * pParam )
{
	LOGT();

	pid_t pid = getpid();
	pid_t tid = process_ids[SECOND_PID_IDX];
	LOGD( "[%d] first_thread => second pid : %d", pid, tid );

	LOGD( "[%d] =============> Try to attach : %d", pid, tid );
	if ( ptrace( PTRACE_ATTACH, tid, NULL, NULL ) == -1 )
	{
		LOGE( "[%d] Can't attach to %d (%d) : %s !!", pid, tid, errno, strerror( errno ));
		return NULL;
	}
	while( ptrace( PTRACE_CONT, tid, NULL, ( void* )SIGCONT ) == -1 ) usleep( 10000 );
	LOGD( "[%d] =============> Attach to %d!!", pid, tid );

	int status, wid;
	while( true )
	{
		usleep( 1000 );

		wid = waitpid( -1, &status, WUNTRACED );
		if ( wid == -1 || wid != tid )
		{
			continue;
		}

		// 메인 프로세스를 중지시킨 시그널 번호를 추출
		int signo = WSTOPSIG( status );
		if ( signo == 0 )
		{
			LOGD( "ERROR : signo is zero, pID[%d]", wid );
			continue;
		}
		LOGD( "[%d] PID[%d] Recv signal : %d", pid, wid, signo );

		while( ptrace( signo, wid, NULL, NULL ) == -1 ) usleep( 10000 );
		LOGD( "[%d] PID[%d] Passed signal : %d", pid, wid, signo );
	}

	return NULL;
}

void * second_thread( void * pParam ) {
	LOGT();

	pid_t pid = getpid();
	pid_t tid = process_ids[THIRD_PID_IDX];
	LOGD( "[%d] second_thread => third pid : %d", pid, tid );

	LOGD( "[%d] =============> Try to attach : %d", pid, tid );
	if ( ptrace( PTRACE_ATTACH, tid, NULL, NULL ) == -1 )
	{
		LOGE( "[%d] Can't attach to %d (%d) : %s !!", pid, tid, errno, strerror( errno ));
		return NULL;
	}
	while( ptrace( PTRACE_CONT, tid, NULL, ( void* )SIGCONT ) == -1 ) usleep( 10000 );
	LOGD( "[%d] =============> Attach to %d!!", pid, tid );

	int status, wid;
	while( true )
	{
		usleep( 1000 );

		wid = waitpid( -1, &status, WUNTRACED );
		if ( wid == -1 || wid != tid )
		{
			continue;
		}

		// 메인 프로세스를 중지시킨 시그널 번호를 추출
		int signo = WSTOPSIG( status );
		if ( signo == 0 )
		{
			LOGD( "ERROR : signo is zero, pID[%d]", wid );
			continue;
		}
		LOGD( "[%d] PID[%d] Recv signal : %d", pid, wid, signo );

		while( ptrace( signo, wid, NULL, NULL ) == -1 ) usleep( 10000 );
		LOGD( "[%d] PID[%d] Passed signal : %d", pid, wid, signo );
	}

	return NULL;
}

void * third_thread( void * pParam ) {
	LOGT();

	pid_t pid = getpid();
	pid_t tid = process_ids[FIRST_PID_IDX];
	LOGD( "[%d] third_thread => first pid : %d", pid, tid );

	LOGD( "[%d] =============> Try to attach : %d", pid, tid );
	if ( ptrace( PTRACE_ATTACH, tid, NULL, NULL ) == -1 )
	{
		LOGE( "[%d] Can't attach to %d (%d) : %s !!", pid, tid, errno, strerror( errno ));
		return NULL;
	}
	while( ptrace( PTRACE_CONT, tid, NULL, ( void* )SIGCONT ) == -1 ) usleep( 10000 );
	LOGD( "[%d] =============> Attach to %d!!", pid, tid );

	int status, wid;
	while( true )
	{
		usleep( 1000 );

		wid = waitpid( -1, &status, WUNTRACED );
		if ( wid == -1 || wid != tid )
		{
			continue;
		}

		// 메인 프로세스를 중지시킨 시그널 번호를 추출
		int signo = WSTOPSIG( status );
		if ( signo == 0 )
		{
			LOGD( "ERROR : signo is zero, pID[%d]", wid );
			continue;
		}
		LOGD( "[%d] PID[%d] Recv signal : %d", pid, wid, signo );

		while( ptrace( signo, wid, NULL, NULL ) == -1 ) usleep( 10000 );
		while( ptrace( PTRACE_CONT, wid, NULL, SIGCONT ) == -1 ) usleep( 10000 );

		LOGD( "[%d] PID[%d] Passed signal : %d", pid, wid, signo );
	}

	return NULL;
}
