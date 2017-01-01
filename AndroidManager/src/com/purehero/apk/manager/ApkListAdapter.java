package com.purehero.apk.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class ApkListAdapter extends BaseAdapter implements Filterable
{
	private final Context context;
	private List<ApkListData> listData 		= new ArrayList<ApkListData>();
	private List<ApkListData> filteredData 	= new ArrayList<ApkListData>();
		
	public ApkListAdapter( Context context ) {
		super();
		this.context = context;
		
		PackageManager pm =  context.getPackageManager();
	    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
	    homeIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		    
	    List<ResolveInfo> launcherActivitys = pm.queryIntentActivities(homeIntent, PackageManager.GET_ACTIVITIES);
	    
	    listData.clear();
	    filteredData.clear();
	    
	    for( ResolveInfo act : launcherActivitys ) {
	    	ApkListData apkData = new ApkListData( context, act, pm );
	    	
	    	listData.add( apkData );
	    	filteredData.add( apkData );
	    }	    
	}
	
	public void remove( int index ) {
		ApkListData apkData = filteredData.remove( index );		
		dataChanged();
		
		listData.remove( apkData );
	}
	
	public void sort() {
		Collections.sort( filteredData, ApkListData.ALPHA_COMPARATOR );
		dataChanged();
	}
	
	/**
	 * 데이터가 갱신 되었음을 리스트에게 알림
	 */
	public void dataChanged() {
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return filteredData.size();
	}

	@Override
	public Object getItem(int index) {
		return filteredData.get(index);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent ) {
		ViewHolder viewHolder;
		if( convertView == null ) {
			viewHolder = new ViewHolder();
			
			LayoutInflater inflater = ( LayoutInflater ) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			convertView  = inflater.inflate( R.layout.apk_list_view_item, null );
			
			viewHolder.icon 		= (ImageView) convertView.findViewById( R.id.apk_list_view_item_icon );
			viewHolder.appName 		= (TextView)  convertView.findViewById( R.id.apk_list_view_item_app_name );
			viewHolder.packageName 	= (TextView)  convertView.findViewById( R.id.apk_list_view_item_package_name );
			
			convertView.setTag( viewHolder );
		} else {
			viewHolder = ( ViewHolder ) convertView.getTag();
		}
		
		ApkListData data = filteredData.get( position );
		
		viewHolder.icon.setImageDrawable( data.getIcon());
		viewHolder.appName.setText( data.getAppName());
		viewHolder.packageName.setText( data.getPackageName());
		
		return convertView;
	}
	
	class ViewHolder 
	{
		public ImageView icon;
		public TextView appName;
		public TextView packageName;
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
        	
        	ArrayList<ApkListData> nlist = new ArrayList<ApkListData>();
        	String filterableString ;
            for (int i = 0; i < listData.size(); i++) {
            	final ApkListData item = listData.get(i);
                
                if (item.getAppName().toLowerCase().contains(filterString) || 
                	item.getPackageName().toLowerCase().contains(filterString)) {
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
            filteredData = (List<ApkListData>) results.values;
            sort();            
        }
    }
}
