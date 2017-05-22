#include <jni.h>
#include <android/log.h>

#include <stdio.h>
#include <pthread.h>
#include "util/util.h"

#include "smc_module.h"
#include "file_observer.h"

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include <sys/time.h>
#include <unistd.h>

#include "util/device_info.h"
#include "util/apk_hash_generator.h"

#include "util/log.h"


#ifndef SMC_MODULE_H_
#define SMC_START_TAG(tagID)
#define SMC_END_TAG(tagID)
#endif

JavaVM * g_pVM = NULL;
JNIEnv * g_env = NULL;
jobject g_appContents = NULL;

void start_anti_speed_hack();
void start_anti_memory_hack();
bool check_installed_xposed_tool();

void exit_timeout_15Sec_after_toast_show ( const char * message )
{
	alarm( 15 );	// 15초 이내 종료되지 않으면 alarm 으로 자동 종료 됨

	JNIEnv * env = g_env;
	/*
	if (g_pVM->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK)
	{
		LOGE("ERROR: GetEnv failed");
		LOGE( "Exit, %s", message );

		exit( -1 );
		return;
	}
	*/

	LOGE( "[EXIT Application] %s", message );
	// Toast 을 띄워주는 함수를 호출한다.

	jclass simpleMainActivity = env->FindClass( "com/example/androidprobetest/SimpleMainActivity" );
	if( simpleMainActivity == NULL ) {
		LOGE( "simpleMainActivity is NULL" );
		exit( -1 );
	}
	jmethodID showAlertPanel  = env->GetMethodID( simpleMainActivity, "showAlertPanel", "(Ljava/lang/String;)V" );
	if( showAlertPanel == NULL ) {
		LOGE( "showAlertPanel is NULL" );
		exit( -1 );
	}

	jstring jstrBuf = env->NewStringUTF( message );
	env->CallVoidMethod( g_appContents, showAlertPanel, jstrBuf );
	//env->ReleaseStringUTFChars( jstrBuf, message );
}

int g_money = 5000;
void Init	 ( JNIEnv * env, jobject obj, jobject appContents )
{
	LOGT();
	SMC_START_TAG( JNI_Init );

	g_appContents = appContents;
	g_env = env;

	device_info dev_info( env );
	LOGE( "DEVICE MODEL : '%s'", dev_info.get_model());
	LOGE( "DEVICE SERIAL : '%s'", dev_info.get_serial());
	LOGE( "DEVICE PLATFORM : '%s(%s)'", dev_info.get_platform(), dev_info.get_api_level());
	//LOGE( "DEVICE EXTERNAL STORAGE : '%s'", dev_info.get_internal_sd_path());

	const char * apk_fullpath = dev_info.get_apk_full_path(appContents);
	LOGE( "APK FULLPATH : %s", apk_fullpath );

	ApkHashGenerator apkHash;
	const char * apk_hash = apkHash.Generator( apk_fullpath );
	LOGE( "APK HASH : %s", apk_hash );

	std::string strGUID;
	GuidGenerator( env, strGUID );
	LOGE( "NDK GENERATED GUID : %s'", strGUID.c_str() );

	std::string strSeedGUID = "test uuid seed value";
	GuidGenerator( env, strGUID, strSeedGUID.c_str() );
	LOGE( "NDK GENERATED GUID : %s (%s)'", strGUID.c_str(), strSeedGUID.c_str() );

	start_anti_speed_hack();
	start_anti_memory_hack();
	if( check_installed_xposed_tool()) {
		exit_timeout_15Sec_after_toast_show( "Installed Xposed framework" );
	}

	SMC_END_TAG( JNI_Init );
}

void BuyItem ( JNIEnv * env, jobject obj )
{
	LOGT();
	g_money -= 500;
}

void SellItem( JNIEnv * env, jobject obj )
{
	LOGT();
	SMC_START_TAG( SellItem );
	g_money += 500;
	SMC_END_TAG( SellItem );
}

jint GetMoney( JNIEnv * env, jobject obj )
{
	LOGT();
	return g_money;
}

jlong GetTimeOfDay( JNIEnv * env, jobject obj )
{
	//SMC_START_TAG( GetTimeOfDay );
	struct timeval mytime;
	gettimeofday( &mytime, NULL );

	sleep( 1 );
	//SMC_END_TAG( GetTimeOfDay );

	return mytime.tv_sec;
}

