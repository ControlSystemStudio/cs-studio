/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.chat;

import java.io.File;

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
	 *  @param password 
	 */
	public void doStartLogin(String user_name, String password);

	/** Check if a person matches ourself
	 *  <p>Called to prevent sending files etc. to ourself
	 *  @param person {@link Person}
	 *  @return <code>true</code> if person describes current user
	 */
	public boolean isOurself(Person person);

	/** User requested individual chat
	 *  @param person User name to contact
	 */
	public void doContact(Person person);

	/** User requested sending a file to a person
	 *  @param person receipient
	 *  @param file File to send
	 */
	public void doSendFile(Person person, File file);
}
