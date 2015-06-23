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

import org.csstudio.sds.ui.internal.localization.Messages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.PlatformUI;

/**
 * Copies a property to the clipboard.
 *
 * @author Sven Wende
 */
final class CopyPropertyAction extends PropertySheetAction {
    /**
     * System clipboard.
     */
    private Clipboard _clipboard;

    /**
     * Creates the action.
     *
     * @param viewer the viewer
     * @param name the name
     * @param clipboard the clipboard
     */
    public CopyPropertyAction(final PropertySheetViewer viewer, final String name,
            final Clipboard clipboard) {
        super(viewer, name);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
                IPropertiesHelpContextIds.COPY_PROPERTY_ACTION);
        _clipboard = clipboard;
    }

    /**
     * Performs this action.
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

        // Place text on the clipboard
        StringBuffer buffer = new StringBuffer();
        buffer.append(entry.getDisplayName());
        buffer.append("\t"); //$NON-NLS-1$
        buffer.append(entry.getValueAsString());

        setClipboard(buffer.toString());
    }

    /**
     * Updates enablement based on the current selection.
     *
     * @param sel the selection
     */
    public void selectionChanged(final IStructuredSelection sel) {
        setEnabled(!sel.isEmpty());
    }

    /**
     * Stores the specified text on the clipboard.
     * @param text the text
     */
    private void setClipboard(final String text) {
        try {
            Object[] data = new Object[] { text };
            Transfer[] transferTypes = new Transfer[] { TextTransfer
                    .getInstance() };
            _clipboard.setContents(data, transferTypes);
        } catch (SWTError e) {
            if (e.code != DND.ERROR_CANNOT_SET_CLIPBOARD) {
                throw e;
            }
            if (MessageDialog.openQuestion(getPropertySheet().getControl()
                    .getShell(), Messages.CopyToClipboardProblemDialog_title,
                    Messages.CopyToClipboardProblemDialog_message)) {
                setClipboard(text);
            }
        }
    }
}
