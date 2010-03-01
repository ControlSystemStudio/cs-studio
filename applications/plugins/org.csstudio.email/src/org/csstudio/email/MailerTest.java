package org.csstudio.email;

import org.junit.Test;

/** JUnit test of the Mailer
 *  <p>
 *  Will not work without adjusting the host, image file name etc.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MailerTest
{
    final private static String host = "smtp.ornl.gov";
    final private static String from = "kasemirk@ornl.gov";
    final private static String to = from;

    @Test
    public void testMailer() throws Exception
    {
        final Mailer mailer = new Mailer(host, from, to, "Test Subject");
        mailer.addText("Hello, this is a test");
        mailer.attachText("testfile.txt");
        mailer.attachImage("/tmp/test.png");
        mailer.attachText("src/org/csstudio/email/MailerTest.java");
        mailer.close();
    }
}
