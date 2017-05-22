/*
 * mapsreader.h
 *
 *  Created on: 2015. 7. 8.
 *      Author: purehero2
 */

#ifndef MAPS_READER_H_
#define MAPS_READER_H_

#include <stdio.h>
#include <vector>
#include <string>
#include <string.h>

typedef struct _mapsinfo{
	uint64_t startAddr,endAddr;
	char permission[5];
	uint64_t unknown1;
	char unknown2[6];
	int unknown3;
	char filename[128];
} maps_info;

class maps_reader_impl {
public:
	maps_reader_impl( int pID );
	virtual ~maps_reader_impl();

	int read();
	const maps_info * search( char * name );
	int search( char * name, std::vector<maps_info> & result );

	const std::vector<maps_info> & get_maps_infos(){ return maps_infos; }

	void print( const maps_info * info );
	void print_all();
protected :
	int process_id;
	std::vector<maps_info> maps_infos;
};

class maps_reader : public maps_reader_impl
{
public :
	maps_reader( int pID );
	virtual ~maps_reader();

	int read();
};

#endif /* MAPS_READER_H_ */
