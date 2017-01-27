/*
 * mapsreader.h
 *
 *  Created on: 2017. 1. 26.
 *      Author: purehero
 */

#ifndef MAPS_READER_H_
#define MAPS_READER_H_

#include <stdio.h>
#include <sys/types.h>
#include <unistd.h>

#include <vector>

typedef struct _mapsinfo{
	char permission[5];
	char filename[128];
	unsigned long startAddr;
	unsigned long endAddr;
} mapsInfo;


class maps_reader {
public:
	maps_reader( pid_t _pid );
	virtual ~maps_reader();

	int read();
	const mapsInfo * get( unsigned int idx );
	void print( const mapsInfo * pMapsInfo );
private :
	const pid_t pid;
	std::vector<mapsInfo> maps_infos;
};

#endif /* MAPS_READER_H_ */
