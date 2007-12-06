package org.csstudio.platform.internal.simpledal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
public class ProcessVariableConnectionServiceTestForASyncOperations extends
		AbstractProcessVariableConnectionServiceTest {


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doConnectionTest(
			IProcessVariableAddress processVariableAddress, ValueType valueType)
			throws InterruptedException {
		// create a test listener which is informed asynchonously
		TestListener listener = createProcessVariableValueListener(
				processVariableAddress, valueType);

		// query the current value asynchronously
		_service.getValueAsync(processVariableAddress, valueType, listener);

		// the unit test needs to sleep for a while to let the asynchronous
		// processes do their job
		Thread.sleep(3000);

		// now we can check the results
		List<Object> values = listener.getReceivedValues();

		// there must be values
		assertNotNull(values);
		assertFalse(values.isEmpty());

		// the values must fit the requested type
		Class expectedJavaType = valueType.getJavaType();

		for (Object value : values) {
			Class type = value.getClass();

			assertTrue("Values needs to be assignable from ["
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
