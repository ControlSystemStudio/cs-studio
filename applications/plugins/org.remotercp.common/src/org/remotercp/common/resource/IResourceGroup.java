package org.remotercp.common.resource;

import java.util.List;

import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterItem;

/**
 * The {@link IResourceGroup} is used to get a list with user and their
 * resources. This interface extends {@link IRosterItem} to support the
 * drag&drop support for {@link IResourceGroup} objects.
 * 
 * @author Eugen Reiswich
 * 
 */
public interface IResourceGroup extends IRosterItem {

	public List<IRosterEntry> getUserWithResources();

	public String getLabel();

	public boolean hasUser(IRosterEntry entry);

}
