#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>
#include "jstr2str.h"

#define  LOG_TAG	"TEST01"
#define  LOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGT()		__android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,"%s:%s",__FILE__,__func__ )

void init( JNIEnv * env, jobject, jobject appContext );

__attribute__((constructor))
void JNI_OnPreLoad()
{
	LOGT();
}



// JNI_OnLoad
jint JNI_OnLoad( JavaVM* vm, void* )
{
	LOGT();

	JNIEnv * env = NULL;
	jint result = -1;

	if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK)
	{
		LOGE("ERROR: GetEnv failed");
		return result;
	}

	const char * classPath = "com/example/myandroidsampleapp00/NativeLibrary2";
	jclass clazz = env->FindClass( classPath );
	if (clazz == NULL)
	{
		LOGE( "Native registration unable to find class '%s'", classPath );
		return JNI_FALSE;
	}

	JNINativeMethod gMethods[1] = {
			{ "init", 			"(Landroid/content/Context;)V", 		(void*) init }
	};

	if (env->RegisterNatives( clazz, gMethods, 1 ) < 0 )
	{
		LOGE( "RegisterNatives failed for '%s'", classPath );
		return -1;
	}

	return JNI_VERSION_1_4;
}


#include <string>
#include <vector>
#include <memory>

/*
 * 문자열이 ELF 구조에서 노출되지 않게 하기위한 함수
 *
 * */
const char * compose_string( std::initializer_list<char> l, std::string & tmp_str )
{
	tmp_str.assign( l.begin(), l.end() );
	return tmp_str.c_str();
}

/*
 * Android 의 Build 정보를 획득하기 위한 함수
 *
 * */
int getDeviceField( JNIEnv* env, const char* name, std::string & ret_value )
{
	std::string tmp_str;

	jclass cls_context = env->FindClass(compose_string({ 'a', 'n', 'd', 'r', 'o', 'i', 'd', '/', 'o', 's', '/', 'B', 'u', 'i', 'l', 'd' }, tmp_str ));
	if ( cls_context == 0  ) {
		LOGE( "ERROR: env->FindClass failed" );
		return -1;
	}

	jfieldID field = env->GetStaticFieldID( cls_context, name, compose_string({ 'L','j','a','v','a','/','l','a','n','g','/','S','t','r','i','n','g',';' }, tmp_str ) );
	if ( field == 0 ) {
		LOGE( "ERROR: env->GetStaticFieldID" );
		return -1;
	}

	jstring str_value = ( jstring )env->GetStaticObjectField( cls_context, field );
	if ( str_value == 0 ) {
		LOGE( "ERROR: env->GetStaticObjectField" );
		return -1;
	}

	jstr2str jstring( env, str_value );
	ret_value.assign( jstring.c_str());

	LOGD( "getDeviceField : %s => %s", name, ret_value.c_str());

	return 0;
}




/*
 * 파일의 존재 유무를 확인하기 위한 함수
 *
 * return : true ( 유 ), false ( 무 )
 * */
#include <unistd.h>
bool file_exist( const char * filepath )
{
	return access( filepath, F_OK ) == 0;
}

/*
 * 실행중인 단말기가 VM 환경인지를 확인 하기 위한 함수
 *
 * return : true ( VM ), false ( No VM )
 * */
