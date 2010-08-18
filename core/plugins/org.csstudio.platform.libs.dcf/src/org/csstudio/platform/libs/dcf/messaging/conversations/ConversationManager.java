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

import java.util.HashMap;
import java.util.Map;

import org.csstudio.platform.libs.dcf.messaging.ConnectionManager;
import org.csstudio.platform.libs.dcf.messaging.internal.ProtocolEnumerator;

/**
 * Handles creation of new conversation and selecting
 * the connection manager based on user preferences.
 * 
 * @author avodovnik
 *
 */
public final class ConversationManager {
	// holds the conversations stored by the manager
	private static Map<String, Conversation> _conversationsMap =
		new HashMap<String, Conversation>();
	/**
	 * Creates a new conversation from the connection manager
	 * which the user has selected in the preferences box.
	 * @return
	 */
	public static Conversation createConversation() {
		// check the user's selection from the preferences
		// TODO: implement this!!!!!!
		// TODO: conn manager can be retrieved from preferences
		Conversation conversation = 
			new Conversation(ProtocolEnumerator.getProtocols()[0]);
		// return the conversation object
		_conversationsMap.put(conversation.getConversationId(),
				conversation);
		return conversation;
	}
	
	public Conversation getConversation(String id) {
		if(_conversationsMap.containsKey(id)) {
			return _conversationsMap.get(id);
		}
		
		// create a conversation with this same id
		Conversation c = new Conversation(ConnectionManager.getDefault(), id);
		return c;
	}
}
