/**
 * 
 */
package org.csstudio.utility.adlparser.fileParser.widgetParts;

//import com.sun.org.apache.bcel.internal.generic.NEW;

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
			rdItem = new RelatedDisplayItem(ADLTestObjects.setupRelDisp());
			assertTrue( "Test Name ", rdItem.getName().startsWith("display"));
			assertTrue( "Label " + rdItem.getLabel(), rdItem.getLabel().equals("myLabel"));
			assertTrue( "FileName " + rdItem.getFileName(), rdItem.getFileName().equals("myfile.adl"));
			assertTrue( "Args " + rdItem.getArgs(), rdItem.getArgs().equals("\"P=$(P),M=$(M)\""));
			assertTrue( "Policy " + rdItem.getPolicy(), rdItem.getPolicy().equals("replace display"));
		} catch (WrongADLFormatException e) {
			fail("This should Pass");
		}
		try {
			rdItem = new RelatedDisplayItem(ADLTestObjects.setupRelDispNoPolicy());
			assertTrue( "Test Name ", rdItem.getName().startsWith("display"));
			assertTrue( "Label " + rdItem.getLabel(), rdItem.getLabel().equals("\"myLabel\""));
			assertTrue( "FileName " + rdItem.getFileName(), rdItem.getFileName().equals("myfile.adl"));
			assertTrue( "Args " + rdItem.getArgs(), rdItem.getArgs().equals("\"P=iocT1:,M=m1:\""));
			assertFalse( "Policy " + rdItem.getPolicy(), rdItem.getPolicy().equals("replace display"));
		} catch (WrongADLFormatException e) {
			fail("This should Pass");
		}
		try {
			rdItem = new RelatedDisplayItem(ADLTestObjects.setupRelDispEmptyArgs());
			assertTrue( "Test Name ", rdItem.getName().startsWith("display"));
			assertTrue( "Label " + rdItem.getLabel(), rdItem.getLabel().equals("my label"));
			assertTrue( "FileName " + rdItem.getFileName(), rdItem.getFileName().equals("path/myfile.adl"));
			assertTrue( "Args " + rdItem.getArgs(), rdItem.getArgs().equals(""));
			assertFalse( "Policy " + rdItem.getPolicy(), rdItem.getPolicy().equals("replace display"));
		} catch (WrongADLFormatException e) {
			fail("This should Pass");
		}
		try {
			rdItem = new RelatedDisplayItem(ADLTestObjects.setupRelDispNoArgs());
			assertTrue( "Test Name ", rdItem.getName().startsWith("display"));
			assertTrue( "Label " + rdItem.getLabel(), rdItem.getLabel().equals("my label"));
			assertTrue( "FileName " + rdItem.getFileName(), rdItem.getFileName().equals("path/myfile.adl"));
			assertTrue( "Args " + rdItem.getArgs(), rdItem.getArgs().equals(""));
			assertFalse( "Policy " + rdItem.getPolicy(), rdItem.getPolicy().equals("replace display"));
		} catch (WrongADLFormatException e) {
			fail("This should Pass");
		}
		try {
			rdItem = new RelatedDisplayItem(ADLTestObjects.setupRelDispUnkownElement());
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

}
