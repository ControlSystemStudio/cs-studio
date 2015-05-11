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
public class ADLMonitorTest extends TestCase {
	ADLConnected monitor = new ADLMonitor();
	
	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.widgetParts.ADLMonitor#ADLMonitor(org.csstudio.utility.adlparser.fileParser.ADLWidget)}.
	 */
	public void testADLMonitorADLWidget() {
		try {
			monitor = new ADLMonitor(setupMonitor());
			assertTrue( "Test Name ", monitor.getName().equals("monitor"));
			assertTrue( "Foreground define ", monitor.isForeColorDefined());
			assertTrue( "Background defined ", monitor.isBackColorDefined());
			assertEquals( "Foreground color ", monitor.getForegroundColor(), 5);
			assertEquals( "Background color ", monitor.getBackgroundColor(), 23);
			assertTrue( "Channel set ", monitor.getChan().equals("chan1"));
		} catch (WrongADLFormatException e){
			fail ("This should Pass");
		}
		try {
			monitor = new ADLMonitor(setupMonitorWithRdbk());
			assertTrue( "Test Name ", monitor.getName().equals("monitor"));
			assertTrue( "Foreground define ", monitor.isForeColorDefined());
			assertTrue( "Background defined ", monitor.isBackColorDefined());
			assertEquals( "Foreground color ", monitor.getForegroundColor(), 5);
			assertEquals( "Background color ", monitor.getBackgroundColor(), 23);
			assertTrue( "Channel set ", monitor.getChan().equals("chan1"));
		} catch (WrongADLFormatException e){
			fail ("This should Pass");
		}
		try {
			monitor = new ADLMonitor(setupNotMonitor());
			fail ("This should not Pass");
		} catch (WrongADLFormatException e){
			// this is the correct path
		}
	}

	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.widgetParts.ADLMonitor#ADLMonitor()}.
	 */
	public void testADLMonitor() {
		assertTrue( "Test Name ", monitor.getName().equals("monitor"));
		assertFalse( "Foreground define ", monitor.isForeColorDefined());
		assertFalse( "Background defined ", monitor.isBackColorDefined());
		assertTrue( "channel initialized ", monitor.getChan().equals(""));
	}

	/**
	 * 	Setup a normal control
	 * @return
	 */
	private ADLWidget setupMonitor() {
		ADLWidget object = new ADLWidget("monitor", null, 5);
		object.addBody(new FileLine("clr=5", 6));
		object.addBody(new FileLine("bclr=23", 7));
		object.addBody(new FileLine("chan=chan1", 8));

		return object;
	}

	/**
	 * 	Setup a older style where control channel is denoted with "ctrl" instead of "chan"
	 * @return
	 */
	private ADLWidget setupMonitorWithRdbk() {
		ADLWidget object = new ADLWidget("monitor", null, 5);
		object.addBody(new FileLine("clr=5", 6));
		object.addBody(new FileLine("bclr=23", 7));
		object.addBody(new FileLine("rdbk=chan1", 8));

		return object;
	}

	/**
	 * 	
	 * @return
	 */
	private ADLWidget setupNotMonitor() {
		ADLWidget object = new ADLWidget("notmonitor", null, 5);
		object.addBody(new FileLine("clr=5", 6));
		object.addBody(new FileLine("bclr=23", 7));
		object.addBody(new FileLine("chan=chan1", 8));

		return object;
	}


}
