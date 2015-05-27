package de.desy.language.libraries.utils.contract;

import org.eclipse.core.runtime.Assert;

/**
 * A very simple contract-class.
 *
 * @author C1 WPS mbH / KM, MZ
 */
public class Contract {

    /**
     * Checks if given object is not null.
     *
     * @param valueName
     *            The name of the checked value, should not be null.
     * @param obj
     *            The value to be checked.
     */
    public static void requireNotNull(final String valueName, final Object obj) {
        Assert.isNotNull(valueName,
                "Precondition unsatisfied: valueName != null");
        Assert.isNotNull(obj, "Precondition unsatisfied: " + valueName
                + " != null");
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
        Assert.isNotNull(description,
                "Precondition unsatisfied: description != null");
        Assert.isTrue(condition, "Precondition unsatisfied: " + description);
    }

    /**
     * Checks if given condition is true.
     *
     * @param condition
     *            The condition to be checked.
     * @param description
     *            The description of that condition.
     */
    public static void ensure(final boolean condition, final String description) {
        Assert.isNotNull(description,
                "Precondition unsatisfied: description != null");
        Assert.isTrue(condition, "Postcondition unsatisfied: " + description);
    }

    /**
     * Checks given result is not null.
     *
     * @param object
     *            The result not to be null.
     */
    public static void ensureResultNotNull(final Object object) {
        Assert.isNotNull(object, "Postcondition unsatisfied: result != null");
    }
}
