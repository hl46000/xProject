/*
 * lua.h
 *
 *  Created on: 2015. 7. 7.
 *      Author: purehero2
 */

#ifndef PROBE_LUA_H_
#define PROBE_LUA_H_

extern "C" {
	#include <lua/lua.h>
	#include <lua/lauxlib.h>
    #include <lua/lualib.h>
}

#include <stdio.h>

class probe_lua {
public:
	probe_lua();
	virtual ~probe_lua();

	void init();
	void release();

	void excute( const char * script, int len );
protected :
	lua_State *L;

	void regist_custom_api();
};

#endif /* PROBE_LUA_H_ */
