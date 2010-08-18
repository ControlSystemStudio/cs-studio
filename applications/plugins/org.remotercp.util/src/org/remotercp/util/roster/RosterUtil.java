package org.remotercp.util.roster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.ecf.presence.roster.RosterEntry;
import org.eclipse.ecf.presence.roster.RosterItem;
import org.remotercp.common.resource.IResourceGroup;
import org.remotercp.common.resource.ResourceGroup;

public class RosterUtil {

	/**
	 * Returns an array with user IDs for all user of a given IRoster item
	 *
	 */
	public synchronized static ID[] getUserIDs(final IRoster roster) {
		final List<IRosterEntry> entries = getRosterEntries(roster);

		final ID[] ids = getUserIDs(entries);

		return ids;
	}

	public synchronized static ID[] getUserIDs(final List<IRosterEntry> rosterEntries) {
		final ID[] ids = new ID[rosterEntries.size()];

		for (int entry = 0; entry < rosterEntries.size(); entry++) {
			ids[entry] = rosterEntries.get(entry).getUser().getID();
		}

		return ids;
	}

	/**
	 * Returns a list of {@link RosterEntry} for a given {@link RosterItem} e.g.
	 * returns users for a given user group
	 *
	 * @param entries
	 *            An empty list of entries which will be filled in this method
	 * @param item
	 *            The {@link IRosterItem} that containes {@link IRosterEntry}
	 * @return A list with {@link IRosterEntry} objects
	 */
	public synchronized static List<IRosterEntry> getRosterEntries(
			final IRosterItem item) {

		List<IRosterEntry> entries = Collections
				.synchronizedList(new ArrayList<IRosterEntry>());

		entries = searchRecursiveForRosterEntries(entries, item);

		return entries;

	}

	private synchronized static List<IRosterEntry> searchRecursiveForRosterEntries(
			final List<IRosterEntry> entries, final IRosterItem item) {
		if (item instanceof IRoster) {
			final IRoster roster = (IRoster) item;
			// iterate over child elements
			final Collection rosterItems = roster.getItems();
			synchronized (rosterItems) {
				for (final Object rosterItem : rosterItems) {
					searchRecursiveForRosterEntries(entries,
							(IRosterItem) rosterItem);
				}
			}
		}

		if (item instanceof IRosterGroup) {
			final IRosterGroup group = (IRosterGroup) item;
			// iterate over child elements
			final Collection groupEntries = group.getEntries();
			synchronized (groupEntries) {
				for (final Object rosterItem : groupEntries) {
					searchRecursiveForRosterEntries(entries,
							(IRosterItem) rosterItem);
				}
			}
		}

		if (item instanceof IResourceGroup) {
			final IResourceGroup resourceGroup = (IResourceGroup) item;
			final List<IRosterEntry> userWithResources = resourceGroup
					.getUserWithResources();
			if (!entries.containsAll(userWithResources)) {
				entries.addAll(userWithResources);
			}
		}

		if (item instanceof IRosterEntry) {
			/*
			 * users can be member in several groups, but user is here needed
			 * only once
			 */
			if (!entries.contains(item)) {
				entries.add((IRosterEntry) item);
			}
		}

		return entries;
	}

	/**
	 * Returns a List with online {@link IRosterEntry} for a given
	 * {@link IRosterItem}
	 *
	 * @param item
	 *            The {@link IRosterItem} which elements need to be checked for
	 *            presence
	 * @return
	 */
	public synchronized static List<IRosterEntry> filterOnlineUser(
			final IRosterItem item) {
		final List<IRosterEntry> entries = getRosterEntries(item);
		final List<IRosterEntry> filteredEntries = new ArrayList<IRosterEntry>();

		for (final IRosterEntry entry : entries) {
			if (entry.getPresence().getType() == IPresence.Type.AVAILABLE) {
				filteredEntries.add(entry);
			}
		}

		return filteredEntries;
	}

	/**
	 * Returns an Array with online {@link IRosterEntry} for a given
	 * {@link IRosterItem}
	 *
	 * @param item
	 *            The {@link IRosterItem} which elements need to be checked for
	 *            presence
	 * @return
	 */
	public synchronized static ID[] filterOnlineUserAsArray(final IRosterItem item) {
		final List<IRosterEntry> onlineUser = filterOnlineUser(item);
		final IRosterEntry[] rosterEntries = onlineUser
				.toArray(new IRosterEntry[onlineUser.size()]);
		final ID[] userIDs = new ID[onlineUser.size()];

		for (int rosterEntry = 0; rosterEntry < rosterEntries.length; rosterEntry++) {
			userIDs[rosterEntry] = rosterEntries[rosterEntry].getUser().getID();
		}

		return userIDs;
	}

