package org.csstudio.config.ioconfig.editorparts;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.config.view.helper.DocumentationManageView;
import org.csstudio.config.ioconfig.model.NodeDBO;
import org.csstudio.config.ioconfig.model.AbstractNodeSharedImpl;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

/**
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @since 03.06.2009
 *
 * @param <T> the Editor to perform the cancel action
 *
 */
final class CancelSelectionListener<T extends AbstractNodeSharedImpl<?,?>> implements SelectionListener {

    private final AbstractNodeEditor<?> _abstractNodeEditor;

    public CancelSelectionListener(@Nonnull final AbstractNodeEditor<?> abstractNodeEditor) {
        _abstractNodeEditor = abstractNodeEditor;
        // Default Constructor
    }

    @Override
    public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
        doCancle();
    }

    @Override
    public void widgetSelected(@Nonnull final SelectionEvent e) {
        doCancle();
    }

    /**
     *
     */
    @SuppressWarnings("unchecked")
    private void doCancle() {
        // if (getNode().isPersistent()) {
        if (!_abstractNodeEditor.isNew()) {
            _abstractNodeEditor.cancel();
            final DocumentationManageView documentationManageView = _abstractNodeEditor.getDocumentationManageView();
            if (documentationManageView != null) {
                documentationManageView.cancel();
            }
            _abstractNodeEditor.setSaveButtonSaved();
        } else {
            final T node = (T) _abstractNodeEditor.getNode();
            final boolean openQuestion = MessageDialog
            .openQuestion(_abstractNodeEditor.getShell(), "Cancel", "You dispose this "
                          + node.getClass().getSimpleName() + "?");
            if (openQuestion) {
                _abstractNodeEditor.setSaveButtonSaved();
                // hrickens (01.10.2010): Beim Cancel einer neuen Facility
                // macht nur Perfrom close Sinn.
                @SuppressWarnings("rawtypes")
                final NodeDBO parent = node.getParent();
                if (parent != null) {
                    parent.removeChild(node);
                }
                _abstractNodeEditor.perfromClose();
            }
        }
    }
}
