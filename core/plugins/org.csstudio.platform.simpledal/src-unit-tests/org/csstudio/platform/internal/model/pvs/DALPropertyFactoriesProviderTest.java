/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
package org.csstudio.platform.internal.model.pvs;

import static org.junit.Assert.*;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.DALPropertyFactoriesProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link DALPropertyFactoriesProvider}.
 * 
 * @author Sven Wende
 * 
 */
public class DALPropertyFactoriesProviderTest {
	DALPropertyFactoriesProvider _provider;

	/**
	 * Setup.
	 */
	@Before
	public void setUp() {
		_provider = DALPropertyFactoriesProvider.getInstance();
	}

	/**
	 * Tear down.
	 */
	@After
	public void tearDown() {
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.model.pvs.DALPropertyFactoriesProvider#getInstance()}.
	 */
	@Test
	public void testGetInstance() {
		assertNotNull(_provider);
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.model.pvs.DALPropertyFactoriesProvider#getPropertyFactory(org.csstudio.platform.model.pvs.ControlSystemEnum)}.
	 */
	@Test
	public void testGetPropertyFactory() {
		assertNotNull(_provider.getPropertyFactory(ControlSystemEnum.DAL_EPICS));
		assertNotNull(_provider.getPropertyFactory(ControlSystemEnum.DAL_TINE));
		assertNotNull(_provider.getPropertyFactory(ControlSystemEnum.DAL_SIMULATOR));
	}

}
