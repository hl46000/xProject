/*
 * hooking.h
 *
 *  Created on: 2015. 9. 11.
 *      Author: purehero2
 */

#ifndef UTIL_HOOKING_H_
#define UTIL_HOOKING_H_

#include <string>
#include <vector>
#include <map>

#include <dlfcn.h>
#include "util/linker.h"

typedef struct _hooking_simple_maps_info_
{
    uintptr_t   s_add, e_add;
    std::string name;
} HOOKING_MAPS_INFO ;

class hooking {
public:
	hooking();
	virtual ~hooking();

	int get_target_module( int pid );
	unsigned try_hooking( const char *symbol, unsigned newval, bool bLog = true );

	const char * find_name( uintptr_t addr );

protected :
	std::vector<HOOKING_MAPS_INFO> m_maps_info;

	unsigned elfhash( const char *_name );
	Elf32_Sym *soinfo_elf_lookup(struct soinfo *si, unsigned hash, const char *name);
	unsigned libhook_patch_address( unsigned addr, unsigned newval );
};

#endif /* UTIL_HOOKING_H_ */
