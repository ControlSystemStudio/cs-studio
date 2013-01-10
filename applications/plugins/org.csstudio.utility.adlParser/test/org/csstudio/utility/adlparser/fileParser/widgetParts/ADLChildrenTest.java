package org.csstudio.utility.adlparser.fileParser.widgetParts;

import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;

import junit.framework.TestCase;

public class ADLChildrenTest extends TestCase {
	ADLChildren children = new ADLChildren();
	
	public void testADLChildrenADLWidget() {
		try {
			children = new ADLChildren(setupChildren());
		} catch (WrongADLFormatException e) {
			fail("This should work");
		}
		try {
			children = new ADLChildren(setupEmptyChildren());
		} catch (WrongADLFormatException e) {
			fail("This should work");
		}
		try {
			children = new ADLChildren(setupNotChildren());
			fail("This should not work");
		} catch (WrongADLFormatException e) {
			// OK
		}
	}

	public void testADLChildren() {
		assertNotNull("Children should be an empty list not null", children.getAdlChildrens());
	}

	public ADLWidget setupChildren(){
		ADLWidget widget = new ADLWidget("children", null, 0);
		widget.addObject(new ADLWidget("text", null, 1));
		widget.addObject(new ADLWidget("meter", null, 1));
		widget.addObject(new ADLWidget("menu", null, 1));
		return widget;
	}
	public ADLWidget setupEmptyChildren(){
		ADLWidget widget = new ADLWidget("children", null, 0);
		return widget;
	}
	public ADLWidget setupNotChildren(){
		ADLWidget widget = new ADLWidget("notchildren", null, 0);
		widget.addObject(new ADLWidget("text", null, 1));
		widget.addObject(new ADLWidget("meter", null, 1));
		widget.addObject(new ADLWidget("menu", null, 1));
		return widget;
	}
}
