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
public class ProcessVariableConnectionServiceTestForSyncOperations extends
		AbstractProcessVariableConnectionServiceTest {


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doConnectionTest(
			IProcessVariableAddress processVariableAddress, ValueType valueType)
			throws InterruptedException {
		
		// we get the values synchronously
		Object value = "";
		String error = "";
		try {
			value = _service.getValue(processVariableAddress, valueType);
		} catch (Throwable e) {
			error = e.getMessage();
		}

		// print the values and errors to the console
		printReceivedValue(processVariableAddress, valueType, value, error);

		// we must receive a value
		assertNotNull(value);

		// the value must fit the expected type
		Class expectedJavaType = valueType.getJavaType();
		Class type = value.getClass();

		assertTrue("Values needs to be assignable from [" + expectedJavaType
				+ "]", expectedJavaType.isAssignableFrom(type));

		
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected long getSleepTime() {
		return 10000;
	}

}
