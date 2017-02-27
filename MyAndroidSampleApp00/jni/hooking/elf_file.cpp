/*
 * elf_file.cpp
 *
 *  Created on: 2017. 2. 17.
 *      Author: purehero
 */

#include "elf_file.h"
#include "alog.h"

#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/syscall.h>

#define PAGE_START(addr) (~(getpagesize() - 1) & (addr))
#define PAGE_END(addr)   PAGE_START((addr) + (PAGE_SIZE-1))

#define MAYBE_MAP_FLAG(x, from, to)  (((x) & (from)) ? (to) : 0)
#define PFLAGS_TO_PROT(x)            (MAYBE_MAP_FLAG((x), PF_X, PROT_EXEC) | \
                                      MAYBE_MAP_FLAG((x), PF_R, PROT_READ) | \
                                      MAYBE_MAP_FLAG((x), PF_W, PROT_WRITE))


int is_little_endian()
{
 int a = 0x01234567;

 printf("%x %x\n",((char*)&a),*((char*)&a));
 printf("%x %x\n",((char*)&a+1),*((char*)&a+1));
 printf("%x %x\n",((char*)&a+2),*((char*)&a+2));
 printf("%x %x\n",((char*)&a+3),*((char*)&a+3));

 if ( *((char*)&a) == 0x67 )
 {
  return 1; // little endian
 }

 return 0; //big endian

}

elf_file::elf_file( unsigned long addr, std::string path )
{
	m_base_address 	= addr;
	m_path			= path;
	m_pFP			= fopen( m_path.c_str(), "r" );

	fseek( m_pFP, 0, SEEK_END );
	long size = ftell( m_pFP );
	//LOGD( "%s file size : %d", path.c_str(), size );

	fseek( m_pFP, 0, SEEK_SET );
	fread( &m_elf_header, sizeof(ElfW(Ehdr)), 1, m_pFP );

	//LOGD( "EI_CLASS : %d", m_elf_header.e_ident[EI_CLASS] );
	//LOGD( "EI_DATA : %d", m_elf_header.e_ident[EI_DATA] );

	//LOGD( "is_little_endian : %d", is_little_endian() );
	//LOGD( "section header num : %d", m_elf_header.e_shnum );

	m_section_header = ( ElfW(Shdr) * ) malloc( sizeof(ElfW(Shdr)) * m_elf_header.e_shnum );
	fseek( m_pFP, m_elf_header.e_shoff, SEEK_SET );
	fread( m_section_header, sizeof(ElfW(Shdr)), m_elf_header.e_shnum, m_pFP );

	m_program_header = ( ElfW(Phdr) * ) malloc( sizeof(ElfW(Phdr)) * m_elf_header.e_phnum );
	fseek( m_pFP, m_elf_header.e_phoff, SEEK_SET );
	fread( m_program_header, sizeof(ElfW(Phdr)), m_elf_header.e_phnum, m_pFP );

	ElfW(Shdr) * section_header = &m_section_header[m_elf_header.e_shstrndx];
	//LOGD( "shstr offset : %d, size : %d", section_header->sh_offset, section_header->sh_size );

	m_pShstrtab = ( char * ) malloc( section_header->sh_size );
	fseek( m_pFP, section_header->sh_offset, SEEK_SET );
	fread( m_pShstrtab, section_header->sh_size, 1, m_pFP );
}

elf_file::~elf_file()
{
	LOGT();

	if( m_section_header != NULL ) {
		free( m_section_header );
	}
	m_section_header = NULL;

	if( m_program_header != NULL ) {
		free( m_program_header );
	}
	m_program_header = NULL;

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

void * elf_file::hook( void * original_function, void * target_function ) {
	LOGT();

	//pid_t pid = getpid();
	//PtraceAttach( pid );

	ElfW(Shdr) * got_section = NULL;

	int len = m_elf_header.e_shnum;
	for( int i = 0; i < len; ++i ) {
		const char * section_name = (const char *)( m_pShstrtab + m_section_header[i].sh_name );

		if (strcmp( section_name, ".got") == 0) {
			//LOGD( "section name : %s", section_name );

			unsigned long got_section_address = m_base_address + m_section_header[i].sh_addr;
			size_t  got_section_size	= m_section_header[i].sh_size;

			// LOGD( "got_section_size : %u", got_section_size );

			unsigned long got_entry = 0;
			for( int i = 0; i < got_section_size; i += sizeof(int)) {
				//got_entry = ptrace(PTRACE_PEEKDATA, pid, (void *)(got_section_address + i), NULL);
				got_entry = (unsigned long)*((unsigned long*)(got_section_address + i));

				//LOGD( "0x%08x, 0x%08x", got_entry, original_function );
				if( got_entry == original_function ) {
					LOGD( "found got entry : 0x%08x", got_entry );

					if( replace_address( (void*)(got_section_address + i), target_function )) {
						LOGD( "replace success" );
					} else {
						LOGD( "replace failed" );
					}

					return original_function;
				}
			}
		}
	}

	//PtraceDetach( pid );
	LOGD( "no found" );
	return NULL;
}

int elf_file::set_mem_access( unsigned long addr, int prots)
{
	LOGT();
    void *page_start_addr = (void *)PAGE_START(addr);
    return mprotect(page_start_addr, getpagesize(), prots);
}

int elf_file::get_mem_access( unsigned long addr, uint32_t* pprot)
{
	LOGT();
    int result = -1;

    const ElfW(Phdr)* phdr_table = m_program_header;
    const ElfW(Phdr)* phdr_end = phdr_table + m_elf_header.e_phnum;

    for (const ElfW(Phdr)* phdr = phdr_table; phdr < phdr_end; phdr++)
    {
        if (phdr->p_type == PT_LOAD)
        {
            ElfW(Addr) seg_start = m_base_address + phdr->p_vaddr;
            ElfW(Addr) seg_end   = seg_start + phdr->p_memsz;

            ElfW(Addr) seg_page_start = PAGE_START(seg_start);
            ElfW(Addr) seg_page_end   = PAGE_END(seg_end);

            if (addr >= seg_page_start && addr < seg_page_end)
            {
                *pprot = PFLAGS_TO_PROT(phdr->p_flags),
                result = 0;

                break;
            }
        }
    }
    return result;
}

int clear_cache(void* addr, size_t len)
{
	LOGT();
	cacheflush((long) addr, (long) addr + len, 0 );
	usleep( 10 );

	void *end = (uint8_t *)addr + len;
    return syscall(0xf0002, addr, end);
}

bool elf_file::replace_address( void * addr, void * replace_func )
{
	LOGT();
	uint32_t old_prots = PROT_READ;

	int result = get_mem_access( addr, &old_prots );
	if ( result != 0 ) {
		LOGD("[-] read mem access fails, error %s.\n", strerror(errno));
		return false;
	}

	uint32_t prots = old_prots | PROT_WRITE | PROT_EXEC;
	if ((prots & PROT_WRITE) != 0) { // make sure we're never simultaneously writable / executable
		prots &= ~PROT_EXEC;
	}

	if(set_mem_access( addr, prots)) {
		LOGD("[-] modify mem access fails, error %s.\n", strerror(errno));
		return false;
	}

	*(void **)addr = replace_func;

	if(set_mem_access( addr, old_prots)) {
		LOGD("[-] modify mem access fails, error %s.\n", strerror(errno));
		return false;
	}

	clear_cache( *(void **)addr, getpagesize());
	usleep( 100 );

	return true;
}