jlong GetTimeAfterBoot( JNIEnv * env, jobject obj )
{
	//SMC_START_TAG( GetTimeAfterBoot );
	char strTemp[64] = { 0, };

	FILE * fp = fopen( "/proc/uptime", "rb" );
	if( fp != NULL ) {
		fread( strTemp, 64, 1, fp );
		fclose( fp );
	}

	//SMC_END_TAG( GetTimeAfterBoot );

	return atol( strTemp );
}

// JNI_OnLoad
jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	LOGT();

	g_pVM = vm;

	JNIEnv * env = NULL;
	jint result = -1;

	SMC_START_TAG( JNI_OnLoad );

	if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK)
	{
		LOGE("ERROR: GetEnv failed");
		return result;
	}

	const char * classPath = "com/example/androidprobetest/SimpleMainActivity";
	jclass clazz = env->FindClass( classPath );
	if (clazz == NULL)
	{
		LOGE( "Native registration unable to find class '%s'", classPath );
		return JNI_FALSE;
	}

	JNINativeMethod gMethods[6] = {
			{ "Init", 				"(Landroid/content/Context;)V", (void*)Init },
			{ "BuyItem", 			"()V", (void*)BuyItem },
			{ "SellItem", 			"()V", (void*)SellItem },
			{ "GetMoney", 			"()I", (void*)GetMoney },
			{ "GetTimeOfDay", 		"()J", (void*)GetTimeOfDay },
			{ "GetTimeAfterBoot", 	"()J", (void*)GetTimeAfterBoot },
	};

	if (env->RegisterNatives( clazz, gMethods, 6 ) < 0 )
	{
		LOGE( "RegisterNatives failed for '%s'", classPath );
		return -1;
	}

	SMC_END_TAG( JNI_OnLoad );

	return JNI_VERSION_1_4;
}

/**************************************************
* ANTI Speed hack								  *
***************************************************/
#include <linux/kernel.h>       /* for struct sysinfo */
#include <sys/sysinfo.h>
void * start_anti_speed_hack_thread_function ( void * )
{
	LOGT();

	SMC_START_TAG( start_anti_speed_hack_thread_function );

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

	while( t1 > 0 ) {
		sleep( 1 );

		sysinfo(&_s_info);
		gettimeofday( &_timeval, NULL );
		clock_gettime(CLOCK_MONOTONIC, &_timespec);

		d1 = _s_info.uptime 	- t1;
		d2 = _timeval.tv_sec  	- t2;
		d3 = _timespec.tv_sec 	- t3;

		if( abs( d2 - d1 ) > 2 ) {
			exit_timeout_15Sec_after_toast_show( "TIME VALUE CHANGED ( gettimeofday )" );
		}

		if( abs( d3 - d1 ) > 2 ) {
			exit_timeout_15Sec_after_toast_show( "TIME VALUE CHANGED ( clock_gettime )" );
		}

		LOGD("Base time : %d, gTime : %d, cTime : %d", d1, d2, d3 );
	}

	SMC_END_TAG( start_anti_speed_hack_thread_function );

	return NULL;
}

void start_anti_speed_hack()
{
	LOGT();

	SMC_START_TAG( start_anti_speed_hack );

	pthread_t threadID = 0;
	pthread_create( &threadID, NULL, start_anti_speed_hack_thread_function, NULL );

	SMC_END_TAG( start_anti_speed_hack );
}

/**************************************************/

/**************************************************
* ANTI Memory hack								  *
***************************************************/
#define DELAY_TIME		usleep(10);

void INOTIFY_HANDLER ( struct inotify_event  *event )
{
	LOGE("INOTIFY_HANDLER name=%s wd=%d mask=%d cookie=%d len=%d dir=%s\n", event->name, event->wd, event->mask, event->cookie, event->len, (event->mask & IN_ISDIR)?"yes":"no");
	//exit( -1 );
	//alarm(15);
	exit_timeout_15Sec_after_toast_show( "Detection internal memory search action" );
}

