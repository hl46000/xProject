package com.purehero.bluetooth.share.categorys;


import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.purehero.bluetooth.share.BaseListAdapter;
import com.purehero.bluetooth.share.BaseListData;
import com.purehero.bluetooth.share.R;

import java.io.File;
import java.util.List;

/**
 * Created by MY on 2017-05-12.
 */

public class MyFileListAdapter extends BaseListAdapter {
    public MyFileListAdapter(Activity context) {
        super(context);
    }

    @Override
    protected void drawIcon(Activity context, BaseListData data, ImageView ivIcon) {
        Glide.with( context ).load( data.getFile()).centerCrop().placeholder( R.drawable.fl_ic_text ).into( ivIcon );
    }

    @Override
    public void setListDatas(List<BaseListData> listDatas) {
        // External 스토리지의 URI 획득
        final Uri uri = MediaStore.Files.getContentUri("external");
        String selection = null;
        String[] projection = { "*" };

        Cursor cursor = context.getContentResolver().query( uri, projection, selection, null, null);
        if (cursor == null) return;
        if (!cursor.moveToFirst()) return;
        do {
            String path = cursor.getString( cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));

            BaseListData data = new BaseListData( new File( path ));
            listDatas.add( data );

        } while (cursor.moveToNext());
    }
}
