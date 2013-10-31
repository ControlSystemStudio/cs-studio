/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;

import org.csstudio.alarm.beast.msghist.model.Message;
import org.csstudio.alarm.beast.msghist.model.Model;
import org.csstudio.alarm.beast.msghist.model.ModelListener;
import org.junit.Test;

/** JUnit test of the Model
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ModelDemo implements ModelListener
{
	private CountDownLatch got_response = new CountDownLatch(1);

    // ModelListener
    @Override
    public void modelChanged(final Model model)
    {
        System.out.println("Received model update");
        got_response.countDown();
    }

    @Test(timeout=10000)
    public void testGetMessages() throws Exception
    {
        final Model model = new Model(MessageRDBTest.URL, MessageRDBTest.USER, MessageRDBTest.PASSWORD, MessageRDBTest.SCHEMA, 1000, null);
        model.addListener(this);

        System.out.println("Starting query");
        model.setTimerange("-1day", "now");
        got_response.await();
        final Message[] messages = model.getMessages();
        System.out.println("Got " + messages.length + " messages");
        assertTrue(messages.length > 0);
    }

	@Override
	public void onErrorModel(String errorMsg) {
		// TODO Auto-generated method stub
		
	}
}
