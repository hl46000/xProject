/*
 * elf_file.cpp
 *
 *  Created on: 2017. 2. 17.
 *      Author: purehero
 */

#include "elf_file.h"
#include "alog.h"

elf_file::elf_file( unsigned long addr, std::string path )
{
	m_base_address 	= addr;
	m_path			= path;
	m_pFP			= fopen( m_path.c_str(), "r" );

	fseek( m_pFP, 0, SEEK_SET );
	fread( &m_ehdr, sizeof(ElfW(Ehdr)), 1, m_pFP );

	m_shdr = ( ElfW(Shdr) * ) malloc( sizeof(ElfW(Shdr)) * m_ehdr.e_shnum );
	fseek( m_pFP, m_ehdr.e_shoff, SEEK_SET );
	fread( m_shdr, sizeof(ElfW(Shdr)), m_ehdr.e_shnum, m_pFP );

	ElfW(Shdr) section_header;
	LOGD( "m_ehdr->e_shstrndx : %d", m_ehdr.e_shstrndx );
	fseek( m_pFP, m_ehdr.e_shoff + m_ehdr.e_shstrndx * sizeof(ElfW(Shdr)), SEEK_SET );
	fread( &section_header, sizeof(ElfW(Ehdr)), 1, m_pFP );

	LOGD( "shstr offset : %ul, size : %d", section_header.sh_offset, section_header.sh_size );

	m_pShstrtab = ( char * ) malloc( section_header.sh_size );
	fseek( m_pFP, section_header.sh_offset, SEEK_SET );
	fread( m_pShstrtab, section_header.sh_size, 1, m_pFP );
}

elf_file::~elf_file()
{
	if( m_shdr != NULL ) {
		free( m_shdr );
	}
	m_shdr = NULL;

	if( m_pShstrtab != NULL ) {
		free( m_pShstrtab );
	}
	m_pShstrtab = NULL;

	if( m_pFP != NULL ) {
		fclose( m_pFP );
	}
	m_pFP = NULL;
}

#include <sys/ptrace.h>
#include <sys/wait.h>
#include <unistd.h>
int PtraceAttach(pid_t pid)
{
	if (ptrace(PTRACE_ATTACH, pid, NULL, NULL) < 0)
	{
		perror(NULL);
		return -1;
	}
	waitpid(pid, NULL, WUNTRACED);
	LOGD("Attached to process %d\n", pid);

	return 0;
}

int PtraceDetach(pid_t pid)
{
	if (pid == -1) {
		return -1;
	}

	if (ptrace(PTRACE_DETACH, pid, NULL, NULL) < 0) {
		perror(NULL);
		return -1;
	}

    LOGD("Detached from process %d\n", pid);
    return 0;
}

unsigned long elf_file::hook( unsigned long original_function_addr, unsigned long target_function_addr ) {
	LOGT();

	//pid_t pid = getpid();
	//PtraceAttach( pid );

	ElfW(Shdr) * got_section = NULL;

	int len = m_ehdr.e_shnum;
	for( int i = 0; i < len; ++i ) {
		const char * section_name = (const char *)( m_pShstrtab + m_shdr[i].sh_name );

		if (strcmp( section_name, ".got") == 0) {
			LOGD( "section name : %s", section_name );

			unsigned long got_section_address = m_base_address + m_shdr[i].sh_addr;
			size_t  got_section_size	= m_shdr[i].sh_size;

			LOGD( "got_section_size : %u", got_section_size );

			unsigned long got_entry = 0;
			for( int i = 0; i < got_section_size; i += sizeof(long)) {
				//got_entry = ptrace(PTRACE_PEEKDATA, pid, (void *)(got_section_address + i), NULL);
				got_entry = (unsigned long)*((unsigned long*)(got_section_address + i));

				LOGD( "%ul, %ul", got_entry, original_function_addr );
				if( got_entry == original_function_addr ) {

					LOGD( "found" );
					return original_function_addr;
				}
			}
		}
	}

	//PtraceDetach( pid );
	LOGD( "no found" );
	return 0;
}
