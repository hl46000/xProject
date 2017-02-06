package com.purehero.contact;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

import com.purehero.common.G;
import com.purehero.common.OrderingByKoreanEnglishNumbuerSpecial;
import com.purehero.common.Utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.content.Loader;
import android.support.v4.content.Loader.OnLoadCompleteListener;
import android.util.Base64;

public class ContactData implements OnLoadCompleteListener<Cursor> {
	private static final String _ID 				= ContactsContract.Contacts._ID;
	private static final String DISPLAY_NAME 		= ContactsContract.Contacts.DISPLAY_NAME;
			
	private static final Uri PhoneCONTENT_URI 		= ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
	private static final String Phone_CONTACT_ID 	= ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
	private static final String NUMBER 				= ContactsContract.CommonDataKinds.Phone.NUMBER;
	
	private static final Uri EmailCONTENT_URI 		= ContactsContract.CommonDataKinds.Email.CONTENT_URI;
	private static final String EmailCONTACT_ID 	= ContactsContract.CommonDataKinds.Email.CONTACT_ID;
	private static final String DATA 				= ContactsContract.CommonDataKinds.Email.DATA;
	
	private static final Uri AddressCONTENT_URI 	= ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
	private static final String AddressCONTACT_ID 	= ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID;
	private static final String ADDRESS				= ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS;
	
	class ContactDataSource {
		final String name;
		final Uri uri;
		final String data;
		public ContactDataSource( String name, Uri uri, String data ) {
			this.name = name;
			this.uri  = uri;
			this.data = data;
		}
		public Uri getUri() { return uri; }
		public String getData(){ return data; };
	}
	
