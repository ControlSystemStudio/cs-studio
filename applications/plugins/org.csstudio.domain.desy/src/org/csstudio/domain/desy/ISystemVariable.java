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
package org.csstudio.domain.desy;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.domain.desy.alarm.IAlarm;
import org.csstudio.domain.desy.common.id.Identifiable;

/**
 * System variables are the fundamental atomic components of any system.
 * A system variable is a value or state (set of values/states) that describes the composed system.
 * It is identifiable and features to any given time a unique value/state or a set of the same that
 * is unique to that time.
 *
 * The state/value may represent an alarm according to some rules/ranges.
 * In other words any system variable gives information about its current alarm state
 * and <code>null</code> when its state is not an alarm.
 *
 * @author bknerr
 * @since 04.11.2010
 */
public interface ISystemVariable<T> extends Identifiable<SystemVariableId> {

    /**
     * The value/state or set of values/states of this system variable.
     * @return the variable
     */
    @Nonnull
    T getValue();

    /**
     * The state/value may represent an alarm according to some rules/ranges.
     * In other words any system variable gives information about its current alarm state.
     *
     * Whether a control system considers an OK or UNKNOWN state as alarm or not, is up to the
     * implementation, hence <code>null</code> represents a possible return value.
     * @return the alarm or <code>null</code>
     */
    @CheckForNull
    IAlarm getAlarm();
}
