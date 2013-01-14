/*************************************************************************\
 * Copyright (c) 2010  UChicago Argonne, LLC
 * This file is distributed subject to a Software License Agreement found
 * in the file LICENSE that is included with this distribution.
/*************************************************************************/
package org.csstudio.opibuilder.adl2boy.translator;

import junit.framework.TestCase;

import org.csstudio.opibuilder.adl2boy.utilities.ColorUtilities;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.model.PolyLineModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLTestObjects;
import org.csstudio.utility.adlparser.fileParser.widgets.ADLDisplay;
import org.csstudio.utility.adlparser.fileParser.widgets.Oval;
import org.eclipse.swt.graphics.RGB;
import org.junit.After;
import org.junit.Before;

/**
 * @author hammonds
 * 
 */
public class AbstractADL2ModelUiPluginTest extends TestCase {
	TestDisp2Model dispModel;
	TestWidget2Model testWidgetModel;
	OPIColor[] tableColors;

	protected class TestDisp2Model extends AbstractADL2Model {

		public TestDisp2Model(RGB[] colorMap) {
			super(colorMap);
		}

		public void processWidget(ADLWidget adlWidget) {
			ADLDisplay adlDisp = new ADLDisplay(adlWidget);

		}

		@Override
		public void makeModel(ADLWidget adlWidget,
				AbstractContainerModel parentModel) {
			widgetModel = new DisplayModel();
		}
	}

	protected class TestWidget2Model extends AbstractADL2Model {

		public TestWidget2Model(RGB[] colorMap) {
			super(colorMap);
		}

		public void processWidget(ADLWidget adlWidget) {
			Oval adlLine = new Oval(adlWidget);
			setADLBasicAttributeProps(adlLine, widgetModel, true);
		}

		@Override
		public void makeModel(ADLWidget adlWidget,
				AbstractContainerModel parentModel) {
			widgetModel = new PolyLineModel();
			parentModel.addChild(widgetModel, true);
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		ColorUtilities
				.loadToBoyColorTable("platform:/base/plugin/org.csstudio.opibuilder.adl2boy/resources/color.def");
		tableColors = ColorUtilities.getTableColors();
	}

	/**
	 * @param disp
	 */
	public void initialilzeDisplay(ADLWidget disp) {
		dispModel = new TestDisp2Model(ADLTestObjects.makeColorMap());
		dispModel.makeModel(disp, null);
		dispModel.processWidget(disp);
		testWidgetModel = new TestWidget2Model(ADLTestObjects.makeColorMap());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	public void testADL2ModelColors() {
		initialilzeDisplay(ADLTestObjects.setupBasicDisplay());
		ADLWidget oval = ADLTestObjects.setupBasicOval1();
		oval.addObject(ADLTestObjects.setupBasicAttributes1(oval));
		DisplayModel d = (DisplayModel) (dispModel.getWidgetModel());
		d.addChild(testWidgetModel.getWidgetModel());
		testWidgetModel.makeModel(oval, (AbstractContainerModel)dispModel.getWidgetModel());
		testWidgetModel.processWidget(oval);
		OPIColor frgd = (OPIColor) testWidgetModel.getWidgetModel()
				.getPropertyValue(AbstractWidgetModel.PROP_COLOR_FOREGROUND);
		assertEquals("Foreground Color", ADLTestObjects.getRGBValue(2),
				frgd.getRGBValue());
		assertEquals("Foreground Color Name", ADLTestObjects.getColorName(2),
				frgd.getColorName());
	}
	
	public void testADLModelColorsWithTable(){
		initialilzeDisplay(ADLTestObjects.setupBasicDisplay());
		testWidgetModel = new TestWidget2Model(ADLTestObjects.makeColorMap());
		ADLWidget oval = ADLTestObjects.setupBasicOval1();
		oval.addObject(ADLTestObjects.setupBasicAttributes2(oval));
		DisplayModel d = (DisplayModel) (dispModel.getWidgetModel());
		d.addChild(testWidgetModel.getWidgetModel());
		testWidgetModel.makeModel(oval, (AbstractContainerModel)dispModel.getWidgetModel());
		testWidgetModel.processWidget(oval);
		OPIColor frgd = (OPIColor) testWidgetModel.getWidgetModel().getPropertyValue(
				AbstractWidgetModel.PROP_COLOR_FOREGROUND);
		assertEquals("Foreground Color", ADLTestObjects.getRGBValue(4),
				frgd.getRGBValue());
		assertEquals("Foreground Color Name", ADLTestObjects.getColorName(4),
				frgd.getColorName());

	}

	public void testADLModelColorsWithTableFromParent1(){
		ADLWidget disp = ADLTestObjects.setupBasicDisplay();
		testWidgetModel = new TestWidget2Model(ADLTestObjects.makeColorMap());
		initialilzeDisplay(disp);
		try {
			TranslatorUtils.setDefaultBasicAttribute(ADLTestObjects.setupBasicAttributesOld1(disp));
		} catch (WrongADLFormatException e) {
			System.out.println("Trouble creating basic attributes");
			e.printStackTrace();
		}
		
		DisplayModel d = (DisplayModel) (dispModel.getWidgetModel());
		d.addChild(testWidgetModel.getWidgetModel());
		ADLWidget oval = ADLTestObjects.setupBasicOval1();
		testWidgetModel.makeModel(oval, (AbstractContainerModel)dispModel.getWidgetModel());
		testWidgetModel.processWidget(oval);
		OPIColor frgd = (OPIColor) testWidgetModel.getWidgetModel().getPropertyValue(
				AbstractWidgetModel.PROP_COLOR_FOREGROUND);
		assertEquals("Foreground Color", ADLTestObjects.getRGBValue(2),
				frgd.getRGBValue());
		assertEquals("Foreground Color Name", ADLTestObjects.getColorName(2),
				frgd.getColorName());

	}

	public void testADLModelColorsWithTableFromParent2(){
		ADLWidget disp = ADLTestObjects.setupBasicDisplay();
		testWidgetModel = new TestWidget2Model(ADLTestObjects.makeColorMap());
		initialilzeDisplay(disp);
		try {
			TranslatorUtils.setDefaultBasicAttribute(ADLTestObjects.setupBasicAttributesOld2(disp));
		} catch (WrongADLFormatException e) {
			System.out.println("Trouble creating basic attributes");
			e.printStackTrace();
		}
		
		DisplayModel d = (DisplayModel) (dispModel.getWidgetModel());
		d.addChild(testWidgetModel.getWidgetModel());
		ADLWidget oval = ADLTestObjects.setupBasicOval1();
		testWidgetModel.makeModel(oval, (AbstractContainerModel)dispModel.getWidgetModel());
		testWidgetModel.processWidget(oval);
		OPIColor frgd = (OPIColor) testWidgetModel.getWidgetModel().getPropertyValue(
				AbstractWidgetModel.PROP_COLOR_FOREGROUND);
		assertEquals("Foreground Color", ADLTestObjects.getRGBValue(4),
				frgd.getRGBValue());
		assertEquals("Foreground Color Name", ADLTestObjects.getColorName(4),
				frgd.getColorName());

	}

}
