package com.purehero.bluetooth.share.contacts;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.purehero.bluetooth.share.G;
import com.purehero.bluetooth.share.MainActivity;
import com.purehero.bluetooth.share.R;
import com.purehero.module.common.CancelableProgressDialog;
import com.purehero.module.common.DialogUtils;
import com.purehero.module.common.ProgressRunnable;
import com.purehero.module.fragment.FragmentEx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactFragment extends FragmentEx implements OnItemClickListener, OnItemLongClickListener, View.OnClickListener {
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
		adapter = new ContactAdapter( context );

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
		context.showActionBarBackButton( true );

		layout 	= inflater.inflate( R.layout.contacts_layout, container, false );

		progressBar	= ( ProgressBar ) layout.findViewById( R.id.progressBar );
		listView	= ( ListView ) layout.findViewById( R.id.contactListView );
		gridView	= ( GridView ) layout.findViewById( R.id.contactGridView );

		progressBar.setVisibility( View.VISIBLE );
		listView.setVisibility( View.GONE );
		listView.setVisibility( View.GONE );

		new Thread( contacts_info_load_runnable ).start();
		return layout;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().invalidateOptionsMenu();
	}

	Runnable contacts_info_load_runnable = new Runnable() {
		@Override
		public void run() {
			adapter.getContactDatas();

			context.runOnUiThread( init_ui_runnable );
		}
	};

	Runnable init_ui_runnable = new Runnable() {

		@Override
		public void run() {
			G.Log( "runOnUiThread run" );

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
		}
	};

	@Override
	public boolean onBackPressed() {
		if( adapter.isSelectMode()) {
			adapter.setSelectMode( false );
			context.invalidateOptionsMenu();
			return true;
		}
		
		return super.onBackPressed();
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.contacts_option_menu, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		int selectedCount = adapter.getSelectedItemCount();

		MenuItem item = menu.findItem( R.id.contacts_action_select_mode );
		if( item != null ) {
			item.setVisible( !adapter.isSelectMode() );
		}

		item = menu.findItem( R.id.contacts_action_view_mode );
		if( item != null ) {
			if (view_layout_mode == VIEW_MODE_GRID) {
				item.setIcon(R.drawable.ic_format_list_bulleted_white_24dp);
			} else {
				item.setIcon(R.drawable.ic_view_module_white_24dp);
			}
		}

		item = menu.findItem( R.id.contacts_action_delete );
		if( item != null ) {
			item.setVisible( adapter.isSelectMode() && selectedCount > 0 );
		}

		item = menu.findItem( R.id.contacts_action_share );
		if( item != null ) {
			item.setVisible( adapter.isSelectMode() && selectedCount > 0 );
		}

		item = menu.findItem( R.id.contacts_action_bluetooth_share );
		if( item != null ) {
			item.setVisible( adapter.isSelectMode() && selectedCount > 0 );
		}

		/*
		item = menu.findItem( R.id.contacts_action_backup );
		if( item != null ) {
			item.setVisible( adapter.isSelectMode() && selectedCount > 0 );
		}
		*/
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if( id == R.id.contacts_action_view_mode ) {
			if (view_layout_mode == VIEW_MODE_LIST) {
				item.setIcon(R.drawable.ic_format_list_bulleted_white_24dp);
				view_layout_mode = VIEW_MODE_GRID;
			} else {
				item.setIcon(R.drawable.ic_view_module_white_24dp);
				view_layout_mode = VIEW_MODE_LIST;
			}
			context.runOnUiThread( init_ui_runnable );
			return true;

		} else if( id == R.id.contacts_action_bluetooth_share ) {
			contacts_bluetooth_share( adapter.getSelectedItem());
			return true;

		} else if( id == R.id.contacts_action_share ) {
			contacts_share( adapter.getSelectedItem());
			return true;

		} else if( id == R.id.contacts_action_select_mode ) {
			adapter.setSelectMode( true );
            context.invalidateOptionsMenu();
			return true;

		} else if( id == R.id.contacts_action_delete ) {
			contacts_deletes( adapter.getSelectedItem());
			return true;

			/*
		} else if( id == R.id.contacts_action_backup ) {
			contacts_backup( adapter.getSelectedItem());
			return true;
			*/
		}

		return super.onOptionsItemSelected(item);
	}

	private void contacts_backup(List<ContactData> selectedItem) {
		String xxxxx = selectedItem.size() == 1 ? String.format( "'%s'", selectedItem.get(0).getDisplayName()) : ""+selectedItem.size();
		String strTemp = context.getString( R.string.contacts_backup_info );
		strTemp = strTemp.replace( "xxxxx", xxxxx );
		DialogUtils.TextInputDialog( context, context.getString( R.string.contacts_backup ), strTemp, context.getString(R.string.contacts_enter_backup_name), 0, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if( which == DialogUtils.DIALOG_BUTTON_ID_YES ) {
					adapter.backupCheckedItems( DialogUtils.getTextInputDialogResult() );
				}
                        }
                    });
                }

            private void contacts_deletes( final List<ContactData> selectedItems) {
                final int selectedItemCount = selectedItems.size();
				String xxxxx = selectedItems.size() == 1 ? String.format( "'%s'", selectedItems.get(0).getDisplayName()) : ""+selectedItems.size();

                String alert_message = String.format( "%s %s", xxxxx, getString( R.string.contacts_delete_message ));

                DialogUtils.no_string_res 	= R.string.cancel;
                DialogUtils.yes_string_res	= R.string.delete;
                DialogUtils.confirmDialog( context, R.string.delete, alert_message, 0, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        if( which != DialogUtils.DIALOG_BUTTON_ID_YES ) return;

                        DialogUtils.progressDialog(context, R.string.delete, "", new ProgressRunnable() {
                            @Override
                            public void run(final CancelableProgressDialog cancelableProgressDialog) {
                                cancelableProgressDialog.setMax( selectedItemCount );

                                final String message_format = getString( R.string.delete_format );
                                int progress_count = 0;
                                for( final ContactData data : selectedItems ) {
                                    data.delete();
                                    cancelableProgressDialog.setProgress( ++ progress_count );

							context.runOnUiThread( new Runnable(){
								@Override
								public void run() {
									cancelableProgressDialog.setMessage( message_format.replace( "xxxxx", data.getDisplayName() ));
								}
							});
							try { Thread.sleep( 100 ); } catch (InterruptedException e) {}
						}

						new Thread( contacts_info_load_runnable ).start();
					}
				});
			}
		} );

	}

	private void contacts_share(List<ContactData> selectedItems) {
		File sharing_file = new File( context.getExternalCacheDir(), "sharing_file.vcf" );
		if( sharing_file.exists()) {
			sharing_file.delete();
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream( sharing_file );
			for( ContactData data : selectedItems ) {
				fos.write( data.readVCardString().getBytes());
			}
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return ;

		} finally {
			G.safe_close( fos );
		}

		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
		shareIntent.setType("*/*");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		}

		ArrayList<Uri> shareDatas = new ArrayList<Uri>();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			shareDatas.add(FileProvider.getUriForFile(context, "com.purehero.bluetooth.share.provider", sharing_file));
		} else {
			shareDatas.add( Uri.fromFile( sharing_file ));
		}
		shareIntent.putParcelableArrayListExtra( Intent.EXTRA_STREAM, shareDatas );

		String temp = selectedItems.size() == 1 ? String.format( "'%s'", selectedItems.get(0).getDisplayName()) : "" + selectedItems.size();
		startActivityForResult(Intent.createChooser(shareIntent, String.format( "Share %s Contacts via", temp )), 100 );
	}

	private void contacts_bluetooth_share(List<ContactData> selectedItems) {
		File sharing_file = new File( context.getExternalCacheDir(), "sharing_file.vcf" );
		if( sharing_file.exists()) {
			sharing_file.delete();
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream( sharing_file );
			for( ContactData data : selectedItems ) {
				fos.write( data.readVCardString().getBytes());
			}
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return ;

		} finally {
			G.safe_close( fos );
		}

		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
		shareIntent.setType("*/*");
		shareIntent.setPackage("com.android.bluetooth");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		}

		ArrayList<Uri> shareDatas = new ArrayList<Uri>();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			shareDatas.add(FileProvider.getUriForFile(context, "com.purehero.bluetooth.share.provider", sharing_file));
		} else {
			shareDatas.add( Uri.fromFile( sharing_file ));
		}
		shareIntent.putParcelableArrayListExtra( Intent.EXTRA_STREAM, shareDatas );

		//startActivityForResult(Intent.createChooser(shareIntent, "Share Contacts" ), 100 );
		try {
			startActivityForResult(shareIntent, 100);
		} catch( ActivityNotFoundException e ) {
			Toast.makeText( context,"No bluetooth share app found!",Toast.LENGTH_SHORT).show();
		}
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ContactData data = ( ContactData ) adapter.getItem( position );
		
		if( adapter.isSelectMode() ) {
			data.setSelected(!data.isSelected());
			adapter.notifyDataSetChanged();
			context.invalidateOptionsMenu();
			return;
		}

		ContactUtils.openDetailView( context, data.getContactID() );
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if( adapter.isSelectMode()) return true;
		return false;
	}
	
	// 메뉴 생성
	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		context.getMenuInflater().inflate(R.menu.contacts_context_menu, menu);
	}
	
	// 메뉴 클릭 
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		G.Log( "onContextItemSelected" );
		
		boolean ret = false;	// 메뉴의 처리 여부 

		// 클릭된 APK 정보
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		G.Log( "onContextItemSelected index : " + info.position );
		ContactData data = ( ContactData ) adapter.getItem( info.position );

		List<ContactData> datas = new ArrayList<ContactData>();
		datas.add( data );

		int id = item.getItemId();
		if( id == R.id.contacts_menu_delete ) {
			contacts_deletes( datas );
			ret = true;

		} else if( id == R.id.contacts_menu_bluetooth_share ) {
			contacts_bluetooth_share( datas );
			ret = true;

		} else if( id == R.id.contacts_menu_share ) {
			contacts_share( datas );
			ret = true;
		}

		return ret;
	}

	@Override
	public void onClick(View view) {
		int viewId = view.getId();
		if( viewId == R.id.refresh ) {
			progressBar.setVisibility( View.VISIBLE );
			listView.setVisibility( View.GONE );
			listView.setVisibility( View.GONE );

			new Thread( contacts_info_load_runnable ).start();
		}
	}
}
