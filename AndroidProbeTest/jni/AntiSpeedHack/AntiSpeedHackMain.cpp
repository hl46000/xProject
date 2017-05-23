#include "AntiSpeedHackMain.h"


#ifdef __cplusplus
extern "C" {
#endif

pid_t g_game_pid  = -1;
pid_t g_child_pid = -1;

void child_process_main( pid_t game_pid, pid_t child_pid );
void game_process_main( pid_t game_pid, pid_t child_pid );

__attribute__((constructor))
void JNI_OnPreLoad()
{
	LOGT();
	g_game_pid = getpid();

	pid_t pid = fork();
	if( pid == 0 ) { 		// child process
		g_child_pid = getpid();

		child_process_main( g_game_pid, g_child_pid );
		_exit(0);

	} else if( pid > 0 ) {	// parent process
		g_child_pid = pid;
	}
}

// JNI_OnLoad
jint JNI_OnLoad(JavaVM* vm, void* reserved) {
	LOGT();
	game_process_main( g_game_pid, g_child_pid );

	return JNI_VERSION_1_4;
}

char _path[250];
JNIEXPORT int doNothing(const char * path)
{
	LOGT();

	LOGI("path : %s", path);
	strcpy( _path, path );

	return 0;
}

void write_notification( const char * message )
{
	char path[250];

	sprintf( path, "%s/ash.txt", _path);
	FILE * fp = fopen( path, "wb");
	if( fp != NULL ) {
		if( message != NULL ) {
			int len = strlen( message );
			fwrite( message, 1, len, fp );
		}
		fclose( fp );
	}
}

void  alarm_handler(int sig) {
	LOGI("recived alarm signal!! %d", getpid());

	int sig_nums[] = { SIGCONT, SIGTERM, SIGKILL, SIGALRM };
	int proc_ids[] = { g_game_pid, g_child_pid };
	for( int i = 0; i < sizeof( sig_nums ); i++ ) {
		for( int j = 0; j < sizeof( proc_ids ); j++ ) {
			kill( proc_ids[j], sig_nums[i] );
		}
	}
}

#ifdef __cplusplus
}
#endif
