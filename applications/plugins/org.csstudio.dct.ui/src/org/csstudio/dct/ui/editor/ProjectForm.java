package org.csstudio.dct.ui.editor;

import java.util.List;

import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.commands.ChangeDbdFileCommand;
import org.csstudio.dct.ui.editor.tables.ITableRow;
import org.csstudio.dct.ui.editor.tables.WorkspaceResourceCellEditor;
import org.csstudio.domain.common.strings.StringUtil;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;

/**
 * Editing form for projects.
 *
 * @author Sven Wende
 *
 */
public final class ProjectForm extends AbstractForm<IProject> {

    /**
     * Constructor.
     *
     * @param editor
     *            the editor instance
     */
    public ProjectForm(DctEditor editor) {
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
    protected void doSetInput(IProject project) {

    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected String doGetFormLabel(IProject input) {
        return "Project";
    }

    /**
     *{@inheritDoc}
     */
    @Override
    protected void doAddCommonRows(List<ITableRow> rows, IProject project) {
        rows.add(new BeanPropertyTableRowAdapter("IOC", project, "ioc", false));
        rows.add(new DbdFileTableRowAdapter(project));

    }

    /**
     * Row adapter for the dbd file setting.
     *
     * @author Sven Wende
     *
     */
    private static class DbdFileTableRowAdapter extends AbstractTableRowAdapter<IProject> {
        public DbdFileTableRowAdapter(IProject delegate) {
            super(delegate);
        }

        /**
         *{@inheritDoc}
         */
        @Override
        protected String doGetKey(IProject project) {
            return "DBD File Path";
        }

        /**
         *{@inheritDoc}
         */
        @Override
        protected RGB doGetForegroundColorForKey(IProject delegate) {
            RGB rgb = super.doGetForegroundColorForKey(delegate);

            if (!StringUtil.hasLength(delegate.getDbdPath())) {
                rgb = new RGB(255, 0, 0);
            }
            return rgb;
        }

        /**
         *{@inheritDoc}
         */
        @Override
        protected String doGetValue(IProject project) {
            return project.getDbdPath();
        }

        /**
         *{@inheritDoc}
         */
        @Override
        protected Command doSetValue(IProject project, Object value) {
            return new ChangeDbdFileCommand(project, value.toString());
        }

        /**
         *{@inheritDoc}
         */
        @Override
        protected CellEditor doGetValueCellEditor(IProject delegate, Composite parent) {
            return new WorkspaceResourceCellEditor(parent, new String[] { "dbd" }, "Select DBD-File");
        }

    }

    /**
     *
     *{@inheritDoc}
     */
    @Override
    protected String doGetAdditionalBreadcrumbLinks(IProject project) {
        return null;
    }
}
