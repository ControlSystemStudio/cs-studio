/**
 * 
 */
package org.csstudio.utility.adlparser.fileParser;

import java.util.ArrayList;

import junit.framework.TestCase;

/**
 * @author hammonds
 *
 */
public class ADLWidgetTest extends TestCase {
	private String rootType = "root_widget";
	private String new_root = "new_root";
	ADLWidget root = new ADLWidget(rootType, null, 0);
	/**
	 * @throws java.lang.Exception
	 */
	protected void setUp() throws Exception {
	}

	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.ADLWidget#ADLWidget(java.lang.String, org.csstudio.utility.adlparser.fileParser.ADLWidget, int)}.
	 */
	public void testADLWidget() {
		assertTrue(root.getType().equals(rootType));
		assertTrue(root.getParent() == null);
		assertTrue(root.getObjectNr() == 0);
		
		//test stripping quotes
		root = new ADLWidget("\"" + rootType + "\"", null, 0);
		assertTrue("Try removing quotes", root.getType().equals(rootType));
		
		//test stripping brace
		root = new ADLWidget("{" + rootType, null, 0);
		assertTrue("Try removing brace", root.getType().equals(rootType));
		
		//test stripping slash
//		System.out.println("\\" + rootType + "\\");
//		root = new ADLWidget("\\" + rootType + "\\", null, 0);
//		assertTrue("Try removing slash " + root.getType(), root.getType().equals(rootType));
		
	}

	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.ADLWidget#setType(java.lang.String)}.
	 */
	public void testSetType() {
		root.setType(new_root);
		assertTrue("Change type " + root.getType(), root.getType().equals(new_root));
		
		//Add with slashes
		root.setType("\"" + new_root + "\"");
		assertTrue("Change with slashes " + root.getType(), root.getType().equals(new_root));
		
		//Add with brace
		root.setType("{" + new_root);
		assertTrue("Change type with brace " + root.getType(), root.getType().equals(new_root));

		//Add with brace
//		root.setType("\\" + new_root + "\\");
//		assertTrue("Change type " + root.getType(), root.getType().equals(new_root));
	}

	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.ADLWidget#addBody(org.csstudio.utility.adlparser.fileParser.FileLine)}.
	 */
	public void testAddBody() {
		buildWidgetTree();
		
		ArrayList<FileLine> rootLines = root.getBody();
		assertEquals("Root line should equal 3", rootLines.size(), 3);
		FileLine rootLine1 = rootLines.get(0);
		assertTrue("Compare rootLine1 Line", rootLine1.getLine().equals("Test Line root 1"));
		assertEquals("Check Line Number for rootLine1", rootLine1.getLineNumber(), 1);
		FileLine rootLine2 = rootLines.get(1);
		assertTrue("Compare rootLine2 Line", rootLine2.getLine().equals("Test Line root 2"));
		assertEquals("Check Line Number for rootLine2", rootLine1.getLineNumber(), 1);
		FileLine rootLine3 = rootLines.get(2);
		assertTrue("Compare rootLine3 Line", rootLine3.getLine().equals("Test Line root 3"));
		assertEquals("Check Line Number for rootLine13", rootLine3.getLineNumber(), 3);
		
		ArrayList<FileLine> level1_1Lines = root.getObjects().get(0).getBody();
		assertEquals("level1_1 line should equal 2", level1_1Lines.size(), 2);
		
		FileLine level1_1Line1 = level1_1Lines.get(0);
		assertTrue("Compare level1_1Line1 Line", level1_1Line1.getLine().equals("Test Line level1_1 1"));
		assertEquals("Check Line Number for level1_1Line1", level1_1Line1.getLineNumber(), 4);

		FileLine level1_1Line2 = level1_1Lines.get(1);
		assertTrue("Compare level1_1Line2 Line", level1_1Line2.getLine().equals("Test Line level1_1 2"));
		assertEquals("Check Line Number for level1_1Line2", level1_1Line2.getLineNumber(), 5);
		
		ArrayList<FileLine> level1_2Lines = root.getObjects().get(1).getBody();
		assertEquals("level1_2 line should equal 4", level1_2Lines.size(), 4);
		
		FileLine level1_2Line1 = level1_2Lines.get(0);
		assertTrue("Compare level1_2Line1 Line", level1_2Line1.getLine().equals("Test Line level1_2 1"));
		assertEquals("Check Line Number for level1_2Line1", level1_2Line1.getLineNumber(), 6);

		FileLine level1_2Line2 = level1_2Lines.get(1);
		assertTrue("Compare level1_2Line2 Line", level1_2Line2.getLine().equals("Test Line level1_2 2"));
		assertEquals("Check Line Number for level1_2Line2", level1_2Line2.getLineNumber(), 7);
		
		FileLine level1_2Line3 = level1_2Lines.get(2);
		assertTrue("Compare level1_2Line3 Line", level1_2Line3.getLine().equals("Test Line level1_2 3"));
		assertEquals("Check Line Number for level1_2Line3", level1_2Line3.getLineNumber(), 8);

		FileLine level1_2Line4 = level1_2Lines.get(3);
		assertTrue("Compare level1_2Line4 Line", level1_2Line4.getLine().equals("Test Line level1_2 4"));
		assertEquals("Check Line Number for level1_2Line4", level1_2Line4.getLineNumber(), 9);
	}

	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.ADLWidget#addObject(org.csstudio.utility.adlparser.fileParser.ADLWidget)}.
	 */
	public void testAddObject() {
		buildWidgetTree();
		ArrayList<ADLWidget> rootList = root.getObjects();
		assertEquals("Test level 1 has only 2 widgets", rootList.size(), 2);
		
		// Inspect the first widget at level 1
		ADLWidget level1_1 = rootList.get(0);
		assertTrue("Test type of 1_1", level1_1.isType("level1_1"));
//		assertEquals("Check Parent of " + level1_1.getType(), level1_1.getParent(), root);
		assertEquals("Check line number of " + level1_1.getType(), level1_1.getObjectNr(), 1);
		// Test second tier under 1_1
		ArrayList<ADLWidget> level1_1_List = level1_1.getObjects();
		assertEquals("Test level 1 widget 1 has only 2 widgets", level1_1_List.size(), 2);
		ADLWidget level2_1 = level1_1_List.get(0);
		assertTrue("Test type of 2_1", level2_1.isType("level2_1"));
//		assertEquals("Check Parent of " + level2_1.getType(), level2_1.getParent(), level1_1);
		assertEquals("Check line number of " + level2_1.getType(), level2_1.getObjectNr(), 2);
		ADLWidget level2_2 = level1_1_List.get(1);
		assertTrue("Test type of 2_2", level2_2.isType("level2_2"));
//		assertEquals("Check Parent of " + level2_2.getType(), level2_2.getParent(), level1_1);
		assertEquals("Check line number of " + level2_2.getType(), level2_2.getObjectNr(), 3);
		
		ADLWidget level1_2 = rootList.get(1);
		assertTrue("Test type of 1_1", level1_2.isType("level1_2"));
//		assertEquals("Check Parent of " + level1_2.getType(), level1_2.getParent(), level1_2);
		assertEquals("Check line number of " + level1_2.getType(), level1_2.getObjectNr(), 4);
		ArrayList<ADLWidget> level1_2_List = level1_2.getObjects();
		assertEquals("Test level 1 widget 1 has only 2 widgets", level1_2_List.size(), 3);
		ADLWidget level2_3 = level1_2_List.get(0);
		assertTrue("Test type of 2_3", level2_3.isType("level2_3"));
//		assertEquals("Check Parent of " + level2_3.getType(), level2_3.getParent(), level1_2);
		assertEquals("Check line number of " + level2_3.getType(), level2_3.getObjectNr(), 5);
		ADLWidget level2_4 = level1_2_List.get(1);
		assertTrue("Test type of 2_4", level2_4.isType("level2_4"));
//		assertEquals("Check Parent of " + level2_4.getType(), level2_4.getParent(), level1_2);
		assertEquals("Check line number of " + level2_4.getType(), level2_4.getObjectNr(), 6);
		ADLWidget level2_5 = level1_2_List.get(2);
		assertTrue("Test type of 2_5", level2_5.isType("level2_5"));
//		assertEquals("Check Parent of " + level2_5.getType(), level2_5.getParent(), level1_2);
		assertEquals("Check line number of " + level2_5.getType(), level2_5.getObjectNr(), 7);
		
	}

	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.ADLWidget#isType(java.lang.String)}.
	 */
	public void testIsType() {
		assertTrue("Test type of widget from setUp", root.isType(rootType));
		root.setType(new_root);
		assertTrue("Change type " + root.getType(), root.isType(new_root));
		
		//Add with slashes
		root.setType("\"" + new_root + "\"");
		assertTrue("Change with slashes " + root.getType(), root.isType(new_root));
		
		//Add with brace
		root.setType("{" + new_root);
		assertTrue("Change type with brace " + root.getType(), root.isType(new_root));

		//Add with brace
//		root.setType("\\" + new_root + "\\");
//		assertTrue("Change type " + root.getType(), root.root.isType(new_root));
	}

