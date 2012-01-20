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

import java.util.Properties;


/**
 * Context, which informs datatypes modelling layer about lifecycle state. When
 * application closes then the application context must fire lifecycle event for
 * destruction, so allocated remote objects can clean up.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public interface AbstractApplicationContext extends LifecycleReporter,
	Identifiable
{
	/**
	 * Returns application configuration. Must not be null. If
	 * implementation can not provide own  configuration, then
	 * <code>System.getProperties()</code> must be returned.
	 *
	 * @return application configuration or <code>System.getProperties()</code>
	 */
	public Properties getConfiguration();

	/**
	 * Name of the application.
	 *
	 * @return application name
	 */
	public String getName();

	/**
	 * Destroys application and finishes it's lifecycle. Must fire
	 * lifecycle event.
	 */
	public void destroy();

	/**
	 * Returns value from arbitrary key/value storage for this application context.
	 * @param keyName name of property
	 * @return prioperty value if exists, otherwise <code>null</code>
	 */
	public Object getApplicationProperty(String keyName);

	/**
	 * Stores named value to arbitrary key/value storage for this application context.
	 * This may be used for application to store additional configuration parameters.
	 * @param keyName the name of stored property
	 * @param value the value of the property
	 */
	public void putApplicationProperty(String keyName, Object value);
}

/* __oOo__ */
