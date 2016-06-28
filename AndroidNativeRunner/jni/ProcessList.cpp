/*
 * ProcessList.cpp
 *
 *  Created on: 2016. 3. 11.
 *      Author: purehero2
 */

#include <ProcessList.h>

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <sys/ptrace.h>
#include <sys/prctl.h>
#include <sys/wait.h>
#include <sys/stat.h>
#include <errno.h>
#include <dirent.h>
#include <pwd.h>

#ifndef MAX_PATH
#define MAX_PATH 260
#endif

#include <sys/types.h>
#include <grp.h>

int read_proc_username( int pid, PS_DATA & data )
{
	char path[MAX_PATH];
	sprintf( path, "proc/%d/cmdline", pid );

	// cmdline 파일의 소유자명을 획득한다.
	struct stat stats;
	stat( path, &stats);

	struct passwd *pw = getpwuid(stats.st_uid);
	if( pw == 0 ) {
		snprintf( data.user, sizeof(data.user),"%d",(int)stats.st_uid);
	} else {
		strcpy( data.user, pw->pw_name );
	}
	return 0;
}

int read_proc_cmdline( int pid, PS_DATA & data )
{
	char path[MAX_PATH];
	char buffer[ 1024 ];

	sprintf( path, "proc/%d/cmdline", pid );

	FILE* proc_fs_p = fopen( path, "r" );
	if ( proc_fs_p == NULL ) return -1;
	if( fgets( buffer, 1023, proc_fs_p ))
	{
		fclose( proc_fs_p );
		proc_fs_p = NULL;

		strcpy( data.name, buffer );
	}
	if( proc_fs_p != NULL ) fclose( proc_fs_p );
	return 0;
}

int read_proc_stat( int pid, PS_DATA & data )
{
	char path[MAX_PATH];
	char buffer[ 1024 ];

	sprintf( path, "proc/%d/stat", pid );

	FILE* proc_fs_p = fopen( path, "r" );
	if ( proc_fs_p == NULL ) return -1;
	if( fgets( buffer, 1023, proc_fs_p ))
	{
		fclose( proc_fs_p );
		proc_fs_p = NULL;

		char * tok_pid 		= strtok( buffer, " " );	// pid
		char * tok_name 	= strtok( NULL, " " );		// name
		char * tok_status 	= strtok( NULL, " " );		// Status
		char * tok_ppid 	= strtok( NULL, " " );		// ppid

		data.pid 	= atoi( tok_pid );
		data.ppid 	= atoi( tok_ppid );
		data.status = tok_status[0];

		int name_len = strlen( data.name );
		int tok_name_len = strlen( tok_name );
		if( tok_name_len > name_len ) { strcpy( data.name, tok_name ); name_len = tok_name_len; }
		if( data.name[0] == '(') { strcpy( data.name, &data.name[1] ); name_len--; }
		if( data.name[ name_len - 1 ] == ')' ) data.name[ name_len - 1 ] = '\0';
	}
	if( proc_fs_p != NULL ) fclose( proc_fs_p );
	return 0;
}

int read_proc_status( int pid, PS_DATA & data )
{
	char path[MAX_PATH];
	char buffer[ 1024 ];

	sprintf( path, "proc/%d/status", pid );

	FILE* proc_fs_p = fopen( path, "r" );
	if ( proc_fs_p == NULL ) return -1;
	if( fread( buffer, 1, 1023, proc_fs_p ) > 0 )
	{
		fclose( proc_fs_p );
		proc_fs_p = NULL;

		char * tpid = strstr( buffer, "TracerPid" );
		if( tpid != NULL )
		{
			sscanf( tpid, "TracerPid:\t%d", &data.tracer_pid);
		}
	}
	if( proc_fs_p != NULL ) fclose( proc_fs_p );
	return 0;
}


int  get_process_list( int pid, PS_DATA & data )
{
	memset( &data, 0, sizeof(data));

	read_proc_username( pid, data );
	read_proc_cmdline( pid, data );
	read_proc_stat( pid, data );
	read_proc_status( pid, data );

	return 0;
}

int get_process_list( std::vector<PS_DATA> & result )
{
	result.clear();

	//Proc 밑에있는 모든 PID의 cmdline을 읽어 버퍼에 저장.
	DIR *dir = opendir( "/proc/" );
	if( dir != NULL )
	{
		struct dirent *file;
		while(( file = readdir( dir )) != NULL )
		{
			if ( strncmp( file->d_name, ".", 1 ) == 0 || strncmp( file->d_name, "..", 2 ) == 0 || file->d_name[0] < '1' || file->d_name[0] > '9' )
				continue;

			PS_DATA data;
			if( 0 == get_process_list( atoi( file->d_name ), data ))
			{
				result.push_back(data);
			}
		}

		closedir(dir);
	}

	return (int) result.size();
}
