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

	unsigned long hook( unsigned long original_function_addr, unsigned long target_function_addr );

protected :
	std::string m_path;
	FILE *		m_pFP;

	ElfW(Ehdr)  m_ehdr;
	ElfW(Shdr)  * m_shdr;

	unsigned long m_base_address;
	char * m_pShstrtab;
};

#endif /* HOOKING_ELF_FILE_H_ */
