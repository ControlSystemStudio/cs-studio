package org.csstudio.alarm.table.logTable;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.jms.MapMessage;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.dataModel.IJMSMessageViewer;
import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.csstudio.alarm.table.dataModel.JMSMessageList;
import org.csstudio.alarm.table.dataModel.TextContainer;
import org.csstudio.alarm.table.dataModel.TextContainerFactory;
import org.csstudio.alarm.table.jms.SendMapMessage;

import org.csstudio.alarm.table.preferences.JmsLogPreferenceConstants;
import org.csstudio.alarm.table.preferences.JmsLogPreferencePage;
import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IControlSystemItem;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDragSource;
import org.csstudio.utility.ldap.engine.Engine;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;

import com.sun.jmx.mbeanserver.MetaData;

import sun.java2d.loops.MaskBlit;

public class JMSLogTableViewer extends TableViewer {

	private Table table;

	String[] columnNames;

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
		columnNames = colNames;
		jmsml = j;
		table = this.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        table.addListener (SWT.Selection, new Listener () {
            public void handleEvent (Event event) {
                JMSMessage message;
                if (event.item instanceof TableItem && event.button==0) {
                    TableItem ti = (TableItem) event.item;
                    if(ti.getChecked()){
                        SendMapMessage sender = new SendMapMessage();
                        String time = TimestampFactory.now().toString();
                        try{
                            sender.startSender();
                            MapMessage mapMessage = sender.getSessionMessageObject();
                            System.out.println("Start Sender");
                            
                            if (ti.getData() instanceof JMSMessage) {
                                message = (JMSMessage) event.item.getData();
                                System.out.println("name: "+message.getName());
                                
                            }else return;
                            HashMap<String, String> hm = message.getHashMap();
                            Iterator<String> it = hm.keySet().iterator();
                            
                            while(it.hasNext()) {
                                String key = it.next();
                                String value = hm.get(key);
                                mapMessage.setString(key, value);
                            }
                            mapMessage.setString("ACK", "TRUE");
                            mapMessage.setString("ACK_TIME", time);
                            Engine.getInstance().addLdapWriteRequest("epicsAlarmAckn", message.getName(), "ack");
                            Engine.getInstance().addLdapWriteRequest("epicsAlarmAcknTimeStamp", message.getName(), time);
    
                            sender.sendMessage();
                        }catch(Exception e){
                        	JmsLogsPlugin.logException("ACK not set", e);
                            e.printStackTrace();
                        }
                        String string = event.detail == SWT.CHECK ? "Checked" : "Selected";
                        System.out.println (event.item + " " + string);
                        System.out.println (event.text + " " + string);
                        System.out.println(event.data);
                    }else{
                        ti.setChecked(true);
                    }
                }
            }
        });
        for (int i = 0; i < columnNames.length; i++) {
			TableColumn tableColumn = new TableColumn(table, SWT.CENTER);
			tableColumn.setText(columnNames[i]);
            if(i==0&&tableType==2){
                tableColumn.setWidth(25);
                
            }
            else
                tableColumn.setWidth(100);
			colName = columnNames[i];
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
		this.setContentProvider(new JMSMessageContentProvider());
		this.setLabelProvider(new JMSMessageLabelProvider());
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
		columnNames = colNames;
	}

