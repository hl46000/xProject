package com.purehero.bluetooth.share;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;

import com.purehero.common.G;

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

public class ContactAdapter extends BaseAdapter implements Filterable, OnCheckedChangeListener {
	
	private final Context context;
	private List<ContactData> listDatas = new ArrayList<ContactData>();
	private List<ContactData> filteredData 	= new ArrayList<ContactData>();
	private boolean showCheckBox = false;
	
	public ContactAdapter( Context context ) {
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return filteredData.size();
	}

	@Override
	public Object getItem(int index) {
		return filteredData.get( index );
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
			Drawable icon = data.getIcon( context );
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
	
	public void getContactDatas() {
		final Uri CONTACT_URI = ContactsContract.Contacts.CONTENT_URI;
		
		listDatas.clear();
		
		ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query( CONTACT_URI, null, null, null, null );
        while( cursor.moveToNext()) {
        	try {
        		ContactData data = new ContactData( contactToString( context, cursor ));
				listDatas.add( data );
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
        
        Collections.sort( listDatas, ContactData.ALPHA_COMPARATOR );
        
        filteredData.clear();
        filteredData.addAll( listDatas );
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
            ContactAdapter.this.notifyDataSetChanged();
        }
    }
	
	private String contactToString( Context context, Cursor cursor ) {
		final String _ID 				= ContactsContract.Contacts._ID;
		final String DISPLAY_NAME 		= ContactsContract.Contacts.DISPLAY_NAME;
				
		final Uri PhoneCONTENT_URI 		= ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		final String Phone_CONTACT_ID 	= ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
		final String NUMBER 			= ContactsContract.CommonDataKinds.Phone.NUMBER;
		
		final Uri EmailCONTENT_URI 		=  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
		final String EmailCONTACT_ID 	= ContactsContract.CommonDataKinds.Email.CONTACT_ID;
		final String DATA 				= ContactsContract.CommonDataKinds.Email.DATA;

		final ContentResolver contentResolver = context.getContentResolver();
		String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
		
		StringBuilder ret = new StringBuilder("{");
		ret.append( String.format( "\"CONTACT_ID\":\"%s\"", contact_id ));
		ret.append( String.format( ",\"DISPLAY_NAME\":\"%s\"", cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ))));
				
		// Query and loop for every phone number of the contact
		Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);
		if( phoneCursor.getCount() > 0 ) {
			ret.append( ",\"PHONE_NUMBER\":[" ); 
			while (phoneCursor.moveToNext()) {
				ret.append( String.format( "\"%s\"", phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER))));
				if( !phoneCursor.isLast()) {
					ret.append( "," );
				}
			}
			ret.append( "]" );
		}
		phoneCursor.close();
		
		// Query and loop for every email of the contact
		Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);
		if( emailCursor.getCount() > 0 ) {
			ret.append( ",\"EMAIL\":[" );
			while (emailCursor.moveToNext()) {
				ret.append( String.format( "\"%s\"", emailCursor.getString(emailCursor.getColumnIndex(DATA))));
				if( !emailCursor.isLast()) {
					ret.append( "," );
				}
			}
			ret.append( "]" );
		}
		emailCursor.close();
		
		ret.append( "}" );
		return ret.toString();
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		ContactData data = filteredData.get( arg0.getId());
		data.setSelected( arg1 );
		G.Log( data.toString());
	}
}
