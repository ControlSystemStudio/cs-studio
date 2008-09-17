package org.csstudio.debugging.jmsmonitor;

import static org.junit.Assert.*;

import org.junit.Test;

/** JUnit Plug-in test of the Model
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ModelTest implements ModelListener
{
    private int messages = 0;

    @Test
    public void testModel() throws Exception
    {
        String url = "failover:(tcp://ics-srv02.sns.ornl.gov:61616,tcp://ics-srv-epics1.ics.sns.gov:61616)";
        String topic = "LOG";
        final Model model = new Model(url, topic);
        model.addListener(this);
        
        System.out.println("Listening to messages, " +
        		"so start something that produces them...");
        Thread.sleep(20 * 1000);
        
        model.close();
        System.out.println("Done.");
        
        assertTrue(messages > 0);
    }

    public void modelChanged(final Model model)
    {
        ++messages ;
        System.out.println(model.getMessages()[0]);
    }
}
