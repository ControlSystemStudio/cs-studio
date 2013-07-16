package org.csstudio.askap.jms2email;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.jms.MapMessage;
import javax.jms.Message;

import org.csstudio.email.EMailSender;

public class EmailMessageHandler implements MessageHandler {

	private String from;
	private String to;
	private String host;
	private String subject;
	
	static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMMMM-dd HH:mm:ss z");
	
    public EmailMessageHandler(String host, String from, String to, String subject) {
    	this.from = from;
    	this.to = to;
    	this.host = host;
    	this.subject = subject;
    	
    }
	
	@Override
	public void handleMessage(Message message) throws Exception {
		String fullSubject = subject;
		
		EMailSender mailSender = new EMailSender(host, from, to, fullSubject);
		
		mailSender.addText("Timestamp: " + dateFormat.format(new Date(message.getJMSTimestamp())));
		mailSender.addText("From: " + message.getJMSDestination().toString());
		
		String msgStr = "";
		
		if (message instanceof MapMessage) {
			MapMessage mapMessage = (MapMessage) message;
			for (Enumeration e = mapMessage.getMapNames(); e.hasMoreElements();) {
				String name = (String) e.nextElement();
				String value = mapMessage.getString(name);
				
				msgStr += name + ": " + value + "\n";
			}	
		} else {
			throw new Exception("Unsupported JMS Message type: " + message.getClass().getName());
		}
		
		mailSender.addText("Message:\n" + msgStr);
		mailSender.close();
	}

}
