package share.file.purehero.com.fileshare;

import android.content.Context;
import android.widget.Toast;

import com.purehero.common.G;

import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.UserFactory;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import java.io.File;
import java.util.List;

/**
 * Created by purehero on 2017-03-14.
 */

public class MyFtpServer {
    Context context;
    private String ftpConnectionMessage = "FTP Server is not starting";
    private static MyFtpServer __self = null;

    public static MyFtpServer getInstance(Context context ) {
        if( __self == null ) {
            __self = new MyFtpServer( context );
        }
        return __self;
    }

    public MyFtpServer(Context context) {
        this.context = context;
    }

    org.apache.ftpserver.FtpServer ftpServer = null;
    public void initFtpServer( String id, String pwd, int port, File root_folder ) {
        String deviceAddr = G.getIPAddress(true);
        if( deviceAddr == null || deviceAddr.length() < 6 ) {
            Toast.makeText( context, R.string.network_disconntion, Toast.LENGTH_LONG ).show();
            return;
        }
        if( ftpServer != null ) {
            return;
        }

        FtpServerFactory serverFactory = new FtpServerFactory();

        ListenerFactory factory = new ListenerFactory();
        factory.setPort( port );
        serverFactory.addListener("default", factory.createListener());

        UserFactory userFact = new UserFactory();
        userFact.setName( id );                  // user name
        userFact.setPassword( pwd );                 // user password
        userFact.setHomeDirectory( root_folder.getAbsolutePath() );           // user root directory

        // 폴더에 쓰기 권한을 준다.
        List<Authority> authorities = ( List<Authority> ) userFact.getAuthorities();
        authorities.add( (Authority) new WritePermission() );
        userFact.setAuthorities( authorities );

        User user = userFact.createUser();
        try {
            serverFactory.getUserManager().save(user);
            //serverFactory.setUserManager( userManager );

            ftpServer = serverFactory.createServer();
            G.Log( "FTP Server address : %s:%d", deviceAddr, factory.getPort() );

            String message = context.getString( R.string.connection_addr );
            String serverAddr = String.format("%s:%d", deviceAddr,  port );
            String ex = String.format( "\n\nex) ftp://%s@%s:%d", id, deviceAddr, port );
            ftpConnectionMessage = message + serverAddr + ex;
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }

    public String getConnectionMessage() {
        return ftpConnectionMessage;
    }

    public boolean isStartedFtpServer() {
        if( ftpServer == null ) return false;
        return !ftpServer.isStopped();
    }

    public void startFtpServer() {
        if( ftpServer != null ) {
            try {
                if( ftpServer.isSuspended()) {
                    ftpServer.resume();
                } else if( ftpServer.isStopped()) {
                    ftpServer.start();
                }
            } catch (FtpException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopFtpServer() {
        if( ftpServer != null ) {
            ftpServer.stop();
        }
    }
}
