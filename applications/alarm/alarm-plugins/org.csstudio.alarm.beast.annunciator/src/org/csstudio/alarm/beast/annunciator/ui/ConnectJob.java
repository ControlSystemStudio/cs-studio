/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.annunciator.ui;

import javax.jms.Connection;

import org.csstudio.alarm.beast.annunciator.Preferences;
import org.csstudio.alarm.beast.annunciator.model.JMSAnnunciator;
import org.csstudio.platform.utility.jms.JMSConnectionFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/** Eclipse background job for connecting to the JMS server
 *  and creating an Annunciator.
 *  The AnnunciatorView is called with the new Annunciator,
 *  and the view then starts it.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ConnectJob extends Job
{
    final private AnnunciatorView view;

    /** Initialize
     *  @param view View to inform about connection
     */
    public ConnectJob(final AnnunciatorView view)
    {
        super("Connect Annunciator");
        this.view = view;
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor)
    {
        final String url = Preferences.getURL();
        monitor.beginTask("URL '" + url + "'", IProgressMonitor.UNKNOWN);

        try
        {
            final Connection connection = JMSConnectionFactory.connect(url);
            final JMSAnnunciator annunciator = new JMSAnnunciator(view, connection,
                    Preferences.getTopics(),
                    Preferences.getTranslationsFile(),
                    Preferences.getThreshold());
            view.setAnnunciator(annunciator);
        }
        catch (Throwable ex)
        {   // May have been interrupted by cancel() -> canceling(),
            // resulting in JMSException: Unexpected failure.
            // Not specific enough to detect this case, so
            // handle like any other error:
            // Display, except after cancel the view will be disposed,
            // so it's actually not displayed, which is OK.
            view.annunciatorError(ex);
        }

        monitor.done();
        return Status.OK_STATUS;
    }

    /** When Job is asked to cancel, interrupt the underlying thread */
    @Override
    protected void canceling()
    {
        getThread().interrupt();
    }
}
