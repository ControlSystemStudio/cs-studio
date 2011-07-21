/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.chat;

/** Listener to a group chat
 *  @author Kay
 */
public interface GroupChatListener extends IndividualChatListener
{
	/** Members of chat group have changed
	 *  @param nerds Names of nerds in the group
	 */
	public void groupMemberUpdate(Person[] nerds);
	
	/** Handle invitation to individual chat
	 *  @param from Person who invited us
	 *  @return {@link IndividualChatGUI} if interested, <code>null</code> to ignore
	 */
	public IndividualChatGUI receivedInvitation(String from);

	/** If listener responds to invitation with GUI,
	 *  it will then receive the handle to the
	 *  individual chat
	 *  @param from Person who invited us
	 *  @param chat {@link IndividualChat} that we accepted
	 */
	public void startIndividualChat(final String from, IndividualChat chat);
}
