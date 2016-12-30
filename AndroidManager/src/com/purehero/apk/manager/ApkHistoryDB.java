package com.purehero.apk.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ApkHistoryDB extends SQLiteOpenHelper {
	private final static String DB_FILENAME = "apk_history.db";
	private final static String TABLE_NAME 	= "APK_HISTORY";
	
	public ApkHistoryDB(Context context, int version) {
		super(context, DB_FILENAME, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL( String.format( "CREATE TABLE %s(", TABLE_NAME )
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "package_name TEXT, "
				+ "app_name TEXT, "
				+ "action TEXT, "
				+ "reg_time TEXT UNIQUE );");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion ) {
		String sql = String.format( "drop table if exists %s;", TABLE_NAME );
        db.execSQL(sql);
        onCreate(db); // 다시 테이블 생성
	}
	
	public long inset( String package_name, String app_name, String action, String reg_time ) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("package_name", package_name);
		values.put("app_name", app_name);
		values.put("action", action);
		values.put("reg_time", reg_time);
		
		long result = -1;
		try {
			result = db.insert( TABLE_NAME, null, values);
		} catch( Exception e ) {}
		
		return result;
	}
	
	public List<List<Object>> select( String sql, String[] args ) {
		List<List<Object>> result = new ArrayList<List<Object>>();
		
		// 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery( sql, args );
        if( c.getCount() == 0 ) return result;
        
        c.moveToFirst();
        while( c.moveToNext()) {
        	List<Object> col = new ArrayList<Object>();
        	
        	for( int i = 0; i < c.getColumnCount(); i++ ) {
        		switch( c.getType(i)) {
        		case Cursor.FIELD_TYPE_INTEGER 	: col.add( Integer.valueOf( c.getInt(i) )); break;
        		case Cursor.FIELD_TYPE_STRING 	: col.add( c.getString(i)); break;
        		}
        	}
        	result.add( col );
        }
     
        return result;
	}

	/**
	 * @return
	 */
	public Cursor selectAll() {
		SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery( String.format( "SELECT * FROM %s ORDER BY _id DESC", TABLE_NAME ), null );        
	}
}
