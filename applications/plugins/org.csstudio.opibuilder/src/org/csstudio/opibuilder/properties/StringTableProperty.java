/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties;

import java.util.List;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.properties.support.PropertySSHelper;
import org.csstudio.opibuilder.util.OPIBuilderMacroUtil;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

/**The property for string table.
 * @author Xihui Chen
 *
 */
public class StringTableProperty extends AbstractWidgetProperty {
	
	
	public class TitlesProvider {
		
		public String[] getTitles(){
			return titles;
		}
	}
	
	/**
	 * XML ELEMENT name for a row.
	 */
	public static final String XML_ELEMENT_ROW= "row"; //$NON-NLS-1$
	
	/**
	 * XML ELEMENT name for a column.
	 */
	public static final String XML_ELEMENT_COLUMN= "col"; //$NON-NLS-1$
	
	
	private String[] titles;
	
	private TitlesProvider titlesProvider;
	

	/**StringList Property Constructor. The property value type is 2D string array.
	 * @param prop_id the property id which should be unique in a widget model.
	 * @param description the description of the property,
	 * which will be shown as the property name in property sheet.
	 * @param category the category of the widget.
	 * @param defaultValue the default value when the widget is first created. It cannot be null.
	 * @param titles the title for each column. 
	 * The length of titles array is the number of columns. it can be null if the property is not
	 * visible.
	 */
	public StringTableProperty(String prop_id, String description,
			WidgetPropertyCategory category, String[][] default_value, String[] titles) {
		super(prop_id, description, category, default_value);
		this.titles = titles;
		titlesProvider = new TitlesProvider();
	}

	@Override
	public Object checkValue(Object value) {
		if(value == null)
			return null;
		String[][] acceptableValue = null;
		if(value instanceof String[][]){				
			acceptableValue = (String[][])value;			
		}		
		return acceptableValue;
	}

	
	@Override
	public Object getPropertyValue() {
		if(widgetModel !=null && widgetModel.getExecutionMode() == ExecutionMode.RUN_MODE){
			String[][] originValue = (String[][])super.getPropertyValue();
			if(originValue.length <=0)
				return originValue;
			String[][] result= new String[originValue.length][originValue[0].length];
			for(int i=0; i<originValue.length; i++){
				for(int j=0; j<originValue[0].length; j++){
					result[i][j] = OPIBuilderMacroUtil.replaceMacros(
					widgetModel, originValue[i][j]);
				}				
			}
			return result;
		}else
			return super.getPropertyValue();
	}
	
	/**
	 * @param titles the titles for each column.
	 */
	public void setTitles(String[] titles) {
		this.titles = titles;
	}
	
	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		if(PropertySSHelper.getIMPL() == null)
			return null;
		PropertyDescriptor propertyDescriptor = 
				PropertySSHelper.getIMPL().getStringTablePropertyDescriptor(
						prop_id, description, titlesProvider);
		return propertyDescriptor;
	}

	@Override
	public String[][] readValueFromXML(Element propElement) {
		List<?> rowChildren = propElement.getChildren();
		if(rowChildren.size() == 0)
			return new String[0][0];
		String[][] result = 
				new String[rowChildren.size()][((Element)rowChildren.get(0)).getChildren().size()];		
		int i=0, j=0;
		for(Object oe : rowChildren){
			Element re = (Element)oe;
			if(re.getName().equals(XML_ELEMENT_ROW)){
				j=0;
				for(Object oc : re.getChildren()){
					result[i][j++] = ((Element)oc).getText();
				}
				i++;
			}
		}		
		return result;
		
	}

	@Override
	public void writeToXML(Element propElement) {
		String[][] data = (String[][]) propertyValue;		
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
