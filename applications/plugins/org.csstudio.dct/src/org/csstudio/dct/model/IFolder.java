package org.csstudio.dct.model;

import java.util.List;

/**
 * Represents a file system like folder.
 *
 * @author Sven Wende
 *
 */
public interface IFolder extends IElement, IFolderMember {

    /**
     * Returns all members.
     *
     * @return all members
     */
    List<IFolderMember> getMembers();

    /**
     * Adds a member.
     *
     * @param member
     *            a member
     */
    void addMember(IFolderMember member);

    /**
     * Adds a member at the specified index.
     *
     * @param index
     *            the position index
     *
     * @param member
     *            the member
     */
    void addMember(int index, IFolderMember member);

    /**
     * Removes a member.
     *
     * @param member
     *            the member
     */
    void removeMember(IFolderMember member);

    /**
     * Replaces the member at the specified position.
     *
     * @param index the list index
     * @param member the member
     */
    void setMember(int index, IFolderMember member);

    /**
     * Removes the member at the specified index.
     *
     * @param index
     *            the position index
     */
    void removeMember(int index);

}
