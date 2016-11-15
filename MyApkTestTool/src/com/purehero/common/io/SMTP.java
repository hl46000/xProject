package com.purehero.common.io;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
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
	/**
	 * Inka의 noreply@appsealing.com 계정으로 이메일을 발송한다. 
	 * 
	 * @param title			e-mail 제목
	 * @param recipientList	수신자 리스트
	 * @param mailBodyLines	e-mail 내용들
	 * @param attachFile	첨부파일( 없으면 null 입력 )
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 */
	public static void sendInkaNoreplyMail(String title, List<String> recipientList, List<String> mailBodyLines, File attachFile ) throws UnsupportedEncodingException, MessagingException {
		if( recipientList == null || recipientList.size() < 1 ) {
			return;
		}
		
		String recipient = "";
		int i;
		for( i = 0; i < recipientList.size() - 1; i++ ) {
			recipient += String.format("%s,", recipientList.get(i));
		}
		recipient += recipientList.get(i);
		
		String mailBody = "";
		if( mailBodyLines != null && mailBodyLines.size() > 0 ) {
			for( i = 0; i < mailBodyLines.size() - 1; i++ ) {
				mailBody += String.format("%s<br>", mailBodyLines.get(i));
			}
			mailBody += mailBodyLines.get(i);
		}
		
		sendInkaNoreplyMail( title, recipient, mailBody, attachFile );
	}
	
	/**
	 * Inka의 noreply@appsealing.com 계정으로 이메일을 발송한다. 
	 * 
	 * @param title e-mail 제목
	 * @param recipient 수신자 문자열( 여러명일 경우 ',' 로 구분 )
	 * @param mailBody e-mail 내용
	 * @param attachFile 첨부파일( 없으면 null 입력 )
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 */
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
