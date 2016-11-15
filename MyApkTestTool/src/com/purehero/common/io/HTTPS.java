package com.purehero.common.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

public class HTTPS {
	
	/** 
	  *  
	  * @param urlString 
	  * @throws IOException 
	  * @throws NoSuchAlgorithmException 
	  * @throws KeyManagementException 
	  */  
	 public static List<String> getHttps(String urlString) 
			 throws IOException, NoSuchAlgorithmException, KeyManagementException, FileNotFoundException {  
		 // Get HTTPS URL connection  
		 URL url = new URL(urlString);    
		 HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();  
	    
		 // Set Hostname verification  
		 conn.setHostnameVerifier(new HostnameVerifier() {  
			 @Override
			 public boolean verify(String arg0, SSLSession arg1) {
				 return false;
			 }  
		 });  
	    
		 // SSL setting  
		  SSLContext context = SSLContext.getInstance("TLS");  
		  context.init(null, null, null);  // No validation for now  
		  conn.setSSLSocketFactory(context.getSocketFactory());  
		    
		  // Connect to host  
		  conn.connect();  
		  conn.setInstanceFollowRedirects(true);  
		  
		  List<String> ret = new ArrayList<String>();
		  
		  int result_code 	= conn.getResponseCode();
		  String result_msg = conn.getResponseMessage();
		  if( result_code > 300 ) {
			ret.add( String.valueOf( result_code )) ;
			ret.add( result_msg );
			
		  } else {
			  // Print response from host  
			  InputStream in = conn.getInputStream();  
			  BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));  
			  
			  String line;		  
			  while ((line = reader.readLine()) != null) {  
				  ret.add( line );  
			  }  
			 
			  reader.close();
		  }
		  
		  return ret;
	}  
	 
	 /*
	  
	private void httpsTest() {
		try {
			List<String> result = HTTPS.getHttps( "https://play.google.com/store/apps/details?id=com.mogloogames.zb12" );
			
			String prefix_title = "<div class=\"id-app-title\" tabindex=\"0\">";
			for( String line : result ) {
				int s_idx = line.indexOf( prefix_title );
				if( s_idx != -1 ) {
					int e_idx = line.indexOf( "</div>", s_idx );
					
					System.out.println( line.substring( s_idx + prefix_title.length(), e_idx ) );
				}
			}
		} catch ( Exception e) {
			e.printStackTrace();
		}
	}
	  
	  
	  
	  */
}
