package com.purehero.root.checker;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DeviceInfoListAdapter extends BaseAdapter
{
	private final Context context;
	private List<DeviceInfoListData> listDatas = new ArrayList<DeviceInfoListData>(); 
	public DeviceInfoListAdapter( Context context ) {
		super();
		this.context = context;
		
		Map<String,String> infos = AndroidDeviceInfo.getInfo(context);
		Set<String> keys = infos.keySet();
		for( String key : keys ) {
			String value = infos.get( key );
			
			listDatas.add( new DeviceInfoListData( key, value ));
		}
		
		Collections.sort( listDatas, ALPHA_COMPARATOR );
	}
	
	@Override
	public int getCount() {
		return listDatas.size();
	}

	@Override
	public Object getItem(int index) {
		return listDatas.get(index);
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
			convertView  = inflater.inflate( R.layout.device_info_list_layout, null );
			
			viewHolder.name 	= (TextView)  convertView.findViewById( R.id.textName );
			viewHolder.value 	= (TextView)  convertView.findViewById( R.id.textValue );
			
			convertView.setTag( viewHolder );
		} else {
			viewHolder = ( ViewHolder ) convertView.getTag();
		}
		
		DeviceInfoListData data = listDatas.get( position );
		
		viewHolder.name.setText( data.getName());
		viewHolder.value.setText( data.getValue());
		
		return convertView;
	}
	
	class ViewHolder 
	{
		public TextView name;
		public TextView value;
	}
	
	/**
	 * DeviceInfoListData 의 list 을 정렬에 필요한 비교자
	 */
	private final Comparator<DeviceInfoListData> ALPHA_COMPARATOR = new Comparator<DeviceInfoListData> () {
		private final Collator collator = Collator.getInstance();
		
		@Override
		public int compare( DeviceInfoListData arg0, DeviceInfoListData arg1) {
			return collator.compare( arg0.getName(), arg1.getName());
		}
	};
}
