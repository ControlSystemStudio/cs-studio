/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
 */
package org.csstudio.sds.ui.internal.runmode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.csstudio.sds.internal.runmode.RunModeBoxInput;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener2;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A box that manages a shell, which uses a GEF graphical viewer to display SDS
 * displays.
 *
 * @author Sven Wende
 * @version $Revision: 1.9 $
 */
public final class ViewRunModeBox extends AbstractRunModeBox implements
        IPartListener2, IPerspectiveListener2 {
    private static final Logger LOG = LoggerFactory.getLogger(ViewRunModeBox.class);

    private DisplayViewPart _viewPart;

    /**
     * Constructor.
     *
     * @param input
     *            the input
     * @param connectionService
     * @param view
     *            optional {@link DisplayViewPart} instance
     */
    public ViewRunModeBox(RunModeBoxInput input, DisplayViewPart view) {
        super(input);
        _viewPart = view;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleWindowPositionChange(int x, int y, int width,
            int height) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bringToTop() {
        IWorkbenchPage currentPage = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage();

        IWorkbenchPage page = _viewPart.getSite().getPage();

        try {
            // find references to all open views
            Map<IPerspectiveDescriptor, IViewReference> viewReferences = WorkbenchManipulationHelper
                    .findAllViewReferences(_viewPart);

            if (!viewReferences.isEmpty()) {
                // is there a view already open in the current perspective
                boolean openInSamePerspective = viewReferences
                        .containsKey(currentPage.getPerspective());

                if (openInSamePerspective) {
                    // we just activate the view
                    currentPage.activate(viewReferences.get(
                            page.getPerspective()).getPart(false));
                } else {
                    // ask the user whether he wants to switch the perspective
                    // or open another view instance in the current view
                    boolean openPerspective = MessageDialog
                            .openQuestion(
                                    Display.getCurrent().getActiveShell(),
                                    "View already open",
                                    "The view is already open in another perspective. Do you want to open the perspective instead?");

                    if (openPerspective) {
                        IPerspectiveDescriptor perspective = viewReferences
                                .keySet().iterator().next();
                        IViewReference viewReference = viewReferences
                                .get(perspective);
                        WorkbenchManipulationHelper.activateViewReference(
                                perspective, viewReference);
                    } else {
                        // open another view instance in the current perspective
                        currentPage.showView(DisplayViewPart.PRIMARY_ID, secId,
                                IWorkbenchPage.VIEW_ACTIVATE);
                    }
                }
            } else {
                // open view instance in the current view

                currentPage.showView(DisplayViewPart.PRIMARY_ID, secId,
                        IWorkbenchPage.VIEW_ACTIVATE);
            }
        } catch (PartInitException e) {
            LOG.error(e.toString());
        }

    }

    boolean isDisposing;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doDispose() {
        if (_viewPart != null) {
             _viewPart.getViewSite().getPage().removePartListener(this);
             _viewPart = null;
        }
    }

    private String secId;

    /**
     * Note: View parts can be injected (see
     * {@link #setViewPart(DisplayViewPart)}). In this case the injected view
     * is used. Otherwise a new view will be created.
     */
    @Override
    protected GraphicalViewer doOpen(int x, int y, boolean openRelative, int width, int height,
            String title) {
        if (_viewPart != null) {
            // the view was already instantiated by the workbench (this usually
            // happens on a perspective restore)
            return _viewPart.getGraphicalViewer();
        } else {
            // create and open the view
            String secondaryId = "" + System.currentTimeMillis();
            secId = secondaryId;
            final IWorkbenchPage page = PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getActivePage();
            try {
                _viewPart = (DisplayViewPart) page.showView(
                        DisplayViewPart.PRIMARY_ID, secondaryId,
                        IWorkbenchPage.VIEW_ACTIVATE);
                _viewPart.setPartName(title);

                _viewPart.getViewSite().getPage().addPartListener(this);

                _viewPart.setPartName(title);

                _viewPart.setTitleToolTip(title);
                PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .addPerspectiveListener(this);

                return _viewPart.getGraphicalViewer();
            } catch (final PartInitException e) {
                _viewPart = null;
            }
        }
        return null;
    }

    public DisplayViewPart getView() {
        return _viewPart;
    }

    @Override
    public void partActivated(IWorkbenchPartReference partRef) {

    }

    @Override
    public void partBroughtToTop(IWorkbenchPartReference partRef) {

    }

    @Override
    public void partClosed(IWorkbenchPartReference partRef) {
        if (partRef.getPart(false) == _viewPart) {
            dispose();
        }
    }

    @Override
    public void partDeactivated(IWorkbenchPartReference partRef) {

    }

    @Override
    public void partHidden(IWorkbenchPartReference partRef) {
    }

    public void partInputChanged(IWorkbenchPartReference partRef) {

    }

    @Override
    public void partOpened(IWorkbenchPartReference partRef) {

    }

    @Override
    public void partVisible(IWorkbenchPartReference partRef) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void perspectiveChanged(IWorkbenchPage page,
            IPerspectiveDescriptor perspective,
            IWorkbenchPartReference partRef, String changeId) {

        // listen for views getting closed in the current perspective
        if (!isDisposing && partRef != null
                && partRef.getPart(false) == _viewPart
                && IWorkbenchPage.CHANGE_VIEW_HIDE.equals(changeId)) {

            // check for other view reference of the same view in other
            // perspectives
            Map<IPerspectiveDescriptor, IViewReference> viewRefs = WorkbenchManipulationHelper
                    .findAllViewReferences(_viewPart);

            // when there are any view references in other perspectives we ask
            // the user for his preferred action (close all view references or
            // just this one)
            if (viewRefs.size() > 1) {

                // display a message dialog that asks the user whether he wants
                // to close all view references of the current view or not
                StringBuffer sb = new StringBuffer();
                Iterator<IPerspectiveDescriptor> it = viewRefs.keySet()
                        .iterator();
                while (it.hasNext()) {
                    sb.append(it.next().getLabel());
                    sb.append(it.hasNext() ? ", " : "");
                }

                boolean closeAllViews = MessageDialog.openQuestion(Display
                        .getCurrent().getActiveShell(), "Q",
                        "The view is opened in different perspectives ("
                                + sb.toString()
                                + ") Should all instances get closed now?");

                // close all view references if the user voted accordingly
                if (closeAllViews) {
                    isDisposing = true;
                    WorkbenchManipulationHelper
                            .closeAllViewReferences(_viewPart);

                    PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .removePerspectiveListener(this);

                    isDisposing = false;
                }
            } else {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .removePerspectiveListener(this);
            }
        }
    }

    @Override
    public void perspectiveActivated(IWorkbenchPage page,
            IPerspectiveDescriptor perspective) {

    }

    @Override
    public void perspectiveChanged(IWorkbenchPage page,
            IPerspectiveDescriptor perspective, String changeId) {

    }

    protected static class WorkbenchManipulationHelper {
        public static Map<IPerspectiveDescriptor, IViewReference> findAllViewReferences(
                final IWorkbenchPart viewPart) {
            Map<IPerspectiveDescriptor, IViewReference> result = new HashMap<IPerspectiveDescriptor, IViewReference>();

            IPerspectiveDescriptor currentPerspective = PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .getPerspective();

            final IWorkbenchWindow[] windows = PlatformUI.getWorkbench()
                    .getWorkbenchWindows();

            // for all workbench windows
            for (int w = 0; w < windows.length; w++) {
                final IWorkbenchPage[] pages = windows[w].getPages();

                // for all workbench pages
                // of a given workbench window
                for (int p = 0; p < pages.length; p++) {
                    final IWorkbenchPage page = pages[p];

                    for (IPerspectiveDescriptor pd : page.getOpenPerspectives()) {
                        page.setPerspective(pd);

                        final IViewReference[] viewRefs = page
                                .getViewReferences();

                        // for all view references
                        // of a given workbench page
                        // of a given workbench window
                        for (int v = 0; v < viewRefs.length; v++) {
                            final IViewReference viewRef = viewRefs[v];

                            if (viewRef.getPart(false) == viewPart) {
                                result.put(pd, viewRef);
                            }
                        }
                    }
                }
            }

            PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage().setPerspective(currentPerspective);

            return result;
        }

        public static void closeAllViewReferences(IWorkbenchPart viewPart) {
            Map<IPerspectiveDescriptor, IViewReference> viewRefs = findAllViewReferences(viewPart);

            for (IPerspectiveDescriptor pd : viewRefs.keySet()) {
                IViewReference viewRef = viewRefs.get(pd);
                IPerspectiveDescriptor currentPerspective = viewRef.getPage()
                        .getPerspective();
                viewRef.getPage().setPerspective(pd);
                viewRef.getPage().hideView(viewRef);
                viewRef.getPage().setPerspective(currentPerspective);
            }
        }

        public static void activateViewReference(
                IPerspectiveDescriptor perspective, IViewReference viewReference) {
            IWorkbenchPage p = viewReference.getPage();
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().setActivePage(
                    p);
            p.setPerspective(perspective);
            p.activate(viewReference.getPart(false));
        }
    }

    @Override
    public Point getCurrentLocation() {
        return _viewPart.getSite().getShell().getLocation();
    }

}
