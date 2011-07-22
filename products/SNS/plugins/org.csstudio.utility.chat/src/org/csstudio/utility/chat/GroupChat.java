/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.chat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.osgi.util.NLS;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.DefaultParticipantStatusListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.Occupant;

/** A place for nerds to chat
 * 
 *  <p>Uses XMPP via the SMACK library.
 *  For one, using that with group chats was
 *  not immediately obvious.
 *  In addition, this layer would allow replacment
 *  of the protocol.
 *  
 *  @author Kay Kasemir
 */
public class GroupChat
{
	/** XMPP 'resource' used to connect */
	private static final String XMPP_RESOURCE = "css"; //$NON-NLS-1$

	/** Connection to server */
	final private XMPPConnection connection;
	
	/** Name of chat group */
	final private String group;
	
	/** Connected chat group */
	private MultiUserChat chat;
	
	/** Nerds in the chat group */
	final private Set<Person> nerds = new HashSet<Person>();
	
	/** Listeners to the {@link GroupChat} */
	final private List<GroupChatListener> listeners = new CopyOnWriteArrayList<GroupChatListener>();

	/** Our name used in this group chat */
	private String user;
	
	/** Initialize
	 *  @param host XMMP server host
	 *  @param group Chat group to join
	 *  @throws Exception on error
	 */
	public GroupChat(final String host, final String group) throws Exception
    {
		// Avoid message "couldn't setup local SOCKS5 proxy on port" on disconnect
		SmackConfiguration.setLocalSocks5ProxyEnabled(false);
		// Connect to host, port
		final ConnectionConfiguration config = new ConnectionConfiguration(host, 5222);
		connection = new XMPPConnection(config);
		connection.connect();
		this.group = group;
    }

	/** @param listener Listener to add */
	public void addListener(final GroupChatListener listener)
	{
		listeners.add(listener);
	}
	
	/** Connect to the server
	 *  @param user User name
	 *  @param password Password
	 *  @throws Exception on error
	 */
    public void connect(final String user, final String password) throws Exception
    {
    	this.user = user;
    	
		// Try to create account.
		// If account already exists, this will fail.
		final AccountManager accounts = connection.getAccountManager();
		if (accounts.supportsAccountCreation())
		{
			try
			{
				accounts.createAccount(user, password);
			}
			catch (final Exception ex)
			{
				// Ignore
			}
		}
		
		// Log on
		connection.login(user, password, XMPP_RESOURCE);

	    // Join chat group
		chat = new MultiUserChat(connection, group);
		chat.join(user);
		
		// Listen to nerd changes
		chat.addParticipantStatusListener(new DefaultParticipantStatusListener()
		{
			@Override
			public void joined(final String participant)
			{
				final String nick = StringUtils.parseResource(participant);
				final Occupant info = chat.getOccupant(participant);
				synchronized (nerds)
                {
					nerds.add(new Person(nick, info.getJid()));
                }
				fireNerdAlert();
			}

			@Override
			public void left(final String participant)
			{
				final String nick = StringUtils.parseResource(participant);
				synchronized (nerds)
                {
					nerds.remove(nick);
                }
				fireNerdAlert();
			}
		});

		// Determine who's there initially
		synchronized (nerds)
        {
			// Add ourself, which we don't always seem to get from server
			nerds.add(new Person(user, connection.getUser()));
			// Then query server, which might include an update for ourself
			final Iterator<String> occupants = chat.getOccupants();
			while (occupants.hasNext())
			{
				final String occupant = occupants.next();
				final Occupant info = chat.getOccupant(occupant);
				final String nick = StringUtils.parseResource(occupant);
				nerds.add(new Person(nick, info.getJid()));
			}
        }
		fireNerdAlert();
		
		// Listen to messages
		chat.addMessageListener(new PacketListener()
		{
			@Override
			public void processPacket(final Packet packet)
			{
				if (packet instanceof Message)
				{
					final Message message = (Message) packet;
					String nick = StringUtils.parseResource(message.getFrom());
					if (nick.length() <= 0)
						nick = Messages.UserSERVER;
					final String body = message.getBody();
					for (GroupChatListener listener : listeners)
						listener.receive(nick, nick.equals(user), body);
				}
			}
		});
		
		// Listen to invitations
		connection.getChatManager().addChatListener(new ChatManagerListener()
		{
			@Override
			public void chatCreated(final Chat chat, final boolean createdLocally)
			{
				if (createdLocally)
					return;
				for (GroupChatListener listener : listeners)
				{
					final String from = chat.getParticipant();
					final IndividualChatGUI gui = listener.receivedInvitation(from);
					if (gui == null)
					{	// Politely refuse
						try
						{
							chat.sendMessage(NLS.bind(Messages.DeclineChatFmt, user));
						}
						catch (Exception ex)
						{
							// Ignore
						}
					}
					else
						listener.startIndividualChat(from, new IndividualChat(user, chat));
				}
			}
		});
    }
    
    /** Notify listeners about current nerd list */
	private void fireNerdAlert()
    {
		final Person[] array;
		synchronized (nerds)
        {
			array = nerds.toArray(new Person[nerds.size()]);
        }
		Arrays.sort(array);
		for (GroupChatListener listener : listeners)
			listener.groupMemberUpdate(array);
    }

    /** @param text Message to send to the chat
     *  @throws Exception on error
     */
	public void send(final String text) throws Exception
    {
    	chat.sendMessage(text);
    }
	
	/** Disconnect from chat server */
    public void disconnect()
    {
    	if (chat != null)
    		chat.leave();
    	listeners.clear();
    	connection.disconnect();
    }

    /** Start individual chat
     *  @param person A {@link Person} in the chat group
     *  @return New {@link IndividualChat}
     */
	public IndividualChat createIndividualChat(final Person person)
    {
		// When we contact the person via the in-room nickname,
		// that "works" but is not the same as contacting the
		// person directly, as done by other chat clients like "pidgin".

		// So try direct
		String address = person.getAddress();
		// and fall back to in-room address
		if (address == null  ||  address.isEmpty())
			address = group + "/" + person.getName(); //$NON-NLS-1$
		final Chat new_chat = chat.createPrivateChat(address, null);
		return new IndividualChat(user, new_chat);
    }
}
