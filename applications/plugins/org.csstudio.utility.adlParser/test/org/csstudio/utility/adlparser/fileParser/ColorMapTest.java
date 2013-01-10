/**
 * 
 */
package org.csstudio.utility.adlparser.fileParser;

import junit.framework.TestCase;

import org.eclipse.swt.graphics.RGB;
import org.junit.Test;

/**
 * @author hammonds
 * 
 */
public class ColorMapTest extends TestCase {
	ADLWidget colorMap = new ADLWidget("color map", null, 10);
	ADLWidget badColorMap1 = new ADLWidget("color map", null, 10);
	ADLWidget badColorMap2 = new ADLWidget("color map", null, 10);
	ADLWidget badColorMap3 = new ADLWidget("color map", null, 10);
	ADLWidget badColorMap4 = new ADLWidget("color map", null, 10);


	private void buildColor_ColorMap() {
		colorMap.addBody(new FileLine("ncolors = 6", 11));
		ADLWidget color_ColorMap = new ADLWidget("colors", null, 12);
		colorMap.addObject(color_ColorMap);
		color_ColorMap.addBody(new FileLine("FFFFFF", 13));
		color_ColorMap.addBody(new FileLine("808080", 14));
		color_ColorMap.addBody(new FileLine("000000", 15));
		color_ColorMap.addBody(new FileLine("FF0000", 16));
		color_ColorMap.addBody(new FileLine("00FF00", 17));
		color_ColorMap.addBody(new FileLine("0000FF", 18));
	}

	/**
	 * Bad map example ncolors should have an = number
	 */
	private void buildColor_BadColorMap1() {
		badColorMap1.addBody(new FileLine("ncolors", 11));
		ADLWidget color_ColorMap = new ADLWidget("colors", null, 12);
		badColorMap1.addObject(color_ColorMap);
		color_ColorMap.addBody(new FileLine("FFFFFF", 13));
		color_ColorMap.addBody(new FileLine("808080", 14));
		color_ColorMap.addBody(new FileLine("000000", 15));
		color_ColorMap.addBody(new FileLine("FF0000", 16));
		color_ColorMap.addBody(new FileLine("00FF00", 17));
		color_ColorMap.addBody(new FileLine("0000FF", 18));
	}

	/**
	 * Bad map example Bad color input
	 */
	private void buildColor_BadColorMap2() {
		badColorMap2.addBody(new FileLine("ncolors = 6", 11));
		ADLWidget color_ColorMap = new ADLWidget("colors", null, 12);
		badColorMap2.addObject(color_ColorMap);
		color_ColorMap.addBody(new FileLine("FFFFFF", 13));
		color_ColorMap.addBody(new FileLine("8080ZW", 14));
		color_ColorMap.addBody(new FileLine("000000", 15));
		color_ColorMap.addBody(new FileLine("FF0000", 16));
		color_ColorMap.addBody(new FileLine("00FF00", 17));
		color_ColorMap.addBody(new FileLine("0000FF", 18));
	}

	private void compareColor(String message, RGB color, int red, int green,
			int blue, int intensity) {
		if (intensity > 0){
		}
		assertEquals(message + " Comparing red", color.red, red );
		assertEquals(message + " Comparing green", color.green, green );
		assertEquals(message + " Comparing blue", color.blue, blue );
		
	}

	/**
	 * Test method for
	 * {@link org.csstudio.utility.adlparser.fileParser.ColorMap#ColorMap(org.csstudio.utility.adlparser.fileParser.ADLWidget)}
	 * .
	 */
	@Test
	public void testColor_ColorMap() {
		buildColor_ColorMap();
		try {
			ColorMap colorMap_Inst = new ColorMap(colorMap);
			int nColors = colorMap_Inst.getNumColors();
			assertEquals("Trouble Matching Number of colors", nColors, 6);
			RGB[] colors = colorMap_Inst.getColors();
			String message = "Comparing RGB values for ";
			compareColor(message + "white:", colors[0], 255, 255, 255, -1);
			compareColor(message + "grey:", colors[1], 128, 128, 128, -1);
			compareColor(message + "black:", colors[2], 0, 0, 0, -1);
			compareColor(message + "red:", colors[3], 255, 0, 0, -1);
			compareColor(message + "green:", colors[4], 0, 255, 0, -1);
			compareColor(message + "blue:", colors[5], 0, 0, 255, -1);

		} catch (WrongADLFormatException e) {
			fail("Color_ColorMap has wrong format");
			e.printStackTrace();
		}

		buildColor_BadColorMap1();
		try {
			@SuppressWarnings("unused")
			ColorMap colorMap_Inst = new ColorMap(badColorMap1);
		} catch (WrongADLFormatException e) {
			// OK
		}
		buildColor_BadColorMap2();
		try {
			@SuppressWarnings("unused")
			ColorMap colorMap_Inst = new ColorMap(badColorMap2);
		} catch (WrongADLFormatException e) {
			// OK
		}
	}

