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
 package org.csstudio.platform.libs.dcf.messaging.conversations;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.platform.libs.dcf.messaging.ConnectionManager;
import org.csstudio.platform.libs.dcf.messaging.IMessageListener;
import org.csstudio.platform.libs.dcf.messaging.IMessageListenerFilter;
import org.csstudio.platform.libs.dcf.messaging.Message;
import org.csstudio.platform.libs.dcf.messaging.MessagingException;
import org.csstudio.platform.libs.dcf.utils.RandomGUID;

/**
 * The conversation class holds information about the
 * messages sent through the duration of this instance
 * and information about which class to notify when a
 * response is recieved that correlates to this conversation.
 * 
 * @author avodovnik
 *
 */
// any notifier is given an object which was passed through
// the message class from the protocol

public class Conversation {
	
	private String _conversationId ="";
	private ConnectionManager _connectionManager;
	private List<IMessageListener> _msgListeners =
		new CopyOnWriteArrayList<IMessageListener>();
	private Queue<Message> _messages =
		new ConcurrentLinkedQueue<Message>();
	
	/**
	 * Constructs an instance of the conversation class.
	 * @param connectionManager The connection manager to be used
	 * when communicating through this conversation.
	 */
	public Conversation(ConnectionManager connectionManager) {
		this(connectionManager, Conversation.createConversationId());
	}
	
	/**
	 * Creates a conversation with the specified connection manager
	 * and the specified conversation id.
	 * @param cm The connection manager.
	 * @param cid The Conversation id.
	 */
	public Conversation(ConnectionManager connectionManager, String cid) {
		// TODO: should the conversaion class be moved to ConversationManager
		// in which case it cannot be used by circumventing the protocol
		// selection
		// assign the connection manager's value
		_connectionManager = connectionManager;
		_conversationId = cid;
		// immediately assign a message listener
		_connectionManager.addMessageListener(new IMessageListener() {

			public void notifyMessageRecieved(Message message) {
				// tell the conversation to notify any listeners
				Conversation.this.notifyMessageRecieved(message);
				// add the message to the message list
				_messages.add(message);
				if(message.getData() instanceof MessagingException)
					_connectionManager.removeMessageListener(this);
			}

			public void notifyMessageSent(Message message) {
				// tell the conversation to notify any listeners
				Conversation.this.notifyMessageSent(message);
				// add the message to the message list
				_messages.add(message);
			}
			
		}, new IMessageListenerFilter() {

			public boolean match(Message message) {
				// pass through only the messages that are part
				// of the current conversation
				if(message == null) return false;
				
				if(getConversationId().equals(message.getConversationID()))
					return true;
				else {
					// check IF maybe it's an error
					if(message.getData() instanceof MessagingException) {
						// check IF MAYBE it's from the same origin
						// as the target, of the first message in this
						// conversation
						System.out.println("ERR.ORIGIN: " + message.getOrigin().toString());
						if(_messages.peek() != null) {
						System.out.println("ERR. TO: " + _messages.peek().getTarget().toString());
							try {
								if(message.getOrigin() == null || message.getTarget() == null)
									return false;
								
								boolean val =  message.getOrigin().toString().contains(
										_messages.peek().getTarget().toString());
								
								return val;
							} catch (Exception e) {
								e.printStackTrace();
								return false;
							}
						}
						return false;
						
					}
					// ignore the rest
					return false;
				}
			}
			
		});
	}
	
	// notifies any listeners that a message was recieved
	private void notifyMessageRecieved(Message msg) {
		for(IMessageListener listener : _msgListeners) {
			listener.notifyMessageRecieved(msg);
		}
	}
	
	// notifies any listeners that a message was sent
	private void notifyMessageSent(Message msg) {
		for(IMessageListener listener : _msgListeners) {
			listener.notifyMessageSent(msg);
		}
	}
	
	/**
	 * Sends the message through the corresponding connection
	 * manager (depending on the underlying protocol) and adds
	 * the conversation id to the attributes of the message.
	 * @param message The message to be sent.
	 * @return Returns null if the message was sent, or the throwable
	 * object that was returned from the protocol if there was an error.
	 */
	public Throwable sendMessage(Message message) {
		message.setConversationID(this._conversationId);
		return _connectionManager.sendMessage(message);
	}
	
	/**
	 * Gets the conversation id of the current conversation.
	 * @return Returns a conversation id for this instance.
	 */
	public String getConversationId()
	{
		return _conversationId;
	}
	
	private static String createConversationId() {
		RandomGUID rg = new RandomGUID();
		return rg.toString();
	}
	
	/**
	 * Gets any messages related to the current conversation.
	 * @return Returns all messages that were exchanged as part of this 
	 * conversation. 
	 */
	public Message[] getMessages() {
		return this._messages.toArray(new Message[this._messages.size()]);
	}

	/**
	 * Adds a message listener which is notified any time a related
	 * message is added to the conversation. 
	 * @param listener The listener to be added. If the listener already
	 * exists in the list, this request is ignored.
	 */
	public void addMessageListener(IMessageListener listener) {
		if(!this._msgListeners.contains(listener))
			this._msgListeners.add(listener);
	}
	
	/**
	 * Removes the message listener from the queue.
	 * @param listener The listener to be removed.
	 */
	public void removeMessageListener(IMessageListener listener) {
		this._msgListeners.remove(listener);
	}
	
}