	/**
	 * Test method for {@link org.csstudio.utility.adlparser.fileParser.ADLWidget#toString()}.
	 */
	public void testToString() {
		buildWidgetTree();
		
		String output = root.toString();
		
		
	}

	/**
	 * Helper method to populate a tree for testing
	 */
	private void buildWidgetTree() {
		root.addBody(new FileLine("Test Line root 1", 1));
		root.addBody(new FileLine("Test Line root 2", 2));
		root.addBody(new FileLine("Test Line root 3", 3));
		ADLWidget level1_1 = new ADLWidget("level1_1", null, 1);
		ADLWidget level2_1 = new ADLWidget("level2_1", null, 2);
		ADLWidget level2_2 = new ADLWidget("level2_2", null, 3);
		ADLWidget level1_2 = new ADLWidget("level1_2", null, 4);
		ADLWidget level2_3 = new ADLWidget("level2_3", null, 5);
		ADLWidget level2_4 = new ADLWidget("level2_4", null, 6);
		ADLWidget level2_5 = new ADLWidget("level2_5", null, 7);
		
		root.addObject(level1_1);
		root.addObject(level1_2);
		level1_1.addObject(level2_1);
		level1_1.addObject(level2_2);
		level1_1.addBody(new FileLine("Test Line level1_1 1", 4));
		level1_1.addBody(new FileLine("Test Line level1_1 2", 5));
		level1_2.addObject(level2_3);
		level1_2.addObject(level2_4);
		level1_2.addObject(level2_5);
		level1_2.addBody(new FileLine("Test Line level1_2 1", 6));
		level1_2.addBody(new FileLine("Test Line level1_2 2", 7));
		level1_2.addBody(new FileLine("Test Line level1_2 3", 8));
		level1_2.addBody(new FileLine("Test Line level1_2 4", 9));
	}


}