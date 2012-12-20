/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.opibuilder.widgets.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.StringTableProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

/**Model for the Table widget.
 * @author Xihui Chen
 *
 */
public class TableModel extends AbstractWidgetModel {
	
	private static final int DEFAULT_COLUMN_WIDTH = 60;

	/**
	 * True if the table cell is editable. If false, it is still selectable,
	 * which is different with disabled.
	 */
	public static final String PROP_EDITABLE = "editable"; //$NON-NLS-1$
	
	/**
	 *Column headers. 
	 */
	public static final String PROP_COLUMN_HEADERS = "column_headers"; //$NON-NLS-1$
	
	/**
	 *Number of columns.
	 */
	public static final String PROP_COLUMNS_COUNT = "columns_count"; //$NON-NLS-1$
	
	
	/**
	 *Default Content of the table.
	 */
	public static final String PROP_DEFAULT_CONTENT = "default_content"; //$NON-NLS-1$

	
	
	/**
	 *Column header visible.
	 */
	public static final String PROP_COLUMN_HEADER_VISIBLE= "column_header_visible"; //$NON-NLS-1$
	
		
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.table"; //$NON-NLS-1$	

	@Override
	protected void configureProperties() {
		
		addProperty(new BooleanProperty(
				PROP_EDITABLE, "Editable", WidgetPropertyCategory.Behavior, true));
		
			
		StringTableProperty contentProperty = new StringTableProperty(
				PROP_DEFAULT_CONTENT, "Default Content", WidgetPropertyCategory.Display, 
				new String[][]{{""}}, new String[]{""});
		
		addProperty(contentProperty);
		
		StringTableProperty headersProperty = new StringTableProperty(
				PROP_COLUMN_HEADERS, "Column Headers",WidgetPropertyCategory.Display,
				new String[0][0], new String[]{"Column Title", "Column Width", "Editable(yes/no)"});
		
		addProperty(headersProperty);
		
		IntegerProperty columnsCountProperty = new IntegerProperty(
				PROP_COLUMNS_COUNT, "Columns Count", WidgetPropertyCategory.Display, 1, 1, 10000);
		
		addProperty(columnsCountProperty);
		
		addProperty(new BooleanProperty(
				PROP_COLUMN_HEADER_VISIBLE, "Column Header Visible", 
				WidgetPropertyCategory.Display, true));
		
		headersProperty.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {			
				updateContentPropertyTitles();
			}			
		});
		
		columnsCountProperty.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				updateContentPropertyTitles();
			}
		});	

	}

	public void updateContentPropertyTitles() {

		String[] headers = getColumnHeaders();
		int c = getColumnsCount();
		if(headers.length > c)
			c = headers.length;
		
		String[] titles = new String[c];

		for (int i = 0; i < titles.length; i++) {
			if (i < headers.length) {
				titles[i] = headers[i];
			} else
				titles[i] = ""; //$NON-NLS-1$
		}
		((StringTableProperty) getProperty(PROP_DEFAULT_CONTENT)).setTitles(titles);
	}
	
	
	public boolean isEditable(){
		return (Boolean)getPropertyValue(PROP_EDITABLE);
	}
	
	public boolean[] isColumnEditable(){
		String[][] headers = (String[][]) getPropertyValue(PROP_COLUMN_HEADERS);
		boolean[] r = new boolean[headers.length];
		if(headers.length ==0 || headers[0].length <3){
			Arrays.fill(r, true);
			return r;
		}
		for(int i=0; i<headers.length; i++){
			r[i] = headers[i][2].equals("no")?false:true; //$NON-NLS-1$
		}
		return r;			
	}
	
	public String[] getColumnHeaders(){
		String[][] headers = (String[][]) getPropertyValue(PROP_COLUMN_HEADERS);
		String[] r = new String[headers.length];
		for(int i=0; i<headers.length; i++){
			r[i] = headers[i][0];
		}
		return r;			
	}
	
	public int[] getColumnWidthes(){
		String[][] headers = (String[][]) getPropertyValue(PROP_COLUMN_HEADERS);
		int[] r = new int[headers.length];
		for(int i=0; i<headers.length; i++){
			try {
				r[i] = Integer.valueOf(headers[i][1]);
			} catch (Exception e) {
				r[i] = DEFAULT_COLUMN_WIDTH;
			}
		}
		return r;	
	}
	
	public int getColumnsCount(){
		return (Integer)getPropertyValue(PROP_COLUMNS_COUNT);
	}
	
	public String[][] getDefaultContent(){
		return (String[][])getPropertyValue(PROP_DEFAULT_CONTENT);
	}
	
	public boolean isColumnHeaderVisible(){
		return (Boolean)getPropertyValue(PROP_COLUMN_HEADER_VISIBLE);
	}
	


	/* (non-Javadoc)
	 * @see org.csstudio.opibuilder.model.AbstractWidgetModel#getTypeID()
	 */
	@Override
	public String getTypeID() {
		return ID;
	}

}
