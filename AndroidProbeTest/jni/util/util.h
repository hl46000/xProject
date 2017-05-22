#pragma once

#include <jni.h>

#include <string>
#include <vector>

int GuidGenerator( JNIEnv *env, std::string & newGuid, const char * seed = NULL );
int read_thread_ids( int pid, std::vector<int> & TIDs );
std::vector<std::string> split2( const std::string& str, unsigned char * separators, int spLen );

// trim from start
std::string &ltrim(std::string &s);

// trim from end
std::string &rtrim(std::string &s);

// trim from both ends
std::string &trim(std::string &s) ;
