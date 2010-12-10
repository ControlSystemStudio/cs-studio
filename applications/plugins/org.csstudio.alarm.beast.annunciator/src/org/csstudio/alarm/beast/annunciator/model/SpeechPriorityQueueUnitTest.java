/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.annunciator.model;

import static org.junit.Assert.*;

import org.csstudio.alarm.beast.annunciator.Preferences;
import org.junit.Test;

/** JUnit Test of the SpeechPriorityQueue.
 *  @author Delphy Armstrong
 *  @author Kay Kasemir
 *  
 *      reviewed by Delphy 1/29/09
 */
@SuppressWarnings("nls")
public class SpeechPriorityQueueUnitTest
{
    /** Add items to queue, check if they can be retrieved
     *  in the expected order.
     */
    @Test
    public void testAdd()
    {
        Severity.initialize(Preferences.DEFAULT_SEVERITIES);
        final SpeechPriorityQueue q = new SpeechPriorityQueue();
      
        // Basic add/remove(poll)
        q.add(Severity.fromString("MINOR"), "Test");
        final AnnunciationMessage item = q.poll();
        assertEquals("Test", item.getMessage());
        assertEquals("MINOR", item.getSeverity().getName());
        
        // Check priority order
        q.add(Severity.fromString("MINOR"), "Three");
        q.add(Severity.fromString("MAJOR"), "Two");
        q.add(Severity.fromString("UNKNOWN"), "Four");
        q.add(Severity.fromString("FATAL"), "One");
        // Expect retrieval ordered by severity
        assertEquals("One", q.poll().getMessage());
        assertEquals("Two", q.poll().getMessage());
        assertEquals("Three", q.poll().getMessage());
        assertEquals("Four", q.poll().getMessage());
        // Queue should now be empty
        assertEquals(0, q.size());
   }
}
