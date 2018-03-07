/*************************************************************************\
 * Copyright (c) 2010  UChicago Argonne, LLC
 * This file is distributed subject to a Software License Agreement found
 * in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import java.util.List;

import org.csstudio.opibuilder.adl2boy.utilities.ColorUtilities;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.script.Expression;
import org.csstudio.opibuilder.script.PVTuple;
import org.csstudio.opibuilder.script.RuleData;
import org.csstudio.opibuilder.script.RulesInput;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.model.AbstractShapeModel;
import org.csstudio.opibuilder.widgets.model.ArcModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLBasicAttribute;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLConnected;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLDynamicAttribute;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLObject;
import org.csstudio.utility.adlparser.fileParser.widgets.ADLAbstractWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.IWidgetWithColorsInBase;
import org.eclipse.swt.graphics.RGB;

/**
 * @author John Hammonds, Argonne National Laboratory
 *
 */
public abstract class AbstractADL2Model {
    AbstractWidgetModel widgetModel;
    RGB colorMap[] = new RGB[0];
    protected String className = new String();

    public AbstractADL2Model(final ADLWidget adlWidget, RGB colorMap[],
            AbstractContainerModel parentModel) {
        this.colorMap = colorMap;
        makeModel(adlWidget, parentModel);
        processWidget(adlWidget);
    }

    public AbstractADL2Model(RGB colorMap[]) {
        this.colorMap = colorMap;
    }

    /**
     * Does the work of converting the adlWidget into the AbstractWidgetModel
     *
     * @param adlWidget
     */
    abstract public void processWidget(ADLWidget adlWidget);

    /**
     * Creates the widgetModel appropriate to the adlWidget. Adds the
     * widgetModel as a child to the parentModel
     *
     * @param adlWidget
     * @param parentModel
     */
    abstract public void makeModel(ADLWidget adlWidget,
            AbstractContainerModel parentModel);

    /**
     *
     * @return
     */
    public AbstractWidgetModel getWidgetModel() {
        return widgetModel;
    }

    /**
     * set the properties contained in the ADL basic properties section in the
     * created widgetModel
     *
     * @param adlWidget
     * @param widgetModel
     */
    protected void setADLObjectProps(ADLAbstractWidget adlWidget,
            AbstractWidgetModel widgetModel) {
        if (adlWidget.hasADLObject()) {
            ADLObject adlObj = adlWidget.getAdlObject();
            widgetModel.setX(adlObj.getX());
            widgetModel.setY(adlObj.getY());
            widgetModel.setHeight(adlObj.getHeight());
            widgetModel.setWidth(adlObj.getWidth());
        }

    }

    /**
     * set the properties contained in the ADL basic properties section in the
     * created widgetModel
     *
     * @param adlWidget
     * @param widgetModel
     */
    protected void setADLBasicAttributeProps(ADLAbstractWidget adlWidget,
            AbstractWidgetModel widgetModel, boolean colorForeground) {
        ADLBasicAttribute basAttr;
        if (adlWidget.hasADLBasicAttribute()) {
            basAttr = adlWidget.getAdlBasicAttribute();
        } else {
            basAttr = TranslatorUtils.getDefaultBasicAttribute();
            adlWidget.setAdlBasicAttribute(basAttr);
        }
        if (basAttr.isColorDefined()) {
            if (colorForeground) {
                setColor(basAttr.getClr(),
                        AbstractWidgetModel.PROP_COLOR_FOREGROUND);
            } else {
                setColor(basAttr.getClr(),
                        AbstractWidgetModel.PROP_COLOR_BACKGROUND);
            }
        } else {
            if (colorForeground) {
                setForegroundColorSameAsParent(widgetModel);
            } else {
                setBackgroundColorSameAsParent(widgetModel);
            }

        }

    }

