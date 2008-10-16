package org.csstudio.apputil.ui.swt.stringtable;

import java.util.List;

import org.csstudio.apputil.ui.swt.AutoSizeColumn;
import org.csstudio.apputil.ui.swt.AutoSizeControlListener;
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
import org.eclipse.swt.widgets.Table;

/** Editor for table (list) of strings,
 *  allows up/down ordering, add and delete
 *  @author Kay Kasemir
 */
public class StringTableEditor extends Composite
{
	private final TableViewer list;

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
		
		// Edit-able Table
		list = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION |
								SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		final Table table = list.getTable();
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.verticalSpan = 3;
		table.setLayoutData(gd);
		table.setLinesVisible(true);
		table.setHeaderVisible(false);
		
		final TableViewerColumn col = 
			AutoSizeColumn.make(list, "Value", 200, 100, false);
		col.setLabelProvider(new StringColumnLabelProvider(list));
		col.setEditingSupport(new StringColumnEditor(list, items));
		list.setContentProvider(new StringTableContentProvider());
		
		list.setInput(items);
		
		new AutoSizeControlListener(this, table);
		
		// Buttons up/down/delete
		final Button up = new Button(this, SWT.PUSH);
		up.setText("Up");
		up.setToolTipText("Move selected items up");
		up.setLayoutData(new GridData());
		up.setEnabled(false);
		up.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final Integer index = (Integer)
				((IStructuredSelection)list.getSelection()).getFirstElement();
				if (index == StringTableContentProvider.ADD_ELEMENT  ||
				    index < 1)
					return;
				final String item = items.get(index);
				items.add(index-1, item);
				items.remove(index + 1);
				list.refresh();
			}
		});

		final Button down = new Button(this, SWT.PUSH);
		down.setText("Down");
		down.setToolTipText("Move selected items down");
		down.setLayoutData(new GridData());
		down.setEnabled(false);
		down.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final Integer index = (Integer)
				((IStructuredSelection)list.getSelection()).getFirstElement();
				if (index == StringTableContentProvider.ADD_ELEMENT  ||
				    index >= items.size()-1)
					return;
				final String item = items.get(index);
				items.add(index+2, item);
				items.remove(index.intValue());
				list.refresh();
			}
		});

		final Button delete = new Button(this, SWT.PUSH);
		delete.setText("Del");
		delete.setToolTipText("Delete selected items");
		delete.setLayoutData(new GridData());
		delete.setEnabled(false);
		delete.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final Object sel[] =
				 ((IStructuredSelection) list.getSelection()).toArray();
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
				list.refresh();
			}
		});
		
		// Enable buttons when items are selected
		list.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				final IStructuredSelection sel = (IStructuredSelection)list.getSelection();
				final int count = sel.size();
				up.setEnabled(count == 1);
				down.setEnabled(count == 1);
				delete.setEnabled(count > 0);
			}
		});
	}
}
