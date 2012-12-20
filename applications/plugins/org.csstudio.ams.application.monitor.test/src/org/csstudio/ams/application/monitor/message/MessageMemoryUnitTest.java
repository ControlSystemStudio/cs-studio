
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.ams.application.monitor.message;

import org.csstudio.ams.application.monitor.message.AbstractCheckMessage;
import org.csstudio.ams.application.monitor.message.InitiatorMessage;
import org.csstudio.ams.application.monitor.message.MessageMemory;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author mmoeller
 * @version 1.0
 * @since 13.04.2012
 */
public class MessageMemoryUnitTest {
    
    @Test
    public void testOrdering() {
        
        MessageMemory<AbstractCheckMessage> memory = new MessageMemory<AbstractCheckMessage>();
        
        InitiatorMessage m1 = new InitiatorMessage();
        m1.setEventTimeValue("2012-04-01 00:00:00.000");
        Assert.assertEquals(1333231200000L, m1.getEventTimeAsLong());
        
        InitiatorMessage m2 = new InitiatorMessage();
        m2.setEventTimeValue("2012-04-02 00:00:00.000");
        Assert.assertEquals(1333317600000L, m2.getEventTimeAsLong());

        InitiatorMessage m3 = new InitiatorMessage();
        m3.setEventTimeValue("2012-04-03 00:00:00.000");
        Assert.assertEquals(1333404000000L, m3.getEventTimeAsLong());
        
        memory.add(m3);
        memory.add(m2);
        memory.add(m1);
        
        Assert.assertSame(m1, memory.pollFirst());
        Assert.assertSame(m2, memory.pollFirst());
        Assert.assertSame(m3, memory.pollFirst());
    }
    
    @Test
    public void testClassValueComparison() {
     
        MessageMemory<AbstractCheckMessage> memory = new MessageMemory<AbstractCheckMessage>();
        
        InitiatorMessage m1 = new InitiatorMessage();
        m1.setEventTimeValue("2012-04-01 00:00:00.000");
        m1.setClassValue("1234567890");
        
        InitiatorMessage m2 = new InitiatorMessage();
        m2.setEventTimeValue("2012-04-02 00:00:00.000");
        m2.setClassValue("567890abcdef");

        InitiatorMessage m3 = new InitiatorMessage();
        m3.setEventTimeValue("2012-04-03 00:00:00.000");
        m3.setClassValue("fedcba987654");
        
        memory.add(m3);
        memory.add(m2);
        memory.add(m1);

        Assert.assertTrue(memory.containsMessageWithClassValue("1234567890"));
        Assert.assertTrue(memory.containsMessageWithClassValue("567890abcdef"));
        Assert.assertTrue(memory.containsMessageWithClassValue("fedcba987654"));
        
        Assert.assertFalse(memory.containsMessageWithClassValue("0011223344"));
    }
}
