package com.purehero.contact;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.purehero.bluetooth.share.R;

public class ContactAdapter extends BaseAdapter 
	implements Filterable, OnCheckedChangeListener
{
	
	private final Activity context;
	private List<ContactData> listDatas 	= new ArrayList<ContactData>();
	private List<ContactData> filteredData 	= new ArrayList<ContactData>();
	private boolean showCheckBox = false;
	
	public ContactAdapter( Activity context ) {
		this.context = context;
	}
	
	/* 
	 * 필터링된( 검색어가 적용된 ) Contact의 개수를 반환한다. 
	 */
	@Override
	public synchronized int getCount() {
		return filteredData.size();
	}

	@Override
	public synchronized Object getItem(int index) {
		return filteredData.get( index );
	}

	@Override
	public synchronized long getItemId(int position) {
		return position;
	}
	
	/**
	 * Contact_id 값에 해당하는  ContactData 객체를 반환한다. 
	 * 
	 * @param contact_id
	 * @return
	 */
	public synchronized ContactData getItemByContactID(long contact_id) {
		for( ContactData data : listDatas ) {
			if( data.getContactID() == contact_id ) {
				return data;
			}
		}
		return null;
	}

	@Override
	public synchronized View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if( convertView == null ) {
			viewHolder = new ViewHolder();
			
			LayoutInflater inflater = ( LayoutInflater ) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			convertView  = inflater.inflate( R.layout.contact_list_cell, null );
			
			viewHolder.checkBox = ( CheckBox )convertView.findViewById( R.id.check_box );
			viewHolder.icon	= (ImageView) convertView.findViewById( R.id.contact_icon );
			viewHolder.name	= (TextView)  convertView.findViewById( R.id.contact_name );
		
			viewHolder.checkBox.setOnCheckedChangeListener( this );
			
			convertView.setTag( viewHolder );
		} else {
			viewHolder = ( ViewHolder ) convertView.getTag();
		}
		
		viewHolder.checkBox.setVisibility( showCheckBox ? View.VISIBLE : View.GONE );
		viewHolder.checkBox.setId( position );
		
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
			viewHolder.checkBox.setChecked( data.isSelected() );
			
		} else {
			viewHolder.icon.setVisibility( View.INVISIBLE );
			viewHolder.name.setText( "" );
			viewHolder.checkBox.setChecked( false );
		}
		
		return convertView;
	}

	class ViewHolder {
		public CheckBox checkBox;
		public ImageView icon;
		public TextView name;		
	}
	
	public synchronized void getContactDatas() {
		final Uri CONTACT_URI = ContactsContract.Contacts.CONTENT_URI;
		
		listDatas.clear();
		
		ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query( CONTACT_URI, null, null, null, null );
        if( cursor.getCount() > 0 ) {
        	while( cursor.moveToNext()) {
	        	try {
	        		ContactData data = new ContactData( context, cursor );
					listDatas.add( data );
				} catch (JSONException e) {
					e.printStackTrace();
				}
	        }
        }
        
        Collections.sort( listDatas, ContactData.ALPHA_COMPARATOR );
        
        filteredData.clear();
        filteredData.addAll( listDatas );
        
        new Thread( new Runnable(){
			@Override
			public void run() {
				for( ContactData data : listDatas ) {
					if( data.loadData( context )) {
						context.runOnUiThread( new Runnable(){
							@Override
							public void run() {
								ContactAdapter.this.notifyDataSetChanged();
							}});						
					}
				}
			}}).start();
	}
	
	public boolean isShowCheckBox() {
		return showCheckBox;
	}
	public void setShowCheckBox( boolean show ) {
		showCheckBox = show;
		notifyDataSetChanged();
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
	
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		ContactData data = filteredData.get( arg0.getId());
		data.setSelected( arg1 );
		//G.Log( data.toString());
	}

	public synchronized int getCheckedCount() {
		int ret = 0;
		for( ContactData data : filteredData ) {
			if( data.isSelected()) ++ret;
		}
		return ret;
	}
	
	public synchronized void setAllChecked( boolean checked ) {
		for( ContactData data : filteredData ) {
			data.setSelected( checked );
		}
	}

	/**
	 * 모든 Contact 을 표시할 수 있는 리스트 정보를 반환한다. ( 아이콘은 제외 )
	 * 
	 * @return
	 */
	public synchronized String getContactListDataALL() {
		StringBuilder ret = new StringBuilder("{\"CONTACTS\":[");
		
		long contact_id = 0;
		String display_name = "";
		
		int i, len = listDatas.size() - 1;
		for( i = 0; i < len; i++ ) {
			ContactData data = listDatas.get(i);
			contact_id 	 = data.getContactID();
			display_name = data.getDisplayName();
			
			ret.append( String.format( "{\"ID\":\"%s\",\"NAME\":\"%s\",\"HAS_ICON\":\"%s\"},", contact_id, display_name, data.getIcon()==null?"false":"true" ));
		}
		
		ContactData data = listDatas.get(i);
		contact_id 	 = data.getContactID();
		display_name = data.getDisplayName();
		
		ret.append( String.format( "{\"ID\":\"%s\",\"NAME\":\"%s\",\"HAS_ICON\":\"%s\"}]}", contact_id, display_name, data.getIcon()==null?"false":"true" ));
		return ret.toString();
	}

	public synchronized void deleteCheckedItems() {
		List<ContactData> deleteDatas 	= new ArrayList<ContactData>();
		for( ContactData data : listDatas ) {
			if( data.isSelected()) {
				deleteDatas.add( data );
				data.delete();
			}
		}
		
		listDatas.removeAll( deleteDatas );
		filteredData.removeAll( deleteDatas );
		
		context.runOnUiThread( 
				new Runnable(){
					@Override
					public void run() {
						setAllChecked( false );
						setShowCheckBox( false );					
					}
				}
			);
	}

	public void backupCheckedItems(String backupName ) {
		if( backupName.length() < 1 ) {
			Toast.makeText( context, context.getString(R.string.enter_backup_name), Toast.LENGTH_LONG ).show();
			return;
		}
		
		File backup_folder = new File( context.getString( R.string.backup_folder) );
		if( !backup_folder.exists()) {
			backup_folder.mkdirs();
		}
		File backup_file = new File( backup_folder, backupName + ".vcf" );
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream( backup_file );
			
			int count = 0;
			for( ContactData data : listDatas ) {
				if( data.isSelected()) {
					fos.write( data.readVCardString().getBytes() );
					++ count;
				}
			}
			
			String backup_result = context.getString( R.string.backup_result );
			backup_result = backup_result.replace( "xxxxx", String.valueOf(count));
			backup_result = backup_result.replace( "yyyyy", backupName );
			Toast.makeText( context, backup_result, Toast.LENGTH_LONG ).show();
			
			context.runOnUiThread( 
				new Runnable(){
					@Override
					public void run() {
						setAllChecked( false );
						setShowCheckBox( false );					
					}
				}
			);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText( context, String.format("'%s' 오류가 발생하였습니다.", e.getMessage()), Toast.LENGTH_LONG ).show();
			
		} finally {
			if( fos != null ) {
				try {
					fos.close();
				} catch (IOException e) {}
			}
		}
	}
}
