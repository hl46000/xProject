#include "AntiSpeedHackMain.h"

#include <stdio.h>
#include <unistd.h>
#include <dirent.h>

#include <signal.h>
#include <sys/ptrace.h>
#include <sys/prctl.h>
#include <sys/wait.h>
#include <errno.h>

#ifdef __cplusplus
extern "C" {
#endif

extern pthread_t anti_speed_thread_id;
extern pthread_t child_debugger_detect_thread_id;

pthread_t attatch_thread_id = -1;

void * attatch_thread( void * );
void child_process_main( pid_t game_pid, pid_t child_pid ) {
	LOGT();
	LOGI("child_process_main");

	char buffer[1024];
	char folder[256], filename[256];
	sprintf( folder, "/proc/%d/task", game_pid );
	LOGI( "GamePid Task : %s", folder );

	LOGI( "anti_speed_thread_id : %ld", anti_speed_thread_id );
	LOGI( "child_debugger_detect_thread_id : %ld", child_debugger_detect_thread_id );

	bool b_anti_speed_thread_flag = false;
	bool b_child_debugger_detect_thread = false;

	pthread_create( &attatch_thread_id, NULL, attatch_thread, (void*) game_pid );

	struct dirent *entry;
	struct stat fstat;
	int pid = 0;

	DIR * dir = NULL;
	FILE * fp = NULL;
	while(( dir = opendir( folder )) != NULL ) {
		usleep( 1000 );

		b_anti_speed_thread_flag = false;
		b_child_debugger_detect_thread = false;

		while((entry = readdir(dir)) != NULL ) {
			usleep( 1000 );

			lstat(entry->d_name, &fstat);
			if(!S_ISDIR(fstat.st_mode)) {
				continue;
			}

			pid = atoi( entry->d_name );
			if( pid <= 0 ) {
				continue;
			}

			if( pid == anti_speed_thread_id ) {
				b_anti_speed_thread_flag = true;

			} else if( pid == child_debugger_detect_thread_id ) {
				b_child_debugger_detect_thread = true;
			}

			// Game thread 는 생성되고 사라질수 있기 때문에 열기 실패시에 continue;
			sprintf( filename, "%s/%d/status", folder, pid );
			fp = fopen( filename, "rb" );
			if( fp == NULL ) {
				LOGI( "%s Game Thread Closed!!", entry->d_name );
				continue;
			}

#if 1
			fread( buffer, 1024, 1, fp );
			fclose( fp );
			fp = NULL;

			char * find_ptr = strstr( buffer, "TracerPid");
			if( find_ptr != NULL ) {
				int tracerPid = atoi( find_ptr + 10 );
				LOGI( "%s : %d", filename, tracerPid );

				if( tracerPid != 0 && tracerPid != child_pid ) {
					sprintf( filename, "Detected Game Thread TracerPid %s : %d", entry->d_name, tracerPid );
					EXIT_TIMER( 5, filename );

					closedir(dir);
				}
			}
#else
			while( fgets( buffer, 1024, fp )) {
				usleep( 1000 );

				if( strncmp( buffer, "TracerPid", 9 ) == 0 ) {
					int tracerPid = atoi( &line[10]);
					//LOGI( "%s : %d", filename, tracerPid );

					if( tracerPid != 0 && tracerPid != child_pid ) {
						sprintf( filename, "Detected Game Thread TracerPid %s : %d", entry->d_name, tracerPid );
						EXIT_TIMER( 5, filename );

						fclose( fp );
						closedir(dir);
					}

					break;
				}
			}

			fclose( fp );
			fp = NULL;
#endif
			usleep( 1000 );
		}
		closedir(dir);

		if( !b_anti_speed_thread_flag ) {
			EXIT_TIMER( 5, "anti-speed-hack thread not found" );
		}
		if( !b_child_debugger_detect_thread ) {
			EXIT_TIMER( 5, "anti-debugging thread not found" );
		}

		usleep( 100000 );
	}

	LOGI( "Exit GamePid Task : %s", folder );
	attatch_thread_id = -1;
}


void * attatch_thread( void * param ) {
	LOGT();

	pid_t game_id = ( pid_t ) param;
	LOGD("GameID : %d", game_id );

	// attach parent process
	while( ptrace( PTRACE_ATTACH, game_id, 0, 0 ) == -1 ) usleep( 1000 );
	while( ptrace( PTRACE_CONT, game_id, 0, 0 ) == -1  )usleep( 1000 );

	pid_t pid;
	int status, signo;
	while( attatch_thread_id != -1 ) {
		// get signal from processes
		pid = waitpid( -1, &status, 0);
		signo = WSTOPSIG( status );

		if( pid != game_id || signo == 0 ) continue;

		if( signo == SIGSEGV ) {
			while( ptrace( PTRACE_DETACH, game_id, 0, 0 ) == -1 ) usleep( 1000 );
		}
		// waitpid 을 깨운 process 에게 원래 시그널을 전달한다.
		ptrace( PTRACE_CONT, pid, NULL, ( void* )signo );

		if( signo == SIGSEGV ) {
			while( ptrace( PTRACE_ATTACH, game_id, 0, 0 ) == -1 ) usleep( 1000 );
			while( ptrace( PTRACE_CONT, game_id, 0, 0 ) == -1  )usleep( 1000 );
		}
	}

	return NULL;
}

#ifdef __cplusplus
}
#endif
