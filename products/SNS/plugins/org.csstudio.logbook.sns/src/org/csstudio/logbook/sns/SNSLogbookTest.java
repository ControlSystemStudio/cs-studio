package org.csstudio.logbook.sns;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.csstudio.logbook.ILogbook;
import org.junit.Test;

/** JUnit test of the SNS Logbook.
 *  <p>
 *  Application code should use org.csstudio.logbook.LogbookFactory
 *  and not directly access a specific implementation like SNS...
 *  
 *  @author Delphy Nypaver
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SNSLogbookTest
{
    private static final String URL =
        "jdbc:oracle:thin:@//snsdb1.sns.ornl.gov:1521/prod";
//  "jdbc:oracle:thin:@snsdev3.sns.ornl.gov:1521:devl";

    private static final String LOGBOOK = "Scratch Pad";

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

        final String short_text = "This is a test entry";
        // Create a very large text string to force an attachment
        String long_text = "abcdefghijklm";        
        for(int i=0;i<4011;i++)
           long_text=long_text.concat(" "+i);

        final ILogbook logbook =
            new SNSLogbookFactory().connect(URL, LOGBOOK, user, password);
        assertNotNull(logbook);

        try
        {
            String title = "Test Entry (short)";
            logbook.createEntry(title, short_text, null);
            if (image.trim().length() > 0)
                logbook.createEntry("Image " + title, short_text, image);
    
            title = "Test Entry (long)";
            logbook.createEntry(title, long_text, null);
            if (image.trim().length() > 0)
                logbook.createEntry("Image " + title, long_text, image);
        }
        finally
        {
            logbook.close();
        }
    }
}