bool check_vm( JNIEnv * env )
{
	LOGT();
	std::string tmp_str;	// compose_string 을 사용하기 위한 임시 객체

	// 해당 파일이 존재하면 VM 으로 판단
	if( file_exist( compose_string({'/','s','y','s','/','d','e','v','i','c','e','s','/','p','l','a','t','f','o','r','m','/','h','d','_','p','o','w','e','r'}, tmp_str ) )) {
		return true;
	}
	if( file_exist( compose_string({'/','s','y','s','/','b','u','s','/','a','c','9','7'}, tmp_str ) )) {
		return true;
	}

	// 해당 파일이 존재하지 않으면 VM으로 판단.

	// 해당 파일이 특정 문자열이 존재하면 VM으로 판다.
	std::unique_ptr<char> line ( new char[2048] );

	bool bFound = false;
	FILE * fp = NULL;

	fp = fopen( compose_string({'/', 'p', 'r', 'o', 'c', '/', 'p', 'a', 'r', 't', 'i', 't', 'i', 'o', 'n', 's'}, tmp_str ), "r");
	if( fp != NULL ) {

		std::string strSDA = compose_string({'s', 'd', 'a'}, tmp_str );	// 찾을 문자열
		while( fgets( line.get(), 2047, fp ) != NULL ) {
			const char * pLine = line.get();

			if( strstr( pLine, strSDA.c_str() ) != NULL ) {
				bFound = true;
				break;
			}
		}
		fclose( fp );
	}
	if( bFound ) return true;

	bFound = false;
	fp = fopen( compose_string({'/','p','r','o','c','/','m','o','d','u','l','e','s'}, tmp_str), "r");
	if( fp != NULL ) {
		std::vector<std::string> check_list;	// 찾을 문자열 들
		check_list.push_back(compose_string({'b','s','t','m','o','u','s','e'}, tmp_str));
		check_list.push_back(compose_string({'b','s','t','t','o','u','c','h'}, tmp_str));
		check_list.push_back(compose_string({'b','s','t','k','b','d'}, tmp_str));
		check_list.push_back(compose_string({'b','s','t','g','p','s'}, tmp_str));
		check_list.push_back(compose_string({'b','s','t','c','m','d'}, tmp_str));
		check_list.push_back(compose_string({'b','s','t','c','a','m','e','r','a'}, tmp_str));
		check_list.push_back(compose_string({'b','s','t','v','i','d','e','o'}, tmp_str));
		check_list.push_back(compose_string({'b','s','t','a','u','d','i','o'}, tmp_str));

		while( fgets( line.get(), 2047, fp ) != NULL ) {
			const char * pLine = line.get();

			for( auto it = check_list.begin(); it != check_list.end(); ++it ) {
				if( strstr( pLine, it->c_str()) != NULL ) {
					bFound = true;
					break;
				}
			}
		}
		fclose( fp );
	}
	if( bFound ) return true;

	// Device 의 Build 정보 획득
	std::string cpu, device, hardware, product;
	getDeviceField( env, compose_string({ 'C', 'P', 'U', '_', 'A', 'B', 'I' }, tmp_str), cpu );
	getDeviceField( env, compose_string({ 'D', 'E', 'V', 'I', 'C', 'E' }, tmp_str), device );
	getDeviceField( env, compose_string({ 'H', 'A', 'R', 'D', 'W', 'A', 'R', 'E' }, tmp_str), hardware );
	getDeviceField( env, compose_string({ 'P', 'R', 'O', 'D', 'U', 'C', 'T' }, tmp_str), product );

	// check ( cpu && hardware && device && product )
	std::vector<std::vector<std::string>> ck_datas;
	ck_datas.push_back( { compose_string({ 'x' }, tmp_str), 		compose_string({ 'g','o','l','d','f','i','s','h' }, tmp_str), 	compose_string({ 'g','e','n','e','r','i','c' }, tmp_str), 					compose_string({ 's','d','k' }, tmp_str)} ) ;
	ck_datas.push_back( { compose_string({ 'x','8','6' }, tmp_str), compose_string({ 'g','o','l','d','f','i','s','h' }, tmp_str), 	compose_string({ 'g','e','n','e','r','i','c','_','x','8','6' }, tmp_str), 	compose_string({ 's','d','k','_','x','8','6' }, tmp_str)} ) ;
	ck_datas.push_back( { compose_string({ 'x','8','6' }, tmp_str), compose_string({ 'a','n','d','y' }, tmp_str), 					compose_string({ 's','a','n','t','o','s' }, tmp_str), 						compose_string({ 's','a','n','t','o','s' }, tmp_str)} ) ;
	ck_datas.push_back( { compose_string({ 'x','8','6' }, tmp_str), compose_string({ 'd','u','o','s' }, tmp_str), 					compose_string({ 'n','a','t','i','v','e' }, tmp_str), 						compose_string({ 'd','u','o','s' }, tmp_str)} ) ;

	bFound = false;
	for( auto it = ck_datas.begin(); it != ck_datas.end(); ++it ) {
		std::string & _cpu 		= (*it)[0];
		std::string & _hardware	= (*it)[1];
		std::string & _device	= (*it)[2];
		std::string & _product	= (*it)[3];

		if (( _cpu.length() 	 < 2 ? true : _cpu.compare( cpu ) == 0 ) 			&&
			( _hardware.length() < 2 ? true : _hardware.compare( hardware ) == 0 ) 	&&
			( _device.length() 	 < 2 ? true : _device.compare( device ) == 0 ) 		&&
			( _product.length()  < 2 ? true : _product.compare( product ) == 0 )) {

			LOGD( "check VM : cpu(%s) && hardware(%s) && device(%s) && product(%s)", _cpu.c_str(), _hardware.c_str(), _device.c_str(), _product.c_str());
			return true;
		}
	}

	// check (cpu && ( hardware || device || product ))
	std::vector<std::vector<std::string>> ck_datas2;
	ck_datas2.push_back( { compose_string({ 'x','8','6' }, tmp_str), compose_string({ 'v','b','o','x','8','6' }, tmp_str), compose_string({ 'v','b','o','x','8','6' }, tmp_str), compose_string({ 'v','b','o','x','8','6' }, tmp_str)} ) ;

	bFound = false;
	for( auto it = ck_datas.begin(); it != ck_datas.end(); ++it ) {
		std::string & _cpu 		= (*it)[0];
		std::string & _hardware	= (*it)[1];
		std::string & _device	= (*it)[2];
		std::string & _product	= (*it)[3];

		if (( _cpu.length() 	 	 < 2 ? true : _cpu.compare( cpu ) == 0 ) &&
			( 	( _hardware.length() < 2 ? true : _hardware.compare( hardware ) == 0 ) 	||
				( _device.length() 	 < 2 ? true : _device.compare( device ) == 0 ) 		||
				( _product.length()  < 2 ? true : _product.compare( product ) == 0 ))
			) {

			LOGD( "check VM : cpu(%s) && ( hardware(%s) || device(%s) || product(%s))", _cpu.c_str(), _hardware.c_str(), _device.c_str(), _product.c_str());
			return true;
		}
	}


	return false;
}

void init( JNIEnv * env, jobject, jobject /*appContext*/ )
{
	LOGT();
	if( check_vm( env )) {
		LOGD("VM");
	} else {
		LOGD("NOT VM");
	}
}
