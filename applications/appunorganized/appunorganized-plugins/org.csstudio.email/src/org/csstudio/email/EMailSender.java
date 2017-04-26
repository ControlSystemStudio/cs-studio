/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.email;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import org.csstudio.email.encoder.Base64Encoder;

import sun.net.smtp.SmtpClient;

/** Send EMail with text or image attachments.
 *  <p>
 *  Uses basic SMTP as supported by JVM 1.5.
 *  No 3rd party libraries, no authentication, no tests of the basic message
 *  body for strange characters.
 *
 *  @author Kay Kasemir
 *  @author Bastian Knerr - documented specific exception types
 */
@SuppressWarnings({ "nls", "restriction" })
public class EMailSender
{
    /** Message boundary marker */
    final private static String boundary = "==XOXOX-ThisIsTheMessageBoundary-XOXOX==";

    /** SMPT client connection */
    final private SmtpClient smtp;

    /** Stream for the message content */
    final private PrintStream message;

    /** @return <code>true</code> if EMail seems to be supported (SMTP host configured) */
    public static boolean isEmailSupported()
    {
        return Preferences.getSMTP_Host().length() > 0;
    }

    /** Initialize
     *  @param host SMTP Host
     *  @param from Sender's email
     *  @param to   Receiver's email
     *  @param subject Message subject
     *  @throws IOException on I/O error
     */
    public EMailSender(final String host, final String from, final String to,
                       final String subject) throws IOException
    {
        smtp = new SmtpClient(host);
        smtp.from(from);
        smtp.to(to);
        message = smtp.startMessage();

        message.println("To: " + to);
        message.println("From: " + from);
        message.println("Subject: " + subject);

        message.println("Content-Type: multipart/mixed; boundary=\"" + boundary + "\"");
        message.println();
        message.println("This is a multi-part message in MIME format.");
        message.println();
    }

    /**
     * @param text Message body, added to email
     */
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
     *  @throws IOException on File I/O error
     *  @throws FileNotFoundException a file not found error
     */
    public void attachText(final String filename) throws FileNotFoundException, IOException
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

    /** @param filename Name of image file to attach. Must contain file ending like ".png" or ".jpg".
     *                  Unclear which file types beyond PNG and JPG are supported.
     *  @throws IOException on File I/O error
     *  @throws FileNotFoundException a file not found error
     */
    public void attachImage(final String filename) throws FileNotFoundException, Exception
    {
        final int end = filename.lastIndexOf('.');
        if (end < 0)
        {
            throw new Exception("Missing file ending, required to determine image type");
        }
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
     *  @throws IOException on I/O error
     */
    public void close() throws IOException
    {
        message.println("--" + boundary + "--");
        smtp.closeServer();
    }
}
