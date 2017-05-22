/*
 * injection_detecter.h
 *
 *  Created on: 2015. 10. 2.
 *      Author: purehero2
 */

#ifndef INJECTION_INJECTION_DETECTER_H_
#define INJECTION_INJECTION_DETECTER_H_

#include "injectionPLT.h"

class injection_detecter : public injectionPLT {
public:
	injection_detecter();
	virtual ~injection_detecter();

	void working();		// 실제 파일을 dlopen 하여 plt 에 등록된 주소가 maps 의 주소 범위에 있는지 확인 합니다.
};

#endif /* INJECTION_INJECTION_DETECTER_H_ */