    /**
     *
     * @param adlWidget
     * @param widgetModel
     */
    protected void setADLDynamicAttributeProps(ADLAbstractWidget adlWidget,
            AbstractWidgetModel widgetModel) {
        ADLDynamicAttribute dynAttr;
        if (adlWidget.hasADLDynamicAttribute()) {
            dynAttr = adlWidget.getAdlDynamicAttribute();
        } else {
            dynAttr = TranslatorUtils.getDefaultDynamicAttribute();
            adlWidget.setAdlDynamicAttribute(dynAttr);
        }
        if (!(dynAttr.get_vis().equals("static"))) {
            if (dynAttr.get_chan() != null) {
                if (dynAttr.get_vis().equals("if not zero")) {
                    addSimpleVisibilityRule("pv0!=0", dynAttr.get_chan(),
                            widgetModel);
                } else if (dynAttr.get_vis().equals("if zero")) {
                    addSimpleVisibilityRule("pv0==0", dynAttr.get_chan(),
                            widgetModel);
                } else if (dynAttr.get_vis().equals("calc")) {
                    RuleData newRule = createNewVisibilityRule(widgetModel);

                    addPVToRule(dynAttr.get_chan(), newRule);
                    addPVToRule(dynAttr.get_chanb(), newRule);
                    addPVToRule(dynAttr.get_chanc(), newRule);
                    addPVToRule(dynAttr.get_chand(), newRule);
                    String newExpr = translateExpression(dynAttr.get_calc());
                    newRule.addExpression(new Expression(newExpr, true));
                    newRule.addExpression(new Expression("!("+newExpr+")", false));

                    addVisibilityRuleToWidgetModel(newRule, widgetModel);
                }
            }
        }

    }

    private void addVisibilityRuleToWidgetModel(RuleData newRule,
            AbstractWidgetModel widgetModel) {
        RulesInput ruleInput = widgetModel.getRulesInput();
        List<RuleData> ruleData = ruleInput.getRuleDataList();
        ruleData.add(newRule);

        widgetModel.setPropertyValue(
                AbstractWidgetModel.PROP_RULES, ruleData);
    }

    /** Create a simple visibility rule.  This places a simple logical
     * expression for one channel
     * @param booleanExpression
     * @param chan
     * @param widgetModel
     * @return
     */
    private void addSimpleVisibilityRule(String booleanExpression,
            String chan, AbstractWidgetModel widgetModel) {
        RuleData newRule = createNewVisibilityRule(widgetModel);
        PVTuple pvs = new PVTuple(chan, true);
        newRule.addPV(pvs);
        newRule.addExpression(new Expression(booleanExpression, true));
        newRule.addExpression(new Expression("!("+ booleanExpression + ")", false));
        addVisibilityRuleToWidgetModel(newRule, widgetModel);

    }


    /**
     * Create a new empty visibility rule.
     * @param widgetModel
     * @return
     */
    private RuleData createNewVisibilityRule(AbstractWidgetModel widgetModel) {
        RuleData newRule = new RuleData(widgetModel);
        newRule.setName("Visibility");
        newRule.setPropId(AbstractWidgetModel.PROP_VISIBLE);
        return newRule;
    }


    /**
     * Perform a translation between an MEDM style
     * calc expression for visibility rules.  Makes
     * the following assumptions
     * 1. The rule is fairy simple. The only
     *    alpha characters are A, B, C & D
     * 2. That the pv fields A, B, C & D are
     *    used sequentially. (i.e. If B is used
     *    A is used, if C is used A & Bare used).
     *    This allows the substitutions
     *        A = pv0
     *        B = pv1
     *        C = pv2
     *        D = PV3
     * 3. Only basic desion maiking is hapenning
     *    ()+-/*=<># were used.  No use of math
     *    functions like ABS, SIN, ...
     * 4. The characters = and # are replaced by
     *    == and != respecively.
     * @param adlExpr
     * @return
     */
    private String translateExpression(String adlExpr) {
        String opiExpr = adlExpr;
        opiExpr = replaceChannel("A", "pv0", opiExpr);
        opiExpr = replaceChannel("B", "pv1", opiExpr);
        opiExpr = replaceChannel("C", "pv2", opiExpr);
        opiExpr = replaceChannel("D", "pv3", opiExpr);
        opiExpr = replaceString("=", "==", opiExpr);
        opiExpr = replaceString("#", "!=", opiExpr);

        // The above can result in "pv0====7".
        // Patch that back into a plain "pv0==7"
        opiExpr = opiExpr.replaceAll("==+", "==");
        return opiExpr.toString();
    }

