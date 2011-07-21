/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.chat;

/** Listener to the {@link GroupChatGUI}
 *  @author Kay Kasemir
 */
public interface GroupChatGUIListener
	extends IndividualChatGUIListener
{
	/** Log in to chat was requested via GUI.
	 * 
	 *  <p>When done, call {@link GroupChatGUI#updateLogin(String)}
	 *  @param user_name User name to use for log in
	 */
	public void doStartLogin(String user_name);

	/** User requested individual chat
	 *  @param person User name to contact
	 */
	public void doContact(Person person);
}
