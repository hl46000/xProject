package com.purehero.ftp.client;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FtpClientSettingsActivity.class);
                //intent.putExtra("lastFolder", listAdapter.getLastFolder().getAbsolutePath());
                MainActivity.this.startActivityForResult(intent, 100);

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        if( 0 == checkPermission()) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragment, new FtpClientFragment().setMainActivity(this));
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, new FtpClientFragment().setMainActivity(this));
        fragmentTransaction.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch( requestCode ) {
            case 100 :  // FTP Server 설정
                new Thread( new Runnable(){
                    @Override
                    public void run() {
                        FTPClient ftpClient = null;
                        ftpClient = new FTPClient();
                        ftpClient.setControlEncoding("utf-8"); // 한글파일명 때문에 디폴트 인코딩을 euc-kr로 합니다 ftpClient.connect("user.chollian.net"); // 천리안 FTP에 접속합니다

                        try {
                            ftpClient.setDefaultPort( 2345 );
                            ftpClient.connect("192.168.123.141");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        int reply = ftpClient.getReplyCode(); // 응답코드가 비정상이면 종료합니다
                        if (!FTPReply.isPositiveCompletion(reply)) {
                            try {
                                ftpClient.disconnect();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println("FTP server refused connection.");
                        } else {
                            System.out.print(ftpClient.getReplyString()); // 응답 메세지를 찍어봅시다
                            try {
                                ftpClient.setSoTimeout(10000); // 현재 커넥션 timeout을 millisecond 값으로 입력합니다
                            } catch (SocketException e) {
                                e.printStackTrace();
                            }

                            try {
                                ftpClient.login("Guest", "1234"); // 로그인 유저명과 비밀번호를 입력 합니다
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // 목록보기 구현
                            FTPFile[] ftpfiles = new FTPFile[0]; // public 폴더의 모든 파일을 list 합니다
                            try {
                                ftpfiles = ftpClient.listFiles("/");
                                if (ftpfiles != null) {
                                    for (int i = 0; i < ftpfiles.length; i++) {
                                        FTPFile file = ftpfiles[i];
                                        Log.d("MyLOG", file.toString()); // file.getName(), file.getSize() 등등..
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            try {
                                ftpClient.logout();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                break;
        }
    }

    private int checkPermission() {
        String permissions[] = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE
        };
        List<String> request_permissions = new ArrayList<String>();
        for( String permission : permissions ) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED ) {
                request_permissions.add( permission );
            }
        }

        if( request_permissions.size() > 0 ) {
            String permissionsList [] = new String[request_permissions.size()];
            request_permissions.toArray(permissionsList);
            ActivityCompat.requestPermissions(this, permissionsList, 123 );
        }

        return request_permissions.size();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
