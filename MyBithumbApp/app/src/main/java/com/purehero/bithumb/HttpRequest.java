package com.purehero.bithumb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by MY on 2017-08-25.
 */

public class HttpRequest {
    public static String get( String url ) {
        InputStream in = null;
        ByteArrayOutputStream baos = null;

        try {
            URLConnection urlConnection = new URL( url ).openConnection();
            in = urlConnection.getInputStream();

            baos = new ByteArrayOutputStream();
            IOUtils.copyInputStreamToOutputStream(in, baos);

            byte byteArray [] = baos.toByteArray();
            return new String( byteArray, 0, byteArray.length );

        } catch( Exception e ) {
            e.printStackTrace();

        } finally {
            if( in != null ) {
                try { in.close(); } catch ( IOException e ){}
                in = null;
            }

            if( baos != null ) {
                try { baos.close(); } catch ( IOException e ){}
                baos = null;
            }
        }
        return null;
    }
}
