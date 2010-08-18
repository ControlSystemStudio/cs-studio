/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.platform.libs.dcf;

import java.util.Map;
import org.csstudio.platform.libs.dcf.actions.SimpleActionRequest;
import org.csstudio.platform.libs.dcf.messaging.ConnectionManager;
import org.csstudio.platform.libs.dcf.messaging.IMessageListener;
import org.csstudio.platform.libs.dcf.messaging.IMessageListenerFilter;
import org.csstudio.platform.libs.dcf.messaging.Message;
import org.csstudio.platform.startupservice.IStartupServiceListener;

/**
 * Create thread to process the startup procedure<p>
 * Note: This may cause confusion in the calling process because the DCF service may not be up and running at the very beginning.
 * @author claus
 * @author Anze Vodovnik
 * 
 */
public class DCFStartupServiceRun extends Thread {
	/**
	 * Just start this class (this.start(); )
	 */
	DCFStartupServiceRun() {
		this.start();
	}

	/**
	 * Process startup procedure
	 */
	public void run() {
		try {
			ConnectionManager.getDefault().initManager();
			ConnectionManager.getDefault().addMessageListener(
					new IMessageListener() {

						public void notifyMessageRecieved(Message message) {
							// the following cast is safe, because
							// the message is filtered
							notifyOnRecieve(message);
						}

						public void notifyMessageSent(Message message) {
							// ignore
						}

					}, new IMessageListenerFilter() {

						public boolean match(Message message) {
							// only notify for messages which are
							// action requests, we know that responses
							// will be recieved by the conversation
							// objects themselves
							if (message == null)
								return false;
							return (message.getData() instanceof SimpleActionRequest);
						}

					});
			// System.out.println(ConnectionManager.getDefault().getId());

			// // do a test printout of the roster
			// for(ContactElement element :
			// ConnectionManager.getDefault().getDirectory().getChildren()) {
			// printElement(element);
			// }
		} catch (Exception e) {
			// ignore the startup
			e.printStackTrace();
		}
	}

	// private void printElement(ContactElement element) {
	// if(element == null) return;
	// System.out.println(element.toString());
	// if(element.getChildren() == null) return;
	// for(ContactElement e : element.getChildren()) {
	// printElement(e);
	// }
	// }

	/**
	 * Callback when message arrives
	 * @param Message msg
	 */
	private void notifyOnRecieve(Message msg) {
		// TODO: add permission checks
		String cid = msg.getConversationID();
		SimpleActionRequest sar = (SimpleActionRequest) msg.getData();

		if (sar.getParameter() != null && sar.getParameter() instanceof Map) {
			Map params = (Map) sar.getParameter();
			params.put("origin", msg.getOrigin());
			sar.setParameter(params);
		} else if (sar.getParameter() == null) {
			sar.setParameter(msg.getOrigin());
		}

		/*
		 * if(sar.getActionId().equals(ActionFileTransfer.ACTION_ID)){ Map
		 * params = (Map)sar.getParameter(); params.put("origin",
		 * msg.getOrigin()); sar.setParameter(params); } if(sar.getParameter() ==
		 * null){ sar.setParameter(msg.getOrigin()); }
		 */

		Object response = sar.execute();
		// TODO: implement a wrapper around the response
		Message reply = new Message(response, cid, msg.getOrigin());
		ConnectionManager.getDefault().sendMessage(reply);
	}

}
