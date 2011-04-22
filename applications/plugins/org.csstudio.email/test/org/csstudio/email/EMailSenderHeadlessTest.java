/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.email;

import junit.framework.Assert;

import org.csstudio.testsuite.util.TestDataProvider;
import org.junit.Test;

/** JUnit test of the Mailer
 *  <p>
 *  Will not work without adjusting the host, image file name etc.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EMailSenderHeadlessTest
{
    // Get site specific test data provider
    public static org.csstudio.testsuite.util.TestDataProvider PROV = createTestDataProvider();
    private static TestDataProvider createTestDataProvider() {
        try {
            return TestDataProvider.getInstance(Activator.ID);
        } catch (final Exception e) {
            Assert.fail("Unexpected exception creating the test data provider for plugin " +
                        Activator.ID + ".\n" + e.getMessage());
        }
        return null;
    }

    /**
     * Please, specify these settings to your site's host and email address.
     */
    final private static String HOST = (String) PROV.get("smtp.host");
    private static String FROM = (String) PROV.get("sender");
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
        mailer.attachText("./testfile.txt");
        mailer.attachImage("./test.jpg");
        mailer.attachText("./src/org/csstudio/email/EMailSender.java");
        mailer.close();
    }
}
