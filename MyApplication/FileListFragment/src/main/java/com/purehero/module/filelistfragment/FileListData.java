package com.purehero.module.filelistfragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 * Created by MY on 2017-02-25.
 */

public class FileListData {
    private final Context context;
    private final File file;
    private String subTitle = "";
    private String fileDate = "";
    private String mimeType = "";
    private Drawable icon 	= null;
    private int subItemCount = 0;
    private boolean selected = false;
    private int clickCount = 0;             // 사용자에 의해 선택되어지 횟수

    public static final String DATE_FORMAT = "MM/dd/yy H:mm a";

    public FileListData(Context context, File file ) {
        this.context = context;
        this.file = file;

        if( file.isDirectory()) {
            File subItems [] = file.listFiles();
            if( subItems != null ) {
                subItemCount = subItems.length;
            }
            subTitle = String.format( "%d item", subItemCount );

        } else {
            subTitle = getFilesize(file);

            try {
                String extension = getFileExt( file.getName());
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension( extension.toLowerCase() );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat( DATE_FORMAT );
        fileDate = sdf.format(file.lastModified());
    }

    private String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

    /**
     * filename 반환한다.
     *
     * @return
     */
    public String getFilename() {
        return file.getName();
    }

    public String getMimeType() { return mimeType; }

    /**
     * subTitle 을 반환한다.
     *
     * @return
     */
    public String getSubTitle() {
        return subTitle;
    }

    public String getFileDate() {
        return fileDate;
    }

    public File getFile() { return file; }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    public void IncrementClickCount() {
        ++clickCount;
    }
    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }

    public Drawable getIcon() {
        if( icon != null ) return icon;

        if( file.isDirectory() ) {
            if( subItemCount > 0 ) {
                icon = context.getResources().getDrawable( R.drawable.fl_ic_folder_full );
            } else {
                icon = context.getResources().getDrawable( R.drawable.fl_ic_folder );
            }
        } else {
            if( mimeType != null ) {
                //G.Log( "mimeType : %s", mimeType );
                if (mimeType.startsWith("image")) {
                    icon = context.getResources().getDrawable(R.drawable.fl_ic_image);
                } else if (mimeType.startsWith("audio")) {
                    icon = context.getResources().getDrawable(R.drawable.fl_ic_music);
                } else if (mimeType.startsWith("video")) {
                    icon = context.getResources().getDrawable(R.drawable.fl_ic_movies);
                } else if (mimeType.endsWith("zip")) {
                    icon = context.getResources().getDrawable(R.drawable.fl_ic_zip);
                } else if (mimeType.endsWith("excel")) {
                    icon = context.getResources().getDrawable(R.drawable.fl_ic_excel);
                } else if (mimeType.endsWith("powerpoint")) {
                    icon = context.getResources().getDrawable(R.drawable.fl_ic_ppt);
                } else if (mimeType.endsWith("word")) {
                    icon = context.getResources().getDrawable(R.drawable.fl_ic_word);
                } else if (mimeType.endsWith("pdf")) {
                    icon = context.getResources().getDrawable(R.drawable.fl_ic_pdf);
                } else if (mimeType.endsWith("xml")) {
                    icon = context.getResources().getDrawable(R.drawable.fl_ic_xml32);
                } else if (mimeType.endsWith("vnd.android.package-archive")) {  // APK
                    icon = context.getResources().getDrawable(R.drawable.fl_ic_apk);
                } else if (mimeType.endsWith("torrent")) {  // APK
                    icon = context.getResources().getDrawable(R.drawable.fl_ic_torrent);
                } else {// torrent
                    // text 로 간주
                    icon = context.getResources().getDrawable(R.drawable.fl_ic_text);
                }
            } else {
                // text 로 간주
                icon = context.getResources().getDrawable(R.drawable.fl_ic_text);
            }
        }
        return icon;
    }

    /**
     * 파일의 크기를 문자열로 반환한다.
     *
     * @param file
     * @return
     */
    public static final String getFilesize( File file ) {
        String result = "0 B";

        float size = file.length();
        if( size < 1024.0f ) {
            result = String.format( "%d B", (int)size );
        } else {
            size /= 1024.0f;
            if( size < 1024.0f ) {
                result = String.format( "%.2f KB", size );
            } else {
                size /= 1024.0f;
                if( size < 1024.0f ) {
                    result = String.format( "%.2f MB", size );
                } else {
                    size /= 1024.0f;
                    result = String.format( "%.2f GB", size );
                }
            }
        }

        return result;
    }

    public static final Comparator<FileListData> ALPHA_COMPARATOR = new Comparator<FileListData> () {
        @Override
        public int compare(FileListData arg0, FileListData arg1) {
            if( arg0.file.isDirectory() && !arg1.file.isDirectory() ) return -1;
            if( !arg0.file.isDirectory() && arg1.file.isDirectory() ) return  1;
            if( arg0.getClickCount() > arg1.getClickCount()) {
                return -1;
            } else if( arg0.getClickCount() < arg1.getClickCount() ) {
                return 1;
            }
            return arg0.getFilename().compareToIgnoreCase( arg1.getFilename());
        }
    };

    int index = -1;
    /**
     * @param value
     */
    public void setIndex( int value ) {
        index = value;
    }

    /**
     * @return
     */
    public int getIndex() {
        return index;
    }
}
