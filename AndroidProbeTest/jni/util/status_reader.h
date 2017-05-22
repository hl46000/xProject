/*
 * status_reader.h
 *
 *  Created on: 2015. 8. 27.
 *      Author: purehero2
 */

#ifndef STATUS_READER_H_
#define STATUS_READER_H_

#include <vector>

class status_reader {
public:
	status_reader( int pID );
	virtual ~status_reader();

	int read();
	const char * search( const char * key );

protected :
	int process_id;
	std::vector<char> _buff;
};

#endif /* STATUS_READER_H_ */
