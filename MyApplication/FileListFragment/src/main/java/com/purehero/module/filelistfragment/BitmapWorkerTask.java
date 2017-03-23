package com.purehero.module.filelistfragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by purehero on 2017-03-23.
 */

public class BitmapWorkerTask extends AsyncTask {
    final Context context;
    final FileListData data;
    final WeakReference<ImageView> imageViewReference;
    final int res_id;

    public BitmapWorkerTask( Context context, FileListData data, ImageView imageView ) {
        this.context = context;
        this.data = data;
        imageViewReference = new WeakReference<ImageView>( imageView );
        res_id = BitmapWorker.getImageResourceID(data);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        if( res_id == R.drawable.fl_ic_image ) {
            return BitmapWorker.decodeBitmapFromFile( context, data.getFile(), 100, 100 );
        }
        return BitmapWorker.decodeBitmapFromResource( context, res_id, 100, 100 );
    }

    @Override
    protected void onPostExecute(Object o) {
        if( isCancelled()) {
            o = null;
        }

        Bitmap bitmap = ( Bitmap ) o;
        if( imageViewReference != null && bitmap != null ) {
            final ImageView imageview = imageViewReference.get();
            if( imageview != null ) {
                imageview.setImageBitmap( bitmap );
            }
        }
        super.onPostExecute(o);
    }


}
