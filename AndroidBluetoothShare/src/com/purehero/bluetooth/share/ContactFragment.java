package com.purehero.bluetooth.share;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.purehero.common.G;

public class ContactFragment extends Fragment implements OnItemClickListener, OnItemLongClickListener {
	private final MainActivity context;
	private View layout = null;

	private ListView listView = null;
	private ProgressBar progressBar = null;
	private ContactAdapter adapter;
	
	public ContactFragment(MainActivity mainActivity) {
		context = mainActivity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		G.Log( "onCreateView" );
		
		layout 		= inflater.inflate( R.layout.contact_list, container, false);
		listView	= ( ListView ) layout.findViewById( R.id.listView );
		listView.setOnItemClickListener( this );
		listView.setOnItemLongClickListener( this );
		
		progressBar	= ( ProgressBar ) layout.findViewById( R.id.progressBar );
		
		new Thread( getContactRunnable ).start();
		
		return layout;
	}
	
	Runnable getContactRunnable = new Runnable() {
		@Override
		public void run() {
			G.Log( "run" );			
			if( adapter == null ) {
				adapter = new ContactAdapter( context );
				adapter.getContactDatas();
			}
			
			context.runOnUiThread( new Runnable(){
				@Override
				public void run() {
					G.Log( "runOnUiThread run" );
					listView.setAdapter( adapter );
					
					adapter.notifyDataSetChanged();
					progressBar.setVisibility( View.INVISIBLE );
					
					// 검색
					EditText apk_search = (EditText) layout.findViewById( R.id.txt_search );
					if( apk_search != null ) {
						apk_search.addTextChangedListener(new TextWatcher() {
					        @Override
					        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
					        	adapter.getFilter().filter(cs);
					        }
					        @Override
					        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) { }
					        @Override
					        public void afterTextChanged(Editable arg0) { }
					    });
					}
				}});
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ContactData data = ( ContactData ) adapter.getItem( position );
		data.openDetailView( context );
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		adapter.setShowCheckBox( !adapter.isShowCheckBox() );
		return true;
	}
}
