/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.visualparts;

import java.util.List;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.script.PVTuple;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;

/**A table editor which can edit string boolean pair.
 * @author Xihui Chen
 *
 */
public class PVTupleTableEditor extends Composite {
	
	private Action addAction;
	private Action removeAction;
	private Action moveUpAction;
	private Action moveDownAction;
	
	private TableViewer pvTupleListTableViewer;
	
	private List<PVTuple> pvTupleList;

	public PVTupleTableEditor(Composite parent, List<PVTuple> pvTupleList, int style) {
		super(parent,style);
		this.pvTupleList = pvTupleList;
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		gridLayout.marginBottom = 0;
		gridLayout.marginTop = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		setLayoutData(gd);
		
		ToolBarManager toolbarManager = new ToolBarManager(SWT.FLAT);
		ToolBar toolBar = toolbarManager.createControl(this);
		GridData grid = new GridData();
		grid.horizontalAlignment = GridData.FILL;
		grid.verticalAlignment = GridData.BEGINNING;
		toolBar.setLayoutData(grid);
		
		createActions();
		toolbarManager.add(addAction);
		toolbarManager.add(removeAction);
		toolbarManager.add(moveUpAction);
		toolbarManager.add(moveDownAction);
		
		toolbarManager.update(true);
		
		pvTupleListTableViewer = createPVTupleListTableViewer(this);
		pvTupleListTableViewer.setInput(pvTupleList);		
		
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		pvTupleListTableViewer.getControl().setEnabled(enabled);
	}
	
	public void updateInput(List<PVTuple> new_items)
	{
		this.pvTupleList = new_items;
		pvTupleListTableViewer.setInput(new_items);
	}
	
