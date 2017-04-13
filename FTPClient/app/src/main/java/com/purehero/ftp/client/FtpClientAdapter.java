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

import com.bumptech.glide.Glide;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

/**
 * Created by MY on 2017-03-11.
 */

public class FtpClientAdapter extends BaseAdapter implements Filterable {
    public static final String DATE_FORMAT = "MM/dd/yy H:mm a";
    SimpleDateFormat dataFormat = new SimpleDateFormat(DATE_FORMAT);

    protected FTPClient ftpClient = null;
    protected List<FtpClientData> listDatas = new ArrayList<FtpClientData>();
    protected List<FtpClientData> filteredDatas = new ArrayList<FtpClientData>();
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


        FtpClientData data = ( FtpClientData ) getItem( position );

        viewHolder.tvTitle.setText( data.getFilename());
        viewHolder.tvSubTitle.setVisibility( View.VISIBLE );
        viewHolder.tvSubTitle.setText( data.getSubTitle());
        viewHolder.tvDate.setVisibility( View.VISIBLE );
        viewHolder.tvDate.setText( data.getFileDate());

        if( isSelectMode()) {
            viewHolder.cbSelected.setVisibility( View.VISIBLE );
            viewHolder.cbSelected.setChecked( data.isSelected() );
        } else {
            viewHolder.cbSelected.setVisibility( View.GONE );
            viewHolder.cbSelected.setChecked( false );
            data.setSelected( false );
        }

        int res_id = getImageResourceID( data );
        Glide.with( context ).load( res_id ).into( viewHolder.ivIcon );

        return view;
    }

    /**
     *
     * @param data
     * @return
     */
    public int getImageResourceID( FtpClientData data) {
        int res_id = -1;

        String mimeType = data.getMimeType();

        if( data.getFile().isDirectory() ) {
            return R.drawable.folder2;
        } else {
            if( mimeType != null ) {
                if (mimeType.startsWith("image")) {
                    res_id = R.drawable.image;
                } else if (mimeType.startsWith("audio")) {
                    res_id = R.drawable.music;
                } else if (mimeType.startsWith("video")) {
                    res_id = R.drawable.movies;
                } else if (mimeType.endsWith("zip")) {
                    res_id = R.drawable.zip;
                } else if (mimeType.endsWith("excel")) {
                    res_id = R.drawable.excel;
                } else if (mimeType.endsWith("powerpoint")) {
                    res_id = R.drawable.ppt;
                } else if (mimeType.endsWith("word")) {
                    res_id = R.drawable.word;
                } else if (mimeType.endsWith("pdf")) {
                    res_id = R.drawable.pdf;
                } else if (mimeType.endsWith("xml")) {
                    res_id = R.drawable.xml32;
                } else if (mimeType.endsWith("vnd.android.package-archive")) {  // APK
                    res_id = R.drawable.apk;
                } else if (mimeType.endsWith("torrent")) {  // APK
                    res_id = R.drawable.torrent;
                } else {// torrent
                    // text 로 간주
                    res_id = R.drawable.text;
                }
            } else {
                // text 로 간주
                res_id = R.drawable.text;
            }
        }

        return res_id;
    }

    boolean bSelectedMode = false;
    public boolean isSelectMode() {
        return bSelectedMode;
    }
    public void setSelectMode(boolean selectMode) {
        bSelectedMode = selectMode;
    }

    private Vector<String> folder_name_stack = new Vector<String>();
    private Vector<FTPFile> folder_stack = new Vector<FTPFile>();
    public synchronized Vector<String> getFolderNameVector() { return folder_name_stack; }
    public synchronized Vector<FTPFile> getFolderVector() { return folder_stack; }
    public void push_folder( final FTPFile file, Object o) {
        folder_stack.add( file );
        folder_name_stack.add( file.getName());

        new Thread( new Runnable(){
            @Override
            public void run() {
                try {
                    cd( file.getName());
                    reload();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /*
    * return : 다음에도 pop 이 가능한지를 반환한다.
    * */
    public synchronized boolean pop_folder( boolean bReload ) {
        if( folder_stack.size() > 0 ) {
            folder_stack.remove( folder_stack.size() - 1 );
            folder_name_stack.remove( folder_name_stack.size() - 1 );

            if( bReload ) {
                new Thread( new Runnable(){
                    @Override
                    public void run() {
                        try {
                            cd( "..");
                            reload();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }

        return is_next_pop_folder();
    }

    public synchronized boolean is_next_pop_folder() {
        return folder_stack.size() > 0 ? true : false;
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

            ArrayList<FtpClientData> nlist = new ArrayList<FtpClientData>();
            for (int i = 0; i < listDatas.size(); i++) {
                final FtpClientData item = listDatas.get(i);

                if (item.getFilename().toLowerCase().contains(filterString) ||
                        item.getFilename().toLowerCase().contains(filterString)) {
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
            filteredDatas = (List<FtpClientData>) results.values;
            sort();
        }
    }

    public synchronized void sort() {
        Collections.sort( filteredDatas, FtpClientData.FTPFile_ALPHA_COMPARATOR );
        context.runOnUiThread( listDataUpdateRunnable );
    }

    Runnable listDataUpdateRunnable = new Runnable(){
        @Override
        public void run() {
            notifyDataSetChanged();
        }
    };

    public void init( String server, int port ) {
        Log.d( "MyLOG", String.format( "%s : %d", server, port ));
        if( server.startsWith("ftp://")) {
            server = server.substring( "ftp://".length());
        }
        this.server = server;
        this.port   = port;
    }

    // 계정과 패스워드로 로그인
    public boolean login(String user, String password) {
        Log.d( "MyLOG", String.format( "%s : %s", user, password ));

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
        Log.d( "MyLOG", String.format( "connect %s:%d", server, port ));
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
            ioe.printStackTrace();

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
        FTPFile[] files = this.ftpClient.listFiles();

        listDatas.clear();
        filteredDatas.clear();

        for( FTPFile file : files ) {
            Log.d("MyLOG", file.toString()); // file.getName(), file.getSize() 등등.
            FtpClientData data = new FtpClientData( context, file );

            listDatas.add( data );
            filteredDatas.add( data );
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