	/**
	 * An example of a good color map
	 */
	private void buildDl_Color_ColorMap() {
		colorMap.addBody(new FileLine("ncolors = 6", 11));
		ADLWidget whiteColor = new ADLWidget("dl_color", null, 12);
		whiteColor.addBody(new FileLine("r=255", 13));
		whiteColor.addBody(new FileLine("g=255", 14));
		whiteColor.addBody(new FileLine("b=255", 15));
		whiteColor.addBody(new FileLine("inten=255", 16));
		ADLWidget greyColor = new ADLWidget("dl_color", null, 22);
		greyColor.addBody(new FileLine("r=128", 23));
		greyColor.addBody(new FileLine("g=128", 24));
		greyColor.addBody(new FileLine("b=128", 25));
		greyColor.addBody(new FileLine("inten=128", 26));
		ADLWidget blackColor = new ADLWidget("dl_color", null, 17);
		blackColor.addBody(new FileLine("r=0", 18));
		blackColor.addBody(new FileLine("g=0", 19));
		blackColor.addBody(new FileLine("b=0", 20));
		blackColor.addBody(new FileLine("inten=0", 21));
		ADLWidget redColor = new ADLWidget("dl_color", null, 27);
		redColor.addBody(new FileLine("r=255", 28));
		redColor.addBody(new FileLine("g=0", 29));
		redColor.addBody(new FileLine("b=0", 30));
		redColor.addBody(new FileLine("inten=128", 31));
		ADLWidget greenColor = new ADLWidget("dl_color", null, 32);
		greenColor.addBody(new FileLine("r=0", 33));
		greenColor.addBody(new FileLine("g=255", 34));
		greenColor.addBody(new FileLine("b=0", 35));
		greenColor.addBody(new FileLine("inten=128", 36));
		ADLWidget blueColor = new ADLWidget("dl_color", null, 37);
		blueColor.addBody(new FileLine("r=0", 38));
		blueColor.addBody(new FileLine("b=255", 39));
		blueColor.addBody(new FileLine("g=0", 40));
		blueColor.addBody(new FileLine("inten=128", 41));

		colorMap.addObject(whiteColor);
		colorMap.addObject(greyColor);
		colorMap.addObject(blackColor);
		colorMap.addObject(redColor);
		colorMap.addObject(greenColor);
		colorMap.addObject(blueColor);
	}

