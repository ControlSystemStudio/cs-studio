package org.csstudio.utility.adlparser.fileParser;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class LinePartsTest  extends TestCase {
	private String rowLineContents = "{ test }";
	private LineParts rowLine = new LineParts(rowLineContents);
	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testLineParts() {
		assertTrue("Test ", rowLine.getRowLine().equals(rowLineContents));
	}

}
