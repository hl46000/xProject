package com.purehero.module.filelistfragment;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.TypedValue;
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

import java.io.File;
import java.lang.ref.WeakReference;
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
                if( file.isHidden()) continue;

                FileListData data = new FileListData( file );
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
        viewHolder.ivIcon.setImageBitmap( data.getIcon( context ));

        if( data.isThumbnail()) {
            new BitmapWorkerTask( context, data, viewHolder.ivIcon ).execute();
        }
        return view;
    }

    protected void loadBitmap( FileListData data, ImageView imageView ) {
        if( cancelPotentialWork( data, imageView )) {
            final BitmapWorkerTask task = new BitmapWorkerTask( context, data, imageView );
            final AsyncDrawable drawable = new AsyncDrawable( context.getResources(), null, task );
            imageView.setImageDrawable( drawable );
            task.execute();
        }
    }

    private boolean cancelPotentialWork(FileListData data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkTask = getBitmapWorkerTask( imageView );
        if( bitmapWorkTask != null ) {
            final FileListData bitmapData = bitmapWorkTask.data;
            if( bitmapData != data ) {
                bitmapWorkTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    private BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if( imageView != null ) {
            final Drawable drawable = imageView.getDrawable();
            if( drawable instanceof AsyncDrawable ) {
                final AsyncDrawable asyncDrawable = ( AsyncDrawable ) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
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
}
