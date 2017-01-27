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
	char unknown2[6];
	int unknown3;
	char filename[128];
	unsigned long startAddr,endAddr;
	unsigned long unknown1;
} mapsInfo;


class maps_reader {
public:
	maps_reader( pid_t _pid );
	virtual ~maps_reader();

	int read();
	const mapsInfo * get( int idx );
	void print( const mapsInfo * pMapsInfo );
private :
	const pid_t pid;
	std::vector<mapsInfo> maps_infos;
};

#endif /* MAPS_READER_H_ */
