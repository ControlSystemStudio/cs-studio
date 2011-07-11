/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.util.OPIBuilderMacroUtil;
import org.jdom.Element;

/**The property for string table.
 * @author Xihui Chen
 *
 */
public class StringTableProperty extends AbstractWidgetProperty {
	
	/**
	 * XML ELEMENT name for single item. This is for backward compatibility purpose.
	 */
	public static final String XML_ELEMENT_SINGLE_ITEM = "s"; //$NON-NLS-1$
	
	/**
	 * XML ELEMENT name for a row.
	 */
	public static final String XML_ELEMENT_ROW= "row"; //$NON-NLS-1$
	
	/**
	 * XML ELEMENT name for a column.
	 */
	public static final String XML_ELEMENT_COLUMN= "col"; //$NON-NLS-1$
	
	
	private String[] titles;
	
	private int colNumber;
	

	/**StringList Property Constructor. The property value type is {@link List}.
	 * @param prop_id the property id which should be unique in a widget model.
	 * @param description the description of the property,
	 * which will be shown as the property name in property sheet.
	 * @param category the category of the widget.
	 * @param defaultValue the default value when the widget is first created.
	 * @param titles the title for each column. The length of this array is the number of columns.
	 */
	public StringTableProperty(String prop_id, String description,
			WidgetPropertyCategory category, List<String[]> default_value, String[] titles) {
		super(prop_id, description, category, default_value);
		this.titles = titles;
		colNumber = titles.length;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object checkValue(Object value) {
		if(value == null)
			return null;
		List<String[]> acceptableValue = null;
		if(value instanceof List){	
			if(((List) value).size() == 0 || 
					(((List) value).size() > 0 && ((List) value).get(0) instanceof String[]))
			acceptableValue = (List<String[]>)value;			
		}		
		return acceptableValue;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public Object getPropertyValue() {
		if(widgetModel !=null && widgetModel.getExecutionMode() == ExecutionMode.RUN_MODE){
			List<String[]> result= new ArrayList<String[]>();
			for(String[] item : (List<String[]>) super.getPropertyValue()){
				String[] temp = new String[item.length];
				int i=0;
				for(String e : item){
				temp[i++] = OPIBuilderMacroUtil.replaceMacros(
					widgetModel, e);
				}
				result.add(temp);
			}
			return result;
		}else
			return super.getPropertyValue();
	}
	
	
	

	@Override
	public List<String[]> readValueFromXML(Element propElement) {
		List<String[]> result = new ArrayList<String[]>();		
		for(Object oe : propElement.getChildren()){
			Element re = (Element)oe;
			String[] row = new String[colNumber];
			if(re.getName().equals(XML_ELEMENT_SINGLE_ITEM)){
				Arrays.fill(row, ""); //$NON-NLS-1$
				row[0] = re.getText();						
			}else if(re.getName().equals(XML_ELEMENT_ROW)){
				int i=0;
				for(Object oc : re.getChildren()){
					row[i++] = ((Element)oc).getText();
				}
			}
			result.add(row);
		}		
		return result;
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void writeToXML(Element propElement) {
		List<String[]> data = (List<String[]>)propertyValue;		
		for(String row[] : data){
			Element rowElement = new Element(XML_ELEMENT_ROW);			
			for(String e : row){
				Element colElement = new Element(XML_ELEMENT_COLUMN);
				colElement.setText(e);
				rowElement.addContent(colElement);
			}			
			propElement.addContent(rowElement);
		}
	}

}
