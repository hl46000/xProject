package com.purehero.common.io;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.sun.mail.smtp.SMTPTransport;

public class SMTP {
	public static void sendInkaNoreplyMail(String title, String recipient, String mailBody, File attachFile ) throws UnsupportedEncodingException, MessagingException {
     	final String smtpServer = "smtp.gmail.com";
		final String sender 	= "noreply@appsealing.com";
		final String sender_pwd	= "1234!@#$";
     	
		Properties props=new Properties();
       	props.setProperty("mail.store.protocol","imaps");
       	props.setProperty("mail.smtp.auth", "true"); // to ignore an untrusted certificate
       	props.setProperty("mail.debug", "false");

		Session session = Session.getDefaultInstance(props, null);
    	final MimeMessage msg = new MimeMessage( session );
	    	
    	msg.setFrom( new InternetAddress( sender, "AppSealing Support Team" ));
		msg.setRecipients( javax.mail.Message.RecipientType.TO, InternetAddress.parse( recipient, false ));
		msg.setSubject( title );		
		
		Multipart multiPart = new MimeMultipart();
		
		MimeBodyPart bodyPart = new MimeBodyPart();
		bodyPart.setText( mailBody, "utf-8", "html" );
		multiPart.addBodyPart( bodyPart );
        
		if( attachFile != null ) {
			MimeBodyPart attachPart = new MimeBodyPart();
	        try {
				attachPart.attachFile( attachFile );
				multiPart.addBodyPart( attachPart );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		msg.setContent( multiPart );
		
		SMTPTransport smtp = ( SMTPTransport )session.getTransport( "smtps" );
		smtp.connect( smtpServer, sender, sender_pwd );
	    smtp.sendMessage( msg, msg.getAllRecipients() );
	    smtp.close();
	}
}