	/**
	 * If the selected item is a user group, check whether at least one user in
	 * the group is online. If the selected item is a user, check if the user is
	 * online
	 */
	public synchronized static boolean isRosterItemOnline(final IRosterItem item) {
		final List<IRosterEntry> entries = getRosterEntries(item);

		boolean userOnline = false;

		for (final IRosterEntry entry : entries) {
			if (entry.getPresence().getType() == IPresence.Type.AVAILABLE) {
				userOnline = true;
			}
		}
		return userOnline;
	}

	/**
	 * Returns whether a IRoster contains already a given IRosterItem.
	 * IRosterItem may be either an IRosterGroup or IRosterEntry
	 *
	 * @param roster
	 * @param item
	 * @return
	 */
	public synchronized static boolean hasRosterItem(final IRoster roster,
			final IRosterItem item) {
		boolean rosterContainsItem = false;

		// items may be IRosterGroup and/or IRosterEntry
		for (final Object rosterItem : roster.getItems()) {
			// compare groups
			if ((rosterItem instanceof IRosterGroup)
					&& (item instanceof IRosterGroup)) {
				final IRosterGroup tempGroup = (IRosterGroup) rosterItem;
				final IRosterGroup parameterGroup = (IRosterGroup) item;

				if (tempGroup.getName().equals(parameterGroup.getName())) {
					rosterContainsItem = true;
					break;
				}
			}

			// compare entries
			if ((rosterItem instanceof IRosterEntry)
					&& (item instanceof IRosterEntry)) {
				final IRosterEntry tempEntry = (IRosterEntry) rosterItem;
				final IRosterEntry parameterEntry = (IRosterEntry) item;

				if (tempEntry.getUser().getID().equals(
						parameterEntry.getUser().getID())) {
					rosterContainsItem = true;
					break;
				}
			}

			// compare roster children with given roster item
			if ((rosterItem instanceof IRosterGroup)
					&& (item instanceof IRosterEntry)) {
				final IRosterGroup group = (IRosterGroup) rosterItem;
				for (final Object groupItem : group.getEntries()) {
					final IRosterEntry tempEntry = (IRosterEntry) groupItem;
					if (tempEntry.getUser().getID().equals(
							((IRosterEntry) item).getUser().getID())) {
						rosterContainsItem = true;
						break;
					}
				}
			}
		}

		return rosterContainsItem;
	}

	public static ID getRosterID(final ID fromID, final IRoster roster) {
		ID returnValue = null;
		final List<IRosterEntry> rosterEntries = getRosterEntries(roster);
		if ((rosterEntries != null) && !rosterEntries.isEmpty()) {
			for (final IRosterEntry rosterEntry : rosterEntries) {
				final ID rosterID = rosterEntry.getUser().getID();
				if (rosterID.equals(fromID)) {
					returnValue = rosterID;
					break;
				}
			}
		}
		return returnValue;
	}

	/**
	 * Returns for a given {@link IRosterEntry} object all entry+resource
	 * objects.
	 *
	 * @param roster
	 *            The roster object
	 * @param entry
	 *            The entry to find all entry+resource objects
	 * @return
	 */
	public static List<IRosterEntry> getRosterEntriesWithResources(
			final IRoster roster, final IRosterEntry entry) {
		final List<IRosterEntry> rosterEntries = getRosterEntries(roster);
		final List<IRosterEntry> entriesForUser = new ArrayList<IRosterEntry>();

		for (final IRosterEntry rosterEntry : rosterEntries) {
			if (rosterEntry.getName().equals(entry.getName())) {
				entriesForUser.add(rosterEntry);
			}
		}
		return entriesForUser;
	}

	/**
	 * Each returned {@link IResourceGroup} object contains a set of users with
	 * the same name but differenz resources.
	 *
	 * @param roster
	 *            The roster object with all users
	 * @param entries
	 *            The entries to get {@link IResourceGroup} for
	 * @return collection of {@link IResourceGroup} objects. For each provided
	 *         user an own {@link IResourceGroup} object is created.
	 */
	public static Collection<IResourceGroup> getResourceGroupsForRosterEntries(
			final IRoster roster, final Collection<IRosterEntry> entries) {
		assert roster != null : "roster != null";

		final Map<String, IResourceGroup> resourceGroups = new HashMap<String, IResourceGroup>();
		/*
		 * Put each user to the appropriate IResourceGroup
		 */
		for (final IRosterEntry entry : entries) {
			final List<IRosterEntry> rosterEntriesWithResources = getRosterEntriesWithResources(
					roster, entry);

			final ResourceGroup resourceGroup = new ResourceGroup();
			for (final IRosterEntry rosterEntry : rosterEntriesWithResources) {
				resourceGroup.putUser(rosterEntry);
			}

			resourceGroups.put(entry.getName(), resourceGroup);

		}

		return resourceGroups.values();
	}
}
