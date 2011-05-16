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

import java.awt.Image;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.auth.security.User;
import org.csstudio.config.ioconfig.model.tools.NodeMap;
import org.hibernate.annotations.Cascade;

/**
*
* @author gerke
* @author $Author: hrickens $
* @version $Revision: 1.4 $
* @since 21.03.2007
* @param <C> The Parent node types
* @param <P> The Children node types 
* 
*/
@SuppressWarnings("rawtypes")
@Entity
@Table(name = "ddb_node")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractNodeDBO<P extends AbstractNodeDBO, C extends AbstractNodeDBO> extends
        NamedDBClass implements Comparable<AbstractNodeDBO<P,C>>, IDocumentable, INode, Serializable {
    
    private static final long serialVersionUID = 1L;

    protected static final int DEFAULT_MAX_STATION_ADDRESS = 255;
    
    /**
     * The highest accept station address.
     */
    @Transient
    public static final int MAX_STATION_ADDRESS = 128;
    
    /**
     * The Node Patent.
     */
    private P _parent;
    
    /**
     * The Version of the Node.
     */
    private int _version;
    
    private Set<C> _children = new HashSet<C>();
    
    /**
     * A collection of documents that relate to this node.
     */
    private Set<DocumentDBO> _documents = new HashSet<DocumentDBO>();
    
    private String _description;
    
    private NodeImageDBO _icon;
    
    /**
     * Default Constructor needed by Hibernate.
     */
    public AbstractNodeDBO() {
        // Do nothing
    }
    
    /**
     *
     * @param parent
     *            set the Parent of this Node
     */
    public void setParent(@Nonnull final P parent) {
        _parent = parent;
    }
    
    /**
     *
     * @return The parent of this Node.
     */
    @Override
    @ManyToOne
    @Nonnull
    public P getParent() {
        return _parent;
    }
    
    /**
     *
     * @param id
     *            set the Node key ID.
     */
    @Override
    public void setId(final int id) {
        super.setId(id);
        NodeMap.put(id, this);
    }
    
    /**
     *
     * @return the Children of this node.
     */
    @OneToMany(mappedBy = "parent", targetEntity = AbstractNodeDBO.class, fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE})
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Nonnull
    public Set<C> getChildren() {
        return _children;
    }
    
    /**
     * Set the Children to this node.
     *
     * @param children
     *            The Children for this node.
     */
    public void setChildren(@Nonnull final Set<C> children) {
        _children = children;
    }
    
    /**
     * Add the Child to this node.
     *
     * @param <T> The Type of the Children.
     * @param child the Children to add.
     * @return null or the old Node for the SortIndex Position.
     * @throws PersistenceException 
     */
    @SuppressWarnings("unchecked")
    @CheckForNull
    public C addChild(@Nonnull final C child) throws PersistenceException {
        short sortIndex = child.getSortIndex();
        C oldNode = getChildrenAsMap().get(sortIndex);
        
        if(oldNode != null && oldNode.equals(child)) {
            return null;
        }
        child.setParent(this);
        child.setSortIndexNonHibernate(sortIndex);
        _children.add(child);
        
        while (oldNode != null) {
            final C node = oldNode;
            sortIndex++;
            oldNode = getChildrenAsMap().get(sortIndex);
            node.setSortIndexNonHibernate(sortIndex);
        }
        return oldNode;
    }
    
    /**
     * Clear all children of this node.
     */
    protected void clearChildren() {
        _children.clear();
    }
    
    /**
     * Remove a children from this Node.
     *
     * @param child
     *            the children that remove.
     */
    public void removeChild(@Nonnull final C child) {
        _children.remove(child);
    }
    
    /**
     * Remove a children from this Node.
     */
    public void removeAllChild() {
        clearChildren();
    }
    
    /**
     * Get the Children of the Node as Map. The Key is the Sort Index.
     * @return the children as map.
     * @throws PersistenceException 
     */
    @Transient
    @Nonnull
    public Map<Short, C> getChildrenAsMap() throws PersistenceException {
        final Map<Short, C> nodeMap = new TreeMap<Short, C>();
        if(hasChildren()) {
            for (final C child : getChildren()) {
                nodeMap.put(child.getSortIndex(), child);
            }
        }
        return nodeMap;
    }
    
    /**
     *
     * @param maxStationAddress
     *            the maximum Station Address.
     * @return the first free Station Address.
     * @throws PersistenceException 
     */
    @Transient
    public short getfirstFreeStationAddress(final int maxStationAddress) throws PersistenceException {
        final Map<Short, C> children = getChildrenAsMap();
        Short nextKey = 0;
        if(!children.containsKey(nextKey)) {
            return nextKey;
        }
        final Set<Short> descendingKeySet = children.keySet();
        for (final Short key : descendingKeySet) {
            if(key - nextKey > 1) {
                return (short) (nextKey + 1);
            }
            if(key >= 0) {
                nextKey = key;
            }
        }
        return (short) (nextKey + 1);
    }
    
    /**
     *
     * @return have this Node one or more children then return true else false.
     * @throws PersistenceException 
     */
    public final boolean hasChildren() throws PersistenceException {
        try {
            return (_children != null) && (_children.size() > 0);
        } catch (Exception e) {
            PersistenceException persistenceException = new PersistenceException(e);
            throw persistenceException;
        }
    }
    
    /**
     *  Die Tabellen MIME_FILES und MIME_FILES_DDB_NODE liegen auf einer anderen DB.
     *  Daher wird hier mit einem Link gearbeitet der folgenden Rechte benötigt.
     *  -  Für MIME_FILES ist das Grand: select.
     *  -  Für MIME_FILES_DDB_NODE ist das Grand: select, insert, update, delete.
     *
     * @return Documents for the Node.
     */
    @Override
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "MIME_FILES_DDB_NODES_LINK", joinColumns = @JoinColumn(name = "docs_id", referencedColumnName = "id", unique = true), inverseJoinColumns = @JoinColumn(name = "nodes_id", referencedColumnName = "id"))
    //    @JoinTable(name = "MIME_FILES_DDB_NODES_LINK_TEST", joinColumns = @JoinColumn(name = "docs_id", referencedColumnName = "id", unique = true), inverseJoinColumns = @JoinColumn(name = "nodes_id", referencedColumnName = "id"))
    @Nonnull
    public Set<DocumentDBO> getDocuments() {
        return _documents;
    }
    
    /**
     *
     * @param documents set the Documents for this node.
     */
    @Override
    public void setDocuments(@Nonnull final Set<DocumentDBO> documents) {
        _documents = documents;
    }
    
    /**
     *
     * @param document add the Document to this node.
     * @return this Node.
     */
    @Nonnull
    public void addDocument(@Nonnull final DocumentDBO document) {
        this._documents.add(document);
//        return this;
    }
    
    /**
     *
     * @return the Version of this node.
     */
    public int getVersion() {
        return _version;
    }
    
    /**
     *
     * @param version
     *            the Version of this node.
     */
    public void setVersion(final int version) {
        this._version = version;
    }
    
    /**
     *
     * @param i
     *            set the Index to sort the node inside his parent.
     * @throws PersistenceException 
     */
    public void setSortIndexNonHibernate(final int i) throws PersistenceException {
        if(getSortIndex() != i) {
            setSortIndex(i);
            if(getSortIndex() >= 0) {
                localUpdate();
            }
        }
    }
    
    /**
     *
     * @return the Description of the Node.
     */
    @Nonnull
    public String getDescription() {
        return _description;
    }
    
    /**
     *
     * @param description set the Description for this node.
     */
    public void setDescription(@Nonnull final String description) {
        this._description = description;
    }
    
    /**
     * Swap the SortIndex of two nodes. Is the given SortIndex in use the other node became the old
     * SortIndex of this node.
     *
     * @param toIdx
     *            the new sortIndex for this node.
     * @throws PersistenceException 
     */
    public void moveSortIndex(final int toIndex) throws PersistenceException {
        short index = this.getSortIndex();
        short toIdx = (short) toIndex;
        if(toIdx == index) {
            return;
        }
        if(getParent() == null) {
            setSortIndexNonHibernate(toIdx);
            return;
        }
        if(index == -1) {
            putNewNode(index, toIdx);
        } else {
            moveNode(index, toIdx);
        }
    }
    
    /**
     * @param direction
     * @param index
     * @param toIdx
     * @throws PersistenceException
     */
    private void moveNode(short index, short toIdx) throws PersistenceException {
        short direction = 1;
        // Move a exist Node
        int start = index;
        P parent = getParent();
        if(parent != null) {
            @SuppressWarnings("unchecked")
            Map<Short, AbstractNodeDBO<AbstractNodeDBO<?,?>, AbstractNodeDBO<?,?>>> childrenAsMap = parent
                    .getChildrenAsMap();
            AbstractNodeDBO<AbstractNodeDBO<?, ?>, AbstractNodeDBO<?, ?>> moveNode = childrenAsMap.get(index);
            if(index > toIdx) {
                direction = -1;
            }
            for (; start != toIdx; start += direction) {
                final AbstractNodeDBO<AbstractNodeDBO<?, ?>, AbstractNodeDBO<?, ?>> nextNode = childrenAsMap.get((short) (start + direction));
                if(nextNode != null) {
                    nextNode.setSortIndexNonHibernate(start);
                }
            }
            moveNode.setSortIndexNonHibernate(toIdx);
        }
    }
    
    private void putNewNode(final short index, final short toIndex) throws PersistenceException {
        short direction = 1;
        short idx = index;
        // Put a new Node in.
        if(idx > toIndex) {
            direction = -1;
        }
        AbstractNodeDBO<P, C> node = this;
        idx = toIndex;
        P parent = getParent();
        if(parent != null) {
            @SuppressWarnings("unchecked")
            Map<Short, ? extends AbstractNodeDBO<P,C>> childrenAsMap = parent.getChildrenAsMap();
            do {
                final AbstractNodeDBO<P, C> nextNode = childrenAsMap.get(idx);
                node.setSortIndexNonHibernate(idx);
                node = nextNode;
                idx = (short) (idx + direction);
            } while (node != null);
        }
    }
    
    @Deprecated
    public void setImage(@Nonnull final Image image) {
        //        if (image != null) {
        // setImageBytes(image.getImageData().data);
        //        }
    }
    
    //    @Transient
    //    public final HashSet<Node> getChangeNodeSet() {
    //        return _changeNodeSet;
    //    }
    
    /**
     * Copy this node to the given Parent Node.
     *
     * @param parentNode
     *            the target parent node.
     * @return the copy of this node.
     * @throws PersistenceException 
     */
    @Nonnull
    public AbstractNodeDBO<P,C> copyThisTo(@Nonnull final P parentNode) throws PersistenceException {
        String createdBy = "Unknown";
        try {
            final User user = SecurityFacade.getInstance().getCurrentUser();
            if(user != null) {
                createdBy = user.getUsername();
            }
        } catch (final NullPointerException e) {
            createdBy = "Unknown";
        }
        final AbstractNodeDBO<P,C> copy = copyParameter(parentNode);
        copy.setCreatedBy(createdBy);
        copy.setUpdatedBy(createdBy);
        copy.setCreatedOn(new Date());
        copy.setUpdatedOn(new Date());
        //TODO: so umbauen das "Copy of" als prefix parameter übergeben wird.
        copy.setName("Copy of " + getName());
        //        copy.setName(getName());
        copy.setVersion(getVersion());
        if(parentNode != null) {
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
     *
     * @return a Copy of this node.
     * @throws PersistenceException 
     */
    @Nonnull
    protected abstract AbstractNodeDBO<P,C> copyParameter(@Nonnull P parent) throws PersistenceException;
    
    /**
     * Save his self.
     * @throws PersistenceException
     */
    public void localSave() throws PersistenceException {
        save();
    }
    
    /**
     * make the data update for his self.
     * @throws PersistenceException 
     */
    protected void localUpdate() throws PersistenceException {
        // nothing to update
    }
    
    /**
     * Update date it self and his siblings.
     * @throws PersistenceException 
     */
    public void update() throws PersistenceException {
        if(isRootNode()) {
            localUpdate();
            updateChildrenOf(this);
        } else {
            P parent = getParent();
            if(parent != null) {
                updateChildrenOf(parent);
            }
        }
    }
    
    /**
     * Update the node an his children.
     * @param node the node to update.
     * @throws PersistenceException 
     */
    protected void updateChildrenOf(@Nonnull final AbstractNodeDBO node) throws PersistenceException {
        @SuppressWarnings("unchecked")
        Map<Short, AbstractNodeDBO<AbstractNodeDBO<?,?>, AbstractNodeDBO<?,?>>> childrenAsMap = node.getChildrenAsMap();
        for (final AbstractNodeDBO<?,?> n : childrenAsMap.values()) {
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
        return getParent() == null;
    }
    
    /**
     * Assemble the Epics Address String of the children Channels.
     */
    @Transient
    public void assembleEpicsAddressString() throws PersistenceException {
        for (final C node : getChildren()) {
            if(node != null) {
                node.assembleEpicsAddressString();
                if(node.isDirty()) {
                    node.save();
                }
            }
        }
    }
    
    /**
     * {@link Comparable}.
     *
     * @param other
     *            the node to compare whit this node.
     * @return if this node equals whit the give node return 0.
     */
    @Override
    public int compareTo(@CheckForNull final AbstractNodeDBO<P,C> other) {
        if(other == null) {
            return -1;
        }
        
        if(this.getClass() != other.getClass()) {
            return -1;
        }
        int compare = getId() - other.getId();
        if(compare == 0 && getId() == 0) {
            compare = this.getSortIndex() - other.getSortIndex();
        }
        return compare;
    }
    
    /**
     * (@inheritDoc)
     */
    @Override
    public boolean equals(@CheckForNull final Object obj) {
        // TODO (hrickens) : check whether this method does what is intended - do we need hashcode as well?
        if(super.equals(obj)) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(this.getClass() != obj.getClass()) {
            return false;
        }
        
        if(obj instanceof AbstractNodeDBO) {
            
            final AbstractNodeDBO<?,?> other = (AbstractNodeDBO<?,?>) obj;
            if(getId() == other.getId()) {
                if(getId() > 0) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }
    
    /**
     * @return Return only true when the node need to work a GSD-File!
     */
    @Nonnull
    public GSDFileTypes needGSDFile() {
        return GSDFileTypes.NONE;
    }
}
