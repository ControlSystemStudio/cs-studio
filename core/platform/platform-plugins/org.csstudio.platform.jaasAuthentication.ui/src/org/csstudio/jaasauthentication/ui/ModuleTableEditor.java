/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.jaasauthentication.ui;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.internal.jassauthentication.preference.JAASPreferenceModel;
import org.csstudio.ui.util.swt.stringtable.StringTableEditor;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ColumnWeightData;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class ModuleTableEditor extends Composite {

	private static final String DELETE = "delete"; //$NON-NLS-1$
	private static final String DOWN = "down"; //$NON-NLS-1$
	private static final String UP = "up"; //$NON-NLS-1$
	private final TableViewer tableViewer;
	private final static ImageRegistry images = new ImageRegistry();

	/**
	 * options to be edited in options table
	 */
	private List<String[]> options = new ArrayList<String[]>();
	private final StringTableEditor optionsTableEditor;
	private Button upButton;
	private Button downButton;
	private Button deleteButton;

	static {
		// Buttons: up/down/delete
		images.put(UP, Activator.getImageDescriptor("icons/up.gif")); //$NON-NLS-1$
		images.put(DOWN, Activator.getImageDescriptor("icons/down.gif")); //$NON-NLS-1$
		images.put(DELETE, Activator.getImageDescriptor("icons/delete.gif")); //$NON-NLS-1$
	}

	public ModuleTableEditor(final Composite parent) {
		super(parent, 0);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		setLayout(new GridLayout(2, false));

		// "Login Modules:"
		Label label = new Label(this, 0);
		label.setText(Messages.JAASPreferencePage_modules);
		label.setLayoutData(new GridData(SWT.FILL, 0, true, false, 2, 1));

     	// Edit-able table in its own parent because of TableColumnLayout
		final Composite table_parent = new Composite(this, 0);
        final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3);
        gd.heightHint = 100;
        table_parent.setLayoutData(gd);

		final TableColumnLayout table_layout = new TableColumnLayout();
		table_parent.setLayout(table_layout);

		tableViewer = new TableViewer(table_parent, SWT.BORDER | SWT.FULL_SELECTION |
				SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		final Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		//Create edit-able columns
		TableViewerColumn view_col = new TableViewerColumn(tableViewer, 0);
		TableColumn col = view_col.getColumn();
		col.setText(Messages.ModuleTableEditor_moduleName);
		col.setResizable(true);
		table_layout.setColumnData(col, new ColumnWeightData(100, 240));
		view_col.setLabelProvider(new ModuleColumnLabelProvider());
		view_col.setEditingSupport(new ModuleColumnEditor(tableViewer, this));

		view_col = new TableViewerColumn(tableViewer, 0);
        col = view_col.getColumn();
        col.setText(Messages.ModuleTableEditor_moduleFlag);
        col.setResizable(true);
        table_layout.setColumnData(col, new ColumnWeightData(20, 60));
        view_col.setLabelProvider(new FlagColumnLabelProvider());
        view_col.setEditingSupport(new FlagColumnEditor(tableViewer));

		tableViewer.setContentProvider(new ModuleTableContentProvider());
		tableViewer.setInput(JAASPreferenceModel.configurationEntryList);

		upButton = createUpButton(JAASPreferenceModel.configurationEntryList);
		downButton = createDownButton(JAASPreferenceModel.configurationEntryList);
		deleteButton = createDeleteButton(JAASPreferenceModel.configurationEntryList);

		//Label for options
		label = new Label(parent, SWT.LEFT);
        label.setText(Messages.ModuleTableEditor_options);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        //get selection
		final Object selection = ((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
		if(selection != null) {
			final Integer index = (Integer)selection;
			if(index != ModuleTableContentProvider.ADD_ELEMENT)
				options = JAASPreferenceModel.configurationEntryList.get(index).getModuleOptionsList();
		}

		//create options table editor
		optionsTableEditor = new StringTableEditor(this,
				new String[]{Messages.ModuleTableEditor_option, Messages.ModuleTableEditor_value},
				new boolean[] {true, true},
				options,
				new EditModuleOptionDialog(this.getShell()),
				new int[] {110, 360});
		optionsTableEditor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		// Enable buttons when items are selected
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
            public void selectionChanged(SelectionChangedEvent event)
			{
				setButtonsEnable();

				updateOptionsTable();
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Button createUpButton(final List items) {
		final Button up = new Button(this, SWT.PUSH);
		up.setImage(images.get(UP));
		up.setToolTipText(Messages.ModuleTableEditor_moveUp);
		up.setLayoutData(new GridData());
		up.setEnabled(false);
		up.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final Integer index = (Integer)
				((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
				if (index == ModuleTableContentProvider.ADD_ELEMENT  ||
				    index < 1)
					return;
				items.add(index-1, items.get(index));
				items.remove(index + 1);
				tableViewer.refresh();
				tableViewer.getTable().setSelection(index-1);
				updateOptionsTable();
			}
		});
		return up;
	}

	@SuppressWarnings("rawtypes")
    private Button createDownButton(final List items) {
		final Button down = new Button(this, SWT.PUSH);
		down.setImage(images.get(DOWN));
		down.setToolTipText(Messages.ModuleTableEditor_moveDown);
		down.setLayoutData(new GridData());
		down.setEnabled(false);
		down.addSelectionListener(new SelectionAdapter()
		{
			@SuppressWarnings("unchecked")
            @Override
			public void widgetSelected(SelectionEvent e)
			{
				final Integer index = (Integer)
				((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
				if (index == ModuleTableContentProvider.ADD_ELEMENT  ||
				    index >= items.size()-1)
					return;
				items.add(index+2, items.get(index));
				items.remove(index.intValue());
				tableViewer.refresh();
				tableViewer.getTable().setSelection(index+1);
				updateOptionsTable();
			}
		});
		return down;
	}

	@SuppressWarnings("rawtypes")
    private Button createDeleteButton(final List items) {
		final Button delete = new Button(this, SWT.PUSH);
		delete.setImage(images.get(DELETE));
		delete.setToolTipText(Messages.ModuleTableEditor_deleteItems);
		delete.setLayoutData(new GridData());
		delete.setEnabled(false);
		delete.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final Object sel[] =
				 ((IStructuredSelection) tableViewer.getSelection()).toArray();
				int adjust = 0;
				for (Object s : sel)
				{
					final Integer index = (Integer)s;
					if (index == ModuleTableContentProvider.ADD_ELEMENT)
						continue;
					items.remove(index.intValue() - adjust);
					// What used to be index N is now N-1...
					++adjust;
				}
				tableViewer.refresh();
				updateOptionsTable();
			}
		});
		return delete;
	}


	public void updateOptionsTable() {
		final IStructuredSelection sel = (IStructuredSelection)tableViewer.getSelection();
		Integer index;
		if(sel != null && !sel.isEmpty())
			index = (Integer)sel.getFirstElement();
		else {
			tableViewer.getTable().setSelection(0);
			setButtonsEnable();
			index = 0;
		}

		if(index == ModuleTableContentProvider.ADD_ELEMENT ||
				index > JAASPreferenceModel.configurationEntryList.size()-1)
			options = new ArrayList<String[]>();
		else
			options = JAASPreferenceModel.configurationEntryList.get(index).getModuleOptionsList();
		optionsTableEditor.updateInput(options);
	}

	/**
	 * Refreshes this viewer completely with information
	 * freshly obtained from this viewer's model.
	 */
	public void refresh() {
		tableViewer.refresh();
		tableViewer.getTable().deselectAll();
		updateOptionsTable();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if(!enabled)
			for(Control control: this.getChildren())
				control.setEnabled(enabled);
		else {
			tableViewer.getTable().getParent().setEnabled(enabled);
            tableViewer.getTable().setEnabled(enabled);
			setButtonsEnable();
		}
		optionsTableEditor.setEnabled(enabled);
	}

	private void setButtonsEnable() {
		final IStructuredSelection sel = (IStructuredSelection)tableViewer.getSelection();
		final int count = sel.size();
		upButton.setEnabled(count == 1);
		downButton.setEnabled(count == 1);
		deleteButton.setEnabled(count > 0);
	}
}
