package org.remotercp.contacts.ui;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.csstudio.platform.management.CommandDescription;
import org.csstudio.platform.management.IManagementCommandService;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.IPresenceListener;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.remotercp.contacts.ContactsActivator;
import org.remotercp.contacts.ContactsContentProvider;
import org.remotercp.contacts.ContactsLabelProvider;
import org.remotercp.contacts.actions.ManagementCommandAction;
import org.remotercp.contacts.images.ImageKeys;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;
import org.remotercp.util.roster.RosterUtil;

public class ContactsView extends ViewPart {

	// ID must correspond to that one in the plugin xml
	public static final String VIEW_ID = "contacts.ui.contactsview";

	private TreeViewer treeViewer;

	private IPresenceListener presenceListener;

	private ISessionService session;
	
	/** ExecutorService for querying the remote management commands. */
	private ExecutorService commandQueryExecutor;

	private final static Logger logger = Logger.getLogger(ContactsView.class
			.getName());

	public ContactsView() {
		this.initServices();
		commandQueryExecutor = Executors.newSingleThreadExecutor();
	}

	protected void initServices() {
		this.session = OsgiServiceLocatorUtil.getOSGiService(ContactsActivator
				.getBundleContext(), ISessionService.class);
	}

	@Override
	public void createPartControl(Composite parent) {
		if (!this.session.hasContainer()) {
			Composite messageNotConnected = new Composite(parent, SWT.NONE);
			messageNotConnected.setLayout(new GridLayout());
			Label message = new Label(messageNotConnected, SWT.NONE);
			message.setText("not connected");
			message.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
			return;
		}
		
		this.treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);

		/*
		 * Register Treeviewer as Selection provider
		 */
		getSite().setSelectionProvider(treeViewer);

		treeViewer.setLabelProvider(new ContactsLabelProvider());
		treeViewer.setContentProvider(new ContactsContentProvider());

		treeViewer.setInput(this.session.getRoster());
		treeViewer.expandAll();
		
		/*
		 * Note: instead of IPresenceListener, in theory IRosterListener should
		 * work better here because it has specific methods to handle add,
		 * remove and update events. But the actual behavior of IRosterListener
		 * is a bit strange:
		 * 
		 * When a user goes online, there is only an update event for the
		 * RosterEntry.
		 * 
		 * When the same user goes online a second time, there is an add event
		 * for the RosterEntry and an update event for the Roster.
		 * 
		 * When one instance terminates, there is an update event for the 
		 * Roster. When the user goes completely offline (i.e., the other
		 * instance goes offline, too), there is again an update event for the
		 * RosterEntry.
		 * 
		 * In my testing, I never got a removed event.
		 * 
		 * Also, when the RosterEntry is updated and I call
		 * treeViewer.refresh(entry), that doesn't seem to have any effect. That
		 * might be because the content provider is designed so that it doesn't
		 * add the actual objects to the tree but only adapters.
		 * 
		 * (jr/20090612)
		 */
		presenceListener = new IPresenceListener() {
			public void handlePresence(ID fromID, IPresence presence) {
				refresh();
			}
		};
		this.session.getRosterManager().addPresenceListener(presenceListener);

		// hook context menu
		this.hookContextMenu();

		// show online user in status line
		this.setStatuslineInfo();

