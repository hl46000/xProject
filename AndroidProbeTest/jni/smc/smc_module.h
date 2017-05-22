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
#define SMC_START_TAG(tagID) 											\
		LOGD("SMC START TAG : " #tagID );								\
		static int __s_tag_start_size = get_smc_start_tag_size();		\
		static int __e_tag_start_size = get_smc_end_tag_size();			\
		static unsigned long __s_##tagID_addr, __e_##tagID_addr;		\
		static unsigned char * __##tagID_values[5];						\
		static bool __b_##tagID_tag_enable = __s_tag_start_size != NOT_APPLIED_SMC_TAG_SIZE_VALUE;	\
		usleep( 1 );						\
		while( __b_##tagID_tag_enable )		\
		{									\
			SMC_PREPARE_START_TAG(tagID);	\
			SMC_PURE_START_TAG(tagID); 		\
			break;							\
		}

// SMC Tag 의 끝을 나타내는 값
#define SMC_END_TAG(tagID) 					\
		LOGD("SMC END TAG : " #tagID );	    \
		usleep( 1 );						\
		while( __b_##tagID_tag_enable )		\
		{									\
			SMC_PURE_END_TAG(tagID);		\
			SMC_POST_END_TAG(tagID);		\
			break;							\
		}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#endif /* SMC_MODULE_H_ */
