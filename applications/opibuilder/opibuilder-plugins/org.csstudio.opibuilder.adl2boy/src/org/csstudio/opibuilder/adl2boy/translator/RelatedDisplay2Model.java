/*************************************************************************\
 * Copyright (c) 2010  UChicago Argonne, LLC
 * This file is distributed subject to a Software License Agreement found
 * in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.csstudio.opibuilder.widgetActions.OpenDisplayAction;
import org.csstudio.opibuilder.widgets.model.MenuButtonModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgetParts.RelatedDisplayItem;
import org.csstudio.utility.adlparser.fileParser.widgets.RelatedDisplay;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.graphics.RGB;

/**
 * Convert MEDMs related display to BOYs MenuButton
 * 
 * @author John Hammonds, Argonne National Laboratory
 * 
 */
public class RelatedDisplay2Model extends AbstractADL2Model {
	// MenuButtonModel menuModel = new MenuButtonModel();

	public RelatedDisplay2Model(ADLWidget adlWidget, RGB[] colorMap,
			AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
	}

	public RelatedDisplay2Model(RGB[] colorMap) {
		super(colorMap);
	}

	public void makeModel(ADLWidget adlWidget, AbstractContainerModel parentModel){
		widgetModel = new MenuButtonModel();
		parentModel.addChild(widgetModel, true);
	}
	
	/**
	 * @param adlWidget
	 */
	public void processWidget(ADLWidget adlWidget) {
		RelatedDisplay rdWidget = new RelatedDisplay(adlWidget);
		if (rdWidget != null) {
			setADLObjectProps(rdWidget, widgetModel);
			setADLDynamicAttributeProps(rdWidget, widgetModel);
			setWidgetColors(rdWidget);
			RelatedDisplayItem[] rdDisplays = rdWidget.getRelatedDisplayItems();
			if (rdDisplays.length > 0) {
				ActionsInput ai = widgetModel.getActionsInput();
				for (int ii = 0; ii < rdDisplays.length; ii++) {
					if (!(rdDisplays[ii].getFileName().replaceAll("\"", "")
							.equals(""))) {
						OpenDisplayAction odAction = createOpenDisplayAction(rdDisplays[ii]);
						ai.addAction(odAction);
					}
				}
			}
		}
		String label = rdWidget.getLabel();
		if (label != null) {
			if (label.startsWith("-")) { // leading "-" was used to flag not
											// using the icon. Just don't use
											// the icon and throw this away
				label = label.substring(1);
			}
		}
		widgetModel.setPropertyValue(MenuButtonModel.PROP_LABEL, label);
	}

	/**
	 * @param rdDisplays
	 * @param ii
	 * @return
	 */
	public OpenDisplayAction createOpenDisplayAction(
			RelatedDisplayItem rdDisplay) {
		OpenDisplayAction odAction = new OpenDisplayAction();

		// Try to add the filename to the PROP_PATH
		IPath fPath = new Path(rdDisplay.getFileName().replaceAll("\"", "")
				.replace(".adl", ".opi"));
		System.out.println("Related display file: "
				+ rdDisplay.getFileName().replace(".adl", ".opi"));
		odAction.setPropertyValue(OpenDisplayAction.PROP_PATH, fPath);

		// Try to add macros
		addMacrosToOpenDisplayAction(rdDisplay, odAction);
		if (rdDisplay.getLabel() != null) {
			odAction.setPropertyValue(OpenDisplayAction.PROP_DESCRIPTION,
					rdDisplay.getLabel().replaceAll("\"", ""));
		}
		if ((rdDisplay.getPolicy() != null)) { // policy is present
			if (rdDisplay.getPolicy().replaceAll("\"", "").equals("replace display")) { // replace
																	// the
																	// display
				odAction.setPropertyValue(OpenDisplayAction.PROP_REPLACE, 1);
			} else { // don't replace the display
				odAction.setPropertyValue(OpenDisplayAction.PROP_REPLACE, 0);
			}
		} else { // policy not present go to default
			odAction.setPropertyValue(OpenDisplayAction.PROP_REPLACE, 0); // don't
																				// replace
																				// the
																				// display
		}
		return odAction;
	}

	/**
	 * @param rdDisplays
	 * @param ii
	 * @param odAction
	 */
	public void addMacrosToOpenDisplayAction(RelatedDisplayItem rdDisplay,
			OpenDisplayAction odAction) {
		if (rdDisplay.getArgs() != null && !rdDisplay.getArgs().isEmpty()) {
			String args = rdDisplay.getArgs().replaceAll("\"", "");
			MacrosInput macIn = makeMacros(args);
			odAction.setPropertyValue(OpenDisplayAction.PROP_MACROS, macIn);
		}
	}

	public void cleanup() {
		widgetModel = null;
	}
}
