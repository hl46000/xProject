/*
 * hooking.cpp
 *
 *  Created on: 2015. 9. 11.
 *      Author: purehero2
 */

#include <errno.h>
#include <stdlib.h>
#include <sys/mman.h>

#include <vector>
#include <map>

#include "util/linker.h"
#include "alog.h"
#include "plt_hooking.h"


plt_hooking::plt_hooking() {}
plt_hooking::~plt_hooking() {}

Elf32_Sym *soinfo_elf_lookup(struct soinfo *si, unsigned hash, const char *name);

int plt_hooking::get_target_module( int pid )
{
	LOGT();

	m_maps_info.clear();

	char buffer[256] = {0};
	sprintf( buffer, "/proc/%d/maps", pid );

	FILE *fp = fopen( buffer, "rt" );
	if( fp == NULL ){
		perror("fopen");
		return -1;
	}

	char * permission 		= &buffer[0];
	char * unknown2			= &buffer[10];
	int * unknown3			= (int *)&buffer[20];
	char *filename			= &buffer[30];
	uintptr_t * unknown1	= (uintptr_t *)&buffer[200];

	HOOKING_MAPS_INFO module;
	while( fgets( buffer, sizeof(buffer), fp ) ) {
		//LOGI( "%s", buffer );
		sscanf( buffer, "%08lu-%08lu %s %08lu %s %d %s", &module.s_add, &module.e_add,(char*)permission, unknown1, unknown2, unknown3, filename);

		if( filename[0] == '[') {
			//LOGI("%s : skip", filename);
			continue;
		}
		//if( strstr( filename, ".so") == NULL ) continue;

		if( permission[0] != 'r' ) continue;
		if( permission[2] != 'x' ) continue;
		if( module.s_add == 0 ) continue;

		unsigned char* tmpChar = (unsigned char *) module.s_add;
		if(tmpChar[18] != 40 && tmpChar[18] != 183 )
		{
			//LOGI("'%s' is not ARM/ARM64 architectures",info->filename);
			continue;
		}

		//if( strstr( filename, "libhooking.so") != NULL ) continue;

		//if( strstr( filename, "libusc.so") != NULL ) continue;
		//if( strstr( filename, "libc.so") != NULL ) continue;

		//if( strstr( filename, "libart.so") == NULL ) continue;

		//if( strstr( filename, "libc.so") == NULL && strstr( filename, "libstdc++.so") == NULL ) continue;


		module.name  = filename;

		m_maps_info.push_back( module );
	}

	fclose(fp);
	return (int) m_maps_info.size();
}


const char * plt_hooking::find_name( uintptr_t addr )
{
	for( std::vector<HOOKING_MAPS_INFO>::iterator it = m_maps_info.begin(); it != m_maps_info.end(); it++ ) {
		if( it->s_add >= addr && it->e_add >= addr ) {
			return it->name.c_str();
		}
	}
	return "";
}

unsigned plt_hooking::try_hooking( const char *symbol, unsigned newval, bool bLog )
{
	LOGT();

	unsigned ret = 0;
	if ( !symbol || !newval ) {
		LOGE( "if ( !symbol || !newval)" );
		return ret;
	}

	unsigned symbol_hash = elfhash(symbol);
	for( std::vector<HOOKING_MAPS_INFO>::iterator it = m_maps_info.begin(); it != m_maps_info.end(); it++ ) {
		usleep( 10 );

		if( bLog ) LOGD( "Trying to hooking '%s' file in function '%s'", it->name.c_str(), symbol );

		if( *((char*)( it->s_add + 1)) != 'E' ) {
			if( bLog ) LOGE( "Not ELF file : %s", it->name.c_str() );
			continue;
		}

		// since we know the module is already loaded and mostly
		// we DO NOT want its constructors to be called again,
		// ise RTLD_NOLOAD to just get its soinfo address.
		soinfo * si = (struct soinfo *) dlopen( it->name.c_str(), RTLD_GLOBAL | RTLD_NOW );
		//soinfo * si = (struct soinfo *) dlopen( it->name.c_str(), 0 );
		if( si == NULL ) {
			if( bLog ) LOGE( "Trying to hooking '%s' file : dlopen error: %s", it->name.c_str(), dlerror() );
			continue;
		}

		//LOGD( "soinfo name : %s", si->name );

		if( si->nbucket == 0)
		{
			if( bLog ) LOGD( "Failed to hooking : nbucket is 0" );
			dlclose( si );

			continue;
		}

		Elf32_Sym * s = soinfo_elf_lookup(si, symbol_hash, symbol);
		if ( s == NULL )
		{
			if( bLog ) LOGE( "Failed to hooking '%s:%s' function (soinfo_elf_lookup)", it->name.c_str(), symbol );
			dlclose( si );
			continue;
		}

		unsigned int sym_offset = s - si->symtab;

		if( si->plt_rel_count == 0 ) {
			if( bLog ) LOGD( "Failed to hooking : plt_rel_count is 0" );
			dlclose( si );

			continue;
		}

		bool found_plt_rel = false;

		size_t i;
		Elf32_Rel *rel = NULL;
		for( i = 0, rel = si->plt_rel; !found_plt_rel && i < si->plt_rel_count; ++i, ++rel ) {
			unsigned type  = ELF32_R_TYPE(rel->r_info);
			unsigned sym   = ELF32_R_SYM(rel->r_info);
			unsigned reloc = (unsigned)(rel->r_offset + si->base);

			//if( bLog ) LOGD( "%d ==> sym_offset[%x], sym[%x]", (int)i, sym_offset, sym );
			if( sym_offset == sym ) {
				switch(type) {
					case R_ARM_JUMP_SLOT:
						ret = libhook_patch_address( reloc, newval );
						LOGD( "Success hooking '%s:%s' %x to %x", it->name.c_str(), symbol, ret, newval );
						found_plt_rel = true;
						break;

					default:
						if( bLog ) LOGD( "Expected '%s' R_ARM_JUMP_SLOT, found 0x%X", it->name.c_str(), type );
						break;
				}
			}
		}

		if( !found_plt_rel ) {
			if( bLog ) LOGD( "Failed to hooking : not found sym offset" );
		}

		dlclose( si );
	}

	return ret;
}

unsigned plt_hooking::elfhash( const char *_name )
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

Elf32_Sym * soinfo_elf_lookup(struct soinfo *si, unsigned hash, const char *name)
{
	Elf32_Sym *symtab = si->symtab;
    const char *strtab = si->strtab;
    unsigned n = si->bucket[hash % si->nbucket];

    //for( n = si->bucket[hash % si->nbucket]; n != 0 && si->nchain > n; n = si->chain[n] ) {
    for( ; n != 0 && si->nchain > n; n = si->chain[n] ) {
    	Elf32_Sym *s = symtab + n;
        const char * p_str = strtab + s->st_name;
        if( strcmp( p_str, name) == 0 ) {
			return s;
		}
	}

    return NULL;
}



unsigned plt_hooking::libhook_patch_address( unsigned addr, unsigned newval )
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

