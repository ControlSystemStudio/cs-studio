package org.csstudio.alarm.table.logTable;


import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.dataModel.IJMSMessageViewer;
import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.csstudio.alarm.table.dataModel.JMSMessageList;
import org.csstudio.alarm.table.preferences.JmsLogPreferenceConstants;
import org.csstudio.alarm.table.preferences.JmsLogPreferenceInitializer;
import org.csstudio.alarm.table.preferences.JmsLogPreferencePage;
import org.csstudio.alarm.table.preferences.LogArchiveViewerPreferenceConstants;
import org.csstudio.alarm.table.preferences.LogArchiveViewerPreferencePage;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;


public class JMSLogTableViewer extends TableViewer {
//	private MessageReceiver receiver;
//	private TableViewer tableViewer;
	private Table table;
	String[] columnNames;
	int max;
	int rows;
	String colName = null;
	boolean sortAlarms = false;
	String columnSelection = null;
	private String lastSort = "";
	private boolean sort =false;
//	public void refresh() {
//		this.refresh();
//	}
//	
	private JMSMessageList jmsml;// = new JMSMessageList();
	
	public JMSLogTableViewer(Composite parent, IWorkbenchPartSite site,
			String[] colNames, JMSMessageList j) {
		super(parent, SWT.SINGLE | SWT.FULL_SELECTION);
		columnNames = colNames;
		jmsml = j;
//		tableViewer = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION);
		table = this.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		
		table.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,1));
//		columnNames = JmsLogsPlugin.getDefault().getPluginPreferences().getString(JmsLogPreferenceConstants.P_STRING).split(";");
//		max = JmsLogsPlugin.getDefault().getPluginPreferences().getInt(JmsLogPreferenceConstants.MAX);
//		rows = JmsLogsPlugin.getDefault().getPluginPreferences().getInt(JmsLogPreferenceConstants.REMOVE);		
		
//		tableViewer.setSorter(new ViewerSorter(){
//			public int compare(Viewer viewer, Object e1, Object e2) {
//				return 1;
//			}
//		});
		 
		
		for(int i = 0; i < columnNames.length; i++)	{
			TableColumn tableColumn = new TableColumn(table, SWT.CENTER);
			tableColumn.setText(columnNames[i]);
			tableColumn.setWidth(100);
			colName = columnNames[i];
			System.out.println("listerner for col: " + colName);
			
			tableColumn.addSelectionListener(new SelectionAdapter() {
			
				private String cName = colName;
				
				public void widgetSelected(SelectionEvent e) {
					System.out.println(cName);
					if(cName.equals(lastSort)){
						sort=!sort;
						JMSLogTableViewer.this.setSorter(new JMSMessageColumnSorter(cName,sort));
					}
					else {
						JMSLogTableViewer.this.setSorter(new JMSMessageColumnSorter(cName,sort));
					}
					lastSort=cName;
				}
			});
		}
		this.setContentProvider(new JMSMessageContentProvider());		
		
//		tableViewer.setLabelProvider(new PersonTableLabelProvider());
		this.setLabelProvider(new JMSMessageLabelProvider());
		this.setInput(jmsml);
		
		if (sortAlarms == true) {
			this.setSorter(new JMSMessageSorter());
		}		
		makeContextMenu(site);
}
	
	public void setColumnNames(String[] colNames) {
		columnNames = colNames;
	}
	
	 private void makeContextMenu(IWorkbenchPartSite site)
	    {
		    // See Plug-ins book p. 285
	        MenuManager manager = new MenuManager("#PopupMenu");
	        Control contr = this.getControl(); 
	        // Other plug-ins can contribute their actions here

	        manager.addMenuListener(new IMenuListener() {

				public void menuAboutToShow(IMenuManager manager) {
					// TODO Auto-generated method stub
					System.out.println("MenuListener");
				
					JMSLogTableViewer.this.fillContextMenu(manager);
				}
	        });

	        
	        contr.addMouseListener(new MouseAdapter() {
	        	@Override
	        	public void mouseDown(MouseEvent e) {
	        		// TODO Auto-generated method stub
	        		System.out.println("MouseListener");
	        		super.mouseDown(e);
	        		if (e.button == 3) {
	        			JMSMessageLabelProvider jmsmlp = (JMSMessageLabelProvider) JMSLogTableViewer.this.getLabelProvider();
	        			Table t = JMSLogTableViewer.this.getTable();
	        			TableItem ti = t.getItem(new Point(e.x,e.y));
	        			for (int i = 0; i < JMSLogTableViewer.this.columnNames.length; i++) {
	        				Rectangle bounds = ti.getBounds(i);
        	                if (bounds.contains(e.x, e.y)) {
        	                	columnSelection = jmsmlp.getColumnName(i);
        	                    System.out.println("index: " + jmsmlp.getColumnName(i));
        	                    break;
        	                }
	        			}
	        		}
	        	}
	        });
	        
	        
	        
	      //  manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	        Menu menu = manager.createContextMenu(contr);
	        contr.setMenu(menu);
	        site.registerContextMenu(manager, this);
	    }

	private void fillContextMenu(IMenuManager m) {
		List<Object> l = this.getSelectionFromWidget();
		
		ListIterator<Object> it = l.listIterator();
		while(it.hasNext()) {
			JMSMessage jmsm = (JMSMessage) it.next();
			System.out.println("list list" + jmsm.getProperty(columnSelection.toUpperCase()));
		}
		m.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	       
	}

	public void setAlarmSorting(boolean b) {
		sortAlarms = b;
	}

	
