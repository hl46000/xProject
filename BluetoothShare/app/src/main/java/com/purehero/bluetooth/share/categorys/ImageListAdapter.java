package com.purehero.bluetooth.share.categorys;


import android.app.Activity;
import android.content.ContentUris;
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

public class ImageListAdapter extends BaseListAdapter {
    public ImageListAdapter(Activity context) {
        super(context);
    }

    @Override
    protected void drawIcon(Activity context, BaseListData data, ImageView ivIcon) {
        Glide.with( context ).load( data.getFile()).centerCrop().placeholder( R.drawable.fl_ic_image ).into( ivIcon );
    }

    @Override
    public void setListDatas(List<BaseListData> listDatas) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String selection = null;
        String[] projection = { "*" };

        Cursor cursor = context.getContentResolver().query( uri, projection, selection, null, null);
        if (cursor == null) return;
        if (!cursor.moveToFirst()) return;
        do {
            Long _ID      = cursor.getLong( cursor.getColumnIndex(MediaStore.Images.Media._ID));
            String path   = cursor.getString( cursor.getColumnIndex(MediaStore.Images.Media.DATA));

            BaseListData data = new BaseListData( new File( path ));

            Uri fileUri = ContentUris.withAppendedId( uri, _ID );
            data.setFileUri( fileUri );

            listDatas.add( data );

        } while (cursor.moveToNext());
    }
}
