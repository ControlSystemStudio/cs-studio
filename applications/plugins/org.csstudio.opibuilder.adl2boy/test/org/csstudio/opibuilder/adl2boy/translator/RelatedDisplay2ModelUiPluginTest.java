/*************************************************************************\
 * Copyright (c) 2010  UChicago Argonne, LLC
 * This file is distributed subject to a Software License Agreement found
 * in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import java.util.Map;

import junit.framework.TestCase;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgetActions.OpenDisplayAction;
import org.csstudio.opibuilder.widgets.model.MenuButtonModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.WrongADLFormatException;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLTestObjects;
import org.csstudio.utility.adlparser.fileParser.widgetParts.RelatedDisplayItem;
import org.csstudio.utility.adlparser.fileParser.widgets.RelatedDisplay;
import org.eclipse.core.runtime.Path;

/**
 * @author John Hammonds, Argonne National Laboratory
 * 
 */
public class RelatedDisplay2ModelUiPluginTest extends TestCase {
	RelatedDisplay2Model converter = null;

	/**
	 * @throws java.lang.Exception
	 */
	protected void setUp() throws Exception {
		converter = new RelatedDisplay2Model(ADLTestObjects.makeColorMap());
		// converter.setColorMap(makeColorMap());
	}

	/**
	 * @throws java.lang.Exception
	 */
	protected void tearDown() throws Exception {
		converter.cleanup();
		converter = null;

	}

	/**
	 * Test method for
	 * {@link org.csstudio.opibuilder.adl2boy.translator.RelatedDisplay2Model#RelatedDisplay2Model(org.csstudio.utility.adlparser.fileParser.ADLWidget, org.eclipse.swt.graphics.RGB[], org.csstudio.opibuilder.model.AbstractContainerModel)}
	 * .
	 */
	public void testRelatedDisplay2Model() {

		ADLWidget adlWidget = ADLTestObjects.setupRelatedDisplayWidget();
		RelatedDisplay adlRd = new RelatedDisplay(adlWidget);
		DisplayModel parentModel = Display2ModelUiPluginTest
				.makeDisplayModel(ADLTestObjects.setupBasicDisplay());
		converter.makeModel(adlWidget, parentModel);
		converter.processWidget(adlWidget);
		MenuButtonModel rd = (MenuButtonModel) converter.getWidgetModel();
		OPIColor frgd = (OPIColor) rd
				.getPropertyValue(AbstractWidgetModel.PROP_COLOR_FOREGROUND);
		OPIColor bkgd = (OPIColor) rd
				.getPropertyValue(AbstractWidgetModel.PROP_COLOR_BACKGROUND);
		assertEquals("Foreground Color",
				ADLTestObjects.getRGBValue(adlRd.getForegroundColor()),
				frgd.getRGBValue());
		assertEquals("ForegroundColorName",
				ADLTestObjects.getColorName(adlRd.getForegroundColor()),
				frgd.getColorName());
		assertEquals("Background Color",
				ADLTestObjects.getRGBValue(adlRd.getBackgroundColor()),
				bkgd.getRGBValue());
		assertEquals("BackgroundColorName",
				ADLTestObjects.getColorName(adlRd.getBackgroundColor()),
				bkgd.getColorName());
		// TODO more tests

	}

	public void testCreateOpenDisplayAction() {
		// Standard test
		{
			OpenDisplayAction od = converter
					.createOpenDisplayAction(ADLTestObjects
							.makeRelatedDisplay1());
			validateOpenDisplayAction(od, "myfile.opi", "myLabel", "", true);

		}
		// test where there is no policy defined
		{
			OpenDisplayAction od = converter
					.createOpenDisplayAction(ADLTestObjects
							.makeRelatedDisplayNoPolicy());
			validateOpenDisplayAction(od, "myfile.opi", "myLabel",
					"P=iocT1:,M=m1:", false);
		}
		// test where there are mixed set of arguments (from parent and not)
		{
			OpenDisplayAction od = converter
					.createOpenDisplayAction(ADLTestObjects
							.makeRelatedDisplayMixedArgs());
			validateOpenDisplayAction(od, "path/myfile.opi", "my label",
					"T=temp:,PREC=3", false);
		}
		// test where the argument list is empty
		{
			OpenDisplayAction od = converter
					.createOpenDisplayAction(ADLTestObjects
							.makeRelatedDisplayEmptyArgs());
			validateOpenDisplayAction(od, "path/myfile.opi", "my label", "",
					false);
		}
		// test where the argument list is not defined
		{
			OpenDisplayAction od = converter
					.createOpenDisplayAction(ADLTestObjects
							.makeRelatedDisplayNoArgs());
			validateOpenDisplayAction(od, "path/myfile.opi", "my label", "",
					false);
		}
	}

