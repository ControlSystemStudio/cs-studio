/**
 * 
 */
package org.epics.css.dal.impl.test;

import junit.framework.Assert;

import org.epics.css.dal.context.ConnectionEvent;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.context.LinkListener;

class LinkListenerImpl implements LinkListener
{
	/**
	 * 
	 */
	private final Assert assertProxy;

	/**
	 * @param proxyReleaseTest
	 */
	LinkListenerImpl(Assert assertProxy) {
		this.assertProxy = assertProxy;
	}

	private ConnectionState[] expConnState = new ConnectionState[0];
	private int stateIdx= 0;
	private boolean listenerActive = true;
	private boolean suspended = false;
	private boolean responseReceived = false;

	public void setListenerActive(boolean active)
	{
		listenerActive = active;
	}

	public void setExpectedConnectionState(ConnectionState... state)
	{
		expConnState = state;
		responseReceived = false;
		stateIdx=0;
	}

	public void setSuspended(boolean suspended)
	{
		this.suspended = suspended;
	}

	public boolean isResponseReceived()
	{
		return responseReceived;
	}
	
	private synchronized void evaluateConnectionState(ConnectionEvent e)
	{
		responseReceived = true;

		if (stateIdx>=expConnState.length) {
			assertProxy.assertNotSame("more states then expected", expConnState.length, stateIdx);
		}
		
		if (listenerActive) {
			assertProxy.assertEquals(expConnState[stateIdx++], e.getState());
		} else {
			assertProxy.assertTrue(false);
		}
		
		notifyAll();
	}

	private synchronized void evaluateSuspended(ConnectionEvent e)
	{
		responseReceived = true;

		if (listenerActive) {
			assertProxy.assertEquals(e.getConnectable().isSuspended(), suspended);
		} else {
			assertProxy.assertTrue(false);
		}

		notifyAll();
	}

	public void resumed(ConnectionEvent e)
	{
		evaluateSuspended(e);
	}

	public void suspended(ConnectionEvent e)
	{
		evaluateSuspended(e);
	}

	public void disconnected(ConnectionEvent e)
	{
		evaluateConnectionState(e);
	}

	public void connectionLost(ConnectionEvent e)
	{
		evaluateConnectionState(e);
	}

	public void destroyed(ConnectionEvent e)
	{
		evaluateConnectionState(e);
	}

	public void connectionFailed(ConnectionEvent e)
	{
		evaluateConnectionState(e);
	}

	public void connected(ConnectionEvent e)
	{
		evaluateConnectionState(e);
	}
	
	public void operational(ConnectionEvent e) {
		evaluateConnectionState(e);
	}

	public synchronized void waitForResponse(long timeout) {
		if (responseReceived) {
			return;
		}
		try {
			wait(timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void reset() {
		responseReceived=false;
		stateIdx=0;
	}

}