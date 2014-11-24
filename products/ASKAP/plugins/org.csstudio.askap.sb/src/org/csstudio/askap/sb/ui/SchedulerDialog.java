/*
 * Copyright (c) 2009 CSIRO Australia Telescope National Facility (ATNF) Commonwealth
 * Scientific and Industrial Research Organisation (CSIRO) PO Box 76, Epping NSW 1710,
 * Australia atnf-enquiries@csiro.au
 *
 * This file is part of the ASKAP software distribution.
 *
 * The ASKAP software distribution is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite
 * 330, Boston, MA 02111-1307 USA
 */

package org.csstudio.askap.sb.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.csstudio.askap.sb.util.SchedulingBlock;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author wu049
 * @created Sep 6, 2010
 * 
 */
public class SchedulerDialog extends Dialog {
	
	private static final Logger logger = Logger.getLogger(SchedulerDialog.class.getName());
	
	private static final String DND_DELIMITER = ":";
	
	TreeSet<SchedulingBlock> fromList = new TreeSet<SchedulingBlock>(new SchedulingBlock.SBNameComparator());
	List<SchedulingBlock> toList = new ArrayList<SchedulingBlock>();
	
	Map<Long, SchedulingBlock> allList = new HashMap<Long, SchedulingBlock>();
	
	Shell shell = null;
	Table fromTable = null;
	Table toTable = null;
	
	

	public static void main(String[] args) {
		final Display display = new Display();
		Shell shell = new Shell(display);

		List<SchedulingBlock> fromList = new ArrayList<SchedulingBlock>();		
		for (int i=0; i<10; i++) {
			SchedulingBlock sb = new SchedulingBlock();
			sb.setId(new Long(i));
			sb.setAliasName("sb_from_" + sb.getId());
			sb.setTemplateName("sbTemplate" + i);
			sb.setMajorVersion(0);
			fromList.add(sb);
		}
		
		
		List<SchedulingBlock> toList = new ArrayList<SchedulingBlock>();
		for (int i=0; i<5; i++) {
			SchedulingBlock sb = new SchedulingBlock();
			sb.setId(new Long(100+i));
			sb.setAliasName("sb_to_" + sb.getId());
			sb.setTemplateName("sbTemplate" + i);
			sb.setMajorVersion(0);
			toList.add(sb);
		}
		
		
		
		SchedulerDialog dialog = new SchedulerDialog(shell, fromList, toList);
		List<SchedulingBlock> list = dialog.open();
		if (list==null) {
			System.out.println("null");
			return;
		}
		for (SchedulingBlock sb : list) {
			System.out.println(sb.getAliasName());
		}

	}
	/**
	 * InputDialog constructor
	 * 
	 * @param parent
	 *            the parent
	 * @param style
	 *            the style
	 */
	public SchedulerDialog(Shell parent, List<SchedulingBlock> fromList, List<SchedulingBlock> toList) {
		super(parent, SWT.APPLICATION_MODAL | SWT.RESIZE );
		this.fromList.addAll(fromList);
		this.toList.addAll(toList);
		
		for (SchedulingBlock sb : fromList) {
			allList.put(sb.getId(), sb);
		}
		
		for (SchedulingBlock sb : toList)
			allList.put(sb.getId(), sb);		
	}
	
	/**
	 * Opens the dialog and returns the input
	 * 
	 * @return String
	 */
	public List<SchedulingBlock> open() {
		// Create the dialog window
		shell = new Shell(getParent(), getStyle());
		shell.setText("Scheduler");
		createContents(shell);
		shell.setSize(1000, 500);
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		// Return the entered value, or null
		return toList;
	}
	
