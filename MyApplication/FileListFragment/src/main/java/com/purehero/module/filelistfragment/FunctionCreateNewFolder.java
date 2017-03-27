package com.purehero.module.filelistfragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;

import com.purehero.module.common.OnSuccessListener;
import com.purehero.module.common.DialogUtils;

import java.io.File;

/**
 * Created by MY on 2017-03-27.
 */

public class FunctionCreateNewFolder {
    final Activity context;
    final File folder;
    final OnSuccessListener successListener;

    public FunctionCreateNewFolder(Activity context, File folder, OnSuccessListener successListener) {
        this.context = context;
        this.folder = folder;
        this.successListener = successListener;
    }

    public void run() {
        Log.d( "MyLOG", "FunctionCreateNewFolder::run" );

        String title    = context.getString( R.string.create_folder_title);
        String hint     = context.getString( R.string.new_folder);
        String text     = null;

        File targetFolder = new File( folder, hint );
        if( targetFolder.exists()) {
            for( int i = 1; targetFolder.exists(); i++ ) {
                hint = String.format( "%s(%d)", context.getString( R.string.new_folder), i );
                targetFolder = new File( folder, hint );
            }
        }

        DialogUtils.no_string_res     = R.string.cancel;
        DialogUtils.yes_string_res    = R.string.create;
        DialogUtils.TextInputDialog( context, title, text, hint, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch( i ) {
                    case DialogUtils.DIALOG_BUTTON_ID_YES :
                        File newFolder = new File( folder, DialogUtils.getTextInputDialogResult());
                        // G.Log( "Try to create folder : '%s'", newFolder.getAbsolutePath() );
                        if( newFolder.mkdirs()) {
                            if( successListener != null ) {
                                successListener.OnSuccess();
                            }
                            //listAdapter.reload();                                               // 리스트를 갱신 시킨다.

                            //context.getSupportActionBar().setDisplayHomeAsUpEnabled( true );    // 액션바에 뒤로 가기 버튼을 표시한다.
                            //listAdapter.push_folder( newFolder);                                // 생성한 폴더로 리스트를 갱신시킨다.
                            //rel.run();

                        }
                        break;
                }
            }
        } );
        DialogUtils.no_string_res     = -1;
        DialogUtils.yes_string_res    = -1;
    }
}
