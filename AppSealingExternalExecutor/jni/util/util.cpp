/*
 * util.cpp
 *
 *  Created on: 2017. 2. 1.
 *      Author: purehero
 */

#include "util.h"


bool file_exist( const char * filepath )
{
	return access( filepath, 0 ) == 0;
}


/*
 * 문자열이 노출되지 않게 하기위한 함수
 *
 * <사용방법>
 * std::string strTemp;
 * compose_string( {'T','e','s','T'}, strTemp ); // strTemp 에 문자열을 기록하고, const char * 타입으로 문자열을 반환한다.
 *
 * */

const char * compose_string( std::initializer_list<char> l )
{
	std::shared_ptr<std::string> tmp_str( new std::string());
	return compose_string( l, *tmp_str );
}

const char * compose_string( std::initializer_list<char> l, std::string & tmp_str )
{
	tmp_str.assign( l.begin(), l.end() );
	return tmp_str.c_str();
}