void start_anti_memory_hack()
{
	LOGT();

	SMC_START_TAG( start_anti_memory_hack );

	const int PID = getpid();
	LOGD( "PID[%d] : TID[%d] in thread", PID, gettid() );

	std::vector<int> PIDs;
	if( 0 > read_thread_ids( PID, PIDs ))
	{
		LOGE( "%d PID Task read error", PID );
		return;
	}

	char strTemp[256], strTemp2[256];
	const char * MEM_FILE_FORMAT 		= "/proc/%d/mem";
	const char * MAPS_FILE_FORMAT 		= "/proc/%d/maps";
	const char * SUB_MEM_FILE_FORMAT 	= "/proc/%d/task/%d/mem";
	const char * SUB_MAPS_FILE_FORMAT 	= "/proc/%d/task/%d/maps";

	static file_observer f_observer;
	if( f_observer.init() < 0 ) return;

	for( int i = 0; i < (int) PIDs.size(); i++ )
	{
		if( PID == PIDs[i] )
		{
			sprintf( strTemp,  MEM_FILE_FORMAT,  PIDs[i] );
			sprintf( strTemp2, MAPS_FILE_FORMAT, PIDs[i] );
		}
		else
		{
			sprintf( strTemp,  SUB_MEM_FILE_FORMAT,  PID, PIDs[i] );
			sprintf( strTemp2, SUB_MAPS_FILE_FORMAT, PID, PIDs[i] );
		}

		f_observer.add( strTemp );
		f_observer.add( strTemp2 );
	}

	f_observer.start( INOTIFY_HANDLER );

	SMC_END_TAG( start_anti_memory_hack );
}


/**************************************************
* ANTI Xposed framework
***************************************************/
#include <dlfcn.h>
#include <errno.h>
#include <stdlib.h>
#include <stdarg.h>

// app_process 파일의 수정 시간과 ps 파일의 수정 시간의 차이를 반환한다.
unsigned long get_app_process_n_ps_time_diff()
{
	SMC_START_TAG( get_app_process_n_ps_time_diff );

	struct stat stat1, stat2;
	stat( "/system/bin/app_process", &stat1 );
	stat( "/system/bin/ps", &stat2 );

	SMC_END_TAG( get_app_process_n_ps_time_diff );

	return stat1.st_mtime - stat2.st_mtime;
}

// app_process 파일의 rel symbol name 중 해당되는 name 이 있는지 확인 합니다.
// 입력된 names 중 하나라도 발견되면 true 을 반환한다.
#include "util/linker.h"
bool search_symbol_name_in_app_process( const std::vector<std::string> & search_datas )
{
	LOGT();

	SMC_START_TAG( search_symbol_name_in_app_process );

	soinfo *si = (struct soinfo *) dlopen( "/system/bin/app_process", 0 /* RTLD_NOLOAD */ );
	if( si == NULL) {
		LOGE( "'/system/bin/app_process' dlopen error: %s.", dlerror() );
		return false;
	}

	const char *strtab = si->strtab;
	Elf32_Sym *symtab = si->symtab;

	Elf32_Rel * rel = si->plt_rel;
	for( size_t i = 0; i < si->plt_rel_count; ++i, ++rel ) {
		unsigned reloc = (unsigned)(rel->r_offset + si->base);

		unsigned sym = ELF32_R_SYM(rel->r_info);
		Elf32_Sym *s = symtab + sym;
		const char * p_str = strtab + s->st_name;

		LOGD( "symbol name : %s", p_str );
#if 1
		for( std::vector<std::string>::const_iterator it = search_datas.begin(); it != search_datas.end(); it++ ) {
			if( strstr( p_str, it->c_str()) != 0 ) {
				dlclose( si );
				return true;
			}
		}
#endif
	}

	dlclose( si );

	SMC_END_TAG( search_symbol_name_in_app_process );

	return false;
}

// app_process 파일안에 입력된 문자열들이 존재 하는지를 확인 한다.
// 입력된 문자열 중 하나라도 존재 하면 true 를 반환한다.
bool search_string_in_app_process( const std::vector<std::string> & search_datas )
{
	LOGT();

	SMC_START_TAG( search_string_in_app_process );

	FILE * fp = fopen( "/system/bin/app_process", "rb" );
	if( fp == NULL ) {
		LOGE( "could not fopen '/system/bin/app_process' file" );
		return false;
	}

	/*
	 * app_process 의 문자열 Table을 읽어서 문자열 비교를 해야 하는데, EFL 의 String parser가 없어
	 * binary 비교로 동작하게 한다. 추후 수정/교체 여지가 있음
	 * */
	struct stat stat1;
	stat( "/system/bin/app_process", &stat1 );

	char * file_buff = new char[(int) stat1.st_size ];
	fread( file_buff, 1, stat1.st_size, fp );
	fclose( fp );

	int MAX_FIND_STRINGS = ( int ) search_datas.size();

	for( int i = 0; i < stat1.st_size; i++ ) {
		for( int idx = 0; idx < MAX_FIND_STRINGS; idx++ ) {
			const char * compare_string = search_datas[idx].c_str();
			if( file_buff[i] == compare_string[0] ) {
				if( strcmp( (char *)&file_buff[i], compare_string ) == 0 ) {
					delete [] file_buff;
					return true;
				}
			}
		}
	}

	delete [] file_buff;

	SMC_END_TAG( search_string_in_app_process );

	return false;
}

