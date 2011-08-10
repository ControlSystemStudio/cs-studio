package org.csstudio.display.waterfall.opi;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.epics.pvmanager.util.TimeDuration;

public class WaterfallModel extends AbstractWidgetModel {
	
	public final String ID = "org.csstudio.display.waterfall.opi.Waterfall"; //$NON-NLS-1$
	
	public static final String INPUT_TEXT = "inputText"; //$NON-NLS-1$	
	public static final String SORT_PROPERTY = "sortProperty"; //$NON-NLS-1$	
	public static final String ADAPTIVE_RANGE = "adaptiveRange"; //$NON-NLS-1$	
	public static final String SCROLL_DOWN = "scrollDown"; //$NON-NLS-1$	
	public static final String PIXEL_DURATION = "pixelDuration"; //$NON-NLS-1$	
	public static final String SHOW_RANGE = "showRange"; //$NON-NLS-1$	
	
	@Override
	protected void configureProperties() {
		addProperty(new StringProperty(INPUT_TEXT, "PV name or tag", WidgetPropertyCategory.Basic, ""));
		addProperty(new StringProperty(SORT_PROPERTY, "Sort property", WidgetPropertyCategory.Basic, ""));
		addProperty(new ComboProperty(ADAPTIVE_RANGE, "Color range", WidgetPropertyCategory.Basic, new String[] {"Metadata", "Auto"}, 0));
		addProperty(new ComboProperty(SCROLL_DOWN, "Scroll", WidgetPropertyCategory.Basic, new String[] {"Up", "Down"}, 0));
		addProperty(new BooleanProperty(SHOW_RANGE, "Show range", WidgetPropertyCategory.Basic, true));
		addProperty(new DoubleProperty(PIXEL_DURATION, "Resolution (ms per pixel)", WidgetPropertyCategory.Basic, 10, 0.0001, Double.MAX_VALUE));
	}

	@Override
	public String getTypeID() {
		return ID;
	}
	
	public String getInputText() {
		return getCastedPropertyValue(INPUT_TEXT);
	}

	public boolean isShowRange() {
		return getCastedPropertyValue(SHOW_RANGE);
	}
	
	private Boolean[] values = new Boolean[] {false, true};
	
	public boolean isAdaptiveRange() {
		return values[getCastedPropertyValue(ADAPTIVE_RANGE)];
	}
	
	public boolean isScrollDown() {
		return values[getCastedPropertyValue(SCROLL_DOWN)];
	}
	
	public String getSortProperty() {
		return getCastedPropertyValue(SORT_PROPERTY);
	}
	
	public TimeDuration getPixelDuration() {
		return TimeDuration.ms((Double) getCastedPropertyValue(PIXEL_DURATION));
	}

}
