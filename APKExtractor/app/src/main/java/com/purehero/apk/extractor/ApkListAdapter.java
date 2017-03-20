package com.purehero.apk.extractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
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
		
	    listData.clear();
	    filteredData.clear();
	    
	    /*
	    List<PackageInfo> packageInfoList =          context.getPackageManager().getInstalledPackages(0);
	    for( PackageInfo pi : packageInfoList ) {
	    	ApkListData apkData = new ApkListData( context, pi, pm );
	    	filteredData.add( apkData );
	    	listData.add( apkData );
	    }
	    */
	    
	    List<ResolveInfo> launcherActivitys = pm.queryIntentActivities(homeIntent, PackageManager.GET_ACTIVITIES );
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
			viewHolder.versionName	= (TextView)  convertView.findViewById( R.id.apk_list_view_item_version_name );
			convertView.setTag( viewHolder );
		} else {
			viewHolder = ( ViewHolder ) convertView.getTag();
		}
		
		ApkListData data = filteredData.get( position );

		Drawable icon = data.getIcon();
		if( icon != null ) {
			viewHolder.icon.setImageDrawable( icon );
		}
		if( data.getClickCount() == 0 ) {
			viewHolder.appName.setText( data.getAppName());
		} else {
			viewHolder.appName.setText( String.format( "%s(%d)", data.getAppName(), data.getClickCount()));
		}
		viewHolder.appName.setSelected( true );
		
		viewHolder.packageName.setText( data.getPackageName());
		viewHolder.packageName.setSelected( true );
		
		String versionName = data.getVersionName();
		if( versionName == null ) {
			viewHolder.versionName.setVisibility( View.GONE );
		} else {
			viewHolder.versionName.setVisibility( View.VISIBLE );
			viewHolder.versionName.setText( versionName );
			viewHolder.versionName.setSelected( true );
		}
		
		return convertView;
	}
	
	class ViewHolder 
	{
		public ImageView icon;
		public TextView appName;
		public TextView packageName;
		public TextView versionName;
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
