/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.ui;

import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.Messages;
import org.eclipse.jface.action.Action;

/** Context menu action that triggers a re-fetch of archived data
 *  @author Kay Kasemir
 */
public class RefreshAction extends Action
{
    final private Controller controller;

    /** Initialize
     *  @param controller Controller that performs the refresh
     */
    @SuppressWarnings("nls")
    public RefreshAction(final Controller controller)
    {
        super(Messages.Refresh,
              Activator.getDefault().getImageDescriptor("icons/refresh_remote.gif"));
        this.controller = controller;
    }

    @Override
    public void run()
    {
        controller.scheduleArchiveRetrieval();
    }
}
