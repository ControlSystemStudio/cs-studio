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
package org.csstudio.config.ioconfig.model.service.internal;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.IDocumentable;
import org.csstudio.config.ioconfig.model.NamedDBClass;

/**
 *
 * @author hrickens
 * @since 05.01.2012
 *
 */
@Entity
@Table(name = "ddb_node")
@Inheritance(strategy = InheritanceType.JOINED)
public class Node4ServicesDBO extends NamedDBClass implements
        Comparable<Node4ServicesDBO>, IDocumentable, Serializable {
    private static final long serialVersionUID = 1L;
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
    public Node4ServicesDBO() {
        // Do nothing
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
    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
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

    @Column(name = "INTERN_ID", nullable = true, length = 20)
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(@CheckForNull final Node4ServicesDBO other) {
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
}
