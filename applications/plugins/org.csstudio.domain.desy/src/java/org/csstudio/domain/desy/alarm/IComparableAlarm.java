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
package org.csstudio.domain.desy.alarm;

import javax.annotation.Nonnull;

/**
 * An alarm which alarm types are comparable, i.e. they have a consistent order.
 *
 * Note, if the implementing class is an enum, then the comparability comes for free with the
 * order of declaration! So don't mess with order of declaration if you use an enum.
 *
 * @author bknerr
 * @since 08.11.2010
 */
public interface IComparableAlarm<T> extends IAlarm {

    /**
     * Compares two types whether they can be ordered (for alarm see following explanation).
     *
     * Why not use interface Comparable<T>?:
     * As many alarms may end up to be enums or composites of enums, they will already have a compareTo
     * method inherited from their enum type. But such a native enum method yields the 'natural' ordering, i.e.
     * their order of declaration in the enum class.
     * This method cannot be overridden as it has been defined final in the enum.
     *
     * This situation is known to be a problem, because it is easily possible to either abstract alarm
     * states, which are different, but would feature equal 'severity', this wouldn't work with
     * the 'natural ordering', apparently), or secondly to mess up the declaration order in an alarm
     * enum by intent, because the enum types might be much easier to read and maintain in a different
     * order, or, not to the least, simply by mistake on adding, removing, renaming types.
     *
     * @param other the other (alarm) type
     * @return -1 if this < other, 0 on equality, 1 on this > other
     */
    public int compareAlarmTo(@Nonnull final T other);
}
