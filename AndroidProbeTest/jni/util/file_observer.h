/*
 * file_observer.h
 *
 *  Created on: 2015. 9. 11.
 *      Author: purehero2
 */

#ifndef UTIL_FILE_OBSERVER_H_
#define UTIL_FILE_OBSERVER_H_

#include <sys/inotify.h>
#include <dirent.h>
#include <stdlib.h>
#include <pthread.h>

#include <vector>
#include <string>

typedef void (*_INOTIFY_HANDLER_)( struct inotify_event  *event );

class file_observer {
public:
	file_observer();
	virtual ~file_observer();

	int init();
	void finish();
	int add( const std::string & path );
	int start( void (*observer_handler)( struct inotify_event  *event ) );

	int get_observer_fd() { return observer_id; }
	_INOTIFY_HANDLER_ get_observer_handler(){ return file_observer_handler; }

private :
	int observer_id;
	pthread_t thread_id;

	void (*file_observer_handler)( struct inotify_event  *event );

	void cancel_start();
};

#endif /* UTIL_FILE_OBSERVER_H_ */
