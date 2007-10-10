/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.components.model;

import org.csstudio.sds.components.internal.localization.Messages;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.ColorProperty;
import org.csstudio.sds.model.properties.DoubleProperty;
import org.csstudio.sds.model.properties.IntegerProperty;
import org.csstudio.sds.model.properties.OptionProperty;
import org.eclipse.swt.graphics.RGB;

/**
 * This class defines an bargraph widget model.
 * 
 * @author Kai Meyer
 * 
 */
public final class BargraphModel extends AbstractWidgetModel {
	
	/**
	 * The ID of the fill grade property.
	 */
	public static final String PROP_FILL = "fill"; //$NON-NLS-1$
	
	/**
	 * The ID of the orientation property.
	 */
	public static final String PROP_ORIENTATION = "orientation"; //$NON-NLS-1$
	
	/**
	 * The ID of the default-fill-Color property.
	 */
	public static final String PROP_DEFAULT_FILL_COLOR = "defaultFillColor";
	
	/**
	 * The ID of the fillbackground-Color property.
	 */
	public static final String PROP_FILLBACKGROUND_COLOR = "fillbackgroundColor";
	
	/**
	 * The ID of the show_value property.
	 */
	public static final String PROP_SHOW_VALUES = "showValues";
	
	/**
	 * The ID of the minimum property.
	 */
	public static final String PROP_MIN = "minimum";
	/**
	 * The ID of the lolo level property.
	 */
	public static final String PROP_LOLO_LEVEL = "loloLevel";
	/**
	 * The ID of the lo level property.
	 */
	public static final String PROP_LO_LEVEL = "loLevel";
	/**
	 * The ID of the hi level property.
	 */
	public static final String PROP_HI_LEVEL = "hiLevel";
	/**
	 * The ID of the hihi level property.
	 */
	public static final String PROP_HIHI_LEVEL = "hihiLevel";
	/**
	 * The ID of the maximum property.
	 */
	public static final String PROP_MAX = "maximum";
	/**
	 * The ID of the show status of the marks.
	 */
	public static final String PROP_SHOW_MARKS = "marksShowStatus";
	/**
	 * The ID of the show status of the marks.
	 */
	public static final String PROP_SHOW_SCALE = "scaleShowStatus";
	/**
	 * The ID of the show status of the marks.
	 */
	public static final String PROP_SCALE_SECTION_COUNT = "sectionCount";
	/**
	 * The ID of the <i>transparent</i> property.
	 */
	public static final String PROP_TRANSPARENT = "transparency";
	/**
	 * The ID of the <i>transparent</i> property.
	 */
	public static final String PROP_SHOW_ONLY_VALUE = "value_representation";

	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.sds.components.Bargraph"; //$NON-NLS-1$
		
	/**
	 * The default value of the fill grade property.
	 */
	private static final double DEFAULT_FILL = 0.25;
	
	/**
	 * The default value of the orientation property.
	 */
	private static final boolean DEFAULT_ORIENTATION_HORIZONTAL = true;

	/**
	 * The default value of the default fill color property. 
	 */
	private static final RGB DEFAULT_FILL_COLOR = new RGB(0,0,255);
	
	/**
	 * The default value of the fillbackground color property. 
	 */
	private static final RGB DEFAULT_FILLBACKGROUND_COLOR = new RGB(120,120,120);
	
	/**
	 * The default value of the show_value property. 
	 */
	private static final boolean DEFAULT_SHOW_VALUES = false;
	
	/**
	 * The default value for the show status of the marks.
	 */
	private static final int DEFAULT_SHOW_MARKS = 1;
	
	/**
	 * The default value for the show status of the scale.
	 */
	private static final int DEFAULT_SHOW_SCALE = 1;
	
	/**
	 * The default value for the section count.
	 */
	private static final int DEFAULT_SECTION_COUNT = 10;
	
	/**
	 * The labels for the MARKS_SHOW_STATUS- property.
	 */
	private static final String[] SHOW_LABELS = new String[] {"None", "Bottom / Right", "Top / Left"};
	
	/**
	 * The default value of the levels property. 
	 */
	private static final double[] DEFAULT_LEVELS = new double[]{0.0, 0.2, 0.4, 0.6, 0.8, 1.0, 1.0};

