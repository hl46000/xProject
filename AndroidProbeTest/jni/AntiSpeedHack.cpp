/*
 * AntiSpeedHack.cpp
 *
 *  Created on: 2016. 5. 10.
 *      Author: purehero
 */

#include <AntiSpeedHack.h>

#include <jni.h>
#include <android/log.h>

#include "smc_module.h"
//#include "util/log.h"

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include <sys/time.h>
#include <unistd.h>

#ifndef SMC_MODULE_H_
#define SMC_START_TAG(tagID)
#define SMC_END_TAG(tagID)
#endif

#ifdef __cplusplus
	extern "C" {
#endif

#if 1
#ifdef LOGI
#undef LOGI
#endif
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,"ASH",__VA_ARGS__)

#ifdef LOGT
#undef LOGT
#define LOGT()	__android_log_print(ANDROID_LOG_DEBUG,"ASH","%s:%s",__FILE__,__func__ )
#endif
#endif

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

void exit_timeout_15Sec_after_toast_show ( const char * message )
{
	//SMC_START_TAG( exit_timeout_15Sec_after_toast_show );
	signal(SIGALRM, SIG_DFL);
	alarm( 15 );	// 15초 이내 종료되지 않으면 alarm 으로 자동 종료 됨

	LOGI( "%s",message );
	write_notification( message );

	//SMC_END_TAG( exit_timeout_15Sec_after_toast_show );
}

void exit_timeout_10Sec_after_toast_show ( const char * message )
{
	//SMC_START_TAG( exit_timeout_15Sec_after_toast_show );
	signal(SIGALRM, SIG_DFL);
	alarm( 10 );	// 10초 이내 종료되지 않으면 alarm 으로 자동 종료 됨

	LOGI( "%s",message );
	write_notification( message );
	//SMC_END_TAG( exit_timeout_15Sec_after_toast_show );
}

void exit_timeout_5Sec_after_toast_show ( const char * message )
{
	//SMC_START_TAG( exit_timeout_15Sec_after_toast_show );
	signal(SIGALRM, SIG_DFL);
	alarm( 5 );	// 10초 이내 종료되지 않으면 alarm 으로 자동 종료 됨

	LOGI( "%s",message );
	write_notification( message );
	//SMC_END_TAG( exit_timeout_15Sec_after_toast_show );
}

/**************************************************
* ANTI Speed hack								  *
***************************************************/
pthread_t anti_speed_thread_id1 = 0;
pthread_t anti_speed_thread_id2 = 0;

#include <linux/kernel.h>       /* for struct sysinfo */
#include <sys/sysinfo.h>
void * start_anti_speed_hack_thread_function1 ( void * )
{
	LOGT();
	//SMC_START_TAG( start_anti_speed_hack_thread_function );

	char strTemp[64] = { 0, };
	struct timeval 	_timeval;
	struct sysinfo 	_s_info;

	gettimeofday( &_timeval, NULL );
	sysinfo(&_s_info);

	unsigned long t1 = _s_info.uptime;
	unsigned long t2 = _timeval.tv_sec;

	long d1 = 0;
	long d2 = 0;

	LOGI("Started anti speed hack module1");

	while( t1 > 0 ) {
		sleep( 2 );

		sysinfo(&_s_info);
		gettimeofday( &_timeval, NULL );

		d1 = _s_info.uptime 	- t1;
		d2 = _timeval.tv_sec  	- t2;

		if( d1 > 10 && anti_speed_thread_id2 == 0 ) {
			exit_timeout_5Sec_after_toast_show( "thread 2 not running" );
		}

		if( abs( d2 - d1 ) > 2 ) {
			exit_timeout_15Sec_after_toast_show( "TIME VALUE CHANGED ( gettimeofday )" );
		}

		// LOGI("Base time : %d, gTime : %d, cTime : %d", d1, d2, d3 );
	}

	anti_speed_thread_id1 = 0;

	//SMC_END_TAG( start_anti_speed_hack_thread_function );

	return NULL;
}

void * start_anti_speed_hack_thread_function2 ( void * )
{
	LOGT();
	//SMC_START_TAG( start_anti_speed_hack_thread_function );

	char strTemp[64] = { 0, };
	struct timespec _timespec;
	struct sysinfo 	_s_info;

	clock_gettime(CLOCK_MONOTONIC, &_timespec);
	sysinfo(&_s_info);

	unsigned long t1 = _s_info.uptime;
	unsigned long t3 = _timespec.tv_sec;

	long d1 = 0;
	long d3 = 0;

	LOGI("Started anti speed hack module2");

	while( t1 > 0 ) {
		sleep( 2 );

		sysinfo(&_s_info);
		clock_gettime(CLOCK_MONOTONIC, &_timespec);

		d1 = _s_info.uptime 	- t1;
		d3 = _timespec.tv_sec 	- t3;

		if( d1 > 10 && anti_speed_thread_id1 == 0 ) {
			exit_timeout_5Sec_after_toast_show( "thread 1 not running" );
		}

		if( abs( d3 - d1 ) > 2 ) {
			exit_timeout_15Sec_after_toast_show( "TIME VALUE CHANGED ( clock_gettime )" );
		}

		// LOGI("Base time : %d, gTime : %d, cTime : %d", d1, d2, d3 );
	}

	anti_speed_thread_id2 = 0;
	//SMC_END_TAG( start_anti_speed_hack_thread_function );

	return NULL;
}


