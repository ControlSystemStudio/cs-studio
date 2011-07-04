
package org.csstudio.nams.common.contract;

import org.junit.Assert;

/**
 * A very simple contract-class.
 * 
 * @author C1 WPS mbH / KM, MZ
 */
public final class Contract {

	/**
	 * Checks if given condition is true.
	 * 
	 * @param condition
	 *            The condition to be checked.
	 * @param description
	 *            The description of that condition.
	 */
	public static void ensure(final boolean condition, final String description) {
		Assert.assertNotNull("Precondition unsatisfied: description != null",
				description);
		Assert
				.assertTrue("Precondition unsatisfied: " + description,
						condition);
	}

	/**
	 * Checks given result is not null.
	 * 
	 * @param object
	 *            The result not to be null.
	 */
	public static void ensureResultNotNull(final Object object) {
		Assert.assertNotNull("Postcondition unsatisfied: §result != null",
				object);
	}

	/**
	 * Checks if given condition is true.
	 * 
	 * @param condition
	 *            The condition to be checked.
	 * @param description
	 *            The description of that condition.
	 */
	public static void require(final boolean condition, final String description) {
		Assert.assertNotNull("Precondition unsatisfied: description != null",
				description);
		Assert
				.assertTrue("Precondition unsatisfied: " + description,
						condition);
	}

	/**
	 * Checks if given object is not null.
	 * 
	 * @param valueName
	 *            The name of the checked value, should not be null.
	 * @param obj
	 *            The value to be checked.
	 */
	public static void requireNotNull(final String valueName, final Object obj) {
		Assert.assertNotNull("Precondition unsatisfied: valueName != null",
				valueName);
		Assert.assertNotNull("Precondition unsatisfied: " + valueName
				+ " != null", obj);
	}

	Contract() {
		throw new AssertionError(
				"Creation of instances of this class is undesired!");
	}
}
