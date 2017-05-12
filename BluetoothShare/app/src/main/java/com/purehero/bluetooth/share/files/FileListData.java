package com.purehero.bluetooth.share.files;

import android.webkit.MimeTypeMap;

import com.purehero.module.common.CommonFileUtils;

import java.io.File;import java.text.SimpleDateFormat;

import java.util.Comparator;

/**
 * Created by MY on 2017-02-25.
 */

public class FileListData {
    private File file;
    private String subTitle = "";
    private String fileDate = "";
    private String mimeType = "";
    private int subItemCount = 0;
    private boolean selected = false;
    private int clickCount = 0;             // 사용자에 의해 선택되어지 횟수

    public FileListData( File file ) {
        init( file );
    }

    private void init( File file ) {
        this.file = file;

        if( file.isDirectory()) {
            File subItems [] = file.listFiles();
            if( subItems != null ) {
                subItemCount = subItems.length;
            }
            subTitle = String.format( "%d item", subItemCount );

        } else {
            subTitle = CommonFileUtils.getFilesize(file);

            try {
                String extension = CommonFileUtils.getFileExt( file.getName());
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension( extension.toLowerCase() );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat( CommonFileUtils.DATE_FORMAT );
        fileDate = sdf.format(file.lastModified());
    }

    public void setFile(File destFile) {
        init( destFile );
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
    public int getSubItemCount() {
        return subItemCount;
    }

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
