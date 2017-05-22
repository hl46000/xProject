/*
 * probehttp.h
 *
 *  Created on: 2015. 7. 17.
 *      Author: purehero2
 */

#ifndef SIMPLE_HTTP_H_
#define SIMPLE_HTTP_H_

extern "C" {
	#include <curl/curl.h>
}

#include <string>
typedef enum
{
	REQUEST_GET = 0 ,
	REQUEST_POST = 1
} ReqeustType;

typedef struct _response_data_
{
	std::string res_body;
	double speed_upload, total_time;
} SimpleHttpResponse;

class simple_http {
public:
	simple_http();
	virtual ~simple_http();

	CURLcode init();
	void release();

	void get_error_msg( std::string & err_msg, const CURLcode code );

	CURLcode send_request  ( const std::string & url, SimpleHttpResponse & response, ReqeustType type );
	CURLcode upload_request( const std::string & url, SimpleHttpResponse & response, const std::string & upload_filename, ReqeustType type );

protected :
	CURLcode	m_global_init;
	CURL * 		m_ctx;
};

#endif /* PROBE_HTTP_H_ */
