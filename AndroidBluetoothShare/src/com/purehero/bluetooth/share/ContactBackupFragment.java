package com.purehero.bluetooth.share;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.purehero.common.FragmentEx;

public class ContactBackupFragment extends FragmentEx {
	private final MainActivity context;
	private View layout = null;

	ListView listView = null;
	ContactBackupAdapter adapter = null;
	List<File> listDatas = new ArrayList<File>();
	
	public ContactBackupFragment(MainActivity mainActivity) {
		context = mainActivity; 
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layout 				= inflater.inflate( R.layout.contact_backup, container, false);
		
		listView = ( ListView ) layout.findViewById( R.id.listView );
		if( listView != null ) {
			adapter = new ContactBackupAdapter( context );
			listView.setAdapter( adapter );
		}
		
		return layout;
	}
	
	@Override
	public boolean onBackPressed() {
		if( adapter.isShowCheckBox()) {
			adapter.setShowCheckBox( false );
			return true;
		}
		
		return super.onBackPressed();
	}
}
