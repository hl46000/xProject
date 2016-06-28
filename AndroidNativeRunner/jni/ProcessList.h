/*
 * ProcessList.h
 *
 *  Created on: 2016. 3. 11.
 *      Author: purehero2
 */

#ifndef PROCESSLIST_H_
#define PROCESSLIST_H_

#include <vector>

typedef struct _ps_result_struct_
{
	int pid;
	int ppid;
	int tracer_pid;
	char status;
	char name[128];
	char user[32];
	char temp[128];
} PS_DATA;

int get_process_list( int pid, PS_DATA & data );
int get_process_list( std::vector<PS_DATA> & result );

#endif /* PROCESSLIST_H_ */
