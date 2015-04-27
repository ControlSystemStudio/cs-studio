/**
 * 
 */
package org.csstudio.utility.adlparser.fileParser.widgetParts;

import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.FileLine;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;

import junit.framework.TestCase;

/**
 * @author hammonds
 *
 */
public class ADLLimitsTest extends TestCase {
	ADLLimits limits = new ADLLimits();
	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.widgetParts.ADLLimits#ADLLimits(org.csstudio.utility.adlparser.fileParser.ADLWidget)}.
	 */
	public void testADLLimitsADLWidget() {
		try {
			limits = new ADLLimits(setupLimits());
			assertTrue("Name " + limits.getName(), limits.getName().equals("limits"));
			assertTrue("loprSrc " + limits.getLoprSrc(), limits.getLoprSrc().equals("Default"));
			assertTrue("hoprSrc " + limits.getHoprSrc(), limits.getHoprSrc().equals("Default"));
			assertTrue("precSrc " + limits.getPrecSrc(), limits.getPrecSrc().equals("Default"));
			assertEquals( "loprdefault " + limits.getLoprDefault(), limits.getLoprDefault(), 0.0f);
			assertEquals( "hoprdefault " + limits.getHoprDefault(), limits.getHoprDefault(), 0.0f);
			assertEquals( "precdefault " + limits.getPrecDefault(), limits.getPrecDefault(), 0);
		} catch (WrongADLFormatException e) {
			fail("This should pass");
		}
		try {
			limits = new ADLLimits(setupLimitsUserSpecified());
			assertTrue("Name " + limits.getName(), limits.getName().equals("limits"));
			assertTrue("loprSrc " + limits.getLoprSrc(), limits.getLoprSrc().equals("User Specified"));
			assertTrue("hoprSrc " + limits.getHoprSrc(), limits.getHoprSrc().equals("User Specified"));
			assertTrue("precSrc " + limits.getPrecSrc(), limits.getPrecSrc().equals("User Specified"));
			assertEquals( "loprdefault " + limits.getLoprDefault(), limits.getLoprDefault(), 1.0f);
			assertEquals( "hoprdefault " + limits.getHoprDefault(), limits.getHoprDefault(), 3.0f);
			assertEquals( "precdefault " + limits.getPrecDefault(), limits.getPrecDefault(), 2);
		} catch (WrongADLFormatException e) {
			fail("This should pass");
		}
	}

	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.widgetParts.ADLLimits#ADLLimits()}.
	 */
	public void testADLLimits() {
		assertTrue("Name " + limits.getName(), limits.getName().equals("limits"));
		assertTrue("loprSrc " + limits.getLoprSrc(), limits.getLoprSrc().equals("Channel"));
		assertTrue("hoprSrc " + limits.getHoprSrc(), limits.getHoprSrc().equals("Channel"));
		assertTrue("precSrc " + limits.getPrecSrc(), limits.getPrecSrc().equals("Channel"));
	}

	/**
	 * Setup a normal limit in default state
	 * @return
	 */
	private ADLWidget setupLimits() {
		ADLWidget object = new ADLWidget("limits", null, 5);
		object.addBody(new FileLine("hoprsrc=Default", 6));
		object.addBody(new FileLine("loprsrc=Default", 7));
		object.addBody(new FileLine("precsrc=Default", 8));

		return object;
	}

	/**
	 * Setup a normal limit in default state
	 * @return
	 */
	private ADLWidget setupLimitsUserSpecified() {
		ADLWidget object = new ADLWidget("limits", null, 5);
		object.addBody(new FileLine("hoprsrc=User Specified", 6));
		object.addBody(new FileLine("loprsrc=User Specified", 7));
		object.addBody(new FileLine("precsrc=User Specified", 8));
		object.addBody(new FileLine("hoprdefault=3.0", 6));
		object.addBody(new FileLine("loprdefault=1.0", 7));
		object.addBody(new FileLine("precdefault=2", 8));
		

		return object;
	}


}
