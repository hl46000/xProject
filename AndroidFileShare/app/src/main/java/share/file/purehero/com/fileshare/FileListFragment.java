package share.file.purehero.com.fileshare;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.purehero.common.FragmentEx;
import com.purehero.common.G;

import java.io.File;

/**
 * Created by MY on 2017-02-25.
 */

public class FileListFragment extends FragmentEx implements SearchTextChangeListener{
    private View layout = null;
    private ListView listView = null;
    private FileListAdapter listAdapter = null;
    private LinearLayout pathList = null;
    private HorizontalScrollView pathScrollView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        G.Log( "onCreateView" );

        layout 		= inflater.inflate( R.layout.file_list, container, false);
        if( layout == null ) return null;

        listView	= ( ListView ) layout.findViewById( R.id.listView );
        if( listView != null ) {
            pathList        = ( LinearLayout ) layout.findViewById( R.id.pathList ) ;
            pathScrollView = ( HorizontalScrollView ) layout.findViewById( R.id.pathScrollView ) ;

            addPathList("/");
            addPathList("acct"); addPathList("bcct"); addPathList("ccct");
            addPathList("dcct"); addPathList("ecct"); addPathList("fcct");
            addPathList("acct"); addPathList("bcct"); addPathList("ccct");
            addPathList("dcct"); addPathList("ecct"); addPathList("fcct");

            listAdapter = new FileListAdapter( getActivity(), new File("/") );;
            listView.setAdapter( listAdapter );

            listAdapter.reload();

            new Handler().postDelayed( new Runnable(){
                @Override
                public void run() {
                    getActivity().runOnUiThread( new Runnable(){
                        @Override
                        public void run() {
                            pathScrollView.fullScroll(ScrollView.FOCUS_RIGHT);
                        }
                    });
                }
            }, 1000 );
        }

        return layout;
    }

    private void addPathList(String pathString) {
        Button btn = new Button( getActivity());
        btn.setText( "〉 " + pathString);
        btn.setMinimumWidth(1);
        btn.setBackgroundResource( android.R.color.transparent );
        btn.setTextColor(Color.parseColor( "#CFCFFF" ));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT );
        params.setMargins(0,0,0,0);
        btn.setLayoutParams( params );
        btn.setPadding(0,0,0,0);

        //pathList.removeAllViews();
        pathList.addView( btn );
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        listAdapter.getFilter().filter(s);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        listAdapter.getFilter().filter(s);
        return true;
    }
}
