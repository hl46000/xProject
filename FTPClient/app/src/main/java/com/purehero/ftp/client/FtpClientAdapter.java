package com.purehero.ftp.client;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by MY on 2017-03-11.
 */

public class FtpClientAdapter extends BaseAdapter implements Filterable {
    protected FTPClient ftpClient = null;
    protected List<FTPFile> listDatas = new ArrayList<FTPFile>();
    protected List<FTPFile> filteredDatas = new ArrayList<FTPFile>();
    private Activity context;

    String server = "192.168.123.141";
    int port = 2345;
    String id = "Guest";
    String password = "1234";

    public FtpClientAdapter( Activity context ) {
        this.context = context;
        if( ftpClient == null ) {
            ftpClient = new FTPClient();
        }
        ftpClient.setControlEncoding("utf-8"); // 한글파일명 때문에 디폴트 인코딩을 euc-kr로 합니다 ftpClient.connect("user.chollian.net"); // 천리안 FTP에 접속합니다
    }

    @Override
    public int getCount() {
        return filteredDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return filteredDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if( view == null ) {
            viewHolder = new ViewHolder();

            LayoutInflater inflater = ( LayoutInflater ) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            view  = inflater.inflate( R.layout.ftp_list_cell, null );

            viewHolder.cbSelected   = (CheckBox)  view.findViewById( R.id.file_list_view_item_checkbox );
            //viewHolder.cbSelected.setOnClickListener( this );
            viewHolder.ivIcon 		= (ImageView) view.findViewById( R.id.file_list_view_item_icon );
            viewHolder.tvTitle 	    = (TextView)  view.findViewById( R.id.file_list_view_item_file_name );
            viewHolder.tvSubTitle 	= (TextView)  view.findViewById( R.id.file_list_view_item_sub_title );
            viewHolder.tvDate 		= (TextView)  view.findViewById( R.id.file_list_view_item_date );

            view.setTag( viewHolder );
        } else {
            viewHolder = ( ViewHolder ) view.getTag();
        }


        FTPFile data = ( FTPFile ) getItem( position );

        viewHolder.tvTitle.setText( data.getName());
        /*
        viewHolder.tvTitle.setSelected( true );
        viewHolder.tvTitle.setHorizontallyScrolling( true );
        viewHolder.tvTitle.setMovementMethod(new ScrollingMovementMethod());
        */

        viewHolder.tvSubTitle.setVisibility( View.VISIBLE );
        if( data.isFile()) {
            viewHolder.tvSubTitle.setText( getFilesize( data.getSize()));
        } else {

        }
        viewHolder.tvDate.setVisibility( View.VISIBLE );
        viewHolder.tvDate.setText( data.getTimestamp().toString());

        if( isSelectMode()) {
            viewHolder.cbSelected.setVisibility( View.VISIBLE );
            //viewHolder.cbSelected.setChecked( data.isSelected() );
        } else {
            viewHolder.cbSelected.setVisibility( View.GONE );
            viewHolder.cbSelected.setChecked( false );
            //data.setSelected( false );
        }

        return view;
    }

    private boolean isSelectMode() {
        return false;
    }

    class ViewHolder
    {
        public CheckBox cbSelected;
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvSubTitle;
        public TextView tvDate;
    }

    @Override
    public Filter getFilter() {
        return new ItemFilter();
    }

    class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if ( constraint == null ) {
                results.values = listDatas;
                results.count = listDatas.size();
                return results;
            }

            String filterString = constraint.toString().toLowerCase();
            if ( filterString.length() <= 0) {
                results.values = listDatas;
                results.count = listDatas.size();
                return results;
            }

            ArrayList<FTPFile> nlist = new ArrayList<FTPFile>();
            for (int i = 0; i < listDatas.size(); i++) {
                final FTPFile item = listDatas.get(i);

                if (item.getName().toLowerCase().contains(filterString) ||
                        item.getName().toLowerCase().contains(filterString)) {
                    nlist.add( item );
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredDatas = (List<FTPFile>) results.values;
            sort();
        }
    }

    public synchronized void sort() {
        Collections.sort( filteredDatas, FTPFile_ALPHA_COMPARATOR );
        context.runOnUiThread( listDataUpdateRunnable );
    }

    Runnable listDataUpdateRunnable = new Runnable(){
        @Override
        public void run() {
            notifyDataSetChanged();
        }
    };

    public static final Comparator<FTPFile> FTPFile_ALPHA_COMPARATOR = new Comparator<FTPFile> () {
        @Override
        public int compare(FTPFile arg0, FTPFile arg1) {
            if( arg0.isDirectory() && !arg1.isDirectory() ) return -1;
            if( !arg0.isDirectory() && arg1.isDirectory() ) return  1;
            /*
            if( arg0.getClickCount() > arg1.getClickCount()) {
                return -1;
            } else if( arg0.getClickCount() < arg1.getClickCount() ) {
                return 1;
            }
            */
            return arg0.getName().compareToIgnoreCase( arg1.getName());
        }
    };

    public static final String getFilesize( long lsize ) {
        String result = "0 B";

        float size = lsize;
        if( size < 1024.0f ) {
            result = String.format( "%d B", (int)size );
        } else {
            size /= 1024.0f;
            if( size < 1024.0f ) {
                result = String.format( "%.2f KB", size );
            } else {
                size /= 1024.0f;
                if( size < 1024.0f ) {
                    result = String.format( "%.2f MB", size );
                } else {
                    size /= 1024.0f;
                    result = String.format( "%.2f GB", size );
                }
            }
        }

        return result;
    }

    public void init( String server, int port ) {
        this.server = server;
        this.port   = port;
    }

    // 계정과 패스워드로 로그인
    public boolean login(String user, String password) {
        try {
            this.connect();
            return ftpClient.login(user, password);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return false;
    }

    // 서버로부터 로그아웃
    private boolean logout() {
        try {
            return ftpClient.logout();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return false;
    }

    // 서버로 연결
    public void connect() {
        try {
            ftpClient.setDefaultPort( port );
            ftpClient.connect(server);
            int reply;
            // 연결 시도후, 성공했는지 응답 코드 확인
            reply = ftpClient.getReplyCode();
            if(!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                System.err.println("서버로부터 연결을 거부당했습니다");
                System.exit(1);
            }
        } catch (IOException ioe) {
            if(ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch(IOException f) {
                    //
                }
            }
            System.err.println("서버에 연결할 수 없습니다");
            System.exit(1);
        }
    }

    // FTP의 ls 명령, 모든 파일 리스트를 가져온다
    public synchronized void reload() throws IOException {
        FTPFile[] files = this.ftpClient.listFiles("/");

        listDatas.clear();
        filteredDatas.clear();

        for( FTPFile file : files ) {
            Log.d("MyLOG", file.toString()); // file.getName(), file.getSize() 등등.
            listDatas.add( file );
            filteredDatas.add( file );
        }

        sort();
    }

    // 파일을 전송 받는다
    public File get(String source, String target) {
        OutputStream output = null;
        try {
            File local = new File(source);
            output = new FileOutputStream(local);
        }
        catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
        File file = new File(source);
        try {
            if (ftpClient.retrieveFile(source, output)) {
                return file;
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    // 서버 디렉토리 이동
    public void cd(String path) {
        try {
            ftpClient.changeWorkingDirectory(path);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // 서버로부터 연결을 닫는다
    private void disconnect() {
        try {
            ftpClient.disconnect();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