    /**
     *
     * @param adlChanName
     * @param opiChanName
     * @param opiExpr
     * @return
     */
    private String replaceChannel(String adlChanName, String opiChanName,
            String opiExpr) {
        opiExpr = replaceString(adlChanName, opiChanName, opiExpr);
        opiExpr = replaceString(adlChanName.toLowerCase(), opiChanName, opiExpr);
        return opiExpr;
    }

    private String replaceString(String inName, String outName, String expr) {
        String retExpr = expr;
        if (retExpr.contains(inName)){
            StringBuffer tempExpr = new StringBuffer();
            String[] parts = retExpr.split(inName);
            tempExpr.append(parts[0]);
            for (int occur = 0; occur<(parts.length-1); occur++){
                if (!inName.equals("=")) {
                    if (inName.equals("=")
                            && (tempExpr.toString().endsWith(">") ||
                                    tempExpr.toString().endsWith("<") ))

                    {
                        tempExpr.append("=");
                        tempExpr.append(parts[occur + 1]);
                    } else {
                        tempExpr.append(outName);
                        tempExpr.append(parts[occur + 1]);
                    }
                }
                else {
                    tempExpr.append(outName);
                    tempExpr.append(parts[occur + 1]);
                }
            }
            retExpr = tempExpr.toString();
        }
        return retExpr;
    }

    private void addPVToRule(String chan, RuleData newRule) {
        if (!chan.equals("")){
            PVTuple pvs = new PVTuple(chan, true);
            newRule.addPV(pvs);
        }
    }

    /**
     * set the properties contained in the ADL basic properties section in the
     * created widgetModel
     *
     * @param adlWidget
     * @param widgetModel
     */
    protected void setADLControlProps(ADLAbstractWidget adlWidget,
            AbstractWidgetModel widgetModel) {
        if (adlWidget.hasADLControl()) {
            ADLConnected adlConnected = adlWidget.getAdlControl();
            setADLConnectedProps(widgetModel, adlConnected);
        }
    }

    /**
     * set the properties contained in the ADL basic properties section in the
     * created widgetModel
     *
     * @param adlWidget
     * @param widgetModel
     */
    protected void setADLMonitorProps(ADLAbstractWidget adlWidget,
            AbstractWidgetModel widgetModel) {
        if (adlWidget.hasADLMonitor()) {
            ADLConnected adlConnected = adlWidget.getAdlMonitor();
            setADLConnectedProps(widgetModel, adlConnected);
        }
    }

    /**
     * @param widgetModel
     * @param adlConnected
     */
    public void setADLConnectedProps(AbstractWidgetModel widgetModel,
            ADLConnected adlConnected) {

        if (adlConnected.isForeColorDefined()) {
            setColor(adlConnected.getForegroundColor(),
                    AbstractWidgetModel.PROP_COLOR_FOREGROUND);
        } else {
            setForegroundColorSameAsParent(widgetModel);
        }
        if (adlConnected.isBackColorDefined()) {
            setColor(adlConnected.getBackgroundColor(),
                    AbstractWidgetModel.PROP_COLOR_BACKGROUND);
        } else {
            setBackgroundColorSameAsParent(widgetModel);
        }
        String channel = adlConnected.getChan();
        if ((channel != null) && (!(channel.equals("")))) {
            widgetModel.setPropertyValue(AbstractPVWidgetModel.PROP_PVNAME,
                    channel);
        }
    }

    /**
     * @param widgetModel
     */
    public void setBackgroundColorSameAsParent(AbstractWidgetModel widgetModel) {
        widgetModel.setPropertyValue(
                AbstractWidgetModel.PROP_COLOR_BACKGROUND,
                widgetModel.getParent().getPropertyValue(
                        AbstractWidgetModel.PROP_COLOR_BACKGROUND));
    }

