package org.csstudio.utility.adlparser.fileParser.widgetParts;

import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.FileLine;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;

import junit.framework.TestCase;

public class ADLObjectTest extends TestCase {
	ADLObject adlObject = null;
	protected void setUp() throws Exception {
		super.setUp();
	}

	private ADLWidget setupADLWidget() {
		ADLWidget object = new ADLWidget("object", null, 5);
		object.addBody(new FileLine("x=57", 6));
		object.addBody(new FileLine("y=102", 7));
		object.addBody(new FileLine("height=25", 8));
		object.addBody(new FileLine("width=327", 9));
		return object;
	}

	// setup for x is bad.  comma replaces equals
	private ADLWidget setupBadADLWidget1() {
		ADLWidget object = new ADLWidget("object", null, 5);
		object.addBody(new FileLine("x,57", 6));
		object.addBody(new FileLine("y=102", 7));
		object.addBody(new FileLine("height=25", 8));
		object.addBody(new FileLine("width=327", 9));
		return object;
	}

	private ADLWidget setupBadADLWidget2() {
		ADLWidget object = new ADLWidget("object", null, 5);
		object.addBody(new FileLine("x=57", 6));
		object.addBody(new FileLine("y=102", 7));
		object.addBody(new FileLine("height=25", 8));
		object.addBody(new FileLine("width=327", 9));
		return object;
	}


	public void testADLObject() {
		setupADLWidget();
		try {
			adlObject = new ADLObject(setupADLWidget());
			
		} catch (WrongADLFormatException e) {
			fail("This object should work");
			e.printStackTrace();
		}
		assertEquals("Check value for X", adlObject.getX(), 57);
		assertEquals("Check value for Y", adlObject.getY(), 102);
		assertEquals("Check value for Height", adlObject.getHeight(), 25);
		assertEquals("Check value for Width", adlObject.getWidth(), 327);

		try {
			adlObject = new ADLObject(setupBadADLWidget1());
			fail("BadADLWidget1 should fail");
			
		} catch (WrongADLFormatException e) {
			//OK
		}
		
	}

	public void testGetName() {
		try {
			adlObject = new ADLObject(setupADLWidget());
			
		} catch (WrongADLFormatException e) {
			fail("This object should work");
			e.printStackTrace();
		}
		assertTrue("Check Name of the widgetPart", adlObject.getName().equals("object"));
	}

}