	private void makeContextMenu(IWorkbenchPartSite site) {
		MenuManager manager = new MenuManager("#PopupMenu");
		Control contr = this.getControl();
		if (tableType == 1) {
			manager.add(new DeleteAction(this));
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
					JMSMessageLabelProvider jmsmlp = (JMSMessageLabelProvider) JMSLogTableViewer.this
							.getLabelProvider();
					Table t = JMSLogTableViewer.this.getTable();
					TableItem ti = t.getItem(new Point(e.x, e.y));
					for (int i = 0; i < JMSLogTableViewer.this.columnNames.length; i++) {
						Rectangle bounds = ti.getBounds(i);
						if (bounds.contains(e.x, e.y)) {
							columnSelection = jmsmlp.getColumnName(i);
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
			System.out.println("list list"
					+ jmsm.getProperty(columnSelection.toUpperCase()));
		}
		
		m.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

	}

	 /** @return Returns the PVs which are currently selected in the table. */
    public JMSMessage[] getSelectedEntries()
    {
        IStructuredSelection selection =
            (IStructuredSelection) this.getSelection();
        if (selection.isEmpty())
            return null;
        int num = selection.size();
        JMSMessage entries[] = new JMSMessage[num];
        int i = 0;
        for (Iterator iter = selection.iterator(); iter.hasNext();)
        {
            JMSMessage entry = (JMSMessage) iter.next();
            entries[i++] = entry;
            if (i > num)
            {
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

	class JMSMessageContentProvider implements IStructuredContentProvider,
			IJMSMessageViewer {

		public void addJMSMessage(JMSMessage jmsm) {
			JMSLogTableViewer.this.add(jmsm);
//			if (sortAlarms == true) {
//				JMSLogTableViewer.this.setSorter(new JMSMessageSorterAlarm());
//			}
		}

		public void removeJMSMessage(JMSMessage jmsm) {
			JMSLogTableViewer.this.remove(jmsm);
		}

		public void dispose() {
			jmsml.removeChangeListener(this);
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput != null)
				((JMSMessageList) newInput).addChangeListener(this);
			if (oldInput != null)
				((JMSMessageList) oldInput).removeChangeListener(this);
		}

		public Object[] getElements(Object inputElement) {
			return jmsml.getMessages().toArray();
		}
	}

	class JMSMessageLabelProvider extends LabelProvider implements
			ITableLabelProvider, ITableColorProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
		 *      int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
		 *      int)
		 */
		public String getColumnText(Object element, int index) {
			try {
                JMSMessage jmsm = (JMSMessage) element;
                return jmsm.getProperty(columnNames[index].toUpperCase());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "N/A";
		}

		public String getColumnName(int index) {
			return columnNames[index].toUpperCase();
		}


		public Color getBackground(Object element, int columnIndex) {
			JMSMessage jmsm = (JMSMessage) element;
			IPreferenceStore lvpp = new JmsLogPreferencePage()
					.getPreferenceStore();
			//
			// if we connect to the ALARM topic - we get alarms
			// we do not have to check for the type!
			//if ((jmsm.getProperty("TYPE").equalsIgnoreCase("Alarm"))) {
			Color backgroundColor = null;
			if ( true) {
				if ((jmsm.getProperty("SEVERITY").equals(lvpp
						.getString(JmsLogPreferenceConstants.KEY0)))) {
					StringTokenizer st = new StringTokenizer(lvpp
							.getString(JmsLogPreferenceConstants.COLOR0), ",");
					backgroundColor = new Color(null, Integer.parseInt(st.nextToken()),
							Integer.parseInt(st.nextToken()), Integer
									.parseInt(st.nextToken()));
				} else if ((jmsm.getProperty("SEVERITY").equals(lvpp
						.getString(JmsLogPreferenceConstants.KEY1)))) {
					StringTokenizer st = new StringTokenizer(lvpp
							.getString(JmsLogPreferenceConstants.COLOR1), ",");
					backgroundColor = new Color(null, Integer.parseInt(st.nextToken()),
							Integer.parseInt(st.nextToken()), Integer
									.parseInt(st.nextToken()));
				} else if ((jmsm.getProperty("SEVERITY").equals(lvpp
						.getString(JmsLogPreferenceConstants.KEY2)))) {
					StringTokenizer st = new StringTokenizer(lvpp
							.getString(JmsLogPreferenceConstants.COLOR2), ",");
					backgroundColor = new Color(null, Integer.parseInt(st.nextToken()),
							Integer.parseInt(st.nextToken()), Integer
									.parseInt(st.nextToken()));
				} else if ((jmsm.getProperty("SEVERITY").equals(lvpp
						.getString(JmsLogPreferenceConstants.KEY3)))) {
					StringTokenizer st = new StringTokenizer(lvpp
							.getString(JmsLogPreferenceConstants.COLOR3), ",");
					backgroundColor = new Color(null, Integer.parseInt(st.nextToken()),
							Integer.parseInt(st.nextToken()), Integer
									.parseInt(st.nextToken()));
				} else if ((jmsm.getProperty("SEVERITY").equals(lvpp
						.getString(JmsLogPreferenceConstants.KEY4)))) {
					StringTokenizer st = new StringTokenizer(lvpp
							.getString(JmsLogPreferenceConstants.COLOR4), ",");
					backgroundColor = new Color(null, Integer.parseInt(st.nextToken()),
							Integer.parseInt(st.nextToken()), Integer
									.parseInt(st.nextToken()));
				} else if ((jmsm.getProperty("SEVERITY").equals(lvpp
						.getString(JmsLogPreferenceConstants.KEY5)))) {
					StringTokenizer st = new StringTokenizer(lvpp
							.getString(JmsLogPreferenceConstants.COLOR5), ",");
					backgroundColor = new Color(null, Integer.parseInt(st.nextToken()),
							Integer.parseInt(st.nextToken()), Integer
									.parseInt(st.nextToken()));
				} else if ((jmsm.getProperty("SEVERITY").equals(lvpp
						.getString(JmsLogPreferenceConstants.KEY6)))) {
					StringTokenizer st = new StringTokenizer(lvpp
							.getString(JmsLogPreferenceConstants.COLOR6), ",");
					backgroundColor = new Color(null, Integer.parseInt(st.nextToken()),
							Integer.parseInt(st.nextToken()), Integer
									.parseInt(st.nextToken()));
				} else if ((jmsm.getProperty("SEVERITY").equals(lvpp
						.getString(JmsLogPreferenceConstants.KEY7)))) {
					StringTokenizer st = new StringTokenizer(lvpp
							.getString(JmsLogPreferenceConstants.COLOR7), ",");
					backgroundColor = new Color(null, Integer.parseInt(st.nextToken()),
							Integer.parseInt(st.nextToken()), Integer
									.parseInt(st.nextToken()));
				} else if ((jmsm.getProperty("SEVERITY").equals(lvpp
						.getString(JmsLogPreferenceConstants.KEY8)))) {
					StringTokenizer st = new StringTokenizer(lvpp
							.getString(JmsLogPreferenceConstants.COLOR8), ",");
					backgroundColor = new Color(null, Integer.parseInt(st.nextToken()),
							Integer.parseInt(st.nextToken()), Integer
									.parseInt(st.nextToken()));
				} else if ((jmsm.getProperty("SEVERITY").equals(lvpp
						.getString(JmsLogPreferenceConstants.KEY9)))) {
					StringTokenizer st = new StringTokenizer(lvpp
							.getString(JmsLogPreferenceConstants.COLOR9), ",");
					backgroundColor = new Color(null, Integer.parseInt(st.nextToken()),
							Integer.parseInt(st.nextToken()), Integer
									.parseInt(st.nextToken()));
				}
			}                           
			if (jmsm.isBackgroundColorGray()) {
				int red = backgroundColor.getRed();
				int green = backgroundColor.getGreen();
				int blue = backgroundColor.getBlue();
				if (red < 125) {
					red = red + 130;
				} else {
					red = 255;
				}
				if (green < 125) {
					green = green + 130;
				} else {
					green = 255;
				}
				if (blue < 125) {
					blue = blue + 130;
				} else {
					blue = 255;
				}
				backgroundColor = new Color(null, red, green, blue);
			}
			return backgroundColor;
		}

		public Color getForeground(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}
	}

}