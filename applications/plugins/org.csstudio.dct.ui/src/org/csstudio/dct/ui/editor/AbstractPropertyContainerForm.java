package org.csstudio.dct.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IPropertyContainer;
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
 * Abstract base class for forms that edit model elements which contain
 * properties and therefore implement {@link IPropertyContainer}.
 * 
 * @author Sven Wende
 * 
 * @param <E>
 */
public abstract class AbstractPropertyContainerForm<E extends IPropertyContainer> extends AbstractForm<E> {

	private TableCitizenTable propertyTable;

	public AbstractPropertyContainerForm(CommandStack commandStack) {
		super(commandStack);
	}

	@Override
	protected void doCreateControl(Composite parent, CommandStack commandStack) {
		// .. input table for properties
		Group group = new Group(parent, SWT.NONE);
		group.setLayoutData(LayoutUtil.createGridDataForVerticalFillingCell(500));
		group.setLayout(new FillLayout());
		group.setText("Properties");
		propertyTable = new TableCitizenTable(group, SWT.None, commandStack);

		// ... popup menu for properties
		TableViewer viewer = propertyTable.getViewer();
		MenuManager popupMenu = new MenuManager();
		PropertyAddAction propertyAddAction = new PropertyAddAction(this);
		popupMenu.add(propertyAddAction);
		PropertyRemoveAction propertyRemoveAction = new PropertyRemoveAction(this);
		popupMenu.add(propertyRemoveAction);
		Menu menu = popupMenu.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
	}
	
	@Override
	protected void doSetInput(E input) {
		// .. prepare input for property table
		if (input instanceof IPropertyContainer) {
			IPropertyContainer container = (IPropertyContainer) input;
			List<ITableRow> rowsForProperties = new ArrayList<ITableRow>();

			for (String key : container.getFinalProperties().keySet()) {
				rowsForProperties.add(new PropertyTableRowAdapter(container, key, getCommandStack()));
			}
			propertyTable.setInput(rowsForProperties);
		}
	};
	
	/**
	 * Returns the currently selected property.
	 * 
	 * @return the selected property or null
	 */
	public String getSelectedProperty() {
		String result = null;

		IStructuredSelection sel = propertyTable != null ? (IStructuredSelection) propertyTable.getViewer().getSelection() : null;

		if (sel != null && sel.getFirstElement() != null) {
			PropertyTableRowAdapter adapter = (PropertyTableRowAdapter) sel.getFirstElement();
			result = adapter.getKey();
		}

		return result;
	}
}
