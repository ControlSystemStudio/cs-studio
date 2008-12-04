package org.csstudio.dct.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.metamodel.Factory;
import org.csstudio.dct.metamodel.IDatabaseDefinition;
import org.csstudio.dct.metamodel.IFieldDefinition;
import org.csstudio.dct.metamodel.IMenuDefinition;
import org.csstudio.dct.metamodel.IRecordDefinition;
import org.csstudio.dct.model.IPropertyContainer;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.internal.Parameter;
import org.csstudio.dct.ui.editor.tables.MenuCellEditor;
import org.csstudio.dct.ui.editor.tables.TableCitizenTable;
import org.csstudio.platform.ui.util.LayoutUtil;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * Editing component for {@link IRecord}.
 * 
 * @author Sven Wende
 * 
 */
public class RecordForm extends AbstractPropertyContainerForm<IRecord> {

	private TableCitizenTable overviewTable;
	private TableCitizenTable recordFieldTable;
	
	public RecordForm(CommandStack commandStack) {
		super(commandStack);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doCreateControl(Composite parent, CommandStack commandStack) {
		super.doCreateControl(parent, commandStack);
		
		Group group;

		// .. overview
		group = new Group(parent, SWT.NONE);
		group.setLayoutData(LayoutUtil.createGridData(500, 100));
		group.setLayout(new FillLayout());
		group.setText("Common");
		overviewTable = new TableCitizenTable(group, SWT.None, commandStack);

		// .. field table
		group = new Group(parent, SWT.NONE);
		group.setLayoutData(LayoutUtil.createGridDataForVerticalFillingCell(500));
		group.setLayout(new FillLayout());
		group.setText("Fields");
		recordFieldTable = new TableCitizenTable(group, SWT.None, commandStack){

			@Override
			protected CellEditor getValueCellEditor(Composite parent, Object a) {
				CellEditor result = new TextCellEditor(parent);
				
				String fName = ((RecordFieldTableRowAdapter)a).getKey();
				
				IDatabaseDefinition dbd = Factory.createSampleDatabaseDefinition();
				IRecordDefinition rdef = dbd.getRecordDefinition(RecordForm.this.getInput().getType());
				IFieldDefinition fdef = rdef.getFieldDefinitions(fName);
				
				
				if(fdef!=null) {
					IMenuDefinition mdef = fdef.getMenu();
					
					if(mdef!=null) {
						result =   new MenuCellEditor(parent, mdef);
					}
				}
				
				return  result;
			}
			
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doSetInput(IRecord record) {
		super.doSetInput(record);
		
		// prepare input for overview table
		List<ITableRow> rowsForOverview = new ArrayList<ITableRow>();
		rowsForOverview.add(new RecordNameTableRowAdapter(record, getCommandStack()));
		overviewTable.setInput(rowsForOverview);

		// prepare input for field table
		List<ITableRow> rowsForFields = new ArrayList<ITableRow>();

		for (String key : record.getFinalFields().keySet()) {
			rowsForFields.add(new RecordFieldTableRowAdapter(record, key, getCommandStack()));
		}

		recordFieldTable.setInput(rowsForFields);
	}

}
