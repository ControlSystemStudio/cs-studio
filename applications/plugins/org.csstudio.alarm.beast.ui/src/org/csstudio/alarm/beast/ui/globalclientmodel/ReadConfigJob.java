/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globalclientmodel;

import org.csstudio.alarm.beast.Messages;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/** Job that invokes the models configuration reader as a background task.
 *  @author Kay Kasemir
 */
public class ReadConfigJob  extends Job
{
    final private GlobalAlarmModel model;

    ReadConfigJob(final GlobalAlarmModel model)
    {
        super(Messages.ReadConfigJobName);
        this.model = model;
        setPriority(LONG);
    }

    /** {@inheritDoc} */
    @Override
    protected IStatus run(final IProgressMonitor monitor)
    {
        model.readConfiguration(monitor);
        return Status.OK_STATUS;
    }
}
