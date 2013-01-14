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
import org.jivesoftware.smackx.ChatState;
import org.jivesoftware.smackx.packet.ChatStateExtension;

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
	/** Our name in the chat */
	final private String user;
	
	/** Smack {@link Chat} */
	private Chat chat;
	
	/** Listener to messages from this chat */
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
    public void processMessage(final Chat chat, final Message message)
    {
		// Have we already disconnected from the chat?
		if (this.chat == null)
		{	// Try to tell sender that we're gone
			try
			{
				chat.sendMessage(NLS.bind(Messages.LeaveChatFmt, user));
			}
			catch (Exception ex)
			{
				// Ignore
			}
			return;
		}
		// Anybody listening (yet)
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
			try
			{
				// Unclear how to close an individual chat.
				// The other participant still stays in the chat,
				// the Smack library still receives updates
				// as long as it has (weak) references to the chat.
				
				// Send a "bye" message to the human reader of the chat
				chat.sendMessage(NLS.bind(Messages.LeftChatFmt, user));

				// Send a 'gone' state update to the other chat program
				final Message message = new Message();
				message.addExtension(new ChatStateExtension(ChatState.gone));
				chat.sendMessage(message);
			}
			catch (Exception ex)
			{
				// Ignore, closing anyway
			}
			// Stop listening, stop notofying out listener
			chat.removeMessageListener(this);
			listener = null;
			// Release reference to chat as flag that we no longer care
			chat = null;
			// Force GC so that smack library removes weak references to this chat
			System.gc();
			// If the chat partner now sends another message on this chat,
			// it will look like an invitation to a new chat to the Smack library
		}
    }
}
