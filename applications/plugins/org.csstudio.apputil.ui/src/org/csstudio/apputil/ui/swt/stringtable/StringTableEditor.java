package org.csstudio.apputil.ui.swt.stringtable;

import java.util.Arrays;
import java.util.List;

import org.csstudio.apputil.ui.Activator;
import org.csstudio.apputil.ui.swt.AutoSizeColumn;
import org.csstudio.apputil.ui.swt.AutoSizeControlListener;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;

/** Editor for table (list) of strings,
 *  allows up/down ordering, add and delete
 *  @author Kay Kasemir, Xihui Chen
 */
public class StringTableEditor extends Composite
{
	private static final String DELETE = "delete";
	private static final String DOWN = "down";
	private static final String UP = "up";
	private static final String EDIT = "edit";
	private final TableViewer tableViewer;
	private final static ImageRegistry images = new ImageRegistry();
	private final TableInputWrapper tableInputWrapper = new TableInputWrapper();
	private Button editButton;
	private Button upButton;
	private Button downButton;
	private Button deleteButton;
	
	static {
		// Buttons: edit/up/down/delete		
		images.put(EDIT, Activator.getImageDescriptor("icons/edit.gif"));
		images.put(UP, Activator.getImageDescriptor("icons/up.gif"));
		images.put(DOWN, Activator.getImageDescriptor("icons/down.gif"));
		images.put(DELETE, Activator.getImageDescriptor("icons/delete.gif"));
	}
	
	/** Creates an editable table.  The size of headers array implies the number of columns. 
	 * @param parent The composite which the table resides in
	 * @param headers Contains the header for each column
	 * @param editable Whether it is editable for each column. The size must be same as headers.  
	 * @param items The items to be displayed and manipulated in the table. 
	 * Each element in the list, which is an array of string, represents the data in a row.  
	 * In turn, each element in the string array represents the data in a cell. 
	 * So it is required that every string array in the list must has the same size as headers.  
	 */
	public StringTableEditor(final Composite parent, final String[] headers, 
			final boolean[] editable, final List<String[]> items, 
			final RowEditDialog rowEditDialog,
			final int[] columnsMinWidth) 
	{
		super(parent, 0);
		tableInputWrapper.setItems(items);
		final GridLayout layout = new GridLayout();
		layout.numColumns = headers.length;
		setLayout(layout);
		
		//Edit-able Table
		tableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION |
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		final Table table = tableViewer.getTable();
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4);
		gd.heightHint = 100;
		table.setLayoutData(gd);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		//Create edit-able columns
		for(int i = 0; i < layout.numColumns; i++) {
			final TableViewerColumn col = 
				AutoSizeColumn.make(tableViewer, headers[i], columnsMinWidth[i], 100, false);
			col.setLabelProvider(new StringMultiColumnsLabelProvider(tableViewer, editable[i]));
			//col.setLabelProvider(new StringColumnLabelProvider(tableViwer));
			if(editable[i]) {
				col.setEditingSupport(new StringMultiColumnsEditor(tableViewer, tableInputWrapper,
						layout.numColumns, i));	
			}
		}
		tableViewer.setContentProvider(new StringTableContentProvider());
		tableViewer.setInput(tableInputWrapper);
		new AutoSizeControlListener(table);		
		editButton = createEditButton(table, layout.numColumns, rowEditDialog);
		upButton = createUpButton();
		downButton = createDownButton();
		deleteButton = createDeleteButton();		
		
