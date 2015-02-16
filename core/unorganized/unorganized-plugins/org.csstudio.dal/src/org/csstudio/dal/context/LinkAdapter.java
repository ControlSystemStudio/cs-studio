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

package org.csstudio.dal.context;


/**
 * Convenience implementation of <code>LinkListener</code>.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
  *
 * @param <C> linkable type
 */
public class LinkAdapter<C extends Linkable> implements LinkListener<C>
{
	/**
	 * Creates a new LinkAdapter object.
	 */
	public LinkAdapter()
	{
		super();
	}

	/**
	 * @see LinkListener#resumed(ConnectionEvent)
	 */
	public void resumed(ConnectionEvent<C> e)
	{
		// override if needed
	}

	/**
	 * @see LinkListener#suspended(ConnectionEvent)
	 */
	public void suspended(ConnectionEvent<C> e)
	{
		// override if needed
	}

	/**
	 * @see LinkListener#connected(ConnectionEvent)
	 */
	public void connected(ConnectionEvent<C> e)
	{
		// override if needed
	}

	/**
	 * @see LinkListener#operational(ConnectionEvent)
	 */
	public void operational(ConnectionEvent<C> e)
	{
		// override if needed
	}

	/**
	 * @see LinkListener#disconnected(ConnectionEvent)
	 */
	public void disconnected(ConnectionEvent<C> e)
	{
		// override if needed
	}

	/**
	 * @see LinkListener#connectionLost(ConnectionEvent)
	 */
	public void connectionLost(ConnectionEvent<C> e)
	{
		// override if needed
	}

	/**
	 * @see LinkListener#destroyed(ConnectionEvent)
	 */
	public void destroyed(ConnectionEvent<C> e)
	{
		// override if needed
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.context.LinkListener#connectionFailed(org.csstudio.dal.context.ConnectionEvent)
	 */
	public void connectionFailed(ConnectionEvent<C> e)
	{
		// override if needed
	}
}

/* __oOo__ */
