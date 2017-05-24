#include "AntiSpeedHackMain.h"

#include <stdio.h>
#include <unistd.h>
#include <dirent.h>

#include <sys/ptrace.h>
#include <sys/prctl.h>
#include <unistd.h>
#include <pthread.h>
#include <sys/wait.h>

#ifdef __cplusplus
extern "C" {
#endif

extern pid_t anti_speed_thread_id;
extern pid_t child_debugger_detect_thread_id;

void game_process_main( pid_t game_pid, pid_t child_pid )
{
	if( anti_speed_thread_id == -1 ) {
		EXIT_TIMER( 10, "Anti-Speed Thread not running" );
	}

	if( child_debugger_detect_thread_id == -1 ) {
		EXIT_TIMER( 10, "Child debugger detect Thread not running" );
	}
}
#include <signal.h>
void * child_debugger_detect_thread( void * param )
{
	pid_t * tid = ( pid_t * ) param;
	* tid = gettid();

	LOGT();
	pid_t child_pid = getpid();

	char buffer[1024];
	char folder[256], filename[256];
	sprintf( folder, "/proc/%d/task", child_pid );
	LOGI( "ChildPid Task : %s", folder );

	struct dirent *entry;
	struct stat fstat;
	int pid = 0;
	int nCount = 0;
	int status;

	DIR * dir = NULL;
	FILE * fp = NULL;
	while(( dir = opendir( folder )) != NULL) {
		usleep( 10000 );

		/*
		pid = waitpid( -1, &status, WUNTRACED );
		if( pid != child_pid ) {
			int signo = WSTOPSIG( status );
			if ( signo != 0 ) {
				ptrace( PTRACE_CONT, pid, NULL, signo );
			}
		}
		*/

		while((entry = readdir(dir)) != NULL) {
			usleep( 10000 );

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

			fread( buffer, 1024, 1, fp );
			fclose( fp );
			fp = NULL;
			usleep( 10000 );

			char * find_ptr = strstr( buffer, "TracerPid");
			if( find_ptr != NULL ) {
				int tracerPid = atoi( find_ptr + 10 );
				//LOGI( "%s : %d", filename, tracerPid );

				if( tracerPid != 0 && tracerPid != child_pid ) {
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

	sprintf( filename, "Exit ChildPid Task : %s", folder );
	EXIT_TIMER( 1, filename );
}

#include <linux/kernel.h>       /* for struct sysinfo */
#include <sys/sysinfo.h>

void * anti_speed_hack_thread_function( void * param )
{
	pid_t * tid = ( pid_t * ) param;
	* tid = gettid();

	LOGT();

	char strTemp[64] = { 0, };
	struct timeval 	_timeval;
	struct timespec _timespec;
	struct sysinfo 	_s_info;

	gettimeofday( &_timeval, NULL );
	clock_gettime(CLOCK_MONOTONIC, &_timespec);
	sysinfo(&_s_info);

	unsigned long t1 = _s_info.uptime;
	unsigned long t2 = _timeval.tv_sec;
	unsigned long t3 = _timespec.tv_sec;

	long d1 = 0;
	long d2 = 0;
	long d3 = 0;

	long m = _s_info.uptime;

	while( t1 > 0 ) {
		sleep( 1 );

		sysinfo(&_s_info);
		gettimeofday( &_timeval, NULL );
		clock_gettime(CLOCK_MONOTONIC, &_timespec);

		d1 = _s_info.uptime;
		if( d1 - m > 2 ) {
			EXIT_TIMER( 10, "Time stopped!!" );
			break;
		}
		m = d1;

		d1 = d1 - t1;
		d2 = _timeval.tv_sec  	- t2;
		d3 = _timespec.tv_sec 	- t3;

		if( abs( d2 - d1 ) > 2 ) {
			EXIT_TIMER( 10, "TIME VALUE CHANGED ( gettimeofday )" );
			break;
		}

		if( abs( d3 - d1 ) > 2 ) {
			EXIT_TIMER( 10, "TIME VALUE CHANGED ( clock_gettime )" );
			break;
		}

		// LOGD("Base time : %d, gTime : %d, cTime : %d", d1, d2, d3 );
	}

	return NULL;
}


#ifdef __cplusplus
}
#endif