//	class MouseAndMenuListener extends MouseAdapter implements IMenuListener {
//
//		public void menuAboutToShow(IMenuManager manager) {
//			// TODO Auto-generated method stub
//			
//		}
//		
//	}
	
	class JMSMessageContentProvider implements IStructuredContentProvider, IJMSMessageViewer {
		
		public void addJMSMessage(JMSMessage jmsm) {
			JMSLogTableViewer.this.add(jmsm);
			if (sortAlarms == true) {
				JMSLogTableViewer.this.setSorter(new JMSMessageSorter());
			}			
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

/* (non-Javadoc)
 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
 */
public Image getColumnImage(Object element, int columnIndex) {
	// TODO Auto-generated method stub
	return null;
}

/* (non-Javadoc)
 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
 */
	public String getColumnText(Object element, int index){
		try {
			JMSMessage jmsm = (JMSMessage) element;
			return jmsm.getProperty(columnNames[index].toUpperCase());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "N/A";
	}
	
	public String getColumnName(int index){
		return columnNames[index].toUpperCase();
	}
	
	
	public Color getBackground(Object element, int columnIndex) {
		JMSMessage jmsm = (JMSMessage) element;
		IPreferenceStore lvpp = new JmsLogPreferencePage().getPreferenceStore();
		if ((jmsm.getProperty("TYPE").equalsIgnoreCase("Alarm"))) {
			if ((jmsm.getProperty("SEVERITY_NUMBER").equals(lvpp.getString(JmsLogPreferenceConstants.KEY0)))) {
				StringTokenizer st = new StringTokenizer(lvpp.getString(JmsLogPreferenceConstants.COLOR0),",");
				return new Color(null, Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken())); 
			}
			else if ((jmsm.getProperty("SEVERITY_NUMBER").equals(lvpp.getString(JmsLogPreferenceConstants.KEY1)))) {
				StringTokenizer st = new StringTokenizer(lvpp.getString(JmsLogPreferenceConstants.COLOR1),",");
				return new Color(null, Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken())); 
			}
			else if ((jmsm.getProperty("SEVERITY_NUMBER").equals(lvpp.getString(JmsLogPreferenceConstants.KEY2)))) {
				StringTokenizer st = new StringTokenizer(lvpp.getString(JmsLogPreferenceConstants.COLOR2),",");
				return new Color(null, Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken())); 
			}
			else if ((jmsm.getProperty("SEVERITY_NUMBER").equals(lvpp.getString(JmsLogPreferenceConstants.KEY3)))) {
				StringTokenizer st = new StringTokenizer(lvpp.getString(JmsLogPreferenceConstants.COLOR3),",");
				return new Color(null, Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken())); 
			}
			else if ((jmsm.getProperty("SEVERITY_NUMBER").equals(lvpp.getString(JmsLogPreferenceConstants.KEY4)))) {
				StringTokenizer st = new StringTokenizer(lvpp.getString(JmsLogPreferenceConstants.COLOR4),",");
				return new Color(null, Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken())); 
			}
			else if ((jmsm.getProperty("SEVERITY_NUMBER").equals(lvpp.getString(JmsLogPreferenceConstants.KEY5)))) {
				StringTokenizer st = new StringTokenizer(lvpp.getString(JmsLogPreferenceConstants.COLOR5),",");
				return new Color(null, Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken())); 
			}
			else if ((jmsm.getProperty("SEVERITY_NUMBER").equals(lvpp.getString(JmsLogPreferenceConstants.KEY6)))) {
				StringTokenizer st = new StringTokenizer(lvpp.getString(JmsLogPreferenceConstants.COLOR6),",");
				return new Color(null, Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken())); 
			}
			else if ((jmsm.getProperty("SEVERITY_NUMBER").equals(lvpp.getString(JmsLogPreferenceConstants.KEY7)))) {
				StringTokenizer st = new StringTokenizer(lvpp.getString(JmsLogPreferenceConstants.COLOR7),",");
				return new Color(null, Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken())); 
			}
			else if ((jmsm.getProperty("SEVERITY_NUMBER").equals(lvpp.getString(JmsLogPreferenceConstants.KEY8)))) {
				StringTokenizer st = new StringTokenizer(lvpp.getString(JmsLogPreferenceConstants.COLOR8),",");
				return new Color(null, Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken())); 
			}
			else if ((jmsm.getProperty("SEVERITY_NUMBER").equals(lvpp.getString(JmsLogPreferenceConstants.KEY9)))) {
				StringTokenizer st = new StringTokenizer(lvpp.getString(JmsLogPreferenceConstants.COLOR9),",");
				return new Color(null, Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken())); 
			}
		}
		return null;
	}
	
	public Color getForeground(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}
}


	


}