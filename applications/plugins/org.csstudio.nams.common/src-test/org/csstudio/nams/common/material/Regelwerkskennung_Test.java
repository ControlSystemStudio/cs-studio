package org.csstudio.nams.common.material;

import org.csstudio.nams.common.testutils.AbstractValue_TestCase;
import org.junit.Test;

public class Regelwerkskennung_Test extends
		AbstractValue_TestCase<Regelwerkskennung> {
	@Test
	public void testErzeugen() {
		Regelwerkskennung.valueOf();
	}

	@Override
	protected Regelwerkskennung doGetAValueOfTypeUnderTest() {
		return Regelwerkskennung.valueOf();
	}

	@Override
	protected Regelwerkskennung[] doGetDifferentInstancesOfTypeUnderTest() {
		return new Regelwerkskennung[] { Regelwerkskennung.valueOf(),
		Regelwerkskennung.valueOf(), Regelwerkskennung.valueOf() };
	}
}
