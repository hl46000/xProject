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
            String _ID      = cursor.getString( cursor.getColumnIndex(MediaStore.Video.Media._ID));
            String path     = cursor.getString( cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            Long totalSecs  = cursor.getLong( cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION));

            BaseListData data = new BaseListData( new File( path ));

            totalSecs /= 1000;
            int hours   = (int)( totalSecs / 3600 );
            int minutes = (int)( (totalSecs % 3600) / 60 );
            int seconds = (int)( totalSecs % 60 );

            if( hours > 0 ) {
                data.setPlayDuration(String.format("%d:%02d:%02d", hours, minutes, seconds));
            } else {
                data.setPlayDuration(String.format("%02d:%02d", minutes, seconds));
            }

            Uri fileUri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, _ID );
            data.setFileUri( fileUri );

            listDatas.add( data );

        } while (cursor.moveToNext());
    }
}
