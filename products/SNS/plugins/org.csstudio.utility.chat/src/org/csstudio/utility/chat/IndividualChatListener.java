package org.csstudio.utility.chat;

/** Listener to a chat with another individual
 *  @author Kay Kasemir
 */
public interface IndividualChatListener
{
	/** Received a message
	 *  @param from Name of sender
	 *  @param is_self Did we send this message?
	 *  @param text Message text
	 */
	public void receive(String from, boolean is_self, String text);
}
