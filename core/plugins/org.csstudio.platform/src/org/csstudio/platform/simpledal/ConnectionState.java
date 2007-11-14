package org.csstudio.platform.simpledal;

/**
 * The state of a connection to a PV.
 * 
 * @author C1 WPS / SW
 */
public enum ConnectionState {
	/**
	 * If state is not a valid DAL-state.
	 */
	UNKNOWN(null),

	/**
	 * If connection is valid and connected.
	 */
	CONNECTED(org.epics.css.dal.context.ConnectionState.CONNECTED),

	/**
	 * If the connection get lost in case of any problem.
	 */
	CONNECTION_LOST(org.epics.css.dal.context.ConnectionState.CONNECTION_LOST),

	/**
	 * If the connection to the PV failed or failed in re-connect.
	 */
	CONNECTION_FAILED(
			org.epics.css.dal.context.ConnectionState.CONNECTION_FAILED),

	/**
	 * If connection get disposed / disconnected.
	 */
	DISCONNECTED(org.epics.css.dal.context.ConnectionState.DISCONNECTED);

	private org.epics.css.dal.context.ConnectionState _dalState;

	private ConnectionState(org.epics.css.dal.context.ConnectionState dalState) {
		_dalState = dalState;
	}

	/**
	 * Transfers this state into a DAL-state.
	 * 
	 * @return The DAL-state of this state.
	 */
	public org.epics.css.dal.context.ConnectionState getDalState() {
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
	public static ConnectionState translate(
			org.epics.css.dal.context.ConnectionState dalState) {
		ConnectionState result = UNKNOWN;

		for (ConnectionState s : values()) {
			if (s.getDalState() == dalState) {
				result = s;
			}
		}

		return result;
	}
}
