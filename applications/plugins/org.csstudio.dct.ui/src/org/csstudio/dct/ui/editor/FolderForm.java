package org.csstudio.dct.ui.editor;

import java.util.List;

import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.ui.editor.tables.ITableRow;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.widgets.ExpandBar;

/**
 * Editing form for folders.
 *
 * @author Sven Wende
 *
 */
public final class FolderForm extends AbstractForm<IFolder> {

    /**
     * Constructor.
     *
     * @param editor
     *            the editor instance
     */
    public FolderForm(DctEditor editor) {
        super(editor);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doCreateControl(ExpandBar bar, CommandStack commandStack) {

    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doSetInput(IFolder input) {

    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected String doGetFormLabel(IFolder input) {
        return "Folder";
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doAddCommonRows(List<ITableRow> rows, IFolder folder) {
    }

    /**
     *
     *{@inheritDoc}
     */
    @Override
    protected String doGetAdditionalBreadcrumbLinks(IFolder folder) {
        return null;
    }

}
