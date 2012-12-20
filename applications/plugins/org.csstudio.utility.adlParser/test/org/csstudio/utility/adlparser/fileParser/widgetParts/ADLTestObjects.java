/*************************************************************************\
 * Copyright (c) 2010  UChicago Argonne, LLC
 * This file is distributed subject to a Software License Agreement found
 * in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.utility.adlparser.fileParser.widgetParts;

import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.FileLine;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;
import org.csstudio.utility.adlparser.fileParser.widgets.ADLDisplay;
import org.eclipse.swt.graphics.RGB;

/**
 * Just a bunch of static methods that produce ADLWidgets that can be used in
 * testing.
 * 
 * @author hammonds
 * 
 */
public class ADLTestObjects {

	/**
	 * Setup a normal control
	 * 
	 * @return
	 */
	public static ADLWidget setupRelDispUnkownElement() {
		ADLWidget object = new ADLWidget("display[0]", null, 5);
		object.addBody(new FileLine("label=myLabel", 6));
		object.addBody(new FileLine("//", 7));
		object.addBody(new FileLine("name=myfile.adl", 8));
		object.addBody(new FileLine("args=\"P=$(P),M=$(M)\"", 9));
		object.addBody(new FileLine("policy=replace display", 10));
		object.addBody(new FileLine("xyz=true", 11));

		return object;
	}

	public static ADLWidget setupRelDisp() {
		ADLWidget object = new ADLWidget("display[1]", null, 5);
		object.addBody(new FileLine("label=myLabel", 6));
		object.addBody(new FileLine("//", 7));
		object.addBody(new FileLine("name=myfile.adl", 8));
		object.addBody(new FileLine("args=\"P=$(P),M=$(M)\"", 9));
		object.addBody(new FileLine("policy=\"replace display\"", 10));

		return object;
	}

	/**
	 * Setup a normal control
	 * 
	 * @return
	 */
	public static ADLWidget setupRelDispNoPolicy() {
		ADLWidget object = new ADLWidget("display[2]", null, 5);
		object.addBody(new FileLine("label=\"myLabel\"", 6));
		object.addBody(new FileLine("//", 7));
		object.addBody(new FileLine("name=myfile.adl", 8));
		object.addBody(new FileLine("args=\"P=iocT1:,M=m1:\"", 9));

		return object;
	}

	/**
	 * Setup a normal control
	 * 
	 * @return
	 */
	public static ADLWidget setupRelDispMixedArgs() {
		ADLWidget object = new ADLWidget("display[0]", null, 5);
		object.addBody(new FileLine("label=my label", 6));
		object.addBody(new FileLine("//", 7));
		object.addBody(new FileLine("name=path/myfile.adl", 8));
		object.addBody(new FileLine("args=\"P=$(P),M=$(M),T=temp:,PREC=3\"", 9));

		return object;
	}

	/**
	 * Setup a display with no arguments
	 * 
	 * @return
	 */
	public static ADLWidget setupRelDispEmptyArgs() {
		ADLWidget object = new ADLWidget("display[0]", null, 5);
		object.addBody(new FileLine("label=my label", 6));
		object.addBody(new FileLine("name=path/myfile.adl", 8));
		object.addBody(new FileLine("args=", 9));

		return object;
	}

	/**
	 * Setup a display with no arguments
	 * 
	 * @return
	 */
	public static ADLWidget setupRelDispNoArgs() {
		ADLWidget object = new ADLWidget("display[0]", null, 5);
		object.addBody(new FileLine("label=my label", 6));
		object.addBody(new FileLine("name=path/myfile.adl", 8));

		return object;
	}

	public static RelatedDisplayItem makeRelatedDisplay1() {
		RelatedDisplayItem rd = null;
		try {
			rd = new RelatedDisplayItem(setupRelDisp());
		} catch (WrongADLFormatException e) {
			// We should not hit here since we are starting with a well known
			// object
			e.printStackTrace();
		}
		return rd;
	}

	public static RelatedDisplayItem makeRelatedDisplayNoPolicy() {
		RelatedDisplayItem rd = null;
		try {
			rd = new RelatedDisplayItem(setupRelDispNoPolicy());
		} catch (WrongADLFormatException e) {
			// We should not hit here since we are starting with a well known
			// object
			e.printStackTrace();
		}
		return rd;
	}

	public static RelatedDisplayItem makeRelatedDisplayMixedArgs() {
		RelatedDisplayItem rd = null;
		try {
			rd = new RelatedDisplayItem(setupRelDispMixedArgs());
		} catch (WrongADLFormatException e) {
			// We should not hit here since we are starting with a well known
			// object
			e.printStackTrace();
		}
		return rd;
	}

	public static RelatedDisplayItem makeRelatedDisplayNoArgs() {
		RelatedDisplayItem rd = null;
		try {
			rd = new RelatedDisplayItem(setupRelDispNoArgs());
		} catch (WrongADLFormatException e) {
			// We should not hit here since we are starting with a well known
			// object
			e.printStackTrace();
		}
		return rd;
	}

	public static RelatedDisplayItem makeRelatedDisplayEmptyArgs() {
		RelatedDisplayItem rd = null;
		try {
			rd = new RelatedDisplayItem(setupRelDispEmptyArgs());
		} catch (WrongADLFormatException e) {
			// We should not hit here since we are starting with a well known
			// object
			e.printStackTrace();
		}
		return rd;
	}

	public static ADLWidget setupBasicFile() {
		ADLWidget object = new ADLWidget("file", null, 0);
		object.addBody(new FileLine("name=testFile.adl", 1));
		object.addBody(new FileLine("version", 6));
		object.addObject(setupBasicDisplay());
		object.addObject(setupBasicColorMap());

		return object;
	}

