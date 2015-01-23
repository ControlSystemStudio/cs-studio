/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;

/** Listener to the Eclipse Log that forwards messages
 *  to java.util.logging
 *  @author Kay Kasemir
 */
public class PluginLogListener implements ILogListener
{
    /** {@inheritDoc} */
    @Override
    public void logging(final IStatus status, final String plugin)
    {
        final Logger logger = Logger.getLogger(plugin);
        final Level level = getLevel(status.getSeverity());
        logger.log(level, status.getMessage(), status.getException());
    }

    /** @param severity Eclipse IStatus severity mask/level
     *  @return Logging {@link Level}
     */
    private Level getLevel(int severity)
    {
        if (severity >= IStatus.ERROR)
            return Level.SEVERE;
        else if (severity >= IStatus.WARNING)
            return Level.WARNING;
        else if (severity >= IStatus.INFO)
            return Level.INFO;
        else
            return Level.FINE;
    }
}
