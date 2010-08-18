package org.remotercp.contacts;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ecf.presence.IFQID;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.Presence;
import org.eclipse.ecf.presence.IPresence.Type;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.ecf.presence.roster.RosterGroup;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.remotercp.common.resource.IResourceGroup;
import org.remotercp.common.resource.IResourceService;
import org.remotercp.contacts.images.ImageKeys;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;
import org.remotercp.util.roster.RosterUtil;

public class ContactsAdapterFactory implements IAdapterFactory {

	private IResourceService resourceService;

	/*****************************************************************
	 * Adapter for {@link IRoster}
	 */
	private IWorkbenchAdapter rosterAdapter = new IWorkbenchAdapter() {
		public Object getParent(Object o) {
			// roster is not supposed to have a parent
			return null;
		}

		public String getLabel(Object o) {
			return "";
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return null;
		}

		public Object[] getChildren(Object o) {
			IRoster roster = (IRoster) o;
			return roster.getItems().toArray();
		}
	};

	/*****************************************************************
	 * Adapter for {@link IRosterGroup}
	 */
	private IWorkbenchAdapter groupAdapter = new IWorkbenchAdapter() {
		public Object getParent(Object o) {
			IRosterGroup group = (IRosterGroup) o;
			return group.getParent();
		}

		public String getLabel(Object o) {
			RosterGroup group = (RosterGroup) o;

			boolean areChildrenRosterEntries = this
					.areChildrenInstanceOfRosterEntry(group);

			if (areChildrenRosterEntries) {
				int available = getNumAvailable(group);

				return group.getName() + " (" + available + "/"
						+ group.getEntries().size() + ")";
			}

			return group.getName();
		}

		/**
		 * Returns the amount of available user to chat with
		 * 
		 * @param group
		 *            The group which online users have to be determined
		 * @return amount of online user
		 */
		@SuppressWarnings("unchecked")
		private int getNumAvailable(IRosterGroup group) {
			int available = 0;
			Collection<IRosterEntry> entries = group.getEntries();

			for (IRosterEntry entry : entries) {
				IPresence presence = ((IRosterEntry) entry).getPresence();
				if (presence != null
						&& presence.getType() != IPresence.Type.UNAVAILABLE) {
					available++;
				}
			}

			return available;
		}

		/**
		 * Groups can contain other groups. This method determines whether the
		 * children of a group are instance of {@link IRosterGroup} or
		 * {@link IRosterEntry}
		 * 
		 * @param child
		 *            The {@link IRosterGroup} to determine children for
		 * @return True if group contains {@link IRosterEntry}, otherwise false
		 */
		private boolean areChildrenInstanceOfRosterEntry(IRosterGroup group) {
			// check whether children are further groups or entries
			if (group.getEntries().iterator().hasNext()) {
				IRosterItem item = (IRosterItem) group.getEntries().iterator()
						.next();

				if (item instanceof IRosterEntry) {
					return true;
				}
			}
			return false;
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return AbstractUIPlugin.imageDescriptorFromPlugin(
					ContactsActivator.PLUGIN_ID, ImageKeys.GROUP);
		}

		@SuppressWarnings("unchecked")
		public Object[] getChildren(Object o) {
			IRosterGroup group = (IRosterGroup) o;

			Collection<IRosterEntry> entries = group.getEntries();

			Collection<IResourceGroup> resourceGroupsForRosterEntries = RosterUtil
					.getResourceGroupsForRosterEntries(group.getRoster(),
							entries);
			return resourceGroupsForRosterEntries.toArray();
		}
	};

	/*****************************************************************
	 * Adapter for {@link IRosterEntry}
	 */
	private IWorkbenchAdapter entryAdapter = new IWorkbenchAdapter() {
		public Object getParent(Object o) {
			IRosterEntry entry = (IRosterEntry) o;
			return entry.getParent();
		}

		public String getLabel(Object o) {
			IRosterEntry entry = ((IRosterEntry) o);

			// ID id = entry.getUser().getID();
			// XMPPID xmppID = (XMPPID) id;
			// String resourceName = xmppID.getResourceName();
			IFQID fqID = (IFQID) entry.getUser().getID()
					.getAdapter(IFQID.class);

			return fqID.getFQName();
			// return xmppID.getName();
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			IRosterEntry entry = (IRosterEntry) object;
			String key = presenceToKey(entry.getPresence());
			return AbstractUIPlugin.imageDescriptorFromPlugin(
					ContactsActivator.PLUGIN_ID, key);
		}

		public Object[] getChildren(Object o) {
			// entries are not supposed to have children
			return new Object[0];

			// return new IResourceGroup[0];
		}
	};

	private IWorkbenchAdapter resourceAdapter = new IWorkbenchAdapter() {

		public Object[] getChildren(Object o) {
			IResourceGroup resourceGroup = (IResourceGroup) o;

			/*
			 * return only user with resources if there is more than one online
			 * user in a group. Only in this case a new node makes sense.
			 */
			List<IRosterEntry> userWithResources = resourceGroup
					.getUserWithResources();
			boolean isAtLeastOneUserOnline = false;
			for (IRosterEntry entry : userWithResources) {
				if (RosterUtil.isRosterItemOnline(entry)) {
					isAtLeastOneUserOnline = true;
					break;
				}
			}

			if (isAtLeastOneUserOnline) {
				return resourceGroup.getUserWithResources().toArray();
			} else {
				return new Object[0];
			}
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			IPresence p = null;
			IResourceGroup resourceGroup = (IResourceGroup) object;

			List<IRosterEntry> userWithResources = resourceGroup
					.getUserWithResources();

			/*
			 * check, if at least one user is online. If that's the case,
			 * display online icon
			 */
			boolean isOneUserOnline = false;
			for (IRosterEntry entry : userWithResources) {
				boolean rosterItemOnline = RosterUtil.isRosterItemOnline(entry);

				if (rosterItemOnline) {
					isOneUserOnline = true;
					break;
				}
			}

			if (isOneUserOnline) {
				p = new Presence(Type.AVAILABLE);
			} else {
				p = new Presence(Type.UNAVAILABLE);
			}
			String presenceKey = presenceToKey(p);

			return AbstractUIPlugin.imageDescriptorFromPlugin(
					ContactsActivator.PLUGIN_ID, presenceKey);
		}

		public String getLabel(Object o) {
			IResourceGroup resource = (IResourceGroup) o;
			return resource.getLabel();
		}

		public Object getParent(Object o) {
			// TODO Auto-generated method stub
			return null;
		}

	};

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof IRosterGroup)
			return groupAdapter;
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof IRosterEntry)
			return entryAdapter;
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof IRoster)
			return rosterAdapter;
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof IResourceGroup)
			return resourceAdapter;
		return null;
	}

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		return new Class[] { IWorkbenchAdapter.class };
	}

	/**
	 * Returns an Image URL for a given Prsence
	 * 
	 * @param presence
	 * @return
	 */
	private String presenceToKey(IPresence p) {
		if (p.getType() == IPresence.Type.AVAILABLE) {
			return ImageKeys.ONLINE16x16;
		}

		if (p.getType() == IPresence.Type.UNAVAILABLE) {
			return ImageKeys.OFFLINE16x16;
		}
		return ImageKeys.OFFLINE16x16;
	}

	private IResourceService getResourceService() {
		if (resourceService == null) {
			resourceService = OsgiServiceLocatorUtil.getOSGiService(
					ContactsActivator.getBundleContext(),
					IResourceService.class);
		}
		assert resourceService != null : "resourceService != null";
		return this.resourceService;
	}
}
