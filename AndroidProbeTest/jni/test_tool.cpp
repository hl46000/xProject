#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <dirent.h>

#include "probe_log.h"

#include <fcntl.h>
#include <signal.h>

#include <sys/inotify.h>


#if 0		// 디렉토리 변화 감시 테스트

			// 폴더 변화에 대한 감시 이벤트는 발생하나, 정확히 폴더의 어디가 변화 되었는지 확인 할 수가 없다.

int pid = 0;
static void handler(int sig, siginfo_t *si, void *data)
{
    printf("event handler %d %d %d %d\n", sig, si->si_fd, si->si_pid, si->si_uid );
    pid = si->si_fd;
    //kill( pid, -9 );
}
void test_dnotify()
{
	LOGT();

	struct sigaction sa;
	sa.sa_sigaction = handler;
	sigemptyset(&sa.sa_mask);
	sa.sa_flags = SA_SIGINFO;
	sigaction(SIGRTMIN + 1, &sa, NULL);

	int fd = ::open( argv[1], O_RDONLY );
	fcntl( fd, F_SETSIG, SIGRTMIN + 1);
	fcntl( fd, F_NOTIFY, DN_ACCESS|DN_CREATE|DN_MODIFY|DN_MULTISHOT );

	while (1)
	{
		pause();
		printf("event occured for fd=%d\n", pid);
	}

}
#endif

#include <vector>
#include <string>

#include <sys/inotify.h>

#define INOTIFY_BUF_LEN (10*(sizeof(struct inotify_event) + NAME_MAX + 1))
void test_inotify( const std::vector<std::string> & paths )
{
	LOGT();

	int nRead;
	char buf[INOTIFY_BUF_LEN] __attribute__((aligned(8)));

	int fd = inotify_init();
	if( fd == -1 ) {
		perror( "inotify_init" );
		return;
	}

	for( int i = 0; i < (int)paths.size(); i++ )
	{
		if( -1 != inotify_add_watch( fd, paths[i].c_str(), IN_ALL_EVENTS ))
		{
			LOGD( "Watching %s", paths[i].c_str() );
		}
	}

	struct inotify_event *event;
	while( 1 )
	{
		nRead = ::read( fd, buf, INOTIFY_BUF_LEN );
		if( nRead <= 0 )
		{
			perror( "read" );
			break;
		}

		LOGD("Read %d bytes from inotify fd\n", (long) nRead);

		for( char * p = buf; p < buf + nRead; )
		{
			event = ( struct inotify_event * )p;
			LOGD( "NOTIFY : %s", event->name );
			p += sizeof(struct inotify_event) + event->len;
		}

	}
}

void test_inotify_run( int nNum, char * argv[] )
{
	LOGT();
	LOGD( "[BEGIN] test_inotify_run" );

	std::vector<std::string> paths;
	for( int i = 0; i < nNum; i++ )
	{
		paths.push_back( argv[1+i] );
	}

	test_inotify( paths );
	LOGD( "[END] test_inotify_run" );
}

int main( int argc, char * argv[] )
{
	LOGT();

	test_inotify_run( argc-1, argv );

	return 0;
}

