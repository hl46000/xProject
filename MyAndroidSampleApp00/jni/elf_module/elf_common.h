#if !defined (__ELF_COMMON_H__)
#define __ELF_COMMON_H__

#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>
//#include "elf_.h"
#include <linux/elf.h>

/* ARM relocs.  */
#define R_ARM_NONE              0       /* No reloc */
#define R_ARM_PC24              1       /* PC relative 26 bit branch */
#define R_ARM_ABS32             2       /* Direct 32 bit  */
#define R_ARM_REL32             3       /* PC relative 32 bit */
#define R_ARM_PC13              4
#define R_ARM_ABS16             5       /* Direct 16 bit */
#define R_ARM_ABS12             6       /* Direct 12 bit */
#define R_ARM_THM_ABS5          7
#define R_ARM_ABS8              8       /* Direct 8 bit */
#define R_ARM_SBREL32           9
#define R_ARM_THM_CALL          10
#define R_ARM_THM_PC8           11
#define R_ARM_AMP_VCALL9        12
#define R_ARM_SWI24             13
#define R_ARM_THM_SWI8          14
#define R_ARM_XPC25             15
#define R_ARM_THM_XPC22         16
#define R_ARM_COPY              20      /* Copy symbol at runtime */
#define R_ARM_GLOB_DAT          21      /* Create GOT entry */
#define R_ARM_JUMP_SLOT         22      /* Create PLT entry */
#define R_ARM_RELATIVE          23      /* Adjust by program base */
#define R_ARM_GOTOFF32          24      /* 32 bit offset to GOT */
#define R_ARM_BASE_PREL         25      /* 32 bit PC relative offset to GOT */
#define R_ARM_GOT_BREL          26      /* 32 bit GOT entry */
#define R_ARM_PLT32             27      /* 32 bit PLT address */
#define R_ARM_CALL              28
#define R_ARM_JUMP24            29
#define R_ARM_THM_JUMP24        30
#define R_ARM_V4BX              40
#define R_ARM_PREL31            42
#define R_ARM_MOVW_ABS_NC       43
#define R_ARM_MOVT_ABS          44
#define R_ARM_THM_MOVW_ABS_NC   47
#define R_ARM_THM_MOVT_ABS      48
#define R_ARM_GNU_VTENTRY       100
#define R_ARM_GNU_VTINHERIT     101
#define R_ARM_THM_PC11          102     /* thumb unconditional branch */
#define R_ARM_THM_PC9           103     /* thumb conditional branch */
#define R_ARM_RXPC25            249
#define R_ARM_RSBREL32          250
#define R_ARM_THM_RPC22         251
#define R_ARM_RREL32            252
#define R_ARM_RABS22            253
#define R_ARM_RPC24             254
#define R_ARM_RBASE             255
/* Keep this the last entry.  */
#define R_ARM_NUM               256

/* TMS320C67xx specific declarations */
/* XXX: no ELF standard yet */

/* TMS320C67xx relocs. */
#define R_C60_32       1
#define R_C60_GOT32     3               /* 32 bit GOT entry */
#define R_C60_PLT32     4               /* 32 bit PLT address */
#define R_C60_COPY      5               /* Copy symbol at runtime */
#define R_C60_GLOB_DAT  6               /* Create GOT entry */
#define R_C60_JMP_SLOT  7               /* Create PLT entry */
#define R_C60_RELATIVE  8               /* Adjust by program base */
#define R_C60_GOTOFF    9               /* 32 bit offset to GOT */
#define R_C60_GOTPC     10              /* 32 bit PC relative offset to GOT */

#define R_C60HI16      0x55       // high 16 bit MVKH embedded
#define R_C60LO16      0x54       // low 16 bit MVKL embedded


#if (ELFHOOK_STANDALONE)

#define log_info(...)   do{ fprintf(stdout, __VA_ARGS__); } while(0)
#define log_error(...)  do{ fprintf(stdout, __VA_ARGS__); } while(0)
#define log_warn(...)   do{ fprintf(stdout, __VA_ARGS__); } while(0)
#define log_fatal(...)  do{ fprintf(stdout, __VA_ARGS__); } while(0)

#if 1
#define log_dbg(...)    do{ } while(0)
#else
#define log_dbg(...)    do{ fprintf(stdout, __VA_ARGS__); } while(0)
#endif

#else

#define sTag ("ELFKooH")
#define log_info(...)   do{ __android_log_print(ANDROID_LOG_INFO,   sTag,  __VA_ARGS__); }while(0)
#define log_error(...)  do{ __android_log_print(ANDROID_LOG_ERROR,  sTag,  __VA_ARGS__); }while(0)
#define log_warn(...)   do{ __android_log_print(ANDROID_LOG_WARN,   sTag,  __VA_ARGS__); }while(0)
#define log_dbg(...)    do{ __android_log_print(ANDROID_LOG_DEBUG,  sTag,  __VA_ARGS__); }while(0)
#define log_fatal(...)  do{ __android_log_print(ANDROID_LOG_FATAL,  sTag,  __VA_ARGS__); }while(0)

#endif


#if defined(__LP64__)
#define ElfW(type) Elf64_ ## type
static inline ElfW(Word) elf_r_sym(ElfW(Xword) info) { return ELF64_R_SYM(info); }
static inline ElfW(Xword) elf_r_type(ElfW(Xword) info) { return ELF64_R_TYPE(info); }
#else
#define ElfW(type) Elf32_ ## type
static inline ElfW(Word) elf_r_sym(ElfW(Word) info) { return ELF32_R_SYM(info); }
static inline ElfW(Word) elf_r_type(ElfW(Word) info) { return ELF32_R_TYPE(info); }
#endif

#if defined(__arm__)
#define R_GENERIC_JUMP_SLOT R_ARM_JUMP_SLOT
#define R_GENERIC_GLOB_DAT  R_ARM_GLOB_DAT
#define R_GENERIC_RELATIVE  R_ARM_RELATIVE
#define R_GENERIC_IRELATIVE R_ARM_IRELATIVE
#define R_GENERIC_ABS       R_ARM_ABS32
#elif defined(__aarch64__)
#define R_GENERIC_JUMP_SLOT R_AARCH64_JUMP_SLOT
#define R_GENERIC_GLOB_DAT  R_AARCH64_GLOB_DAT
#define R_GENERIC_RELATIVE  R_AARCH64_RELATIVE
#define R_GENERIC_IRELATIVE R_AARCH64_IRELATIVE
#define R_GENERIC_ABS       R_AARCH64_ABS64
#endif




#define powerof2(x)     ((((x)-1)&(x))==0)
#define SOINFO_NAME_LEN (128)

inline static int GetTargetElfMachine()
{
#if defined(__arm__)
    return EM_ARM;
#elif defined(__aarch64__)
    return EM_AARCH64;
#elif defined(__i386__)
    return EM_386;
#elif defined(__mips__)
    return EM_MIPS;
#elif defined(__x86_64__)
    return EM_X86_64;
#endif
}

#endif
