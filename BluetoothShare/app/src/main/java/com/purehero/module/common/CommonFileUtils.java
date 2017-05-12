package com.purehero.module.common;

import java.io.File;

/**
 * Created by MY on 2017-05-12.
 */

public class CommonFileUtils {
    public static final String DATE_FORMAT = "MM/dd/yy H:mm a";

    /**
     * 파일의 크기를 문자열로 반환한다.
     *
     * @param file
     * @return
     */
    public static String getFilesize(File file) {
        String result = "0 B";

        float size = file.length();
        if( size < 1024.0f ) {
            result = String.format( "%d B", (int)size );
        } else {
            size /= 1024.0f;
            if( size < 1024.0f ) {
                result = String.format( "%.2f KB", size );
            } else {
                size /= 1024.0f;
                if( size < 1024.0f ) {
                    result = String.format( "%.2f MB", size );
                } else {
                    size /= 1024.0f;
                    result = String.format( "%.2f GB", size );
                }
            }
        }

        return result;
    }

    public static String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }
}
