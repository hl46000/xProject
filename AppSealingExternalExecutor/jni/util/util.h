/*
 * util.h
 *
 *  Created on: 2017. 2. 1.
 *      Author: purehero
 */

#ifndef UTIL_UTIL_H_
#define UTIL_UTIL_H_

#include <string>
#include <vector>
#include <memory>
#include <unistd.h>

bool file_exist( const char * filepath );
const char * compose_string( std::initializer_list<char> l );
const char * compose_string( std::initializer_list<char> l, std::string & tmp_str );


#endif /* UTIL_UTIL_H_ */
