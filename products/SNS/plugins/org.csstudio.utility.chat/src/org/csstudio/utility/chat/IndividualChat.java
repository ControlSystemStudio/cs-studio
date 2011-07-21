/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.chat;

import org.eclipse.osgi.util.NLS;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;

/** A place for individuals to chat
 * 
 *  <p>Uses XMPP via the SMACK library.
 *  This layer would allow replacment
 *  of the protocol.
 *  
 *  @author Kay Kasemir
 */
public class IndividualChat implements MessageListener
{
	final private String user;
	final private Chat chat;
	private IndividualChatListener listener = null;
	
	/** Initialize
	 *  @param user We
	 *  @param chat XMPP chat
	 */
	public IndividualChat(final String user, final Chat chat)
    {
		this.user = user;
		this.chat = chat;
		chat.addMessageListener(this);
    }

	/** @param listener Listener to add */
	public void addListener(final IndividualChatListener listener)
	{
		if (this.listener != null)
			throw new Error("Listener already set"); //$NON-NLS-1$
		this.listener = listener;
	}

	/** {@inheritDoc} */
	@Override
    public void processMessage(Chat chat, Message message)
    {
		if (listener == null)
			return;
		String name = StringUtils.parseName(message.getFrom());
		if (name.length() <= 0)
			name = Messages.UserSERVER;
		final String body = message.getBody();
		listener.receive(name, false, body);
    }
	
    /** @param text Message to send to the chat
     *  @throws Exception on error
     */
	public void send(final String text) throws Exception
    {
		if (chat == null)
			return;
    	chat.sendMessage(text);
		listener.receive(user, true, text);
    }

	/** Disconnect from the chat */
	public void disconnect()
    {
		if (chat != null)
		{
			// TODO Unclear how to close a chat
			// The other participant still stays in the chat.
			// For now just sending a "bye" message
			try
			{
				chat.sendMessage(NLS.bind(Messages.LeaveChatFmt, user));
			}
			catch (Exception ex)
			{
				// Ignore, closing anyway
			}
			chat.removeMessageListener(this);
		}
		listener = null;
    }
}
