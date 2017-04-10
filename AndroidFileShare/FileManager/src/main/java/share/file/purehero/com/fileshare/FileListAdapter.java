package share.file.purehero.com.fileshare;


import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.purehero.common.G;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

/**
 * Created by MY on 2017-02-25.
 */

public class FileListAdapter extends BaseAdapter implements Filterable, View.OnClickListener {
    private List<FileListData> listData = new ArrayList<FileListData>();    // 전체 데이터
    private List<FileListData> filterData = new ArrayList<FileListData>();  // 검색이 적용된 데이터
    //private boolean selectMode = false;

    private MainActivity context;
    public FileListAdapter( MainActivity context) {
        this.context = context;
    }

    @Override
    public int getCount() { return filterData.size(); }

    @Override
    public Object getItem(int i) {
        return filterData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setSelectALL(boolean selected ) {
        for( FileListData data : filterData ) {
            data.setSelected( selected );
        }
    }

    public List<FileListData> getSelectedItems() {
        List<FileListData> ret = new ArrayList<FileListData>();
        for( FileListData data : filterData ) {
            if( data.isSelected() ) {
                ret.add( data );
            }
        }

        return ret;
    }

    public int getSelectedCount() {
        int ret = 0;
        for( FileListData data : filterData ) {
            if( data.isSelected() ) {
                ++ret;
            }
        }
        return ret;
    }

    Runnable listDataUpdateRunnable = new Runnable(){
        @Override
        public void run() {
            notifyDataSetChanged();
        }
    };

    public synchronized void sort() {
        Collections.sort( filterData, FileListData.ALPHA_COMPARATOR );
        context.runOnUiThread( listDataUpdateRunnable );
    }

    public synchronized void reload(){
        listData.clear();
        filterData.clear();

        File subItems [] = getLastFolder().listFiles();
        if( subItems != null ) {
            for( File file : subItems ) {
                FileListData data = new FileListData( context, file );
                FileClickCount.loadClickCount( data );

                filterData.add( data );
                listData.add( data );
            }
        }
        sort();
    }

    @Override
    public synchronized View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if( view == null ) {
            viewHolder = new ViewHolder();

            LayoutInflater inflater = ( LayoutInflater ) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            view  = inflater.inflate( R.layout.file_list_cell, null );

            viewHolder.cbSelected   = (CheckBox)  view.findViewById( R.id.file_list_view_item_checkbox );
            viewHolder.cbSelected.setOnClickListener( this );
            viewHolder.ivIcon 		= (ImageView) view.findViewById( R.id.file_list_view_item_icon );
            viewHolder.tvTitle 	    = (TextView)  view.findViewById( R.id.file_list_view_item_file_name );
            viewHolder.tvSubTitle 	= (TextView)  view.findViewById( R.id.file_list_view_item_sub_title );
            viewHolder.tvDate 		= (TextView)  view.findViewById( R.id.file_list_view_item_date );

            view.setTag( viewHolder );
        } else {
            viewHolder = ( ViewHolder ) view.getTag();
        }

        FileListData data = ( FileListData ) getItem( position );

        viewHolder.tvTitle.setText( data.getFilename());
        /*
        viewHolder.tvTitle.setSelected( true );
        viewHolder.tvTitle.setHorizontallyScrolling( true );
        viewHolder.tvTitle.setMovementMethod(new ScrollingMovementMethod());
        */

        viewHolder.tvSubTitle.setVisibility( View.VISIBLE );
        viewHolder.tvSubTitle.setText( data.getSubTitle());
        viewHolder.tvDate.setVisibility( View.VISIBLE );
        viewHolder.tvDate.setText( data.getFileDate());

        if( isSelectMode()) {
            viewHolder.cbSelected.setVisibility( View.VISIBLE );
            viewHolder.cbSelected.setChecked( data.isSelected() );
        } else {
            viewHolder.cbSelected.setVisibility( View.GONE );
            viewHolder.cbSelected.setChecked( false );
            data.setSelected( false );
        }
        viewHolder.cbSelected.setId( position );

        int res_id = getImageResourceID( data );
        if( res_id == R.drawable.image ||
                res_id == R.drawable.movies ) {
            Glide.with(context).load(data.getFile()).centerCrop().placeholder(res_id).into(viewHolder.ivIcon);
        } else if( res_id == R.drawable.music ) {
            Glide.with(context).load(getArtUriFromMusicFile(data.getFile())).centerCrop().placeholder(res_id).into(viewHolder.ivIcon);
        } else {
            Glide.with( context ).load( res_id ).into( viewHolder.ivIcon );
        }
        /*
        Drawable icon = data.getIcon();
        if( icon == null ) {
            viewHolder.ivIcon.setVisibility( View.INVISIBLE );
        } else {
            viewHolder.ivIcon.setImageDrawable( icon );
            viewHolder.ivIcon.setVisibility( View.VISIBLE );
        }
        */

        return view;
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

    /**
     *
     * @param data
     * @return
     */
    public int getImageResourceID(FileListData data) {
        int res_id = -1;

        File file = data.getFile();
        String mimeType = data.getMimeType();

        if( file.isDirectory() ) {
            if( data.getSubItemCount() > 0 ) {
                res_id = R.drawable.folder_full;
            } else {
                res_id = R.drawable.folder;
            }
        } else {
            if( mimeType != null ) {
                if (mimeType.startsWith("image")) {
                    res_id = R.drawable.image;
                } else if (mimeType.startsWith("audio")) {
                    res_id = R.drawable.music;
                } else if (mimeType.startsWith("video")) {
                    res_id = R.drawable.movies;
                } else if (mimeType.endsWith("zip")) {
                    res_id = R.drawable.zip;
                } else if (mimeType.endsWith("excel")) {
                    res_id = R.drawable.excel;
                } else if (mimeType.endsWith("powerpoint")) {
                    res_id = R.drawable.ppt;
                } else if (mimeType.endsWith("word")) {
                    res_id = R.drawable.word;
                } else if (mimeType.endsWith("pdf")) {
                    res_id = R.drawable.pdf;
                } else if (mimeType.endsWith("xml")) {
                    res_id = R.drawable.xml32;
                } else if (mimeType.endsWith("vnd.android.package-archive")) {  // APK
                    res_id = R.drawable.apk;
                } else if (mimeType.endsWith("torrent")) {  // APK
                    res_id = R.drawable.torrent;
                } else {// torrent
                    // text 로 간주
                    res_id = R.drawable.text;
                }
            } else {
                // text 로 간주
                res_id = R.drawable.text;
            }
        }

        return res_id;
    }

    View.OnTouchListener TextViewTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_MOVE ) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
            } else {
                v.getParent().requestDisallowInterceptTouchEvent(false);
            }
            return true;
        }
    };


    @Override
    public void onClick(View view) {
        CheckBox cb = ( CheckBox ) view;
        FileListData data = filterData.get( cb.getId() );
        data.setSelected( cb.isChecked());
    }

    class ViewHolder
    {
        public CheckBox cbSelected;
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvSubTitle;
        public TextView tvDate;
    }

    private Vector<String> folder_name_stack = new Vector<String>();
    private Vector<File> folder_stack = new Vector<File>();
    public synchronized Vector<String> getFolderNameVector() { return folder_name_stack; }
    public synchronized Vector<File> getFolderVector() { return folder_stack; }
    public synchronized void push_folder(File file, String name ) {
        folder_stack.add( file );
        folder_name_stack.add( name==null? file.getName() : name );
    }

    /*
    * return : 다음에도 pop 이 가능한지를 반환한다.
    * */
    public synchronized boolean pop_folder( boolean bReload ) {
        if( folder_stack.size() > 1 ) {
            folder_stack.remove( folder_stack.size() - 1 );
            folder_name_stack.remove( folder_name_stack.size() - 1 );

            if( bReload ) {
                reload();
            }
        }

        return is_next_pop_folder();
    }

    public synchronized boolean is_next_pop_folder() {
        return folder_stack.size() > 1 ? true : false;
    }

    public synchronized String getLastFolderName() {
        return folder_name_stack.lastElement();
    }
    public synchronized File getLastFolder() {
        return folder_stack.lastElement();
    }

    public boolean isSelectMode() { return context.isSelectMode(); }
    /*
    public void setSelectMode(boolean selectMode) {
        this.selectMode = selectMode;
    }
    */


    @Override
    public Filter getFilter() {
        return new ItemFilter();
    }

    class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if ( constraint == null ) {
                results.values = listData;
                results.count = listData.size();
                return results;
            }

            String filterString = constraint.toString().toLowerCase();
            if ( filterString.length() <= 0) {
                results.values = listData;
                results.count = listData.size();
                return results;
            }

            ArrayList<FileListData> nlist = new ArrayList<FileListData>();
            for (int i = 0; i < listData.size(); i++) {
                final FileListData item = listData.get(i);

                if (item.getFilename().toLowerCase().contains(filterString) ||
                        item.getFilename().toLowerCase().contains(filterString)) {
                    nlist.add( item );
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filterData = (List<FileListData>) results.values;
            sort();
        }
    }
}
