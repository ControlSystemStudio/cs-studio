package org.csstudio.dct.model.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IFolderMember;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IVisitor;

/**
 * Standard implementation of {@link IFolder}.
 *
 * @author Sven Wende
 */
public class Folder extends AbstractElement implements IFolderMember, IFolder {
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

    /**
     * Constructor.
     *
     * @param name
     *            the name
     *
     * @param id
     *            the id
     */
    public Folder(String name, UUID id) {
        super(name, id);
        members = new ArrayList<IFolderMember>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<IFolderMember> getMembers() {
        return members;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void addMember(IFolderMember member) {
        assert member != null;
        assert member.getParentFolder() == null;
        members.add(member);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setMember(int index, IFolderMember member) {
        assert member != null;

        // .. fill with nulls
        while (index >= members.size()) {
            members.add(null);
        }

        members.set(index, member);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void addMember(int index, IFolderMember member) {
        assert member.getParentFolder() == null;
        members.add(index, member);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void removeMember(IFolderMember member) {
        assert member.getParentFolder() == this : "member.getParentFolder()==this";
        members.remove(member);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void removeMember(int index) {
        IFolderMember member = members.remove(index);

        assert member.getParentFolder() == this : "member.getParentFolder()==this";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final IFolder getParentFolder() {
        return parentFolder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setParentFolder(IFolder folder) {
        parentFolder = folder;
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

    /**
     *{@inheritDoc}
     */
    @Override
    public final boolean isInherited() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void accept(IVisitor visitor) {
        visitor.visit((Folder) this);

        for (IFolderMember member : getMembers()) {
            member.accept(visitor);
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public IProject getProject() {
        if(this instanceof IProject) {
            return (IProject) this;
        } else {
            return parentFolder.getProject();
        }
    }

}
