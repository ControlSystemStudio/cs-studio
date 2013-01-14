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
public class ADLBasicAttributeTest extends TestCase {
	ADLBasicAttribute attribute = new ADLBasicAttribute();

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.widgetParts.ADLBasicAttribute#ADLBasicAttribute(org.csstudio.utility.adlparser.fileParser.ADLWidget)}.
	 */
	public void testADLBasicAttributeADLWidget() {
		try {
			attribute = new ADLBasicAttribute(setupWithAttrSublevelADLWidget());
			assertEquals("Testing color on BasicAttribute", attribute.getClr(), 5);
			assertTrue("testing color defined on BasicAttribute", attribute.isColorDefined());
			assertEquals("Testing line width on BasicAttribute", attribute.getWidth(), 3);
			assertTrue("Testing style on BasicAttribute", attribute.getStyle().equals("dash"));
			assertTrue("Testing fill on BasicAttribute", attribute.getFill().equals("outline"));
		} catch (WrongADLFormatException e) {
			fail("This should pass");
			e.printStackTrace();
		}
		
		try {
			attribute = new ADLBasicAttribute(setupBasicADLWidget());
			assertEquals("Testing color on BasicAttribute", attribute.getClr(), 5);
			assertTrue("testing color defined on BasicAttribute", attribute.isColorDefined());
			assertEquals("Testing line width on BasicAttribute", attribute.getWidth(), 3);
			assertTrue("Testing style on BasicAttribute", attribute.getStyle().equals("dash"));
			assertTrue("Testing fill on BasicAttribute", attribute.getFill().equals("outline"));
		} catch (WrongADLFormatException e) {
			fail("This should pass");
			e.printStackTrace();
		}
		
		try {
			attribute = new ADLBasicAttribute(setupColorCommentedADLWidget());
			assertEquals("Testing color on BasicAttribute", attribute.getClr(), 0);
			assertFalse("testing color defined on BasicAttribute", attribute.isColorDefined());
			assertEquals("Testing line width on BasicAttribute", attribute.getWidth(), 3);
			assertTrue("Testing style on BasicAttribute", attribute.getStyle().equals("dash"));
			assertTrue("Testing fill on BasicAttribute", attribute.getFill().equals("outline"));
		} catch (WrongADLFormatException e) {
			fail("This should pass");
			e.printStackTrace();
		}

		try {
			attribute = new ADLBasicAttribute(setupBadADLWidget1());
			fail("This should not pass, comma instead of equals by clr");
		} catch (WrongADLFormatException e) {
			//OK
		}
		
		try {
			attribute = new ADLBasicAttribute(setupBadADLWidget2());
			fail("This should not pass, one of the parameters is misspelled");
		} catch (WrongADLFormatException e) {
			//OK
		}
		
	}
	

	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.widgetParts.ADLBasicAttribute#ADLBasicAttribute()}.
	 */
	public void testADLBasicAttribute() {
		assertTrue( "Test default Name ", attribute.getName().equals("basic attribute"));
		assertEquals( "Test default color ", attribute.getClr(), 0);
		assertEquals( "Test default width ", attribute.getWidth(), 0);
		assertTrue( "Test default style ", attribute.getStyle().equals("solid"));
		assertTrue( "Test default fill ", attribute.getFill().equals("solid"));
		assertFalse( "Test if color is defined", attribute.isColorDefined());
		
	}

	/**
	 * 	
	 * @return
	 */
	private ADLWidget setupBasicADLWidget() {
		ADLWidget object = new ADLWidget("basic attribute", null, 5);
		object.addBody(new FileLine("clr=5", 6));
		object.addBody(new FileLine("width=3", 7));
		object.addBody(new FileLine("style=dash", 8));
		object.addBody(new FileLine("fill=outline", 9));

		return object;
	}

	/**
	 * 	
	 * @return
	 */
	private ADLWidget setupWithAttrSublevelADLWidget() {
		ADLWidget object = new ADLWidget("basic attribute", null, 5);
		ADLWidget attr = new ADLWidget("attr", object, 6);
		attr.addBody(new FileLine("clr=5", 7));
		attr.addBody(new FileLine("width=3", 8));
		attr.addBody(new FileLine("style=dash", 9));
		attr.addBody(new FileLine("fill=outline", 10));
		object.addObject(attr);

		return object;
	}

	/**
	 * 	
	 * @return
	 */
	private ADLWidget setupColorCommentedADLWidget() {
		ADLWidget object = new ADLWidget("basic attribute", null, 5);
		object.addBody(new FileLine("//clr=5", 6));
		object.addBody(new FileLine("width=3", 7));
		object.addBody(new FileLine("style=dash", 8));
		object.addBody(new FileLine("fill=outline", 9));

		return object;
	}

	/**
	 * 	clr line has comma instead of equals
	 * @return
	 */
	private ADLWidget setupBadADLWidget1() {
		ADLWidget object = new ADLWidget("basic attribute", null, 5);
		object.addBody(new FileLine("clr,5", 6));
		object.addBody(new FileLine("width=3", 7));
		object.addBody(new FileLine("style=dash", 8));
		object.addBody(new FileLine("fill=outline", 9));

		return object;
	}

	/**
	 * 	misspell clr  color
	 * @return
	 */
	private ADLWidget setupBadADLWidget2() {
		ADLWidget object = new ADLWidget("basic attribute", null, 5);
		object.addBody(new FileLine("color =5", 6));
		object.addBody(new FileLine("width=3", 7));
		object.addBody(new FileLine("style=dash", 8));
		object.addBody(new FileLine("fill=outline", 9));

		return object;
	}

}
