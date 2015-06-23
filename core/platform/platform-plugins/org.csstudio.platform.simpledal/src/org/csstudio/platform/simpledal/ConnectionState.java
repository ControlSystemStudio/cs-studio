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
package org.csstudio.platform.simpledal;

/**
 * The state of a connection to a PV.
 *
 * @author Sven Wende
 */
@Deprecated
public enum ConnectionState {
    /**
     * If state is not a valid DAL-state.
     * Used as initial connection state.
     */
    UNKNOWN(null),

    INITIAL(org.csstudio.dal.context.ConnectionState.INITIAL),

    /**
     * If connection is valid and connected.
     */
    CONNECTED(org.csstudio.dal.context.ConnectionState.CONNECTED),

    /**
     * If the connection get lost in case of any problem.
     */
    CONNECTION_LOST(org.csstudio.dal.context.ConnectionState.CONNECTION_LOST),

    /**
     * If the connection to the PV failed or failed in re-connect.
     */
    CONNECTION_FAILED(org.csstudio.dal.context.ConnectionState.CONNECTION_FAILED),

    /**
     * If connection get disposed / disconnected.
     */
    DISCONNECTED(org.csstudio.dal.context.ConnectionState.DISCONNECTED);

    private org.csstudio.dal.context.ConnectionState _dalState;

    /**
     * Constructor.
     * @param dalState
     */
    private ConnectionState(org.csstudio.dal.context.ConnectionState dalState) {
        _dalState = dalState;
    }

    /**
     * Transfers this state into a DAL-state.
     *
     * @return The DAL-state of this state.
     */
    public org.csstudio.dal.context.ConnectionState getDalState() {
        return _dalState;
    }

    /**
     * Translates a DAL-state to a matching value of this state-type.
     *
     * @param dalState
     *            The DAL-state to be translated.
     * @return The matching state of this type, {@link ConnectionState.UNKNOWN}
     *         if not avail.
     */
    public static ConnectionState translate(org.csstudio.dal.context.ConnectionState dalState) {

        ConnectionState result = UNKNOWN;

        for (ConnectionState s : values()) {
            if(s.getDalState() == dalState) {
                result = s;
            }
        }

        // TODO:
        // If the incomming state is org.csstudio.dal.context.ConnectionState.OPERATIONAL,
        // the result value is always UNKNOWN
        // because the old ConnectionState enum does not contain such a state.
        // This causes always an error in the application DepartmentDecision (NAMS)
        if ((result == UNKNOWN) && (dalState == org.csstudio.dal.context.ConnectionState.OPERATIONAL)) {
            result = ConnectionState.CONNECTED;
        }

        return result;
    }
}
