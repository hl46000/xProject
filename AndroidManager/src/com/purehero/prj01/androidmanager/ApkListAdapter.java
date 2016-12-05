package com.purehero.prj01.androidmanager;

import java.util.ArrayList;
import java.util.Collections;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ApkListAdapter extends BaseAdapter 
{
	private final Context context;
	private ArrayList<ApkListData> listData = new ArrayList<ApkListData>();
	
	public ApkListAdapter( Context context ) 
	{
		super();
		this.context = context;
	}
	
	public ApkListData addItem( Drawable icon, String appName, String packageName ) 
	{
		ApkListData data = new ApkListData();
		data.icon = icon;
		data.appName = appName;
		data.packageName = packageName;
		
		return listData.add( data ) ? data : null;		
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
		if( data.icon != null ) {
			viewHolder.icon.setVisibility( View.VISIBLE );
			viewHolder.icon.setImageDrawable( data.icon );
		} else {
			viewHolder.icon.setVisibility( View.GONE );
		}
		
		viewHolder.appName.setText( data.appName );
		viewHolder.packageName.setText( data.packageName );
		
		return convertView;
	}
	
	class ViewHolder 
	{
		public ImageView icon;
		public TextView appName;
		public TextView packageName;
	}
}
