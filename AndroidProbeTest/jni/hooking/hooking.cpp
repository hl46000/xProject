/*
 * hooking.cpp
 *
 *  Created on: 2015. 9. 11.
 *      Author: purehero2
 */

#include "hooking.h"

#include <dlfcn.h>
#include <errno.h>
#include <stdlib.h>
#include <sys/mman.h>

#include <vector>
#include <map>

#include "linker.h"
#include "util/log.h"


hooking::hooking() {}
hooking::~hooking() {}

int hooking::get_target_module( int pid )
{
	LOGT();

	m_maps_info.clear();

	char buffer[1024] = {0};
	sprintf( buffer, "/proc/%d/maps", pid );

	FILE *fp = fopen( buffer, "rt" );
	if( fp == NULL ){
		perror("fopen");
		return -1;
	}

	HOOKING_MAPS_INFO module;
	while( fgets( buffer, sizeof(buffer), fp ) ) {
		if( strstr( buffer, "r-xp" ) ){
			if( strstr( buffer, "libc.so" ) != NULL ) continue;
			//if( strstr( buffer, "libMainHooking.so" ) != NULL ) continue;
			//if( strstr( buffer, "libMainSimple.so" ) == NULL ) continue;

			char * pName 	 = strrchr( buffer, ' ' ) + 1;
			if( pName[0] == '[' ) continue;
			if( pName[0] == '(' ) {		// 파일명 끝에 (deleted) 라는 문구 처리
				memcpy( pName-1, "\n\0", 2 );
				pName = strrchr( buffer, ' ' ) + 1;
			}

			module.s_add = (uintptr_t)strtoul( buffer, NULL, 16 );
			module.e_add = (uintptr_t)strtoul( strchr( buffer, '-' ) + 1, NULL, 16 );
			module.name  = pName;
			module.name.resize( module.name.size() - 1);

			m_maps_info.push_back( module );
		}
	}

	if(fp){
		fclose(fp);
	}

	return (int) m_maps_info.size();
}


int hooking::try_hooking( const char *symbol, unsigned newval )
{
	//LOGT();

	if ( !symbol || !newval ) {
		LOGE( "if ( !symbol || !newval)" );
		return -1;
	}

	unsigned symbol_hash = elfhash(symbol);
	for( std::vector<HOOKING_MAPS_INFO>::iterator it = m_maps_info.begin(); it != m_maps_info.end(); it++ ) {
		LOGD( "Trying to hooking '%s' file in function '%s'", it->name.c_str(), symbol );

		if( *((char*)( it->s_add + 1)) != 'E' ) {
			LOGE( "Not ELF file : %s", it->name.c_str() );
			continue;
		}

		// since we know the module is already loaded and mostly
		// we DO NOT want its constructors to be called again,
		// ise RTLD_NOLOAD to just get its soinfo address.
		soinfo * si = (struct soinfo *) dlopen( it->name.c_str(), 0 /* RTLD_NOLOAD */ );
		if( !si ) {
			LOGE( "Trying to hooking '%s' file : dlopen error: %s", it->name.c_str(), dlerror() );
			continue;
		}

		LOGD( "soinfo name : %s", si->name );

		Elf32_Sym * s = soinfo_elf_lookup(si, symbol_hash, symbol);
		if (!s)
		{
			LOGE( "Failed to hooking '%s:%s' function (soinfo_elf_lookup)", it->name.c_str(), symbol );
			continue;
		}

		unsigned int sym_offset = s - si->symtab;

		if( si->plt_rel_count == 0 ) {
			LOGD( "Failed to hooking : plt_rel_count is 0" );
			continue;
		}

		bool found_plt_rel = false;

		size_t i;
		Elf32_Rel *rel = NULL;
		for( i = 0, rel = si->plt_rel; !found_plt_rel && i < si->plt_rel_count; ++i, ++rel ) {
			unsigned type  = ELF32_R_TYPE(rel->r_info);
			unsigned sym   = ELF32_R_SYM(rel->r_info);
			unsigned reloc = (unsigned)(rel->r_offset + si->base);

			//LOGD( "%d ==> sym_offset[%x], sym[%x]", (int)i, sym_offset, sym );
			if( sym_offset == sym ) {
				switch(type) {
					case R_ARM_JUMP_SLOT:
						//LOGD( "Found function %s %x-%x '%x' ==> '%x'", it->name.c_str(), it->s_add, it->e_add, reloc, newval );
						LOGD( "Success hooking '%s:%s' %x to %x", it->name.c_str(), symbol, libhook_patch_address( reloc, newval ), newval );
						found_plt_rel = true;

						break;

					default:
						LOGD( "Expected '%s' R_ARM_JUMP_SLOT, found 0x%X", it->name.c_str(), type );
						break;
				}
			}
		}

		if( !found_plt_rel ) {

			LOGD( "Failed to hooking : not found sym offset" );
		}
	}

	return 0;
}

unsigned hooking::elfhash( const char *_name )
{
	const unsigned char *name = (const unsigned char *) _name;
	unsigned h = 0, g;
	while(*name)
	{
		h = (h << 4) + *name++;
		g = h & 0xf0000000;
		h ^= g;
		h ^= g >> 24;
	}

	return h;
}

Elf32_Sym * hooking::soinfo_elf_lookup(struct soinfo *si, unsigned hash, const char *name)
{
	Elf32_Sym *symtab = si->symtab;
    const char *strtab = si->strtab;
    unsigned n;

    for( n = si->bucket[hash % si->nbucket]; n != 0; n = si->chain[n] ) {
        Elf32_Sym *s = symtab + n;
        const char * p_str = strtab + s->st_name;

        if( strcmp( p_str, name) == 0 ) {
        	return s;
        }
	}

    return NULL;
}



unsigned hooking::libhook_patch_address( unsigned addr, unsigned newval )
{
	//LOGT();

	unsigned original = -1;
    size_t pagesize = sysconf(_SC_PAGESIZE);
    const void *aligned_pointer = (const void*)(addr & ~(pagesize - 1));

    mprotect(aligned_pointer, pagesize, PROT_WRITE | PROT_READ);

    original = *(unsigned *)addr;
    *((unsigned*)addr) = newval;

    mprotect(aligned_pointer, pagesize, PROT_READ);

    return original;
}
