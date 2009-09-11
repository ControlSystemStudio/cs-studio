package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.widgets.figures.PolylineFigure;
import org.csstudio.opibuilder.widgets.util.GraphicsUtil;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;



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
	
	public static final String PROP_ARROW_LENGTH = "arrowLength"; //$NON-NLS-1$
	
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
		addProperty(new IntegerProperty(PROP_ARROW_LENGTH, "Arrow Length", 
				WidgetPropertyCategory.Display, 20, 1, 1000));
		
	}
	
	@Override
	public String getTypeID() {
		return ID;
	}
	
	public int getArrowType(){
		return (Integer)getCastedPropertyValue(PROP_ARROW); 
	}
	
	public void setArrowType(int type){
		setPropertyValue(PROP_ARROW, type);
	}
	
	
	public int getArrowLength(){
		return (Integer)getCastedPropertyValue(PROP_ARROW_LENGTH);
	}
	
	public void setArrowLength(int value){
		setPropertyValue(PROP_ARROW_LENGTH, value);
	}
	
	
	
	/**
	 * Sets the specified _points for the polygon.
	 * 
	 * @param points
	 *            the polygon points
	 * @param rememberPoints true if the zero relative points should be remembered, false otherwise.
	 */
	@Override
	public void setPoints(final PointList points,
			final boolean rememberPoints) {
		if (points.size() > 0) {
			PointList copy = points.getCopy();
			if (rememberPoints) {
				rememberZeroDegreePoints(copy);
			}
			getProperty(PROP_POINTS).setPropertyValue(points);
			
			updateBounds();
		}
	}


	/**
	 * Update the figure bounds based on points and arrows.
	 */
	public void updateBounds() {
		Rectangle bounds = GraphicsUtil.getPointsBoundsWithArrows(getPoints(),
				ArrowType.values()[getArrowType()], getArrowLength(), PolylineFigure.ARROW_ANGLE);
		getProperty(PROP_XPOS).setPropertyValue(bounds.x);
		getProperty(PROP_YPOS).setPropertyValue(bounds.y);
		getProperty(PROP_WIDTH).setPropertyValue(bounds.width);
		getProperty(PROP_HEIGHT).setPropertyValue(bounds.height);
	}
	
	


	public boolean isFillArrow() {
		return (Boolean)getCastedPropertyValue(PROP_FILL_ARROW);
	}
	
	public void setFillArrow(boolean fill){
		setPropertyValue(PROP_FILL_ARROW, fill);
	}

}
