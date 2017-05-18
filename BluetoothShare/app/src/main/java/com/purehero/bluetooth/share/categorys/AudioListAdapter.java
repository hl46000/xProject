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

public class AudioListAdapter extends BaseListAdapter {
    public AudioListAdapter(Activity context) {
        super(context);
    }

    @Override
    protected void drawIcon(Activity context, BaseListData data, ImageView ivIcon) {
        Uri iconUri = data.getIconUri();
        if( iconUri == null ) {
            Glide.with(context).load( getArtUriFromMusicFile(data.getFile())).centerCrop().placeholder(R.drawable.fl_ic_music).into(ivIcon);
        } else {
            Glide.with(context).load(iconUri).centerCrop().placeholder(R.drawable.fl_ic_music).into(ivIcon);
        }
    }

    @Override
    public void setListDatas(List<BaseListData> listDatas) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = null;
        String[] projection = { "*" };

        Cursor cursor = context.getContentResolver().query( uri, projection, selection, null, null);
        if (cursor == null) return;
        if (!cursor.moveToFirst()) return;
        do {
            String _ID      = cursor.getString( cursor.getColumnIndex(MediaStore.Images.Media._ID));
            String path     = cursor.getString( cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            Long totalSecs  = cursor.getLong( cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

            Long albumId    = cursor.getLong( cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);

            BaseListData data = new BaseListData( new File( path ));
            data.setIconUri( albumArtUri );

            totalSecs /= 1000;
            int hours   = (int)( totalSecs / 3600 );
            int minutes = (int)( (totalSecs % 3600) / 60 );
            int seconds = (int)( totalSecs % 60 );

            if( hours > 0 ) {
                data.setPlayDuration(String.format("%d:%02d:%02d", hours, minutes, seconds));
            } else {
                data.setPlayDuration(String.format("%02d:%02d", minutes, seconds));
            }

            Uri fileUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, _ID );
            data.setFileUri( fileUri );

            listDatas.add( data );

        } while (cursor.moveToNext());
    }

    public Uri getArtUriFromMusicFile(File file) {
        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = { MediaStore.Audio.Media.ALBUM_ID };

        final String where = MediaStore.Audio.Media.IS_MUSIC + "=1 AND " + MediaStore.Audio.Media.DATA + " = '"
                + file.getAbsolutePath() + "'";
        final Cursor cursor = context.getContentResolver().query(uri, cursor_cols, where, null, null);
        //Log.d(TAG, "Cursor count:" + cursor.getCount());
        /*
         * If the cusor count is greater than 0 then parse the data and get the art id.
         */
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            Long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
            cursor.close();
            return albumArtUri;
        }
        return Uri.EMPTY;
    }
}
