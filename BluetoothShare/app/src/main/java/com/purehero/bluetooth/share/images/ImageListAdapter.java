package com.purehero.bluetooth.share.images;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.purehero.bluetooth.share.R;
import com.purehero.bluetooth.share.contacts.ContactData;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by MY on 2017-05-12.
 */

public class ImageListAdapter extends BaseAdapter implements Filterable, View.OnClickListener {
    private List<ImageListData> listData = new ArrayList<ImageListData>();    // 전체 데이터
    private List<ImageListData> filterData = new ArrayList<ImageListData>();  // 검색이 적용된 데이터

    private Activity context;
    public ImageListAdapter(Activity context) {
        this.context = context;
    }

    @Override
    public int getCount() { return filterData.size(); }

    @Override
    public Object getItem(int i) { return filterData.get(i); }

    @Override
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if( view == null ) {
            viewHolder = new ViewHolder();

            LayoutInflater inflater = ( LayoutInflater ) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            if( viewGroup instanceof ListView) {
                view  = inflater.inflate( R.layout.myfile_list_cell_layout, null );

                viewHolder.tvSubTitle 	= (TextView)  view.findViewById( R.id.file_list_view_item_sub_title );
                viewHolder.tvDate 		= (TextView)  view.findViewById( R.id.file_list_view_item_date );

            } else if( viewGroup instanceof GridView) {
                view  = inflater.inflate( R.layout.myfile_grid_cell_layout, null );

                viewHolder.tvSubTitle 	= null;
                viewHolder.tvDate 		= null;
            }

            viewHolder.cbSelected   = (CheckBox)  view.findViewById( R.id.file_list_view_item_checkbox );
            viewHolder.cbSelected.setOnClickListener( this );
            viewHolder.ivIcon 		= (ImageView) view.findViewById( R.id.file_list_view_item_icon );
            viewHolder.tvTitle 	    = (TextView)  view.findViewById( R.id.file_list_view_item_file_name );


            view.setTag( viewHolder );
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        ImageListData data = ( ImageListData ) getItem( position );

        viewHolder.tvTitle.setText( data.getFilename());
        if( viewHolder.tvSubTitle != null ) {
            viewHolder.tvSubTitle.setVisibility(View.VISIBLE);
            viewHolder.tvSubTitle.setText(data.getSubTitle());
        }
        if( viewHolder.tvDate != null ) {
            viewHolder.tvDate.setVisibility(View.VISIBLE);
            viewHolder.tvDate.setText(data.getFileDate());
        }

        if( isSelectMode()) {
            viewHolder.cbSelected.setVisibility( View.VISIBLE );
            viewHolder.cbSelected.setChecked( data.isSelected() );
        } else {
            viewHolder.cbSelected.setVisibility( View.GONE );
            viewHolder.cbSelected.setChecked( false );
            data.setSelected( false );
        }
        viewHolder.cbSelected.setId( position );

        Glide.with( context ).load( data.getFile()).centerCrop().placeholder( R.drawable.fl_ic_image ).into( viewHolder.ivIcon );
        return view;
    }

    @Override
    public void onClick(View view) {
        if( view instanceof CheckBox ) {
            int position = view.getId();
            ImageListData data = filterData.get( position );
            data.setSelected( !data.isSelected());
        }
    }

    class ViewHolder
    {
        public CheckBox cbSelected;
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvSubTitle;
        public TextView tvDate;
    }

    public void reload() {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String selection = null;
        String[] projection = { "*" };

        listData.clear();
        filterData.clear();

        Cursor cursor = context.getContentResolver().query( uri, projection, selection, null, null);
        if (cursor == null) return;
        if (!cursor.moveToFirst()) return;
        do {
            String path = cursor.getString( cursor.getColumnIndex(MediaStore.Images.Media.DATA));

            ImageListData data = new ImageListData( new File( path ));
            listData.add( data );
            filterData.add( data );

        } while (cursor.moveToNext());

        sort();
    }

    public void sort() {
        Collections.sort( filterData, ImageListData.ALPHA_COMPARATOR );
        context.runOnUiThread( new Runnable(){
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    private boolean selectMode = false;
    public boolean isSelectMode() { return selectMode; }
    public void setSelectMode(boolean selectMode) {
        boolean bChanged = this.selectMode != selectMode;

        this.selectMode = selectMode;
        if( bChanged ) {
            for( ImageListData data : filterData ) {
                data.setSelected( false );
            }
            context.runOnUiThread( new Runnable(){
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    public int getSelectedItemCount() {
        int ret = 0;
        for( ImageListData data : filterData ) {
            if( data.isSelected()) ++ ret;
        }
        return ret;
    }

    public List<ImageListData> getSelectedItems() {
        List<ImageListData> ret = new ArrayList<ImageListData>();

        for( ImageListData data : filterData ) {
            if( data.isSelected()) {
                ret.add( data );
            }
        }
        return ret;
    }

    public synchronized void setAllSelected( boolean bSelect ) {
        for( ImageListData data : filterData ) {
            data.setSelected( bSelect );
        }
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

            ArrayList<ImageListData> nlist = new ArrayList<ImageListData>();
            for (int i = 0; i < listData.size(); i++) {
                final ImageListData item = listData.get(i);

                if (item.checkFilteredData( filterString )) {
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
            filterData = (List<ImageListData>) results.values;

            sort();
        }
    }
}
