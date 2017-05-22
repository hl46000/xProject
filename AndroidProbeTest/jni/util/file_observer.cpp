/*
 * file_observer.cpp
 *
 *  Created on: 2015. 9. 11.
 *      Author: purehero2
 */

#include <file_observer.h>
#include "log.h"

#include <unistd.h>


void * file_observer_thread_module( void * param );

file_observer::file_observer()
{
	observer_id = -1;
	thread_id	= -1;

	file_observer_handler = NULL;
}

file_observer::~file_observer()
{
	finish();
}

int file_observer::init()
{
	observer_id = inotify_init();
	if ( observer_id < 0)
	{
		perror("inotify_init");
	}
	return observer_id;
}

void file_observer::finish()
{
	cancel_start();
	if( observer_id != -1 )
	{
		close( observer_id );

		observer_id = -1;
	}
}

int file_observer::add( const std::string & path )
{
	if( -1 != inotify_add_watch( observer_id, path.c_str(), IN_ALL_EVENTS ))
	{
		LOGD( "ADD WATCHED : %s", path.c_str());
		return 1;
	}
	return -1;
}

void file_observer::cancel_start()
{
	if( thread_id != -1 )
	{
		pthread_kill( thread_id, SIGUSR1 );
	}
	thread_id = -1;
}

int file_observer::start( void (*observer_handler)( struct inotify_event  *event ) )
{
	file_observer_handler = observer_handler;

	cancel_start();
	pthread_create( &thread_id, NULL, file_observer_thread_module, this );

	return 0;
}

void * file_observer_thread_module( void * param )
{
	file_observer * parent = ( file_observer * ) param;

	void (*file_observer_handler)( struct inotify_event  *event );
	file_observer_handler = parent->get_observer_handler();

	int inotify_fd = parent->get_observer_fd();

	const int EVENT_SIZE  = ( sizeof (struct inotify_event) );
	const int BUF_LEN     = ( 1024 * ( EVENT_SIZE + 16 ) );

	char read_buff[BUF_LEN];
	int len;

	while( 1 )
	{
		if(( len = read( inotify_fd, read_buff, BUF_LEN)) < 0 )
		{
			perror("read");
			break;
		}

		int i = 0;
		while( i < len )
		{
			struct inotify_event  *event = (struct inotify_event *) &read_buff[i];
			//LOGD("[debug] wd=%d mask=%d cookie=%d len=%d dir=%s\n", event->wd, event->mask, event->cookie, event->len, (event->mask & IN_ISDIR)?"yes":"no");

			if( file_observer_handler != NULL )
			{
				(*file_observer_handler)( event );
			}

			i += EVENT_SIZE + event->len;
		}
	}

	return NULL;
}
