package com.purehero.bithumb;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MY on 2017-08-26.
 */

public class TenSecondDBTable extends SQLiteOpenHelper {
    final String TABLE_NAME = "TEN_SECOND_DATA";

    public TenSecondDBTable( Context context, String name, int version) {
        super(context, name, null, version);
    }

    final int INDEX_CURRENCY        = 0;
    final int INDEX_TIME            = 1;
    final int INDEX_PRICE           = 2;

    public List<Map<String,Object>> selectData(String currency ) {
        String sqlSelect = "SELECT * FROM %s WHERE currency = '%s'" ;
        Cursor cursor = getReadableDatabase().rawQuery( String.format( sqlSelect, TABLE_NAME, currency ), null ) ;

        List<Map<String,Object>> ret = new ArrayList<Map<String,Object>>();
        if( !cursor.moveToFirst()) return null;
        while( cursor.moveToNext()) {
            Map<String,Object> data = new HashMap<String,Object>();

            data.put( cursor.getColumnName(INDEX_CURRENCY), cursor.getString(INDEX_CURRENCY));
            data.put( cursor.getColumnName(INDEX_TIME),     cursor.getString(INDEX_TIME));
            data.put( cursor.getColumnName(INDEX_PRICE),    cursor.getLong(INDEX_CURRENCY));

            ret.add(data);
        }

        return ret;
    }

    public void insertData( String currency, long time, long price ) {
        String sqlInsert = "INSERT OR REPLACE INTO %s (currency, time, price) VALUES ('%s', '%d', %d)" ;
        getWritableDatabase().execSQL( String.format( sqlInsert, TABLE_NAME, currency, time, price )) ;
    }

    public void deleteData( String currency ) {
        String sqlInsert = "DELETE FROM %s WHERE currency = '%s'" ;
        getWritableDatabase().execSQL( String.format( sqlInsert, TABLE_NAME, currency )) ;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlCreateTbl = "CREATE TABLE IF NOT EXIST %s (currency TEXT, time TEXT, price INTEGER)";
        sqLiteDatabase.execSQL( String.format( sqlCreateTbl, TABLE_NAME )) ;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate( sqLiteDatabase );
    }
}
