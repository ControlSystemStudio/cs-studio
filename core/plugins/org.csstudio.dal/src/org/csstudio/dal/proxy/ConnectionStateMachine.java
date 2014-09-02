/**
 * 
 */
package org.csstudio.dal.proxy;

import java.util.EnumSet;

import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.DynamicValueState;
import org.csstudio.dal.context.ConnectionState;

/**
 * This is state machine pattern implementation which helps plug implementator 
 * to properly track of changes of connection state   
 * @author ikriznar
 *
 */
public class ConnectionStateMachine {

	/*
	 * matrix of allowed state transitions
	 */
	private static boolean[][] transitions=
	{
						/* to  INITIA READY  CNTING CNCTED OPERAL CNLOST CNFAIL DSCING DSCTED DSTRED*/
/*   from INITIAL*/			  {false, true,  true,  false, false, false, false, false, false, true},
		/*READY*/			  {true,  false, true,  false, false, false, false, false, false, true},
		/*CONNECTING*/		  {false, false, false, true,  false, false, true,  false, false, true},
		/*CONNECTED*/		  {false, false, false, false, true,  true,  false, true,  false, false},
		/*OPERATIONAL*/		  {false, false, false, false, false, true,  false, true,  false, false},
		/*CONNECTION_LOST*/	  {false, false, false, true,  true,  false, false, true,  false, false},
		/*CONNECTION_FAILED*/ {true,  false, false, true,  true,  false, false, true,  false, true},
		/*DISCONNECTING*/ 	  {false, false, false, false, false, false, false, false, true,  false},
		/*DISCONNECTED*/ 	  {true,  true,  true,  false, false, false, false, false, false, true}, 
		/*DESTROYED*/		  {false, false, false, false, false, false, false, false, false, false}
	};

	/**
	 * Returns <code>true</code> if transition from state1 to state2 is allowed.
	 * @param state1 initial state
	 * @param state2 target state
	 * @return <code>true</code> if transition from state1 to state2 is allowed
	 */
	public static final boolean isTransitionAllowed(ConnectionState state1, ConnectionState state2) {
		return transitions[state1.ordinal()][state2.ordinal()];
	}

	private ConnectionState connectionState= ConnectionState.INITIAL;
	
	/**
	 * Returns current connection state.
	 * @return
	 */
	public ConnectionState getConnectionState() {
		return connectionState;
	}
	
	public boolean isTransitionAllowed(ConnectionState state) {
		return transitions[connectionState.ordinal()][state.ordinal()];
	}

	/**
	 * Requests from this state machine to change connection state to provided parameter.
	 * If request was illegal regarding the current state, it will throw exception.
	 * If sconnection wtate was changes, <code>true</code> is returned. 
	 * @param state requested new state
	 * @return <code>true</code> if state was accepted and changed
	 * @throws IllegalStateException if request is illegal
	 */
	public synchronized boolean requestNextConnectionState(ConnectionState state) throws IllegalStateException {
		
		if (connectionState==state) {
			return false;
		}
		
		if (isTransitionAllowed(state)) {
			connectionState=state;
			return true;
		} else {
			// FIXME: Temporary workaround, let's catch first all problems, then become nasty 
			IllegalStateException e= new IllegalStateException("State transition from current "+connectionState.toString()+" to "+state.toString()+" is not allowed.");
			e.printStackTrace();

			connectionState=state;
			return true;
		}
	}
	
	/**
	 * Move state one state closer toward connected state. If this called changed internal state, then <code>true</code> is returned.
	 * @return <code>true</code> if this operation changed current state of machine
	 */
	public synchronized boolean moveTowardConnected() {
		if (connectionState.ordinal()>=ConnectionState.CONNECTED.ordinal()) {
			return false;
		}
		
		ConnectionState con= ConnectionState.values()[connectionState.ordinal()+1];
		if (isTransitionAllowed(con)) {
			connectionState=con;
			return true;
		}
		
		return false;
	}

	/**
	 * Return true if connection was successfully established. Actual connection 
	 * state might be CONNECTED, CONNECTION_LOST or OPERATIONAL.
	 * @return if connection process has been successfully completed.
	 */
	public boolean isConnected() {
		return connectionState.isConnected();
	}

	public boolean isOperational() {
		return connectionState == ConnectionState.OPERATIONAL;
	}

	public boolean isDestroyed() {
		return connectionState == ConnectionState.DESTROYED;
	}

	public boolean isConnectionAlive() {
		return connectionState.isConnectionAlive();
	}

	public boolean isConnectionFailed() {
		return connectionState == ConnectionState.CONNECTION_FAILED;
	}

	public boolean isConnecting() {
		return connectionState == ConnectionState.CONNECTING;	
	}
	
	/**
	 * Returns updated condition which has states that corresponds to connection state. 
	 * If no change is necessary, then returned condition is same as provided.  
	 * @param condition the condition to be copied and updated
	 * @return updated condition or same condition, if no update necessary
	 */
	public DynamicValueCondition deriveUpdatedCondition(DynamicValueCondition condition) {
		EnumSet<DynamicValueState> set = EnumSet.copyOf(condition.getStates());
		
		ConnectionState s= connectionState;
		
		boolean change= false;
		if (s==ConnectionState.CONNECTED) {
			change |= set.remove(DynamicValueState.LINK_NOT_AVAILABLE);
			change |= set.remove(DynamicValueState.ERROR);
			change |= set.add(DynamicValueState.NORMAL);
		} else if (s==ConnectionState.DISCONNECTED) {
			change |= set.add(DynamicValueState.LINK_NOT_AVAILABLE);
		} else if (s==ConnectionState.DESTROYED) {
			change |= set.add(DynamicValueState.LINK_NOT_AVAILABLE);
		} else if (s==ConnectionState.CONNECTION_FAILED) {
			change |= set.add(DynamicValueState.LINK_NOT_AVAILABLE);
			change |= set.add(DynamicValueState.ERROR);
		} else if (s==ConnectionState.CONNECTION_LOST) {
			change |= set.add(DynamicValueState.LINK_NOT_AVAILABLE);
		}
		
		if (change) {
			return new DynamicValueCondition(set,null,DynamicValueCondition.CONNECTION_STATE_UPDATE_MESSAGE);
		} else {
			return condition;
		}
	}
	
	/**
	 * Checks state condition set and sets operation connection state if possible. 
	 * @param set to be tested for operation
	 * @return <code>true</code> if operation changed state machine
	 */
	public boolean requestOperationalState(EnumSet<DynamicValueState> set) {
		if (connectionState == ConnectionState.OPERATIONAL 
				|| set.contains(DynamicValueState.LINK_NOT_AVAILABLE)) {
			return false;
		}
		
		if (isTransitionAllowed(ConnectionState.OPERATIONAL) 
				&& set.contains(DynamicValueState.HAS_LIVE_DATA)
				&& set.contains(DynamicValueState.HAS_METADATA)) {
			
			requestNextConnectionState(ConnectionState.OPERATIONAL);
			return true;
		}
		
		return false;
		
	}
	
	@Override
	public String toString() {
		return this.getClass().getName()+':'+connectionState.name();
	}
}
