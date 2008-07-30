package org.csstudio.nams.application.department.decision;

import junit.framework.Assert;

import org.csstudio.nams.common.testutils.AbstractObject_TestCase;

public class ThreadTypesOfDecisionDepartment_Test extends
		AbstractObject_TestCase<ThreadTypesOfDecisionDepartment> {

	public void testNumberOfElements() {
		Assert.assertEquals(4, ThreadTypesOfDecisionDepartment.values().length);
	}

	@Override
	protected ThreadTypesOfDecisionDepartment getNewInstanceOfClassUnderTest() {
		return ThreadTypesOfDecisionDepartment.ABTEILUNGSLEITER;
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected ThreadTypesOfDecisionDepartment[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		return new ThreadTypesOfDecisionDepartment[] {
				ThreadTypesOfDecisionDepartment.AUSGANGSKORBBEARBEITER,
				ThreadTypesOfDecisionDepartment.SACHBEARBEITER,
				ThreadTypesOfDecisionDepartment.TERMINASSISTENZ };
	}

}
