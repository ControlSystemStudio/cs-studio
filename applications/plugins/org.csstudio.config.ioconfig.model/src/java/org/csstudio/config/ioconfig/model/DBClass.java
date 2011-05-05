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

import javax.annotation.Nonnull;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.5 $
 * @since 17.10.2008
 */

@MappedSuperclass
public class DBClass {
    /**
     * Key ID.
     */
    private int _id;

    /**
     * The name of the creator of this Node.
     */
    private String _createdBy;

    /**
     * The date who this Node is create.
     */
    private Date _createdOn;

    /**
     * The name of the user that have make the last update of this Node.
     */
    private String _updatedBy;

    /**
     * The date who this Node last update is.
     */
    private Date _updatedOn;

    private boolean _dirty;

    /**
     *
     * @return the Node key ID.
     */
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_G_DDB")
    @SequenceGenerator(name="SEQ_G_DDB", sequenceName="SEQ_DDB")
    public int getId() {
        return _id;
    }

    /**
     *
     * @param id
     *            set the Node key ID.
     */
    public void setId(final int id) {
        this._id = id;
    }

    /**
     *
     * @return the Name of the creator of this Node.
     */
    @Nonnull 
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     *
     * @param createdBy
     *            set the Name of the creator of this Node.
     */
    public void setCreatedBy(@Nonnull final String createdBy) {
        this._createdBy = createdBy;
    }

    /**
     *
     * @return Created-Date of this node.
     */
    @Nonnull 
    public Date getCreatedOn() {
        return _createdOn;
    }

    /**
     *
     * @param createdOn
     *            set Created-Date of this node. Normally only use by create.
     */
    public void setCreatedOn(@Nonnull final Date createdOn) {
        this._createdOn = createdOn;
    }

    /**
     *
     * @return Give the user that have make the last update.
     */
    @Nonnull 
    public String getUpdatedBy() {
        return _updatedBy;
    }

    /**
     *
     * @param updatedBy
     *            set the User that make a update.
     */
    public void setUpdatedBy(@Nonnull final String updatedBy) {
        this._updatedBy = updatedBy;
    }

    /**
     *
     * @return get the date of last update.
     */
    @Nonnull 
    public Date getUpdatedOn() {
        return _updatedOn;
    }

    /**
     * Set the date of last Update.
     *
     * @param updatedOn
     *            The date of last update.
     */
    public void setUpdatedOn(@Nonnull final Date updatedOn) {
        this._updatedOn = updatedOn;
    }

    /**
     * Save or Update the Class to the Repository.
     * @throws PersistenceException wa
     */
    public void save() throws PersistenceException{
        Repository.saveOrUpdate(this);
    }

    /**
     *
     * @return is this node persistent at DB return true.
     */
    @Transient
    public boolean isPersistent() {
        return getId() != 0;
    }

    /**
     * Class have changes that non persisted.
     * @return is this node dirty.
     */
    @Transient
    public boolean isDirty() {
        return _dirty;
    }

    /**
     * Set class have changes that non persisted.
     * @param dirty set the node dirty.
     */
    public void setDirty(final boolean dirty) {
        _dirty = dirty;
    }
}
