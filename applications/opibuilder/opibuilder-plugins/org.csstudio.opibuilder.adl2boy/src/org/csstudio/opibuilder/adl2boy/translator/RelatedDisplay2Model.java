/*************************************************************************\
 * Copyright (c) 2010  UChicago Argonne, LLC
 * This file is distributed subject to a Software License Agreement found
 * in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.runmode.RunModeService.DisplayMode;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.csstudio.opibuilder.widgetActions.OpenDisplayAction;
import org.csstudio.opibuilder.widgets.model.ActionButtonModel;
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

    @Override
    public void makeModel(ADLWidget adlWidget, AbstractContainerModel parentModel){
        final RelatedDisplay rdWidget = new RelatedDisplay(adlWidget);
        final RelatedDisplayItem[] rdDisplays = rdWidget.getRelatedDisplayItems();
        // ActionButton allows pressing Ctrl etc. to influence how a display
        // is opened at runtime, so prefer that.
        // .. unless there are more than one related display,
        // in which case the menu button is more practical
        if (rdDisplays.length > 1)
            widgetModel = new MenuButtonModel();
        else
            widgetModel = new ActionButtonModel();
        parentModel.addChild(widgetModel, true);
    }

    /**
     * @param adlWidget
     */
    @Override
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
                    if (!(rdDisplays[ii].getFileName().replaceAll("\"", "").equals("")))
                    {
                        final OpenDisplayAction odAction = createOpenDisplayAction(rdDisplays[ii]);
                        // For menu, always new tab because menu button doesn't
                        // allow user to use 'Ctrl' etc at runtime.
                        // Users can always close the new tab, but have no other way
                        // to get new tab.
                        if (widgetModel instanceof MenuButtonModel)
                            odAction.setPropertyValue(OpenDisplayAction.PROP_MODE, DisplayMode.NEW_TAB.ordinal());
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
        if (widgetModel instanceof ActionButtonModel)
            widgetModel.setPropertyValue(ActionButtonModel.PROP_TEXT, label);
        else
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
            if (rdDisplay.getPolicy().replaceAll("\"", "").equals("replace display")) {
                // replace the display
                odAction.setPropertyValue(OpenDisplayAction.PROP_MODE, DisplayMode.REPLACE.ordinal());
            } else { // don't replace the display
                odAction.setPropertyValue(OpenDisplayAction.PROP_MODE, DisplayMode.NEW_TAB.ordinal());
            }
        } else { // policy not present go to default, i.e. don't replace, open new tab
            odAction.setPropertyValue(OpenDisplayAction.PROP_MODE, DisplayMode.NEW_TAB.ordinal());
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
