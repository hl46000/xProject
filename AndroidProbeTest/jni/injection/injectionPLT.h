/*
 * injectionPLT.h
 *
 *  Created on: 2015. 9. 15.
 *      Author: purehero2
 */

#ifndef INJECTION_INJECTIONPLT_H_
#define INJECTION_INJECTIONPLT_H_

#include <string>
#include <vector>

#include "util/linker.h"
#include "util/maps_reader.h"

/*
* ELF 구조의 plt 에 등록된 주소가 maps 파일에 표시된 주소 영역내에 존재 하는지를 테스트
*/
class injectionPLT {
public:
	injectionPLT();
	virtual ~injectionPLT();

	int init( void * param );
	virtual void release();

	void set_log_fp( const char * _log_filename );

	int get_target_module( int pid );		// maps 파일 중 같은 파일의 주소 영역을 계산하여 로딩 합니다.
	void working();		// 실제 파일을 dlopen 하여 plt 에 등록된 주소가 maps 의 주소 범위에 있는지 확인 합니다.

protected :
	const char * search_name_of_addr( unsigned addr );	// addr 을 포함하고 있는 모듈의 이름을 반환한다.
	void print_log( const char *fmt, ... );

	std::vector<maps_info>	m_maps_infos;
	void * init_param;
	FILE * log_fp;
	std::string log_filename;

	void print_soinfo( soinfo *si );
	void print_phdr( soinfo *si );
	void print_symtab( soinfo *si );
	void print_plt_rel( soinfo *si );

	void just_do_it( soinfo *si, void * param );
};

#endif /* INJECTION_INJECTIONPLT_H_ */
