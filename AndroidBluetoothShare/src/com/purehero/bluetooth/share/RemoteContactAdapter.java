package com.purehero.bluetooth.share;

import java.util.ArrayList;
import java.util.List;

import com.purehero.contact.ContactData;

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

public class RemoteContactAdapter extends BaseAdapter implements Filterable {
	
	private final Context context;
	private List<ContactData> listDatas = new ArrayList<ContactData>();
	private List<ContactData> filteredData = new ArrayList<ContactData>();
	
	public RemoteContactAdapter( Context context ) {
		this.context = context;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if( convertView == null ) {
			viewHolder = new ViewHolder();
			
			LayoutInflater inflater = ( LayoutInflater ) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			convertView  = inflater.inflate( R.layout.remote_contact_list_cell, null );
			
			viewHolder.icon	= (ImageView) convertView.findViewById( R.id.contact_icon );
			viewHolder.name	= (TextView)  convertView.findViewById( R.id.contact_name );
			
			convertView.setTag( viewHolder );
		} else {
			viewHolder = ( ViewHolder ) convertView.getTag();
		}
		
		ContactData data = ( ContactData ) getItem( position );
		if( data != null ) {
			Drawable icon = data.getIcon();
			if( icon == null ) {
				viewHolder.icon.setImageResource( R.drawable.ic_contact );
			} else {
				viewHolder.icon.setImageDrawable( icon );				
			}
			
			viewHolder.name.setVisibility( View.VISIBLE );
			viewHolder.name.setText( data.getDisplayName());
			
		} else {
			viewHolder.icon.setVisibility( View.INVISIBLE );
			viewHolder.name.setText( "" );
		}
		
		return convertView;
	}
	
	class ViewHolder {
		public ImageView icon;
		public TextView name;		
	}

	private boolean showCheckBox = false;
	public boolean isShowCheckBox() {
		return showCheckBox;
	}
	public void setShowCheckBox( boolean show ) {
		showCheckBox = show;
		notifyDataSetChanged();
	}
	
	public int getCheckedCount() {
		int ret = 0;
		for( ContactData data : listDatas ) {
			if( data.isSelected()) ++ret;
		}
		return ret;
	}
	
	public void setAllChecked( boolean checked ) {
		for( ContactData data : listDatas ) {
			data.setSelected( checked );
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
        	results.values = listDatas;
            results.count = listDatas.size();
            
        	if ( constraint == null ) {
        		return results;
        	}
        	
        	String filterString = constraint.toString().toLowerCase();
        	if ( filterString.length() <= 0) {
        		return results;
        	}
        	
        	ArrayList<ContactData> nlist = new ArrayList<ContactData>();
            for (int i = 0; i < listDatas.size(); i++) {
            	final ContactData item = listDatas.get(i);
                
                if (item.getDisplayName().toLowerCase().contains(filterString) 	||   
                	item.getPhoneNumbers().toLowerCase().contains(filterString) || 
                	item.getEmails().toLowerCase().contains(filterString)) {
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
            filteredData = (List<ContactData>) results.values;
            notifyDataSetChanged();
        }
    }
}
