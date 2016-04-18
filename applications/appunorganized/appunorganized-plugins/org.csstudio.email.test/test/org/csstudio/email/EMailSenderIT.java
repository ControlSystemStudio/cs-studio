/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.email;

import java.io.File;

import org.junit.Assert;

import org.junit.Test;

/** JUnit test of the Mailer
 *  <p>
 *  Will not work without adjusting the host, image file name etc.
 *  @author Kay Kasemir
 *  @author Bastian Knerr - Changed to TestDataProvider
 */
@SuppressWarnings("nls")
public class EMailSenderIT
{
    /**
     * Please, specify these settings to your site's host and email address.
     */
    final private static String HOST = System.getProperty("smtp.host");
    private static String FROM = System.getProperty("sender");
    static {
        if (FROM == null) {
            FROM = System.getProperty("user.email"); // preferable for user triggered tests!
            if (FROM == null) {
                Assert.fail("No valid sender or receiver property has been defined. Use a dummy one.");
            }
        }
    }
    final private static String TO = FROM;


    @Test
    public void testMailer() throws Exception
    {
        /**
         * Please, overwrite these settings to your host and email address.
         */
        final EMailSender mailer = new EMailSender(HOST, FROM, TO, "Test Subject");

        mailer.addText("Hello, this is a test");

        final File textFile = new File("./testfile.txt");
        mailer.attachText(textFile.getAbsolutePath());

        final File imgFile = new File("./test.jpg");
        mailer.attachImage(imgFile.getAbsolutePath());

        mailer.close();
    }
}
