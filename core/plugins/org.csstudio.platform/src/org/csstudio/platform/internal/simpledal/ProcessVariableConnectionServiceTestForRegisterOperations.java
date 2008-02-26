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
 package org.csstudio.platform.internal.simpledal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.internal.simpledal.ProcessVariableConnectionServiceTest.ProcessVariableListener;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.ValueType;

/**
 * Test implementation for {@link ProcessVariableConnectionService} which tests
 * the asynchronous single get operations.
 * 
 * @author swende
 * 
 */
public class ProcessVariableConnectionServiceTestForRegisterOperations extends
		AbstractProcessVariableConnectionServiceTest {


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doConnectionTest(
			IProcessVariableAddress processVariableAddress, ValueType valueType)
			throws InterruptedException {
		
		// we create a test listeners
		TestListener listener = createProcessVariableValueListener(processVariableAddress, valueType);

		// we register that listener
		_service.register(listener, processVariableAddress, valueType);

		// the unit test needs to sleep for a while to let the asynchronous
		// processes do their job
		Thread.sleep(30000);

		// now we can check the results
		assertTrue("The listener must receive more updates.", listener
				.getReceivedValues().size() > 0);

		for (Object value : new ArrayList(listener.getReceivedValues())) {
			Class expectedJavaType = valueType.getJavaType();
			Class type = value.getClass();

			assertNotNull(value);

			assertTrue("All values need to be assignable from ["
					+ expectedJavaType + "]", expectedJavaType
					.isAssignableFrom(type));
		}
		
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected long getSleepTime() {
		return 10000;
	}

}
