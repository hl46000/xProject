package com.purehero.bluetooth.share.contacts;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.purehero.bluetooth.share.G;
import com.purehero.bluetooth.share.MainActivity;
import com.purehero.bluetooth.share.R;
import com.purehero.module.fragment.FragmentEx;

public class ContactFragment extends FragmentEx implements OnItemClickListener, OnItemLongClickListener, OnClickListener {
	final int VIEW_MODE_LIST = 0;
	final int VIEW_MODE_GRID = 1;

	private MainActivity context = null;
	private View layout = null;

	private ListView listView = null;
	private GridView gridView = null;
	private ProgressBar progressBar = null;
	private ContactAdapter adapter;

	int view_layout_mode = VIEW_MODE_LIST;

	public ContactFragment setMainActivity(MainActivity mainActivity) {
		context = mainActivity;

		return this;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		G.Log( "onCreateView" );

		// Fragment 가 option menu을 가지고 있음을 알림
		setHasOptionsMenu(true);

		// ActionBar Title 변경
		AppCompatActivity ACActivity = ( AppCompatActivity ) getActivity();
		ActionBar aBar = ACActivity.getSupportActionBar();
		if( aBar != null ) {
			aBar.setTitle( R.string.contact );
		}
		layout 	= inflater.inflate( R.layout.contacts_layout, container, false );

		progressBar	= ( ProgressBar ) layout.findViewById( R.id.progressBar );
		progressBar.setVisibility( View.VISIBLE );

		new Thread( getContactRunnable ).start();

		/*
		int btnIDs [] = { R.id.btnReload, R.id.btnBluetooth};
		for( int id : btnIDs ) {
			Button btn = ( Button ) layout.findViewById( id );
			if( btn != null ) {
				btn.setOnClickListener( this );
			}
		}
		*/
		
		return layout;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().invalidateOptionsMenu();
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

					listView	= ( ListView ) layout.findViewById( R.id.contactListView );
					gridView	= ( GridView ) layout.findViewById( R.id.contactGridView );

					if( view_layout_mode == VIEW_MODE_LIST ) {
						gridView.setVisibility( View.GONE );
						listView.setVisibility( View.VISIBLE );
						listView.setOnItemClickListener( ContactFragment.this );
						listView.setOnItemLongClickListener( ContactFragment.this );
						registerForContextMenu( listView );

						listView.setAdapter( adapter );

					} else {
						listView.setVisibility( View.GONE);
						gridView.setVisibility( View.VISIBLE );
						gridView.setOnItemClickListener( ContactFragment.this );
						gridView.setOnItemLongClickListener( ContactFragment.this );
						registerForContextMenu(gridView);

						gridView.setAdapter( adapter );
					}

					adapter.notifyDataSetChanged();
					progressBar.setVisibility( View.INVISIBLE );
					
					// 검색
					EditText search = (EditText) layout.findViewById( R.id.txt_search );
					if( search != null ) {
						search.addTextChangedListener(new TextWatcher() {
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
	public boolean onBackPressed() {
		if( adapter.isShowCheckBox()) {
			adapter.setAllChecked( false );
			adapter.setShowCheckBox( false );
			return true;
		}
		
		return super.onBackPressed();
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		switch (id ) {
			case R.id.action_view_mode :
				if( view_layout_mode == VIEW_MODE_LIST ) {
					item.setIcon( R.drawable.ic_view_headline_white_24dp);
					view_layout_mode = VIEW_MODE_GRID;
				} else {
					item.setIcon( R.drawable.ic_view_module_white_24dp);
					view_layout_mode = VIEW_MODE_LIST;
				}
				new Thread( getContactRunnable ).start();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.contacts_option_menu, menu);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ContactData data = ( ContactData ) adapter.getItem( position );
		
		if( adapter.isShowCheckBox() ) {
			data.setSelected( !data.isSelected() );
			adapter.notifyDataSetChanged();
		} else {		
			ContactUtils.openDetailView( context, data.getContactID() );
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if( adapter.isShowCheckBox()) {
			return false;			
		}
		ContactData data = ( ContactData ) adapter.getItem( position );
		data.setSelected( true );
		
		adapter.setShowCheckBox( !adapter.isShowCheckBox() );
		return true;
	}
	
	// 메뉴 생성
	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if ( v.getId() == R.id.listView ) {
			context.getMenuInflater().inflate(R.menu.contacts_context_menu, menu);
		}
	}
	
	// 메뉴 클릭 
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		G.Log( "onContextItemSelected" );
		
		boolean ret = false;	// 메뉴의 처리 여부 
		String strTemp;
		
		// 클릭된 APK 정보
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		G.Log( "onContextItemSelected index : " + info.position );
		
		switch( item.getItemId()) {
			/*
		case R.id.menu_delete :
			strTemp = context.getString( R.string.delete_info );
			strTemp = strTemp.replace( "xxxxx", String.valueOf( adapter.getCheckedCount() )); 
			G.confirmDialog( context, context.getString( R.string.delete ), strTemp, 0, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					switch( arg1 ) {
					case G.DIALOG_BUTTON_ID_YES :
						final String title 				= context.getString( R.string.delete );
						final String delete_format_msg 	= context.getString( R.string.delete_format );
						G.progressDialog( context, title, "", new ProgressRunnable(){

							@Override
							public void run(final ProgressDialog dialog) {
								dialog.setMax( adapter.getCheckedCount() );
								int count = 0;
								
								for( int i = 0; i < adapter.getCount(); i++ ) {
									final ContactData data = ( ContactData ) adapter.getItem(i);
									if( data.isSelected()) {
										data.delete();
										dialog.setProgress( ++count );
										
										context.runOnUiThread( new Runnable(){
											@Override
											public void run() {
												dialog.setMessage( delete_format_msg.replace( "xxxxx", data.getDisplayName() ));
												try {
													Thread.sleep( 50 );
												} catch (InterruptedException e) {
												}
											}});
										
										try {
											Thread.sleep( 50 );
										} catch (InterruptedException e) {
										}
									}
								}
								context.runOnUiThread( new Runnable(){
									@Override
									public void run() {
										adapter.setAllChecked( false );
										adapter.setShowCheckBox( false );										
									}});
							}});
						
						break;
					}
				}});			
			ret = true;
			break;
		case R.id.contacts_menu_backup_selected :
			strTemp = context.getString( R.string.backup_info );
			strTemp = strTemp.replace( "xxxxx", String.valueOf( adapter.getCheckedCount() ));
			G.textInputDialog( context, context.getString( R.string.backup ), strTemp,  
					context.getString(R.string.enter_backup_name), 0, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					switch( arg1 ) {
					case G.DIALOG_BUTTON_ID_YES :
						adapter.backupCheckedItems( G.getTextInputDialogResult() );
						break;
					}
				}});
			
			ret = true;	
			break;
			*/
		}
							
		return ret;
	}

	@Override
	public void onClick(View arg0) {
		switch( arg0.getId()) {
		case R.id.btnReload :
			adapter.getContactDatas();
			adapter.notifyDataSetChanged();
			break;

		}
	}
}
