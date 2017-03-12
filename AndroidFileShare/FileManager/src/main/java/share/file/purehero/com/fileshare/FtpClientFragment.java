package share.file.purehero.com.fileshare;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.oroinc.net.ftp.FTPClient;
import com.oroinc.net.ftp.FTPFile;
import com.oroinc.net.ftp.FTPReply;
import com.purehero.common.FragmentEx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by MY on 2017-03-11.
 */

public class FtpClientFragment extends FragmentEx implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    private MainActivity context;
    protected FtpClientAdapter listAdapter = null;
    private View layout = null;
    private ListView listView = null;

    public FtpClientFragment setMainActivity( MainActivity activity ) {
        context = activity;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout 		= inflater.inflate( R.layout.file_list, container, false);
        if( layout == null ) return null;
        listView	= ( ListView ) layout.findViewById( R.id.listView );
        if( listView != null ) {
            listAdapter = new FtpClientAdapter( context );;

            listView.setAdapter( listAdapter );
            listView.setOnItemClickListener( this );
            listView.setOnItemLongClickListener( this );

            try {
                listAdapter.reload();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return layout;
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        return false;
    }


}
