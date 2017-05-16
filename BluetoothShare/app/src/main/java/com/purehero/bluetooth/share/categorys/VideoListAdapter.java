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

public class VideoListAdapter extends BaseListAdapter {
    public VideoListAdapter(Activity context) {
        super(context);
    }

    @Override
    protected void drawIcon(Activity context, BaseListData data, ImageView ivIcon) {
        Glide.with( context ).load( data.getFile()).centerCrop().placeholder( R.drawable.fl_ic_movies ).into( ivIcon );
    }

    @Override
    public void setListDatas(List<BaseListData> listDatas) {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String selection = null;
        String[] projection = { "*" };

        Cursor cursor = context.getContentResolver().query( uri, projection, selection, null, null);
        if (cursor == null) return;
        if (!cursor.moveToFirst()) return;
        do {
            String path = cursor.getString( cursor.getColumnIndex(MediaStore.Video.Media.DATA));

            BaseListData data = new BaseListData( new File( path ));
            listDatas.add( data );

        } while (cursor.moveToNext());
    }
}
