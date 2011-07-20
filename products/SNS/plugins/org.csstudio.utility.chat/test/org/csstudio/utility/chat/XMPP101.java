/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.chat;

import java.util.Collection;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Presence;
import org.junit.Test;

/** Openfire running on localhost
 *
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class XMPP101
{
	static class DemoUser implements MessageListener, ChatManagerListener
	{
		final private String user;
		final XMPPConnection connection;

		public DemoUser(final String host, final String user, final String password) throws Exception
		{
			this.user = user;
			final ConnectionConfiguration config = new ConnectionConfiguration(host, 5222);
			config.setSelfSignedCertificateEnabled(true);
			config.setSASLAuthenticationEnabled(true);
			config.setSecurityMode(SecurityMode.enabled);
			connection = new XMPPConnection(config);
			connection.connect();

			System.out.println(user + " connected to " + connection.getServiceName());
			final AccountManager accounts = connection.getAccountManager();
			if (accounts.supportsAccountCreation())
			{
				try
				{	// Try to create account.
					// If already exists, this will fail
					accounts.createAccount(user, password);
				}
				catch (final Exception ex)
				{
					// Ignore
				}
			}
			connection.login(user, password);

			final Presence presence = new Presence(Presence.Type.available);
			presence.setStatus("I'm " + user);
			connection.sendPacket(presence);

			final Roster roster = connection.getRoster();
			final Collection<RosterGroup> groups = roster.getGroups();
			for (final RosterGroup group : groups)
				System.out.println("Group " + group.getName());

			final Collection<RosterEntry> entries = roster.getEntries();
			for (final RosterEntry entry : entries)
				System.out.println("Entry " + entry.getUser());

			final ChatManager chats = connection.getChatManager();
			chats.addChatListener(this);

		}

		public void chat(final String to) throws Exception
        {
			final Chat chat = connection.getChatManager().createChat(to, this);
			chat.sendMessage("Hello from " + user);
			chat.sendMessage("Sorry for the spam.");
        }

		/** @see ChatManagerListener */
		@Override
	    public void chatCreated(final Chat chat, final boolean createdLocally)
	    {
			System.out.println(user + " notified about " + chat.getThreadID() +
					" by " + chat.getParticipant() +
					", locally: " + createdLocally
			);
			chat.addMessageListener(this);
	    }

		/** @see IndividualChatListener */
		@Override
		public void processMessage(final Chat chat, final Message message)
		{
			if (message.getType() != Type.chat)
			{	// Dump unknown message in XML
				System.out.println(user + " received " + message.toXML());
			}
			else
			{	// Display message
				System.out.print(message.getFrom() + " -> ");
				System.out.print(message.getTo() + ": ");
	    		System.out.println(message.getBody());

	    		if (! message.getBody().startsWith("Thanks,"))
	    		{   // Sent lame auto-reply
					try
	                {
						String from = message.getFrom();
						final int i = from.indexOf('@');
						if (i > 0)
							from = from.substring(0, i);
						chat.sendMessage("Thanks, " + from + " for " + message.getBody());
	                }
					catch (final XMPPException e)
	                {
		                e.printStackTrace();
	                }
	    		}
			}
		}

		public void close()
        {
			connection.disconnect();
        }
	};

	@Test
	public void test() throws Exception
	{
		// Connection.DEBUG_ENABLED = true;
		final DemoUser fred = new DemoUser("localhost", "fred", "$fred");
		final DemoUser jane = new DemoUser("localhost", "jane", "$jane");

		fred.chat("jane@localhost");
		fred.chat("newbee@localhost");

		Thread.sleep(3000l);

		final DemoUser newbee = new DemoUser("localhost", "newbee", "$newbee");

		Thread.sleep(5000l);
		
		newbee.close();
		jane.close();
		fred.close();
	}
}
