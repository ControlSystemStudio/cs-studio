/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.config.ioconfig.model;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.tools.UserName;

/**
*
* @author gerke
* @author $Author: hrickens $
* @version $Revision: 1.4 $
* @since 21.03.2007
* @param <C> The Parent node types
* @param <P> The Children node types
*/
@SuppressWarnings("rawtypes")
@MappedSuperclass
public abstract class AbstractNodeSharedImpl<P extends NodeDBO, C extends NodeDBO<?,?>> extends NodeDBO<P,C> implements INode {

    protected static final int DEFAULT_MAX_STATION_ADDRESS = 255;
    private static final long serialVersionUID = 1L;
    private NodeImageDBO _icon;

    public AbstractNodeSharedImpl() {

    }

    public AbstractNodeSharedImpl(@Nonnull final P parent) throws PersistenceException {
        super(parent);
    }

    @SuppressWarnings("unused")
    @Transient
    public int getfirstFreeStationAddress() throws PersistenceException {
        final Map<Short, C> children = getChildrenAsMap();
        Short nextKey = 0;
        if (!children.containsKey(nextKey)) {
            return nextKey;
        }
        final Set<Short> descendingKeySet = children.keySet();
        for (final Short key : descendingKeySet) {
            if (key - nextKey > 1) {
                return (short) (nextKey + 1);
            }
            if (key >= 0) {
                nextKey = key;
            }
        }
        return nextKey + 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    @Nonnull
    public INode getParentAsINode() {
        return (INode) getParent();
    }

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public P getParent() {
//        return super.getParent();
//    }

    /**
     * Swap the SortIndex of two nodes. Is the given SortIndex in use the other node became the old
     * SortIndex of this node.
     *
     * @param toIdx the new sortIndex for this node.
     * @throws PersistenceException
     */
    public void moveSortIndex(final int toIndex) throws PersistenceException {
        final short index = this.getSortIndex();
        final short toIdx = (short) toIndex;
        if (toIdx == index) {
            return;
        }
        if (getParent() == null) { // TODO (hrickens) [02.08.2011]: Kann das nicht weg!
            setSortIndexNonHibernate(toIdx);
            return;
        }
        if (index == -1) {
            putNewNode(index, toIdx);
        } else {
            moveNode(index, toIdx);
        }
    }

    private void moveNode(final short index, final short toIndex) throws PersistenceException {
        int idx = index;
        final int direction = index > toIndex ? -1 : 1;
        final P parent = getParent();
        if (parent != null) {
            @SuppressWarnings("unchecked")
            final Map<Short, AbstractNodeSharedImpl<AbstractNodeSharedImpl<?, ?>, AbstractNodeSharedImpl<?, ?>>> childrenAsMap = parent
                    .getChildrenAsMap();
            final AbstractNodeSharedImpl<AbstractNodeSharedImpl<?, ?>, AbstractNodeSharedImpl<?, ?>> moveNode = childrenAsMap
                    .get(index);
            for (; idx != toIndex; idx += direction) {
                final AbstractNodeSharedImpl<AbstractNodeSharedImpl<?, ?>, AbstractNodeSharedImpl<?, ?>> nextNode = childrenAsMap
                        .get((short) (idx + direction));
                if (nextNode != null) {
                    nextNode.setSortIndexNonHibernate(idx);
                }
            }
            moveNode.setSortIndexNonHibernate(toIndex);
        }
    }

    private void putNewNode(final short index, final short toIndex) throws PersistenceException {
        short idx = index;
        final int direction = idx > toIndex ? -1 : 1;
        AbstractNodeSharedImpl<P, C> node = this;
        idx = toIndex;
        final P parent = getParent();
        if (parent != null) {
            @SuppressWarnings("unchecked")
            final Map<Short, ? extends AbstractNodeSharedImpl<P, C>> childrenAsMap = parent
                    .getChildrenAsMap();
            do {
                final AbstractNodeSharedImpl<P, C> nextNode = childrenAsMap.get(idx);
                node.setSortIndexNonHibernate(idx);
                node = nextNode;
                idx = (short) (idx + direction);
            } while (node != null);
        }
    }

    /**
     * Copy this node to the given Parent Node.
     *
     * @param parentNode the target parent node.
     * @return the copy of this node.
     * @throws PersistenceException
     */
    @Nonnull
    public AbstractNodeSharedImpl<P, C> copyThisTo(@Nonnull final P parentNode,
                                            @CheckForNull final String namePrefix) throws PersistenceException {
        final String createdBy = UserName.getUserName();
        final AbstractNodeSharedImpl<P, C> copy = copyParameter(parentNode);
        copy.setCreationData(createdBy, new Date());
        if (namePrefix == null || namePrefix.isEmpty()) {
            copy.setName(getName());
        } else {
            copy.setName(namePrefix + getName());
        }

        copy.setVersion(getVersion());
        if (parentNode != null) {
            parentNode.localUpdate();
        }
        return copy;
    }

    @Transient
    @CheckForNull
    public NodeImageDBO getIcon() {
        return _icon;
    }

    public void setIcon(@Nullable final NodeImageDBO icon) {
        _icon = icon;
    }

    /**
     * Copy this node and set Special Parameter.
     *
     * @param parent the parent Node for the Copy.
     * @return a Copy of this node.
     */
    @Nonnull
    protected abstract AbstractNodeSharedImpl<P, C> copyParameter(@Nonnull P parent) throws PersistenceException;

    /**
     * Save his self.
     */
    public void localSave() throws PersistenceException {
        save();
    }

    /**
     * Update date it self and his siblings.
     * @throws PersistenceException
     */
    public void update() throws PersistenceException {
        if (isRootNode()) {
            localUpdate();
            updateChildrenOf(this);
        } else {
            final P parent = getParent();
            if (parent != null) {
                updateChildrenOf(parent);
            }
        }
    }

    /**
     * Update the node an his children.
     * @param node the node to update.
     * @throws PersistenceException
     */
    protected void updateChildrenOf(@Nonnull final NodeDBO node) throws PersistenceException {
        @SuppressWarnings("unchecked")
        final Map<Short, AbstractNodeSharedImpl<AbstractNodeSharedImpl<?, ?>, AbstractNodeSharedImpl<?, ?>>> childrenAsMap = node
                .getChildrenAsMap();
        for (final AbstractNodeSharedImpl<?, ?> n : childrenAsMap.values()) {
            n.localUpdate();
            updateChildrenOf(n);
        }
    }

    /**
     *
     * @return is only true if this Node a Root Node.
     */
    @Transient
    public boolean isRootNode() {
        return getParent() == null || getParent().getClass() == VirtualRoot.class;
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(@CheckForNull final AbstractNodeSharedImpl<P, C> other) {
        if (other == null) {
            return -1;
        }
        if (this.getClass() != other.getClass()) {
            return -1;
        }
        int compare = getId() - other.getId();
        if (compare == 0 && getId() == 0) {
            compare = this.getSortIndex() - other.getSortIndex();
        }
        return compare;
    }


    /**
     * @return Return only true when the node need to work a GSD-File!
     */
    @Nonnull
    public GSDFileTypes needGSDFile() {
        return GSDFileTypes.NONE;
    }

    @Nonnull
    public abstract C createChild() throws PersistenceException;

    public abstract void accept(@Nonnull final INodeVisitor visitor);
}
