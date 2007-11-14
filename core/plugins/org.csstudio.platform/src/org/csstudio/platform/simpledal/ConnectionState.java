package org.csstudio.platform.simpledal;

public enum ConnectionState {
	UNKNOWN(null),

	CONNECTED(org.epics.css.dal.context.ConnectionState.CONNECTED),

	CONNECTION_LOST(org.epics.css.dal.context.ConnectionState.CONNECTION_LOST),

	CONNECTION_FAILED(
			org.epics.css.dal.context.ConnectionState.CONNECTION_FAILED),

	DISCONNECTED(org.epics.css.dal.context.ConnectionState.DISCONNECTED);

	private org.epics.css.dal.context.ConnectionState _dalState;

	private ConnectionState(org.epics.css.dal.context.ConnectionState dalState) {
		_dalState = dalState;
	}

	public org.epics.css.dal.context.ConnectionState getDalState() {
		return _dalState;
	}

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
