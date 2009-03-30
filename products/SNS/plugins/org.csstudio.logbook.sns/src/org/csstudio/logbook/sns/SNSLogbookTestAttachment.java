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
 *  Testing adding attachments to the logbook.
 *  
 *  @author Delphy Nypaver
 */
@SuppressWarnings("nls")
public class SNSLogbookTestAttachment
{
    private static final String URL =
       "jdbc:oracle:thin:@//snsdb1.sns.ornl.gov:1521/prod";
//       "jdbc:oracle:thin:@snsdev3.sns.ornl.gov:1521:devl";
    
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
        
        /* Create a very large text string to force an attachment */
        String text = "abcdefghijklm";        
        for(int i=0;i<4011;i++)
           text=text.concat(" "+i);
        
        /* Connect to the logbook database */
        ILogbook logbook =
            new SNSLogbookFactory().connect(URL, LOGBOOK, user, password);
        assertNotNull(logbook);

        try
        {
        /* If an image is entered send 2 attachments to the logbook */
            if (image!=null && image.trim().length() > 0)
                logbook.createEntry("Test Entry", text, image);
            
            /* Otherwise send 1 attachment to the logbook */
            else
            {
               String title = "Another Test Entry";
               logbook.createEntry(title, text, null);
            }
        }
        finally
        {
            logbook.close();
        }
    }
}
