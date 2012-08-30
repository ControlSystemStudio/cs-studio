/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.annunciator.model;

import org.csstudio.alarm.beast.annunciator.Messages;
import org.csstudio.utility.speech.Annunciator;
import org.csstudio.utility.speech.AnnunciatorFactory;
import org.csstudio.utility.speech.Translation;
import org.eclipse.osgi.util.NLS;

/** Queue Manager for the JMS-to-speech tool.
 *  Reads messages from queue, annunciates them, notifies listener.
 *
 *  @author Katia Danilova
 *  @author Delphy Armstrong
 *  @author Kay Kasemir
 *
 *    reviewed by Delphy 1/29/09
 */
@SuppressWarnings("nls")
public class QueueManager implements Runnable
{
	/** Delay (millisecs) to wait after an error */
    final private static int ERROR_DELAY_MS = 5000;

	/** Code used to wake the queue manager; not spoken */
    final static private String MAGIC_EXIT_MESSAGE = "PleaseDoExitNow?!";

    final private JMSAnnunciatorListener listener;
    final private SpeechPriorityQueue queue;
    final private Translation translations[];
    final private int threshold;
    final private Thread thread;

    /** Set to <code>false</code> to stop thread */
    private volatile boolean run = true;

    /** Enable the voice annunciations, or silence them? */
    private volatile boolean enabled = true;

    /** Initialize Queue Manager
     *  @param listener Listener to notify about progress
     *  @param queue SpeechPriorityQueue where the messages and Severity information will arrive
     *  @param translations Translations to use or <code>null</code>
     *  @param threshold max. number of queues messages to allow
     */
    public QueueManager(final JMSAnnunciatorListener listener,
                        final SpeechPriorityQueue queue,
                        final Translation translations[],
                        final int threshold)
    {
        this.listener = listener;
        this.queue = queue;
        this.translations = translations;
        this.threshold = threshold;
        // Handle queue in background thread
        thread = new Thread(this, "Annunciation QueueManager");
        thread.setDaemon(true);
    }

    /** Start the queue manager thread */
    public void start()
    {
        thread.start();
    }

	/** method run is the code to be executed by new thread */
    @Override
    public void run()
    {
        while (run)
        {
        	Annunciator speech = null;
            try
            {
                // Create annunciator
                speech = AnnunciatorFactory.getAnnunciator();
                if (translations != null)
                    speech.setTranslations(translations);

                while (run) // Wait for anybody to add messages to the queue
                {
                    // Retrieves and removes the head of this queue, waiting if
                    // no elements are present on this queue.
                	AnnunciationMessage qc = queue.poll();
                    String message = qc.getMessage();

                    // Exit requested?
                    if (!run)
                        return;
                    // Speak message off queue (may be silenced)
                    if (enabled)
                        speech.say(message);
                    // .. then notify listener
                    listener.performedAnnunciation(qc);

                    // See if the set threshold for messages waiting in the
                    // queue has been exceeded.
                    if (queue.size() > threshold)
                    {
                        // Speak messages marked as 'standout'
                        int flurry = 0;
                        while (queue.size() > 0)
                        {
                            qc = queue.poll();
                            if (qc.isStandoutMessage())
                            {
                                // Speak message off queue, then notify listener
                                message = qc.getMessage();
                                speech.say(message);
                                listener.performedAnnunciation(qc);
                            }
                            else
                                ++flurry;
                        }
                        if (flurry > 0)
                        {
                            final String more = NLS.bind(Messages.MoreMessagesFmt, flurry);
                            speech.say(more);
                            listener.performedAnnunciation(new AnnunciationMessage(Severity.forInfo(), more));
                        }
                    }
                }
            }
            catch (Throwable ex)
            {
                listener.annunciatorError(ex);
                try
                {
                	Thread.sleep(ERROR_DELAY_MS);
                }
                catch (InterruptedException iex)
                {
                	// Ignore
                }
            }
            finally
            {
                if (speech != null)
                    speech.close();
            }
        }
    }

    /** Add magic message to request thread to exit */
    public void stop()
    {
        // Indicate that thread should quit
        run = false;
        queue.add(Severity.forInfo(), MAGIC_EXIT_MESSAGE);
        try
        {
            // Wait for the thread to exit
            thread.join();
        }
        catch (InterruptedException ex)
        {
            // NOPs
        }
    }

    /** @param enabled Enable the voice annunciations? */
    public void setEnabled(final boolean enabled)
    {
        this.enabled = enabled;
    }
}
