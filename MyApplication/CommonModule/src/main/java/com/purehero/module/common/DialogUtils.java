package com.purehero.module.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.R.attr.data;

/**
 * Created by purehero on 2017-03-24.
 */

public class DialogUtils {
    public static void FileDeleteDialog( final Activity context, final List<File> files ) {
        final String digTitle = context.getString( R.string.dialog_title_delete );

        AlertDialog.Builder dlg = new AlertDialog.Builder( context );
        dlg.setTitle( digTitle );
        if( files.size() > 1 ) {
            dlg.setMessage(String.format("'%s'\n\n%d %s", files.size(), context.getString(R.string.dialog_item_delete_confirm)));
        } else {
            dlg.setMessage(String.format("'%s'\n\n'%s' %s", files.get(0).getName(), context.getString(R.string.dialog_delete_confirm)));
        }

        //.setIcon(R.drawable.delete)
        dlg.setPositiveButton( R.string.dialog_button_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                new progressDialogTask( context, digTitle, "", ProgressDialog.STYLE_HORIZONTAL, new ProgressRunnable(){
                    @Override
                    public void run( final CancelableProgressDialog dialog) {
                        dialog.setMax( files.size());

                        int progress_count = 0;
                        for( final File deleteFile : files ) {
                            context.runOnUiThread( new Runnable() {
                                @Override
                                public void run() {
                                    dialog.setMessage( deleteFile.getName() );
                                }
                            });
                            try {
                                if( deleteFile.isDirectory()) {
                                    FileUtils.deleteDirectory( deleteFile );
                                } else {
                                    FileUtils.forceDelete( deleteFile );
                                }
                            } catch( Exception e ) {
                                e.printStackTrace();
                            }

                            dialog.setProgress( ++progress_count );
                        }
                    }
                }).execute();
                dialog.dismiss();
            }
        });
        dlg.setNegativeButton( R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dlg.create().show();
    }

    private static class progressDialogTask extends AsyncTask<Void, Void, Void> {
        final CancelableProgressDialog asyncDialog;
        final ProgressRunnable runnable;

        public progressDialogTask(Activity activity, String title, String message, int dialogType, ProgressRunnable runnable ) {
            asyncDialog 	= new CancelableProgressDialog( activity );
            this.runnable	= runnable;

            asyncDialog.setProgressStyle(dialogType);
            if( title != null ) asyncDialog.setTitle( title );
            if( message != null ) asyncDialog.setMessage( message );
        }

        @Override
        protected void onPreExecute() {
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            asyncDialog.setCancel( false );
            runnable.run( asyncDialog );
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }
    }
}
