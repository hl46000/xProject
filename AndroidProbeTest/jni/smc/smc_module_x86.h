/*
 * smc_module_x86.h
 *
 *  Created on: 2015. 7. 24.
 *      Author: purehero2
 */

#ifndef SMC_MODULE_X86_H_
#define SMC_MODULE_X86_H_

#define CONSTANT		"$"
#define ACC_REG			"%eax"
#define STACK_ACC_REG	"%eax"
#define ASM_PUSH		"push"
#define ASM_POP			"pop"
#define LINE_FEED		"\n"
#define ASM				__asm__ __volatile__

#if 1

#define SET_LABEL(lb) 				ASM( #lb ":")
#define SET_LABEL2(lb) 				ASM( lb ":")
#define LOAD_LABEL(lb,val)			ASM("lea " #lb ",%0" LINE_FEED :"=r"(val))
#define LOAD_LABEL2(lb,val)			ASM("lea " lb ",%0" LINE_FEED :"=r"(val))

#define GET_CURRENT_ADDR(value) 	\
		LOAD_LABEL( #value "_current_pos_label", value); \
		SET_LABEL(  #value "_current_pos_label" )

#else

// PC 값을 이용한 현재 주소를 얻는 방법
// 정상 동작 하지 않음
// X86에서 엉퉁한 주소를 반환 한다. esp는 stack 의 top을 반환하는 것으로 보임, eip 값을 얻어야 하는데, eip는 빌드가 안됨
#define GET_CURRENT_ADDR(addr) \
			ASM("mov %%esp, %0\n\t":"=r"(addr)); \
			addr-=sizeof( short );
#endif
#endif /* SMC_MODULE_X86_H_ */
