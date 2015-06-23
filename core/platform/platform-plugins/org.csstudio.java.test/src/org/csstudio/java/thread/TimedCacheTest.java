package org.csstudio.java.thread;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/** JUnit test of the {@link TimedCache}
 *  @author Kay Kasemir
 */
public class TimedCacheTest
{
    @Test
    public void testTimedCache() throws Exception
    {
        final TimedCache<Integer, String> cache = new TimedCache<Integer, String>(2);//2 seconds

        // Originally, cache is empty
        String entry = cache.getValue(1);
        assertNull(entry);

        // Add some entries
        cache.remember(1, "test1");
        cache.remember(2, "test2");

        // Check if they're found
        entry = cache.getValue(1);
        System.out.println("\nvalue of 1 = "+ entry);
        assertEquals("test1", entry);

        entry = cache.getValue(2);
        System.out.println("value of 2 = "+  entry);
        assertEquals("test2", entry);


        // Should still be found after 1 sec
        Thread.sleep(1000);
        assertEquals("test1", cache.getValue(1));
        assertEquals("test2", cache.getValue(2));

        // None should be found after total of 3 secs
        Thread.sleep(2000);

        entry = cache.getValue(1);
        System.out.println("\nvalue of 1 = "+ entry);
        assertNull(entry);
        entry = cache.getValue(2);
        System.out.println("value of 2 = "+  entry);
        assertNull(entry);
        System.out.println("Cache entries did expire");
    }
}
