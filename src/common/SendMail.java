package common;


import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {
	      // Recipient's email ID needs to be mentioned.
	      String to;

	      // Sender's email ID needs to be mentioned
	      String from = "obl3mail@gmail.com";
	      final String username = "obl3mail";
	      final String password = "305141095";
	      String msg;
	      String Subject;


	      public SendMail(String toMail,String Subject, String msg){
	    	to = toMail;
	    	this.msg=msg;
	    	this.Subject=Subject;
	  		Properties props = new Properties();
			props.put("mail.smtp.host", "smtp.gmail.com");
			//props.put("mail.smtp.socketFactory.port", "587");
			//props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", "587");
			Session session = Session.getDefaultInstance(props,new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username,password);
					}
				});
			try {
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(from));
				message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(to));
				message.setSubject(Subject);
				message.setText(msg);
				Transport.send(message);
				System.out.println("Done");

			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
	      }
}
