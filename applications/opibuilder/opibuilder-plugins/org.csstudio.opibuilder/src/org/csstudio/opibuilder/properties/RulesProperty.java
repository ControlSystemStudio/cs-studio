/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.properties.support.PropertySSHelper;
import org.csstudio.opibuilder.script.Expression;
import org.csstudio.opibuilder.script.PVTuple;
import org.csstudio.opibuilder.script.RuleData;
import org.csstudio.opibuilder.script.RulesInput;
import org.csstudio.opibuilder.util.OPIBuilderMacroUtil;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

/**The property for rules.
 * @author Xihui Chen
 *
 */
public class RulesProperty extends AbstractWidgetProperty {

    /**
     * XML ELEMENT name <code>RULE</code>.
     */
    public static final String XML_ELEMENT_RULE = "rule"; //$NON-NLS-1$

    /**
     * XML ATTRIBUTE name <code>NAME</code>.
     */
    public static final String XML_ATTRIBUTE_NAME = "name"; //$NON-NLS-1$

    /**
     * XML ATTRIBUTE name <code>PROPID</code>.
     */
    public static final String XML_ATTRIBUTE_PROPID= "prop_id"; //$NON-NLS-1$

    /**
     * XML ATTRIBUTE name <code>OUTPUTEXPRESSION</code>.
     */
    public static final String XML_ATTRIBUTE_OUTPUTEXPRESSION = "out_exp"; //$NON-NLS-1$

    /**
     * XML ELEMENT name <code>EXPRESSION</code>.
     */
    public static final String XML_ELEMENT_EXPRESSION = "exp"; //$NON-NLS-1$

    /**
     * XML ATTRIBUTE name <code>BOOLEXP</code>.
     */
    public static final String XML_ATTRIBUTE_BOOLEXP= "bool_exp"; //$NON-NLS-1$

    /**
     * XML ELEMENT name <code>VALUE</code>.
     */
    public static final String XML_ELEMENT_VALUE = "value"; //$NON-NLS-1$

    /**
     * XML Element name <code>PV</code>.
     */
    public static final String XML_ELEMENT_PV = "pv"; //$NON-NLS-1$

    public static final String XML_ATTRIBUTE_TRIGGER = "trig"; //$NON-NLS-1$

    /**Rules Property Constructor. The property value type is {@link RulesInput}.
     * @param prop_id the property id which should be unique in a widget model.
     * @param description the description of the property,
     * which will be shown as the property name in property sheet.
     * @param category the category of the widget.
     */
    public RulesProperty(String prop_id, String description,
            WidgetPropertyCategory category) {
        super(prop_id, description, category, new RulesInput());

    }

    @Override
    public Object checkValue(Object value) {
        if(value == null)
            return null;
        RulesInput acceptableValue = null;
        if(value instanceof RulesInput){
            acceptableValue = (RulesInput)value;
        }
        return acceptableValue;
    }


    @Override
    public Object getPropertyValue() {
        if(executionMode == ExecutionMode.RUN_MODE && widgetModel !=null){
            RulesInput value = (RulesInput) super.getPropertyValue();
            for(RuleData rd : value.getRuleDataList()){
                for(Object pv : rd.getPVList().toArray()){
                    PVTuple pvTuple = (PVTuple)pv;
                    String newPV = OPIBuilderMacroUtil.replaceMacros(widgetModel, pvTuple.pvName);
                    if(!newPV.equals(pvTuple.pvName)){
                        int i= rd.getPVList().indexOf(pv);
                        rd.getPVList().remove(pv);
                        rd.getPVList().add(i, new PVTuple(newPV, pvTuple.trigger));
                    }
                }
            }
            return value;
        }else
            return super.getPropertyValue();
    }


    @Override
    protected PropertyDescriptor createPropertyDescriptor() {
        if(PropertySSHelper.getIMPL() == null)
            return null;
        return PropertySSHelper.getIMPL().getRulesPropertyDescriptor(prop_id, widgetModel, description);
    }

    @Override
    public RulesInput readValueFromXML(Element propElement) throws Exception {
        RulesInput result = new RulesInput();
        for(Object oe : propElement.getChildren(XML_ELEMENT_RULE)){
            Element se = (Element)oe;
            RuleData ruleData = new RuleData(widgetModel);
            ruleData.setName(se.getAttributeValue(XML_ATTRIBUTE_NAME));
            ruleData.setPropId(se.getAttributeValue(XML_ATTRIBUTE_PROPID));
            ruleData.setOutputExpValue(
                    Boolean.parseBoolean(se.getAttributeValue(XML_ATTRIBUTE_OUTPUTEXPRESSION)));

            for(Object eo : se.getChildren(XML_ELEMENT_EXPRESSION)){
                Element ee = (Element)eo;
                String booleanExpression = ee.getAttributeValue(XML_ATTRIBUTE_BOOLEXP);
                Object value = "null";
                Element valueElement = ee.getChild(XML_ELEMENT_VALUE);
                if(ruleData.isOutputExpValue())
                    value = valueElement.getText();
                else{
                    value = ruleData.getProperty().readValueFromXML(valueElement);
                }
                Expression exp = new Expression(booleanExpression, value);
                ruleData.addExpression(exp);
            }

            for(Object pvo : se.getChildren(XML_ELEMENT_PV)){
                Element pve = (Element)pvo;
                boolean trig = true;
                if(pve.getAttribute(XML_ATTRIBUTE_TRIGGER) != null)
                    trig = Boolean.parseBoolean(pve.getAttributeValue(XML_ATTRIBUTE_TRIGGER));
                ruleData.addPV(new PVTuple(pve.getText(), trig));
            }

            result.getRuleDataList().add(ruleData);
        }
        return result;
    }

    @Override
    public void writeToXML(Element propElement) {
        for(RuleData ruleData : ((RulesInput)getPropertyValue()).getRuleDataList()){
                Element ruleElement = new Element(XML_ELEMENT_RULE);
                ruleElement.setAttribute(XML_ATTRIBUTE_NAME, ruleData.getName());
                ruleElement.setAttribute(XML_ATTRIBUTE_PROPID, ruleData.getPropId());
                ruleElement.setAttribute(XML_ATTRIBUTE_OUTPUTEXPRESSION,
                        Boolean.toString(ruleData.isOutputExpValue()));

                for(Expression exp : ruleData.getExpressionList()){
                    Element expElement = new Element(XML_ELEMENT_EXPRESSION);
                    expElement.setAttribute(XML_ATTRIBUTE_BOOLEXP, exp.getBooleanExpression());
                    Element valueElement = new Element(XML_ELEMENT_VALUE);
                    if(ruleData.isOutputExpValue())
                        valueElement.setText(exp.getValue().toString());
                    else{
                        Object savedValue = ruleData.getProperty().getPropertyValue();
                        ruleData.getProperty().setPropertyValue_IgnoreOldValue(exp.getValue());
                        ruleData.getProperty().writeToXML(valueElement);
                        ruleData.getProperty().setPropertyValue_IgnoreOldValue(savedValue);
                    }
                    expElement.addContent(valueElement);
                    ruleElement.addContent(expElement);
                }

                for(PVTuple pv : ruleData.getPVList()){
                    Element pvElement = new Element(XML_ELEMENT_PV);
                    pvElement.setText(pv.pvName);
                    pvElement.setAttribute(XML_ATTRIBUTE_TRIGGER, Boolean.toString(pv.trigger));
                    ruleElement.addContent(pvElement);
                }
                propElement.addContent(ruleElement);
        }
    }

}
