package org.csstudio.askap.jms2email;

import java.util.logging.Level;

import javax.jms.Message;

public class TestMessageHandler implements MessageHandler {
	
	public TestMessageHandler() {
	}

	@Override
	public void handleMessage(Message message) 
		throws Exception {
		// printout the message
		Activator.getLogger().log(Level.INFO, "Got JMS Message " + message.toString());
	}
}
