/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties;

import java.util.LinkedHashMap;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.properties.support.PropertySSHelper;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.OPIBuilderMacroUtil;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

/**The property for macros.
 * @author Xihui Chen
 *
 */
public class MacrosProperty extends AbstractWidgetProperty {

    /**
     * XML ELEMENT name <code>INCLUDE_PARENT_MACROS</code>.
     */
    public static final String XML_ELEMENT_INCLUDE_PARENT_MACROS = "include_parent_macros"; //$NON-NLS-1$

    /**Macros Property Constructor. The property value type is {@link MacrosInput}.
     * @param prop_id the property id which should be unique in a widget model.
     * @param description the description of the property,
     * which will be shown as the property name in property sheet.
     * @param category the category of the widget.
     * @param default_macros the default macros when the widget is first created.
     */
    public MacrosProperty(String prop_id, String description,
            WidgetPropertyCategory category, MacrosInput default_macros) {
        super(prop_id, description, category, default_macros);

    }

    @Override
    public Object checkValue(Object value) {
        if(value == null)
            return null;
        MacrosInput acceptableValue = null;
        if(value instanceof MacrosInput){
            acceptableValue = (MacrosInput)value;
        }

        return acceptableValue;
    }


    @Override
    public Object getPropertyValue() {
        if(executionMode == ExecutionMode.RUN_MODE && widgetModel !=null){
            MacrosInput value = ((MacrosInput) super.getPropertyValue()).getCopy();
            for(String key : value.keySet()){
                    String newS = OPIBuilderMacroUtil.replaceMacros(widgetModel, value.get(key));
                    if(!newS.equals(value.get(key))){
                        value.put(key, newS);
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
        return PropertySSHelper.getIMPL().getMacrosPropertyDescriptor(prop_id, description);
    }

    @Override
    public MacrosInput readValueFromXML(Element propElement) {
        LinkedHashMap<String, String> macros = new LinkedHashMap<String, String>();
        boolean b = true;
        for(Object oe : propElement.getChildren()){
            Element se = (Element)oe;
            if(se.getName().equals(XML_ELEMENT_INCLUDE_PARENT_MACROS))
                b = Boolean.parseBoolean(se.getText());
            else
                macros.put(se.getName(), se.getText());
        }
        return new MacrosInput(macros, b);

    }

    @Override
    public void writeToXML(Element propElement) {
        MacrosInput macros = (MacrosInput)propertyValue;
        Element be = new Element(XML_ELEMENT_INCLUDE_PARENT_MACROS);
        be.setText("" + macros.isInclude_parent_macros()); //$NON-NLS-1$
        propElement.addContent(be);
        for(String key : macros.keySet()){
            Element newElement = new Element(key);
            newElement.setText(macros.get(key));
            propElement.addContent(newElement);
        }
    }

}
