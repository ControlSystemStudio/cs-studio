package org.csstudio.utility.chat;

/** Listener to a group chat
 *  @author Kay
 */
public interface GroupChatListener extends IndividualChatListener
{
	/** Members of chat group have changed
	 *  @param nerds Names of nerds in the group
	 */
	public void groupMemberUpdate(String[] nerds);
	
	/** Handle invitation to individual chat
	 *  @param from Person who invited us
	 *  @return {@link IndividualChatGUI} if interested, <code>null</code> to ignore
	 */
	public IndividualChatGUI receivedInvitation(String from);
}
