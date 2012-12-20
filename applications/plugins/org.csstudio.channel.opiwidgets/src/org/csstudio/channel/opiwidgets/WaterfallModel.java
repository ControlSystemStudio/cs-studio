package org.csstudio.channel.opiwidgets;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.eclipse.swt.SWT;
import org.epics.pvmanager.util.TimeDuration;

public class WaterfallModel extends AbstractChannelWidgetModel {
	
	public final String ID = "org.csstudio.channel.opiwidgets.Waterfall"; //$NON-NLS-1$
	
	public static final String SORT_PROPERTY = "sort_property"; //$NON-NLS-1$	
	public static final String VALUE_RANGE = "value_range"; //$NON-NLS-1$	
	public static final String SCROLL_DIRECTION = "scroll_direction"; //$NON-NLS-1$	
	public static final String RESOLUTION = "resolution"; //$NON-NLS-1$	
	public static final String SHOW_TIME_AXIS = "show_time_axis"; //$NON-NLS-1$	
	
	@Override
	protected void configureProperties() {
		addProperty(new StringProperty(SORT_PROPERTY, "Sort Property", WidgetPropertyCategory.Basic, ""));
		addProperty(new ComboProperty(VALUE_RANGE, "Value Range", WidgetPropertyCategory.Basic, new String[] {"Metadata", "Auto"}, 0));
		addProperty(new ComboProperty(SCROLL_DIRECTION, "Scroll Direction", WidgetPropertyCategory.Basic, new String[] {"Up", "Down"}, 0));
		addProperty(new BooleanProperty(SHOW_TIME_AXIS, "Show Time Axis", WidgetPropertyCategory.Basic, true));
		addProperty(new DoubleProperty(RESOLUTION, "Resolution (ms per pixel)", WidgetPropertyCategory.Basic, 10, 0.0001, Double.MAX_VALUE));
	}

	@Override
	public String getTypeID() {
		return ID;
	}

	public boolean isShowTimeAxis() {
		return getCastedPropertyValue(SHOW_TIME_AXIS);
	}
	
	private Boolean[] values = new Boolean[] {false, true};
	private int[] scrollDirections = new int[] {SWT.UP, SWT.DOWN};
	
	public boolean isAdaptiveRange() {
		return values[getCastedPropertyValue(VALUE_RANGE)];
	}
	
	public int getScrollDirection() {
		return scrollDirections[getCastedPropertyValue(SCROLL_DIRECTION)];
	}
	
	public String getSortProperty() {
		return getCastedPropertyValue(SORT_PROPERTY);
	}
	
	public TimeDuration getResolution() {
		return TimeDuration.ms((Double) getCastedPropertyValue(RESOLUTION));
	}

}
