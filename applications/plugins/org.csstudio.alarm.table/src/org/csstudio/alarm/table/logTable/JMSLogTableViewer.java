/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.alarm.table.logTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.SendAcknowledge;
import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.csstudio.alarm.table.dataModel.JMSMessageList;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDragSource;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;

public class JMSLogTableViewer extends TableViewer {

	private Table table;

	String[] columnHeader;

	String colName = null;

	boolean sortAlarms = false;

	String columnSelection = null;

	String[] selection = new String[2];

	String selCol;

	String selText;

	private String lastSort = "";

	private boolean sort = false;

	private JMSMessageList jmsml;

	private int tableType;
	
	int[] columnWidth;

	/**
	 * Setting Column names from preference pages, providers. 
	 * tableType: (1: log table, 2: alarm table, 3: archive table)
	 * The table type is necessary for sorting the messages.
	 * 
	 * @param parent
	 * @param site
	 * @param colNames
	 * @param j
	 * @param tableType (1: log table, 2: alarm table, 3: archive table)
	 */
	public JMSLogTableViewer(Composite parent, IWorkbenchPartSite site,
			String[] colNames, JMSMessageList j, int tableType, int style) {
		super(parent, style);
		this.tableType = tableType;
		columnWidth = new int[colNames.length];
		columnHeader = colNames;
		jmsml = j;
		table = this.getTable();

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.item instanceof TableItem && event.button == 0
						&& event.detail == 32) {
					TableItem ti = (TableItem) event.item;
					if (ti.getChecked()) {
						if (ti.getData() instanceof JMSMessage) {
							List<JMSMessage> msgList = new ArrayList<JMSMessage>();
							msgList.add((JMSMessage) event.item.getData());
							SendAcknowledge sendAck = SendAcknowledge.newFromJMSMessage(msgList);
							sendAck.schedule();
						} else {
							return;
						}
					} else {
						ti.setChecked(true);
					}
					//Click on other columns but ack should not check or uncheck the ack box
				} else if (event.item instanceof TableItem && event.button == 0
						&& event.detail == 0) {
					TableItem ti = (TableItem) event.item;
					if (ti.getChecked() == false) {
						ti.setChecked(false);
					}
				}
			}
		});
		String[] columnName = new String[columnHeader.length];
		for (int i = 0; i < columnHeader.length; i++) {
			final TableColumn tableColumn = new TableColumn(table, SWT.CENTER);
			String[] temp = columnHeader[i].split(",");
			tableColumn.addDisposeListener(new DisposeListener(){

				public void widgetDisposed(DisposeEvent e) {
					columnWidth[tableColumn.getParent().indexOf(tableColumn)]=tableColumn.getWidth();
				}
				
			});
			colName = temp[0];
			columnName[i]=colName;
			tableColumn.setText(temp[0]);
			if(temp.length==2){
				tableColumn.setWidth(Integer.parseInt(columnHeader[i].split(",")[1]));
			}else if (i == 0 && tableType == 2) {
				tableColumn.setWidth(25);

			} else
				tableColumn.setWidth(100);
//			colName = columnNames[i];
			tableColumn.addSelectionListener(new SelectionAdapter() {

				private String cName = colName;

				public void widgetSelected(SelectionEvent e) {
					if (cName.equals(lastSort)) {
						sort = !sort;
						JMSLogTableViewer.this
								.setSorter(new JMSMessageColumnSorter(cName,
										sort));
					} else {
						JMSLogTableViewer.this
								.setSorter(new JMSMessageColumnSorter(cName,
										sort));
					}
					lastSort = cName;
				}
			});
		}
		//		this.setContentProvider(new JMSMessageContentProvider());

		this.setContentProvider(new TableContentProvider(this, jmsml));
		TableLabelProvider labelProvider = new TableLabelProvider(columnName);
		this.setLabelProvider(labelProvider);
		//		this.setLabelProvider(new JMSMessageLabelProvider());
		if (tableType == 1) {
			JMSLogTableViewer.this.setSorter(new JMSMessageSorterLog());
		} else if (tableType == 2) {
			JMSLogTableViewer.this.setSorter(new JMSMessageSorterAlarm());
		}

		this.setInput(jmsml);

		makeContextMenu(site);

		new ProcessVariableDragSource(this.getTable(), this);

	}

	public void setColumnNames(String[] colNames) {
		columnHeader = colNames;
	}

	private void makeContextMenu(IWorkbenchPartSite site) {
		MenuManager manager = new MenuManager("#PopupMenu");
		Control contr = this.getControl();
		manager.add(new ShowMessagePropertiesAction(this));
		
		if ((tableType == 1) || (tableType == 2)) {
			manager.add(new DeleteAction(this));
			manager.add(new DeleteAllAction(this));
		}
		manager.add(new Separator());
		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				JMSLogTableViewer.this.fillContextMenu(manager);
			}
		});
		contr.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				super.mouseDown(e);
				if (e.button == 3) {
					TableLabelProvider lablProvider = (TableLabelProvider) JMSLogTableViewer.this
							.getLabelProvider();
					//					JMSMessageLabelProvider lablProvider = (JMSMessageLabelProvider) JMSLogTableViewer.this
					//					.getLabelProvider();

					Table t = JMSLogTableViewer.this.getTable();
					TableItem ti = t.getItem(new Point(e.x, e.y));
					for (int i = 0; i < JMSLogTableViewer.this.columnHeader.length; i++) {
						Rectangle bounds = ti.getBounds(i);
						if (bounds.contains(e.x, e.y)) {
							columnSelection = lablProvider.getColumnName(i);
							break;
						}
					}
				}
			}
		});
		Menu menu = manager.createContextMenu(contr);
		contr.setMenu(menu);
		site.registerContextMenu(manager, this);
	}

	private void fillContextMenu(IMenuManager m) {
		List<Object> l = this.getSelectionFromWidget();

		ListIterator<Object> it = l.listIterator();
		while (it.hasNext()) {
			JMSMessage jmsm = (JMSMessage) it.next();
		}

		m.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

	}

	/** @return Returns the PVs which are currently selected in the table. */
	public JMSMessage[] getSelectedEntries() {
		IStructuredSelection selection = (IStructuredSelection) this
				.getSelection();
		if (selection.isEmpty())
			return null;
		int num = selection.size();
		JMSMessage entries[] = new JMSMessage[num];
		int i = 0;
		for (Iterator iter = selection.iterator(); iter.hasNext();) {
			JMSMessage entry = (JMSMessage) iter.next();
			entries[i++] = entry;
			if (i > num) {
				JmsLogsPlugin.logError("Selection grew beyond " + num); //$NON-NLS-1$
				return null;
			}
		}
		return entries;
	}

	/** @return Returns the TableViewer. */
	public TableViewer getTableViewer() {
		return this;
	}

	/** @return Returns the TableViewer. */
	public JMSMessageList getTableModel() {
		return this.jmsml;
	}

	public void setAlarmSorting(boolean b) {
		sortAlarms = b;
	}

	/**
	 * @return the columnWidth
	 */
	public int[] getColumnWidth() {
		return columnWidth;
	}
}