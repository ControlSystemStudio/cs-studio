/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.time.IHasTimeStamp;

/**
 * System variables are the fundamental atomic components of any system.
 * A system variables is an entity in form of a value or state (set of values/states) to a given
 * time instant.
 * A complete system is composed of a non-empty set of system variables.
 *
 * It is identifiable and features to any given time a unique value/state or a set of the same that
 * is unique to that time.
 *
 * FIXME (bknerr) : conflicting with the ICssValueType and its derived types - get rid of them, use this one
 *
 * @author bknerr
 * @since 04.11.2010
 *
 * @param <T> the type of the system variable
 */
public interface ISystemVariable<T> extends IHasTimeStamp, Serializable {

    /**
     * The descriptive (and usually but not necessarily) unique name for this variable in the
     * context of its origin ({@link ControlSystem}.
     * @return the name
     */
    @Nonnull
    String getName();

    /**
     * The datum entity (value(s) and/or state(s) of this system variable.
     * @return the variable
     */
    @Nonnull
    T getData();

    /**
     * The control system in whose context this variable exists.
     * @return the control system
     */
    @Nonnull
    ControlSystem getOrigin();

}

