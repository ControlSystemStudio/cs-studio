package org.remotercp.common.resource;

import java.util.Collection;

import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;

public interface IResourceService {

	public Collection<IResourceGroup> getResourceGroupsForRosterEntries(
			IRoster roster, Collection<IRosterEntry> entries);

}
