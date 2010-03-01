package org.csstudio.email;

import java.io.File;
import java.io.PrintStream;

import org.csstudio.email.internal.Base64Encoder;

import sun.net.smtp.SmtpClient;

/** Send EMail with text or image attachments.
 *  <p>
 *  Uses basic SMTP as supported by JVM 1.5.
 *  No 3rd party libraries, no authentication, no tests of the basic message
 *  body for strange characters.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EMailSender
{
    /** Message boundary marker */
    final private static String boundary = "==XOXOX-ThisIsTheMessageBoundary-XOXOX==";

    /** SMPT client connection */
    final private SmtpClient smtp;

    /** Stream for the message content */
    final private PrintStream message;

    /** Initialize
     *  @param host SMTP Host
     *  @param from Sender's email
     *  @param to   Receiver's email
     *  @param subject Message subject
     * @throws Exception on I/O error
     */
    public EMailSender(final String host, final String from, final String to,
                  final String subject) throws Exception
    {
        smtp = new SmtpClient(host);
        smtp.from(from);
        smtp.to(to);
        message = smtp.startMessage();
        
        message.println("To: " + to);
        message.println("From: " + from);
        message.println("Subject: Test");

        message.println("Content-Type: multipart/mixed; boundary=\"" + boundary + "\"");
        message.println();
        message.println("This is a multi-part message in MIME format.");
        message.println();
    }

    /** @param text Message body, added to email */
    public void addText(final String text)
    {
        message.println("--" + boundary);
        message.println("Content-Type: text/plain");
        message.println("Content-Transfer-Encoding: binary");
        message.println();
        message.println(text);
        message.println();
    }
        
    /** @param filename Name of text file to attach 
     *  @throws Exception on File I/O error
     */
    public void attachText(final String filename) throws Exception
    {
        message.println("--" + boundary);
        message.println("Content-Type: text/plain");
        message.println("Content-Transfer-Encoding: base64");
        message.println("Content-Disposition: attachment; filename=\"" + basename(filename) + "\"");
        message.println();
        final Base64Encoder encoder = new Base64Encoder(message);
        encoder.encode(filename);
        message.println();
    }

    /** @param filename Name of image file to attach 
     *  @throws Exception on File I/O error
     */
    public void attachImage(final String filename) throws Exception
    {
        final int end = filename.lastIndexOf('.');
        if (end < 0)
            throw new Exception("Missing file ending");
        final String type = filename.substring(end + 1).toLowerCase();
        message.println("--" + boundary);
        message.println("Content-Type: image/" + type);
        message.println("Content-Transfer-Encoding: base64");
        message.println("Content-Disposition: attachment; filename=\"" + basename(filename) + "\"");
        message.println();
        final Base64Encoder encoder = new Base64Encoder(message);
        encoder.encode(filename);
        message.println();
    }

    /** @param filename File name with full path
     *  @return Just the file name without path
     */
    private String basename(final String filename)
    {
        final File file = new File(filename);
        return file.getName();
    }

    /** Close the message, send it out 
     *  @throws Exception on I/O error
     */
    public void close() throws Exception
    {
        message.println("--" + boundary + "--");
        smtp.closeServer();
    }
}