	private final JSONObject jobj;
	public ContactData( Context context, Cursor cursor ) throws JSONException {
		final ContentResolver contentResolver = context.getContentResolver();
		contact_id = cursor.getLong(cursor.getColumnIndex( _ID ));
		
		StringBuilder ret = new StringBuilder("{");
		ret.append( String.format( "\"CONTACT_ID\":\"%d\"", contact_id ));
		ret.append( String.format( ",\"DISPLAY_NAME\":\"%s\"", cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ))));
				
		String phoneNumbers = readPhoneNumbers( contentResolver, contact_id );
		if( phoneNumbers != null ) {
			ret.append( "," );
			ret.append( phoneNumbers );
		}
		
		String emails = readEmails( contentResolver, contact_id );
		if( emails != null ) {
			ret.append( "," );
			ret.append( emails );
		}
		
		String address = readAddress( contentResolver, contact_id );
		if( address != null ) {
			ret.append( "," );
			ret.append( address );
		}
		
		String vcardString = readVCardString( contentResolver, cursor );
		if( vcardString != null ) {
			G.Log( "[vcfString] %s", vcardString );
		}
				
		InputStream is = null;
		try {
			is = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(contact_id)));
 
            if (is != null) {
            	byte icon_bytes[] = Utils.inputStreamToByteArray( is );
            	is.close();
            	
            	byte compressed_icon_bytes [] = Utils.compress( icon_bytes );
            	String base64IconString = Base64.encodeToString( compressed_icon_bytes, Base64.DEFAULT );
            	
            	ret.append( ",\"ICON\":" );
            	ret.append( String.format( "\"%s\"", base64IconString ));
            	
            	is = Utils.byteArrayToInputStream( icon_bytes );
            	icon = Drawable.createFromStream( is, "icon");
            }
        } catch (Exception e) {
        } finally {
        	if( is != null ) {
        		try {
        			is.close();
				} catch (IOException e) {
				}
        	}
        }
		
		ret.append( "}" );
		String ret_value = ret.toString();
		
		jobj = new JSONObject( ret_value );
		G.Log( "%s : %d byte", getDisplayName(), ret_value.getBytes().length );
		//G.Log( ret_value ); 
	}
	
	private String readEmails( ContentResolver contentResolver, long contactID ) {
		StringBuilder ret = null;
		// Query and loop for every email of the contact
		Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID+ " = ?", new String[] { String.valueOf( contact_id )}, null);
		if( emailCursor.getCount() > 0 ) {
			ret = new StringBuilder( "\"EMAIL\":[" ); 
			while (emailCursor.moveToNext()) {
				String emailAddress = emailCursor.getString(emailCursor.getColumnIndex(DATA));
				int emailType = emailCursor.getInt(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE ));
				switch( emailType ) {
				case ContactsContract.CommonDataKinds.Email.TYPE_HOME 	:
					ret.append( String.format( "\"HOME:%s\"", emailAddress)); 
					break;
				case ContactsContract.CommonDataKinds.Email.TYPE_MOBILE :
					ret.append( String.format( "\"MOBILE:%s\"", emailAddress));
					break;
				case ContactsContract.CommonDataKinds.Email.TYPE_WORK :
					ret.append( String.format( "\"WORK:%s\"", emailAddress));
					break;
				case ContactsContract.CommonDataKinds.Email.TYPE_OTHER :
				default :
					ret.append( String.format( "\"OTHER:%s\"", emailAddress));
					break;
				}				
				if( !emailCursor.isLast()) {
					ret.append( "," );
				}
			}
			ret.append( "]" );
		}
		emailCursor.close();
		return ret == null ? null : ret.toString(); 
	}
	
	private String readPhoneNumbers( ContentResolver contentResolver, long contactID ) {
		StringBuilder ret = null;
		// Query and loop for every phone number of the contact
		Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { String.valueOf( contact_id )}, null);
		if( phoneCursor.getCount() > 0 ) {
			ret = new StringBuilder( "\"PHONE_NUMBER\":[" ); 
			while (phoneCursor.moveToNext()) {
				int phoneType = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
				String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
				switch (phoneType) {
                case Phone.TYPE_MOBILE:
                	ret.append( String.format( "\"MOBILE:%s\"", phoneNumber));
                	break;
                case Phone.TYPE_HOME:
                	ret.append( String.format( "\"HOME:%s\"", phoneNumber));
                    break;
                case Phone.TYPE_WORK:
                	ret.append( String.format( "\"WORK:%s\"", phoneNumber));
                    break;
                case Phone.TYPE_OTHER:
                default:
                	ret.append( String.format( "\"OTHER:%s\"", phoneNumber));
                    break;
				}
				
				if( !phoneCursor.isLast()) {
					ret.append( "," );
				}
			}
			ret.append( "]" );
		}
		phoneCursor.close();
		return ret == null ? null : ret.toString(); 
	}
	
	
	private String readAddress( ContentResolver contentResolver, long contactID ) {
		StringBuilder ret = null;
		// Query and loop for every phone number of the contact
		Cursor addressCursor = contentResolver.query(AddressCONTENT_URI, null, AddressCONTACT_ID + " = ?", new String[] { String.valueOf( contact_id )}, null);
		if( addressCursor.getCount() > 0 ) {
			ret = new StringBuilder( "\"ADDRESS\":[" ); 
			while (addressCursor.moveToNext()) {
				int phoneType = addressCursor.getInt(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
				String phoneNumber = addressCursor.getString(addressCursor.getColumnIndex(ADDRESS));
				switch (phoneType) {
                case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME:
                	ret.append( String.format( "\"HOME:%s\"", phoneNumber));
                	break;
                case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK:
                	ret.append( String.format( "\"WORK:%s\"", phoneNumber));
                    break;
                case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER:
                default:
                	ret.append( String.format( "\"OTHER:%s\"", phoneNumber));
                    break;
				}
				
				if( !addressCursor.isLast()) {
					ret.append( "," );
				}
			}
			ret.append( "]" );
		}
		addressCursor.close();
		return ret == null ? null : ret.toString(); 
	}
	
	private String readVCardString( ContentResolver contentResolver, Cursor cursor ) {
		String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
        
        AssetFileDescriptor fd;
        FileInputStream fis = null;
    	try {
            fd = contentResolver.openAssetFileDescriptor(uri, "r");
            fis = fd.createInputStream();
            
            byte [] buffer = Utils.inputStreamToByteArray( fis );
            return new String( buffer );
            
    	} catch( Exception e ) {
    		if( fis != null ) {
    			try {
					fis.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
    		}
    	}
        
		return null;
	}
	
	@Override
	public String toString() {
		return String.format( 
				"%s : %s", 
				getDisplayName(),
				isSelected() ? "selected" : "");
	}



	private Drawable icon = null;
	public Drawable getIcon() {
		if( icon != null ) return icon;
		
		InputStream inputStream = null;
		try {
			if( jobj.has( "ICON" )) {
				String b64IconString = jobj.getString("ICON");
				byte compressed_icon_bytes [] = Base64.decode( b64IconString, Base64.DEFAULT );
				byte icon_bytes [] = Utils.decompress( compressed_icon_bytes );
				inputStream = Utils.byteArrayToInputStream( icon_bytes );
			}
			
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

	private long contact_id = -1;
	public long getContactID() {
		if( contact_id != -1 ) return contact_id;
		try {
			contact_id = jobj.getLong( "CONTACT_ID" );
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		return contact_id;
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
	
	@Override
	public void onLoadComplete( Loader<Cursor> loader, Cursor cursor ) {
		switch (loader.getId()) {
		
		}
	}
}
