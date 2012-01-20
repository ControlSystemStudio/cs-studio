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

/**
 *
 */
package org.csstudio.dal.context;

import org.apache.log4j.Logger;


/**
 * Convenience class, whcih blocks current thread until provided <code>Linkable</code>
 * is connected or connection process fails.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 */
public class LinkBlocker<C extends Linkable> extends LinkAdapter<C>
{
	ConnectionEvent e;
	boolean failed = false;
	C l;

	/**
	 * Convenience static method for blocking.
	 * @param l linkable
	 * @param timeout timeout
	 * @param throwException if exception is thrown when failure occurs
	 * @return blocker
	 * @throws ConnectionException
	 */
	public static final <T extends Linkable> LinkBlocker<T> blockUntillConnected(T l,
	    long timeout, boolean throwException) throws ConnectionException
	{
		LinkBlocker<T> b = new LinkBlocker<T>(l);
		b.blockTillConnected(timeout, throwException);

		return b;
	}

	/**
	 * Creates link blocker for provided linkable.
	 * @param l linkable
	 */
	public LinkBlocker(C l)
	{
		this.l = l;
		l.addLinkListener(this);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.context.LinkAdapter#connected(org.csstudio.dal.context.ConnectionEvent)
	 */
	@Override
	public synchronized void connected(ConnectionEvent<C> e)
	{
		this.e = e;
		l.removeLinkListener(this);
		notify();
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.context.LinkAdapter#connectionFailed(org.csstudio.dal.context.ConnectionEvent)
	 */
	@Override
	public synchronized void connectionFailed(ConnectionEvent<C> e)
	{
		this.e = e;
		failed = true;
		l.removeLinkListener(this);
		notify();
	}

	public synchronized void blockTillConnected(long timeout,
	    boolean throwException) throws ConnectionException
	{
		if (l.isConnected()) {
			return;
		}

		if (l.isConnectionFailed()) {
			failed = true;

			if (throwException) {
				throw new ConnectionException(l, "Connection failed.", null);
			}
		}

		try {
			wait(timeout);
		} catch (Exception e) {
			Logger.getLogger(LinkBlocker.class).debug("Wait interrupted.", e);
		}

		if (throwException) {
			if (e == null) {
				throw new ConnectionException(l,
				    "Connection failed, "+(int)(timeout/1000)+"s timeout exceeded.", null);
			}

			if (e.getState() != ConnectionState.CONNECTED) {
				throw new ConnectionException(l, "Connection failed.",
				    e.getError());
			}
		}
	}
}

/* __oOo__ */
