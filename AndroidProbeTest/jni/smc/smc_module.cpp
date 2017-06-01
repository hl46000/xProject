/*
 * smc_module.cpp
 *
 *  Created on: 2015. 7. 24.
 *      Author: purehero2
 */

#include "smc_module_impl.h"
#include "util/log.h"

#include <sys/mman.h>
#include <unistd.h>

#define SETRWX(addr, len) mprotect((void*)((addr) & ~0xFFF), (len) + ((addr) - ((addr) &~0xFFF)), PROT_READ | PROT_EXEC | PROT_WRITE);usleep(100);
#define SETRX(addr, len) mprotect((void*)((addr) & ~0xFFF), (len) + ((addr) - ((addr) &~0xFFF)), PROT_READ | PROT_EXEC);usleep(100);

#if 0
/*
 * SMC target area 에 대한 복호화 작업을 진행한다.
 */
int process_target_addr( unsigned char ** _tag_values, unsigned long _s_pos, unsigned long _e_pos, int _s_tag_len, int _e_tag_len )
{
	LOGT();
	for( int i = 0; i < 5; i++ ) LOGD( "Tag Values[%d] : %d", i, *_tag_values[i] );
	LOGD("s_tag_addr : %lu\ne_tag_addr : %lu", _s_pos, _e_pos );
	LOGD("s_tag_len : %d\ne_tag_len : %d", _s_tag_len, _e_tag_len );

	unsigned long target_start_offset 	= _s_pos + _s_tag_len;
	unsigned long target_end_offset 	= _e_pos - _e_tag_len;

	unsigned char * target_data_ptr 	= ( unsigned char * ) target_start_offset;
	int 			target_data_len		= target_end_offset - target_start_offset;

	print_address( "Target data", target_data_ptr, target_data_len );

	int tag_area_len = _e_pos - _s_pos;
	SETRWX( _s_pos, tag_area_len );

	unsigned char * tmpBuff = target_data_ptr;
	for( int i = 0; i < target_data_len; i++ )
	{
		*tmpBuff = *tmpBuff ^ 0xaa;
		++tmpBuff;
	}

	     if( *_tag_values[DEF_TAG_VALUE_TYPE_INDEX] == 1 ) *_tag_values[DEF_TAG_VALUE_TYPE_INDEX] = 2;
	else if( *_tag_values[DEF_TAG_VALUE_TYPE_INDEX] == 2 ) *_tag_values[DEF_TAG_VALUE_TYPE_INDEX] = 1;
	     if( *_tag_values[DEF_TAG_VALUE_TYPE_INDEX] == 3 ) *_tag_values[DEF_TAG_VALUE_TYPE_INDEX] = 4;
	else if( *_tag_values[DEF_TAG_VALUE_TYPE_INDEX] == 4 ) *_tag_values[DEF_TAG_VALUE_TYPE_INDEX] = 3;

   	SETRX( _s_pos, tag_area_len );

   	print_address( "SMC Target data", target_data_ptr, target_data_len );

#ifndef __i386__
	cacheflush( _s_pos, _e_pos, 0);
#endif

	return -1;
}

/*
 * SMC start tag의 byte 수를 반환 합니다.
 */
const int get_smc_start_tag_size()
{
	LOGT();
	SMC_FIND_START_SIZE_TAG();
	return NOT_APPLIED_SMC_TAG_SIZE_VALUE;
}

/*
 * SMC end tag의 byte 수를 반환 합니다.
 */
const int get_smc_end_tag_size()
{
	LOGT();
	SMC_FIND_END_SIZE_TAG();
	return NOT_APPLIED_SMC_TAG_SIZE_VALUE;
}

const int * get_tag_values_index()
{
	static int g_tag_values_index[5] = { -1, -1, -1, -1, -1 };
	if( g_tag_values_index[0] == -1 )
	{
		SMC_FIND_TAG_VALUES_INDEX_TAG();

		g_tag_values_index[0] = 0x7A;
		g_tag_values_index[1] = 0x7B;
		g_tag_values_index[2] = 0x7C;
		g_tag_values_index[3] = 0x7D;
		g_tag_values_index[4] = 0x7E;
	}
	return g_tag_values_index;
}

/*
 * SMC end tag의 byte 수를 반환 합니다.
 */
void load_tag_values( const unsigned long offset, unsigned char ** values )
{
	const int * indexOfs = get_tag_values_index();
	char * pAddr = ( char * ) offset;

	values[0] = ( unsigned char * )( pAddr + indexOfs[0] );
	values[1] = ( unsigned char * )( pAddr + indexOfs[1] );
	values[2] = ( unsigned char * )( pAddr + indexOfs[2] );
	values[3] = ( unsigned char * )( pAddr + indexOfs[3] );
	values[4] = ( unsigned char * )( pAddr + indexOfs[4] );
}
#endif
