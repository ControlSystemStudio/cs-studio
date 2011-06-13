/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import org.junit.Test;

/** JUnit Plug-in demo, requires exactly one plugin that provides the
 *  ILogbookFactory extension point to be loaded.
 *
 *  Should run via "Run As/JUnit Plug-in Test",
 *  with the application set to "Headless Mode".
 *
 *  Pretty much everything in here depends on the particular setup:
 *  User, password, test image, so it'll prompt for them.
 *  Not a true unit test that runs on its own.
 *
 *  @author nypaver
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LogbookDemo
{
    private File createImage() throws Exception
    {
        final BufferedImage buf = new BufferedImage(300, 150, BufferedImage.TYPE_INT_RGB);
        final Graphics2D gc = buf.createGraphics();
        gc.setColor(Color.YELLOW);
        gc.fillRect(0, 0, buf.getWidth(), buf.getHeight());
        gc.setColor(Color.BLUE);
        gc.drawString("Test", 10, buf.getHeight()/2);
        final File file = File.createTempFile("test", ".png");
        ImageIO.write(buf, "png", file);
        return file;
    }

    @Test
    public void testLoogbook() throws Exception
    {
        // Obtain a logbook factory.
        // This requires the presence of a plugin that actually
        // implements the logbook extension point.
        final ILogbookFactory logbook_factory = LogbookFactory.getInstance();
        assertNotNull(logbook_factory);

        // Show available logbooks.
        final String[] logbooks = logbook_factory.getLogbooks();
        assertNotNull(logbooks);
        System.out.println("Available logbooks:");
        for (String log_name : logbooks)
            System.out.println(log_name);
        final String default_logbook = logbook_factory.getDefaultLogbook();
        assertNotNull(default_logbook);
        System.out.println("Default logbook: " + default_logbook);

        // Get user/pw/... for creating entries
        final BufferedReader command_line
            = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("User      : ");
        final String user = command_line.readLine();
        System.out.print("Password  : ");
        final String password = command_line.readLine();
        final String logbook_name = "Scratch Pad";

        // Long, dummy text
        final StringBuilder buf = new StringBuilder();
        for (int i=0; i<800; ++i)
            buf.append("This is a long text. ");

        // Images
        final File image1 = createImage();
        final File image2 = createImage();
        System.out.println(image1.getAbsolutePath());

        // Create entries
        final ILogbook logbook =
            logbook_factory.connect(logbook_name, user, password);
        final String long_text = buf.toString();
        try
        {
            // Short text-only entry
            logbook.createEntry("Test Text Entry", "This is a test");

            // Long text-only entry
            logbook.createEntry("Long Test Text Entry", long_text);

            // Short text and image entry
            logbook.createEntry("Test Text/Image Entry", "This is a test with image", image1.getAbsolutePath());

            // Long text and image entry
            logbook.createEntry("Long Text/Image Entry", long_text, image1.getAbsolutePath());

            // Short text and image entry
            logbook.createEntry("Test Text w/ multiple Images", "This is a test with images", image1.getAbsolutePath(), image2.getAbsolutePath());

            // Captioned images
            logbook.createEntry("Test Text w/ multiple Images and captions", "This is a test with images and captions",
            		new String[] { image1.getAbsolutePath(), image2.getAbsolutePath() },
            		new String[] { "Image 1 Caption", "... and this is the second image" });
        }
        finally
        {
            logbook.close();
        }
    }
}
