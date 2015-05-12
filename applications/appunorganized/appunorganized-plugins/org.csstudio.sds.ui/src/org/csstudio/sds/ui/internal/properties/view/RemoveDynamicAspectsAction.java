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
 package org.csstudio.sds.ui.internal.properties.view;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;

/**
 * Removes dynamic aspects of a property.
 */
final class RemoveDynamicAspectsAction extends
        PropertySheetAction {
    /**
     * Creates the action.
     *
     * @param viewer
     *            the viewer
     * @param name
     *            the name
     */
    public RemoveDynamicAspectsAction(final PropertySheetViewer viewer, final String name) {
        super(viewer, name);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
                IPropertiesHelpContextIds.COPY_PROPERTY_ACTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        // Get the selected property
        IStructuredSelection selection = (IStructuredSelection) getPropertySheet()
                .getSelection();
        if (selection.isEmpty()) {
            return;
        }

        // Assume single selection
        IPropertySheetEntry entry = (IPropertySheetEntry) selection
                .getFirstElement();


        // Open Wizard
        if (entry != null) {
            entry.applyDynamicsDescriptor(null);
        }
    }

    /**
     * Updates enablement based on the current selection.
     *
     * @param sel
     *            the selection
     */
    public void selectionChanged(final IStructuredSelection sel) {
        setEnabled(!sel.isEmpty());
    }
}