// xposed framework tool 이 설정 되어 있는지를 확인 합니다.
// xposed framework 의 설치가 의심되면 true 을 반환합니다.
bool check_installed_xposed_tool()
{
	LOGT();

	//SMC_START_TAG( check_installed_xposed_tool );

	// app_process 파일의 수정 날짜를 다른 모듈과 비교한다.
	/*
	 * 해당 값은 사용하지 않는다.
	 * 이유 : 단말기의 SW Update에 의해 app_process 만 변경 될 수 있다. ( 이 경우 오동작원 원인되어 해당 로직을 사용하지 않는다. )
	 * */
	bool is_pass_check_last_modify = get_app_process_n_ps_time_diff() > 100;
	LOGE("[TEST] last modify time : %s", is_pass_check_last_modify ? "not pass" : "pass");

	// symbol table 에 등록된 symbol string 을 검사한다.
	/*
	 * app_process 에서 dlsym 함수를 사용하지 않는다는 보장이 필요하다.
	 * */
	std::vector<std::string> search_symbol_datas;
	//search_symbol_datas.push_back( "dlsym" );			// xposed 에서 사용, 원본 app_process 에서 다른 so 파일의 심볼을 얻는 dlsym 함수는 필요하지 않음
	search_symbol_datas.push_back( "dvmCallMethod" );	// xposed 에서 사용, 원본 app_process 에서 libdvm.so에 있는 함수를 사용하지 않음
	search_symbol_datas.push_back( "dvmInvokeMethod" );
	//search_symbol_datas.push_back( "setenv" );		// xposed 에서 사용, 원본 app_process 에서 환경변수의 값을 변경하지 않음

	// dvm 으로 시작되는 함수들 모두 추가
	search_symbol_datas.push_back( "dvmInitClass" );
	search_symbol_datas.push_back( "dvmSetFinalizable" );
	search_symbol_datas.push_back( "dvmAllocArrayByClass" );
	search_symbol_datas.push_back( "dvmReleaseTrackedAlloc" );
	search_symbol_datas.push_back( "dvmDumpObject" );
	search_symbol_datas.push_back( "dvmGetMethodFromReflectObj" );
	search_symbol_datas.push_back( "dvmInvokeMethod" );
	search_symbol_datas.push_back( "dvmFindPrimitiveClass" );
	search_symbol_datas.push_back( "dvmBoxPrimitive" );
	search_symbol_datas.push_back( "dvmMarkCard" );
	search_symbol_datas.push_back( "dvmSetNativeFunc" );
	search_symbol_datas.push_back( "dvmFindArrayClass" );

	// android : xml parser 함수도 추가
	search_symbol_datas.push_back( "ResXMLParser" );
	search_symbol_datas.push_back( "ResStringPool" );

	bool is_pass_check_dlsym_name = search_symbol_name_in_app_process( search_symbol_datas );
	LOGE("[TEST] symbol string : %s", is_pass_check_dlsym_name ? "not pass" : "pass");

	// app_process binary 의 문자열을 검색 합니다.
	/*
	 * */
	std::vector<std::string> search_string_datas;
	search_string_datas.push_back( "hookMethodNative" );		// xposed 에서 사용
	search_string_datas.push_back( "handleHookedMethod" );		// xposed 에서 사용
	search_string_datas.push_back( "Xposed" );					// xposed 에서 사용
	search_string_datas.push_back( "()Ljava/lang/String;" );	// xposed 에서 사용, 원본 app_process 에서 Java class 관련 문구는 사용하지 않음
	search_string_datas.push_back( "libdvm.so" );				// xposed 에서 사용, 원본 app_process 에서 libdvm.so 파일에 대한 언급이 필요하지 않음

	bool is_pass_check_string_search = search_string_in_app_process( search_string_datas );
	LOGE("[TEST] binary string : %s", is_pass_check_string_search ? "not pass" : "pass" );

	//SMC_END_TAG( check_installed_xposed_tool );

	return is_pass_check_string_search || is_pass_check_dlsym_name;
}
/**************************************************/
