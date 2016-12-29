package com.purehero.apk.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ApkHistoryDB extends SQLiteOpenHelper {
	private final String TABLE_NAME = "APK_HISTORY";
	
	public ApkHistoryDB(Context context, String db_filename, CursorFactory factory, int version) {
		super(context, db_filename, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL( String.format( "CREATE TABLE %s(", TABLE_NAME )
				+ "seq INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "package_name TEXT, "
				+ "app_name TEXT, "
				+ "action TEXT, "
				+ "reg_time TEXT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion ) {
		String sql = String.format( "drop table if exists %s;", TABLE_NAME );
        db.execSQL(sql);
        onCreate(db); // 다시 테이블 생성
	}
	
	public long inset( String package_name, String app_name, String action ) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("package_name", package_name);
		values.put("app_name", app_name);
		values.put("action", action);
		values.put("reg_time", "datetime('now','localtime')");
		
		return db.insert( TABLE_NAME, null, values);
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
}
