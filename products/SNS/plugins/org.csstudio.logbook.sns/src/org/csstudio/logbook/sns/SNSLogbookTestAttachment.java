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
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SNSLogbookTestAttachment
{
    private static final String URL =
        "jdbc:oracle:thin:@//snsdb1.sns.ornl.gov:1521/prod";
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
        
        String text = "abcdefghijklm";        
        for(int i=0;i<4011;i++)
           text=text.concat(" a");
        
        ILogbook logbook =
            new SNSLogbookFactory().connect(URL, LOGBOOK, user, password);
        assertNotNull(logbook);

        try
        {
            String title = "Test Entry";
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
