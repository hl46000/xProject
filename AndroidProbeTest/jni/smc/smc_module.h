/*
 * smc_module.h
 *
 *  Created on: 2015. 7. 24.
 *      Author: purehero2
 */

#ifndef SMC_MODULE_H_
#define SMC_MODULE_H_

#include "smc_module_impl.h"
#include "util/log.h"
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 실제 코드에서 SMC 영역을 나타내기 위한 TAG
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#define SMC_START_TAG	\
	unsigned long s_addr = (unsigned long)&&S_SMC_BEGIN_TAG_S;	\
	unsigned long e_addr = (unsigned long)&&E_SMC_END_TAG_E;	\
	LOGD( "s_addr(0x%lx), e_addr(0x%lx)", s_addr, e_addr );	\
	S_SMC_BEGIN_TAG_S: \
	SMC_BEGIN_MARKER


// SMC Tag 의 끝을 나타내는 값
#define SMC_END_TAG	\
	E_SMC_END_TAG_E:	\
	SMC_END_MARKER


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#endif /* SMC_MODULE_H_ */
