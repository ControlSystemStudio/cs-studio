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
package org.csstudio.sds.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.csstudio.sds.internal.model.test.WidgetModelTestHelper;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for class {@link ConnectionElement}.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class ConnectionElementTest {
	/**
	 * The connection element that will be tested.
	 */
	private ConnectionElement _testConnection;

	/**
	 * The source of this widget model.
	 */
	private AbstractWidgetModel _sourceModel;

	/**
	 * The target widget model.
	 */
	private AbstractWidgetModel _targetModel;

	/**
	 * Set up the test case.
	 * 
	 * @throws java.lang.Exception
	 *             If an execption occurs during setup.
	 */
	@Before
	public void setUp() throws Exception {
		_sourceModel = WidgetModelTestHelper.createWidgetModel();
		_targetModel = WidgetModelTestHelper.createWidgetModel();
		_testConnection = new ConnectionElement(_sourceModel, _targetModel);
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.model.ConnectionElement#getDoubleTestProperty()}.
	 */
	@Test
	public void testGetDoubleTestProperty() {
		assertNull(_testConnection.getDoubleTestProperty());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.model.ConnectionElement#getTypeID()}.
	 */
	@Test
	public void testGetTypeID() {
		assertEquals(ConnectionElement.ID, _testConnection.getTypeID());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.model.ConnectionElement#disconnect()}.
	 */
	@Test
	public void testDisconnect() {
		assertTrue(_testConnection.isConnected());
		_testConnection.disconnect();
		assertFalse(_testConnection.isConnected());
		_testConnection.reconnect();
		assertTrue(_testConnection.isConnected());
		assertEquals(_sourceModel, _testConnection.getSourceModel());
		assertEquals(_targetModel, _testConnection.getTargetModel());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.model.ConnectionElement#getLineStyle()}.
	 */
	@Test
	public void testGetLineStyle() {
		assertEquals(1, _testConnection.getLineStyle());
	}

	/**
	 * Test method for {@link org.csstudio.sds.model.ConnectionElement#invert()}.
	 */
	@Test
	public void testInvert() {
		assertEquals(_sourceModel, _testConnection.getSourceModel());
		assertEquals(_targetModel, _testConnection.getTargetModel());

		_testConnection.invert();

		assertEquals(_targetModel, _testConnection.getSourceModel());
		assertEquals(_sourceModel, _testConnection.getTargetModel());

		_testConnection.invert();

		assertEquals(_sourceModel, _testConnection.getSourceModel());
		assertEquals(_targetModel, _testConnection.getTargetModel());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.model.ConnectionElement#getSourceModel()}.
	 */
	@Test
	public void testGetSourceElement() {
		assertEquals(_sourceModel, _testConnection.getSourceModel());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.model.ConnectionElement#getTargetModel()}.
	 */
	@Test
	public void testGetTargetElement() {
		assertEquals(_targetModel, _testConnection.getTargetModel());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.model.ConnectionElement#reconnect(org.csstudio.sds.model.AbstractWidgetModel, org.csstudio.sds.model.AbstractWidgetModel)}.
	 */
	@Test
	public void testReconnect() {
		assertTrue(_testConnection.isConnected());

		assertEquals(_sourceModel, _testConnection.getSourceModel());
		assertEquals(_targetModel, _testConnection.getTargetModel());

		_testConnection.reconnect(_targetModel, _sourceModel);

		assertEquals(_targetModel, _testConnection.getSourceModel());
		assertEquals(_sourceModel, _testConnection.getTargetModel());

		_testConnection.invert();

		assertEquals(_sourceModel, _testConnection.getSourceModel());
		assertEquals(_targetModel, _testConnection.getTargetModel());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.sds.model.ConnectionElement#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		ConnectionElement anotherConnection = new ConnectionElement(
				_sourceModel, _targetModel);
		assertEquals(_testConnection, anotherConnection);
		
		assertFalse(_testConnection.hashCode() == anotherConnection.hashCode());
		
		assertFalse(_testConnection.equals("123")); //$NON-NLS-1$
	}

}
