// Zipper.h: interface for the CZipper class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_TYPE_H__4249275D_B50B_4AAE_8715_B706D1CA0F2F__INCLUDED_)
#define AFX_TYPE_H__4249275D_B50B_4AAE_8715_B706D1CA0F2F__INCLUDED_

#if !defined(_WIN32)

#ifndef DEF_DWORD
#define DEF_DWORD
typedef unsigned long DWORD;
#endif

#ifndef DEF_UINT
#define DEF_UINT
typedef unsigned int UINT;
#endif

#ifndef DEF_LPCTSTR
#define DEF_LPCTSTR
typedef const char * LPCTSTR;
#endif

#ifndef DEF_LPTSTR
#define DEF_LPTSTR
typedef char * LPTSTR;
#endif


//////////////////////////////////////////////////////////////////////////////////////////////
#ifndef MAX_PATH
#define MAX_PATH	260
#endif

#ifndef _MAX_FNAME
#define _MAX_FNAME	128
#endif

#ifndef _MAX_EXT
#define _MAX_EXT	16
#endif

#ifndef _MAX_DRIVE
#define _MAX_DRIVE	3
#endif

#ifndef INVALID_HANDLE_VALUE
#define INVALID_HANDLE_VALUE	-1
#endif

#ifndef FILE_ATTRIBUTE_DIRECTORY
#define FILE_ATTRIBUTE_DIRECTORY 0x00000010
#endif

#ifndef FALSE
#define FALSE false
#endif

#ifndef TRUE
#define TRUE true
#endif

///////////////////////////////////////////////////////////////////////////////////////////////
#define lstrlen				strlen
#define lstrcpy 			strcpy
#define ZeroMemory(x,y)		memset(x,0,y)
#define lstrcmpi			strcmp


#endif // !defined(_WIN32)

#endif // !defined(AFX_TYPE_H__4249275D_B50B_4AAE_8715_B706D1CA0F2F__INCLUDED_)
