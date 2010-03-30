package org.csstudio.apputil.time;

import static org.junit.Assert.*;

import org.junit.Test;

/** JUnit test/demo of the BenchmarkTimer
 *  @author Kay Kasemir
 */
public class BenchmarkTimerTest
{
    @Test
    public void testTimer() throws Exception
    {
        final BenchmarkTimer timer = new BenchmarkTimer();
        Thread.sleep(500);
        timer.stop();
        System.out.println(timer.toString());
        assertEquals(timer.getSeconds(), 0.5, 0.1);
    }

    @Test
    public void testAccumulative() throws Exception
    {
        final BenchmarkTimer timer = new BenchmarkTimer();
        Thread.sleep(500);
        timer.stop();
        System.out.println(timer.toString());
        assertEquals(timer.getSeconds(), 0.5, 0.1);
        
        // Do something else
        Thread.sleep(1000);
        
        // Continue timer from when it was stopped
        timer.cont();
        Thread.sleep(500);
        timer.stop();
        System.out.println(timer.toString());
        assertEquals(timer.getSeconds(), 1.0, 0.1);
    }
}
