package org.csstudio.logbook;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Test;

/** JUnit Plugin test, requires exactly one plugin that provides the
 *  ILogbookFactory extension point to be loaded.
 *  
 *  Can run with the application set to "Headless Mode".
 *  
 *  Pretty much everything in there depends on the particular setup:
 *  User, password, test image, so it'll prompt for them.
 *  Not a true unit test that runs on its own.
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
        final BufferedReader command_line
            = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("User      : ");
        final String user = command_line.readLine();
        
        System.out.print("Password  : ");
        final String password = command_line.readLine();

        System.out.print("Image File: ");
        final String image = command_line.readLine();

        ILogbook logbook = LogbookFactory.connect(user, password);
        assertNotNull(logbook);

        String title = "Test Entry";
        final String text = "This is a test entry";
        logbook.createEntry(title, text, null);

        title = "Another Test Entry";
        logbook.createEntry(title, text, image);

        logbook.close();
    }
}
