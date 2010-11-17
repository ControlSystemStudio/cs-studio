/**
 * 
 */
package org.csstudio.utility.adlparser.fileParser.widgetParts;

//import com.sun.org.apache.bcel.internal.generic.NEW;

import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.FileLine;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;

import junit.framework.TestCase;

/**
 * @author hammonds
 *
 */
public class RelatedDisplayItemTest extends TestCase {
	RelatedDisplayItem rdItem = new RelatedDisplayItem();
	
	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.widgetParts.RelatedDisplayItem#RelatedDisplayItem(org.csstudio.utility.adlparser.fileParser.ADLWidget)}.
	 */
	public void testRelatedDisplayItemADLWidget() {
		try {
			rdItem = new RelatedDisplayItem(setupRelDisp());
			assertTrue( "Test Name ", rdItem.getName().startsWith("display"));
			assertTrue( "Label " + rdItem.getLabel(), rdItem.getLabel().equals("myLabel"));
			assertTrue( "FileName " + rdItem.getFileName(), rdItem.getFileName().equals("myfile.adl"));
			assertTrue( "Args " + rdItem.getArgs(), rdItem.getArgs().equals("\"P=$(P),M=$(M)\""));
			assertTrue( "Policy " + rdItem.getPolicy(), rdItem.getPolicy().equals("true"));
		} catch (WrongADLFormatException e) {
			fail("This should Pass");
		}
		try {
			rdItem = new RelatedDisplayItem(setupRelDispUnkownElement());
			fail("This should not Pass");
		} catch (WrongADLFormatException e) {
			// This case should fail
		}
	}

	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.widgetParts.RelatedDisplayItem#RelatedDisplayItem()}.
	 */
	public void testRelatedDisplayItem() {
		assertTrue( "Test Name ", rdItem.getName().equals("display"));
		assertTrue( "Label " + rdItem.getLabel(), rdItem.getLabel().equals(""));
		assertTrue( "FileName " + rdItem.getFileName(), rdItem.getFileName().equals(""));
		assertTrue( "Args " + rdItem.getArgs(), rdItem.getArgs().equals(""));
		assertTrue( "Policy " + rdItem.getPolicy(), rdItem.getPolicy().equals("false"));
	}

	/**
	 * 	Setup a normal control
	 * @return
	 */
	private ADLWidget setupRelDisp() {
		ADLWidget object = new ADLWidget("display[0]", null, 5);
		object.addBody(new FileLine("label=myLabel", 6));
		object.addBody(new FileLine("//", 7));
		object.addBody(new FileLine("name=myfile.adl", 8));
		object.addBody(new FileLine("args=\"P=$(P),M=$(M)\"", 9));
		object.addBody(new FileLine("policy=true", 10));

		return object;
	}

	/**
	 * 	Setup a normal control
	 * @return
	 */
	private ADLWidget setupRelDispUnkownElement() {
		ADLWidget object = new ADLWidget("display[0]", null, 5);
		object.addBody(new FileLine("label=myLabel", 6));
		object.addBody(new FileLine("//", 7));
		object.addBody(new FileLine("name=myfile.adl", 8));
		object.addBody(new FileLine("args=\"P=$(P),M=$(M)\"", 9));
		object.addBody(new FileLine("policy=true", 10));
		object.addBody(new FileLine("xyz=true", 11));

		return object;
	}

}
