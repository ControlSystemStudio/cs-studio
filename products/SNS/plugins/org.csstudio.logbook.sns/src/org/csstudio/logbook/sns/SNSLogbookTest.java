package org.csstudio.logbook.sns;

import static org.junit.Assert.*;

import org.csstudio.logbook.ILogbook;
import org.junit.Ignore;
import org.junit.Test;

/** JUnit test of the SNS Logbook.
 *  <p>
 *  Application code should use org.csstudio.logbook.LogbookFactory
 *  and not directly access a specific implementation like SNS...
 *  
 *  @author Delphy Nypaver Armstrong
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SNSLogbookTest
{
    private static final String URL =
        "jdbc:oracle:thin:@//snsdb1.sns.ornl.gov:1521/prod";
//  "jdbc:oracle:thin:@snsdev3.sns.ornl.gov:1521:devl";

    private static final String LOGBOOK = "Scratch Pad";

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // Set user, password, image to use.
    // Then remove at least the password before checking into CVS!
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private static final String USER = "user_name";
    private static final String PASSWORD = "";
    private static final String IMAGE = "/tmp/print.gif";

    @Test
    public void shortText() throws Exception
    {
        final ILogbook logbook =
            new SNSLogbookFactory().connect(URL, LOGBOOK, USER, PASSWORD);
        assertNotNull(logbook);
        try
        {
            logbook.createEntry("Test Entry", "This is a test entry", null);
        }
        finally
        {
            logbook.close();
        }
    }

    @Test
    public void longText() throws Exception
    {
        final ILogbook logbook =
            new SNSLogbookFactory().connect(URL, LOGBOOK, USER, PASSWORD);
        assertNotNull(logbook);
        try
        {
            logbook.createEntry("Test Entry (long)", getLongText(), null);
        }
        finally
        {
            logbook.close();
        }
    }
    
    @Test
    public void shortTextWithImage() throws Exception
    {
        final ILogbook logbook =
            new SNSLogbookFactory().connect(URL, LOGBOOK, USER, PASSWORD);
        assertNotNull(logbook);
        try
        {
            logbook.createEntry("Test Entry with image", "Look at that image", IMAGE);
        }
        finally
        {
            logbook.close();
        }
    }

    @Test
    public void longTextWithImage() throws Exception
    {
        final ILogbook logbook =
            new SNSLogbookFactory().connect(URL, LOGBOOK, USER, PASSWORD);
        assertNotNull(logbook);
        try
        {
            logbook.createEntry("Test Entry (long) with image", getLongText(), IMAGE);
        }
        finally
        {
            logbook.close();
        }
    }


    /** @return Very large text string to force an attachment */
    private String getLongText()
    {
        final StringBuilder long_text = new StringBuilder();
        long_text.append("abcdefghijklm");        
        for(int i=0;i<4011;i++)
           long_text.append(" "+i);
        return long_text.toString();
    }
}
