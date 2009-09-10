package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;



public class PolyLineModel extends AbstractPolyModel {

	
	public enum ArrowType{
		None,
		From,
		To,
		Both;
		
		public static String[] stringValues(){
			String[] sv = new String[values().length];
			int i=0;
			for(ArrowType p : values())
				sv[i++] = p.toString();
			return sv;
		}
	}
	
	
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.polyline"; //$NON-NLS-1$	
	
	
	public static final String PROP_ARROW = "arrow";//$NON-NLS-1$
	
	public static final String PROP_FILL_ARROW = "fillArrow"; //$NON-NLS-1$
	
	public PolyLineModel() {
		setLineWidth(1);
		
	}
	
	
	@Override
	protected void configureProperties() {
		super.configureProperties();
		addProperty(new ComboProperty(PROP_ARROW, "Arrows", 
				WidgetPropertyCategory.Display, ArrowType.stringValues(), 0));
		addProperty(new BooleanProperty(PROP_FILL_ARROW, "Fill Arrow", 
				WidgetPropertyCategory.Display, true));
		
	}
	
	@Override
	public String getTypeID() {
		return ID;
	}

}
