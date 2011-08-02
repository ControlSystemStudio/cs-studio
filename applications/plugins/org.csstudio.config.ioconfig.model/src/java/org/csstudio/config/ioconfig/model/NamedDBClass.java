/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
 * AT http://www.desy.de/legal/license.htm
 */
/*
 * $Id: NamedDBClass.java,v 1.4 2010/08/20 13:33:06 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * This class represent a DB Table with Name and SortIndex and extends the {@link DBClass}
 *
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.4 $
 * @since 03.06.2009
 */
@MappedSuperclass
public class NamedDBClass extends DBClass {

    /**
     * The Node Name.
     */
    private String _name;

    /**
     * The Index to sort the node inside his parent.
     */
    private Short _sortIndex = -1;

    /**
     *
     * @param name
     *            set the Name of this Node.
     */
    public void setName(@Nullable final String name) {
        this._name = name;
        Diagnose.addNewLine(_name+"\t"+this.getClass().getSimpleName());
        Diagnose.countNamedDBClass();
    }

    /**
     *
     * @return the Name of this Node.
     */
    @CheckForNull
    public String getName() {
        return _name;
    }

    /**
     *
     * @return the Index to sort the node inside his parent.
     */
    @Nonnull
    public Short getSortIndex() {
        return _sortIndex;
    }

    /**
     *
     * @param sortIndex
     *            set the Index to sort the node inside his parent.
     */
    public void setSortIndex(@Nonnull final Short sortIndex) {
        _sortIndex = sortIndex;
    }
    @Transient
    public void setSortIndex(@Nonnull final Integer sortIndex) {
        setSortIndex(sortIndex.shortValue());
    }

    /**
     * @return The Name of this Node.
     */
    @Override
    @Nonnull
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (getSortIndex() != null) {
            sb.append(getSortIndex());
        }
        if (getName() != null) {
            sb.append(':');
            sb.append(getName());
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getId();
        result = prime * result + ( (_sortIndex == null) ? 0 : _sortIndex.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(@CheckForNull final Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        final NamedDBClass other = (NamedDBClass) obj;
        if(getId() != other.getId()) {
            return false;
        }
        if(_sortIndex == null) {
            return other._sortIndex == null;
        } else { 
            return _sortIndex.equals(other._sortIndex);
        }
    }
}
