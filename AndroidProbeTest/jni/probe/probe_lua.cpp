/*
 * lua.cpp
 *
 *  Created on: 2015. 7. 7.
 *      Author: purehero2
 */

#include "probe_lua.h"
#include "util/log.h"

#include <jni.h>

#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <sys/wait.h>
#include <errno.h>
#include <time.h>

#include "util/simple_http.h"
#include "util/status_reader.h"
#include <string>
#include <map>

probe_lua::probe_lua() : L(NULL)
{
}

probe_lua::~probe_lua()
{
}

void probe_lua::init()
{
	LOGT();

	L = lua_open();   /* opens Lua */

	luaopen_base(L);             /* opens the basic library */
	luaopen_table(L);            /* opens the table library */
	//luaopen_io(L);               /* opens the I/O library */
	luaopen_string(L);           /* opens the string lib. */
	luaopen_math(L);             /* opens the math lib. */

	regist_custom_api();
}

void probe_lua::release()
{
	LOGT();

	if( L != NULL ) {
		lua_close(L);
	}
	L = NULL;
}

void probe_lua::excute( const char * script, int len )
{
	LOGT();
	if( L == NULL ) return;

	luaL_loadbuffer(L, script, len, "line");
	int error = lua_pcall(L, 0, 0, 0);
	if (error)
	//if ( luaL_dostring(L,script.c_str()) )
	{
		fprintf(stderr, "%s", lua_tostring(L, -1));
		lua_pop(L, 1);  /* pop error message from the stack */
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////
int LUA_LOGD( lua_State * L )
{
	const char * message = luaL_checkstring(L, 1);	// 1 첫번째 입력 파라메터
	LOGD( "[lua] => %s", message );
	return 0;	// 반환값의 개수
}

int LUA_LOGE( lua_State * L )
{
	const char * message = luaL_checkstring(L, 1);	// 1 첫번째 입력 파라메터
	LOGE( "[lua] => %s", message );
	return 0;	// 반환값의 개수
}

int LUA_GET_CLOCK_GETTIME( lua_State * L )
{
	struct timespec time1, time2;

	clock_gettime( CLOCK_MONOTONIC, &time1 );
	usleep( 100000 );
	clock_gettime( CLOCK_MONOTONIC, &time2 );

	long gap = ( time2.tv_sec * 1000 + time2.tv_nsec / 1000 / 1000 ) - ( time1.tv_sec * 1000 + time1.tv_nsec / 1000 / 1000 );
	lua_pushnumber(L, gap); // Push the result
	return 1;
}

int LUA_GET_CURRENT_MSEC( lua_State * L )
{
	struct timespec time;

	clock_gettime( CLOCK_MONOTONIC, &time );

	long val = time.tv_sec * 1000 + time.tv_nsec / 1000 / 1000;
	lua_pushnumber(L, val); // Push the result
	return 1;
}

int LUA_EXIT( lua_State * L )
{
	int value = luaL_checknumber(L, 1);	// 1 첫번째 입력 파라메터
	exit( value );
	return 0;
}

int LUA_KILL( lua_State * L )
{
	int pid = luaL_checknumber(L, 1);	// 1 첫번째 입력 파라메터
	kill( pid, SIGKILL );
	return 0;
}

int LUA_USLEEP( lua_State * L )
{
	int usec = luaL_checknumber(L, 1);	// 1 첫번째 입력 파라메터
	usleep( usec );
	return 0;
}

int LUA_FORK( lua_State * L )
{
	int pid = fork();
	lua_pushnumber(L, pid); // Push the result
	return 1;
}

typedef struct _lua_thread_param_
{
	lua_State * L;
	char fname[128];
} LUA_THREAD_PARAM;

void * lua_thread_function( void * param )
{
	LUA_THREAD_PARAM * p = ( LUA_THREAD_PARAM * ) param;

	LOGD( "lua_thread_function : %s", p->fname );

	lua_getglobal( p->L, p->fname );
	lua_pcall(p->L, 0, 0, 0);

	delete p;
	return NULL;
}

int LUA_CREATE_THREAD( lua_State * L )
{
	const char * fname = luaL_checkstring(L, 1);	// 1 첫번째 입력 파라메터( function name )

	LUA_THREAD_PARAM * p = new LUA_THREAD_PARAM();
	p->L = L;
	strcpy( p->fname, fname );

	pthread_t threadID = 0;
	pthread_create( &threadID, NULL, lua_thread_function, p );

	return 0;
}

int LUA_ISVALID_PID( lua_State * L )
{
	pid_t pid = ( pid_t ) luaL_checknumber(L, 1);
	lua_pushnumber(L, getpgid( pid )); // Push the result

	return 1;
}

int LUA_GET_PID( lua_State * L )
{
	lua_pushnumber(L, getpid()); // Push the result
	return 1;
}

int LUA_READ_PROCESS_STATE( lua_State * L )
{
	int pid = luaL_checknumber(L, 1);

	char state = 'e';	// default (e)rror
	char path[64], name[128], buff[1024];
	sprintf( path, "/proc/%d/stat", pid );

	FILE * fp = fopen( path, "r" );
	if( fp != NULL )
	{
		if ( fgets( buff, sizeof( buff ), fp ))
		{
			sscanf( buff, "%d %s %c", ( int* )&pid, name, &state );
		}
		fclose( fp );
	}

	lua_pushnumber(L, toupper( state )); // Push the result
	return 1;
}

int LUA_HTTP_REQUEST( lua_State * L )
{
	const char * url = luaL_checkstring(L, 1);	// 1 첫번째 입력 파라메터

	SimpleHttpResponse response;

	simple_http http;
	CURLcode code = http.init();
	if( code == CURLE_OK )
	{
		code = http.send_request( url, response, REQUEST_GET );
		if( code != CURLE_OK )
		{
			LOGE("CURL REQUEST FAILED!!");
			http.get_error_msg( response.res_body, code );
			lua_pushstring(L, response.res_body.c_str()); // Push the result
			return 1;
		}
	}
	else
	{
		LOGE("CURL INIT FAILED!!");
		http.get_error_msg( response.res_body, code );

		lua_pushstring(L, response.res_body.c_str()); // Push the result
		return 1;
	}

	lua_pushstring(L, response.res_body.c_str()); // Push the result
	return 1;
}


int LUA_GET_SATAUE_VALUE( lua_State * L )
{
	int pid = luaL_checknumber(L, 1);
	const char * key = luaL_checkstring(L, 2);	// 2 첫번째 입력 파라메터

	char strError[128];

	status_reader reader( pid );
	if( reader.read() < 0 )
	{
		sprintf( strError, "Failed to read '/proc/%d/status'", pid );
		LOGE( strError );

		lua_pushstring(L, strError); // Push the result
		return 1;
	}

	const char * value = reader.search( key );
	if( value == NULL )
	{
		sprintf( strError, "Failed to searching field name [%s]", key );
		LOGE( strError );

		lua_pushstring(L, strError);
		return 1;
	}

	lua_pushstring(L, value);
	return 1;
}

int LUA_GET_TIME_OF_DAY_SEC( lua_State * L )
{
	struct timeval mytime;
	gettimeofday( &mytime, NULL );
	lua_pushnumber(L, mytime.tv_sec);

	return 1;
}

int LUA_GET_UPTIME_SEC( lua_State * L )
{
	char strTemp[64] = { 0, };
	FILE * fp = fopen( "/proc/uptime", "rb" );
	if( fp != NULL ) {
		fread( strTemp, 64, 1, fp );
		fclose( fp );
	}

	lua_pushnumber(L, atol( strTemp ));
	return 1;
}

int LUA_GET_CLOCKGETTIME_SEC( lua_State * L )
{
	struct timespec start;
	clock_gettime(CLOCK_MONOTONIC, &start);

	lua_pushnumber(L, start.tv_sec );
	return 1;
}


const luaL_Reg inkalib[] = {
  {"LOGD", LUA_LOGD},
  {"LOGE", LUA_LOGE},
  {"getTimeGap", LUA_GET_CLOCK_GETTIME},		// 100ms 을 sleep 시킨 후 그 차이값을 반환한다. usleep의 기능 확인
  {"exit", LUA_EXIT},
  {"kill", LUA_KILL},
  {"usleep", LUA_USLEEP},						//
  {"fork", LUA_FORK},							// fork 시킨다.
  {"create_thread", LUA_CREATE_THREAD},			// thread 을 생성 시킨다.
  {"getpid", LUA_GET_PID},						// process id 값을 반환 합니다.
  {"checkpid", LUA_ISVALID_PID},				// process id 값의 유효성을 체크 한다. ( -1: 유효하지 않음 )

  {"pstate", LUA_READ_PROCESS_STATE},			// 입력된 pid의 상태값을 정수형태로 반환한다.
  {"msec_time", LUA_GET_CURRENT_MSEC},			// 현재 시간을 ms 단위로 반환한다.
  {"http", LUA_HTTP_REQUEST},					// http 요청을 보내고 결과값을 반환한다.
  {"pstateValue", LUA_GET_SATAUE_VALUE},		// 입력된 pid의 status 에서 key 값을 찾아 반환한다. : value = pstateValue( pid, key );

  {"getTimeOfDaySec", LUA_GET_TIME_OF_DAY_SEC},	// getTimeOfDay의 sec 값을 반환 합니다.
  {"getUptimeSec", LUA_GET_UPTIME_SEC},			// /proc/uptime 의 sec 값을 반환 합니다.
  {"getClockGetTimeSec", LUA_GET_CLOCKGETTIME_SEC},			// clock_gettime 의 sec 값을 반환 합니다.

  {NULL, NULL}
};


#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <dirent.h>

#include "util/maps_reader.h"
#include <set>
#include <map>

std::map<std::string,std::string> m_game_infos;
int LUA_READ_PACKAGE_NAMES( lua_State * L )
{
	LOGT();

	const int READ_BUFF_SIZE = 10240;
	const char * path = luaL_checkstring(L, 1);

	char strTemp[1024];
	m_game_infos.clear();

	int fd = ::open( path, O_RDONLY );
	if( fd == -1 )
	{
		perror( "open" );

		lua_pushnumber(L, 0);
		return 1;
	}

	char tmpBuff[READ_BUFF_SIZE];
	std::vector<char> file_content;

	// maps 파일의 내용을 모두 읽어 옵니다.
	int nRead = ::read( fd, tmpBuff, READ_BUFF_SIZE );
	while( nRead > 0 )
	{
		LOGI( "%d byte readed", nRead );

		file_content.insert( file_content.end(), &tmpBuff[0], &tmpBuff[nRead] );
		nRead = ::read( fd, tmpBuff, READ_BUFF_SIZE );
	}
	::close( fd );

	if( file_content.size() < 10 )
	{
		LOGE( "FILE Size is %d byte too small", file_content.size());

		lua_pushnumber(L, 0);
		return 1;
	}

	std::vector<std::string> game_infos;
	char * token = strtok(( char * ) &file_content[0], "," );
	while( token != NULL )
	{
		LOGI( "ADDED PackageName : %s", token );

		game_infos.push_back( token );
		token = strtok( NULL, "," );
	}

	for( int i = 0; i < game_infos.size(); i++ )
	{
		strcpy( strTemp, game_infos[i].c_str() );

		char * pName = strtok( strTemp, ":");
		char * gName = strtok( NULL, ":");

		if( pName != NULL && gName != NULL )
		{
			m_game_infos.insert( std::pair<std::string,std::string>( pName, gName));
		}

		LOGI( "ADDED PackageName : %s", strTemp );
	}

	lua_pushnumber(L, (int)m_game_infos.size());
	return 1;
}


std::set<std::string> loaded_maps_content;
int LUA_RUN_TEST( lua_State * L )
{
	LOGT();

	char strTemp[128];
	char tmpBuff[128];

	std::string ret;

	simple_http http;
	CURLcode code = http.init();
	if( code != CURLE_OK )
	{
		LOGE("CURL INIT FAILED!!");
		http.get_error_msg( ret, code );

		lua_pushstring(L, ret.c_str()); // Push the result
		return 1;
	}

	std::map<int,std::string> targetPIDs;

	struct dirent *file;
	DIR *dir = opendir( "/proc/" );
	while(( file = readdir( dir )) != NULL )
	{
		if ( strncmp( file->d_name, ".", 1 ) == 0 || strncmp( file->d_name, "..", 2 ) == 0 || file->d_name[0] < '1' || file->d_name[0] > '9' )
		{
			continue;
		}

		sprintf( strTemp, "/proc/%s/cmdline", file->d_name );

		int fd = ::open( strTemp, O_RDONLY );
		if( fd != -1 )
		{
			int nRead = ::read ( fd, tmpBuff, 128 );
			::close( fd );

			if( nRead < 1 ) continue;
		}

		for( std::map<std::string,std::string>::iterator it = m_game_infos.begin(); it != m_game_infos.end(); it++ )
		{
			if( strstr( tmpBuff, it->first.c_str() ) != NULL )
			{
				LOGI( "target pid : %s, name : %s, pname : %s", file->d_name, tmpBuff, it->first.c_str() );

				targetPIDs.insert( std::pair<int,std::string>( atoi( file->d_name ), it->first ));
				break;
			}
		}
	}
	closedir( dir );

	// 작업을 진행해야할 PID 수집 완료
	for( std::map<int,std::string>::iterator it = targetPIDs.begin(); it != targetPIDs.end(); it++ )
	{
		maps_reader reader( it->first );
		int nRead = reader.read();
		LOGD( "maps_reader count : %d %d", nRead, it->first );

		if( nRead > 0 )
		{
			const std::vector<maps_info> infos = reader.get_maps_infos();
			for( std::vector<maps_info>::const_iterator infos_it = infos.begin(); infos_it != infos.end(); infos_it++ )
			{
				if( strncmp( infos_it->filename, "[", 1 ) == 0 ) continue;
				if( strncmp( infos_it->filename, "\r[", 2 ) == 0 ) continue;
				if( strncmp( infos_it->filename, "\n[", 2 ) == 0 ) continue;

				int old_size = (int)loaded_maps_content.size();
				loaded_maps_content.insert( infos_it->filename );

				if( old_size < (int)loaded_maps_content.size())
				{
					LOGD( "FOUND NEW NAME : %s", infos_it->filename );
					sprintf( strTemp, "http://192.168.100.99/package_name.php?pname=%s&name=%s", it->second.c_str(), infos_it->filename );

					SimpleHttpResponse response;
					code = http.send_request( strTemp, response, REQUEST_GET );
					LOGD( "response ===> %d %s", (int)code, response.res_body.c_str());
				}
			}
		}
	}

	LOGD( "CURRENT NAME COUNT : %d", (int)loaded_maps_content.size() );


	return 0;
}

const luaL_Reg maps_watcher_lib[] = {
	{"read_package_names", LUA_READ_PACKAGE_NAMES},
	{"run", LUA_RUN_TEST},

	{NULL, NULL}
};
////////////////////////////////////////////////////////////////////////////////////////////////////////

void probe_lua::regist_custom_api()
{
	LOGT();
	luaL_register(L,"inka",inkalib);
	luaL_register(L,"test",maps_watcher_lib);
}
