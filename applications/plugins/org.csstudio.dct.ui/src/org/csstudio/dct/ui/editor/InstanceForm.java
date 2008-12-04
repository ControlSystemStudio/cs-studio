package org.csstudio.dct.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPropertyContainer;
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
public class InstanceForm extends AbstractPropertyContainerForm<IInstance> {


	private TableCitizenTable overviewTable;
	private TableCitizenTable parameterValuesTable;

	public InstanceForm(CommandStack commandStack) {
		super(commandStack);
	}

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
		group.setText("Parameter Values");
		parameterValuesTable = new TableCitizenTable(group, SWT.None, commandStack);
		
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doSetInput(IInstance instance) {
		super.doSetInput(instance);
		
		// prepare input for overview table
		List<ITableRow> rowsForOverview = new ArrayList<ITableRow>();
		// ... name
		rowsForOverview.add(new ElementNameTableRowAdapter(instance, getCommandStack()));
		// ... derived from
		rowsForOverview.add(new DerivedFromTableRowAdapter(instance, getCommandStack()));

		overviewTable.setInput(rowsForOverview);

		// prepare input for parameter table
		List<ITableRow> rowsForParameters = new ArrayList<ITableRow>();
		for (Parameter parameter : instance.getPrototype().getParameters()) {
			rowsForParameters.add(new ParameterValueTableRowAdapter(instance, parameter, getCommandStack()));
		}

		parameterValuesTable.setInput(rowsForParameters);

	}

	class DerivedFromTableRowAdapter extends AbstractReadOnlyTableRowAdapter<IInstance> {

		public DerivedFromTableRowAdapter(IInstance delegate, CommandStack commandStack) {
			super(delegate, commandStack);
		}

		@Override
		protected boolean doCanModifyValue(IInstance instance) {
			return false;
		}

		@Override
		protected String doGetKey(IInstance instance) {
			return "Derived From Prototype";
		}

		@Override
		protected Object doGetValue(IInstance instance) {
			return instance.getPrototype().getName();
		}

	}


}
