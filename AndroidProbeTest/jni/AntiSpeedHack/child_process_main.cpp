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

void child_process_main( pid_t game_pid, pid_t child_pid ) {
	LOGT();
	LOGI("child_process_main");

	char buffer[1024];
	char folder[256], filename[256];
	sprintf( folder, "/proc/%d/task", game_pid );
	LOGI( "GamePid Task : %s", folder );

	usleep( 100000 );

	struct dirent *entry;
	struct stat fstat;
	int pid = 0;

	DIR * dir = NULL;
	FILE * fp = NULL;
	while(( dir = opendir( folder )) != NULL ) {
		usleep( 10000 );

		while((entry = readdir(dir)) != NULL ) {
			usleep( 10000 );

			lstat(entry->d_name, &fstat);
			if(!S_ISDIR(fstat.st_mode)) {
				continue;
			}

			pid = atoi( entry->d_name );
			if( pid <= 0 ) {
				continue;
			}

			// Game thread 는 생성되고 사라질수 있기 때문에 열기 실패시에 continue;
			sprintf( filename, "%s/%d/status", folder, pid );
			fp = fopen( filename, "rb" );
			if( fp == NULL ) {
				LOGI( "%s Game Thread Closed!!", entry->d_name );
				continue;
			}

			fread( buffer, 1024, 1, fp );
			fclose( fp );
			fp = NULL;
			usleep( 10000 );

			char * find_ptr = strstr( buffer, "TracerPid");
			if( find_ptr != NULL ) {
				int tracerPid = atoi( find_ptr + 10 );
				//LOGI( "%s : %d", filename, tracerPid );

				if( tracerPid != 0 ) {
					sprintf( filename, "Detected Game Thread TracerPid %s : %d", entry->d_name, tracerPid );
					EXIT_TIMER( 10, filename );

					closedir(dir);
				}
			}
			usleep( 10000 );
		}
		closedir(dir);

		usleep( 100000 );
	}

	LOGI( "Exit GamePid Task : %s", folder );
}

#ifdef __cplusplus
}
#endif
