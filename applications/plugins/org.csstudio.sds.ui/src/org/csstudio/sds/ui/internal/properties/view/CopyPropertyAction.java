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
