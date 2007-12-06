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
		Thread.sleep(3000);

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