	/**
	 * Creates the dialog's contents
	 * 
	 * @param shell
	 *            the dialog window
	 */
	private void createContents(final Shell page) {
		
		GridLayout gridLayout = new GridLayout(3, false);
		page.setLayout(gridLayout);			
		
		
		(new Label(page, SWT.NONE)).setText("From (submitted scheduling blocks):");
		new Label(page, SWT.NONE);
		(new Label(page, SWT.NONE)).setText("To (scheduled scheduling blocks):");
		
		
		createFromTable(page);
		
		Composite buttonPanel = new Composite(page, SWT.BORDER);
		buttonPanel.setLayout(new FormLayout());
		GridData gridData = new GridData();
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		buttonPanel.setLayoutData(gridData);
		
		Button addButton = new Button(buttonPanel, SWT.PUSH);
		addButton.setText("Add >");
		addButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
					addSB();
			}
			
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 0);
		formData.width = 100;
		addButton.setLayoutData(formData);
		
		
		
		Button removeButton = new Button(buttonPanel, SWT.PUSH);
		removeButton.setText("< Remove");
		removeButton.addSelectionListener(new SelectionListener() {	
			public void widgetSelected(SelectionEvent arg0) {
				removeSB();
			}
			
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		formData = new FormData();
		formData.top = new FormAttachment(addButton, 10);
		formData.width = 100;
		removeButton.setLayoutData(formData);
		
		
		Button doneButton = new Button(buttonPanel, SWT.PUSH);
		doneButton.setText("Done");
		doneButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				page.close();
			}
		});
		
		Button cancelButton = new Button(buttonPanel, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				toList = null;
				page.close();
			}
		});


		formData = new FormData();
		formData.bottom = new FormAttachment(100, -10);
		formData.width = 100;
		cancelButton.setLayoutData(formData);
		
		formData = new FormData();
		formData.bottom = new FormAttachment(cancelButton, -10);
		formData.width = 100;
		doneButton.setLayoutData(formData);
		
		
		createToTable(page);

		page.pack();
	}
	/**
	 * 
	 */
	protected void addSB() {
		TableItem selectedItems[] = fromTable.getSelection();
		if (selectedItems==null || selectedItems.length==0)
			return;
		
		Long sbIds[] = new Long[selectedItems.length];
		for (int i=0; i<selectedItems.length; i++) {
			sbIds[i] = (Long) selectedItems[i].getData();
		}
		
		removeFromList(fromList, sbIds);
		
		int index = toList.size();
		if (toTable.getSelection().length>0)
			index = toTable.getSelectionIndex();
		
		addToList(toList, index, sbIds);
		
		repopulateFromTable();
		repopulateToTable();		
	}
	
	
	protected void removeSB() {
		TableItem selectedItems[] = toTable.getSelection();
		if (selectedItems==null || selectedItems.length==0)
			return;
		
		Long sbIds[] = new Long[selectedItems.length];
		for (int i=0; i<selectedItems.length; i++) {
			sbIds[i] = (Long) selectedItems[i].getData();
		}
		
		removeFromList(toList, sbIds);
		
		addToList(fromList, -1, sbIds);
		
		repopulateFromTable();
		repopulateToTable();
	}
	
	private void createFromTable(Shell page) {
		fromTable = new Table(page, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER );
		fromTable.setLinesVisible (true);
		fromTable.setHeaderVisible (true);
		
		TableColumn column0 = new TableColumn (fromTable, SWT.NONE);
		column0.setText ("SB Alias Name");
		
		TableColumn column1 = new TableColumn (fromTable, SWT.NONE);
		column1.setText ("Template Name");

		TableColumn column2 = new TableColumn (fromTable, SWT.NONE);
		column2.setText ("Major Version");

		repopulateFromTable();
		
		GridData g3 = new GridData();
		g3.horizontalAlignment = GridData.FILL;	
		g3.verticalAlignment = GridData.FILL;	
		g3.grabExcessHorizontalSpace = true;
		g3.grabExcessVerticalSpace = true;
		fromTable.setLayoutData(g3);
	}

	/**
	 * @param page
	 */
	private void createToTable(Shell page) {
		toTable = new Table(page, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER);
		toTable.setLinesVisible (true);
		toTable.setHeaderVisible (true);
		
		TableColumn column0 = new TableColumn (toTable, SWT.NONE);
		column0.setText ("SB Alias Name");
		
		TableColumn column1 = new TableColumn (toTable, SWT.NONE);
		column1.setText ("Template Name");

		TableColumn column2 = new TableColumn (toTable, SWT.NONE);
		column2.setText ("Major Version");
		
		repopulateToTable();
		
		GridData g3 = new GridData();
		g3.horizontalAlignment = GridData.FILL;	
		g3.verticalAlignment = GridData.FILL;	
		g3.grabExcessHorizontalSpace = true;
		g3.grabExcessVerticalSpace = true;
		toTable.setLayoutData(g3);
		
		// drag and drop
		DragSource dragSource = new DragSource(toTable, DND.DROP_MOVE);
		dragSource.setTransfer(new Transfer[]{TextTransfer.getInstance()});
		dragSource.addDragListener(new DragSourceListener() {
			String dragData[] = null;
			
			public void dragStart(DragSourceEvent event) {
				// remember the selected sb names
				TableItem dragItems[] = toTable.getSelection();
				if (dragItems!=null && dragItems.length>0) {
					dragData = new String[dragItems.length];
					for (int i=0; i<dragItems.length; i++) {
						dragData[i] = dragItems[i].getData().toString();
					}
				} else 
					dragData = null;
			}
			
			public void dragSetData(DragSourceEvent event) {
				if (dragData!=null && dragData.length>0) {
					String data = "";
					for (String s : dragData)
						data += s + DND_DELIMITER;
					
					event.data = data;
				}
			}
			
			public void dragFinished(DragSourceEvent event) {
				// don't need to do anything. Draged elements are deleted by
				// DropTarget. Since Source and Target are the same Control
			}
		});
		
		DropTarget dropTarget = new DropTarget(toTable, DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[]{TextTransfer.getInstance()});
		dropTarget.addDropListener(new DropTargetListener() {
			
			public void dropAccept(DropTargetEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			public void drop(DropTargetEvent event) {
				String dropData = (String) event.data;
				if (dropData!=null && dropData.length()>0) {
					// find out the items
					String str[] = dropData.split(DND_DELIMITER);
					Long sbIds[] = new Long[str.length];
					for (int i=0; i<str.length; i++) {
						sbIds[i] = Long.parseLong(str[i]);
					}
					
					removeFromList(toList, sbIds);
					
					// find out which item in the list to drop the items
					Point p = event.display.map(null, toTable, event.x, event.y);
					int index = 0;
					TableItem dropItem = toTable.getItem(p);
					if (dropItem!=null) {
						Long sbId = (Long) dropItem.getData();
						// add the items to the new index
						index = toList.indexOf(allList.get(sbId));
					} else {
						index = toList.size();
					}
					
					addToList(toList, index, sbIds);
				}
				
				repopulateToTable();
			}
			
			public void dragOver(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_SCROLL | DND.FEEDBACK_SELECT;
			}
			
			public void dragOperationChanged(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_SCROLL | DND.FEEDBACK_SELECT;
			}
			
			public void dragLeave(DropTargetEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			public void dragEnter(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_SCROLL | DND.FEEDBACK_SELECT;
				
			}
		});
	}
	
	void repopulateToTable() {
		toTable.removeAll();
		
		for (SchedulingBlock sb : toList) {
			TableItem item = new TableItem(toTable, 0);
			item.setText(getRowData(sb));
			item.setData(sb.getId());
		}
		
		setTableSize(toTable);
//		toTable.redraw();
	}
	
	void repopulateFromTable() {
		fromTable.removeAll();
		
		for (Iterator<SchedulingBlock> iter = fromList.iterator(); iter.hasNext();) {
			SchedulingBlock sb = iter.next();
			TableItem item = new TableItem(fromTable, 0);
			item.setText(getRowData(sb));
			item.setData(sb.getId());			
		}
		setTableSize(fromTable);
//		fromTable.redraw();

	}
	
	/**
	 * @param sb
	 * @return
	 */
	protected String[] getRowData(SchedulingBlock sb) {
		String rowData[] = new String[3];
		rowData[0] = sb.getAliasName();
		rowData[1] = sb.getTemplateName();
		rowData[2] = "" + sb.getMajorVersion();
		
		return rowData;
	}
	/**
	 * @param toList2
	 * @param index
	 * @param sbIds
	 */
	protected void addToList(Collection<SchedulingBlock> list, int index,
			Long[] sbIds) {
		
		List<SchedulingBlock> addList = new ArrayList<SchedulingBlock>();
		for (Long id : sbIds) {
			if (allList.get(id) != null)
				addList.add(allList.get(id));
		}
		
		if (list instanceof List<?>) {
			((List<SchedulingBlock>)list).addAll(index, addList);
		} else 
			list.addAll(addList);
	}
	/**
	 * @param toList2
	 * @param sbIds
	 */
	protected void removeFromList(Collection<SchedulingBlock> list, Long[] sbIds) {
		List<SchedulingBlock> removeList = new ArrayList<SchedulingBlock>();
		for (Long id : sbIds) {
			if (allList.get(id)!=null)
				removeList.add(allList.get(id));
		}
		
		boolean changed = list.removeAll(removeList);
	}
	
	
	private void setTableSize(Table table) {
		Rectangle area = table.getParent().getClientArea();
		Point size = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		ScrollBar vBar = table.getVerticalBar();
		int width = area.width - table.computeTrim(0,0,0,0).width - vBar.getSize().x;
		if (size.y > area.height + table.getHeaderHeight()) {
			// Subtract the scrollbar width from the total column width
			// if a vertical scrollbar will be required
			Point vBarSize = vBar.getSize();
			width -= vBarSize.x;
		}
		table.getColumn(0).setWidth(width * 20/100);
		table.getColumn(1).setWidth(width * 15/100);
		table.getColumn(2).setWidth(width * 10/100);
	}	

	
}