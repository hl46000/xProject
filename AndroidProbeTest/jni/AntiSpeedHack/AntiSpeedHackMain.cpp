#include "AntiSpeedHackMain.h"


#ifdef __cplusplus
extern "C" {
#endif

pid_t anti_speed_thread_id = -1;
pid_t child_debugger_detect_thread_id = -1;

void child_process_main( pid_t game_pid, pid_t child_pid );
void game_process_main( pid_t game_pid, pid_t child_pid );

void * child_debugger_detect_thread( void * );
void * anti_speed_hack_thread_function( void * );

__attribute__((constructor))
void JNI_OnPreLoad()
{
	LOGT();

	pthread_t thread_id;
	pthread_create( &thread_id, NULL, anti_speed_hack_thread_function, (void*)&anti_speed_thread_id );
}

// JNI_OnLoad
jint JNI_OnLoad(JavaVM* vm, void* reserved) {
	LOGT();
#if 1
#else
	g_game_pid = getpid();

	pid_t pid = fork();
	if( pid == 0 ) { 		// child process
		g_child_pid = getpid();

		child_process_main( g_game_pid, g_child_pid );
		_exit(0);

	} else if( pid > 0 ) {	// parent process
		g_child_pid = pid;
	}
#endif
	pthread_t thread_id;
	pthread_create( &thread_id, NULL, child_debugger_detect_thread, (void*)&child_debugger_detect_thread_id );

	return JNI_VERSION_1_4;
}

char _path[250];
JNIEXPORT int doNothing(const char * path)
{
	LOGT();

	LOGI("path : %s", path);
	strcpy( _path, path );

	if( anti_speed_thread_id == -1 ) {
		EXIT_TIMER( 5, "Anti-Speed Thread not running" );
	}

	if( child_debugger_detect_thread_id == -1 ) {
		EXIT_TIMER( 5, "Child debugger detect Thread not running" );
	}

	return 0;
}

void write_notification( const char * message )
{
	char path[250];

	sprintf( path, "%s/ash.txt", _path);
	FILE * fp = fopen( path, "wb");
	if( fp != NULL ) {
		LOGI("writed : %s", path );
		/*
		if( message != NULL ) {
			int len = strlen( message );
			fwrite( message, 1, len, fp );
		}
		*/
		fclose( fp );
	}
}

void  alarm_handler(int sig) {
	LOGI("recived alarm signal!! %d", getpid());

	_exit(0);
	int sig_nums[] = { SIGCONT, SIGTERM, SIGKILL, SIGALRM };
	//int proc_ids[] = { g_game_pid, g_child_pid };
	int proc_ids[] = { getpid()  };
	for( int i = 0; i < sizeof( sig_nums ); i++ ) {
		for( int j = 0; j < sizeof( proc_ids ); j++ ) {
			kill( proc_ids[j], sig_nums[i] );
		}
	}
}

#ifdef __cplusplus
}
#endif
