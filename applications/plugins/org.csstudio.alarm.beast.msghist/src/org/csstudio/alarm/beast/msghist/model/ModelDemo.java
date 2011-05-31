/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.model;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.Semaphore;

import org.junit.Test;

/** JUnit test of the Model
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ModelDemo implements ModelListener
{
    private static final String URL = "jdbc:oracle:thin:@//172.31.75.138:1521/prod";
    private static final String USER = "css_msg_user";
    private static final String PASSWORD = "sns";
    private static final String SCHEMA = "MSG_LOG";

	private Semaphore got_response = new Semaphore(0);

    // ModelListener
    @Override
    public void modelChanged(final Model model)
    {
        System.out.println("Received model update");
        got_response.release();
    }

    @Test
    public void testGetMessages() throws Exception
    {
        final Model model = new Model(URL, USER, PASSWORD, SCHEMA, 1000);
        model.addListener(this);

        System.out.println("Starting query");
        model.setTimerange("-1day", "now");
        got_response.acquire();
        final Message[] messages = model.getMessages();
        System.out.println("Got " + messages.length + " messages");
        assertTrue(messages.length > 0);
    }
}