void start_anti_speed_hack2()
{
	LOGT();

}


/**************************************************
* ANTI Debugging hack								  *
***************************************************/
pthread_t anti_debugging_thread_id2 = 0;

#include <stdio.h>
#include <unistd.h>
#include <dirent.h>
void * start_anti_debugging_thread_function2 ( void * )
{
	LOGT();

	LOGI("Started anti debugging module2");

	char line[1024];
	char folder[256], filename[256];
	sprintf( folder, "/proc/%d/task", getpid());
	LOGI( "folder : %s", folder );

	struct dirent *entry;
	struct stat fstat;
	int pid = 0;
	int nCount = 0;

	DIR * dir = NULL;
	FILE * fp = NULL;
	while( true ) {

		dir = opendir( folder );
		while((entry = readdir(dir)) != NULL)
		{
			lstat(entry->d_name, &fstat);
			if(!S_ISDIR(fstat.st_mode)) {
				continue;
			}

			pid = atoi( entry->d_name );
			if( pid <= 0 ) {
				continue;
			}

			sprintf( filename, "%s/%d/status", folder, pid );
			fp = fopen( filename, "rb" );
			if( fp == NULL ) {
				sprintf( filename, "%s thread not found", entry->d_name );
				exit_timeout_5Sec_after_toast_show( filename );
				break;
			}

			while( fgets( line, 1024, fp )) {
				if( strncmp( line, "TracerPid", 9 ) == 0 ) {
					int tracerPid = atoi( &line[10]);
					if( tracerPid != 0 ) {
						sprintf( filename, "%s TracerPid : %d", entry->d_name, tracerPid );
						exit_timeout_5Sec_after_toast_show( filename );

						fclose( fp );
						closedir(dir);

						return NULL;
					}

					break;
				}
			}

			fclose( fp );
			fp = NULL;
		}

		closedir(dir);
		sleep( 1 );

		if( nCount < 10 ) {
			nCount++;

		} else {
			if( anti_speed_thread_id1 == 0 ) {
				exit_timeout_5Sec_after_toast_show( "anti speed hack thread 1 not running!!" );
			}

			if( anti_speed_thread_id2 == 0 ) {
				exit_timeout_5Sec_after_toast_show( "anti speed hack thread 2 not running!!" );
			}
		}
	}

	return NULL;
}



void start_anti_debugging2()
{
	LOGT();
	pthread_create( &anti_debugging_thread_id2, NULL, start_anti_debugging_thread_function2, NULL );
}


#include <sys/ptrace.h>
#include <sys/wait.h>
void * start_anti_debugging_thread_function1 ( void * )
{
	LOGT();

	LOGI("Started anti debugging module1");
	pid_t ppid = getpid();
	pid_t pid = fork();
	if( pid == 0 ) {
		// child process
		int status;
		if (ptrace(PTRACE_ATTACH, ppid, NULL, NULL) == 0) {
			waitpid(ppid, &status, 0);

			ptrace(PTRACE_CONT, ppid, NULL, NULL);

			while (waitpid(ppid, &status, 0)) {
				if (WIFSTOPPED(status)) {
					ptrace(PTRACE_CONT, ppid, NULL, NULL);

				} else {
					break;
				}
			}
		}
		_exit(0);
	}

	return NULL;
}

pthread_t anti_debugging_thread_id1 = 0;
void start_anti_debugging1()
{
	LOGT();
	pthread_create( &anti_debugging_thread_id1, NULL, start_anti_debugging_thread_function1, NULL );
}

__attribute__((constructor))
jint JNI_OnPreLoad()
{
	LOGT();
	start_anti_debugging1();

	return 0;
}

// JNI_OnLoad
jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	//SMC_START_TAG( JNI_OnLoad );

	LOGT();

	JNIEnv * env = NULL;
	jint result = -1;

	if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK)
	{
		LOGE("ERROR: GetEnv failed");
		LOGE("Can not run the Application.");

		exit( -1 );
		return result;
	}


	pthread_create( &anti_speed_thread_id2, NULL, start_anti_speed_hack_thread_function2, NULL );
	pthread_create( &anti_speed_thread_id1, NULL, start_anti_speed_hack_thread_function1, NULL );

	start_anti_debugging2();
	//SMC_END_TAG( JNI_OnLoad );

	return JNI_VERSION_1_4;
}
#ifdef __cplusplus
	}
#endif
