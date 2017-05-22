/*
 * smc_module_impl.h
 *
 *  Created on: 2015. 7. 24.
 *      Author: purehero2
 */

#ifndef SMC_MODULE_IMPL_H_
#define SMC_MODULE_IMPL_H_

#define NOT_APPLIED_SMC_TAG_SIZE_VALUE			250	  // SMC 처리가 되지 않을 때의 tag len 값
#define DEF_TAG_VALUE_TYPE_INDEX		0

#include <unistd.h>

const int get_smc_start_tag_size();
const int get_smc_end_tag_size();
void load_tag_values( unsigned long offset, unsigned char ** values );
const int * get_tag_values_index();
int process_target_addr( unsigned char ** _tag_values, unsigned long _s_pos, unsigned long _e_pos, int _s_tag_len, int _e_tag_len );

#ifdef __i386__
#include "smc_module_x86.h"
#define ASM_CMD3(op,rg,val)					ASM( #op " " CONSTANT val "," rg LINE_FEED )
#define ASM_CMD3_CONSTANT(op,rg,val)		ASM( #op " " val "," rg LINE_FEED )
#endif

#ifdef __arm__
#include "smc_module_arm.h"
#define ASM_CMD3(op,rg,val)					ASM( #op " " rg "," CONSTANT val LINE_FEED )
#define ASM_CMD3_CONSTANT(op,rg,val)		ASM( #op " " rg "," val LINE_FEED )
#endif

#ifdef __aarch64__
#endif

#define ASM_CMD2(op,rg)			ASM( op " " rg LINE_FEED )

#define PUSH_ACC_REG()			ASM_CMD2(ASM_PUSH,STACK_ACC_REG)
#define MOV_2_ACC_REG(value)	ASM_CMD3(mov, ACC_REG, #value)
#define POP_ACC_REG()			ASM_CMD2(ASM_POP,STACK_ACC_REG)

// #define GET_CURRENT_ADDR(addr) 현재 주소를 반환하는 MACRO 함수


/*
* 서버에서 SMC Start tag size을 반환하는 함수를 찾기 위한 TAG
*/
#define SMC_FIND_START_SIZE_TAG() 	\
		PUSH_ACC_REG(); 			\
		MOV_2_ACC_REG( 10 );MOV_2_ACC_REG( 20 );MOV_2_ACC_REG( 30 );MOV_2_ACC_REG( 40 );MOV_2_ACC_REG( 50 ); \
		POP_ACC_REG();

/*
* 서버에서 SMC End tag size을 반환하는 함수를 찾기 위한 TAG
*/
#define SMC_FIND_END_SIZE_TAG() 	\
		PUSH_ACC_REG(); 			\
		MOV_2_ACC_REG( 50 );MOV_2_ACC_REG( 40 );MOV_2_ACC_REG( 30 );MOV_2_ACC_REG( 20 );MOV_2_ACC_REG( 10 ); \
		POP_ACC_REG();

/*
* 서버에서 SMC Tag values index tag 값을 반환하는 함수를 찾기 위한 TAG
*/
#define SMC_FIND_TAG_VALUES_INDEX_TAG()			\
		PUSH_ACC_REG(); 			\
		MOV_2_ACC_REG( 20 );MOV_2_ACC_REG( 30 );MOV_2_ACC_REG( 40 );MOV_2_ACC_REG( 50 );MOV_2_ACC_REG( 60 ); \
		POP_ACC_REG();

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 서버에서 암호화를 수행하기 위한 영역을 찾기 위한 TAG
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#define SMC_PURE_START_TAG(tagID) 	\
		PUSH_ACC_REG(); \
		MOV_2_ACC_REG( 110 );MOV_2_ACC_REG( 120 );MOV_2_ACC_REG( 130 );MOV_2_ACC_REG( 140 );MOV_2_ACC_REG( 150 );\
		POP_ACC_REG();

#define SMC_PURE_END_TAG(tagID) 	\
		PUSH_ACC_REG();\
		MOV_2_ACC_REG( 150 );MOV_2_ACC_REG( 140 );MOV_2_ACC_REG( 130 );MOV_2_ACC_REG( 120 );MOV_2_ACC_REG( 110 );\
		POP_ACC_REG();

/*
* SMC Dummy Tag 크기는 항상 SMC_PURE_START_TAG와 동일하게 해야 한다.
*/
#define SMC_DUMMY_TAG(tagID) 	\
		PUSH_ACC_REG(); \
		MOV_2_ACC_REG( 111 );MOV_2_ACC_REG( 122 );MOV_2_ACC_REG( 133 );MOV_2_ACC_REG( 144 );MOV_2_ACC_REG( 155 );\
		POP_ACC_REG();
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*
* SMC Start tag가 시작되기 전에 수행되어야 되는 동작
*/
#define SMC_PREPARE_START_TAG(tagID)											\
		LOAD_LABEL2( "__s_"#tagID"_s__",__s_##tagID_addr); 						\
		LOAD_LABEL2("__e_"#tagID"_e__",__e_##tagID_addr); 						\
		load_tag_values(__s_##tagID_addr, (unsigned char **)&__##tagID_values[0]);										\
		if( 0 > process_target_addr( __##tagID_values, __s_##tagID_addr, __e_##tagID_addr, __s_tag_start_size, __e_tag_start_size )) break;	\
		SMC_DUMMY_TAG(tagID);												\
		SET_LABEL2( "__s_"#tagID"_s__" );


/*
* SMC End tag가 후에 수행하여야 하는 동작
*/
#define SMC_POST_END_TAG(tagID)							\
		SET_LABEL2( "__e_"#tagID"_e__" );				\
		process_target_addr( __##tagID_values, __s_##tagID_addr, __e_##tagID_addr, __s_tag_start_size, __e_tag_start_size );

#endif /* SMC_MODULE_H_ */
