package org.csstudio.utility.adlparser.fileParser.widgetParts;

import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.FileLine;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;

import junit.framework.TestCase;

public class ADLDynamicAttributeTest extends TestCase {
	ADLDynamicAttribute attribute = new ADLDynamicAttribute();

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testADLDynamicAttributeADLWidget() {
		try {
			attribute = new ADLDynamicAttribute(setupDynamicADLWidget());
			assertTrue( "Test default Name ", attribute.getName().equals("dynamic attribute"));
			assertTrue( "Test default visability", attribute.get_vis().equals("if zero"));
			assertTrue( "Test default calc", attribute.get_calc().equals("A+B"));
			assertTrue( "Test default chan", attribute.get_chan().equals("chan1"));
			assertTrue( "Test default chanb", attribute.get_chanb().equals("chan2"));
			assertTrue( "Test default chanc", attribute.get_chanc().equals("chan3"));
			assertTrue( "Test default chand", attribute.get_chand().equals("chan4"));
		} catch (WrongADLFormatException e) {
			fail("This should pass");
		}
		
		try {
			attribute = new ADLDynamicAttribute(setupWithAttrSublevelDynamicADLWidget());
			assertTrue( "Test default Name ", attribute.getName().equals("dynamic attribute"));
			assertTrue( "Test default visability", attribute.get_vis().equals("if zero"));
			assertTrue( "Test default calc", attribute.get_calc().equals("A+B"));
			assertTrue( "Test default chan", attribute.get_chan().equals("chan1"));
			assertTrue( "Test default chanb", attribute.get_chanb().equals("chan2"));
			assertTrue( "Test default chanc", attribute.get_chanc().equals("chan3"));
			assertTrue( "Test default chand", attribute.get_chand().equals("chan4"));
		} catch (WrongADLFormatException e) {
			fail("This should pass");
		}
		
		try {
			attribute = new ADLDynamicAttribute(setupWithColorModeDiscreteDynamicADLWidget());
			assertTrue( "Test default Name ", attribute.getName().equals("dynamic attribute"));
			assertTrue( "Test default visability", attribute.get_vis().equals("if zero"));
			assertTrue( "Test default calc", attribute.get_calc().equals("A+B"));
			assertTrue( "Test default chan", attribute.get_chan().equals("chan1"));
			assertTrue( "Test default chanb", attribute.get_chanb().equals("chan2"));
			assertTrue( "Test default chanc", attribute.get_chanc().equals("chan3"));
			assertTrue( "Test default chand", attribute.get_chand().equals("chan4"));
		} catch (WrongADLFormatException e) {
			fail("This should pass");
		}
		
		try {
			attribute = new ADLDynamicAttribute(setupBadDynamicADLWidget1());
			fail("This should pass.  A parameter has a bad name");
		} catch (WrongADLFormatException e) {
			//OK
		}
		
		//TODO add more test cases to testADLDynamicAttributeADLWidget
	}

	public void testADLDynamicAttribute() {
		assertTrue( "Test default Name ", attribute.getName().equals("dynamic attribute"));
		assertTrue( "Test default visability", attribute.get_vis().equals("static"));
		assertTrue( "Test default calc", attribute.get_calc().equals(""));
		assertTrue( "Test default chan", attribute.get_chan().equals(""));
		assertTrue( "Test default chanb", attribute.get_chanb().equals(""));
		assertTrue( "Test default chanc", attribute.get_chanc().equals(""));
		assertTrue( "Test default chand", attribute.get_chand().equals(""));
		
	}

	/**
	 * 	
	 * @return
	 */
	private ADLWidget setupDynamicADLWidget() {
		ADLWidget object = new ADLWidget("dynamic attribute", null, 5);
		object.addBody(new FileLine("vis=if zero", 6));
		object.addBody(new FileLine("calc=A+B", 7));
		object.addBody(new FileLine("chan=chan1", 8));
		object.addBody(new FileLine("chanb=chan2", 9));
		object.addBody(new FileLine("chanc=chan3", 10));
		object.addBody(new FileLine("chand=chan4", 11));

		return object;
	}

	/**
	 * 	
	 * @return
	 */
	private ADLWidget setupWithAttrSublevelDynamicADLWidget() {
		ADLWidget object = new ADLWidget("dynamic attribute", null, 5);
		ADLWidget attr = new ADLWidget("attr", object, 6);
		attr.addBody(new FileLine("vis=if zero", 6));
		attr.addBody(new FileLine("calc=A+B", 7));
		attr.addBody(new FileLine("chan=chan1", 8));
		attr.addBody(new FileLine("chanb=chan2", 9));
		attr.addBody(new FileLine("chanc=chan3", 10));
		attr.addBody(new FileLine("chand=chan4", 11));
		object.addObject(attr);

		return object;
	}

	/**
	 * 	Bad parameter vis has changed to visibility
	 * @return
	 */
	private ADLWidget setupBadDynamicADLWidget1() {
		ADLWidget object = new ADLWidget("dynamic attribute", null, 5);
		object.addBody(new FileLine("visibility=if zero", 6));
		object.addBody(new FileLine("calc=A+B", 7));
		object.addBody(new FileLine("chan=chan1", 8));
		object.addBody(new FileLine("chanb=chan2", 9));
		object.addBody(new FileLine("chanc=chan3", 10));
		object.addBody(new FileLine("chand=chan4", 11));

		return object;
	}

	/**
	 * 	Bad parameter vis has changed to visibility
	 * @return
	 */
	private ADLWidget setupWithColorModeDiscreteDynamicADLWidget() {
		ADLWidget object = new ADLWidget("dynamic attribute", null, 5);
		object.addBody(new FileLine("clr=discrete", 6));
		object.addBody(new FileLine("vis=if zero", 6));
		object.addBody(new FileLine("calc=A+B", 7));
		object.addBody(new FileLine("chan=chan1", 8));
		object.addBody(new FileLine("chanb=chan2", 9));
		object.addBody(new FileLine("chanc=chan3", 10));
		object.addBody(new FileLine("chand=chan4", 11));

		return object;
	}

}
