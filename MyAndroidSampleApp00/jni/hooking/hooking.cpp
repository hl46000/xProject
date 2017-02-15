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

#include "util/linker.h"
#include "alog.h"


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

	char permission[5];
	char unknown2[6];
	int unknown3;
	char filename[128];
	unsigned long startAddr,endAddr;
	unsigned long unknown1;

	HOOKING_MAPS_INFO module;
	while( fgets( buffer, sizeof(buffer), fp ) ) {
		sscanf( buffer, "%08lx-%08lx %s %08lx %s %d %s", &startAddr, &endAddr,(char*)&permission,&unknown1,(char*)&unknown2,&unknown3,(char*)&filename);

		if( filename[0] == '[') continue;
		if( strstr( filename, ".so") == NULL ) continue;

		if( permission[0] != 'r' ) continue;
		if( permission[2] != 'x' ) continue;
		if( startAddr == 0 ) continue;

		unsigned char* tmpChar = (unsigned char *)startAddr;
		if(tmpChar[18] != 40 && tmpChar[18] != 183 )
		{
			//LOGI("'%s' is not ARM/ARM64 architectures",info->filename);
			continue;
		}

		if( strstr( filename, "libhooking.so") != NULL ) continue;

		module.s_add = startAddr;
		module.e_add = endAddr;
		module.name  = filename;

		m_maps_info.push_back( module );
	}

	fclose(fp);
	return (int) m_maps_info.size();
}


unsigned hooking::try_hooking( const char *symbol, unsigned newval )
{
	//LOGT();

	unsigned ret = 0;
	if ( !symbol || !newval ) {
		LOGE( "if ( !symbol || !newval)" );
		return ret;
	}

	unsigned symbol_hash = elfhash(symbol);
	for( std::vector<HOOKING_MAPS_INFO>::iterator it = m_maps_info.begin(); it != m_maps_info.end(); it++ ) {
		usleep( 10 );

		LOGD( "Trying to hooking '%s' file in function '%s'", it->name.c_str(), symbol );

		if( *((char*)( it->s_add + 1)) != 'E' ) {
			LOGE( "Not ELF file : %s", it->name.c_str() );
			continue;
		}

		// since we know the module is already loaded and mostly
		// we DO NOT want its constructors to be called again,
		// ise RTLD_NOLOAD to just get its soinfo address.
		//soinfo * si = (struct soinfo *) dlopen( it->name.c_str(), RTLD_GLOBAL | RTLD_NOW );
		soinfo * si = (struct soinfo *) dlopen( it->name.c_str(), 0 );
		if( !si ) {
			//LOGE( "Trying to hooking '%s' file : dlopen error: %s", it->name.c_str(), dlerror() );
			continue;
		}

		LOGD( "soinfo name : %s", si->name );

		Elf32_Sym * s = soinfo_elf_lookup(si, symbol_hash, symbol);
		if (!s)
		{
			//LOGE( "Failed to hooking '%s:%s' function (soinfo_elf_lookup)", it->name.c_str(), symbol );
			continue;
		}

		LOGD( "soinfo_elf_lookup" );

		unsigned int sym_offset = s - si->symtab;

		if( si->plt_rel_count == 0 ) {
			//LOGD( "Failed to hooking : plt_rel_count is 0" );
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
						ret = libhook_patch_address( reloc, newval );
						LOGD( "Success hooking '%s:%s' %x to %x", it->name.c_str(), symbol, ret, newval );
						found_plt_rel = true;


						hooking_table.insert( std::pair<std::string,unsigned>( symbol, reloc ));
						break;

					default:
						//LOGD( "Expected '%s' R_ARM_JUMP_SLOT, found 0x%X", it->name.c_str(), type );
						break;
				}
			}
		}

		if( !found_plt_rel ) {

			//LOGD( "Failed to hooking : not found sym offset" );
		}
	}

	return ret;
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

#include <fcntl.h>
int isValidPtr(const void*p, int len) {
	if (!p) {
		return 0;
	}
	int ret = 1;
	int nullfd = open("/dev/random", O_WRONLY);
	if (nullfd == 0)
		return 0;

	if (write(nullfd, p, len) < 0) {
		ret = 0;
	}
	close(nullfd);
	return ret;
}

Elf32_Sym * hooking::soinfo_elf_lookup(struct soinfo *si, unsigned hash, const char *name)
{
	Elf32_Sym *symtab = si->symtab;
    const char *strtab = si->strtab;
    unsigned n;

    for( n = si->bucket[hash % si->nbucket]; n != 0; n = si->chain[n] ) {
        Elf32_Sym *s = symtab + n;
        const char * p_str = strtab + s->st_name;

        //if( isValidPtr( p_str, 20 ))
        {
			if( strcmp( p_str, name) == 0 ) {
				return s;
			}
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


unsigned hooking::getOriginalAddr( const char * func_name )
{
	std::map<std::string,unsigned>::iterator it = hooking_table.find( func_name );
	if( it != hooking_table.end()) return it->second;

	return 0;
}
