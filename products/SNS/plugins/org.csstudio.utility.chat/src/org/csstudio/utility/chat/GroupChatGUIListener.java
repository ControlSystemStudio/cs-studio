package org.csstudio.utility.chat;

/** Listener to the {@link GroupChatGUI}
 *  @author Kay Kasemir
 */
public interface GroupChatGUIListener
{
	/** Log in to chat was requested via GUI.
	 * 
	 *  <p>When done, call {@link GroupChatGUI#updateLogin(String)}
	 *  @param user_name User name to use for log in
	 */
	public void doStartLogin(String user_name);

	/** User entered message to send in GUI
	 *  @param message_text Message text
	 */
	public void doSend(String message_text);
}
