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
 package org.csstudio.platform.libs.xmpp.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.csstudio.platform.libs.dcf.messaging.Message;
import org.csstudio.platform.libs.dcf.utils.Base64Coder;
import org.jivesoftware.smack.packet.IQ;


public class XmppMessage extends IQ{

	// the namespace for the message packets
	public static final String MESSAGE_NAMESPACE = "jabber:iq:xmpp:message";
	
	// constants representing the tags in the XML structure
	public static final String PARENT_TAG = "message";
	public static final String DATA_TAG = "data";
	
	private Message _data;
	
	public XmppMessage() {
		this.setPacketID(MESSAGE_NAMESPACE);
	}
	
	public XmppMessage(Message encapsulatedMsg) {
		this._data = encapsulatedMsg;
		this.setPacketID(MESSAGE_NAMESPACE);
	}

	
	@Override
	public String getChildElementXML() {
		// create the XML structure of the packet
		StringBuilder packet = new StringBuilder();
		packet.append("<".concat(PARENT_TAG).concat(" xmlns=\"")
				.concat(MESSAGE_NAMESPACE)
				.concat("\">"));
		
			packet.append("<".concat(DATA_TAG).concat(">"));
			packet.append(this.getData());
			packet.append("</".concat(DATA_TAG).concat(">"));
			
		packet.append("</".concat(PARENT_TAG).concat(">"));
		
		// return the packet
		return packet.toString();
	}
	
	public Message getEncapsulatedMessage() {
		return this._data;
	}
	
	public void setEncapsulatedMessage(Message msg) {
		this._data = msg;
	}
	
	public void setData(String msg) {
		try {
			ObjectInputStream ois = new ObjectInputStream(
					new ByteArrayInputStream(getByteArrayFromString(msg)));
			this._data = (Message)ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			this._data = null;
		}
	}
	
	public String getData() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			if(_data instanceof Message)
				_data = _data.prepareForSending();
			oos.writeObject(this._data);
			baos.flush();
			return getStringFromByteArray(baos.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	private byte[] getByteArrayFromString(String data) {
//		char[] ch = data.toCharArray();
//		byte[] bt = new byte[ch.length];
//
//		for (int i = 0; i < ch.length; i++) {
//			bt[i] = (byte) ch[i];
//		}
//
//		return bt;
		return Base64Coder.decode(data);
	}

	private String getStringFromByteArray(byte[] data) {
		char[] d = Base64Coder.encode(data);

		return new String(d);
	}

}
