package com.purehero.bluetooth.share;

import java.io.IOException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Base64;

import com.purehero.common.Utils;

public class ContactUtils {
	public static String contactToString( Context context, Cursor cursor ) {
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
		
		InputStream is = null;
		try {
			is = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(contact_id)));
 
            if (is != null) {
            	byte icon_bytes[] = Utils.inputStreamToByteArray( is );
            	byte compressed_icon_bytes [] = Utils.compress( icon_bytes );
            	String base64IconString = Base64.encodeToString( compressed_icon_bytes, Base64.DEFAULT );
            	
            	ret.append( ",\"ICON\":" );
            	ret.append( String.format( "\"%s\"", base64IconString ));
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
		return ret.toString();
	}
}
