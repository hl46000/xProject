#ifndef __ANTI_SPEED_HACK_MAIN_HEADER__
#define __ANTI_SPEED_HACK_MAIN_HEADER__

#include <jni.h>
#include <android/log.h>

#define LOG_TAG		"ASH"
#define  LOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGT()		__android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,"%s:%s",__FILE__,__func__ )

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include <sys/time.h>
#include <unistd.h>

#ifdef __cplusplus
extern "C" {
#endif

void write_notification( const char * message );
void  alarm_handler(int sig);

#define EXIT_TIMER(t,m)					\
		signal(SIGALRM, alarm_handler);	\
		alarm(t);						\
		LOGI("%s",m);					\
		write_notification(m);

#ifdef __cplusplus
}
#endif

#endif
