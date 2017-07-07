package com.purehero.module.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.purehero.bluetooth.share.G;
import com.purehero.bluetooth.share.MainActivity;

import java.io.File;

/**
 * Created by purehero on 2017-03-24.
 */

public class FileIntentUtils {

    public static String FileShareProviderName = null;

    /**
     * File 을 공유 시킨다.
     *
     * @param file
     */
    public static Intent Sharing(Context context, File file ) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && FileShareProviderName != null ) {
            shareIntent.setDataAndType( FileProvider.getUriForFile(context, FileShareProviderName, file), getMimeType( file.getName() ));
        } else {
            shareIntent.setDataAndType( Uri.fromFile( file ), getMimeType( file.getName() ));
        }

        return shareIntent;
    }

    /**
     * File 을 실행 시킨다.
     *
     * @param file
     */
    public static Intent Running( Context context, File file ) {
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        myIntent.addCategory(Intent.CATEGORY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //myIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && FileShareProviderName != null ) {
            myIntent.setDataAndType( FileProvider.getUriForFile(context, FileShareProviderName, file), getMimeType( file.getName() ));
            //myIntent.setData( FileProvider.getUriForFile(context, FileShareProviderName, file));
        } else {
            myIntent.setDataAndType( Uri.fromFile( file ), getMimeType( file.getName() ));
            //myIntent.setData( Uri.fromFile( file ));
        }

        return myIntent;
    }

    /**
     * File 을 실행 시킨다.
     *
     * @param file
     */
    public static Intent Running( Context context, Uri fileUri ) {
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        myIntent.addCategory(Intent.CATEGORY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            myIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myIntent.setDataAndType( fileUri, context.getContentResolver().getType( fileUri ) );

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
