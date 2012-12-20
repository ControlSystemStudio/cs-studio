/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.chat;

import java.io.File;

/** JUnit demo for receiving a file
 *  @see FileSendDemo
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class FileReceiveDemo
{
    public static void main(String[] args) throws Exception
    {
	    final GroupChat chat = new GroupChat("localhost", "css@conference.localhost");
	    chat.connect("test_receiver", "$test_receiver");
	    chat.addListener(new GroupChatListener()
		{
			@Override
			public void receive(final String from, final boolean is_self, final String text)
			{

				System.out.println("Received message " + text);
			}
			
			@Override
			public void startIndividualChat(final String from, final IndividualChat chat)
			{
			}
			
			@Override
			public IndividualChatGUI receivedInvitation(final String from)
			{
				return null;
			}
			
			@Override
			public File receivedFile(final String requestor, final String fileName)
			{
				System.out.println("Receiving file " + fileName);
				return new File("/tmp/received.txt");
			}
			
			@Override
			public void groupMemberUpdate(final Person[] nerds)
			{
				System.out.println("Logged in:");
				for (Person person : nerds)
					System.out.println(person);
			}
		});
	    synchronized (FileReceiveDemo.class)
        {
	    	FileReceiveDemo.class.wait();
        }
    }
}
