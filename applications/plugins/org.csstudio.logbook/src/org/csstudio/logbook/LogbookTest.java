package org.csstudio.logbook;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Test;

/** JUnit Plug-in test, requires exactly one plugin that provides the
 *  ILogbookFactory extension point to be loaded.
 *  
 *  Should run via "Run As/JUnit Plug-in Test",
 *  with the application set to "Headless Mode".
 *  
 *  Pretty much everything in here depends on the particular setup:
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
        // Obtain a logbook factory.
        // This requires the presence of a plugin that actually
        // implements the logbook extension point.
        final ILogbookFactory logbook_factory = LogbookFactory.getInstance();
        assertNotNull(logbook_factory);
        
        // Show available logbooks.
        final String[] logbooks = logbook_factory.getLoogbooks();
        assertNotNull(logbooks);
        System.out.println("Available logbooks:");
        for (String log_name : logbooks)
            System.out.println(log_name);
        final String default_logbook = logbook_factory.getDefaultLogbook();
        assertNotNull(default_logbook);
        System.out.println("Default logbook: " + default_logbook);
        
        // Get user/pw/... for creating entries
        final BufferedReader command_line
            = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("User      : ");
        final String user = command_line.readLine();
        System.out.print("Password  : ");
        final String password = command_line.readLine();
        System.out.print("Image File: ");
        final String image = command_line.readLine();
        final String logbook_name = "Scratch Pad";
        
        // Create entries
        final ILogbook logbook =
            logbook_factory.connect(logbook_name, user, password);
        try
        {
            String title = "Test Entry";
            final String text = "This is a test entry";
            logbook.createEntry(title, text, null);
    
            if (image.trim().length() > 0)
            {
                title = "Another Test Entry";
                logbook.createEntry(title, text, image);
            }
        }
        finally
        {
            logbook.close();
        }
    }
}
