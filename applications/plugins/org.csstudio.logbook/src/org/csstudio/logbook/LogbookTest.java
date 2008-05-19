package org.csstudio.logbook;

import static org.junit.Assert.*;

import org.junit.Test;

/** JUnit Plugin test, requires exactly one plugin that provides the
 *  ILogbookFactory extension point to be loaded.
 *  
 *  Can run with the application set to "Headless Mode".
 *  
 *  Pretty much everything in there depends on the particular setup:
 *  User, password, test image will likely require changes to work
 *  for you.
 *
 *  @author nypaver
 *  @author Kay Kasemir
 */
public class LogbookTest
{
    @SuppressWarnings("nls")
    @Test
    public void testLoogbook() throws Exception
    {
        final String user = "EPICS_MON";
        // TODO Don't put the pw into CVS
        final String password = "RCK26WRZ";

        ILogbook logbook = LogbookFactory.connect(user, password);
        assertNotNull(logbook);

        String title = "Test Image Entry";
        final String text = "This is a test entry";

        // final String image = "/home/ky9/css_empty_databrowser.png";
        final String image = "/home/nypaver/hprf.jpg";

        logbook.createEntry(title, text, image);

        title = "Another Test Entry";
        logbook.createEntry(title, text, null);

        logbook.close();
    }
}
