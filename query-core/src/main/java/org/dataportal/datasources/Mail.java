package org.dataportal.datasources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.dataportal.Config;

public class Mail {
    
    private static Logger logger = Logger.getLogger(Mail.class);
	
	private void send(String from, String to, String subject, String content)
			throws MessagingException {
		Properties props = new Properties();
        //props.put("mail.debug", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", Config.get("mail.smtp.host"));
		props.put("mail.smtp.port", Config.get("mail.smtp.port"));
		props.put("mail.smtp.socketFactory.port", Config.get("mail.smtp.port"));
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");

		Session session = Session.getDefaultInstance(props,	new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(
			        Config.get("mail.user"),
			        Config.get("mail.password")
			    );
			}
		});
		
		try {
			Message msg = new MimeMessage(session);
			InternetAddress[] addressFrom = new InternetAddress[1];
			addressFrom[0] = new InternetAddress(from);
			msg.setFrom(addressFrom[0]);
	
			InternetAddress[] addressTo = new InternetAddress[1];
			addressTo[0] = new InternetAddress(to);
			
			msg.setReplyTo(addressFrom);
			msg.addRecipients(Message.RecipientType.TO, addressTo);
			msg.setSubject(subject);
			msg.setContent(content, "text/plain");
			Transport.send(msg);
		} catch (MessagingException e) {
		    logger.error(e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * Send a template-based mail.
	 * 
	 * @param to The mail address to send the message to.
	 * @param subject The message title.
	 * @param template The message body template name.
	 * @param vars The key-value pairs to apply to the template.
     * @throws IOException Problems reading template.
	 * @throws MessagingException Problems sending mail.
	 */
	public void send(String to, String subject, String template,
			Map<String, String> vars) throws MessagingException, IOException {
		String from = Config.get("mail.address");
		InputStream is = Mail.class.getResourceAsStream("/"+template+".txt");
		String content = IOUtils.toString(is, "UTF-8");
		for(Map.Entry<String, String> var : vars.entrySet()) {
			content = content.replaceAll("\\{"+var.getKey()+"\\}", var.getValue());
		}
		send(from, to, subject, content);
	}

}
