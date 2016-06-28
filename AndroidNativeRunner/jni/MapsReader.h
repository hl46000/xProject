/*
 * MapsResder.h
 *
 *  Created on: 2016. 4. 7.
 *      Author: purehero2
 */

#ifndef MAPSREADER_H_
#define MAPSREADER_H_

#include <stdio.h>

#include <vector>
#include <map>

class MAPS_DATA {
public :
	uint64_t startAddr,endAddr;
	char permission[5];
	uint64_t unknown1;
	char unknown2[6];
	int unknown3;
	char filename[128];

	void print() const;
};

class MapsReader {
public:
	MapsReader( int pid );
	virtual ~MapsReader();

	int read_maps_infos();
	const std::vector<MAPS_DATA> * get_maps_infos(){ return &m_maps_infos; }

	int find_infos( const char * filename, std::vector<MAPS_DATA*> & result);
protected :
	int m_pid;

	std::vector<MAPS_DATA> 			m_maps_infos;
};

#endif /* MAPSREADER_H_ */
