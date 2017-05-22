// Unzipper.cpp: implementation of the CUnzipper class.
//
//////////////////////////////////////////////////////////////////////

#ifdef _WIN32
#include "stdafx.h"
#include "Unzipper.h"

#include "zlib\unzip.h"
#include "zlib\iowin32.h"

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

#else
#include "Unzipper.h"
#include "minizip/unzip.h"
#include "minizip/ioapi.h"
#endif

//#include "../jni_helper.h"

#include <string>

#ifdef MEMORY_LEAK	// 메모리릭 감지
#define new new(__FILE__, __LINE__)
#endif

const std::string SplitFullPathName( const std::string & _str );

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

const UINT BUFFERSIZE = 2048;

CUnzipper::CUnzipper(LPCTSTR szFilePath) : m_uzFile(0)
{
	OpenZip(szFilePath);
}

CUnzipper::~CUnzipper()
{
	//LOGT();

	CloseZip();
}

bool CUnzipper::CloseZip()
{
	//LOGT();

	unzCloseCurrentFile(m_uzFile);

	int nRet = unzClose(m_uzFile);
	m_uzFile = NULL;

	return (nRet == UNZ_OK);
}

// extended interface
bool CUnzipper::OpenZip(LPCTSTR szFilePath)
{
	//LOGT();
	//LOGD( "szFilePath = '%s", szFilePath );

	CloseZip();

	if (!szFilePath || !lstrlen(szFilePath))
		return false;

	// convert szFilePath to fully qualified path 
	char szFullPath[MAX_PATH];
	
#ifdef _WIN32
	if (!GetFullPathName(szFilePath, MAX_PATH, szFullPath, NULL))
		return false;
#else
	//const std::string path = SplitFullPathName( szFilePath );
	strcpy( szFullPath, szFilePath );
#endif
	
	//LOGD( "szFullPath = '%s", szFullPath );

	m_uzFile = unzOpen(szFullPath);
	return (m_uzFile != NULL);
}

int CUnzipper::GetFileCount()
{
	if (!m_uzFile)
		return 0;

	unz_global_info info;

	if (unzGetGlobalInfo(m_uzFile, &info) == UNZ_OK)
	{
		return (int)info.number_entry;
	}

	return 0;
}

bool CUnzipper::GetFileInfo(int nFile, UZ_FileInfo& info)
{
	if (!m_uzFile)
		return FALSE;

	if (!GotoFile(nFile))
		return FALSE;

	return GetFileInfo(info);
}

bool CUnzipper::GotoFirstFile(LPCTSTR szExt)
{
	if (!m_uzFile)
		return FALSE;

	if (!szExt || !lstrlen(szExt))
		return (unzGoToFirstFile(m_uzFile) == UNZ_OK);

	// else
#if 0
	if (unzGoToFirstFile(m_uzFile) == UNZ_OK)
	{
		UZ_FileInfo info;

		if (!GetFileInfo(info))
			return FALSE;

		// test extension
		char szFExt[_MAX_EXT];
		_splitpath(info.szFileName, NULL, NULL, NULL, szFExt);

		if (szFExt)
		{
			if (lstrcmpi(szExt, szFExt + 1) == 0)
				return TRUE;
		}

		return GotoNextFile(szExt);
	}
#endif

	return FALSE;
}

bool CUnzipper::GotoNextFile(LPCTSTR szExt)
{
	if (!m_uzFile)
		return FALSE;

	if (!szExt || !lstrlen(szExt))
		return (unzGoToNextFile(m_uzFile) == UNZ_OK);

	// else
	UZ_FileInfo info;
#if 0
	while (unzGoToNextFile(m_uzFile) == UNZ_OK)
	{
		if (!GetFileInfo(info))
			return FALSE;

		// test extension
		char szFExt[_MAX_EXT];
		_splitpath(info.szFileName, NULL, NULL, NULL, szFExt);

		if (szFExt)
		{
			if (lstrcmpi(szExt, szFExt + 1) == 0)
				return TRUE;
		}
	}
#endif
	return FALSE;

}

