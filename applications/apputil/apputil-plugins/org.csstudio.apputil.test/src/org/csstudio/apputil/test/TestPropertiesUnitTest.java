package org.csstudio.apputil.test;

import static org.junit.Assert.*;

import java.util.Enumeration;

import org.junit.Test;

/** JUnit test of the TestProperties
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TestPropertiesUnitTest
{
    private static final String TEST_SETTINGS = "test_settings";

    @Test
    public void testProperties() throws Exception
    {
        final TestProperties settings = new TestProperties();
        assertNotNull(settings);

        // Access one settings. If it is defined, it must be numeric
        final String text = settings.getString(TEST_SETTINGS);
        if (text == null)
            System.out.println("Skipping " + TEST_SETTINGS);
        else
        {
            System.out.println(TEST_SETTINGS + " = " + text);
            System.out.println(".. as number: " + settings.getInteger(TEST_SETTINGS));
        }

        // Dump names of all settings
        final Enumeration<String> keys = settings.getKeys();
        if (! keys.hasMoreElements())
        {
            System.out.println("There seem to be no test settings, skipping.");
            return;
        }
        System.out.println("The following test properties are available:");
        while (keys.hasMoreElements())
            System.out.println(keys.nextElement());
    }
}
