package org.csstudio.email;

import org.junit.Test;

/** JUnit test of the Mailer
 *  <p>
 *  Will not work without adjusting the host, image file name etc.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EMailSenderTest
{
    final private static String host = "krynfs.desy.de";
    final private static String from = "bastian.knerr@desy.de";
    final private static String to = from;

    @Test
    public void testMailer() throws Exception
    {
        final EMailSender mailer = new EMailSender(host, from, to, "Test Subject");
        mailer.addText("Hello, this is a test");
        mailer.attachText("testfile.txt");
        mailer.attachImage("test.jpg");
        mailer.attachText("src/org/csstudio/email/EMailSender.java");
        mailer.close();
    }
}
