package org.csstudio.dct.model;

/**
 * Represents a folder member.
 * 
 * @author Sven Wende
 * 
 */
public interface IFolderMember {
	/**
	 * Sets the parent folder.
	 * 
	 * @param folder the parent folder
	 */
	void setParentFolder(IFolder folder);

	/**
	 * Returns the parent folder.
	 * 
	 * @return the parent folder
	 */
	IFolder getParentFolder();
}
