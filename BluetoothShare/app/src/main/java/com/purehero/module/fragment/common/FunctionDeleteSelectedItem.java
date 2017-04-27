package com.purehero.module.fragment.common;

import android.app.Activity;
import android.content.DialogInterface;

import com.purehero.bluetooth.share.R;
import com.purehero.module.common.CancelableProgressDialog;
import com.purehero.module.common.DialogUtils;
import com.purehero.module.common.OnSuccessListener;
import com.purehero.module.common.ProgressRunnable;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by purehero on 2017-03-28.
 */

public class FunctionDeleteSelectedItem {
    final Activity context;
    final List<File> targetFiles;
    final OnSuccessListener successListener;

    public FunctionDeleteSelectedItem(Activity context, List<File> targetFiles, OnSuccessListener successListener) {
        this.context = context;
        this.targetFiles = targetFiles;
        this.successListener = successListener;
    }

    public void run() {
        final int item_count = targetFiles.size();
        String message = String.format( "%d %s\n\n", item_count, context.getString( R.string.delete_message ));

        DialogUtils.no_string_res     = R.string.cancel;
        DialogUtils.yes_string_res    = R.string.delete;
        DialogUtils.confirmDialog( context, R.string.delete, message, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == DialogUtils.DIALOG_BUTTON_ID_YES ) {       // Clicked Delete
                    DialogUtils.progressDialog( context, R.string.delete, "", new ProgressRunnable(){
                        @Override
                        public void run( final CancelableProgressDialog dialog) {

                            dialog.setMax( item_count );

                            int progress_count = 0;
                            for( final File data : targetFiles ) {
                                context.runOnUiThread( new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.setMessage(data.getName());
                                    }
                                });

                                try {
                                    FileUtils.forceDelete( data );
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                dialog.setProgress( ++progress_count );
                            }

                            if( successListener != null ) {
                                successListener.OnSuccess();
                            }
                        }
                    });
                }
            }
        });


        DialogUtils.no_string_res     = -1;
        DialogUtils.yes_string_res    = -1;
    }
}
