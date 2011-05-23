/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.platform.ui.util.CustomMediaFactory;

/**The model for text indicator.
 * @author Xihui Chen
 *
 */
public class TextIndicatorModel extends LabelModel {
	
	public enum FormatEnum {
		DEFAULT("Default"),
		DECIAML("Decimal"),
		EXP("Exponential"),
		HEX("Hex 32"),		
		STRING("String"),
		HEX64("Hex 64");
		
		private String description;
		private FormatEnum(String description) {
			this.description = description;
		}
		
		@Override
		public String toString() {
			return description;
		}
		
		public static String[] stringValues(){
			String[] result = new String[values().length];
			int i =0 ;
			for(FormatEnum f : values()){
				result[i++] = f.toString();
			}
			return result;
		}
	}
	
	public static final String PROP_FORMAT_TYPE = "format_type";	//$NON-NLS-1$
	public static final String PROP_PRECISION = "precision";	//$NON-NLS-1$
	public static final String PROP_PRECISION_FROM_DB = "precision_from_pv";	//$NON-NLS-1$
	public static final String PROP_SHOW_UNITS = "show_units";	
	
	
	public TextIndicatorModel() {
		setSize(100, 20);
		setForegroundColor(CustomMediaFactory.COLOR_BLACK);
		setTooltip("$(" + PROP_PVNAME + ")\n" + "$(" + PROP_PVVALUE + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
	
	
	@Override
	public String getTypeID() {
		return "org.csstudio.opibuilder.widgets.TextUpdate"; //$NON-NLS-1$;
	}
	
	
	@Override
	protected void configureProperties() {
		pvModel = true;
		super.configureProperties();	
		WidgetPropertyCategory category = new WidgetPropertyCategory(){
			@Override
			public String toString() {
				return "Format";
			}
		};
		addProperty(new ComboProperty(PROP_FORMAT_TYPE, "Format Type", category, FormatEnum.stringValues(), 0));
		addProperty(new IntegerProperty(PROP_PRECISION, "Precision", category, 0, 0, 100));
		addProperty(new BooleanProperty(PROP_PRECISION_FROM_DB, "Precision from PV", category, true));
		addProperty(new BooleanProperty(PROP_SHOW_UNITS, "Show Units", category, true));
		
		setPropertyValue(PROP_TEXT, "######");
		setPropertyValue(PROP_ALIGN_H, 0);
		setPropertyValue(PROP_ALIGN_V, 1);
	}
	
	public FormatEnum getFormat(){
		return FormatEnum.values()[(Integer)getCastedPropertyValue(PROP_FORMAT_TYPE)];
	}
	
	public int getPrecision(){
		return (Integer)getCastedPropertyValue(PROP_PRECISION);
	}
	
	public boolean isPrecisionFromDB(){
		return (Boolean)getCastedPropertyValue(PROP_PRECISION_FROM_DB);
	}
	
	public boolean isShowUnits(){
		return (Boolean)getCastedPropertyValue(PROP_SHOW_UNITS);
	}
}
