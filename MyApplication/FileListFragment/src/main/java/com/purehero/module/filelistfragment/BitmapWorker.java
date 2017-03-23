package com.purehero.module.filelistfragment;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.support.v4.util.
import android.support.v7.app.WindowDecorActionBar;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static android.os.Environment.isExternalStorageRemovable;

/**
 * Created by purehero on 2017-03-23.
 */

public class BitmapWorker {
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private final Object mDiskCacheLock = new Object();

    static LruCache<String,Bitmap> memoryCache = null;
    static DiskLruCache mDiskLruCache;

    /**
     *
     * @param data
     * @return
     */
    public static int getImageResourceID(FileListData data) {
        int res_id = -1;

        File file = data.getFile();
        String mimeType = data.getMimeType();

        if( file.isDirectory() ) {
            if( data.getSubItemCount() > 0 ) {
                res_id = R.drawable.fl_ic_folder_full;
            } else {
                res_id = R.drawable.fl_ic_folder;
            }
        } else {
            if( mimeType != null ) {
                if (mimeType.startsWith("image")) {
                    res_id = R.drawable.fl_ic_image;
                } else if (mimeType.startsWith("audio")) {
                    res_id = R.drawable.fl_ic_music;
                } else if (mimeType.startsWith("video")) {
                    res_id = R.drawable.fl_ic_movies;
                } else if (mimeType.endsWith("zip")) {
                    res_id = R.drawable.fl_ic_zip;
                } else if (mimeType.endsWith("excel")) {
                    res_id = R.drawable.fl_ic_excel;
                } else if (mimeType.endsWith("powerpoint")) {
                    res_id = R.drawable.fl_ic_ppt;
                } else if (mimeType.endsWith("word")) {
                    res_id = R.drawable.fl_ic_word;
                } else if (mimeType.endsWith("pdf")) {
                    res_id = R.drawable.fl_ic_pdf;
                } else if (mimeType.endsWith("xml")) {
                    res_id = R.drawable.fl_ic_xml32;
                } else if (mimeType.endsWith("vnd.android.package-archive")) {  // APK
                    res_id = R.drawable.fl_ic_apk;
                } else if (mimeType.endsWith("torrent")) {  // APK
                    res_id = R.drawable.fl_ic_torrent;
                } else {// torrent
                    // text 로 간주
                    res_id = R.drawable.fl_ic_text;
                }
            } else {
                // text 로 간주
                res_id = R.drawable.fl_ic_text;
            }
        }

        return res_id;
    }

    public static synchronized Bitmap decodeBitmapFromFile(Context context, File imgFile, int w, int h ) {
        Bitmap ret = getBitmapFromMemCache( imgFile.getAbsolutePath() );
        if( ret != null ) {
            return ret;
        }

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile( imgFile.getAbsolutePath(), bmOptions );

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/w, photoH/h);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        ret = BitmapFactory.decodeFile( imgFile.getAbsolutePath(), bmOptions);
        addBitmapFromMemCache( context, imgFile.getAbsolutePath(), ret );

        return ret;
    }

    public static synchronized Bitmap decodeBitmapFromResource( Context context, int res_id, int w, int h ) {
        Bitmap ret = getBitmapFromMemCache( String.valueOf(res_id) );
        if( ret != null ) {
            return ret;
        }

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource( context.getResources(), res_id, bmOptions );

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/w, photoH/h);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        ret = BitmapFactory.decodeResource( context.getResources(), res_id, bmOptions);
        addBitmapFromMemCache( context, String.valueOf( res_id ), ret );

        return ret;
    }

    private static void addBitmapFromMemCache( Context context, String key, Bitmap bmp ) {
        if( memoryCache == null ) {
            // Get memory class of this device, exceeding this amount will throw an
            // OutOfMemory exception.
            final int memClass = ((ActivityManager) context.getSystemService( Context.ACTIVITY_SERVICE)).getMemoryClass();

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = 1024 * 1024 * memClass / 16;
            memoryCache = new LruCache<String, Bitmap>( cacheSize ) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getByteCount();
                }
            };
        }
        memoryCache.put( key, bmp );
    }

    private static void addBitmapFromDiskCache( Context context, String key, Bitmap bmp ) {
        if( mDiskLruCache == null ) {
            try {
                mDiskLruCache = DiskLruCache.open( getDiskCacheDir( context, ".fileicons"), 1, 100, DISK_CACHE_SIZE );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit( key );

        } catch (IOException e) {
            e.printStackTrace();
        }
        if( memoryCache == null ) {
            // Get memory class of this device, exceeding this amount will throw an
            // OutOfMemory exception.
            final int memClass = ((ActivityManager) context.getSystemService( Context.ACTIVITY_SERVICE)).getMemoryClass();

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = 1024 * 1024 * memClass / 16;
            memoryCache = new LruCache<String, Bitmap>( cacheSize ) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getByteCount();
                }
            };
        }
        memoryCache.put( key, bmp );
    }


    private static Bitmap getBitmapFromMemCache( String key ) {
        if( memoryCache == null ) return null;
        return memoryCache.get( key );
    }

    public Bitmap getBitmapFromDiskCache(Context context, String key) {
        if( mDiskLruCache == null ) return null;

        synchronized (mDiskCacheLock) {
            // Wait while disk cache is started from background thread
            try {
                mDiskCacheLock.wait();
            } catch (InterruptedException e) {}


            DiskLruCache.Snapshot snapshot = null;
            try {
                snapshot = mDiskLruCache.get(key);
                if (snapshot != null) {
                    final InputStream input = snapshot.getInputStream( 0 );
                    if ( input != null ) {
                        BufferedInputStream buffered = new BufferedInputStream( input );
                        return BitmapFactory.decodeStream( buffered, null, new BitmapFactory.Options());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if( snapshot != null ) {
                    snapshot.close();
                }
            }
        }
        return null;
    }

    // Creates a unique subdirectory of the designated app cache directory. Tries to use external
// but if not mounted, falls back on internal storage.
    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !isExternalStorageRemovable() ? context.getExternalCacheDir().getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }
}
