package com.purehero.bluetooth.share;

import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.purehero.module.common.CommonFileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 * Created by purehero on 2017-05-16.
 */

public class BaseListData {
    private File file;
    private String subTitle = "";
    private String fileDate = "";
    private String mimeType = "";


    public BaseListData(){}
    public BaseListData( File file ) { init( file ); }

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

    private String playDuration = null;
    public String getPlayDuration() { return playDuration; }
    public void setPlayDuration( String duration ) {
        playDuration = duration;
    }

    private Uri iconUrl = null;
    public Uri getIconUri() { return iconUrl; }
    public void setIconUri( Uri iconUri ) {
        this.iconUrl = iconUri;
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

    public int compare( BaseListData target ) {
        //return target.getFile().getName().compareTo( file.getName());
        /*
        int result = file.getParent().compareToIgnoreCase( target.getFile().getParent());
        if( result != 0 ) return result;
        */
        long diff = target.getFile().lastModified() - file.lastModified();
        if( diff == 0 ) return 0;
        return diff > 0 ? 1 : -1;
    }

    /**
     * ContactData 의 list 을 정렬에 필요한 비교자
     */
    public static final Comparator<BaseListData> ALPHA_COMPARATOR = new Comparator<BaseListData> () {
        @Override
        public int compare(BaseListData arg0, BaseListData arg1) {
            return arg0.compare( arg1 );
        }
    };

    public String getFileDate() { return fileDate; }
    public String getFilename() { return file.getName(); }
    public String getSubTitle() { return subTitle; }
}
