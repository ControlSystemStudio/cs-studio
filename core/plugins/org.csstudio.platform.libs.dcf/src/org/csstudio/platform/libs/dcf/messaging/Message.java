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
 /**
 * 
 */
package org.csstudio.platform.libs.dcf.messaging;

import java.io.Serializable;

import org.csstudio.platform.libs.dcf.directory.ContactElement;

/**
 * This class represents the base message that will be
 * sent to any underlying protocol when a user wants to
 * execute an action. The message object will contain 
 * any information relevant to the conversation, such
 * as the conversation identification, origin and target.
 * 
 * @author avodovnik
 *
 */
public final class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6850446585408840494L;
	
	// contains the data object carried by this message
	private Object _data;
	// contains a unique string identification of the conversation
	private String _conversationID;
	// holds the origin identification for this message
	private ContactElement _origin;
	// holds the target identification for this message
	private ContactElement _target;
	
	/**
	 * The default constructor for the Message object. It creates
	 * a new instance and sets all the values according to the 
	 * passed parameters.
	 * 
	 * @param data The message data. 
	 * @param converationID The conversation ID of the message. If
	 * this is empty, a new conversation ID is generated.
	 * empty, the caller can rely on the protocol to set the originator.
	 * @param target The target identification (i.e. an XMPP address) that will
	 * be used by the unedrlying protocol to send the message.
	 */
	public Message(Object data, 
			String converationID,
			ContactElement target) {
		// TODO: implement an exception to be thrown if address
		// is null
		// set the parameters
		this._data = data;
		this._conversationID = converationID;
		this._target = target;
	}
	
	/**
	 * Gets the data contained in the message.
	 * @return Returns an object representing the data of the message.
	 */
	public Object getData() {
		// return the data
		return _data;
	}
	
	/**
	 * Gets the conversation ID for this message.
	 * @return Returns a string representing a unique identifier for the 
	 * conversation of which the message is part of.
	 */
	public String getConversationID() {
		return _conversationID;
	}
	
	/**
	 * Sets the conversation ID for this message.
	 * @param id The conversation id.
	 */
	public void setConversationID(String id) {
		this._conversationID = id;
	}
	
	/**
	 * Gets the originating address of this message.
	 * @return Returns a string representing the address from where the
	 * message originated. 
	 */
	public ContactElement getOrigin() {
		return _origin;
	}

	/**
	 * Sets the origin of for the message.
	 * @param value The origin.
	 */
	public void setOrigin(ContactElement value) {
		_origin = value;
	}
	/**
	 * Gets the target address for this message.
	 * @return Returns a string representing the address to which the message
	 * will be sent using the underlying protocol.
	 */
	public ContactElement getTarget() {
		return _target;
	}
	
	public void setTarget(ContactElement ce) {
		this._target = ce;
	}

	public Message prepareForSending() {
		return new Message(this.getData(), this.getConversationID(), this._target);
	}
}
