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


/**
 * Instances of this exception type are thrown by methods in DAL
 * library which query a primary data source. Primary data sources are
 * data sources that  actually generate the data, such as measurement devices,
 * databases etc. The failure may occur because of the inability to connect to
 * the data source,  to communicate to the data source or to actually exchange
 * the data (for example,  because of incompatible data formats). This
 * exception type acts as a wrapper  for an exception thrown by the layer
 * underlying the implementation of DAL.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public class DataExchangeException extends RemoteException
{
	private static final long serialVersionUID = 1L;

	/**
	     * Constructs the exception with the source and message specification.
	     *
	     * @param instance object creating the exception
	     * @param message exception message
	     */
	public DataExchangeException(Object instance, String message)
	{
		super(instance, message);
	}

	/**
	     * Constructs the exception with the source, message and cause specification.
	     *
	     * @param instance object creating the exception
	     * @param message exception message
	     * @param t the exception that is causing <code>this</code> exception to
	     *            be thrown
	     */
	public DataExchangeException(Object instance, String message, Throwable t)
	{
		super(instance, message, t);
	}
} /* __oOo__ */


/* __oOo__ */
