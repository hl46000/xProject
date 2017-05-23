#include "AntiSpeedHackMain.h"

#include <stdio.h>
#include <unistd.h>
#include <dirent.h>

#ifdef __cplusplus
extern "C" {
#endif

void * attatch_thread( void * );
void child_process_main( pid_t game_pid, pid_t child_pid ) {
	LOGT();
	LOGI("child_process_main");

	char line[1024];
	char folder[256], filename[256];
	sprintf( folder, "/proc/%d/task", game_pid );
	LOGI( "GamePid Task : %s", folder );

	pthread_t thread_id;
	pthread_create( &thread_id, NULL, attatch_thread, (void*) game_pid );

	struct dirent *entry;
	struct stat fstat;
	int pid = 0;

	DIR * dir = NULL;
	FILE * fp = NULL;
	while(( dir = opendir( folder )) != NULL ) {
		usleep( 1000 );

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

			// Game thread 는 생성되고 사라질수 있기 때문에 열기 실패시에 continue;
			sprintf( filename, "%s/%d/status", folder, pid );
			fp = fopen( filename, "rb" );
			if( fp == NULL ) {
				LOGI( "%s Game Thread Closed!!", entry->d_name );
				continue;
			}

			while( fgets( line, 1024, fp )) {
				usleep( 1000 );

				if( strncmp( line, "TracerPid", 9 ) == 0 ) {
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

			usleep( 1000 );
		}

		closedir(dir);
		usleep( 100000 );
	}

	LOGI( "Exit GamePid Task : %s", folder );
}


void * attatch_thread( void * param ) {
	LOGT();

	pid_t game_id = ( pid_t ) param;
	LOGD("GameID : %d", game_id );

	return NULL;
}

#ifdef __cplusplus
}
#endif
