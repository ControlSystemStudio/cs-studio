/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 *
 * $Id$
 */
package org.csstudio.alarm.treeview.views;


import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to offer convenience methods to control views.
 *
 * @author bknerr
 * @author $Author: bknerr $
 * @version $Revision: 1.7 $
 * @since 26.08.2010
 */
public final class WorkbenchWindowHelper {

    private static final Logger LOG = LoggerFactory.getLogger(WorkbenchWindowHelper.class);

    /**
     * Constructor.
     */
    private WorkbenchWindowHelper() {
        // Don't instantiate
    }

    /**
     * Gets the active page from the workbench window and shows the view with the given id.
     * @param viewId the id of the view to be shown
     * @return the view part to be shown or <code>null</code> if activePage was
     */
    @CheckForNull
    public static IViewPart showView(@Nonnull final String viewId) {

        final IWorkbenchPage activePage = getActivePage();
        if (activePage != null) {
            try {
                return activePage.showView(viewId);
            } catch (final PartInitException e) {
                LOG.error("View with ID " + viewId + " could not be initialized by the active page.");
                return null;
            }
        }
        return null;
    }

    /**
     * Gets the active page from the workbench window and hides the view with given id.
     * @param viewId the id of the view to be shown
     * @return <code>null</code> if activePage was
     */
    public static boolean hideView(@Nonnull final String viewId) {

        final IWorkbenchPage activePage = getActivePage();
        if (activePage != null) {
            final IViewPart view = activePage.findView(viewId);
            if (view != null) {
                activePage.hideView(view);
                return true;
            }
        }
        return false;
    }


    @CheckForNull
    private static IWorkbenchPage getActivePage() {
        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        final IWorkbenchPage activePage = window.getActivePage();
        return activePage;
    }
}
