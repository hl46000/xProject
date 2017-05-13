package com.purehero.bluetooth.share.images;

import android.webkit.MimeTypeMap;

import com.purehero.module.common.CommonFileUtils;
import java.text.SimpleDateFormat;
import java.io.File;
import java.util.Comparator;

/**
 * Created by MY on 2017-05-12.
 */

public class ImageListData {
    private File file;
    private String subTitle = "";
    private String fileDate = "";
    private String mimeType = "";

    public ImageListData(){}
    public ImageListData( File file ) { init( file ); }

    private void init( File file ) {
        this.file = file;

        subTitle = CommonFileUtils.getFilesize(file);

        try {
            String extension = CommonFileUtils.getFileExt( file.getName());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension( extension.toLowerCase() );
        } catch (Exception e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat( CommonFileUtils.DATE_FORMAT );
        fileDate = sdf.format(file.lastModified());
    }

    public void setFile(File destFile) {
        init( destFile );
    }
    public File getFile() { return file; }

    private boolean selected = false;
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    public boolean checkFilteredData(String filterString) {
        if( file.getName().contains( filterString )) return true;
        return false;
    }

    public int compare( ImageListData target ) {
        //return file.getName().compareTo( target.getFile().getName());
        int result = file.getParent().compareToIgnoreCase( target.getFile().getParent());
        if( result != 0 ) return result;

        return (int)( target.getFile().lastModified() - file.lastModified() );
    }

    /**
     * ContactData 의 list 을 정렬에 필요한 비교자
     */
    public static final Comparator<ImageListData> ALPHA_COMPARATOR = new Comparator<ImageListData> () {
        @Override
        public int compare(ImageListData arg0, ImageListData arg1) {
            return arg0.compare( arg1 );
        }
    };

    public String getFileDate() { return fileDate; }
    public String getFilename() { return file.getName(); }
    public String getSubTitle() { return subTitle; }
}
