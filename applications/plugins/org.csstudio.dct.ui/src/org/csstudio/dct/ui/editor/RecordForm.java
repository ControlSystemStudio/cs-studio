package org.csstudio.dct.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.ui.Activator;
import org.csstudio.dct.ui.editor.tables.BeanPropertyTableRowAdapter;
import org.csstudio.dct.ui.editor.tables.ITableRow;
import org.csstudio.dct.ui.editor.tables.TableCitizenTable;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.platform.ui.util.LayoutUtil;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

/**
 * Editing component for {@link IRecord}.
 * 
 * @author Sven Wende
 * 
 */
public class RecordForm extends AbstractPropertyContainerForm<IRecord> {
	private TableCitizenTable recordFieldTable;
	
	public RecordForm(DctEditor editor) {
		super(editor);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doCreateControl(ExpandBar bar, CommandStack commandStack) {
		super.doCreateControl(bar, commandStack);
		
		// .. field table
		Composite composite = new Composite(bar, SWT.NONE);
		composite.setLayout(LayoutUtil.createGridLayout(1, 5, 8, 8));

		recordFieldTable = new TableCitizenTable(composite, SWT.None, commandStack);
		recordFieldTable.getViewer().getControl().setLayoutData(LayoutUtil.createGridDataForHorizontalFillingCell(300));
		
		// .. filter button
		Composite buttons = new Composite(composite, SWT.None);
		buttons.setLayout(new FillLayout());
		
		final Button addButton = new Button(buttons, SWT.CHECK);
		addButton.setText("Show All (includes empty settings)");
		addButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if(addButton.getSelection()) {
					recordFieldTable.getViewer().setFilters(new ViewerFilter[0]);
				} else {
					ViewerFilter filter = new ViewerFilter() {
						@Override
						public boolean select(Viewer viewer, Object parentElement, Object element) {
							RecordFieldTableRowAdapter row = (RecordFieldTableRowAdapter) element;
							return row.getDelegate().getField(row.getFieldKey())!=null;
						}
					};
					
					recordFieldTable.getViewer().setFilters(new ViewerFilter[]{filter});
				}
				
				
			}
		});
		
		addButton.setSelection(true);
		
		// .. the expand item
		ExpandItem expandItem = new ExpandItem (bar, SWT.NONE);
		expandItem.setText("Fields");
		expandItem.setHeight(370);
		expandItem.setControl(composite);
		expandItem.setExpanded(true);
		expandItem.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID, "icons/tab_fields.png"));
	}		

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doSetInput(IRecord record) {
		super.doSetInput(record);
		
		// prepare input for field table
		List<ITableRow> rowsForFields = new ArrayList<ITableRow>();

		for (String key : record.getFinalFields().keySet()) {
			rowsForFields.add(new RecordFieldTableRowAdapter(record, key, getCommandStack()));
		}

		recordFieldTable.setInput(rowsForFields);
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	protected String doGetFormLabel() {
		return "Record";
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	protected void doAddCommonRows(List<ITableRow> rows, IRecord record) {
		rows.add(new BeanPropertyTableRowAdapter("Type", record, getCommandStack(), "type", true));
		rows.add(new RecordEpicsNameTableRowAdapter(record, getCommandStack()));
	}
	
	/**
	 * 
	 *{@inheritDoc}
	 */
	@Override
	protected String doGetLinkText(IRecord record) {
		String text = "";

		if(record.isInherited()) {
			text+="jump to <a href=\""+record.getParentRecord().getId()+"\">parent record</a>";
		} else {
			text+="Record has no parent.";
		}
		
		return text;
	}
	
	/**
	 * Returns the currently selected property.
	 * 
	 * @return the selected property or null
	 */
	public String getSelectedField() {
		String result = null;

		IStructuredSelection sel = recordFieldTable != null ? (IStructuredSelection) recordFieldTable.getViewer().getSelection() : null;

		if (sel != null && sel.getFirstElement() != null) {
			RecordFieldTableRowAdapter adapter = (RecordFieldTableRowAdapter) sel.getFirstElement();
			result = adapter.getKey();
		}

		return result;
	}
}
