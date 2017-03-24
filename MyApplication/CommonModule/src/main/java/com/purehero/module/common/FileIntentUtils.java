package com.purehero.module.common;

import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * Created by purehero on 2017-03-24.
 */

public class FileIntentUtils {

    /**
     * File 을 공유 시킨다.
     *
     * @param file
     */
    public static Intent Sharing( File file ) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setDataAndType( Uri.fromFile( file ), getMimeType( file.getName() ));
        return shareIntent;
    }

    /**
     * File 을 실행 시킨다.
     *
     * @param file
     */
    public static Intent Running( File file ) {
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        myIntent.addCategory(Intent.CATEGORY_DEFAULT);
        myIntent.setDataAndType( Uri.fromFile( file ), getMimeType( file.getName() ));
        return myIntent;
    }

    private static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
