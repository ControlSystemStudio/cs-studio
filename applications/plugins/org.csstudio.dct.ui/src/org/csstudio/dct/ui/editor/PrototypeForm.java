package org.csstudio.dct.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.internal.Parameter;
import org.csstudio.dct.ui.editor.tables.TableCitizenTable;
import org.csstudio.platform.ui.util.LayoutUtil;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;

/**
 * Editing component for {@link IPrototype}.
 * 
 * @author Sven Wende
 * 
 */
public class PrototypeForm extends AbstractPropertyContainerForm<IPrototype>{

	public PrototypeForm(CommandStack commandStack) {
		super(commandStack);
	}

	private TableCitizenTable overviewTable;
	private TableCitizenTable parameterTable;
	
	private ParameterAddAction parameterAddAction;
	private ParameterRemoveAction parameterRemoveAction;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doCreateControl(Composite parent, CommandStack commandStack) {
		super.doCreateControl(parent, commandStack);
		
		Group group;

		// create overview
		group = new Group(parent, SWT.NONE);
		group.setLayoutData(LayoutUtil.createGridData(500, 100));
		group.setLayout(new FillLayout());
		group.setText("Common");
		overviewTable = new TableCitizenTable(group, SWT.None, commandStack);

		// create field table
		group = new Group(parent, SWT.NONE);
		group.setLayoutData(LayoutUtil.createGridDataForVerticalFillingCell(500));
		group.setLayout(new FillLayout());
		group.setText("Parameters");
		parameterTable = new TableCitizenTable(group, SWT.None, commandStack);
		
		// ... popup menu
		TableViewer viewer = parameterTable.getViewer();
		MenuManager popupMenu = new MenuManager();
		parameterAddAction = new ParameterAddAction(this);
		popupMenu.add(parameterAddAction);
		parameterRemoveAction = new ParameterRemoveAction(this);
		popupMenu.add(parameterRemoveAction);
		Menu menu = popupMenu.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doSetInput(IPrototype prototype) {
		super.doSetInput(prototype);
		
		// prepare input for overview table
		List<ITableRow> rowsForOverview = new ArrayList<ITableRow>();
		rowsForOverview.add(new ElementNameTableRowAdapter(prototype, getCommandStack()));
		overviewTable.setInput(rowsForOverview);

		// prepare input for parameter table
		List<ITableRow> rowsForParameters = new ArrayList<ITableRow>();
		for (Parameter p : prototype.getParameters()) {
			rowsForParameters.add(new ParameterTableRowAdapter(p, getCommandStack()));
		}

		parameterTable.setInput(rowsForParameters);
	}
	
	public void refreshParameters() {
		doSetInput(getInput());
	}
	
	/**
	 * Returns the currently selected parameter.
	 * 
	 * @return the currently selected parameter or null
	 */
	public Parameter getSelectedParameter() {
		Parameter result = null;
		
		IStructuredSelection sel =  parameterTable!=null ? (IStructuredSelection) parameterTable.getViewer().getSelection() : null;
		
		if (sel != null &&  sel.getFirstElement() != null) {
			ParameterTableRowAdapter adapter = (ParameterTableRowAdapter) sel.getFirstElement();
			result = adapter.getDelegate();
		}
		
		return result;
	}

}
