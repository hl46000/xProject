// Unzipper.h: interface for the CUnzipper class (c) daniel godson 2002
//
// CUnzipper is a simple c++ wrapper for the 'unzip' file extraction
// api written by Gilles Vollant (c) 2002, which in turn makes use of 
// 'zlib' written by Jean-loup Gailly and Mark Adler (c) 1995-2002.
//
// This software is provided 'as-is', without any express or implied
// warranty.  In no event will the authors be held liable for any damages
// arising from the use of this software.
//
// Permission is granted to anyone to use this software for any purpose,
// including commercial applications, and to alter it and redistribute it
// freely without restriction.
//
// Notwithstanding this, you are still bound by the conditions imposed
// by the original authors of 'unzip' and 'zlib'
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_UNZIPPER_H__EBC42716_31C7_4659_8EF3_9BF8D4409709__INCLUDED_)
#define AFX_UNZIPPER_H__EBC42716_31C7_4659_8EF3_9BF8D4409709__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "Type.h"
#include <stdio.h>

const UINT MAX_COMMENT = 255;

// create our own fileinfo struct to hide the underlying implementation
struct UZ_FileInfo
{
	char szFileName[MAX_PATH + 1];
	char szComment[MAX_COMMENT + 1];
	
	DWORD dwVersion;  
	DWORD dwVersionNeeded;
	DWORD dwFlags;	 
	DWORD dwCompressionMethod; 
	DWORD dwDosDate;	
	DWORD dwCRC;   
	DWORD dwCompressedSize; 
	DWORD dwUncompressedSize;
	DWORD dwInternalAttrib; 
	DWORD dwExternalAttrib; 
	bool bFolder;
};

class CUnzipper  
{
public:
	CUnzipper(LPCTSTR szFilePath = NULL);
	virtual ~CUnzipper();
	
	// extended interface
	bool OpenZip(LPCTSTR szFilePath);
	bool CloseZip(); // for multiple reuse

	// unzip by file index
	int GetFileCount();
	bool GetFileInfo(int nFile, UZ_FileInfo& info);

	// unzip current file
	bool GotoFirstFile(LPCTSTR szExt = NULL);
	bool GotoNextFile(LPCTSTR szExt = NULL);
	bool GetFileInfo(UZ_FileInfo& info);

	bool OpenCurrentFile();
	bool CloseCurrentFile();
	int  ReadCurrentFile( char * buffer, int bufferLen );

	// helpers
	bool GotoFile(LPCTSTR szFileName, bool bIgnoreFilePath = TRUE);
	bool GotoFile(int nFile);
	
protected:
	void* m_uzFile;
};

#endif // !defined(AFX_UNZIPPER_H__EBC42716_31C7_4659_8EF3_9BF8D4409709__INCLUDED_)
