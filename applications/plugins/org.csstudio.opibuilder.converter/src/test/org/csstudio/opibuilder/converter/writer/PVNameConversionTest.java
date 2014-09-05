package org.csstudio.opibuilder.converter.writer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PVNameConversionTest {

	// Note that there may be different correct ways of rendering 
	// these PVs, and these tests rely on the ordering provided here in the
	// answers.
	// All backslashes must be doubled in java String literals.
	String CALC_MULT_EDM = "CALC\\\\\\{a*1000\\}(SR-DI-EBPM-01:SA:X:MEAN)";
	String CALC_MULT_CSS = "=(pv(\"SR-DI-EBPM-01:SA:X:MEAN\") * 1000)";
	String CALC_DIV_EDM = "CALC\\\\\\{A/B\\}($(device):FF:PROCESS_TIME,106)";
	String CALC_DIV_CSS = "=(pv(\"$(device):FF:PROCESS_TIME\") / 106)";
	String LOC_EDM = "LOC\\\\show-required";
	String LOC_CSS = "loc://show-required";
	// There's no VInt local PV, but VDouble works
	String LOC_INT_EDM = "LOC\\\\gridSizePV=i:100";
	String LOC_INT_CSS = "loc://gridSizePV(100)";
	String LOC_INT_WINDOW_EDM = "LOC\\\\$(!W)menumux=i:0";
	String LOC_INT_WINDOW_CSS = "loc://$(DID)menumux(0)";
	String CALC_LT_EDM = "CALC\\\\\\{A<100000000\\}($(P)$(M).$(ACCL))";
	String CALC_LT_CSS = "=(pv(\"$(P)$(M).$(ACCL)\") < 100000000)";
	String CALC_OR_EDM = "CALC\\\\\\{A=0||A=3\\}(PVNAME)";
	String CALC_OR_CSS = "=(pv(\"PVNAME\") == 0 || pv(\"PVNAME\") == 3)";
	String CALC_SUM_EDM = "CALC\\\\sum(PVNAME, 1000)";
	String CALC_SUM_CSS = "=(pv(\"PVNAME\") + 1000)";
	String CALC_LOC_EDM = "CALC\\\\\\{A=0||A=3\\}(LOC\\\\$(!W)menumux)";
	String CALC_LOC_CSS = "=(pv(\"loc://$(DID)menumux\") == 0 || pv(\"loc://$(DID)menumux\") == 3)";
	// Some failure cases
	String LOC_JUNK_PV_EDM = "LOC\\\\junkasdaaas(((Gas'";
	String LOC_JUNK_PV_CSS = "loc://junkasdaaas(((Gas'";
	String CALC_JUNK_PV = "CALC\\\\junkasdaaas(((Gas'";
	String CALC_BOGUS_EXPR = "CALC\\\\\\{asdfa}(10)";

	// I don't understand this calc PV:
	// /dls_sw/prod/R3.14.12.3/support/diagOpi/2-42/xbpm/xbpmsij.edl
	String mystery = "CALC\\\\\\{A*1000\\}()FE$(idi)-DI-PBPM-01:BEAMX";


	private void testConversion(String edm, String css) {
		css = css.replaceAll("\\s+", "");
		String converted = PVNameConversion.convertPVName(edm);
		// Remove any spaces from conversions to avoid ambiguity.
		css = css.replaceAll("\\s+", "");
		converted = converted.replaceAll("\\s+", "");
		assertEquals(css, converted);
	}

	@Test
	public void convertLocalPV() {
		testConversion(LOC_EDM, LOC_CSS);
	}

	@Test
	public void convertLocalIntPV() {
		testConversion(LOC_INT_EDM, LOC_INT_CSS);
	}

	@Test
	public void calcDivPV() {
		testConversion(CALC_DIV_EDM, CALC_DIV_CSS);
	}

	@Test
	public void calcMultPV() {
		testConversion(CALC_MULT_EDM, CALC_MULT_CSS);
	}

	@Test
	public void localWindowPV() {
		testConversion(LOC_INT_WINDOW_EDM, LOC_INT_WINDOW_CSS);
	}

	@Test
	public void calcLessThanPV() {
		testConversion(CALC_LT_EDM, CALC_LT_CSS);
	}

	@Test
	public void calcOrPV() {
		testConversion(CALC_OR_EDM, CALC_OR_CSS);
	}

	@Test
	public void calcSumPV() {
		testConversion(CALC_SUM_EDM, CALC_SUM_CSS);
	}

	@Test
	public void calcAndLocPV() {
		testConversion(CALC_LOC_EDM, CALC_LOC_CSS);
	}

	@Test
	public void locJunkPV() {
		testConversion(LOC_JUNK_PV_CSS, LOC_JUNK_PV_CSS);
	}

	@Test
	public void calcJunkPV() {
		testConversion(CALC_JUNK_PV, CALC_JUNK_PV);
	}

	@Test
	public void calcBogusExpression() {
		testConversion(CALC_BOGUS_EXPR, CALC_BOGUS_EXPR);
	}
}
