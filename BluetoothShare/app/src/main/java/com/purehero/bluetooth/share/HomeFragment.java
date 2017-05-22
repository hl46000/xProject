package com.purehero.bluetooth.share;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.purehero.module.common.DialogUtils;
import com.purehero.module.fragment.FragmentEx;

import org.w3c.dom.Text;

/**
 * Created by MY on 2017-02-25.
 */
public class HomeFragment extends FragmentEx implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private MainActivity context = null;
    private View layout = null;
    private Switch bluetoothSwitch = null;
    private ImageView bluetoothStatusImage = null;
    private TextView bluetoothName = null;
    private BluetoothAdapter btAdapter = null;

    public HomeFragment setMainActivity(MainActivity mainActivity) {
        context = mainActivity;

        return this;
    }

    public void setBluetoothAdapter(BluetoothAdapter btAdapter) {
        this.btAdapter = btAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        G.Log( "onCreateView" );

        // Fragment 가 option menu을 가지고 있음을 알림
        setHasOptionsMenu(true);

        // ActionBar Title 변경
        AppCompatActivity ACActivity = ( AppCompatActivity ) getActivity();
        ActionBar aBar = ACActivity.getSupportActionBar();
        if( aBar != null ) {
            aBar.setTitle( R.string.app_name );
        }
        context.showActionBarBackButton( false );

        layout 	= inflater.inflate( R.layout.home_layout, container, false );

        int btnIDs[] = {R.id.btnApps, R.id.btnAudios, R.id.btnContacts, R.id.btnDocuments, R.id.btnDownloads, R.id.btnMyFiles, R.id.btnPhotos, R.id.btnVideos };
        for( int id : btnIDs ) {
            LinearLayout btn = (LinearLayout ) layout.findViewById( id );
            if( btn != null ) {
                btn.setOnClickListener( replaceFragmentOnClickListener );
            }
        }

        bluetoothSwitch = ( Switch ) layout.findViewById( R.id.switchBluetooth );
        bluetoothSwitch.setOnCheckedChangeListener( this );

        bluetoothName   = (TextView) layout.findViewById( R.id.btnBluetoothName);

        if( btAdapter != null ) {
            bluetoothStatusImage = (ImageView) layout.findViewById( R.id.btnOpenBluetoothAdmin );
            if( bluetoothStatusImage != null ) {
                bluetoothStatusImage.setOnClickListener( this );

                bluetoothStatusImage.setImageResource(btAdapter.isEnabled() ? R.drawable.ic_bluetooth_blue : R.drawable.ic_bluetooth_gray);
                bluetoothSwitch.setChecked( btAdapter.isEnabled() );
            }

            bluetoothName.setText( btAdapter.getName());
        } else {
            bluetoothSwitch.setEnabled( false );
            bluetoothName.setText( R.string.bluetooth_not_enable );
        }


        int textBtnIDs[] = { R.id.btnSwitchOff, R.id.btnSwitchOn, R.id.btnBluetoothName };
        for( int id : textBtnIDs ) {
            TextView btn = (TextView) layout.findViewById( id );
            if( btn != null ) {
                btn.setOnClickListener( this );
                btn.setEnabled( btAdapter != null );
            }
        }

        TextView tvAvailSize = ( TextView ) layout.findViewById( R.id.tvDiskSpace) ;
        if( tvAvailSize != null ) {
            StatFs externalStat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            StatFs internalStat = new StatFs(context.getFilesDir().getPath());

            long total_external_memory = 0, free_external_memory = 0;
            long total_internal_memory = 0, free_internal_memory = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                total_external_memory   = externalStat.getTotalBytes();        //return value is in bytes
                total_internal_memory   = internalStat.getTotalBytes();
                free_external_memory    = externalStat.getFreeBytes();         //return value is in bytes
                free_internal_memory    = internalStat.getFreeBytes();
            } else {
                total_external_memory = ( externalStat.getFreeBlocks() + externalStat.getBlockCount() ) * externalStat.getBlockSize(); //return value is in bytes
                total_internal_memory = ( internalStat.getFreeBlocks() + internalStat.getBlockCount() ) * internalStat.getBlockSize(); //return value is in bytes
                free_external_memory  = externalStat.getFreeBlocks() * externalStat.getBlockSize();     //return value is in bytes
                free_internal_memory  = internalStat.getFreeBlocks() * internalStat.getBlockSize();     //return value is in bytes
            }

            //One binary gigabyte equals 1,073,741,824 bytes.
            final double GBSyze = 1024.0f * 1024.0f * 1024.0f;
            tvAvailSize.setText( String.format( "%.2fGB / %.2fGB ", (double)free_external_memory / GBSyze, (double)total_external_memory / GBSyze));
            tvAvailSize.setText( String.format( "%.2fGB / %.2fGB ", (double)free_internal_memory / GBSyze, (double)total_internal_memory / GBSyze));
        }

        return layout ;
    }

    @Override
    public void onResume() {
        super.onResume();

        if( btAdapter != null ) {
            bluetoothStatusImage.setImageResource(btAdapter.isEnabled() ? R.drawable.ic_bluetooth_blue : R.drawable.ic_bluetooth_gray);
            bluetoothSwitch.setChecked(btAdapter.isEnabled());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == MainActivity.REQUEST_ENABLE_BT ) {
            if( btAdapter != null ) {
                bluetoothStatusImage.setImageResource(btAdapter.isEnabled() ? R.drawable.ic_bluetooth_blue : R.drawable.ic_bluetooth_gray);
                bluetoothSwitch.setChecked(btAdapter.isEnabled());
            }
        }
    }

    private View.OnClickListener replaceFragmentOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            int id = view.getId();
            context.replaceFragmentById( id );
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home_option_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_bluetooth_admin) {
            context.openBluetoothAdminPage();
            return true;

        } else if( id == R.id.action_bluetooth_identity ) {
            renameBluetoothDevice();
            return true;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if( id == R.id.btnOpenBluetoothAdmin ) {
            context.openBluetoothAdminPage();

        } else if( id == R.id.btnSwitchOff ) {
            bluetoothSwitch.setChecked( false );

        } else if( id == R.id.btnSwitchOn ) {
            bluetoothSwitch.setChecked( true );

        } else if( id == R.id.btnBluetoothName ) {
            if( btAdapter != null ) {
                if( btAdapter.isEnabled()) {
                    renameBluetoothDevice();
                }
            }
        }
    }

    private void renameBluetoothDevice() {
        DialogUtils.yes_string_res = R.string.ok;
        DialogUtils.no_string_res = R.string.cancel;
        DialogUtils.TextInputDialog(context, R.string.rename_device, R.string.device_name_change_contents, btAdapter.getName(), 0, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if( i == DialogUtils.DIALOG_BUTTON_ID_YES ) {
                    String result = DialogUtils.getTextInputDialogResult();
                    if( btAdapter != null ) {
                        btAdapter.setName( result );
                        bluetoothName.setText( btAdapter.getName() );
                    }
                }
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if( btAdapter != null ) {
            if( b ) btAdapter.enable();
            else    btAdapter.disable();

            bluetoothName.setText( btAdapter.getName() );
        }
        bluetoothStatusImage.setImageResource( b ? R.drawable.ic_bluetooth_blue : R.drawable.ic_bluetooth_gray);
    }
}