	/**
	 * An example of a bad color map. the number of colors is not properly
	 * defined
	 */
	private void buildDl_Color_BadColorMap1() {
		badColorMap1.addBody(new FileLine("ncolors, 6", 11)); // Bad line here comma instead of equals
		ADLWidget whiteColor = new ADLWidget("dl_color", null, 12);
		whiteColor.addBody(new FileLine("r=255", 13));
		whiteColor.addBody(new FileLine("g=255", 14));
		whiteColor.addBody(new FileLine("b=255", 15));
		whiteColor.addBody(new FileLine("inten=255", 16));
		ADLWidget greyColor = new ADLWidget("dl_color", null, 22);
		greyColor.addBody(new FileLine("r=128", 23));
		greyColor.addBody(new FileLine("g=128", 24));
		greyColor.addBody(new FileLine("b=128", 25));
		greyColor.addBody(new FileLine("inten=128", 26));
		ADLWidget blackColor = new ADLWidget("dl_color", null, 17);
		blackColor.addBody(new FileLine("r=0", 18));
		blackColor.addBody(new FileLine("g=0", 19));
		blackColor.addBody(new FileLine("b=0", 20));
		blackColor.addBody(new FileLine("inten=0", 21));
		ADLWidget redColor = new ADLWidget("dl_color", null, 27);
		redColor.addBody(new FileLine("r=255", 28));
		redColor.addBody(new FileLine("g=0", 29));
		redColor.addBody(new FileLine("b=0", 30));
		redColor.addBody(new FileLine("inten=128", 31));
		ADLWidget greenColor = new ADLWidget("dl_color", null, 32);
		greenColor.addBody(new FileLine("r=0", 33));
		greenColor.addBody(new FileLine("g=255", 34));
		greenColor.addBody(new FileLine("b=0", 35));
		greenColor.addBody(new FileLine("inten=128", 36));
		ADLWidget blueColor = new ADLWidget("dl_color", null, 37);
		blueColor.addBody(new FileLine("r=0", 38));
		blueColor.addBody(new FileLine("b=255", 39));
		blueColor.addBody(new FileLine("g=0", 40));
		blueColor.addBody(new FileLine("inten=128", 41));

		badColorMap1.addObject(whiteColor);
		badColorMap1.addObject(greyColor);
		badColorMap1.addObject(blackColor);
		badColorMap1.addObject(redColor);
		badColorMap1.addObject(greenColor);
		badColorMap1.addObject(blueColor);
	}

	/**
	 * An example of a bad color map. Green is out of bounds on greyColor
	 */
	private void buildDl_Color_BadColorMap2() {
		badColorMap2.addBody(new FileLine("ncolors = 6", 11));
		ADLWidget whiteColor = new ADLWidget("dl_color", null, 12);
		whiteColor.addBody(new FileLine("r=255", 13));
		whiteColor.addBody(new FileLine("g=255", 14));
		whiteColor.addBody(new FileLine("b=255", 15));
		whiteColor.addBody(new FileLine("inten=255", 16));
		ADLWidget greyColor = new ADLWidget("dl_color", null, 22);
		greyColor.addBody(new FileLine("r=128", 23));
		greyColor.addBody(new FileLine("g=723", 24));   //Bad line is here value is too big
		greyColor.addBody(new FileLine("b=128", 25));
		greyColor.addBody(new FileLine("inten=128", 26));
		ADLWidget blackColor = new ADLWidget("dl_color", null, 17);
		blackColor.addBody(new FileLine("r=0", 18));
		blackColor.addBody(new FileLine("g=0", 19));
		blackColor.addBody(new FileLine("b=0", 20));
		blackColor.addBody(new FileLine("inten=0", 21));
		ADLWidget redColor = new ADLWidget("dl_color", null, 27);
		redColor.addBody(new FileLine("r=255", 28));
		redColor.addBody(new FileLine("g=0", 29));
		redColor.addBody(new FileLine("b=0", 30));
		redColor.addBody(new FileLine("inten=128", 31));
		ADLWidget greenColor = new ADLWidget("dl_color", null, 32);
		greenColor.addBody(new FileLine("r=0", 33));
		greenColor.addBody(new FileLine("g=255", 34));
		greenColor.addBody(new FileLine("b=0", 35));
		greenColor.addBody(new FileLine("inten=128", 36));
		ADLWidget blueColor = new ADLWidget("dl_color", null, 37);
		blueColor.addBody(new FileLine("r=0", 38));
		blueColor.addBody(new FileLine("b=255", 39));
		blueColor.addBody(new FileLine("g=0", 40));
		blueColor.addBody(new FileLine("inten=128", 41));

		badColorMap2.addObject(whiteColor);
		badColorMap2.addObject(greyColor);
		badColorMap2.addObject(blackColor);
		badColorMap2.addObject(redColor);
		badColorMap2.addObject(greenColor);
		badColorMap2.addObject(blueColor);
	}

