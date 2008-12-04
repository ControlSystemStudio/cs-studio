package org.csstudio.dct.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.internal.Project;
import org.csstudio.dct.ui.editor.InstanceForm.DerivedFromTableRowAdapter;
import org.csstudio.dct.ui.editor.tables.TableCitizenTable;
import org.csstudio.platform.ui.util.LayoutUtil;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class ProjectForm extends AbstractForm<Project> {

	private TableCitizenTable overviewTable;

	public ProjectForm(CommandStack commandStack) {
		super(commandStack);
	}

	@Override
	protected void doCreateControl(Composite parent, CommandStack commandStack) {
		// create overview
		Group group = new Group(parent, SWT.NONE);
		group.setLayoutData(LayoutUtil.createGridData(500, 100));
		group.setLayout(new FillLayout());
		group.setText("Common");
		overviewTable = new TableCitizenTable(group, SWT.None, commandStack);
		
	
	}

	@Override
	protected void doSetInput(Project project) {
		// prepare input for overview table
		List<ITableRow> rowsForOverview = new ArrayList<ITableRow>();
		// ... name
		rowsForOverview.add(new ElementNameTableRowAdapter(project, getCommandStack()));

		overviewTable.setInput(rowsForOverview);
	}

}
