package de.c1wps.desy.ams.allgemeines.regelwerk;

import org.junit.Test;

import de.c1wps.desy.ams.AbstractValue_TestCase;

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
