#include "AntiSpeedHackMain.h"

#include <stdio.h>
#include <unistd.h>
#include <dirent.h>

#ifdef __cplusplus
extern "C" {
#endif

void * child_debugger_detect_thread( void * );

void game_process_main( pid_t game_pid, pid_t child_pid ) {
	LOGT();

	pthread_t thread_id;
	pthread_create( &thread_id, NULL, child_debugger_detect_thread, (void*) child_pid );
}


void * child_debugger_detect_thread( void * param ) {
	LOGT();

	pid_t child_pid = ( pid_t ) param;

	char line[1024];
	char folder[256], filename[256];
	sprintf( folder, "/proc/%d/task", child_pid );
	LOGI( "ChildPid Task : %s", folder );

	struct dirent *entry;
	struct stat fstat;
	int pid = 0;
	int nCount = 0;

	DIR * dir = NULL;
	FILE * fp = NULL;
	while(( dir = opendir( folder )) != NULL) {
		usleep( 1000 );

		while((entry = readdir(dir)) != NULL) {
			usleep( 1000 );

			lstat(entry->d_name, &fstat);
			if(!S_ISDIR(fstat.st_mode)) {
				continue;
			}

			pid = atoi( entry->d_name );
			if( pid <= 0 ) {
				continue;
			}

			// Child process thread 는 생성되고 사라지면 않되기 때문에 열기 실패시에 앱을 종료 시킨다.
			sprintf( filename, "%s/%d/status", folder, pid );
			fp = fopen( filename, "rb" );
			if( fp == NULL ) {
				sprintf( filename, "%s Child Thread Closed", entry->d_name );
				continue;
			}

			while( fgets( line, 1024, fp )) {
				usleep( 1000 );

				if( strncmp( line, "TracerPid", 9 ) == 0 ) {
					int tracerPid = atoi( &line[10]);
					//LOGI( "%s : %d", filename, tracerPid );

					if( tracerPid != 0 ) {
						sprintf( filename, "Detected Child Thread TracerPid %s : %d", entry->d_name, tracerPid );
						EXIT_TIMER( 5, filename );
					}

					break;
				}
			}

			fclose( fp );
			fp = NULL;

			usleep( 1000 );
		}

		closedir(dir);
		usleep( 100000 );
	}

	sprintf( filename, "Exit ChildPid Task : %s", folder );
	EXIT_TIMER( 1, filename );
}

#ifdef __cplusplus
}
#endif