	private static ADLWidget setupBasicColorMap() {
		ADLWidget colorMap = new ADLWidget("color map", null, 10);
		colorMap.addBody(new FileLine("ncolors = 6", 11));
		ADLWidget color_ColorMap = new ADLWidget("colors", null, 12);
		colorMap.addObject(color_ColorMap);
		color_ColorMap.addBody(new FileLine("FFFFFF", 13));
		color_ColorMap.addBody(new FileLine("808080", 14));
		color_ColorMap.addBody(new FileLine("000000", 15));
		color_ColorMap.addBody(new FileLine("FF0000", 16));
		color_ColorMap.addBody(new FileLine("00FF00", 17));
		color_ColorMap.addBody(new FileLine("0000FF", 18));
		return colorMap;
	}

	public static ADLWidget setupBasicDisplay() {
		ADLWidget display = new ADLWidget("display", null, 4);
		ADLWidget object = new ADLWidget("object", null, 5);
		object.addBody(new FileLine("x=81", 6));
		object.addBody(new FileLine("y=107", 7));
		object.addBody(new FileLine("width=1020", 8));
		object.addBody(new FileLine("height=610", 9));
		display.addObject(object);
		display.addBody(new FileLine("clr=2", 10));
		display.addBody(new FileLine("bclr=4", 11));
		display.addBody(new FileLine("cmap=\"\"", 12));
		display.addBody(new FileLine("gridSpacing=5", 13));
		display.addBody(new FileLine("gridOn=0", 14));
		display.addBody(new FileLine("snapToGrid=0", 15));

		return display;
	}

	public static ADLDisplay makeBasicDisplay() {
		ADLDisplay adlDisp = new ADLDisplay(setupBasicDisplay());
		return adlDisp;
	}
	
	public static ADLWidget setupRelatedDisplayWidget() {
		ADLWidget widget = new ADLWidget("related display", null, 0);
		ADLWidget object = new ADLWidget("object", null, 5);
		object.addBody(new FileLine("x=81", 6));
		object.addBody(new FileLine("y=107", 7));
		object.addBody(new FileLine("width=1020", 8));
		object.addBody(new FileLine("height=610", 9));
		widget.addObject(object);
		widget.addBody(new FileLine("clr=3", 10));
		widget.addBody(new FileLine("bclr=5", 11));
		widget.addBody(new FileLine("label=test_entry", 11));
		widget.addObject(setupRelDispMixedArgs());
		widget.addObject(setupRelDisp());

		return widget;
	}

	/**
	 * Convenience method to create a colorMap
	 * 
	 * @return the colorMap
	 */
	public static RGB[] makeColorMap() {
		RGB[] map = new RGB[6];
		map[0] = new RGB(255, 255, 255);
		map[1] = new RGB(128, 128, 128);
		map[2] = new RGB(0, 0, 0);
		map[3] = new RGB(255, 0, 0);
		map[4] = new RGB(0, 255, 0);
		map[5] = new RGB(0, 0, 255);
		return map;
	}

	public static ADLWidget setupBasicOval1() {
		ADLWidget line = new ADLWidget("oval", null, 4);
		return line;
	}

	public static ADLWidget setupBasicOval2() {
		ADLWidget line = new ADLWidget("oval", null, 4);
		return line;
	}

	public static ADLWidget setupBasicOval3() {
		ADLWidget line = new ADLWidget("oval", null, 4);
		return line;
	}

	public static ADLWidget setupBasicAttributes1(ADLWidget parent) {
		ADLWidget baParts = new ADLWidget("basic attribute", parent, 0);
		baParts.addBody(new FileLine("clr=2", 10));
		baParts.addBody(new FileLine("width=3", 10));
		baParts.addBody(new FileLine("style=solid", 10));
		baParts.addBody(new FileLine("fill=solid", 10));
		return baParts;
	}

	public static ADLWidget setupBasicAttributes2(ADLWidget parent) {
		ADLWidget baParts = new ADLWidget("basic attribute", parent, 0);
		baParts.addBody(new FileLine("clr=4", 10));
		baParts.addBody(new FileLine("width=3", 10));
		baParts.addBody(new FileLine("style=solid", 10));
		baParts.addBody(new FileLine("fill=solid", 10));
		return baParts;
	}

	public static ADLWidget setupBasicAttributesOld1(ADLWidget parent) {
		ADLWidget attr = new ADLWidget("attr", null, 0);

		attr.addBody(new FileLine("clr=2", 10));
		attr.addBody(new FileLine("width=3", 10));
		attr.addBody(new FileLine("style=solid", 10));
		attr.addBody(new FileLine("fill=solid", 10));
		return attr;
	}

	public static ADLWidget setupBasicAttributesOld2(ADLWidget parent) {
		ADLWidget attr = new ADLWidget("attr", null, 0);

		attr.addBody(new FileLine("clr=4", 10));
		attr.addBody(new FileLine("width=3", 11));
		attr.addBody(new FileLine("style=solid", 12));
		attr.addBody(new FileLine("fill=solid", 13));
		return attr;
	}

	public static String getColorName(int index) {
		String name = null;
		if (index > 2) {
			if (index == 3) {
				name = "Red";
			} else if (index == 4) {
				name = "Green";
			} else if (index == 5) {
				name = "Blue";
			} else {
				name = "Unknown";
			}
		} else {
			RGB rgb = makeColorMap()[index];
			name = "(" + rgb.red + "," + rgb.green + "," + rgb.blue + ")";
		}
		return name;
	}

	public static RGB getRGBValue(int index) {
		return makeColorMap()[index];
	}

}