    /**
     * @param widgetModel
     */
    public void setForegroundColorSameAsParent(AbstractWidgetModel widgetModel) {
        widgetModel.setPropertyValue(
                AbstractWidgetModel.PROP_COLOR_FOREGROUND,
                widgetModel.getParent().getPropertyValue(
                        AbstractWidgetModel.PROP_COLOR_FOREGROUND));
    }

    /**
     * @param displayForeColor
     * @param propertyName
     */
    public void setColor(int displayForeColor, String propertyName) {
        OPIColor color;
        color = ColorUtilities
                .matchToTableColor(this.colorMap[displayForeColor]);
        widgetModel.setPropertyValue(propertyName, color);
    }

    protected void setShapesColorFillLine(ADLAbstractWidget shapeWidget) {
        if (shapeWidget.getAdlBasicAttribute().getFill().equals("solid") ) {
            if (!(widgetModel instanceof ArcModel)){
                widgetModel.setPropertyValue(AbstractShapeModel.PROP_TRANSPARENT, false);
            }
            widgetModel.setPropertyValue(AbstractShapeModel.PROP_FILL_LEVEL, 100);
            widgetModel.setPropertyValue(AbstractShapeModel.PROP_HORIZONTAL_FILL, true);

        }
        else if (shapeWidget.getAdlBasicAttribute().getFill().equals("outline")) {
            if (!(widgetModel instanceof ArcModel)){
                widgetModel.setPropertyValue(AbstractShapeModel.PROP_TRANSPARENT, true);
            }
            OPIColor fColor = (OPIColor)widgetModel.getPropertyValue(AbstractWidgetModel.PROP_COLOR_FOREGROUND);
            widgetModel.setPropertyValue(AbstractShapeModel.PROP_LINE_COLOR, fColor);
            if ( shapeWidget.getAdlBasicAttribute().getStyle().equals("solid") ) {
                widgetModel.setPropertyValue(AbstractShapeModel.PROP_LINE_STYLE, 0);
            }
            if ( shapeWidget.getAdlBasicAttribute().getStyle().equals("dash") ) {
                widgetModel.setPropertyValue(AbstractShapeModel.PROP_LINE_STYLE, 1);

            }
            int lineWidth = shapeWidget.getAdlBasicAttribute().getWidth();
            if (lineWidth == 0)lineWidth = 1;
            widgetModel.setPropertyValue(AbstractShapeModel.PROP_LINE_WIDTH, lineWidth );
        }
    }

    /**
     * @param args
     * @return
     */
    public MacrosInput makeMacros(String args) {
        String resArgs = removeParentMacros(args);
        String argsIn = "true, " + resArgs;
        MacrosInput macIn = null;
        try {
            macIn = MacrosInput.recoverFromString(argsIn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return macIn;
    }

    /**
     * Remove parent macros (i.e. P=$(P))from the list. We can now pass parent
     * Macros.
     *
     * @param args
     * @return
     */
    public String removeParentMacros(String args) {
        String[] argList = args.split(",");
        StringBuffer strBuff = new StringBuffer();
        for (int ii = 0; ii < argList.length; ii++) {
            String[] argParts = argList[ii].split("=");
            if (!argParts[1].replaceAll(" ", "").equals(
                    "$(" + argParts[0].trim() + ")")) {
                if (strBuff.length() != 0)
                    strBuff.append(", ");
                strBuff.append(argList[ii]);
            }
        }
        String resArgs = strBuff.toString();
        return resArgs;
    }

    /**
     * @param rdWidget
     */
    public void setWidgetColors(IWidgetWithColorsInBase rdWidget) {
        if (rdWidget.isForeColorDefined()) {
            setColor(rdWidget.getForegroundColor(),
                    AbstractWidgetModel.PROP_COLOR_FOREGROUND);
        }
        if (rdWidget.isBackColorDefined()) {
            setColor(rdWidget.getBackgroundColor(),
                    AbstractWidgetModel.PROP_COLOR_BACKGROUND);
        }
    }
}
