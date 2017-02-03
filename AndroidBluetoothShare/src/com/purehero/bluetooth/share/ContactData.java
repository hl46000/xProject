package com.purehero.bluetooth.share;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;

import com.purehero.common.OrderingByKoreanEnglishNumbuerSpecial;

public class ContactData {

	private final JSONObject jobj;
	public ContactData( String json_string ) throws JSONException {
		jobj = new JSONObject( json_string );
		
		//G.Log( json_string );
	}
	
	
	
	@Override
	public String toString() {
		return String.format( "%s : %s", getDisplayName(), isSelected() ? "selected" : "");
	}



	private Drawable icon = null;
	public Drawable getIcon( Context context ) {
		if( icon != null ) return icon;
		
		InputStream inputStream = null;
		try {
            inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(getContactID())));
 
            if (inputStream != null) {
            	icon = Drawable.createFromStream( inputStream, "icon"); 
            }
        } catch (Exception e) {
            e.printStackTrace();
            
        } finally {
        	if( inputStream != null ) {
        		try {
					inputStream.close();
				} catch (IOException e) {
				}
        	}
        }
		return icon;
	}

	private int contect_id = -1;
	public int getContactID() {
		if( contect_id != -1 ) return contect_id;
		try {
			contect_id = jobj.getInt( "CONTACT_ID" );
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		return contect_id;
	}
	
	private boolean selected = false;
	public boolean isSelected() {
		return selected;
	}
	public void setSelected( boolean selected ) {
		this.selected = selected;
	}
	
	private String display_name = null;
	public String getDisplayName() {
		if( display_name != null ) return display_name;
		try {
			display_name = jobj.getString( "DISPLAY_NAME" );
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		return display_name;
	}
	
	private String phone_numbers = null;
	public String getPhoneNumbers() {
		if( phone_numbers != null ) return phone_numbers;
		
		if( jobj.has( "PHONE_NUMBER" )) {
			try {
				phone_numbers = jobj.getString( "PHONE_NUMBER" );
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			phone_numbers = "";
		}
		 
		return phone_numbers;
	}
	
	private String emails = null;
	public String getEmails() {
		if( emails != null ) return emails;
		if( jobj.has( "EMAIL" )) {
			try {
				emails = jobj.getString( "EMAIL" );
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			emails = "";
		}
		 
		return emails;
	}
	
	public void openDetailView( Context context ) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(getContactID()));
		intent.setData(uri);
		context.startActivity(intent);
	}
	
	/**
	 * ContactData 의 list 을 정렬에 필요한 비교자
	 */
	public static final Comparator<ContactData> ALPHA_COMPARATOR = new Comparator<ContactData> () {
		@Override
		public int compare(ContactData arg0, ContactData arg1) {
			return OrderingByKoreanEnglishNumbuerSpecial.compare( arg0.getDisplayName(), arg1.getDisplayName());
		}
	};
}
