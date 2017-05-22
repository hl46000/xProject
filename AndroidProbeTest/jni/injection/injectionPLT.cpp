/*
 * injectionPLT.cpp
 *
 *  Created on: 2015. 9. 15.
 *      Author: purehero2
 */

#include <injection/injectionPLT.h>

#include <dlfcn.h>
#include <errno.h>
#include <stdlib.h>
#include <stdarg.h>

#include <map>

#include "util/util.h"
#include "util/log.h"
#include "util/simple_http.h"

injectionPLT::injectionPLT()
{
	init_param 	= NULL;
	log_fp		= NULL;
}
injectionPLT::~injectionPLT()
{
	if( log_fp != NULL ) {
		fclose( log_fp );
	}
}

void injectionPLT::set_log_fp( const char * _log_filename )
{
	if( log_fp != NULL ) {
		fclose( log_fp );
	}

	log_filename = _log_filename;
	log_fp = fopen( log_filename.c_str(), "wb" );
}

int injectionPLT::init( void * param )
{
	init_param = param;
	log_fp		= NULL;

	return 0;
}

void injectionPLT::release()
{
	if( log_fp != NULL ) {
		fclose( log_fp );
	}
	log_fp = NULL;
#if 0
	std::string guid;
	GuidGenerator(( JNIEnv * ) init_param, guid );

	std::string url  = "http://192.168.100.99/maps_info.php";
	url += "?fname=" + guid;

	simple_http http;
	http.init();

	SimpleHttpResponse response;
	http.upload_request( url, response, log_filename.c_str(), REQUEST_POST );
	http.release();

	LOGE( "Speed: %.3f bytes/sec during %.3f seconds\n", response.speed_upload, response.total_time);
	LOGE( "RESPONSE : %s", response.res_body.c_str());

	remove( log_filename.c_str() );
#endif
}

int injectionPLT::get_target_module( int pid )
{
	m_maps_infos.clear();

	char strTemp[1024];
	sprintf( strTemp, "/proc/%d/maps", pid );

	maps_reader reader( pid );
	int result = reader.read();

	LOGD( "maps read cnt : %d", result );
	if( result < 1 ) return -1;

	m_maps_infos.clear();

	std::map<std::string,maps_info> maps_infos;

	const std::vector<maps_info> & infos = reader.get_maps_infos();
	for( std::vector<maps_info>::const_iterator it = infos.begin(); it != infos.end(); it++ ) {
		if( strlen( it->filename ) < 3 ) continue;

		//print_log( "xxxx %08llx-%08llx", it->startAddr, it->endAddr );

		if( maps_infos.empty()) {
			if( strstr( it->permission, "r" ) ) {
				maps_infos.insert( std::pair<std::string,maps_info>( it->filename, * it ));
			}
		} else {
			std::map<std::string,maps_info>::iterator find_it = maps_infos.find( it->filename );
			if( find_it == maps_infos.end()) {
				if( strstr( it->permission, "r" ) ) {
					maps_infos.insert( std::pair<std::string,maps_info>( it->filename, * it ));
				}
			} else {
				if( find_it->second.startAddr > it->startAddr ) find_it->second.startAddr = it->startAddr;
				if( find_it->second.endAddr   < it->endAddr   ) find_it->second.endAddr   = it->endAddr;
			}
		}
	}

	if( maps_infos.size() > 0 ) {
		for( std::map<std::string,maps_info>::iterator it = maps_infos.begin(); it != maps_infos.end(); it++ ) {
			m_maps_infos.push_back( it->second );
		}
	}

	return (int)m_maps_infos.size();
}

#define UNKNOW_NAME		"UNKNOW NAME"
const char * injectionPLT::search_name_of_addr( unsigned addr )
{
	for( std::vector<maps_info>::iterator it = m_maps_infos.begin(); it != m_maps_infos.end(); it++ ) {
		if( it->startAddr > addr ) continue;
		if( it->endAddr   < addr ) continue;

		return it->filename;
	}

	return UNKNOW_NAME;
}

