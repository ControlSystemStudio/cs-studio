/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
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
