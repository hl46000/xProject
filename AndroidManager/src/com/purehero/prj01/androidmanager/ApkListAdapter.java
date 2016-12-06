package com.purehero.prj01.androidmanager;

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
import android.widget.ImageView;
import android.widget.TextView;

public class ApkListAdapter extends BaseAdapter 
{
	private final Context context;
	private List<ApkListData> listData = new ArrayList<ApkListData>();
	
	public ApkListAdapter( Context context ) 
	{
		super();
		this.context = context;
		
		PackageManager pm =  context.getPackageManager();
	    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
	    homeIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		    
	    List<ResolveInfo> launcherActivitys = pm.queryIntentActivities(homeIntent, PackageManager.GET_ACTIVITIES);
	    
	    listData.clear();
	    for( ResolveInfo act : launcherActivitys ) {
	    	listData.add( new ApkListData( act, pm ));
	    }
	}
	
	public void remove( int index ) 
	{
		listData.remove( index );
		dataChanged();
	}
	
	public void sort() 
	{
		Collections.sort( listData, ApkListData.ALPHA_COMPARATOR );
		dataChanged();
	}
	
	/**
	 * 데이터가 갱신 되었음을 리스트에게 알림
	 */
	public void dataChanged() 
	{
		notifyDataSetChanged();
	}

	@Override
	public int getCount() 
	{
		return listData.size();
	}

	@Override
	public Object getItem(int index) 
	{
		return listData.get(index);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent ) 
	{
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
		
		ApkListData data = listData.get( position );
		
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
}
