package org.remotercp.contacts.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.IPresenceListener;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.ecf.presence.roster.Roster;
import org.eclipse.ecf.presence.roster.RosterGroup;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;
import org.remotercp.common.resource.IResourceGroup;
import org.remotercp.contacts.ContactsActivator;
import org.remotercp.contacts.ContactsContentProvider;
import org.remotercp.contacts.ContactsLabelProvider;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;
import org.remotercp.util.roster.RosterUtil;

public class SelectedContactsView extends ViewPart {

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private TreeViewer selectedContactsViewer;

	public SelectedContactsView() {
		// nothing to do
	}

	@Override
	public void createPartControl(Composite parent) {
		this.selectedContactsViewer = new TreeViewer(parent, SWT.BORDER
				| SWT.MULTI | SWT.V_SCROLL);

		/*
		 * Register Treeviewer as Selection provider
		 */
		getSite().setSelectionProvider(this.selectedContactsViewer);

		this.selectedContactsViewer
				.setContentProvider(new ContactsContentProvider());
		this.selectedContactsViewer
				.setLabelProvider(new ContactsLabelProvider());
		this.selectedContactsViewer
				.setFilters(new ViewerFilter[] { new SelectedContactsFilter() });

		this.hookKeyListener();

		this.initDragAndDropSupport();

		this.hookPresenceListener();

	}

	private void hookPresenceListener() {
		ISessionService sessionService = OsgiServiceLocatorUtil.getOSGiService(
				ContactsActivator.getBundleContext(), ISessionService.class);
		sessionService.getRosterManager().addPresenceListener(
				new IPresenceListener() {

					public void handlePresence(ID fromID, IPresence presence) {

						SelectedContactsView.this.selectedContactsViewer
								.getControl().getDisplay().asyncExec(
										new Runnable() {
											public void run() {
												SelectedContactsView.this.selectedContactsViewer
														.refresh();
											}
										});
					}
				});

	}

	private void hookKeyListener() {
		this.selectedContactsViewer.getControl().addKeyListener(
				new KeyAdapter() {
					public void keyPressed(KeyEvent e) {
						if (e.character == SWT.DEL) {
							IStructuredSelection selection = (IStructuredSelection) selectedContactsViewer
									.getSelection();
							removeTreeItem(selection);
						}
					}
				});
	}

	/*
	 * This method is still buggy! Think of an easier way to remove contacts
	 * from tree
	 */
	private void removeTreeItem(IStructuredSelection selection) {
		Object oldInput = this.selectedContactsViewer.getInput();
		Roster roster = (Roster) oldInput;
		IRosterItem item = (IRosterItem) selection.getFirstElement();

		if (item instanceof IRoster) {
			roster = null;
		} else {
			roster.removeItem(item);
		}

		/*
		 * selected item might be in a group and not directly belonging to
		 * roster. Walk through the groups and delete the item in the according
		 * group
		 */
		if (selection.getFirstElement() instanceof IRosterEntry) {
			for (Object rosterItem : roster.getItems()) {
				if (rosterItem instanceof IRosterGroup) {
					RosterGroup group = (RosterGroup) rosterItem;
					/* item will only be removed if group contains the item */
					group.remove(item);

					// check if group has further items. If not delete the group
					// as well
					if (RosterUtil.filterOnlineUser(group).isEmpty()) {
						roster.removeItem(group);
					}
					/* check if roster has further items */
					if (RosterUtil.filterOnlineUser(roster).isEmpty()) {
						roster = null;
					}
				}
			}
		}
		List<IRosterEntry> filterOnlineUser = RosterUtil
				.filterOnlineUser(roster);
		if (filterOnlineUser.isEmpty()) {
			roster = null;
		}

		// this.setInput(roster);
		this.selectedContactsViewer.refresh();
		pcs.firePropertyChange("Input changed", oldInput, roster);
	}

	private void initDragAndDropSupport() {
		int dropOperations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
		Transfer[] transfers = new Transfer[] { TreeObjectTransfer
				.getInstance() };

		this.selectedContactsViewer.addDropSupport(dropOperations, transfers,
				new DropTargetAdapter() {
					@Override
					public void drop(DropTargetEvent event) {
						final IRosterItem dragItem = DragAndDropSupport
								.getInstance().getDragItem();

						if (dragItem != null) {
							Display.getCurrent().asyncExec(new Runnable() {
								public void run() {
									SelectedContactsView.this
											.setInput(dragItem);
								}
							});
						}
					}

					@Override
					public void dragOver(DropTargetEvent event) {
						/*
						 * Display copy symbol as a hint whether drop is
						 * supported
						 */
						event.detail = DND.DROP_COPY;
					}
				});
	}

	/**
	 * As a user can drag and drop items in the selected contacts view the old
	 * input must me kept in the content provider. But at the same time one has
	 * to check whether same items will be droped in and filter them out
	 * 
	 * @param input
	 */
	@SuppressWarnings("unchecked")
	protected void setInput(Object input) {
		IRoster oldInput = (IRoster) this.selectedContactsViewer.getInput();

		IRoster roster = new Roster(null);
		if (oldInput != null) {
			roster.getItems().addAll(oldInput.getItems());
		}

		// check if roster contains already the given input
		if (input != null
				&& !RosterUtil.hasRosterItem(roster, (IRosterItem) input)) {
			roster.getItems().add(input);
		} else {
			roster = null;
		}
		/*
		 * now roster has old and new input
		 */
		this.selectedContactsViewer.setInput(roster);
		this.selectedContactsViewer.expandAll();

		// inform listener about input changes
		pcs.firePropertyChange("Input changed", oldInput, roster);
	}

	@Override
	public void setFocus() {
		this.selectedContactsViewer.getControl().setFocus();
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter.getName().equals(IPropertyChangeListener.class.getName())) {
			return this.pcs;
		}
		if (adapter.getName().equals(IRoster.class.getName())) {
			return this.selectedContactsViewer.getInput();
		}
		return null;
	}

	/**
	 * This filter is responsible to filter online user from all IRorsterItems.
	 * 
	 * @author Eugen Reiswich
	 * 
	 */
	private class SelectedContactsFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {

			boolean isOnline = true;
			if (element instanceof IRosterEntry) {
				IRosterEntry entry = (IRosterEntry) element;
				if (entry.getPresence().getType() != IPresence.Type.AVAILABLE) {
					isOnline = false;
				}
			}

			// filter offline IResourceGroup objects with just one user
			if (element instanceof IResourceGroup) {
				IResourceGroup resourceGroup = (IResourceGroup) element;
				List<IRosterEntry> userWithResources = resourceGroup
						.getUserWithResources();
				if (userWithResources.size() == 1) {
					IRosterEntry rosterEntry = userWithResources.get(0);
					assert rosterEntry != null : "rosterEntry != null";
					if (rosterEntry.getPresence().getType() != IPresence.Type.AVAILABLE) {
						isOnline = false;
					}
				}

			}
			return isOnline;
		}

	}

}