	/**
	 * An example of a bad map.  an RGB assignment is wrong in black
	 */
	private void buildDl_Color_BadColorMap3() {
		badColorMap3.addBody(new FileLine("ncolors = 6", 11));
		ADLWidget whiteColor = new ADLWidget("dl_color", null, 12);
		whiteColor.addBody(new FileLine("r=255", 13));
		whiteColor.addBody(new FileLine("g=255", 14));
		whiteColor.addBody(new FileLine("b=255", 15));
		whiteColor.addBody(new FileLine("inten=255", 16));
		ADLWidget greyColor = new ADLWidget("dl_color", null, 22);
		greyColor.addBody(new FileLine("r=128", 23));
		greyColor.addBody(new FileLine("g=128", 24));
		greyColor.addBody(new FileLine("b=128", 25));
		greyColor.addBody(new FileLine("inten=128", 26));
		ADLWidget blackColor = new ADLWidget("dl_color", null, 17);
		blackColor.addBody(new FileLine("r=0", 18));
		blackColor.addBody(new FileLine("g,0", 19)); //Bad line here comma instead of equals
		blackColor.addBody(new FileLine("b=0", 20));
		blackColor.addBody(new FileLine("inten=0", 21));
		ADLWidget redColor = new ADLWidget("dl_color", null, 27);
		redColor.addBody(new FileLine("r=255", 28));
		redColor.addBody(new FileLine("g=0", 29));
		redColor.addBody(new FileLine("b=0", 30));
		redColor.addBody(new FileLine("inten=128", 31));
		ADLWidget greenColor = new ADLWidget("dl_color", null, 32);
		greenColor.addBody(new FileLine("r=0", 33));
		greenColor.addBody(new FileLine("g=255", 34));
		greenColor.addBody(new FileLine("b=0", 35));
		greenColor.addBody(new FileLine("inten=128", 36));
		ADLWidget blueColor = new ADLWidget("dl_color", null, 37);
		blueColor.addBody(new FileLine("r=0", 38));
		blueColor.addBody(new FileLine("b=255", 39));
		blueColor.addBody(new FileLine("g=0", 40));
		blueColor.addBody(new FileLine("inten=128", 41));

		badColorMap3.addObject(whiteColor);
		badColorMap3.addObject(greyColor);
		badColorMap3.addObject(blackColor);
		badColorMap3.addObject(redColor);
		badColorMap3.addObject(greenColor);
		badColorMap3.addObject(blueColor);
	}

	/**
	 * An example of a good color map
	 */
	private void buildDl_Color_BadColorMap4() {
		badColorMap4.addBody(new FileLine("ncolors = 6", 11));
		ADLWidget whiteColor = new ADLWidget("dl_color", null, 12);
		whiteColor.addBody(new FileLine("r=255", 13));
		whiteColor.addBody(new FileLine("g=255", 14));
		whiteColor.addBody(new FileLine("b=255", 15));
		whiteColor.addBody(new FileLine("inten=255", 16));
		ADLWidget greyColor = new ADLWidget("dl_color", null, 22);
		greyColor.addBody(new FileLine("r=128", 23));
		greyColor.addBody(new FileLine("g=A", 24));  //Bad line here value after equals is invalid
		greyColor.addBody(new FileLine("b=128", 25));
		greyColor.addBody(new FileLine("inten=128", 26));
		ADLWidget blackColor = new ADLWidget("dl_color", null, 17);
		blackColor.addBody(new FileLine("r=0", 18));
		blackColor.addBody(new FileLine("g=0", 19));
		blackColor.addBody(new FileLine("b=0", 20));
		blackColor.addBody(new FileLine("inten=0", 21));
		ADLWidget redColor = new ADLWidget("dl_color", null, 27);
		redColor.addBody(new FileLine("r=255", 28));
		redColor.addBody(new FileLine("g=0", 29));
		redColor.addBody(new FileLine("b=0", 30));
		redColor.addBody(new FileLine("inten=128", 31));
		ADLWidget greenColor = new ADLWidget("dl_color", null, 32);
		greenColor.addBody(new FileLine("r=0", 33));
		greenColor.addBody(new FileLine("g=255", 34));
		greenColor.addBody(new FileLine("b=0", 35));
		greenColor.addBody(new FileLine("inten=128", 36));
		ADLWidget blueColor = new ADLWidget("dl_color", null, 37);
		blueColor.addBody(new FileLine("r=0", 38));
		blueColor.addBody(new FileLine("b=255", 39));
		blueColor.addBody(new FileLine("g=0", 40));
		blueColor.addBody(new FileLine("inten=128", 41));

		badColorMap4.addObject(whiteColor);
		badColorMap4.addObject(greyColor);
		badColorMap4.addObject(blackColor);
		badColorMap4.addObject(redColor);
		badColorMap4.addObject(greenColor);
		badColorMap4.addObject(blueColor);
	}