		this.initDragAndDrop();
	}

	private void hookContextMenu() {
		final MenuManager menuManager = new MenuManager("#PopupMenu");
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ContactsView.this.fillContextMenu(menuManager);
			}
		});
		
		Menu menu = menuManager.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager, treeViewer);
	}

	/**
	 * Fills the context menu.
	 * 
	 * @param menuManager
	 *            the menu manager for the context menu.
	 */
	private void fillContextMenu(MenuManager menuManager) {
		IStructuredSelection selection =
			(IStructuredSelection) treeViewer.getSelection();
		if (selection.size() == 1) {
			Object selectedItem = selection.getFirstElement();
			if (selectedItem instanceof IRosterEntry) {
				addManagementActions((IRosterEntry) selectedItem, menuManager);
			}
		}
		
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/**
	 * Adds the management actions provided by the specified roster entry to the
	 * menu manager.
	 * 
	 * @param rosterEntry
	 *            the roster entry.
	 * @param menuManager
	 *            the menu manager.
	 */
	private void addManagementActions(final IRosterEntry rosterEntry, final MenuManager menuManager) {
		BusyIndicator.showWhile(treeViewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				ID userId = rosterEntry.getUser().getID();
				ManagementCommandAction[] actions =
					requestManagementCommandActionsWithTimeout(userId);
				for (ManagementCommandAction action : actions) {
					menuManager.add(action);
				}
			}
		});
	}

	/**
	 * Requests the management commands from the remote user with the given ID.
	 * If no remote management service is found or if the service does not
	 * return a result within a limited time, this method returns an empty array
	 * and cancels the request.
	 * 
	 * @param userId
	 *            the user id from which to query the management commands.
	 * @return the management command actions.
	 */
	private ManagementCommandAction[] requestManagementCommandActionsWithTimeout(
			final ID userId) {
		Callable<ManagementCommandAction[]> request = new Callable<ManagementCommandAction[]>() {
			public ManagementCommandAction[] call() {
				List<IManagementCommandService> managementServices =
					ContactsView.this.session.getRemoteServiceProxies(
						IManagementCommandService.class, new ID[] {userId});
				if (managementServices.size() == 1) {
					final IManagementCommandService service =
						managementServices.get(0);
					CommandDescription[] commands = service.getSupportedCommands();
					ManagementCommandAction[] actions = new ManagementCommandAction[commands.length];
					for (int i = 0; i < commands.length; i++) {
						actions[i] = new ManagementCommandAction(commands[i], service);
					}
					return actions;
				} else {
					return new ManagementCommandAction[0];
				}
			}
		};
		
		Future<ManagementCommandAction[]> result = commandQueryExecutor.submit(request);
		try {
			return result.get(3, TimeUnit.SECONDS);
		} catch (Exception e) {
			return new ManagementCommandAction[0];
		}
	}

	/*
	 * Displays the current user in the status line
	 */
	private void setStatuslineInfo() {
		IStatusLineManager statusLineManager = getViewSite().getActionBars()
				.getStatusLineManager();
		ImageDescriptor imageDescriptorFromPlugin = AbstractUIPlugin
				.imageDescriptorFromPlugin(ContactsActivator.PLUGIN_ID,
						ImageKeys.ONLINE);
		Image image = imageDescriptorFromPlugin.createImage();

		IUser user = this.session.getRoster().getUser();

		statusLineManager.setMessage(image, "Online user: " + user.getName());
	}

	private void refresh() {
		getSite().getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				// TODO: getExpanded/setExpanded does not work.
				// This might also be because the content provider doesn't add
				// the roster items themselves to the tree but only adapters.
				// Maybe it creates new adapters every time?
//				TreePath[] expanded = treeViewer.getExpandedTreePaths();
				treeViewer.refresh();
//				treeViewer.setExpandedTreePaths(expanded);
			}
		});
	}

	private void initDragAndDrop() {
		final int dragOperations = DND.DROP_MOVE | DND.DROP_COPY;
		final Transfer[] types = new Transfer[] { TreeObjectTransfer
				.getInstance() };

		this.treeViewer.addDragSupport(dragOperations, types,
				new DragSourceListener() {

					IRosterItem selection = null;

					public void dragFinished(DragSourceEvent event) {
						if (event.detail == DND.DROP_MOVE) {
							/*
							 * think of coloring the draged files gray in order
							 * to show which items have already been selected
							 */
							logger.info("drag finished");
						}

					}

					public void dragSetData(DragSourceEvent event) {
						if (TreeObjectTransfer.getInstance().isSupportedType(
								event.dataType)) {

							// event.data = selection;
							DragAndDropSupport.getInstance().setDragItem(
									selection);
							logger.info("drag set data");
						}

					}

					public void dragStart(DragSourceEvent event) {
						/*
						 * only start the drag if the selected IRosterItem is
						 * online
						 */

						selection = getTreeViewerSelection();

						if (RosterUtil.isRosterItemOnline(selection)) {
							event.doit = true;
						} else {
							event.doit = false;
						}

						logger.info("Drag start = " + event.doit);
					}

				});
	}

	private IRosterItem getTreeViewerSelection() {
		IStructuredSelection selection = (IStructuredSelection) ContactsView.this.treeViewer
				.getSelection();

		IRosterItem item = (IRosterItem) selection.getFirstElement();
		return item;
	}

	@Override
	public void setFocus() {
		// null check is required because treeViewer is not created if the
		// SessionService is not connected
		if (treeViewer != null) {
			this.treeViewer.getControl().setFocus();
		}
	}

	@Override
	public void dispose() {
		session.getRosterManager().removePresenceListener(presenceListener);
		presenceListener = null;
		treeViewer = null;
	}
}
