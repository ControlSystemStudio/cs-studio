/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLTestObjects;
import org.eclipse.swt.graphics.RGB;

import junit.framework.TestCase;

/**
 * @author hammonds
 *
 */
public class TranslatorUtilsUiPluginTest extends TestCase {
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.csstudio.opibuilder.adl2boy.translator.TranslatorUtils#getColorMap(org.csstudio.utility.adlparser.fileParser.ADLWidget)}.
	 */
	public void testGetColorMap() {
		RGB[] colorTable = TranslatorUtils.getColorMap(ADLTestObjects.setupBasicFile());
		assertEquals( "colorTable # colors ", 6, colorTable.length);
		assertEquals( "colorTable[0]", new RGB(255,255,255), colorTable[0]);
		assertEquals( "colorTable[1]", new RGB(128,128,128), colorTable[1]);
		assertEquals( "colorTable[2]", new RGB(0,0,0), colorTable[2]);
		assertEquals( "colorTable[3]", new RGB(255,0,0), colorTable[3]);
		assertEquals( "colorTable[4]", new RGB(0,255,0), colorTable[4]);
		assertEquals( "colorTable[5]", new RGB(0,0,255), colorTable[5]);

	}

	/**
	 * Test method for {@link org.csstudio.opibuilder.adl2boy.translator.TranslatorUtils#initializeDisplayModel(org.csstudio.utility.adlparser.fileParser.ADLWidget, org.eclipse.swt.graphics.RGB[])}.
	 */
	public void testInitializeDisplayModel() {
		RGB[] colorMap = ADLTestObjects.makeColorMap();
		DisplayModel display = TranslatorUtils.initializeDisplayModel(ADLTestObjects.setupBasicFile(), colorMap);
		assertEquals("display X", 81, display.getX());
		assertEquals("display Y", 107, display.getY());
		assertEquals("display Width", 1020, display.getWidth());
		assertEquals("display Height", 610, display.getHeight());
		assertEquals("display foreground color", colorMap[2], display.getForegroundColor());
		assertEquals("display background color", colorMap[4], display.getBackgroundColor());
		assertEquals("display grid spacing", 5, display.getPropertyValue(DisplayModel.PROP_GRID_SPACE));
		assertEquals("display grid on", false, display.getPropertyValue(DisplayModel.PROP_SHOW_GRID));
		assertEquals("display snap to grid", false, display.getPropertyValue(DisplayModel.PROP_SNAP_GEOMETRY));
		
	}

}
