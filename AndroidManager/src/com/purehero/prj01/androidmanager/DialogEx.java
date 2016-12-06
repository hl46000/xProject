package com.purehero.prj01.androidmanager;

import android.app.Dialog;
import android.content.Context;

public class DialogEx {
	public Dialog createdDialog( Context context, int resID ) 
	{
		Dialog dlg = new Dialog(context);
		dlg.setContentView( resID );
	        
	    return dlg;
	}
}
