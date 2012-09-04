/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.product;

import org.csstudio.startup.application.OpenDocumentEventProcessor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;

/** Tell the workbench how to behave.
 *  @author Kay Kasemir
 *  @author Xihui Chen - IDE-specific workbench images
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor
{
	private OpenDocumentEventProcessor openDocProcessor;

    public ApplicationWorkbenchAdvisor(
			OpenDocumentEventProcessor openDocProcessor) {
    	this.openDocProcessor = openDocProcessor;
    }

	@Override
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
                    final IWorkbenchWindowConfigurer configurer)
    {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

    @SuppressWarnings("nls")
    @Override
    public void initialize(final IWorkbenchConfigurer configurer)
    {
        // Per default, state is not preserved (RCP book 5.1.1)
        configurer.setSaveAndRestore(true);

        // Register adapters needed by Navigator view to display workspace files
        IDE.registerAdapters();

        // Declares all IDE-specific workbench images. This includes both "shared"
    	// images (named in {@link IDE.SharedImages}) and internal images.
        configurer.declareImage(IDE.SharedImages.IMG_OBJ_PROJECT,
				Activator.getImageDescriptor("icons/project_open.png"), true);
        configurer.declareImage(IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED,
				Activator.getImageDescriptor("icons/project_close.png"), true);
    }

    /** @return ID of initial perspective */
    @Override
    public String getInitialWindowPerspectiveId()
    {
        return CSS_Perspective.ID;
    }

    @Override
    public void eventLoopIdle(final Display display)
    {
    	if(openDocProcessor != null)
    		openDocProcessor.catchUp(display);
    	super.eventLoopIdle(display);
    }
    
    @Override
	public boolean preShutdown() {
    	try {
			ResourcesPlugin.getWorkspace().save(true, new NullProgressMonitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return true;
	}

}
