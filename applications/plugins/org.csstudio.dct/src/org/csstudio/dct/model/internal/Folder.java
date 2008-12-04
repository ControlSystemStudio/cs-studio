package org.csstudio.dct.model.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IFolderMember;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;

/**
 * Standard implementation of {@link IFolder}.
 * 
 * @author Sven Wende
 */
public class Folder extends AbstractElement implements IFolderMember, IFolder, IAdaptable {
	private List<IFolderMember> members;
	private IFolder parentFolder;

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            the name
	 */
	public Folder(String name) {
		super(name);
		members = new ArrayList<IFolderMember>();
	}
	
	public Folder(String name, UUID id) {
		super(name, id);
		members = new ArrayList<IFolderMember>();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IFolderMember> getMembers() {
		return members;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addMember(IFolderMember member) {
		assert member.getParentFolder() != null;
		members.add(member);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setMember(int index, IFolderMember member) {
		assert member.getParentFolder() != null;
		
		// .. fill with nulls
		while(index>=members.size()) {
			members.add(null);
		}
		
		members.set(index, member);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addMember(int index, IFolderMember member) {
		assert member.getParentFolder() != null;
		members.add(index, member);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeMember(IFolderMember member) {
		assert member.getParentFolder() == this : "member.getParentFolder()==this";
		members.remove(member);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeMember(int index) {
		IFolderMember member = members.remove(index);

		assert member.getParentFolder() == this : "member.getParentFolder()==this";
	}

	/**
	 * {@inheritDoc}
	 */
	public IFolder getParentFolder() {
		return parentFolder;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setParentFolder(IFolder folder) {
		parentFolder = folder;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		boolean result = false;

		if (obj instanceof Folder) {
			Folder instance = (Folder) obj;

			if (super.equals(obj)) {
				// .. members
				if (getMembers().equals(instance.getMembers())) {
					result = true;
				}
			}
		}

		return result;
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((members == null) ? 0 : members.hashCode());
		return result;

	}



}
