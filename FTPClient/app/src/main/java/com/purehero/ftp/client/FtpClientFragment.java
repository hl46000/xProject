package com.purehero.ftp.client;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.purehero.module.common.OnBackPressedListener;
import com.purehero.module.tabhost.FragmentEx;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by MY on 2017-03-11.
 */

public class FtpClientFragment extends Fragment implements OnBackPressedListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    private MainActivity context;
    protected FtpClientAdapter listAdapter = null;
    private View layout = null;
    private ListView listView = null;
    private LinearLayout pathList = null;
    private HorizontalScrollView pathScrollView = null;

    public FtpClientFragment setMainActivity( MainActivity activity ) {
        context = activity;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout 		= inflater.inflate( R.layout.ftp_file_list, container, false);
        if( layout == null ) return null;
        listView	= ( ListView ) layout.findViewById( R.id.listView );
        if( listView != null ) {
            pathList        = ( LinearLayout ) layout.findViewById( R.id.pathList ) ;
            pathScrollView = ( HorizontalScrollView ) layout.findViewById( R.id.pathScrollView ) ;

            listAdapter = new FtpClientAdapter( context );;

            listView.setAdapter( listAdapter );
            listView.setOnItemClickListener( this );
            listView.setOnItemLongClickListener( this );

            FloatingActionButton fab = (FloatingActionButton) layout.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent( context, FtpClientSettingsActivity.class);
                    startActivityForResult(intent, 100);
                }
            });
        }
        return layout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch( requestCode ) {
            case 100 :  // FTP Server 설정
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                final String strAddr  = sharedPref.getString( "tfp_server_addr", "ftp://192.168.123.130" );
                final String strPort  = sharedPref.getString( "tfp_server_port", "2345" );
                final String userID   = sharedPref.getString( "ftp_server_user_id", "Guest" );
                final String userPWD  = sharedPref.getString( "ftp_server_user_pwd", "1234" );

                listAdapter.init( strAddr, Integer.valueOf( strPort ) );

                new Thread( new Runnable(){
                    @Override
                    public void run() {
                        try {
                            if( listAdapter.login( userID, userPWD )) {
                                listAdapter.reload();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } ).start();
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        if( listAdapter.is_next_pop_folder()) {
            listAdapter.pop_folder( true );
            context.runOnUiThread( pathListUpdateRunnable );
            return true;
        }
        return false;
    }

    Handler pathScrollViewPosition = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            pathScrollView.fullScroll(ScrollView.FOCUS_RIGHT);
        }
    };
    Runnable pathListUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            pathList.removeAllViews();

            Vector<String> folder_names = listAdapter.getFolderNameVector();
            String name = null;

            for( int i = 0; i < folder_names.size(); i++ ) {
                name = folder_names.get(i);
                if( name.trim().isEmpty()) {
                    continue;
                }

                addPathList( name, i == folder_names.size() - 1 );
            }
            //G.Log(  "pathList.getChildCount() : %d", pathList.getChildCount());
            if( pathList.getChildCount() > 0 ) {
                pathScrollView.setVisibility( View.VISIBLE );
                pathScrollViewPosition.sendEmptyMessageDelayed( 100, 300 );
            } else {
                pathScrollView.setVisibility( View.GONE );
            }
        }
    };

    private void addPathList(String pathString, boolean isLastItem ) {
        TextView tv = new TextView( context );
        tv.setText(" / ");
        tv.setTextColor(Color.parseColor( "#CFCFFF" ));
        tv.setIncludeFontPadding( false );
        tv.setTextSize( TypedValue.COMPLEX_UNIT_SP, 18 );
        pathList.addView( tv );

        tv = new TextView( context );
        tv.setText(pathString);
        tv.setTextColor(Color.parseColor( "#CFCFFF" ));
        tv.setIncludeFontPadding( false );
        tv.setTextSize( TypedValue.COMPLEX_UNIT_SP, 18 );
        if( isLastItem ) {
            tv.setTypeface(null, Typeface.BOLD);
        }
        tv.setOnClickListener( pathListTextViewOnClickListener );
        pathList.addView( tv );
    }

    View.OnClickListener pathListTextViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextView tv = ( TextView ) view;
            String name = tv.getText().toString();

            /*
            while( name.compareToIgnoreCase( listAdapter.getLastFolderName()) != 0 ) {
                listAdapter.pop_folder( false );
            }
            reloadListView();
            */
            context.getSupportActionBar().setDisplayHomeAsUpEnabled(listAdapter.is_next_pop_folder());
        }
    };

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        final FtpClientData data = ( FtpClientData ) listAdapter.getItem( position );
        data.IncrementClickCount();

        if( listAdapter.isSelectMode()) {                                       // 파일 선택 모드일 경우
            data.setSelected( !data.isSelected());                              // 선택한 항목의 선택을 반전시킨다.
            listAdapter.notifyDataSetChanged();                                 // 리스트의 내용을 갱신시킨다.
            return;                                                             // 함수를 반환한다.
        }

        if( data.getFile().isDirectory()) {
            context.getSupportActionBar().setDisplayHomeAsUpEnabled( true );    // 액션바에 뒤로 가기 버튼을 표시한다.
            listAdapter.push_folder( data.getFile(), null);                     // 선택한 폴더로 리스트를 갱신시킨다.
            context.runOnUiThread( pathListUpdateRunnable );

        } else {
            /*
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_VIEW);
            sendIntent.setDataAndType( Uri.fromFile( data.getFile()), data.getMimeType());
            sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (sendIntent.resolveActivity( context.getPackageManager()) != null) {
                context.startActivity( Intent.createChooser( sendIntent, getString( R.string.choose_application )) );
            }
            */
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        final FtpClientData data = ( FtpClientData ) listAdapter.getItem( position );
        data.IncrementClickCount();

        listAdapter.setSelectMode( listAdapter.isSelectMode());
        listAdapter.notifyDataSetChanged();                 // 데이터가 변경되어 리스트를 갱신한다.
        return true;
    }


}
