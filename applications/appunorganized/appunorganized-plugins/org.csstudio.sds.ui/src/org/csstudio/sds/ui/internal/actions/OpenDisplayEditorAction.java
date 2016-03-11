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
 package org.csstudio.sds.ui.internal.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/**
 * Action that opens the Synoptic Display Editor.
 *
 * @author Alexander Will
 * @version $Revision: 1.11 $
 *
 */
public final class OpenDisplayEditorAction implements
        IWorkbenchWindowActionDelegate {

    /**
     * A workbench window handle.
     */
    private IWorkbenchWindow _window;

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final IWorkbenchWindow window) {
        _window = window;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(final IAction action) {
//        String query = "x."+DisplayEditor.SDS_FILE_EXTENSION; //$NON-NLS-1$
//
//        // we need a dummy editor input...
//        IEditorInput editorInput = new ControlSystemItemEditorInput(
//                CentralItemFactory.createProcessVariable("x")); //$NON-NLS-1$
//
//        IEditorRegistry editorRegistry = PlatformUI.getWorkbench()
//                .getEditorRegistry();
//        IEditorDescriptor descriptor = editorRegistry.getDefaultEditor(query);
//
//        if (descriptor != null && editorInput != null) {
//            IWorkbenchPage page = _window.getActivePage();
//            try {
//                page.openEditor(editorInput, descriptor.getId());
//            } catch (PartInitException e) {
//                CentralLogger.getInstance()
//                        .error(this, "Cannot open editor", e); //$NON-NLS-1$
//            }
//        }
        try {
            PlatformUI.getWorkbench().showPerspective("org.csstudio.sds.ui.internal.workbench.SynopticDisplayStudioPerspective", _window);
        } catch (WorkbenchException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectionChanged(final IAction action,
            final ISelection selection) {
    }
}
