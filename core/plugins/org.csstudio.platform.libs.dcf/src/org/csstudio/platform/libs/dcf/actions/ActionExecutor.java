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
 package org.csstudio.platform.libs.dcf.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.csstudio.platform.libs.dcf.directory.ContactElement;
import org.csstudio.platform.libs.dcf.messaging.IMessageListener;
import org.csstudio.platform.libs.dcf.messaging.Message;
import org.csstudio.platform.libs.dcf.messaging.MessagingException;
import org.csstudio.platform.libs.dcf.messaging.conversations.Conversation;
import org.csstudio.platform.libs.dcf.messaging.conversations.ConversationManager;

/**
 * This class takes care of invoking the correct protocol,
 * based on users choice in the preference page, setting
 * and getting the conversation id (starting a conversation
 * through the conversation manager). 
 * 
 * @author avodovnik
 *
 */
public final class ActionExecutor {

	private static List<IActionRequestListener> _requestListeners =
		new ArrayList<IActionRequestListener>();
	
	private static Map<String, ActionRequestMonitor> _monitors =
		new ConcurrentHashMap<String, ActionRequestMonitor>();
	
	/**
	 * Executes an action on a remote machine.
	 * @param actionId The ID of the action to be executed.
	 * @param parameter The parameter object (can be array) passed to 
	 * 		the action on the recieveing side.
	 * @param target The target of the invokation.
	 * @param responseNotifier The notifier to use when a response is 
	 * 		recieved.
	 */
	public static void execute(final String actionId, 
			final Object parameter,
			final ContactElement target,
			final IActionResponseReceived responseNotifier) {
		// create a new conversation from the conversation manager
		final Conversation c = ConversationManager.createConversation();
		// create a new message wrapping action information
		Message msg = new Message(
				new SimpleActionRequest(actionId, parameter),
				c.getConversationId(), target);

		// register listeners on the conversation
		c.addMessageListener(new IMessageListener() {

			public void notifyMessageRecieved(Message message) {
				// notify the listener that a message was
				// recieved
				responseNotifier.notifyResponseReceived(message.getData());
				notifyActionResponseRecieved(c.getConversationId(), message);
				if(message.getData() instanceof MessagingException) {
					c.removeMessageListener(this);
					Thread.currentThread().interrupt();
				}
				
			}

			public void notifyMessageSent(Message message) {
				// ignore
			}
			
		});
		
		// send the message through the conversation
		c.sendMessage(msg);
		
		
		notifyActionSentListeners(c.getConversationId(),
				new ActionRequestMonitor(actionId, parameter, target));
	}
	
	public static Object executeObjectSynchronous(String actionId, 
			Object parameter,
			ContactElement target)
	{
		// add an error listener
//		ConnectionManager.getDefault().addMessageListener(new IMessageListener() {
//
//			public void notifyMessageRecieved(Message message) {
//				responses.add(message.getData());
//				// we must remove the listener to avoid causing an illegal state
//				// due to filling up the queue without anyone taking objects from it
//				ConnectionManager.getDefault().removeMessageListener(this);
//			}
//
//			public void notifyMessageSent(Message message) {
//
//			}
//			
//		}, new IMessageListenerFilter() {
//
//			public boolean match(Message message) {
//				if(message.getData() instanceof MessagingException)
//					return true;
//				return false;
//			}
//			
//		});
		// execute
		
		execute(actionId, parameter, target, new IActionResponseReceived() {
			public void notifyResponseReceived(Object response) {
				if(response != null)
					responses.offer(response);
			}
		});
		
		try {
			return responses.poll(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static BlockingQueue<Object> responses =
		new SynchronousQueue<Object>();
	/**
	 * Sends a return value for an action to the reciever.
	 * 
	 * @param actionId The id of the action that was executed.
	 * @param response The return value of the action.
	 * @param target The target of the response.
	 */
	public static void respond(String actionId, 
			Object response, ContactElement target) {
	}
	
	protected static void notifyActionResponseRecieved(String cid, Message msg) {
		ActionRequestMonitor monitor = _monitors.get(cid);
		if(monitor != null)
			monitor.notifyResponseRecieved(msg.getData(), msg.getOrigin());
	}
	
	protected static void notifyActionSentListeners(String cid, ActionRequestMonitor monitor) {
		_monitors.put(cid, monitor);
		System.out.println("Called but with : " + _requestListeners.size());
		for(IActionRequestListener arl : _requestListeners) {
			arl.notify(monitor);
		}
	}
	
	public static ActionRequestMonitor[] getActionRequestMonitors() {
		return _monitors.values().toArray(new ActionRequestMonitor[0]);
	}
	
	public static void addActionRequestSentListener(IActionRequestListener listener) {
		_requestListeners.add(listener);
	}
	
	public static void removeActionRequestSentListener(IActionRequestListener listener) {
		_requestListeners.remove(listener);
	}
	
	/**
	 * Classes can implement this interface and register it
	 * with the executor to recieve updates when an action
	 * request is created and sent trhough.
	 * @author avodovnik
	 *
	 */
	public interface IActionRequestListener {
		public void notify(ActionRequestMonitor monitor);
	}

}
