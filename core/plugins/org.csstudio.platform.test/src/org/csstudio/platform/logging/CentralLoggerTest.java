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
package org.csstudio.platform.logging;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Test class for {@link org.csstudio.platform.logging.CentralLogger}.
 * 
 * @author Alexander Will
 * 
 */
public class CentralLoggerTest {
	
	/**
	 * Log message for testing.
	 */
	private String _testMessage = "test log message"; //$NON-NLS-1$
	
	/**
	 * A sample throwable for testing.
	 */
	private Throwable _testThrowable = new Throwable("test throwable"); //$NON-NLS-1$

	/**
	 * Test method for {@link org.csstudio.platform.logging.CentralLogger#getInstance()}.
	 */
	@Test
	public final void testGetInstance() {
		CentralLogger l = CentralLogger.getInstance();
		assertNotNull(l);
	}

	/**
	 * Test method for {@link org.csstudio.platform.logging.CentralLogger#configure()}.
	 */
	@Test
	public final void testConfigure() {
		CentralLogger.getInstance().configure();
	}

	/**
	 * Test method for {@link org.csstudio.platform.logging.CentralLogger#info(java.lang.Object, java.lang.String)}.
	 */
	@Test
	public final void testInfoObjectString() {
		CentralLogger.getInstance().info(null, _testMessage);
		CentralLogger.getInstance().info(this, _testMessage);
	}

	/**
	 * Test method for {@link org.csstudio.platform.logging.CentralLogger#info(java.lang.Object, java.lang.Throwable)}.
	 */
	@Test
	public final void testInfoObjectThrowable() {
		CentralLogger.getInstance().info(null, _testThrowable);
		CentralLogger.getInstance().info(this, _testThrowable);
	}

	/**
	 * Test method for {@link org.csstudio.platform.logging.CentralLogger#info(java.lang.Object, java.lang.String, java.lang.Throwable)}.
	 */
	@Test
	public final void testInfoObjectStringThrowable() {
		CentralLogger.getInstance().info(null, _testMessage, _testThrowable);
		CentralLogger.getInstance().info(this, _testMessage, _testThrowable);
	}

	/**
	 * Test method for {@link org.csstudio.platform.logging.CentralLogger#debug(java.lang.Object, java.lang.String)}.
	 */
	@Test
	public final void testDebugObjectString() {
		CentralLogger.getInstance().debug(null, _testMessage);
		CentralLogger.getInstance().debug(this, _testMessage);
	}

	/**
	 * Test method for {@link org.csstudio.platform.logging.CentralLogger#debug(java.lang.Object, java.lang.Throwable)}.
	 */
	@Test
	public final void testDebugObjectThrowable() {
		CentralLogger.getInstance().debug(null, _testThrowable);
		CentralLogger.getInstance().debug(this, _testThrowable);
	}

	/**
	 * Test method for {@link org.csstudio.platform.logging.CentralLogger#debug(java.lang.Object, java.lang.String, java.lang.Throwable)}.
	 */
	@Test
	public final void testDebugObjectStringThrowable() {
		CentralLogger.getInstance().debug(null, _testMessage, _testThrowable);
		CentralLogger.getInstance().debug(this, _testMessage, _testThrowable);
	}

	/**
	 * Test method for {@link org.csstudio.platform.logging.CentralLogger#warn(java.lang.Object, java.lang.String)}.
	 */
	@Test
	public final void testWarnObjectString() {
		CentralLogger.getInstance().warn(null, _testMessage);
		CentralLogger.getInstance().warn(this, _testMessage);
	}

	/**
	 * Test method for {@link org.csstudio.platform.logging.CentralLogger#warn(java.lang.Object, java.lang.Throwable)}.
	 */
	@Test
	public final void testWarnObjectThrowable() {
		CentralLogger.getInstance().warn(null, _testThrowable);
		CentralLogger.getInstance().warn(this, _testThrowable);
	}

	/**
	 * Test method for {@link org.csstudio.platform.logging.CentralLogger#warn(java.lang.Object, java.lang.String, java.lang.Throwable)}.
	 */
	@Test
	public final void testWarnObjectStringThrowable() {
		CentralLogger.getInstance().warn(null, _testMessage, _testThrowable);
		CentralLogger.getInstance().warn(this, _testMessage, _testThrowable);
	}

	/**
	 * Test method for {@link org.csstudio.platform.logging.CentralLogger#error(java.lang.Object, java.lang.String)}.
	 */
	@Test
	public final void testErrorObjectString() {
		CentralLogger.getInstance().error(null, _testMessage);
		CentralLogger.getInstance().error(this, _testMessage);
	}

	/**
	 * Test method for {@link org.csstudio.platform.logging.CentralLogger#error(java.lang.Object, java.lang.Throwable)}.
	 */
	@Test
	public final void testErrorObjectThrowable() {
		CentralLogger.getInstance().error(null, _testThrowable);
		CentralLogger.getInstance().error(this, _testThrowable);
	}

	/**
	 * Test method for {@link org.csstudio.platform.logging.CentralLogger#error(java.lang.Object, java.lang.String, java.lang.Throwable)}.
	 */
	@Test
	public final void testErrorObjectStringThrowable() {
		CentralLogger.getInstance().error(null, _testMessage, _testThrowable);
		CentralLogger.getInstance().error(this, _testMessage, _testThrowable);
	}

	/**
	 * Test method for {@link org.csstudio.platform.logging.CentralLogger#fatal(java.lang.Object, java.lang.String)}.
	 */
	@Test
	public final void testFatalObjectString() {
		CentralLogger.getInstance().fatal(null, _testMessage);
		CentralLogger.getInstance().fatal(this, _testMessage);
	}

	/**
	 * Test method for {@link org.csstudio.platform.logging.CentralLogger#fatal(java.lang.Object, java.lang.Throwable)}.
	 */
	@Test
	public final void testFatalObjectThrowable() {
		CentralLogger.getInstance().fatal(null, _testThrowable);
		CentralLogger.getInstance().fatal(this, _testThrowable);
	}

	/**
	 * Test method for {@link org.csstudio.platform.logging.CentralLogger#fatal(java.lang.Object, java.lang.String, java.lang.Throwable)}.
	 */
	@Test
	public final void testFatalObjectStringThrowable() {
		CentralLogger.getInstance().fatal(null, _testMessage, _testThrowable);
		CentralLogger.getInstance().fatal(this, _testMessage, _testThrowable);
	}

}
