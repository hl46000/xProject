package com.purehero.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Stack;

/**
 * Created by MY on 2017-03-04.
 */

public class FileUtils {
    public static void copyFileOrDirectory(File srcFile, File destFolder ) throws IOException {
        Stack<File> folders = new Stack<File>();
        do {
            if( srcFile.isDirectory()) {

            } else {
                copyFile( srcFile, destFolder );
            }
        } while( !folders.empty());
    }

    public static void copyFile(File srcFile, File destFolder) throws IOException {
        if (!destFolder.exists()) {
            destFolder.mkdirs();
        }

        File destFile = new File(destFolder, srcFile.getName());
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel src = null;
        FileChannel dest = null;

        try {
            src = new FileInputStream(srcFile).getChannel();
            dest = new FileOutputStream(destFile).getChannel();
            dest.transferFrom(src, 0, src.size());

        } catch (IOException e) {
            throw e;

        } finally {
            if (src != null) {
                try {
                    src.close();
                } catch (Exception e) {
                }
            }
            if (dest != null) {
                try {
                    dest.close();
                } catch (Exception e) {
                }
            }
        }
    }
}
