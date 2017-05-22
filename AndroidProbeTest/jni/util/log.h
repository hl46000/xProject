/*
 * probelog.h
 *
 *  Created on: 2015. 7. 7.
 *      Author: purehero2
 */

#ifndef PROBE_LOG_H_
#define PROBE_LOG_H_

#include <android/log.h>

#if 0
#define  LOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE,get_logTag(),__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,get_logTag(),__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,get_logTag(),__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,get_logTag(),__VA_ARGS__)
#define  LOGT()		__android_log_print(ANDROID_LOG_DEBUG,get_logTag(),"%s:%s",__FILE__,__func__ )
#else
#define  LOGV(...)
#define  LOGD(...)
#define  LOGI(...)
#define  LOGW(...)
#define  LOGT()

#endif

#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,get_logTag(),__VA_ARGS__)

const char * get_logTag();
void change_logTag( const char * logTag );

void print_address( const char * title, const unsigned char * pAddr, int len );

#endif /* PROBE_LOG_H_ */
