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
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;

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
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
            final IWorkbenchWindowConfigurer configurer)
    {
        return new BasicWorkbenchWindowAdvisor(configurer);
    }

    @Override
    public void initialize(final IWorkbenchConfigurer configurer)
    {
        // Per default, state is not preserved (RCP book 5.1.1)
        configurer.setSaveAndRestore(true);
        // Register adapters needed by Navigator view to display workspace files
        IDE.registerAdapters();
    }

    /** @return ID of initial perspective */
    @Override
    public String getInitialWindowPerspectiveId()
    {
        return BasicPerspective.ID;
    }

    @Override
    public void eventLoopIdle(final Display display)
    {
        if(openDocProcessor != null)
            openDocProcessor.catchUp(display);
        super.eventLoopIdle(display);
    }
}
