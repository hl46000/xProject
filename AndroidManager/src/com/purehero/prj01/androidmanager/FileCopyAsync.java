package com.purehero.prj01.androidmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class FileCopyAsync extends AsyncTask<File, String, String> 
{
	private final Context context;
	private final String appTitle;
	private File output_file = null;
	public FileCopyAsync( Context context, String appTitle ) {
		super();
		this.context = context;
		this.appTitle = appTitle;
	}

	private ProgressDialog progressDialog = null;
		
	@Override
	protected void onPreExecute() 
	{
		super.onPreExecute();
		
		progressDialog = new ProgressDialog( context );
		//progressDialog.setContentView( R.layout. );
		progressDialog.show();
	}

	@Override
	protected String doInBackground(File... files) 
	{
		output_file = files[1];
		
		copyFiles( files[0], files[1] );
		return null;
	}

	private void copyFiles(File src, File dest ) 
	{
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		try {
			fis = new FileInputStream( src );
			fos = new FileOutputStream( dest );
		
			int buff_size = fis.available();
			byte buff[] = new byte[ buff_size ];
			
			int nbytes = fis.read( buff, 0, buff_size );
			while( nbytes > 0 ) {
				fos.write( buff, 0, nbytes );
				nbytes = fis.read( buff, 0, buff_size );
			}
			fos.flush();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} finally {
			G.safe_close( fis );
			G.safe_close( fos );
		}
	}

	@Override
	protected void onProgressUpdate(String... values) 
	{
		progressDialog.setProgress( Integer.valueOf( values[0] ));
	}

	@Override
	protected void onPostExecute(String result) 
	{
		progressDialog.dismiss();
		
		String format = context.getResources().getString( R.string.send_to_sdcard_format );
		Toast.makeText( context, 
			String.format( format, appTitle, output_file.getAbsolutePath()), Toast.LENGTH_LONG ).show();
	}
}
