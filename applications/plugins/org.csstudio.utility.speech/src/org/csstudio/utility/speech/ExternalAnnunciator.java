/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.speech;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/** Annunciator that uses an external command
 *  @author Kay Kasemir
 */
class ExternalAnnunciator extends BaseAnnunciator
{
    /** External command used to perform the annunciation.
     *  Must take the text as an argument.
     */
    private static final String SAY = "say"; //$NON-NLS-1$

    /** Timeout for the command to finish */
    private static final long TIMEOUT_MILLI = 60*1000;

    /** Helper for dumping any output of the command to stdout */
    static class StreamDumper extends Thread
    {
        final private InputStream stream;

        StreamDumper(final InputStream stream)
        {
            super("ExternalAnnunciatorStreamDumper"); //$NON-NLS-1$
            this.stream = stream;
        }

        @Override
        public void run()
        {
            try
            {
                final InputStreamReader isr = new InputStreamReader(stream);
                // Buffer the reads, but use smaller buffer because
                // we hope to read very little from the external command
                final BufferedReader br = new BufferedReader(isr, 512);
                String line;
                while ((line = br.readLine()) != null)
                {
                    System.out.println(line);
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }

    }

    /** {@inheritDoc} */
    @Override
    public void say(final String something) throws Exception
    {
        // Translate text
        final String text = applyTranslations(something);

        // Start external command
        final String command[] = new String[] { SAY, text };
        final Process process = new ProcessBuilder(command).start();
        // Read its output to prevent potential block
        final StreamDumper stderr = new StreamDumper(process.getErrorStream());
        final StreamDumper stdout = new StreamDumper(process.getInputStream());
        stderr.start();
        stdout.start();
        // Wait for all to finish
        stderr.join(TIMEOUT_MILLI);
        stdout.join(TIMEOUT_MILLI);
        // Takes too long?
        if (stderr.isAlive()  ||  stdout.isAlive())
        {
            process.destroy();
            throw new Exception("External annunciation timed out"); //$NON-NLS-1$
        }
        // Assume that it finished and this should return immediately
        process.waitFor();
    }

    /** {@inheritDoc} */
    @Override
    public void close()
    {
        // NOP
    }
}

