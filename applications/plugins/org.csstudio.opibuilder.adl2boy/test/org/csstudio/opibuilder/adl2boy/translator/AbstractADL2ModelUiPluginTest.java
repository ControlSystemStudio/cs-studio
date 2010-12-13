/*************************************************************************\
 * Copyright (c) 2010  UChicago Argonne, LLC
 * This file is distributed subject to a Software License Agreement found
 * in the file LICENSE that is included with this distribution.
/*************************************************************************/
package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.model.PolyLineModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLTestObjects;
import org.csstudio.utility.adlparser.fileParser.widgets.ADLDisplay;
import org.csstudio.utility.adlparser.fileParser.widgets.Line;
import org.csstudio.utility.adlparser.fileParser.widgets.Oval;
import org.csstudio.utility.adlparser.fileParser.widgets.PolyLine;
import org.eclipse.swt.graphics.RGB;
import org.junit.After;
import org.junit.Before;
import junit.framework.TestCase;

/**
 * @author hammonds
 * 
 */
public class AbstractADL2ModelUiPluginTest extends TestCase {
	TestDisp2Model dispModel;
	TestWidget2Model testWidgetModel;
	protected class TestDisp2Model extends AbstractADL2Model {

		public TestDisp2Model(RGB[] colorMap) {
			super(colorMap);
			widgetModel = new DisplayModel();
		}

		@Override
		public AbstractWidgetModel getWidgetModel() {
			// TODO Auto-generated method stub
			return widgetModel;
		}

		public void processWidget(ADLWidget adlWidget) {
			ADLDisplay adlDisp = new ADLDisplay(adlWidget);
		}
	}
	protected class TestWidget2Model extends AbstractADL2Model {

		public TestWidget2Model(RGB[] colorMap) {
			super(colorMap);
			widgetModel = new PolyLineModel();
		}

		@Override
		public AbstractWidgetModel getWidgetModel() {
			// TODO Auto-generated method stub
			return widgetModel;
		}

		public void processWidget(ADLWidget adlWidget) {
			Oval adlLine = new Oval(adlWidget);
			setADLBasicAttributeProps(adlLine, widgetModel, true);
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		dispModel = new TestDisp2Model(
				ADLTestObjects.makeColorMap());
		dispModel.processWidget(ADLTestObjects.setupBasicDisplay());
		testWidgetModel = new TestWidget2Model(
				ADLTestObjects.makeColorMap());
		DisplayModel d = (DisplayModel)(dispModel.getWidgetModel());
		d.addChild(testWidgetModel.getWidgetModel());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	public void testADL2ModelColors(){
		testWidgetModel.processWidget(ADLTestObjects.setupBasicLine());
		OPIColor frgd = (OPIColor)testWidgetModel.getWidgetModel().getPropertyValue(AbstractWidgetModel.PROP_COLOR_FOREGROUND);
		assertEquals ("Foreground Color", ADLTestObjects.makeColorMap()[2], frgd.getRGBValue());
	}
}
