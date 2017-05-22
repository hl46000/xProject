/*
 * probe.h
 *
 *  Created on: 2015. 7. 7.
 *      Author: purehero2
 */

#ifndef PROBE_H_
#define PROBE_H_

#include "util/log.h"
#include "probe_lua.h"

class probe {
public:
	probe();
	virtual ~probe();

	void run( int port, char * pProbe, int len );

protected :
	int connection_to_parent( int port );
	int self_hash_check( int sockfd, char * pProbe, int probe_len );
	int execute_lua_script( int sockfd );

	probe_lua lua;
};

#endif /* PROBE_H_ */
