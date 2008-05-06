package de.c1wps.desy.ams.allgemeines.contract;

import junit.framework.TestCase;

import org.junit.Test;

import de.c1wps.desy.ams.AbstractObject_TestCase;

/**
 * Dies ist kein {@link AbstractObject_TestCase}, da Exemplare nicht möglich
 * sind!
 */
public class Contract_Test extends TestCase {

	@Test
	public void testCreationImpossible() {
		try {
			new Contract();
			fail("failed expected assertion error");
		} catch (AssertionError ae) {
			assertEquals("Creation of instances of this class is undesired!",
					ae.getMessage());
		}
	}

	@Test
	public void testRequireNotNull() {
		try {
			Contract.requireNotNull("null", null);
			fail("failed expected assertion error");
		} catch (AssertionError ae) {
			assertEquals("Precondition unsatisfied: null != null", ae
					.getMessage());
		}
	}

	@Test
	public void testRequireNotNullSuccess() {
		Contract.requireNotNull("new Object()", new Object());
	}

	@Test
	public void testRequireNotNullWithoutValueName() {
		try {
			Contract.requireNotNull(null, new Object());
			fail("failed expected assertion error");
		} catch (AssertionError ae) {
			assertEquals("Precondition unsatisfied: valueName != null", ae
					.getMessage());
		}
	}

	@Test
	public void testRequireNotNullWithoutAnything() {
		try {
			Contract.requireNotNull(null, null);
			fail("failed expected assertion error");
		} catch (AssertionError ae) {
			assertEquals("Precondition unsatisfied: valueName != null", ae
					.getMessage());
		}
	}

	@Test
	public void testRequireFail() {
		try {
			Contract.require(false, "false");
			fail("failed expected assertion error");
		} catch (AssertionError ae) {
			assertEquals("Precondition unsatisfied: false", ae.getMessage());
		}
	}

	@Test
	public void testRequireFailWithoutConditionDescriptio() {
		try {
			Contract.require(false, null);
			fail("failed expected assertion error");
		} catch (AssertionError ae) {
			assertEquals("Precondition unsatisfied: description != null", ae
					.getMessage());
		}
	}

	@Test
	public void testRequireSuccess() {
		Contract.require(true, "true");
	}

	@Test
	public void testRequireSuccessWithoutConditionDescriptio() {
		try {
			Contract.require(false, null);
			fail("failed expected assertion error");
		} catch (AssertionError ae) {
			assertEquals("Precondition unsatisfied: description != null", ae
					.getMessage());
		}
	}

	@Test
	public void testEnsure() {
		try {
			Contract.ensure(false, "false");
			fail("failed expected assertion error");
		} catch (AssertionError ae) {
			assertEquals("Precondition unsatisfied: false", ae.getMessage());
		}
	}

	@Test
	public void testEnsureSuccess() {
		Contract.ensure(true, "true");
	}

	@Test
	public void testEnsureResultNotNull() {
		try {
			Contract.ensureResultNotNull(null);
			fail("failed expected assertion error");
		} catch (AssertionError ae) {
			assertEquals("Postcondition unsatisfied: §result != null", ae
					.getMessage());
		}
	}

	@Test
	public void testEnsureResultNotNullSuccess() {
		Contract.ensureResultNotNull(new Object());
	}

}
