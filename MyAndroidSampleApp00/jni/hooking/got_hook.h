/*
 * got_hook.h
 *
 *  Created on: 2017. 2. 22.
 *      Author: MY
 */

#ifndef HOOKING_GOT_HOOK_H_
#define HOOKING_GOT_HOOK_H_

#include "elf_file.h"

class got_hook {
public:
	got_hook();
	virtual ~got_hook();

	void * hooking( const char * fname, void * original_function, void * target_function );

protected :
	void * try_to_got_hooking( const char * fname, void * original_function, void * target_function );
	void * try_to_plt_hooking( const char * fname, void * original_function, void * target_function );
};

#endif /* HOOKING_GOT_HOOK_H_ */
