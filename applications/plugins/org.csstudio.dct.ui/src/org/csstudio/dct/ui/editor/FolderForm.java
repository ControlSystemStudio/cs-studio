package org.csstudio.dct.ui.editor;

import java.util.List;

import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.ui.editor.tables.BeanPropertyTableRowAdapter;
import org.csstudio.dct.ui.editor.tables.ITableRow;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;

public class FolderForm extends AbstractForm<IFolder> {

	public FolderForm(DctEditor editor) {
		super(editor);
	}

	@Override
	protected void doCreateControl(ExpandBar bar, CommandStack commandStack) {

	}

	@Override
	protected void doSetInput(IFolder input) {

	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	protected String doGetFormLabel() {
		return "Folder";
	}

	@Override
	protected void doAddCommonRows(List<ITableRow> rows, IFolder folder) {
		rows.add(new BeanPropertyTableRowAdapter("Name", folder, getCommandStack(), "name"));
	}

	/**
	 * 
	 *{@inheritDoc}
	 */
	@Override
	protected String doGetLinkText(IFolder folder) {
		String text ="";
		
		if (folder.getParentFolder() != null) {
			text = "jump to <a href=\"" + folder.getParentFolder().getId() + "\">parent folder</a>";
		}
		return text;
	}

}