void injectionPLT::print_soinfo( soinfo *si )
{
	char strBuff[1024], strTemp[1024];
	sprintf( strBuff, "soinfo,%s", si->name );

	sprintf( strTemp, ",%u", (unsigned)si->phdr ); strcat( strBuff, strTemp );
	//LOGD( "\tphdr %u", (unsigned)si->phdr );

	sprintf( strTemp, ",%u", si->phnum ); strcat( strBuff, strTemp );
	//LOGD( "\tphnum %u", si->phnum );

	sprintf( strTemp, ",%u", si->entry ); strcat( strBuff, strTemp );
	//LOGD( "\tentry %u", si->entry );

	sprintf( strTemp, ",%u", si->base ); strcat( strBuff, strTemp );
	//LOGD( "\tbase %u", si->base );

	sprintf( strTemp, ",%u", si->size ); strcat( strBuff, strTemp );
	//LOGD( "\tsize %u", si->size );

	sprintf( strTemp, ",%u", si->ba_index ); strcat( strBuff, strTemp );
	//LOGD( "\tba_index %u", si->ba_index );

	sprintf( strTemp, ",%u", (unsigned)si->dynamic ); strcat( strBuff, strTemp );
	//LOGD( "\tdynamic %u", (unsigned)si->dynamic );

	sprintf( strTemp, ",%u", (unsigned)si->wrprotect_start ); strcat( strBuff, strTemp );
	//LOGD( "\twrprotect_start %u", si->wrprotect_start );

	sprintf( strTemp, ",%u", (unsigned)si->wrprotect_end ); strcat( strBuff, strTemp );
	//LOGD( "\twrprotect_end %u", si->wrprotect_end );

	sprintf( strTemp, ",%u", (unsigned)si->flags ); strcat( strBuff, strTemp );
	//LOGD( "\tflags %u", si->flags );

	sprintf( strTemp, ",%u", (unsigned)si->strtab ); strcat( strBuff, strTemp );
	//LOGD( "\tstrtab %u", (unsigned)si->strtab );

	sprintf( strTemp, ",%u", (unsigned)si->symtab ); strcat( strBuff, strTemp );
	//LOGD( "\tsymtab %u", (unsigned)si->symtab );

	sprintf( strTemp, ",%u", (unsigned)si->nbucket ); strcat( strBuff, strTemp );
	//LOGD( "\tnbucket %u", si->nbucket );

	sprintf( strTemp, ",%u", (unsigned)si->nchain ); strcat( strBuff, strTemp );
	//LOGD( "\tnchain %u", si->nchain );

	sprintf( strTemp, ",%u", (unsigned)si->bucket ); strcat( strBuff, strTemp );
	//LOGD( "\tbucket %u", (unsigned)si->bucket );

	sprintf( strTemp, ",%u", (unsigned)si->chain ); strcat( strBuff, strTemp );
	//LOGD( "\tchain %u", (unsigned) si->chain );

	sprintf( strTemp, ",%u", (unsigned)si->plt_got ); strcat( strBuff, strTemp );
	//LOGD( "\tplt_got %u", (unsigned)si->plt_got );

	sprintf( strTemp, ",%u", (unsigned)si->plt_rel ); strcat( strBuff, strTemp );
	//LOGD( "\tplt_rel %u", (unsigned)si->plt_rel );

	sprintf( strTemp, ",%u", (unsigned)si->plt_rel_count ); strcat( strBuff, strTemp );
	//LOGD( "\tplt_rel_count %u", si->plt_rel_count );

	sprintf( strTemp, ",%u", (unsigned)si->rel ); strcat( strBuff, strTemp );
	//LOGD( "\trel %u", (unsigned)si->rel );

	sprintf( strTemp, ",%u", (unsigned)si->rel_count ); strcat( strBuff, strTemp );
	//LOGD( "\trel_count %u", si->rel_count );
#ifdef ANDROID_SH_LINKER
	sprintf( strTemp, ",%u", (unsigned)si->plt_rela ); strcat( strBuff, strTemp );
	//LOGD( "\tplt_rela %u", si->plt_rela );

	sprintf( strTemp, ",%u", (unsigned)si->plt_rela_count ); strcat( strBuff, strTemp );
	//LOGD( "\tplt_rela_count %u", si->plt_rela_count );

	sprintf( strTemp, ",%u", (unsigned)si->rela ); strcat( strBuff, strTemp );
	//LOGD( "\trela %u", si->rela );

	sprintf( strTemp, ",%u", (unsigned)si->rela_count ); strcat( strBuff, strTemp );
	//LOGD( "\trela_count %u", si->rela_count );
#endif

	sprintf( strTemp, ",%u", (unsigned)si->preinit_array ); strcat( strBuff, strTemp );
	//LOGD( "\tpreinit_array %u", (unsigned) si->preinit_array );

	sprintf( strTemp, ",%u", (unsigned)si->preinit_array_count ); strcat( strBuff, strTemp );
	//LOGD( "\tpreinit_array_count %u", si->preinit_array_count );

	sprintf( strTemp, ",%u", (unsigned)si->init_array ); strcat( strBuff, strTemp );
	//LOGD( "\tinit_array %u", (unsigned)si->init_array );

	sprintf( strTemp, ",%u", (unsigned)si->init_array_count ); strcat( strBuff, strTemp );
	//LOGD( "\tinit_array_count %u", si->init_array_count );

	sprintf( strTemp, ",%u", (unsigned)si->fini_array ); strcat( strBuff, strTemp );
	//LOGD( "\tfini_array %u", (unsigned)si->fini_array );

	sprintf( strTemp, ",%u", (unsigned)si->fini_array_count ); strcat( strBuff, strTemp );
	//LOGD( "\tfini_array_count %u", si->fini_array_count );

	sprintf( strTemp, ",%u", (unsigned)si->refcount ); strcat( strBuff, strTemp );
	//LOGD( "\trefcount %u", si->refcount );

	print_log( "%s", strBuff );
}

