/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.sns.product;

import org.csstudio.startup.application.OpenDocumentEventProcessor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/** Tell the workbench how to behave.
 *  @author Kay Kasemir
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

    @Override
    public void initialize(final IWorkbenchConfigurer configurer)
    {
        // Per default, state is not preserved (RCP book 5.1.1)
        configurer.setSaveAndRestore(true);
        // Register adapters needed by Navigator view to display workspace files
        IDE.registerAdapters();

     // register shared images
		declareWorkbenchImages();
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

    /**
	 * Declares all IDE-specific workbench images. This includes both "shared"
	 * images (named in {@link IDE.SharedImages}) and internal images.
	 *
	 * @see IWorkbenchConfigurer#declareImage
	 */
	private void declareWorkbenchImages() {
		declareWorkbenchImage(IDE.SharedImages.IMG_OBJ_PROJECT,
				"icons/project_open.png", true); //$NON-NLS-1$
		declareWorkbenchImage(IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED,
				"icons/project_close.png", true); //$NON-NLS-1$
	}

	/**
	 * Declares a workbench image.
	 *
	 * @param symbolicName
	 *            the symbolic name of the image
	 * @param path
	 *            the path of the image file relative to the product plugin;
	 * @param shared
	 *            <code>true</code> if this is a shared image, and
	 *            <code>false</code> if this is not a shared image
	 * @see IWorkbenchConfigurer#declareImage
	 */
	private void declareWorkbenchImage(String symbolicName,
			String path, boolean shared) {
		ImageDescriptor desc = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, path);
		getWorkbenchConfigurer().declareImage(symbolicName, desc, shared);
	}
}