	/**
	 * Test method for
	 * {@link org.csstudio.utility.adlparser.fileParser.ColorMap#ColorMap(org.csstudio.utility.adlparser.fileParser.ADLWidget)}
	 * .
	 */
	@Test
	public void testDl_color_ColorMap() {
		// A proper color map with elements in dl_color form
		buildDl_Color_ColorMap();
		try {
			ColorMap colorMap_Inst = new ColorMap(colorMap);
			int nColors = colorMap_Inst.getNumColors();
			assertEquals("Trouble Matching Number of colors", nColors, 6);
			RGB[] colors = colorMap_Inst.getColors();
			String message = "Comparing RGB values for ";
			compareColor(message + "white:", colors[0], 255, 255, 255, -1);
			compareColor(message + "grey:" , colors[1], 128, 128, 128, -1);
			compareColor(message + "black:", colors[2], 0, 0, 0, -1);
			compareColor(message + "red:", colors[3], 255, 0, 0, -1);
			compareColor(message + "green:", colors[4], 0, 255, 0, -1);
			compareColor(message + "blue:", colors[5], 0, 0, 255, -1);
		} catch (WrongADLFormatException e) {
			e.printStackTrace();
			fail("Wrong format chosen for test colorMap");
		}
		
		//A bad color map.  Number of colors not defined properly
		buildDl_Color_BadColorMap1();
		try {
			@SuppressWarnings("unused")
			ColorMap colorMap_Inst = new ColorMap(badColorMap1);
			fail("This is a bad map should fail");
		} catch (WrongADLFormatException e) {
			// OK
		}

		// A bad map.  One of the colors has a value that is too large
		buildDl_Color_BadColorMap2();
		try {
			@SuppressWarnings("unused")
			ColorMap colorMap_Inst = new ColorMap(badColorMap2);
			fail("This is a bad map should fail");
		} catch (WrongADLFormatException e) {
			// OK
		}

		// A bad map.  One of the colors is not properly defined
		buildDl_Color_BadColorMap3();
		try {
			@SuppressWarnings("unused")
			ColorMap colorMap_Inst = new ColorMap(badColorMap3);
			fail("This is a bad map should fail");
		} catch (WrongADLFormatException e) {
			// OK
		}

		// A bad map.  One of the colors has a non int value
		buildDl_Color_BadColorMap4();
		try {
			@SuppressWarnings("unused")
			ColorMap colorMap_Inst = new ColorMap(badColorMap4);
			fail("This is a bad map should fail");
		} catch (WrongADLFormatException e) {
			// OK
		}

	}

	/**
	 * Test method for
	 * {@link org.csstudio.utility.adlparser.fileParser.ColorMap#getRGBColor(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetRGBColor() {
		testColor("FF0000", "Red", 255, 0, 0);
		testColor("00FF00", "Green", 0, 255, 0);
		testColor("0000FF", "Blue", 0, 0, 255);
		testColor("FFFFFF", "White", 255, 255, 255);
		testColor("000000", "Black", 0, 0, 0);
		testColor("808080", "Grey", 128, 128, 128);
		try {
			testColor("FFF", "Don't Know", 1, 1, 1);
			fail("This should fail since it makes no sense");
		} catch (IllegalArgumentException ex) {
			// OK
		}
		try {
			testColor("FFFFFFF", "Don't Know", 1, 1, 1);
			fail("This should fail since it makes no sense");
		} catch (IllegalArgumentException ex) {
			// OK
		}
		try {
			testColor("FDEGZS", "Don't Know", 1, 1, 1);
			fail("Each pair should be hex that transforms to an integer");
		} catch (IllegalArgumentException ex) {
			// OK
		}

	}

	private void testColor(String rgbTriad, String colorName, int redVal,
			int greenVal, int blueVal) {
		RGB color = ColorMap.getRGBColor(rgbTriad);
		assertEquals("Test " + colorName + " cblack red value", color.red,
				redVal);
		assertEquals("Test " + colorName + " green value", color.green,
				greenVal);
		assertEquals("Test " + colorName + " blue value", color.blue, blueVal);

	}

}
