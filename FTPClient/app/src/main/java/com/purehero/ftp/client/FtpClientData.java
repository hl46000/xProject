package com.purehero.ftp.client;

import android.content.Context;
import android.webkit.MimeTypeMap;

import org.apache.commons.net.ftp.FTPFile;

import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 * Created by MY on 2017-02-25.
 */

public class FtpClientData {
    private final Context context;
    private final FTPFile file;
    private String subTitle = "";
    private String fileDate = "";
    private String mimeType = "";

    private int subItemCount = 0;
    private boolean selected = false;
    private int clickCount = 0;             // 사용자에 의해 선택되어지 횟수

    public static final String DATE_FORMAT = "yyyy/M/d a h:mm";

    public FtpClientData(Context context, FTPFile file ) {
        this.context = context;
        this.file = file;

        if( file.isDirectory()) {
            subTitle = file.toFormattedString().substring(0,10);

        } else {
            subTitle = getFilesize(file.getSize());

            try {
                String extension = getFileExt( file.getName());
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension( extension.toLowerCase() );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat( DATE_FORMAT );
        fileDate = sdf.format(file.getTimestamp().getTime());
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

    public FTPFile getFile() { return file; }

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


    /**
     * 파일의 크기를 문자열로 반환한다.
     *
     * @param lsize
     * @return
     */
    public static final String getFilesize(long lsize ) {
        String result = "0 B";

        float size = lsize;
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

    public static final Comparator<FtpClientData> FTPFile_ALPHA_COMPARATOR = new Comparator<FtpClientData> () {
        @Override
        public int compare(FtpClientData arg0, FtpClientData arg1) {
            if( arg0.getFile().isDirectory() && !arg1.getFile().isDirectory() ) return -1;
            if( !arg0.getFile().isDirectory() && arg1.getFile().isDirectory() ) return  1;
            /*
            if( arg0.getClickCount() > arg1.getClickCount()) {
                return -1;
            } else if( arg0.getClickCount() < arg1.getClickCount() ) {
                return 1;
            }
            */

            return arg0.getFilename().compareToIgnoreCase( arg1.getFilename());
        }
    };

    public int getSubItemCount() {
        return subItemCount;
    }
}
