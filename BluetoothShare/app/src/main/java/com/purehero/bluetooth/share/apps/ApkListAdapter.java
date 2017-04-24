package com.purehero.bluetooth.share.apps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.purehero.bluetooth.share.G;
import com.purehero.bluetooth.share.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApkListAdapter extends BaseAdapter implements Filterable, CompoundButton.OnCheckedChangeListener, View.OnClickListener {
	private final Context context;
	private List<ApkListData> listData 		= new ArrayList<ApkListData>();
	private List<ApkListData> filteredData 	= new ArrayList<ApkListData>();
		
	public ApkListAdapter( Context context ) {
        super();
        this.context = context;
    }

    public void loadApps() {
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

	boolean bSelectMode = false;
	public boolean isSelectMode() {
		return bSelectMode;
	}
	public void setSelectMode( boolean bMode ) {
		boolean bChanged = bSelectMode != bMode;

		bSelectMode = bMode;
		if( bChanged ) {
            for( ApkListData data : filteredData ) {
                data.setSelected( false );
            }
			notifyDataSetChanged();
		}
	}

	public int getSelectedItemCount() {
        int ret = 0;
        for( ApkListData data : filteredData ) {
            if( data.isSelected()) ++ ret;
        }
        return ret;
    }

	public List<ApkListData> getSelectedItem() {
		List<ApkListData> ret = new ArrayList<ApkListData>();

		for( ApkListData data : filteredData ) {
			if( data.isSelected()) {
				ret.add( data );
			}
		}
		return ret;
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
		if( parent instanceof ListView ) {
			return getViewForListView( position, convertView, parent );

		} else if( parent instanceof GridView ) {
			return getViewForGridView( position, convertView, parent );

		} else {
			G.Log( "ViewGroup parent" );
		}
		return null;
	}

	private View getViewForGridView(int position, View convertView, ViewGroup parent) {
		G.Log( "parent instanceof GridView" );

		ViewHolder viewHolder;
		if( convertView == null ) {
			viewHolder = new ViewHolder();

			LayoutInflater inflater = ( LayoutInflater ) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			convertView  = inflater.inflate( R.layout.apps_grid_cell_layout, null );

			viewHolder.check		= (CheckBox)  convertView.findViewById( R.id.apps_cell_check_box );
			viewHolder.icon 		= (ImageView) convertView.findViewById( R.id.apps_cell_icon );
			viewHolder.appName 	= (TextView)  convertView.findViewById( R.id.apps_cell_app_name );

			viewHolder.check.setOnClickListener( this );

			convertView.setTag( viewHolder );
		} else {
			viewHolder = ( ViewHolder ) convertView.getTag();
		}

		ApkListData data = filteredData.get( position );

		Drawable icon = data.getIcon();
		if( icon != null ) {
			viewHolder.icon.setImageDrawable( icon );
		}
		viewHolder.appName.setText( data.getAppName());
		viewHolder.appName.setSelected( true );

		viewHolder.check.setVisibility( isSelectMode() ? View.VISIBLE : View.GONE );
		viewHolder.check.setChecked( data.isSelected() );
		viewHolder.check.setTag( data );

		return convertView;
	}

	private View getViewForListView(int position, View convertView, ViewGroup parent) {
		G.Log( "getViewForListView" );

		ViewHolder viewHolder;
		if( convertView == null ) {
			viewHolder = new ViewHolder();

			LayoutInflater inflater = ( LayoutInflater ) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			convertView  = inflater.inflate( R.layout.apps_list_cell_layout, null );

			viewHolder.check		= (CheckBox)  convertView.findViewById( R.id.apps_cell_check_box );
			viewHolder.icon 		= (ImageView) convertView.findViewById( R.id.apps_cell_icon );
			viewHolder.appName 		= (TextView)  convertView.findViewById( R.id.apps_cell_app_name );
			viewHolder.packageName 	= (TextView)  convertView.findViewById( R.id.apps_cell_package_name );
			viewHolder.versionName	= (TextView)  convertView.findViewById( R.id.apps_cell_version_name );

			viewHolder.check.setOnClickListener( this );

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

		viewHolder.check.setVisibility( isSelectMode() ? View.VISIBLE : View.GONE );
		viewHolder.check.setChecked( data.isSelected() );
		viewHolder.check.setTag( data );

		return convertView;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		ApkListData data = ( ApkListData ) buttonView.getTag();
		data.setSelected( isChecked );
	}

	@Override
	public void onClick(View v) {
		if( v instanceof CheckBox ) {
			ApkListData data = (ApkListData) v.getTag();
			CheckBox cb = ( CheckBox ) v;

			data.setSelected( cb.isChecked());
		}
	}

	class ViewHolder
	{
		public CheckBox check;
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
