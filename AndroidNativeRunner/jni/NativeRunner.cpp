#include <stdio.h>
#include <stdlib.h>

#include <sys/ptrace.h>
#include <sys/wait.h>
#include <unistd.h>

#include "ProcessList.h"
#include "MapsReader.h"

void test_maps_reader( int pid )
{

}

/*
 * �Էµ� pid �� Attach �� �Ǵ����� �׽�Ʈ �մϴ�.
 * */
void test_attach( int pid )
{
	printf( "\n[PROCESS ATTACH TEST]\n");
	printf( "trying to attach to %d", pid );
	for( int i = 0; i < 30; i++ )
	{
		if ( ptrace( PTRACE_ATTACH, pid, NULL, NULL ) == -1 )
		{
			printf(".");
			fflush( stdout );

			usleep(100000);

			continue;
		}

		// attach �Ϸ� ���
		int status;
		waitpid( pid, &status, WUNTRACED );
		printf( "\n    --> Attach OK\n" );

		printf( "detach waitting \n" );
		for( int i = 0; i < 10; i++ )
		{
			printf("%d\n", 9 - i );
			fflush( stdout );
			sleep(1);
		}
		ptrace( PTRACE_DETACH, pid, NULL, NULL );
		printf( "detached\n" );

		while( ptrace( PTRACE_CONT, pid, NULL, NULL ) == -1 ) usleep( 10000 );
		printf( "continue\n" );

		break;
	}

	printf( "\ndone\n" );
}


int main()
{
	// �ܸ����� Process ������ ȹ���Ѵ�.
	ProcessList process_list;
	int ps_cnt = process_list.read_process_list();
	if( ps_cnt < 1 )
	{
		printf("failed to get process list\n" );
		return -1;
	}

#if 0
	const std::vector<PS_DATA> * proc_infos = process_list.get_process_infos();
	(*proc_infos)[0].print_header();
	for( int i = 0; i < proc_infos->size(); i++ )
	{
		(*proc_infos)[i].print();
	}

#else
	// zygote �� pid �� ã���ϴ�.
	int zygote_pid = process_list.find_pid("zygote");
	if( zygote_pid < 1 )
	{
		printf("could not found 'zygote' process name\n" );
		return -2;
	}

	// ppid �� zygote �� pid�� Ư�� Package name �� ������ process ������ ȹ���Ѵ�.
	const char * target_process_name = "com.nexon.caocao.inhouse";
	int target_pid = process_list.find_pid( zygote_pid, target_process_name );
	if( target_pid < 1 )
	{
		printf("could not found '%s' process name\n", target_process_name );
		return -3;
	}

	const PS_DATA * ps_data = process_list.get_process_info( target_pid );
	if( ps_data != NULL )
	{
		ps_data->print_header();
		ps_data->print();
	}

	MapsReader maps_reader( target_pid );
	int cnt_maps_info = maps_reader.read_maps_infos();
	if( cnt_maps_info < 0 )
	{
		printf("failed to get maps infos!! pid:%d process name:%s\n", ps_data->pid, ps_data->name );
		return -3;
	}
	printf("\nreaded %d maps items\n", cnt_maps_info );
	std::vector<MAPS_DATA *> result;

	//int cnt = maps_reader.find_infos( "Assembly-CSharp.dll", result );
	int cnt = maps_reader.find_infos( "/data/data/com.nexon.caocao.inhouse/lib/", result );
	if( cnt > 0 )
	{
		for( int i = 0; i < result.size(); i++ )
		{
			result[i]->print();
		}
	}


	// ã�� pid �� attach �� �õ� �մϴ�.
	test_attach( target_pid );

	// ã�� pid �� Maps ������ �о� �ɴϴ�.
	test_maps_reader( target_pid );

#endif
	return 0;
}