	/**
	 * Constructor.
	 */
	public BargraphModel() {
		setSize(100, 60);
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
		addProperty(PROP_FILL, new DoubleProperty(Messages.FillLevelProperty,
				WidgetPropertyCategory.Behaviour, DEFAULT_FILL));
		addProperty(PROP_ORIENTATION, new BooleanProperty("Horizontal Orientation", WidgetPropertyCategory.Behaviour, DEFAULT_ORIENTATION_HORIZONTAL));
		//Colors
		addProperty(PROP_DEFAULT_FILL_COLOR, new ColorProperty("Fill Color",WidgetPropertyCategory.Display,DEFAULT_FILL_COLOR));
		addProperty(PROP_FILLBACKGROUND_COLOR, new ColorProperty("Color Fillbackground",WidgetPropertyCategory.Display,DEFAULT_FILLBACKGROUND_COLOR));
		//Levels
		addProperty(PROP_MIN, new DoubleProperty("Minimum", WidgetPropertyCategory.Behaviour,DEFAULT_LEVELS[0]));
		addProperty(PROP_LOLO_LEVEL, new DoubleProperty("Level LOLO", WidgetPropertyCategory.Behaviour,DEFAULT_LEVELS[1]));
		addProperty(PROP_LO_LEVEL, new DoubleProperty("Level LO", WidgetPropertyCategory.Behaviour,DEFAULT_LEVELS[2]));
		addProperty(PROP_HI_LEVEL, new DoubleProperty("Level HI", WidgetPropertyCategory.Behaviour,DEFAULT_LEVELS[4]));
		addProperty(PROP_HIHI_LEVEL, new DoubleProperty("Level HIHI", WidgetPropertyCategory.Behaviour,DEFAULT_LEVELS[5]));
		addProperty(PROP_MAX, new DoubleProperty("Maximum", WidgetPropertyCategory.Behaviour,DEFAULT_LEVELS[6]));
		//Show_Value
		addProperty(PROP_SHOW_VALUES, new BooleanProperty("Show Values", WidgetPropertyCategory.Display, DEFAULT_SHOW_VALUES));
		addProperty(PROP_SHOW_MARKS, new OptionProperty("Tickmarks",WidgetPropertyCategory.Display,SHOW_LABELS,DEFAULT_SHOW_MARKS));
		addProperty(PROP_SHOW_SCALE, new OptionProperty("Scale",WidgetPropertyCategory.Display,SHOW_LABELS,DEFAULT_SHOW_SCALE));
		addProperty(PROP_SCALE_SECTION_COUNT, new IntegerProperty("SectionCcount", WidgetPropertyCategory.Display,DEFAULT_SECTION_COUNT,1,Integer.MAX_VALUE));
		addProperty(PROP_TRANSPARENT, new BooleanProperty("Transparent Background",WidgetPropertyCategory.Display,true));
		addProperty(PROP_SHOW_ONLY_VALUE, new BooleanProperty("Show only value", WidgetPropertyCategory.Display, false));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeID() {
		return ID;
	}
	
	/**
	 * Gets the fill level.
	 * 
	 * @return double
	 * 				The fill level
	 */
	public double getFillLevel() {
		return (Double) getProperty(PROP_FILL).getPropertyValue();
	}
	
	/**
	 * Gets the orientation.
	 * 
	 * @return the orientation
	 */
	public boolean getOrientation() {
		return (Boolean) getProperty(PROP_ORIENTATION).getPropertyValue();
	}
	
	/**
	 * Gets the RGB for the default fill color.
	 * @return The default fill color
	 */
	public RGB getDefaultFillColor() {
		return (RGB) getProperty(PROP_DEFAULT_FILL_COLOR).getPropertyValue();
	}
	
//	/**
//	 * Gets the RGB for lolo fill level.
//	 * @return The lolo fill level color
//	 */
//	public RGB getLoloColor() {
//		return (RGB) getProperty(PROP_LOLO_COLOR).getPropertyValue();
//	}
//	
//	/**
//	 * Gets the RGB for lo fill level.
//	 * @return The lo fill level color
//	 */
//	public RGB getLoColor() {
//		return (RGB) getProperty(PROP_LO_COLOR).getPropertyValue();
//	}
//	
//	/**
//	 * Gets the RGB for hi fill level.
//	 * @return The hi fill level color
//	 */
//	public RGB getHiColor() {
//		return (RGB) getProperty(PROP_HI_COLOR).getPropertyValue();
//	}
//	
//	/**
//	 * Gets the RGB for hihi fill level.
//	 * @return The hihi fill level color
//	 */
//	public RGB getHihiColor() {
//		return (RGB) getProperty(PROP_HIHI_COLOR).getPropertyValue();
//	}
	
	/**
	 * Gets the RGB for fillbackground.
	 * @return The fillbackground color
	 */
	public RGB getFillbackgroundColor() {
		return (RGB) getProperty(PROP_FILLBACKGROUND_COLOR).getPropertyValue();
	}
	
	/**
	 * Gets the minimum value for this model.
	 * @return double
	 * 				The minimum value
	 */
	public double getMinimum() {
		return (Double) getProperty(PROP_MIN).getPropertyValue();
	}
	
	/**
	 * Gets the lolo level for this model.
	 * @return double
	 * 				The lolo level
	 */
	public double getLoloLevel() {
		return (Double) getProperty(PROP_LOLO_LEVEL).getPropertyValue();
	}
	
	/**
	 * Gets the lo level for this model.
	 * @return double
	 * 				The lo level
	 */
	public double getLoLevel() {
		return (Double) getProperty(PROP_LO_LEVEL).getPropertyValue();
	}
	
	/**
	 * Gets the hi level for this model.
	 * @return double
	 * 				The hi level
	 */
	public double getHiLevel() {
		return (Double) getProperty(PROP_HI_LEVEL).getPropertyValue();
	}
	
	/**
	 * Gets the minimum value for this model.
	 * @return double
	 * 				The minimum value
	 */
	public double getHihiLevel() {
		return (Double) getProperty(PROP_HIHI_LEVEL).getPropertyValue();
	}
	
	/**
	 * Gets the maximum value for this model.
	 * @return double
	 * 				The maximum value
	 */
	public double getMaximum() {
		return (Double) getProperty(PROP_MAX).getPropertyValue();
	}
	
	/**
	 * Gets, if the values should be shown or not.
	 * @return boolean
	 * 				true, if the values should be shown, false otherwise
	 */
	public boolean isShowValues() {
		return (Boolean) getProperty(PROP_SHOW_VALUES).getPropertyValue();
	}
	
	/**
	 * Gets, if the marks should be shown or not.
	 * @return int
	 * 				0 = don't show, 1 = show Bottom/Right, 2 = show Top/Left
	 */
	public int getShowMarks() {
		return (Integer) getProperty(PROP_SHOW_MARKS).getPropertyValue();
	}
	
	/**
	 * Gets, if the scale should be shown or not.
	 * @return int
	 * 				0 = don't show, 1 = show Bottom/Right, 2 = show Top/Left
	 */
	public int getShowScale() {
		return (Integer) getProperty(PROP_SHOW_SCALE).getPropertyValue();
	}
	
	/**
	 * Gets the count of section in the scale.
	 * @return int
	 * 				The count of sections in  the scale
	 */
	public int getScaleSectionCount() {
		return (Integer) getProperty(PROP_SCALE_SECTION_COUNT).getPropertyValue();
	}
	
	public boolean getShowOnlyValue() {
		return (Boolean) getProperty(PROP_SHOW_ONLY_VALUE).getPropertyValue();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDoubleTestProperty() {
		return PROP_FILL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColorTestProperty() {
		return PROP_COLOR_FOREGROUND;
	}
	
//	/**
//	 * Return the Id of the widget model lolo color property.
//	 * @return String
//	 * 				The Id of the widget model lolo color property
//	 */
//	public String getLoloColorTestProperty() {
//		return PROP_LOLO_COLOR;
//	}
//	
//	/**
//	 * Return the Id of the widget model lo color property.
//	 * @return String
//	 * 				The Id of the widget model lo color property
//	 */
//	public String getLoColorTestProperty() {
//		return PROP_LO_COLOR;
//	}
//	
//	/**
//	 * Return the Id of the widget model hi color property.
//	 * @return String
//	 * 				The Id of the widget model hi color property
//	 */
//	public String getHiColorTestProperty() {
//		return PROP_HI_COLOR;
//	}
//	
//	/**
//	 * Return the Id of the widget model hihi color property.
//	 * @return String
//	 * 				The Id of the widget model hihi color property
//	 */
//	public String getHihiColorTestProperty() {
//		return PROP_HIHI_COLOR;
//	}
	
	/**
	 * Returns, if this widget should have a transparent background.
	 * @return boolean
	 * 				True, if it should have a transparent background, false otherwise
	 */
	public boolean getTransparent() {
		return (Boolean) getProperty(PROP_TRANSPARENT).getPropertyValue();
	}

}
