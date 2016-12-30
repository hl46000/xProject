package com.purehero.apk.manager;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ApkHistoryListAdapter extends CursorAdapter {
	public ApkHistoryListAdapter( Context context, ApkHistoryDB db ) {
		super( context, db.selectAll(), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER );
	}

	@Override
	public void bindView( View view, Context context, Cursor cursor ) {
		ViewHolder holder = ( ViewHolder ) view.getTag();
		if( holder == null ) {
			holder = new ViewHolder();
			view.setTag( holder );
			
			holder.icon 		= (ImageView) view.findViewById( R.id.apk_history_view_item_icon );
			holder.app_name 	= (TextView)  view.findViewById( R.id.apk_history_view_item_app_name );
			holder.action 		= (TextView)  view.findViewById( R.id.apk_history_view_item_action );
			holder.reg_time 	= (TextView)  view.findViewById( R.id.apk_history_view_item_date );
		}
		
		String package_name = cursor.getString( cursor.getColumnIndex("package_name"));
		String action 		= cursor.getString( cursor.getColumnIndex("action"));
		String reg_time 	= cursor.getString( cursor.getColumnIndex("reg_time"));
		
		File base_folder 	= new File( context.getCacheDir(), "package" );
		File folder			= new File( base_folder, package_name );
		File iconFile		= new File( folder, "icon" );
		try {
			holder.icon.setImageDrawable( BitmapDrawable.createFromPath( Uri.fromFile( iconFile ).getPath() ));
		} catch( Exception e ) {
		}
		
		String app_name		= cursor.getString( cursor.getColumnIndex("app_name"));
		if( app_name.compareTo( "unknown" ) == 0 ) {
			String f_app_name = G.readFile( new File( folder, "app_name"));
			if( f_app_name != null ) {
				app_name = f_app_name;
			}
		}
		
		holder.app_name.setText( app_name );
		holder.action.setText( checkNull( action ));
		holder.reg_time.setText( checkNull( reg_time ));
	}
	
	/**
	 * @param txt
	 * @return
	 */
	private String checkNull( String txt ) {
		return txt == null ? "null" : txt.trim();
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent ) {
		LayoutInflater inflater = LayoutInflater.from( context );
        View v = inflater.inflate( R.layout.apk_history_view_item, parent, false );
        return v;
	}

	
	
	@Override
	public boolean isEnabled(int position) {
		return false;
	}



	class ViewHolder 
	{
		public ImageView icon;
		public TextView app_name;
		public TextView action;
		public TextView reg_time;
	}
}
