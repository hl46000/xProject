/*
 * jstr2str.h
 *
 *  Created on: 2016. 12. 12.
 *      Author: purehero
 */

#ifndef JSTR2STR_H_
#define JSTR2STR_H_

#include <jni.h>

class jstr2str {
public:
	jstr2str( JNIEnv * _pEnv, jstring _jstr );
	virtual ~jstr2str();

	const char * c_str();
private :
	JNIEnv * pEnv;
	const jstring jstr;
	const char * pStr;
};

#endif /* JSTR2STR_H_ */
