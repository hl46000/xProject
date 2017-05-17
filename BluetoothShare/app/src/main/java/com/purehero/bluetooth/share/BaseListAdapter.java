package com.purehero.bluetooth.share;

import android.app.Activity;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by purehero on 2017-05-16.
 */

public abstract class BaseListAdapter extends BaseAdapter implements Filterable, View.OnClickListener {
    private List<BaseListData> listData = new ArrayList<BaseListData>();    // 전체 데이터
    private List<BaseListData> filterData = new ArrayList<BaseListData>();  // 검색이 적용된 데이터

    protected Activity context;
    public BaseListAdapter(Activity context) {
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
        BaseListAdapter.ViewHolder viewHolder;
        if( view == null ) {
            viewHolder = new BaseListAdapter.ViewHolder();

            LayoutInflater inflater = ( LayoutInflater ) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            if( viewGroup instanceof ListView) {
                view  = inflater.inflate( R.layout.myfile_list_cell_layout, null );

                viewHolder.tvSubTitle 	    = (TextView)  view.findViewById( R.id.file_list_view_item_sub_title );
                viewHolder.tvDate 		    = (TextView)  view.findViewById( R.id.file_list_view_item_date );

            } else if( viewGroup instanceof GridView) {
                view  = inflater.inflate( R.layout.myfile_grid_cell_layout, null );

                viewHolder.tvSubTitle 	= null;
                viewHolder.tvDate 		= null;
            }

            viewHolder.cbSelected   = (CheckBox)  view.findViewById( R.id.file_list_view_item_checkbox );
            viewHolder.cbSelected.setOnClickListener( this );
            viewHolder.ivIcon 		= (ImageView) view.findViewById( R.id.file_list_view_item_icon );
            viewHolder.tvTitle 	    = (TextView)  view.findViewById( R.id.file_list_view_item_file_name );
            viewHolder.tvDuration     = (TextView)  view.findViewById( R.id.file_list_view_item_duration );

            view.setTag( viewHolder );
        } else {
            viewHolder = (BaseListAdapter.ViewHolder) view.getTag();
        }

        BaseListData data = ( BaseListData ) getItem( position );

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

        drawIcon( context, data, viewHolder.ivIcon );

        String playDuration = data.getPlayDuration();
        if( playDuration == null ) {
            viewHolder.tvDuration.setVisibility( View.GONE );
        } else {
            viewHolder.tvDuration.setVisibility( View.VISIBLE );
            viewHolder.tvDuration.setText( playDuration );
        }
        //Glide.with( context ).load( data.getFile()).centerCrop().placeholder( R.drawable.fl_ic_image ).into( viewHolder.ivIcon );
        return view;
    }

    protected abstract void drawIcon(Activity context, BaseListData data, ImageView ivIcon);

    @Override
    public void onClick(View view) {
        if( view instanceof CheckBox ) {
            int position = view.getId();
            BaseListData data = filterData.get( position );
            data.setSelected( !data.isSelected());
            context.invalidateOptionsMenu();
        }
    }

    class ViewHolder
    {
        public CheckBox cbSelected;
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvSubTitle;
        public TextView tvDate;
        public TextView tvDuration;
    }

    public abstract void setListDatas( List<BaseListData> listDatas );
    public void reload() {
        listData.clear();
        setListDatas( listData );

        filterData = new ArrayList<>( listData );
        sort();
    }

    public void sort() {
        Collections.sort( filterData, BaseListData.ALPHA_COMPARATOR );
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
            for( BaseListData data : filterData ) {
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
        for( BaseListData data : filterData ) {
            if( data.isSelected()) ++ ret;
        }
        return ret;
    }

    public List<BaseListData> getSelectedItems() {
        List<BaseListData> ret = new ArrayList<BaseListData>();

        for( BaseListData data : filterData ) {
            if( data.isSelected()) {
                ret.add( data );
            }
        }
        return ret;
    }

    public synchronized void setAllSelected( boolean bSelect ) {
        for( BaseListData data : filterData ) {
            data.setSelected( bSelect );
        }
    }

    @Override
    public Filter getFilter() {
        return new BaseListAdapter.ItemFilter();
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

            ArrayList<BaseListData> nlist = new ArrayList<BaseListData>();
            for (int i = 0; i < listData.size(); i++) {
                final BaseListData item = listData.get(i);

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
            filterData = (List<BaseListData>) results.values;

            sort();
        }
    }
}
