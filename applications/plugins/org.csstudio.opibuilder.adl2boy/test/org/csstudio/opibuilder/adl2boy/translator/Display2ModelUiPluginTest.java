/*************************************************************************\
 * Copyright (c) 2010  UChicago Argonne, LLC
 * This file is distributed subject to a Software License Agreement found
 * in the file LICENSE that is included with this distribution.
/*************************************************************************/
package org.csstudio.opibuilder.adl2boy.translator;

import junit.framework.TestCase;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLTestObjects;
import org.csstudio.utility.adlparser.fileParser.widgets.ADLDisplay;
import org.eclipse.swt.graphics.RGB;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author hammonds
 *
 */
public class Display2ModelUiPluginTest extends TestCase {
	Display2Model converter = null;
	RGB[] colorMap = ADLTestObjects.makeColorMap();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		converter = new Display2Model(colorMap);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.csstudio.opibuilder.adl2boy.translator.Display2Model#Display2Model(org.csstudio.utility.adlparser.fileParser.ADLWidget, org.eclipse.swt.graphics.RGB[], org.csstudio.opibuilder.model.AbstractContainerModel)}.
	 */
	@Test
	public void testDisplay2Model() {
		ADLDisplay adlDisp = new ADLDisplay(ADLTestObjects.setupBasicDisplay());
		converter.processWidget(ADLTestObjects.setupBasicDisplay());;
		DisplayModel disp = (DisplayModel)converter.getWidgetModel();
		OPIColor frgd = (OPIColor)disp.getPropertyValue(AbstractWidgetModel.PROP_COLOR_FOREGROUND);
		OPIColor bkgd = (OPIColor)disp.getPropertyValue(AbstractWidgetModel.PROP_COLOR_BACKGROUND);
		assertEquals ("Foreground Color", ADLTestObjects.getRGBValue(adlDisp.getForegroundColor()), frgd.getRGBValue());
		assertEquals("ForegroundColorName", ADLTestObjects.getColorName(adlDisp.getForegroundColor()), frgd.getColorName());
		assertEquals ("Background Color", ADLTestObjects.getRGBValue(adlDisp.getBackgroundColor()), bkgd.getRGBValue());
		assertEquals ("BackgroundColorName", ADLTestObjects.getColorName(adlDisp.getBackgroundColor()), bkgd.getColorName());
		assertEquals ("X", adlDisp.getAdlObject().getX(), disp.getX());
		assertEquals ("Y", adlDisp.getAdlObject().getY(), disp.getY());
		assertEquals ("Height", adlDisp.getAdlObject().getHeight(), disp.getHeight());
		assertEquals ("Width", adlDisp.getAdlObject().getWidth(), disp.getWidth());
	}

}
