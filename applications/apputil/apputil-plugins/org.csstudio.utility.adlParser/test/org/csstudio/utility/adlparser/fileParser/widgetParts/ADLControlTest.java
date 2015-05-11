/*************************************************************************\
 * Copyright (c) 2010  UChicago Argonne, LLC
 * This file is distributed subject to a Software License Agreement found
 * in the file LICENSE that is included with this distribution.
/*************************************************************************/
package org.csstudio.utility.adlparser.fileParser.widgetParts;

import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.FileLine;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;

import junit.framework.TestCase;

public class ADLControlTest extends TestCase {
	ADLControl control = new ADLControl();

	public void testADLControlADLWidget() {
		try {
			control = new ADLControl(setupControl());
			assertTrue("Test Name ", control.getName().equals("control"));
			assertTrue("Foreground define ", control.isForeColorDefined());
			assertTrue("Background defined ", control.isBackColorDefined());
			assertEquals("Foreground color ", control.getForegroundColor(), 5);
			assertEquals("Background color ", control.getBackgroundColor(), 23);
			assertTrue("Channel set ", control.getChan().equals("chan1"));
		} catch (WrongADLFormatException e) {
			fail("This should Pass");
		}
		try {
			control = new ADLControl(setupControlWithCtrl());
			assertTrue("Test Name ", control.getName().equals("control"));
			assertTrue("Foreground define ", control.isForeColorDefined());
			assertTrue("Background defined ", control.isBackColorDefined());
			assertEquals("Foreground color ", control.getForegroundColor(), 5);
			assertEquals("Background color ", control.getBackgroundColor(), 23);
			assertTrue("Channel set ", control.getChan().equals("chan1"));
		} catch (WrongADLFormatException e) {
			fail("This should Pass");
		}
		try {
			control = new ADLControl(setupNotControl());
			fail("This should not Pass");
		} catch (WrongADLFormatException e) {
			// this is the correct path
		}
	}

	public void testADLControl() {
		assertTrue("Test Name ", control.getName().equals("control"));
		assertFalse("Foreground define ", control.isForeColorDefined());
		assertFalse("Background defined ", control.isBackColorDefined());
		assertTrue("channel initialized ", control.getChan().equals(""));

	}

	/**
	 * Setup a normal control
	 * 
	 * @return
	 */
	private ADLWidget setupControl() {
		ADLWidget object = new ADLWidget("control", null, 5);
		object.addBody(new FileLine("clr=5", 6));
		object.addBody(new FileLine("bclr=23", 7));
		object.addBody(new FileLine("chan=chan1", 8));

		return object;
	}

	/**
	 * Setup a older style where control channel is denoted with "ctrl" instead
	 * of "chan"
	 * 
	 * @return
	 */
	private ADLWidget setupControlWithCtrl() {
		ADLWidget object = new ADLWidget("control", null, 5);
		object.addBody(new FileLine("clr=5", 6));
		object.addBody(new FileLine("bclr=23", 7));
		object.addBody(new FileLine("ctrl=chan1", 8));

		return object;
	}

	/**
	 * 
	 * @return
	 */
	private ADLWidget setupNotControl() {
		ADLWidget object = new ADLWidget("notcontrol", null, 5);
		object.addBody(new FileLine("clr=5", 6));
		object.addBody(new FileLine("bclr=23", 7));
		object.addBody(new FileLine("chan=chan1", 8));

		return object;
	}

}