void injectionPLT::print_phdr( soinfo *si )
{
	char strBuff[1024];
	for( size_t i = 0; i < si->phnum; ++i ) {
		Elf32_Phdr *phdr = &si->phdr[i];

		print_log( "ph,%d,%d,%d,%d,%d,%d,%d,%d", phdr->p_type, phdr->p_offset, phdr->p_vaddr, phdr->p_paddr, phdr->p_filesz, phdr->p_memsz, phdr->p_flags, phdr->p_align );
	}
}

void injectionPLT::print_symtab( soinfo *si )
{
	const char *strtab = si->strtab;
	Elf32_Sym *symtab = si->symtab;

	//char strBuff[1024];
	for( size_t i = 0; i < si->nchain; ++i ) {
		Elf32_Sym *s = &si->symtab[i];
		if( s->st_shndx == SHN_UNDEF ) continue;

		const char * p_str = strtab + s->st_name;
		print_log( "sym,%s,%d,%d,%d,%d,%d,%d", p_str, s->st_name, s->st_value, s->st_size, s->st_info, s->st_other, s->st_shndx  );
	}
}

/*
* 외부 심볼에 대한 정보를 출력 합니다.
*/
void injectionPLT::print_plt_rel( soinfo *si )
{
	const char *strtab = si->strtab;
	Elf32_Sym *symtab = si->symtab;

	//char strBuff[1024];

	Elf32_Rel * rel = si->plt_rel;
	for( size_t i = 0; i < si->plt_rel_count; ++i, ++rel ) {
		unsigned reloc = (unsigned)(rel->r_offset + si->base);

		unsigned val = *(unsigned *) reloc;
		const char * searched_reloc_name = search_name_of_addr( reloc );
		const char * searched_val_name   = search_name_of_addr( val );

		unsigned sym = ELF32_R_SYM(rel->r_info);
		Elf32_Sym *s = symtab + sym;
		const char * p_str = strtab + s->st_name;

		print_log( "plt_rel,%s:%s,%u,%u reloc[%s],%s", si->name, p_str, rel->r_info, rel->r_offset, searched_reloc_name, searched_val_name );
	}
}

void injectionPLT::print_log( const char *fmt, ... )
{
	char buf[1024] = {0,};
	va_list ap;

	va_start( ap, fmt );
	vsprintf( buf, fmt, ap );
	va_end(ap);

	LOGD( "%s", buf );

	if( log_fp != NULL ) {
		fwrite( buf, strlen( buf ), 1, log_fp ); fwrite( "\r\n", 2, 1, log_fp );
	}
}

void injectionPLT::working()
{
	soinfo *si = NULL;
	for( std::vector<maps_info>::iterator it = m_maps_infos.begin(); it != m_maps_infos.end(); it++ ) {
		//if( *((char*)( it->s_add + 1)) != 'E' ) continue;
		print_log( "\r\nTrying to '%s' file %08llx-%08llx", it->filename, it->startAddr, it->endAddr );

		if(( si = (struct soinfo *)dlopen( it->filename, 0 /* RTLD_NOLOAD */ )) == NULL) {
			print_log( "'%s' dlopen error: %s.", it->filename, dlerror() );
			continue;
		}

		just_do_it( si, NULL );

		dlclose( si );
		si = NULL;
	}
}

void injectionPLT::just_do_it( soinfo *si, void * param )
{
	print_log( "just_do_it '%s'", si->name );

	print_soinfo( si);
	print_phdr( si );
	print_symtab( si );
	print_plt_rel( si );

	if( log_fp != NULL ) {
		fflush( log_fp );
	}
}
