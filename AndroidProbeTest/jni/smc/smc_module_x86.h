/*
 * smc_module_x86.h
 *
 *  Created on: 2015. 7. 24.
 *      Author: purehero2
 */

#ifndef SMC_MODULE_X86_H_
#define SMC_MODULE_X86_H_

#define ASM				__asm__ __volatile__

#define PUSH_ACC		ASM ("push %eax")
#define MOVE(x)			ASM ("mov $"#x",%eax")
#define POP_ACC			ASM ("pop %eax")

#endif /* SMC_MODULE_X86_H_ */
