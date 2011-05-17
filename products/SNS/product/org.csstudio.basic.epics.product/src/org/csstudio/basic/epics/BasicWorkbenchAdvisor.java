/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.basic.epics;

import org.csstudio.startup.application.OpenDocumentEventProcessor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/** Workbench advisor that defines the initial perspective etc.
 *  @author Kay Kasemir
 *  @author Xihui Chen - eventLoopIdle
 */
public class BasicWorkbenchAdvisor extends WorkbenchAdvisor
{
    final private OpenDocumentEventProcessor openDocProcessor;

    /** Initialize
     *  @param openDocProcessor
     */
    public BasicWorkbenchAdvisor(final OpenDocumentEventProcessor openDocProcessor)
    {
        this.openDocProcessor = openDocProcessor;
    }

    @Override
    public String getInitialWindowPerspectiveId()
    {
        return BasicPerspective.ID;
    }

    @Override
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
            IWorkbenchWindowConfigurer configurer)
    {
        return new BasicWorkbenchWindowAdvisor(configurer);
    }

    @Override
    public void eventLoopIdle(Display display) {
        if(openDocProcessor != null)
            openDocProcessor.catchUp(display);
        super.eventLoopIdle(display);
    }
}
