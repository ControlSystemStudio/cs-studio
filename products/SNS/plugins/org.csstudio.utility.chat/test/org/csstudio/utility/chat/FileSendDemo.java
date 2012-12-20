/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.chat;

import java.io.File;

/** JUnit demo for sending a file
 *  @see FileReceiveDemo
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class FileSendDemo
{
	final private static String RECEIVER_NAME = "test_receiver";
	final private static String RECEIVER_ADDRESS = "test_receiver@localhost/css";
	final private static String DEMO_FILE = "/tmp/demo.xml";

	public static void main(String[] args) throws Exception
    {
	    final GroupChat chat = new GroupChat("localhost", "css@conference.localhost");
	    chat.connect("test_sender", "$test_sender");
	    chat.addListener(new GroupChatListener()
		{
			@Override
			public void receive(String from, boolean is_self, String text)
			{

				System.out.println("Received message " + text);
			}
			
			@Override
			public void startIndividualChat(String from, IndividualChat chat)
			{
			}
			
			@Override
			public IndividualChatGUI receivedInvitation(String from)
			{
				return null;
			}
			
			@Override
			public File receivedFile(String requestor, String fileName)
			{
				return null;
			}
			
			@Override
			public void groupMemberUpdate(Person[] nerds)
			{
				System.out.println("Logged in:");
				for (Person person : nerds)
					System.out.println(person);
			}
		});
	    
	    chat.sendFile(new Person(RECEIVER_NAME, RECEIVER_ADDRESS), new File(DEMO_FILE));
	    Thread.sleep(5000);
	    chat.disconnect();
	    Thread.sleep(3000);
    }
}
