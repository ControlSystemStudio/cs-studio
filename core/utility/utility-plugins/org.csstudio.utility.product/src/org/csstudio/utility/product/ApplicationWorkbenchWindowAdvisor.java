/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.product;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.logging.ui.ConsoleViewHandler;
import org.csstudio.ui.menu.app.ApplicationActionBarAdvisor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
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
    private List<IWorkbenchWindowAdvisorExtPoint> hooks = new ArrayList<IWorkbenchWindowAdvisorExtPoint>();

    public ApplicationWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer)
    {
        super(configurer);
        IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(IWorkbenchWindowAdvisorExtPoint.ID);
        for(IConfigurationElement element : config){
            try{
                final Object o = element.createExecutableExtension(IWorkbenchWindowAdvisorExtPoint.NAME); //$NON-NLS-1$
                if(o instanceof IWorkbenchWindowAdvisorExtPoint){
                    hooks.add((IWorkbenchWindowAdvisorExtPoint)o);
                }
            } catch(CoreException e){
                e.printStackTrace();
            }
        }
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

        for(IWorkbenchWindowAdvisorExtPoint hook : hooks){
            try{
                hook.preWindowOpen();
            } catch(Throwable t){
                 t.printStackTrace();
            }
        }

    }

    @Override
    public void postWindowCreate()
    {
        super.postWindowCreate();
        for(IWorkbenchWindowAdvisorExtPoint hook : hooks){
            try{
                hook.postWindowCreate();
            } catch(Throwable t){
                 t.printStackTrace();
            }
        }

        // Add console view to the logger
        ConsoleViewHandler.addToLogger();
    }



    @Override
    public void postWindowRestore() throws WorkbenchException {

        super.postWindowRestore();
        for(IWorkbenchWindowAdvisorExtPoint hook : hooks){
            try{
                hook.postWindowRestore();
            } catch(Throwable t){
                 t.printStackTrace();
            }
        }
    }

    @Override
    public void postWindowOpen() {

        super.postWindowOpen();
        for(IWorkbenchWindowAdvisorExtPoint hook : hooks){
            try{
                hook.postWindowOpen();
            } catch(Throwable t){
                 t.printStackTrace();
            }
        }
    }

    @Override
    public boolean preWindowShellClose() {

        boolean window = super.preWindowShellClose();
        for(IWorkbenchWindowAdvisorExtPoint hook : hooks){
            try{
                window = window && hook.preWindowShellClose();
            } catch(Throwable t){
                 t.printStackTrace();
            }
        }
        return window;
    }

    @Override
    public void postWindowClose() {

        super.postWindowClose();
        for(IWorkbenchWindowAdvisorExtPoint hook : hooks){
            try{
                hook.postWindowClose();
            } catch(Throwable t){
                 t.printStackTrace();
            }
        }
        if (PlatformUI.getWorkbench().getWorkbenchWindowCount() > 0 && !PlatformUI.getWorkbench().isClosing()) {
            //This is required in order to at least partially clean up the mess that RCP leaves behind.
            //The code below will dispose of unused actions and a few other stuff that are not disposed from the
            //memory after the workbench window closes.
            IWorkbenchWindow win = getWindowConfigurer().getWindow();
            IWorkbenchPage[] pages = win.getPages();
            for (IWorkbenchPage p : pages) {
                try {
                    p.close();
                } catch (Exception e) {
                    //ignore
                }
            }
            win.setActivePage(null);
        }
    }

    @Override
    public IStatus saveState(IMemento memento) {

        IStatus iStatus = super.saveState(memento);
        for(IWorkbenchWindowAdvisorExtPoint hook : hooks){
            try{
                IStatus iStatus2 = hook.saveState(memento);
                iStatus = iStatus2.getCode()>iStatus.getCode()?iStatus2:iStatus;
            } catch(Throwable t){
                 t.printStackTrace();
            }
        }
        return iStatus;
    }

    @Override
    public IStatus restoreState(IMemento memento) {

        IStatus iStatus = super.restoreState(memento);
        for(IWorkbenchWindowAdvisorExtPoint hook : hooks){
            try{
                IStatus iStatus2 = hook.restoreState(memento);
                iStatus = iStatus2.getCode()>iStatus.getCode()?iStatus2:iStatus;
            } catch(Throwable t){
                 t.printStackTrace();
            }
        }
        return iStatus;
    }

    @Override
    public ActionBarAdvisor createActionBarAdvisor(final IActionBarConfigurer configurer)
    {
        return new ApplicationActionBarAdvisor(configurer);
    }


}