		// Enable buttons when items are selected
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				setButtonsEnable();
			}
		});
		
		
	}
	
	/** Initialize
	 *  @param parent Parent widget
	 *  @param items Array of items, will be changed in-place
	 */
	public StringTableEditor(final Composite parent, final List<String> items)
	{
		super(parent, 0);
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		setLayout(layout);
		tableInputWrapper.setItems(items);
		// Edit-able List
		tableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION |
								SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		final Table table = tableViewer.getTable();
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3);
		table.setLayoutData(gd);
		table.setLinesVisible(true);
		table.setHeaderVisible(false);
		// Create edit-able column
		final TableViewerColumn col = 
			AutoSizeColumn.make(tableViewer, "Value", 200, 100, false);
		col.setLabelProvider(new StringColumnLabelProvider(tableViewer));
		col.setEditingSupport(new StringColumnEditor(tableViewer, tableInputWrapper));
		tableViewer.setContentProvider(new StringTableContentProvider());
		tableViewer.setInput(tableInputWrapper);
		new AutoSizeControlListener(table);
		
		upButton = createUpButton();
		downButton = createDownButton();
		deleteButton = createDeleteButton();		
		
		// Enable buttons when items are selected
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				setButtonsEnable();
			}
		});
	}

	/** Refresh the editor after the list of items was changed */
	public void refresh()
	{
		tableViewer.refresh();
	}
	
	private Button createEditButton(final Table table, 
			final int numColumns, final RowEditDialog rowEditDialog) 
	{
		final Button edit = new Button(this, SWT.PUSH);
		edit.setImage(images.get(EDIT));
		edit.setToolTipText("Edit the selected item");
		edit.setLayoutData(new GridData());
		edit.setEnabled(false);
		edit.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				final List<String[]> items = (List<String[]>) tableInputWrapper.getItems();
				final Integer index = (Integer)
					((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
				
				if(index == StringTableContentProvider.ADD_ELEMENT) {
					String[] emptyData = new String[numColumns];
					Arrays.fill(emptyData, "");
					rowEditDialog.setRowData(emptyData);					
				}else
					rowEditDialog.setRowData(items.get(index));
				
				if(rowEditDialog.open() != RowEditDialog.OK) {
					return;
				}
				
				if(index == StringTableContentProvider.ADD_ELEMENT)
					//when you click <Add>, it already added a new row.
					items.set(items.size()-1, rowEditDialog.getRowData());
				else
					items.set(index, rowEditDialog.getRowData());

				tableViewer.refresh();
				
			}
		});
		return edit;
	}
	
	@SuppressWarnings("unchecked")
	private Button createUpButton() {
		final Button up = new Button(this, SWT.PUSH);
		up.setImage(images.get(UP));
		up.setToolTipText("Move selected items up");
		up.setLayoutData(new GridData());
		up.setEnabled(false);
		up.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final List items = tableInputWrapper.getItems();
				final Integer index = (Integer)
				((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
				if (index == StringTableContentProvider.ADD_ELEMENT  ||
				    index < 1)
					return;
				//final String[] item = items.get(index);
				items.add(index-1, items.get(index));
				items.remove(index + 1);
				tableViewer.refresh();
				tableViewer.getTable().setSelection(index-1);
			}
		});
		return up;
	}
	
	@SuppressWarnings("unchecked")
	private Button createDownButton() {
		final Button down = new Button(this, SWT.PUSH);
		down.setImage(images.get(DOWN));
		down.setToolTipText("Move selected items down");
		down.setLayoutData(new GridData());
		down.setEnabled(false);
		down.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{	
				final List items = tableInputWrapper.getItems();
				final Integer index = (Integer)
				((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
				if (index == StringTableContentProvider.ADD_ELEMENT  ||
				    index >= items.size()-1)
					return;
				items.add(index+2, items.get(index));
				items.remove(index.intValue());
				tableViewer.refresh();
				tableViewer.getTable().setSelection(index+1);
			}
		});
		return down;
	}
	
	@SuppressWarnings("unchecked")
	private Button createDeleteButton() {
		final Button delete = new Button(this, SWT.PUSH);
		delete.setImage(images.get(DELETE));
		delete.setToolTipText("Delete selected items");
		delete.setLayoutData(new GridData());
		delete.setEnabled(false);
		delete.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{	
				final List items = tableInputWrapper.getItems();
				final Object sel[] =
				 ((IStructuredSelection) tableViewer.getSelection()).toArray();
				int adjust = 0;
				for (Object s : sel)
				{
					final Integer index = (Integer)s;
					if (index == StringTableContentProvider.ADD_ELEMENT)
						continue;
					items.remove(index.intValue() - adjust);
					// What used to be index N is now N-1...
					++adjust;
				}
				tableViewer.refresh();
			}
		});
		return delete;
	}
	
	public void updateInput(List<?> items) {
		tableInputWrapper.setItems(items);
		tableViewer.refresh();
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if(enabled) {
			tableViewer.getTable().setEnabled(enabled);
			setButtonsEnable();
		}
		else
			for(Control control: this.getChildren())
				control.setEnabled(enabled);
	}

	private void setButtonsEnable() {
		final IStructuredSelection sel = (IStructuredSelection)tableViewer.getSelection();
		final int count = sel.size();
		if(editButton != null)
			editButton.setEnabled(count == 1);
		upButton.setEnabled(count == 1);
		downButton.setEnabled(count == 1);
		deleteButton.setEnabled(count > 0);
	}
	
}	
	