	/**
	 * Creates and configures a {@link TableViewer}.
	 * 
	 * @param parent
	 *            The parent for the table
	 * @return The {@link TableViewer}
	 */
	private TableViewer createPVTupleListTableViewer(final Composite parent) {
		final TableViewer viewer = new TableViewer(parent, SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		
		final TableViewerColumn pvColumn = new TableViewerColumn(viewer, SWT.NONE);		
		pvColumn.getColumn().setText("PV Name");
		pvColumn.getColumn().setMoveable(false);
		pvColumn.getColumn().setWidth(220);
		pvColumn.setEditingSupport(new PVColumnEditingSupport(viewer, viewer.getTable()));	
		
		
		final TableViewerColumn TrigColumn = new TableViewerColumn(viewer, SWT.NONE);
		TrigColumn.getColumn().setText("Trigger");
		TrigColumn.getColumn().setMoveable(false);
		TrigColumn.getColumn().pack();
		TrigColumn.setEditingSupport(new TriggerColumnEditingSupport(viewer, viewer.getTable()));	
		
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new PVTupleLabelProvider());
		
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent event) {
				refreshToolbarOnSelection();
			}
		});
		viewer.getTable().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		return viewer;
	}

	
	/**
	 * Refreshes the enabled-state of the actions.
	 */
	private void refreshToolbarOnSelection() {
		
		IStructuredSelection selection = (IStructuredSelection) pvTupleListTableViewer
				.getSelection();
		if (!selection.isEmpty()
				&& selection.getFirstElement() instanceof PVTuple) {
			removeAction.setEnabled(true);
			moveUpAction.setEnabled(true);
			moveDownAction.setEnabled(true);
			
		} else {
			removeAction.setEnabled(false);
			moveUpAction.setEnabled(false);
			moveDownAction.setEnabled(false);
		}
	}
	
	/**
	 * @param tuple the tuple to be selected
	 */
	private void refreshTableViewerFromAction(PVTuple tuple){
		pvTupleListTableViewer.refresh();
		if(tuple == null)
			pvTupleListTableViewer.setSelection(StructuredSelection.EMPTY);
		else {
			pvTupleListTableViewer.setSelection(new StructuredSelection(tuple));
		}
	}
	
	/**
	 * Creates the actions.
	 */
	private void createActions() {	
		addAction = new Action("Add") {
			@Override
			public void run() {	
				PVTuple tuple = new PVTuple("", true);
				pvTupleList.add(tuple);
				refreshTableViewerFromAction(tuple);
			}
		};
		addAction.setToolTipText("Add a PV");
		addAction.setImageDescriptor(CustomMediaFactory.getInstance()
				.getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
						"icons/add.gif"));
		
		
		removeAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) pvTupleListTableViewer
						.getSelection();
				if (!selection.isEmpty()
						&& selection.getFirstElement() instanceof PVTuple) {
					pvTupleList.remove(selection.getFirstElement());
					refreshTableViewerFromAction(null);
					super.setEnabled(false);
				}
			}
		};
		removeAction.setText("Remove");
		removeAction
				.setToolTipText("Remove the selected PV from the list");
		removeAction.setImageDescriptor(CustomMediaFactory.getInstance()
				.getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
						"icons/delete.gif"));
		removeAction.setEnabled(false);

		moveUpAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) pvTupleListTableViewer
						.getSelection();
				if (!selection.isEmpty()
						&& selection.getFirstElement() instanceof PVTuple) {
					PVTuple tuple = (PVTuple) selection
							.getFirstElement();
					int i = pvTupleList.indexOf(tuple);
					if(i>0){
						pvTupleList.remove(tuple);
						pvTupleList.add(i-1, tuple);
						refreshTableViewerFromAction(tuple);
					}	
				}
			}
		};
		moveUpAction.setText("Move Up");
		moveUpAction.setToolTipText("Move up the selected PV");
		moveUpAction.setImageDescriptor(CustomMediaFactory.getInstance()
				.getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
						"icons/search_prev.gif"));
		moveUpAction.setEnabled(false);

		moveDownAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) pvTupleListTableViewer
						.getSelection();
				if (!selection.isEmpty()
						&& selection.getFirstElement() instanceof PVTuple) {
					PVTuple tuple = (PVTuple) selection
							.getFirstElement();
					int i = pvTupleList.indexOf(tuple);
					if(i<pvTupleList.size()-1){
						pvTupleList.remove(tuple);
						pvTupleList.add(i+1, tuple);
						refreshTableViewerFromAction(tuple);
					}			
				}
			}
		};
		moveDownAction.setText("Move Down");
		moveDownAction.setToolTipText("Move down the selected PV");
		moveDownAction.setImageDescriptor(CustomMediaFactory.getInstance()
				.getImageDescriptorFromPlugin(OPIBuilderPlugin.PLUGIN_ID,
						"icons/search_next.gif"));
		moveDownAction.setEnabled(false);		
	
	}
	
	
	private final static class PVTupleLabelProvider extends LabelProvider implements ITableLabelProvider{

		public Image getColumnImage(Object element, int columnIndex) {			
			if (columnIndex == 1 && element instanceof PVTuple) {
				if (((PVTuple)element).trigger) {
					return CustomMediaFactory.getInstance().getImageFromPlugin(OPIBuilderPlugin.PLUGIN_ID, "icons/checked.gif");
				} else {
					return CustomMediaFactory.getInstance().getImageFromPlugin(OPIBuilderPlugin.PLUGIN_ID, "icons/unchecked.gif");
				}
			} else {
				return null;
			}			
		}

		public String getColumnText(Object element, int columnIndex) {
			if(columnIndex == 0 && element instanceof PVTuple)
				return ((PVTuple)element).pvName;
			if(columnIndex == 1 && element instanceof PVTuple)
				return ((PVTuple)element).trigger ? "yes" : "no";
			return null;
		}
		
	}
	
	private final static class PVColumnEditingSupport extends EditingSupport{

		private Table table;
		public PVColumnEditingSupport(ColumnViewer viewer, Table table) {
			super(viewer);
			this.table = table;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return new TextCellEditor(table);
		}

		@Override
		protected Object getValue(Object element) {
			if(element instanceof PVTuple){
				return ((PVTuple)element).pvName;
			}
			return null;
		}

		@Override
		protected void setValue(Object element, Object value) {
			if(element instanceof PVTuple){
				String s = value == null ? "" : value.toString(); //$NON-NLS-1$								
				((PVTuple)element).pvName = s;
				getViewer().refresh();
			}				
		}
		
	}
	
	
	private final static class TriggerColumnEditingSupport extends EditingSupport{

		private Table table;
		public TriggerColumnEditingSupport(ColumnViewer viewer, Table table) {
			super(viewer);
			this.table = table;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return new CheckboxCellEditor(table);
		}

		@Override
		protected Object getValue(Object element) {
			if(element instanceof PVTuple){
				return ((PVTuple)element).trigger;
			}
			return null;
		}

		@Override
		protected void setValue(Object element, Object value) {
			if(element instanceof PVTuple){									
				((PVTuple)element).trigger = (Boolean)value;
				getViewer().refresh();
			}				
		}
		
	}
	

}
