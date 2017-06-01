/*
 * smc_module_impl.h
 *
 *  Created on: 2015. 7. 24.
 *      Author: purehero2
 */

#ifndef SMC_MODULE_IMPL_H_
#define SMC_MODULE_IMPL_H_

#include <unistd.h>

#if defined( __arm__ )
#include "smc_module_arm.h"

#elif defined( __i386__ )
#include "smc_module_x86.h"

#elif defined( __aarch64__ )

#endif

#define SMC_BEGIN_MARKER \
	PUSH_ACC; \
	MOVE('b');MOVE('e');MOVE('g');MOVE('i');MOVE('n');MOVE('_');MOVE('m');MOVE('a');MOVE('r');MOVE('k');MOVE('e');MOVE('r'); \
	POP_ACC;

#define SMC_END_MARKER \
	PUSH_ACC; \
	MOVE('e');MOVE('n');MOVE('d');MOVE('_');MOVE('m');MOVE('a');MOVE('r');MOVE('k');MOVE('e');MOVE('r');MOVE('e');MOVE('r'); \
	POP_ACC;

#endif