	/**
	 * @param pathExpect
	 * @param labelExpect
	 * @param argsExpect
	 * @param replaceExpect
	 */
	private void validateOpenDisplayAction(OpenDisplayAction od,
			String pathExpect, String labelExpect, String argsExpect,
			boolean replaceExpect) {
		String[] bString = {"false", "true"};
		Path path = (Path) od.getPropertyValue(OpenDisplayAction.PROP_PATH);
		assertTrue("FilePath " + path, path.toString().equals(pathExpect));
		Integer replace = (Integer) od
				.getPropertyValue(OpenDisplayAction.PROP_REPLACE);
		assertEquals("Replace display " + replace, new Boolean(replaceExpect),
				new Boolean(bString[replace]));
		String label = (String) od
				.getPropertyValue(OpenDisplayAction.PROP_DESCRIPTION);
		assertTrue(" TestLabel " + label, labelExpect.equals(label));
		// TODO write a real test for the converted argument list
		assertTrue(" Arguments ", argsExpect.equals(argsExpect));
	}

	public void testAddMacrosToOpenDisplayAction() {
		// Explicit definition
		RelatedDisplayItem[] rds = makeRelatedDisplays(ADLTestObjects
				.setupRelDispNoPolicy());
		OpenDisplayAction odAction = new OpenDisplayAction();
		converter.addMacrosToOpenDisplayAction(rds[0], odAction);
		Map<String, String> map = ((MacrosInput) (odAction
				.getPropertyValue(OpenDisplayAction.PROP_MACROS)))
				.getMacrosMap();
		assertEquals("Map size for P=iocT1:,M=m1:, " + map, 2, map.size());
		// Definition using parent macros
		rds = makeRelatedDisplays(ADLTestObjects.setupRelDisp());
		odAction = new OpenDisplayAction();
		converter.addMacrosToOpenDisplayAction(rds[0], odAction);
		map = ((MacrosInput) (odAction
				.getPropertyValue(OpenDisplayAction.PROP_MACROS)))
				.getMacrosMap();
		assertEquals("Map size for P=$(P),M=$(M):, " + map, 0, map.size());
		// Definition using parent macros
		rds = makeRelatedDisplays(ADLTestObjects.setupRelDispMixedArgs());
		odAction = new OpenDisplayAction();
		converter.addMacrosToOpenDisplayAction(rds[0], odAction);
		map = ((MacrosInput) (odAction
				.getPropertyValue(OpenDisplayAction.PROP_MACROS)))
				.getMacrosMap();
		assertEquals("Map size for P=$(P),M=$(M),T=temp:,PREC=3", 2, map.size());
	}

	private RelatedDisplayItem[] makeRelatedDisplays(ADLWidget adl) {
		RelatedDisplayItem[] rds = new RelatedDisplayItem[1];
		try {
			rds[0] = new RelatedDisplayItem(adl);
		} catch (WrongADLFormatException e) {
			fail("This should work since test items are known");
			e.printStackTrace();
		}
		return rds;
	}

	/**
	 * Test the makeMacros function
	 */
	public void testMakeMacros() {
		// Explicit definition
		MacrosInput mi = converter.makeMacros("P=iocT1:,M=m1:");
		Map<String, String> map = mi.getMacrosMap();
		assertEquals("Map size for P=iocT1:,M=m1:, " + map, 2, map.size());
		// Definition with straight macros
		mi = converter.makeMacros("P=$(P),M=$(M)");
		map = mi.getMacrosMap();

		assertEquals("Map size for P=$(P),M=$(M)", map.size(), 0);
		// Mixed macros with parent macros
		mi = converter.makeMacros("P=$(P),M=$(M),T=temp:,PREC=3");
		map = mi.getMacrosMap();
		assertEquals("Map size for P=$(P),M=$(M),T=temp:,PREC=3", map.size(), 2);
	}

}
