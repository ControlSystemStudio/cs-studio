/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
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

import org.csstudio.config.ioconfig.model.tools.NodeMap;
import org.hibernate.annotations.Cascade;

/**
 *
 * @author hrickens
 * @since 05.01.2012
 *
 * @param <C> The Parent node types
 * @param <P> The Children node types
 */
@SuppressWarnings("rawtypes")
@Entity
@Table(name = "ddb_node")
@Inheritance(strategy = InheritanceType.JOINED)
public class NodeDBO<P extends NodeDBO, C extends NodeDBO> extends NamedDBClass implements
        Comparable<NodeDBO<P, C>>, IDocumentable, Serializable {
    private static final long serialVersionUID = 1L;
    @Transient
    private P _parent;
    private Set<C> _children = new HashSet<C>();
    private String _description;
    private String _krykNo = "";
    /**
     * The Version of the Node.
     */
    private int _version;
    /**
     * A collection of documents that relate to this node.
     */
    private Set<DocumentDBO> _documents = new HashSet<DocumentDBO>();

    /**
     * Default Constructor needed by Hibernate.
     */
    public NodeDBO() {
        // Do nothing
    }

    @SuppressWarnings("unchecked")
    public NodeDBO(@Nonnull final P parent) throws PersistenceException {
        _parent = parent;
        _parent.addChild(this);
    }

    @SuppressWarnings("unchecked")
    @CheckForNull
    public C addChild(@Nonnull final C child) throws PersistenceException {
        short sortIndex = child.getSortIndex();
        C oldNode = getChildrenAsMap().get(sortIndex);

        if (oldNode != null && oldNode.equals(child)) {
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

    public void setSortIndexNonHibernate(final int i) throws PersistenceException {
        if (getSortIndex() != i) {
            setSortIndex(i);
            if (getSortIndex() >= 0) {
                localUpdate();
            }
        }
    }

    /**
     *  Die Tabellen MIME_FILES und MIME_FILES_DDB_NODE liegen auf einer anderen DB.
     *  Daher wird hier mit einem Link gearbeitet der folgenden Rechte benötigt.
     *  -  Für MIME_FILES ist das Grant: select.
     *  -  Für MIME_FILES_DDB_NODE ist das Grant: select, insert, update, delete.
     *
     *  Beim erstellen sind leider die Columname vertauscht worden. In der Column docs_id stehen
     *  die node_id und in der id stehen die doc_ids.
     * @return Documents for the Node.
     */
    // TODO (hrickens) [17.05.2011]: Spalten namen sind Vertausch.
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinTable(name = "MIME_FILES_DDB_NODES_LINK", joinColumns = @JoinColumn(name = "docs_id", referencedColumnName = "id", unique = true), inverseJoinColumns = @JoinColumn(name = "nodes_id", referencedColumnName = "id"))
    @Nonnull
    @Override
    public Set<DocumentDBO> getDocuments() {
        return _documents;
    }

    /**
     * @param documents set the Documents for this node.
     */
    @Override
    public void setDocuments(@Nonnull final Set<DocumentDBO> documents) {
        _documents = documents;
    }

    /**
     * @param document add the Document to this node.
     * @return this Node.
     */
    @Nonnull
    public void addDocument(@Nonnull final DocumentDBO document) {
        this._documents.add(document);
    }

    @Column(name = "INTERN_ID", nullable = false, length = 20)
    @CheckForNull
    public String getKrykNo() {
        return _krykNo;
    }

    @Transient
    @Nonnull
    public String getKrykNoNH() {
        return _krykNo == null ? "" : _krykNo;
    }

    public void setKrykNo(@Nullable final String krykNo) {
        _krykNo = krykNo;
    }

    @ManyToOne
    @Nonnull
    public P getParent() {
        return _parent;
    }

    @Override
    public void setId(final int id) {
        super.setId(id);
        NodeMap.put(id, this);
    }

    /**
     * @return the Version of this node.
     */
    public int getVersion() {
        return _version;
    }

    /**
     * @param version the Version of this node.
     */
    public void setVersion(final int version) {
        this._version = version;
    }

    @Nonnull
    public String getDescription() {
        return _description;
    }

    public void setDescription(@Nonnull final String description) {
        this._description = description;
    }

    @SuppressWarnings("deprecation")
    @OneToMany(mappedBy = "parent", targetEntity = NodeDBO.class, fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST, CascadeType.MERGE })
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Nonnull
    public Set<C> getChildren() {
        return _children;
    }

    public void setChildren(@Nonnull final Set<C> children) {
        _children = children;
    }

    /**
     * Get the Children of the Node as Map. The Key is the Sort Index.
     * @return the children as map.
     */
    @Transient
    @Nonnull
    public SortedMap<Short, C> getChildrenAsMap() {
        final SortedMap<Short, C> nodeMap = new TreeMap<Short, C>();
        if (hasChildren()) {
            for (final C child : getChildren()) {
                nodeMap.put(child.getSortIndex(), child);
            }
        }
        return nodeMap;
    }

    public void removeChild(@Nonnull final C child) {
        _children.remove(child);
    }

    public void removeAllChild() {
        _children.clear();
    }

    /**
     * @param parent set the Parent of this Node
     */
    public void setParent(@Nonnull final P parent) {
        _parent = parent;
    }

    /**
     * make the data update for his self.
     * @throws PersistenceException
     */
    @SuppressWarnings("unused")
    protected void localUpdate() throws PersistenceException {
        // nothing to update
    }

    /**
     * @return have this Node one or more children then return true else false.
     */
    public final boolean hasChildren() {
        return _children != null && _children.size() > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(@CheckForNull final NodeDBO<P, C> other) {
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
     * Assemble the Epics Address String of the children Channels.
     */
    @Transient
    public void assembleEpicsAddressString() throws PersistenceException {
        final Set<C> children = getChildren();
        for (final C node : children) {
            if (node != null) {
                node.assembleEpicsAddressString();
                if (node.isDirty()) {
                    node.save();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public P delete() throws PersistenceException {
        final P parent = getParent();
        parent.removeChild(this);
        parent.save();
        return parent;
    }
}
