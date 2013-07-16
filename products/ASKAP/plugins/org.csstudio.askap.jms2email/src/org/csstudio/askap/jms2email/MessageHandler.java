package org.csstudio.askap.jms2email;

import javax.jms.Message;

public interface MessageHandler {
	
	public void handleMessage(Message message) throws Exception;
}
