package share.file.purehero.com.fileshare;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.purehero.common.G;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by MY on 2017-02-25.
 */

public class FileListAdapter extends BaseAdapter implements Filterable {
    private List<FileListData> listData = new ArrayList<FileListData>();    // 전체 데이터
    private List<FileListData> filterData = new ArrayList<FileListData>();  // 검색이 적용된 데이터

    private final Context context;
    private File baseFolder;
    public FileListAdapter(Context context, File baseFolder ) {
        this.context = context;
        this.baseFolder = baseFolder;
    }

    @Override
    public int getCount() {
        G.Log("getCount : %d", filterData.size());
        return filterData.size();
    }

    @Override
    public Object getItem(int i) {
        return filterData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void sort() {
        Collections.sort( filterData, FileListData.ALPHA_COMPARATOR );
        notifyDataSetChanged();
    }

    public void reload(){
        listData.clear();
        filterData.clear();

        File subItems [] = baseFolder.listFiles();
        if( subItems != null ) {
            for( File file : subItems ) {
                FileListData data = new FileListData( context, file );

                filterData.add( data );
                listData.add( data );
            }
        }
        sort();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if( view == null ) {
            viewHolder = new ViewHolder();

            LayoutInflater inflater = ( LayoutInflater ) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            view  = inflater.inflate( R.layout.file_list_cell, null );

            viewHolder.ivIcon 		= (ImageView) view.findViewById( R.id.file_list_view_item_icon );
            viewHolder.tvTitle 	= (TextView)  view.findViewById( R.id.file_list_view_item_file_name );
            viewHolder.tvSubTitle 	= (TextView)  view.findViewById( R.id.file_list_view_item_sub_title );
            viewHolder.tvDate 		= (TextView)  view.findViewById( R.id.file_list_view_item_date );

            view.setTag( viewHolder );
        } else {
            viewHolder = ( ViewHolder ) view.getTag();
        }

        FileListData data = filterData.get( position );
        viewHolder.tvTitle.setText( data.getFilename());
        viewHolder.tvSubTitle.setVisibility( View.VISIBLE );
        viewHolder.tvSubTitle.setText( data.getSubTitle());
        viewHolder.tvDate.setVisibility( View.VISIBLE );
        viewHolder.tvDate.setText( data.getFileDate());

        Drawable icon = data.getIcon();
        if( icon == null ) {
            viewHolder.ivIcon.setVisibility( View.INVISIBLE );
        } else {
            viewHolder.ivIcon.setImageDrawable( icon );
            viewHolder.ivIcon.setVisibility( View.VISIBLE );
        }

        return view;
    }

    class ViewHolder
    {
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvSubTitle;
        public TextView tvDate;
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
