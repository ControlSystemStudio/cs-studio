package org.csstudio.dct.ui.editor;

import java.util.List;

import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.commands.ChangeDbdFileCommand;
import org.csstudio.dct.ui.editor.tables.AbstractTableRowAdapter;
import org.csstudio.dct.ui.editor.tables.BeanPropertyTableRowAdapter;
import org.csstudio.dct.ui.editor.tables.ITableRow;
import org.csstudio.dct.ui.editor.tables.ResourceCellEditor;
import org.csstudio.platform.util.StringUtil;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;

public class ProjectForm extends AbstractForm<IProject> {

	public ProjectForm(DctEditor editor) {
		super(editor);
	}

	@Override
	protected void doCreateControl(ExpandBar bar, CommandStack commandStack) {

	}

	@Override
	protected void doSetInput(IProject project) {

	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	protected String doGetFormLabel() {
		return "Project";
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	protected void doAddCommonRows(List<ITableRow> rows, IProject project) {
		rows.add(new BeanPropertyTableRowAdapter("IOC", project, getCommandStack(), "ioc"));
		rows.add(new DbdFileTableRowAdapter(project, getCommandStack()));

	}

	private static class DbdFileTableRowAdapter extends AbstractTableRowAdapter<IProject> {
		public DbdFileTableRowAdapter(IProject delegate, CommandStack commandStack) {
			super(delegate, commandStack);
		}

		@Override
		protected String doGetKey(IProject project) {
			return "DBD File Path";
		}

		@Override
		protected RGB doGetForegroundColorForKey(IProject delegate) {
			RGB rgb = super.doGetForegroundColorForKey(delegate);

			if (!StringUtil.hasLength(delegate.getDbdPath())) {
				rgb = new RGB(255, 0, 0);
			}
			return rgb;
		}

		@Override
		protected Object doGetValue(IProject project) {
			return project.getDbdPath();
		}

		@Override
		protected Command doSetValue(IProject project, Object value) {
			return new ChangeDbdFileCommand(project, value.toString());
		}

		@Override
		protected CellEditor doGetValueCellEditor(IProject delegate, Composite parent) {
			return new ResourceCellEditor(parent, new String[] { "dbd" }, "Select DBD-File");
		}

	}

	/**
	 * 
	 *{@inheritDoc}
	 */
	@Override
	protected String doGetLinkText(IProject project) {
		String text = "";
		return text;
	}
}
