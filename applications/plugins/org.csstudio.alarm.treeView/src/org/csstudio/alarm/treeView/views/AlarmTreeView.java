package org.csstudio.alarm.treeView.views;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;

import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.views.models.Alarm;
import org.csstudio.alarm.treeView.views.models.AlarmConnection;
import org.csstudio.alarm.treeView.views.models.AlarmTreeObject;
import org.csstudio.alarm.treeView.views.models.ContextTreeObject;
import org.csstudio.alarm.treeView.views.models.LdapConnection;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

/**
 * Tree view of process variables and their alarm state. This view uses LDAP
 * to get a hierarchy of process variables and displays them in a tree view.
 * Process variables which are in an alarm state are visually marked in the
 * view.
 */
public class AlarmTreeView extends ViewPart {

	private final static String ID = "org.csstudio.alarm.treeView.views.LdapTView";
	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action refreshAction;
	private Action disableAlarm;
	private Action doubleClickAction;
	private Action autoMap;
	
	/**
	 * Returns the id of this view.
	 * @return the id of this view.
	 */
	public static String getID(){
		return ID;
	}
	
	/**
	 * Creates an LDAP tree viewer.
	 */
	public AlarmTreeView() {
	}

	/**
	 * Creates the controls for this view. This method is called by the
	 * workbench.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		AlarmTreeContentProvider contentProvider = new AlarmTreeContentProvider();
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new AlarmTreeLabelProvider());
		viewer.setComparator(new ViewerComparator());
		viewer.setInput(getViewSite());
		createActions();
		initializeContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

        AlarmTreePlugin myPluginInstance = AlarmTreePlugin.getDefault();
        myPluginInstance.initalizeConnections();
        viewer.refresh();
	}

	/**
	 * Adds a context menu to the tree view.
	 */
	private void initializeContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		
		// add menu items to the context menu when it is about to show
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		
		// add the context menu to the tree viewer
		Menu contextMenu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(contextMenu);
		
		// register the context menu for extension by other plug-ins
		getSite().registerContextMenu(menuMgr, viewer);
		getSite().setSelectionProvider(viewer);
	}

	/**
	 * Adds tool buttons and menu items to the action bar of this view.
	 */
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * Adds the actions for the action bar's pull down menu.
	 * @param manager the menu manager.
	 */
	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(refreshAction);
	}

	/**
	 * Adds the context menu actions.
	 * @param manager the menu manager.
	 */
	private void fillContextMenu(IMenuManager manager) {
		ISelection selection = viewer.getSelection();
		Object obj = ((IStructuredSelection)selection).getFirstElement();
		if (obj == null) {
			// nothing selected -> empty context menu
		}
		else if (obj instanceof LdapConnection) {
			// empty context menu
		}
		else if (obj instanceof ContextTreeObject) {
			if (((ContextTreeObject)obj).getMaxUnacknowledgedAlarm()>0) {
				manager.add(disableAlarm);
			}
		}
		else if (obj instanceof AlarmTreeObject) {
			manager.add(disableAlarm);
		}
		else if (obj instanceof AlarmConnection) {
			if (!((AlarmConnection)obj).isMapped()) {
				manager.add(autoMap);
			}
		}
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		
		// adds a separator after which contributed actions from other plug-ins
		// will be displayed
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	/**
	 * Adds the tool bar actions.
	 * @param manager the menu manager.
	 */
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(refreshAction);
	}

	/**
	 * Creates the actions offered by this view.
	 */
	private void createActions() {
		autoMap = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object alarmSelection = ((IStructuredSelection)selection).getFirstElement();
				if (alarmSelection instanceof AlarmConnection){
					List lst = AlarmTreePlugin.getDefault().getConnections();
					Iterator iter = lst.iterator();
					boolean found = true;
					while ((iter.hasNext()) || found){
						Object ob = iter.next();
						if (ob instanceof LdapConnection){
							found = false;
							((AlarmConnection)alarmSelection).mapHierarchy((LdapConnection)ob);
						}
					}
				}
				else {
					showMessage("Select your Alarm connection first!");
				}
			}
		};
		autoMap.setText("Auto map to hierarchy");
		autoMap.setToolTipText("Map Alarm source on tree hierarchy automatically.");
		autoMap.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));

		disableAlarm = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object contextSelection = ((IStructuredSelection)selection).getFirstElement();
				if (contextSelection instanceof ContextTreeObject){
					if (((ContextTreeObject)contextSelection).getMyActiveAlarmState()!=null){
						((ContextTreeObject)contextSelection).acknowledgeMyAlarmState();
					}
					else {
						Alarm alm = ((ContextTreeObject)contextSelection).getMaxUnacknowledgedAlarmObject();
						if (alm instanceof AlarmTreeObject) {((AlarmTreeObject)alm).disableAlarm();}
						else ((ContextTreeObject)contextSelection).disableAlarm(alm);
					}
					
				}
				else if (contextSelection instanceof AlarmTreeObject){
					if (contextSelection instanceof AlarmConnection){
						
					}
					else {((AlarmTreeObject)contextSelection).disableAlarm();}
				}
				else {
					showMessage("Select your node first!");
				}
				viewer.refresh();
			}
		};
		disableAlarm.setText("Acknowledge Alarm");
		disableAlarm.setToolTipText("Acknowledge alarm on node - or highest children's alarm is node hasn't got an alarm");
		disableAlarm.setImageDescriptor(AlarmTreePlugin.getImageDescriptor("./icons/Alarm.gif"));
		
		refreshAction = new Action() {
			public void run() {
				viewer.refresh(true);
			}
		};
		refreshAction.setText("Refresh");
		refreshAction.setToolTipText("Refresh view");
		refreshAction.setImageDescriptor(AlarmTreePlugin.getImageDescriptor("./icons/refresh.gif"));
		
		doubleClickAction = disableAlarm;
	}

	/**
	 * Adds a double click action to the tree viewer.
	 */
	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	
	/**
	 * Displays an informational message in a popup message dialog.
	 * @param message the message to display.
	 */
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Ldap tree view",
			message);
	}

	/**
	 * Passes the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	/**
	 * Refreshes this view.
	 */
	public void refresh(){
		viewer.refresh();
	}

}