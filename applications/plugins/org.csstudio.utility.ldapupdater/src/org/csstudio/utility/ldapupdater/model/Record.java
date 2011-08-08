/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.ldapupdater.model;

import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Class type for IOC records.
 *
 * @author bknerr
 */
public class Record implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String _name;
    private final String _description;

    /**
     * Constructor.
     */
    public Record(@Nonnull final String name) {
        this(name, null);
    }
    /**
     * Constructor.
     * @param name .
     * @param desc description
     */
    public Record(@Nonnull final String name,
                  @Nullable final String desc) {
        _name = name;
        _description = desc;
    }

    /**
     * Getter.
     * @return the name
     */
    @Nonnull
    public final String getName() {
        return _name;
    }
    /**
     * Getter.
     * @return the description
     */
    @CheckForNull
    public final String getDescription() {
        return _description;
    }

    /**
     * (@inheritDoc)
     * @return the hash code
     */
    @Override
    public final int hashCode() {
        final int prime = 31;
        final int result = prime + ( _name == null ? 0 : _name.hashCode());
        return result;
    }

    /**
     * (@inheritDoc)
     */
    @Override
    public final boolean equals(@Nullable final Object obj) {
        if (!(obj instanceof Record)) {
            return false;
        }
        final Record other = (Record) obj;
        if (!_name.equals(other._name)) {
            return false;
        }
        if (_description != null &&  _description.equals(other._description)) {
            return true;
        }
        return other._description == null;
    }
}
