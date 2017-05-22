/*
 * smc_module_arm.h
 *
 *  Created on: 2015. 7. 24.
 *      Author: purehero2
 */

#ifndef SMC_MODULE_ARM_H_
#define SMC_MODULE_ARM_H_

#define CONSTANT		"#"
#define ACC_REG			"r0"
#define STACK_ACC_REG	"{" ACC_REG "}"
#define ASM_PUSH		"push"
#define ASM_POP			"pop"
#define LINE_FEED		"\n\t"
#define ASM				__asm__ __volatile__

#if 1
// asm label 을 이용한 현재 주소를 얻는 방법
#define SET_LABEL(lb) 				ASM( #lb ":")
#define SET_LABEL2(lb) 				ASM( lb ":")
#define LOAD_LABEL(lb,val)			ASM("ldr %0,=" #lb LINE_FEED :"=r"(val))
#define LOAD_LABEL2(lb,val)			ASM("ldr %0,=" lb LINE_FEED :"=r"(val))

#define GET_CURRENT_ADDR(value) 	\
		LOAD_LABEL( #value "_current_pos_label", value); \
		SET_LABEL(  #value "_current_pos_label" )

#else
// PC 값을 이용한 현재 주소를 얻는 방법

// ARM은 r15 register가 program counter 값임
// 아래 로직도 정상동작 함
#define GET_CURRENT_ADDR(addr) \
			ASM("mov %0, r15\n\t":"=r"(addr)); \
			addr-=sizeof( short );
#endif
#endif /* SMC_MODULE_ARM_H_ */
