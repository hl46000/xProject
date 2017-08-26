package com.purehero.bithumb.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by MY on 2017-08-25.
 */

public class IOUtils {
    public static void copyInputStreamToOutputStream(InputStream in, String msgTag ) throws IOException {
        InputStreamReader isr = new InputStreamReader( in );
        BufferedReader br = new BufferedReader( isr );

        String line;
        while(( line = br.readLine()) != null ) {
            Log.d( msgTag, line );
        }
    }

    public static void copyInputStreamToOutputStream(InputStream in, OutputStream os ) throws IOException {
        byte buffer[] = new byte[ 10240 ];

        int nBytes = 0;
        while(( nBytes = in.read( buffer, 0, 10240 )) > 0 ) {
            os.write( buffer, 0, nBytes );
        }
    }
}
