/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

package org.csstudio.dal;

import java.util.EnumSet;


/**
 * Enumeration <code>DynamicValue</code> describes available states in which
 * dynamic value property might find itself. This state does not described
 * connection management status since this is done by ConnectionState.
 * By definition DynamicValueState
 * objects define data property from point when property is connected to remote
 * object till the point this connection is destroyed on local system. Dynamic
 * value property might be described at given moment with set of different
 * states. Interpretation of states is left to the particular implementation.
 * Also implementation migth find some states compatible and some not with
 * eachother.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public enum DynamicValueState {
    /**
     * No value received from remote object up to this point.
     */
    NO_VALUE,
    /**
     * Normal state, no errors or alarms, connection to remote object functions
     * without problems.
     */
    NORMAL,
    /**
     * General warning, dynamic value my not be reliable. Corresponds to MINOR_ALARM severity in EPICS.
     */
    WARNING,
    /**
     * More serious than warning, dynamic value my not be reliable. Corresponds to MAJOR_ALARM severity in EPICS.
     */
    ALARM,
    /**
     * Error occured on remote object, dynamic value my not be reliable. Corresponds to INVALID_ALARM severity in EPICS.
     */
    ERROR,
    /**
     * Dynamic value updated are not ariving for longer than timeout period.
     */
    TIMEOUT,
    /**
     * Dynamic value updates are arriving but with delay larger than timelag
     * period.
     */
    TIMELAG,
    /**
     * Remote object is not available, value is not reliable, timeout my occur as
     * well.
     */
    LINK_NOT_AVAILABLE,
    /**
     * Dynamic value is has initialized basic set of meta-data characteristic. This means that
     * call for meta-data characteristic will return values.
     */
    HAS_METADATA,
    /**
     * Dynamic value has received at least one live update. This means for example that proxy returns
     * non-null value for latestReceivedValueResponse.
     */
    HAS_LIVE_DATA;


    /**
     * Return <code>true</code> if both sets has same states.
     * @param set1 first set
     * @param set2 second set
     * @return <code>true</code> if both sets has same states
     */
    public static final boolean areSetsEqual(final EnumSet<DynamicValueState> set1, final EnumSet<DynamicValueState> set2)
    {
        if (set1 == null && set2 == null) {
            return true;
        }
        if (set1 == null && set2 !=null
                || set1 != null && set2 ==null) {
            return false;
        }

        if (set1.size()!=set2.size()) {
            return false;
        }

        // if sizes are equal, than all in A must be in B.
        for (DynamicValueState dvs : set2) {
            if (!set1.contains(dvs)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns <code>true</code> if provided state set contains at least one of provided states.
     *
     * @param set set to be tested against
     * @param states the states to be checked for inclusion
     * @return <code>true</code> if at least one of provided states is inside this condition
     */
    public static final boolean containsAnyOfStates(final EnumSet<DynamicValueState> set, final DynamicValueState... states)
    {
        for (DynamicValueState state : states) {
            if (set.contains(state)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns <code>true</code> if provided state set contains at least one of provided states.
     *
     * @param states1 set to be tested against
     * @param states2 the states to be checked for inclusion
     * @return <code>true</code> if at least one of provided states is inside this condition
     */
    public static final boolean containsAnyOfStates(final EnumSet<DynamicValueState> states1, final EnumSet<DynamicValueState> states2)
    {
        for (DynamicValueState state : states2) {
            if (states1.contains(state)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns <code>true</code> only of all states are inside provided state set.
     * @param set the set to be tested against
     * @param states the states to be checked for inclusion
     * @return <code>true</code> only of all states are inside this condition
     */
    public static final boolean containsAllStates(
            final EnumSet<DynamicValueState> set,
            final DynamicValueState... states)
    {
        for (DynamicValueState st: states) {
            if (!set.contains(st)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns <code>true</code> only of all states are inside provided state set.
     * @param states1 the set to be tested against
     * @param states2 the states to be checked for inclusion
     * @return <code>true</code> only of all states are inside this condition
     */
    public static final boolean containsAllStates(EnumSet<DynamicValueState> states1,
            EnumSet<DynamicValueState> states2)
    {
        for (DynamicValueState st: states2) {
            if (!states1.contains(st)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns set, which is copy of provided state only without requested states.
     * @param set the set to be copied
     * @param states the states to be excluded in copy
     * @return set, which is copy of provided state only without requested states
     */
    public static final EnumSet<DynamicValueState> deriveSetWithoutStates(
            final EnumSet<DynamicValueState> set,
            final DynamicValueState... states)
    {
        EnumSet<DynamicValueState> s = EnumSet.copyOf(set);

        for (DynamicValueState state : states) {
            s.remove(state);
        }

        return s;
    }

    /**
     * Returns set, which is copy of provided state only with included requested states.
     * @param set the set to be copied
     * @param states the states to be included in copy
     * @return set, which is copy of provided state only with included requested states
     */
    public static final EnumSet<DynamicValueState> deriveSetWithStates(
        final EnumSet<DynamicValueState> set,
        final DynamicValueState... states)
    {
        EnumSet<DynamicValueState> s = EnumSet.copyOf(set);

        for (DynamicValueState state : states) {
            s.add(state);
        }

        return s;
    }

}
/* __oOo__ */
