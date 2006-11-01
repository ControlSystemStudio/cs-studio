package org.csstudio.alarm.table.logTable;


import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.dataModel.IJMSMessageViewer;
import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.csstudio.alarm.table.dataModel.JMSMessageList;
import org.csstudio.alarm.table.preferences.LogViewerPreferenceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;



public class CopyOfJMSLogTableViewer {
//	private MessageReceiver receiver;
	private TableViewer tableViewer;
	private Table table;
	String[] columnNames;
	int max;
	int rows;
	String colName = null;
	boolean sortAlarms = false;
	
	public void refresh() {
		tableViewer.refresh();
	}
	
	private JMSMessageList jmsml;// = new JMSMessageList();
	
	public CopyOfJMSLogTableViewer(Composite parent, IWorkbenchPartSite site, JMSMessageList j, boolean sortAlarms) {
		this.sortAlarms = sortAlarms;
		jmsml = j;
		tableViewer = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		table.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,1));
		columnNames = JmsLogsPlugin.getDefault().getPluginPreferences().getString(LogViewerPreferenceConstants.P_STRING).split(";");
		max = JmsLogsPlugin.getDefault().getPluginPreferences().getInt(LogViewerPreferenceConstants.MAX);
		rows = JmsLogsPlugin.getDefault().getPluginPreferences().getInt(LogViewerPreferenceConstants.REMOVE);		
		
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
					tableViewer.setSorter(new JMSMessageColumnSorter(cName));
				}
			});
		}
		tableViewer.setContentProvider(new JMSMessageContentProvider());		
		
//		tableViewer.setLabelProvider(new PersonTableLabelProvider());
		tableViewer.setLabelProvider(new JMSMessageLabelProvider());
		tableViewer.setInput(jmsml);
		
		if (sortAlarms == true) {
			tableViewer.setSorter(new JMSMessageSorter());
		}		
		makeContextMenu(site);
}
	
	 private void makeContextMenu(IWorkbenchPartSite site)
	    {
	        // See Plug-ins book p. 285
	        MenuManager manager = new MenuManager("#PopupMenu");
	        Control contr = tableViewer.getControl(); 
	        // Other plug-ins can contribute their actions here
	        manager.addMenuListener(new IMenuListener() {

				public void menuAboutToShow(IMenuManager manager) {
					// TODO Auto-generated method stub
					CopyOfJMSLogTableViewer.this.fillContextMenu(manager);
				}
	        });
	        
	      //  manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	        Menu menu = manager.createContextMenu(contr);
	        contr.setMenu(menu);
	        site.registerContextMenu(manager, tableViewer);
	    }

	private void fillContextMenu(IMenuManager m) {
		//tableViewer.getSelectionFromWidget();
		m.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	       
	}
	
	class JMSMessageContentProvider implements IStructuredContentProvider, IJMSMessageViewer {
		
		public void addJMSMessage(JMSMessage jmsm) {
			tableViewer.add(jmsm);
			if (sortAlarms == true) {
				tableViewer.setSorter(new JMSMessageSorter());
			}			
		}
		
		public void removeJMSMessage(JMSMessage jmsm) {
			tableViewer.remove(jmsm);
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
	
	public Color getBackground(Object element, int columnIndex) {
		JMSMessage jmsm = (JMSMessage) element;
		if ((jmsm.getProperty("SEVERITY_NUMBER").equals("1")) && (jmsm.getProperty("TYPE").equalsIgnoreCase("Alarm"))) {
			return new Color(null, 200, 200, 200); 
		}
		if ((jmsm.getProperty("SEVERITY_NUMBER").equals("2")) && (jmsm.getProperty("TYPE").equalsIgnoreCase("Alarm"))) {
			return new Color(null, 255, 129, 55); 
		}
		if ((jmsm.getProperty("SEVERITY_NUMBER").equals("3")) && (jmsm.getProperty("TYPE").equalsIgnoreCase("Alarm"))) {
			return new Color(null, 255, 0, 0); 
		}
		
		return null;
	}
	
	public Color getForeground(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}
}
	


}
