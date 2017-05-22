/*
 * injection_detecter_not_read_maps.h
 *
 *  Created on: 2015. 10. 6.
 *      Author: purehero2
 */

#ifndef INJECTION_INJECTION_DETECTER_NOT_READ_MAPS_H_
#define INJECTION_INJECTION_DETECTER_NOT_READ_MAPS_H_

#include "injection/injection_detecter.h"

class injection_detecter_not_read_maps : public injection_detecter {
public:
	injection_detecter_not_read_maps();
	virtual ~injection_detecter_not_read_maps();

	int get_target_module( int pid );		// maps 파일 중 같은 파일의 주소 영역을 계산하여 로딩 합니다.
	void working();
};

#endif /* INJECTION_INJECTION_DETECTER_NOT_READ_MAPS_H_ */
