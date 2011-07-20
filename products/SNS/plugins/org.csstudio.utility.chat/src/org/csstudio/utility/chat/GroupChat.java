package org.csstudio.utility.chat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.DefaultParticipantStatusListener;
import org.jivesoftware.smackx.muc.MultiUserChat;

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
@SuppressWarnings("nls")
public class GroupChat
{
	/** Connection to server */
	final private XMPPConnection connection;
	
	/** Name of chat group */
	final private String group;
	
	/** Connected chat group */
	private MultiUserChat chat;
	
	/** Nerds in the chat group */
	final private Set<String> nerds = new HashSet<String>();
	
	/** Listeners to the {@link GroupChat} */
	final private List<GroupChatListener> listeners = new CopyOnWriteArrayList<GroupChatListener>();
	
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
	 *  @throws Exception on error
	 */
    public void connect(final String user) throws Exception
    {
    	// Default password
		final String password = "$" + user;
		
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
		connection.login(user, password, "css");

		connection.sendPacket(
			new Presence(Presence.Type.available, "online", 0, Presence.Mode.chat));
		
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
				synchronized (nerds)
                {
					nerds.add(nick);
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
			final Iterator<String> occupants = chat.getOccupants();
			while (occupants.hasNext())
			{
				final String occupant = occupants.next();
				final String nick = StringUtils.parseResource(occupant);
				nerds.add(nick);
			}
			// Add ourself, which we don't always seem to get from server
			nerds.add(user);
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
						nick = "SERVER";
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
				for (GroupChatListener listener : listeners)
				{
					listener.receivedInvitation(chat.getParticipant());
				}
			}
		});
    }
    
    /** Notify listeners about current nerd list */
	private void fireNerdAlert()
    {
		final String[] array;
		synchronized (nerds)
        {
			array = nerds.toArray(new String[nerds.size()]);
        }
		Arrays.sort(array);
		for (GroupChatListener listener : listeners)
			listener.groupMemberUpdate(array);
    }

	/** Disconnect from chat server */
    public void disconnect()
    {
    	if (chat != null)
    		chat.leave();
    	listeners.clear();
    	connection.disconnect();
    }

    /** @param text Message to send to the chat
     *  @throws Exception on error
     */
	public void send(final String text) throws Exception
    {
    	chat.sendMessage(text);
    }
}
