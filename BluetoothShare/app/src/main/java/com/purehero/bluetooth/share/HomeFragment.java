package com.purehero.bluetooth.share;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
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
            }
        }
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
