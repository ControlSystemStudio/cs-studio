/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.product;

import org.csstudio.logging.ui.ConsoleViewHandler;
import org.csstudio.ui.menu.app.ApplicationActionBarAdvisor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.ide.EditorAreaDropAdapter;

/** Configure the workbench window.
 *  @author Kay Kasemir
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor
{
    public ApplicationWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer)
    {
        super(configurer);
    }

    /** Set initial workbench window size and title */
    @Override
    public void preWindowOpen()
    {
        final IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(1024, 768));
        configurer.setShowMenuBar(true);
        configurer.setShowPerspectiveBar(true);
        configurer.setShowCoolBar(true);
        configurer.setShowFastViewBars(true);
        configurer.setShowProgressIndicator(true);
        configurer.setShowStatusLine(true);
        configurer.setTitle(Messages.Window_Title);
        
        // Workaround for text editor DND bug.
        // See http://www.eclipse.org/forums/index.php/m/333816/
        configurer.configureEditorAreaDropListener(
        	new EditorAreaDropAdapter(configurer.getWindow()));
    }

	@Override
    public void postWindowCreate()
    {
        super.postWindowCreate();

        // Add console view to the logger
        ConsoleViewHandler.addToLogger();
    }

    @Override
    public ActionBarAdvisor createActionBarAdvisor(final IActionBarConfigurer configurer)
    {
        return new ApplicationActionBarAdvisor(configurer);
    }
}
