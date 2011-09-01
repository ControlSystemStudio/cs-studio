/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.domain.desy.system;

import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.MapMaker;

/**
 * An identifiable control system of a distinct type.
 *
 * @author bknerr
 * @since 09.02.2011
 */
public final class ControlSystem implements Serializable {

    public static final ControlSystem EPICS_DEFAULT =
        new ControlSystem("EpicsDefault",
                          ControlSystemType.EPICS_V3);
    public static final ControlSystem DOOCS_DEFAULT =
        new ControlSystem("DoocsDefault",
                          ControlSystemType.DOOCS);
    public static final ControlSystem TANGO_DEFAULT =
        new ControlSystem("TangoDefault",
                          ControlSystemType.TANGO);

    private static final long serialVersionUID = 3883445164802010609L;

    /**
     * There will be few control systems, but many entities referring to them.
     * Use flyweight pattern.
     */
    private static final ConcurrentMap<String, ControlSystem> CS_CACHE =
        new MapMaker().initialCapacity(3).softValues().makeMap();
    static {
        CS_CACHE.put(EPICS_DEFAULT.getId(), EPICS_DEFAULT);
        CS_CACHE.put(DOOCS_DEFAULT.getId(), DOOCS_DEFAULT);
        CS_CACHE.put(TANGO_DEFAULT.getId(), TANGO_DEFAULT);
    }

    private final String _id;
    private final ControlSystemType _type;

    /**
     * Constructor.
     */
    private ControlSystem(@Nonnull final String id,
                          @Nonnull final ControlSystemType type) {

        _id = id;
        _type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( _id == null ? 0 : _id.hashCode());
        result = prime * result + ( _type == null ? 0 : _type.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (!(obj instanceof ControlSystem)) { // implicit obj==null check in instanceof
            return false;
        }
        final ControlSystem other = (ControlSystem) obj;
        if (!_id.equals(other._id) || !_type.equals(other._type)) {
                return false;
        }
        return true;
    }

    @Nonnull
    public String getId() {
        return _id;
    }

    @Nonnull
    public ControlSystemType getType() {
        return _type;
    }

    /**
     * Returns the control system with the given parameters.
     *
     * @param id
     * @param type
     * @return the cached instance of the control system (flyweight pattern)
     * @throws IllegalArgumentException if the if points to an existing control system with differing type
     */
    @Nonnull
    public static ControlSystem valueOf(@Nonnull final String id,
                                        @Nonnull final ControlSystemType type) {
        if(CS_CACHE.containsKey(id)) {
            final ControlSystem cs = CS_CACHE.get(id);
            if (cs == null) {
                throw new IllegalArgumentException("Control system with id " + id +
                " doesn't exist.");
            }
            if (!cs.getType().equals(type)) {
                throw new IllegalArgumentException("Control system with id " + id +
                                                   " exists already with different type.");
            }
            return cs;
        }
        return CS_CACHE.put(id, new ControlSystem(id, type));
    }


}
