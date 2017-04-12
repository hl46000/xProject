package com.purehero.module.fragment.filelist;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.purehero.module.common.R;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * Created by MY on 2017-02-25.
 */

public class FileListAdapter extends BaseAdapter implements Filterable, View.OnClickListener {
    private List<FileListData> listData = new ArrayList<FileListData>();    // 전체 데이터
    private List<FileListData> filterData = new ArrayList<FileListData>();  // 검색이 적용된 데이터

    private Activity context;
    public FileListAdapter(Activity context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return filterData.size();
    }

    @Override
    public Object getItem(int i) {
        FileListData ret = filterData.get(i);
        ret.setIndex( i );
        return ret;
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
                if( file.isHidden()) continue;

                FileListData data = new FileListData( file );
                FileClickCount.loadClickCount( data );

                filterData.add( data );
                listData.add( data );
            }
        }
        sort();
    }

    class DataInputStreamThread extends Thread implements Runnable {
        private final DataInputStream dis;

        public DataInputStreamThread( DataInputStream dis ) {
            this.dis = dis;
            this.start();
        }

        @Override
        public void run() {
            int nRead;
            try {
                byte buffer[] = new byte[1024];
                while(( nRead = dis.read( buffer, 0, 1024  )) > 0 ) {
                    Log.d( "MyLOG", new String( buffer, 0, nRead) );
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        if( res_id == R.drawable.fl_ic_image || res_id == R.drawable.fl_ic_movies ) {
            Glide.with( context ).load( data.getFile()).centerCrop().placeholder( res_id ).into( viewHolder.ivIcon );
        } else {
            Glide.with( context ).load( res_id ).into( viewHolder.ivIcon );
        }

        return view;
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

    public void remove(int index) {
        filterData.remove( index );
        notifyDataSetChanged();
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

    private boolean selectMode = false;
    public boolean isSelectMode() { return selectMode; }
    public void setSelectMode(boolean selectMode) {
        this.selectMode = selectMode;
    }

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
}
