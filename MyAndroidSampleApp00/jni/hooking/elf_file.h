/*
 * elf_file.h
 *
 *  Created on: 2017. 2. 17.
 *      Author: purehero
 */

#ifndef HOOKING_ELF_FILE_H_
#define HOOKING_ELF_FILE_H_

#include <string>
#include "elf_header.h"
#include "elf_common.h"

class elf_file {
public:
	elf_file( unsigned long addr, std::string path );
	virtual ~elf_file();

	void * hook( void * original_function, void * target_function );

protected :
	std::string m_path;
	FILE *		m_pFP;

	ElfW(Ehdr)  m_elf_header;
	ElfW(Shdr)  * m_section_header;
	ElfW(Phdr)  * m_program_header;

	unsigned long m_base_address;
	char * m_pShstrtab;

	int  get_mem_access(unsigned long addr, uint32_t* pprot);
	int  set_mem_access(unsigned long addr, int prots);
	bool replace_address( void * addr, void * replace_func );
};

#endif /* HOOKING_ELF_FILE_H_ */
