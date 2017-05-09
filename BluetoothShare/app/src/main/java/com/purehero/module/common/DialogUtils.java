package com.purehero.module.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.method.ScrollingMovementMethod;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.R.attr.data;

/**
 * Created by purehero on 2017-03-24.
 */

public class DialogUtils {
    public static final int DIALOG_BUTTON_ID_YES 	= -1;
    public static final int DIALOG_BUTTON_ID_NO 	= -2;

    /**
     * 확인 용 Dialog 을 띄워준다.
     *
     * @param
     * @param
     * @param
     * @param res_icon
     * @param listener
     */
    public static void confirmDialog( final Activity activity, int title_res_id, int message_res_id, final int res_icon, final DialogInterface.OnClickListener listener ){
        confirmDialog( activity, title_res_id, activity.getString( message_res_id ), res_icon, listener );
    }

    public static void confirmDialog( final Activity activity, int title_res_id, String message, final int res_icon, final DialogInterface.OnClickListener listener ){
        confirmDialog( activity, activity.getString( title_res_id ), message, res_icon, listener );
    }

    public static void confirmDialog( final Activity activity, final String title, final String message, final int res_icon, final DialogInterface.OnClickListener listener ){
        activity.runOnUiThread( new Runnable(){
            @Override
            public void run() {
                AlertDialog.Builder alt_bld = new AlertDialog.Builder( activity );
                if( no_string_res == -1 || yes_string_res == -1 ) {
                    alt_bld.setMessage( message ).setCancelable( false )
                            .setNegativeButton( "NO", listener )		// -2
                            .setPositiveButton( "YES", listener );	// -1
                } else {
                    alt_bld.setMessage( message ).setCancelable( false )
                            .setNegativeButton( no_string_res, listener )		// -2
                            .setPositiveButton( yes_string_res, listener );	// -1
                }

                AlertDialog alert = alt_bld.create();
                // Title for AlertDialog
                if( title != null ) {
                    alert.setTitle( title );
                }
                // Icon for AlertDialog
                if( res_icon > 0 ) {
                    alert.setIcon( res_icon );
                }
                alert.show();
            }});
    }



    /**
     * @param activity
     * @param title
     * @param message
     * @param runnable
     */
    public static boolean progressDialogCanceled = false;
    public static void progressDialog( final Activity activity, int title_res_id, final String message, final ProgressRunnable runnable ) {
        String title = activity.getString( title_res_id );
        progressDialog( activity, title, message, runnable );
    }
    public static void progressDialog( final Activity activity, final String title, final String message, final ProgressRunnable runnable ) {
        new progressDialogTask( activity, title, message, ProgressDialog.STYLE_HORIZONTAL, runnable ).execute();
    }

    private static class progressDialogTask extends AsyncTask<Void, Void, Void> {
        final CancelableProgressDialog asyncDialog;
        final ProgressRunnable runnable;

        public progressDialogTask( Activity activity, String title, String message, int dialogType, ProgressRunnable runnable ) {
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
            progressDialogCanceled = false;
            runnable.run( asyncDialog );
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }
    }



    /**
     * 한줄 입력을 받는 Dialog 을 띄운다.
     *
     * @param activity
     * @param title
     * @param message
     * @param res_icon
     * @param listener
     * @return
     */
    private static EditText __input = null;
    public static void TextInputDialog( final Activity activity, final int title_res, final int message_res, String hint, final int res_icon, final DialogInterface.OnClickListener listener ) {
        TextInputDialog( activity, activity.getString(title_res), activity.getString(message_res), hint, res_icon, listener );
    }

    public static void TextInputDialog( final Activity activity, final String title, final String message, String hint, final int res_icon, final DialogInterface.OnClickListener listener ) {
        final LinearLayout layout = new LinearLayout( activity );

        __input = new EditText( activity );
        //__input.setHint( hint );
        __input.setText( hint );
        __input.selectAll();
        __input.setMaxLines(1);
        __input.setHorizontallyScrolling(true);
        __input.setMovementMethod(new ScrollingMovementMethod());
        layout.addView(  __input, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
        layout.setPadding( DipUtils.dipToPixel( activity, 40 ), 0, DipUtils.dipToPixel( activity, 40 ), 0 );

        activity.runOnUiThread( new Runnable(){
            @Override
            public void run() {
                AlertDialog.Builder alt_bld = new AlertDialog.Builder( activity );
                alt_bld.setView(layout);
                if( no_string_res == -1 || yes_string_res == -1 ) {
                    alt_bld.setMessage(message).setCancelable(false)
                            .setNegativeButton( "NO", listener)        // -2
                            .setPositiveButton( "YES", listener);    // -1
                } else {
                    alt_bld.setMessage(message).setCancelable(false)
                            .setNegativeButton( no_string_res, listener)        // -2
                            .setPositiveButton( yes_string_res, listener);    // -1
                }
                AlertDialog alert = alt_bld.create();
                // Title for AlertDialog
                if( title != null ) {
                    alert.setTitle( title );
                }
                // Icon for AlertDialog
                if( res_icon > 0 ) {
                    alert.setIcon( res_icon );
                }
                alert.show();
            }});
    }
    public static int no_string_res = -1;
    public static int yes_string_res = -1;

    public static String getTextInputDialogResult() {
        return __input.getText().toString();
    }
}
