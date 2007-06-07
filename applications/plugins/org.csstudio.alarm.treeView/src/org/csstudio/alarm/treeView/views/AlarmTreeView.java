package org.csstudio.alarm.treeView.views;

import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.jms.AlarmQueueSubscriber;
import org.csstudio.alarm.treeView.ldap.LdapDirectoryReader;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.ui.progress.PendingUpdateAdapter;

/**
 * Tree view of process variables and their alarm state. This view uses LDAP
 * to get a hierarchy of process variables and displays them in a tree view.
 * Process variables which are in an alarm state are visually marked in the
 * view.
 */
public class AlarmTreeView extends ViewPart {

	private final static String ID = "org.csstudio.alarm.treeView.views.LdapTView";
	private TreeViewer viewer;
	private Action reloadAction;
	private AlarmQueueSubscriber alarmQueueSubscriber;
	
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
		viewer.setContentProvider(new AlarmTreeContentProvider());
		viewer.setLabelProvider(new AlarmTreeLabelProvider());
		viewer.setComparator(new ViewerComparator());

		createActions();
		initializeContextMenu();
		contributeToActionBars();
		
        // The directory is read in the background. Until then, set the viewer's
		// input to a placeholder object.
		viewer.setInput(new Object[] {new PendingUpdateAdapter()});
		startDirectoryReaderJob();
	}

	/**
	 * Starts a job which reads the contents of the directory in the background.
	 */
	private void startDirectoryReaderJob() {
		IWorkbenchSiteProgressService progressService =
			(IWorkbenchSiteProgressService) getSite().getAdapter(
					IWorkbenchSiteProgressService.class);
		final SubtreeNode rootNode = new SubtreeNode("ROOT");
		Job directoryReader = new LdapDirectoryReader(rootNode);
		
		// Add a listener that sets the viewers input to the root node
		// when the reader job is finished.
		directoryReader.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				setJmsListenerTree(rootNode);
				asyncSetViewerInput(rootNode);
			}
		});
		progressService.schedule(directoryReader, 0, true);
	}
	
	/**
	 * Sets the tree to which the JMS listener will apply updates. If the
	 * JMS listener is not started yet, this will also initialize and start
	 * the JMS listener.
	 * @param tree the tree to which updates should be applied.
	 */
	private void setJmsListenerTree(final SubtreeNode tree) {
		if (alarmQueueSubscriber == null) {
			alarmQueueSubscriber = new AlarmQueueSubscriber(tree);
			alarmQueueSubscriber.openConnection();
		} else {
			alarmQueueSubscriber.setTree(tree);
		}
	}
	
	/**
	 * Stops the alarm queue subscriber.
	 */
	private void disposeJmsListener() {
		alarmQueueSubscriber.closeConnection();
	}

	/**
	 * Sets the input for the tree. The actual work will be done asynchronously
	 * in the UI thread.
	 * @param inputElement the new input element.
	 */
	private void asyncSetViewerInput(final SubtreeNode inputElement) {
		getSite().getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				viewer.setInput(inputElement);
			}
		});
	}
	
	@Override
	public void dispose() {
		disposeJmsListener();
		super.dispose();
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
		// currently there are no actions in the pulldown menu
	}

	/**
	 * Adds the context menu actions.
	 * @param manager the menu manager.
	 */
	private void fillContextMenu(IMenuManager manager) {
		// currently this plugin itself doesn't offer any context menu actions
		
		// adds a separator after which contributed actions from other plug-ins
		// will be displayed
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	/**
	 * Adds the tool bar actions.
	 * @param manager the menu manager.
	 */
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(reloadAction);
	}

	/**
	 * Creates the actions offered by this view.
	 */
	private void createActions() {
		reloadAction = new Action() {
			public void run() {
				startDirectoryReaderJob();
			}
		};
		reloadAction.setText("Reload");
		reloadAction.setToolTipText("Reload");
		reloadAction.setImageDescriptor(
				AlarmTreePlugin.getImageDescriptor("./icons/refresh.gif"));
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