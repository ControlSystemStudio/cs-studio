package org.remotercp.common.resource;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ecf.presence.IFQID;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterItem;

public class ResourceGroup implements IResourceGroup {

	public List<IRosterEntry> user;

	public ResourceGroup() {
		this.user = new ArrayList<IRosterEntry>();
	}

	/**
	 * Adds a user with resources to the {@link IResourceGroup}
	 * 
	 * @param rosterID
	 *            The roster ID
	 * @precondition rosterID is not in the group
	 */
	public void putUser(IRosterEntry entry) {
		assert hasUser(entry) == false;

		this.user.add(entry);
	}

	public boolean hasUser(IRosterEntry entry) {
		assert entry != null : "rosterID != null";
		IFQID paramFQID = (IFQID) entry.getUser().getID().getAdapter(
				IFQID.class);
		boolean result = false;

		for (IRosterEntry id : this.user) {
			IFQID tempID = (IFQID) id.getUser().getID().getAdapter(IFQID.class);

			if (tempID.equals(paramFQID)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public List<IRosterEntry> getUserWithResources() {
		assert user != null : "user != null";
		return this.user;
	}

	public String getLabel() {
		assert !this.user.isEmpty();

		IRosterEntry id = this.user.get(0);
		return id.getUser().getID().getName();
	}

	public String getName() {
		return getLabel();
	}

	public IRosterItem getParent() {
		return null;
	}

	public IRoster getRoster() {
		return null;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

}
