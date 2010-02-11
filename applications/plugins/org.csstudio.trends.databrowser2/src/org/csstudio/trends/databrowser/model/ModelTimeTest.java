package org.csstudio.trends.databrowser.model;

import static org.junit.Assert.*;

import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.junit.Test;

/** JUnit Test of model's start/end time handling
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ModelTimeTest
{
    @Test
    public void testTimes() throws InterruptedException
    {
        final double hour = 60.0*60;
        final Model model = new Model();
   
        // Check scroll mode
        model.setTimespan(hour);
        assertEquals(true, model.isScrollEnabled());
        assertEquals(hour, model.getTimespan(), 1.0);
        
        // 1 hour ago ... now?
        ITimestamp now = TimestampFactory.now();
        ITimestamp start = model.getStartTime();
        ITimestamp end = model.getEndTime();
        assertEquals(now.toDouble(), end.toDouble(), 5.0);
        assertEquals(now.toDouble() - hour, start.toDouble(), 5.0);
        
        System.out.println("Scroll starts OK, waiting 10 seconds...");
        Thread.sleep(10*1000);

        // Still 1 hour ago ... now, but 'now' has changed?
        double change = model.getEndTime().toDouble() - now.toDouble();
        assertEquals(10.0, change, 2.0);
        
        now = TimestampFactory.now();
        start = model.getStartTime();
        end = model.getEndTime();
        assertEquals(now.toDouble(), end.toDouble(), 5.0);
        assertEquals(now.toDouble() - hour, start.toDouble(), 5.0);

        System.out.println("Scroll updated OK, waiting 10 seconds...");
        Thread.sleep(10*1000);
        
        // Turn scrolling off
        model.enableScrolling(false);
        assertEquals(false, model.isScrollEnabled());

        // Start/end should no longer change
        assertEquals(start.toDouble(), model.getStartTime().toDouble(), 2.0);
        assertEquals(end.toDouble(),   model.getEndTime().toDouble(), 2.0);
        System.out.println("Start/end stayed constant in no-scroll mode");
    }
}
