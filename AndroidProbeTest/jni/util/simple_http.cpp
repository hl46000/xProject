/*
 * probehttp.cpp
 *
 *  Created on: 2015. 7. 17.
 *      Author: purehero2
 */

#include "simple_http.h"

simple_http::simple_http()
{
	m_global_init 	= CURLE_FAILED_INIT;
	m_ctx			= NULL;
}

simple_http::~simple_http()
{
	release();
}

CURLcode simple_http::init()
{
	m_global_init = curl_global_init( CURL_GLOBAL_ALL );
	if( m_global_init != CURLE_OK ) return m_global_init;

	m_ctx = curl_easy_init();
	if( m_ctx == NULL )
	{
		curl_global_cleanup();
		return CURLE_FAILED_INIT;
	}

	return CURLE_OK;
}

void simple_http::release()
{
	if( m_ctx != NULL )
	{
		curl_easy_cleanup( m_ctx ) ;
	}
	m_ctx = NULL;

	if( m_global_init == CURLE_OK )
	{
		curl_global_cleanup();
	}
	m_global_init = CURLE_FAILED_INIT;
}

void simple_http::get_error_msg( std::string & err_msg, const CURLcode code )
{
	err_msg.clear();
	const char * msg = curl_easy_strerror( code );
	err_msg.assign( msg, msg + strlen( msg ));
}

static size_t WriteCallback(void *contents, size_t size, size_t nmemb, void *userp)
{
    ((std::string*)userp)->append((char*)contents, size * nmemb);
    return size * nmemb;
}

#define EASY_SETOPT(X)		{code=X;if(X!=CURLE_OK)return code;}
CURLcode simple_http::send_request( const std::string & url, SimpleHttpResponse & response, ReqeustType type )
{
	// context 객체를 설정한다.
	// 긁어올 url을 명시하고, url이 URL정보임을 알려준다.
	CURLcode code = curl_easy_setopt( m_ctx, CURLOPT_URL, url.c_str() ) ;
	if( code != CURLE_OK ) return code;

	EASY_SETOPT( curl_easy_setopt( m_ctx, CURLOPT_WRITEFUNCTION, WriteCallback));
	EASY_SETOPT( curl_easy_setopt( m_ctx, CURLOPT_WRITEDATA, &response.res_body));
	EASY_SETOPT( curl_easy_setopt( m_ctx, CURLOPT_FOLLOWLOCATION, 1L));
	EASY_SETOPT( curl_easy_setopt( m_ctx, CURLOPT_NOSIGNAL, 1));
	EASY_SETOPT( curl_easy_setopt( m_ctx, CURLOPT_ACCEPT_ENCODING, "deflate" ));
	EASY_SETOPT( curl_easy_setopt ( m_ctx, CURLOPT_TIMEOUT, 3));
	EASY_SETOPT( curl_easy_setopt ( m_ctx, CURLOPT_SSL_VERIFYPEER, 0 ));
	EASY_SETOPT( curl_easy_setopt ( m_ctx, CURLOPT_SSL_VERIFYHOST, 0 ));
	EASY_SETOPT( curl_easy_setopt ( m_ctx, CURLOPT_SSLVERSION,3)); // SSL 버젼 (https 접속시에 필요)
	EASY_SETOPT( curl_easy_setopt ( m_ctx, CURLOPT_HEADER, 0)); // 헤더 출력 여부
	//EASY_SETOPT( curl_easy_setopt ( m_ctx, CURLOPT_TRANSFER, 1)); // 결과값을 받을것인지
	EASY_SETOPT( curl_easy_setopt ( m_ctx, CURLOPT_REFERER, ""));

	// 웹페이지를 긁어온다.
	return curl_easy_perform( m_ctx );
}

size_t upload_read_data (char *bufptr, size_t size, size_t nitems, void *userp)
{
	size_t read;
	read = fread(bufptr, size, nitems, (FILE*)userp);
	return read;
}

CURLcode simple_http::upload_request( const std::string & url, SimpleHttpResponse & response, const std::string & upload_filename, ReqeustType type )
{
	CURLcode code = curl_easy_setopt( m_ctx, CURLOPT_URL, url.c_str() ) ;
	if( code != CURLE_OK ) return code;

	struct curl_httppost *formpost=NULL;
	struct curl_httppost *lastptr=NULL;

	curl_formadd( &formpost, &lastptr, CURLFORM_COPYNAME, "upload", CURLFORM_FILE, upload_filename.c_str(), CURLFORM_FILENAME, "upload_file", CURLFORM_END );

	EASY_SETOPT( curl_easy_setopt( m_ctx, CURLOPT_WRITEFUNCTION, WriteCallback));
	EASY_SETOPT( curl_easy_setopt( m_ctx, CURLOPT_WRITEDATA, &response.res_body));
	EASY_SETOPT( curl_easy_setopt ( m_ctx, CURLOPT_POST, 1L));
	EASY_SETOPT( curl_easy_setopt ( m_ctx, CURLOPT_HTTPPOST, formpost ));

	//fseek( fp, 0, SEEK_END );
	//long size = ftell( fp );
	//fseek( fp, 0, SEEK_SET );

	//EASY_SETOPT( curl_easy_setopt ( m_ctx, CURLOPT_INFILESIZE_LARGE, (curl_off_t)size ));
	EASY_SETOPT( curl_easy_setopt ( m_ctx, CURLOPT_FOLLOWLOCATION, true )); // if any redirection after upload


	// 웹페이지를 긁어온다.
	CURLcode ret = curl_easy_perform( m_ctx );

	curl_easy_getinfo( m_ctx, CURLINFO_SPEED_UPLOAD, &response.speed_upload );
	curl_easy_getinfo( m_ctx, CURLINFO_TOTAL_TIME, &response.total_time );

	curl_formfree(formpost);

	return ret;
}
