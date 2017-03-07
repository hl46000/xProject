package share.file.purehero.com.fileshare;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.icu.text.Collator;

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
        }

        SimpleDateFormat sdf = new SimpleDateFormat( DATE_FORMAT );
        fileDate = sdf.format(file.lastModified());
    }

    /**
     * filename 반환한다.
     *
     * @return
     */
    public String getFilename() {
        return file.getName();
    }

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
                icon = context.getResources().getDrawable( R.drawable.folder_full );
            } else {
                icon = context.getResources().getDrawable( R.drawable.folder );
            }

        } else {
            icon = context.getResources().getDrawable( R.drawable.text );
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
            result = String.format( "%.2f B", size );
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
}