bool CUnzipper::OpenCurrentFile()
{
	if (!m_uzFile)
	{
		return false;
	}

	if (unzOpenCurrentFile(m_uzFile) != UNZ_OK)
	{
		return false;
	}

	return true;
}

bool CUnzipper::CloseCurrentFile()
{
	if (!m_uzFile)
	{
		return false;
	}

	unzCloseCurrentFile(m_uzFile);
	return true;
}

int  CUnzipper::ReadCurrentFile( char * buffer, int bufferLen )
{
	return (int) unzReadCurrentFile( m_uzFile, buffer, bufferLen );
}


#include <stdlib.h>
bool CUnzipper::GetFileInfo(UZ_FileInfo& info)
{
	if (!m_uzFile)
		return FALSE;

	unz_file_info uzfi;

	ZeroMemory(&info, sizeof(info));
	ZeroMemory(&uzfi, sizeof(uzfi));

	if (UNZ_OK != unzGetCurrentFileInfo(m_uzFile, &uzfi, info.szFileName, MAX_PATH, NULL, 0, info.szComment, MAX_COMMENT))
		return FALSE;

	// copy across
	info.dwVersion = uzfi.version;	
	info.dwVersionNeeded = uzfi.version_needed;
	info.dwFlags = uzfi.flag;	
	info.dwCompressionMethod = uzfi.compression_method; 
	info.dwDosDate = uzfi.dosDate;  
	info.dwCRC = uzfi.crc;	 
	info.dwCompressedSize = uzfi.compressed_size; 
	info.dwUncompressedSize = uzfi.uncompressed_size;
	info.dwInternalAttrib = uzfi.internal_fa; 
	info.dwExternalAttrib = uzfi.external_fa; 

	// replace filename forward slashes with backslashes
	int nLen = lstrlen(info.szFileName);

	while (nLen--)
	{
		if (info.szFileName[nLen] == '/')
			info.szFileName[nLen] = '\\';
	}

	// is it a folder?
	info.bFolder = ((info.dwExternalAttrib & FILE_ATTRIBUTE_DIRECTORY) == FILE_ATTRIBUTE_DIRECTORY);

	return TRUE;
}


bool CUnzipper::GotoFile(int nFile)
{
	if (!m_uzFile)
		return FALSE;

	if (nFile < 0 || nFile >= GetFileCount())
		return FALSE;

	GotoFirstFile();

	while (nFile--)
	{
		if (!GotoNextFile())
			return FALSE;
	}

	return TRUE;
}

bool CUnzipper::GotoFile(LPCTSTR szFileName, bool bIgnoreFilePath)
{
	if (!m_uzFile)
		return FALSE;

	// try the simple approach
	if (unzLocateFile(m_uzFile, szFileName, 2) == UNZ_OK)
		return TRUE;

	else if (bIgnoreFilePath)
	{ 
		// brute force way
		if (unzGoToFirstFile(m_uzFile) != UNZ_OK)
			return FALSE;

		UZ_FileInfo info;

		do
		{
			if (!GetFileInfo(info))
				return FALSE;

#ifdef _WIN32
			// test name
			char szFName[_MAX_FNAME], szName[_MAX_FNAME], szExt[_MAX_EXT];

			_splitpath(info.szFileName, NULL, NULL, szName, szExt);
			_makepath(szFName, NULL, NULL, szName, szExt);

			if (lstrcmpi(szFileName, szFName) == 0)
				return TRUE;
#else
			if (strcmp( info.szFileName, szFileName) == 0)
			{
				return TRUE;
			}
#endif
		}
		while (unzGoToNextFile(m_uzFile) == UNZ_OK);
	}

	// else
	return FALSE;
}

const std::string SplitFullPathName( const std::string & _str )
{
	std::string str = _str;

	size_t found;
	found = str.find_last_of("/\\");
	if( found > 0 )
	{
		return std::string( str.substr(0,found));
	}

	return "";
}
