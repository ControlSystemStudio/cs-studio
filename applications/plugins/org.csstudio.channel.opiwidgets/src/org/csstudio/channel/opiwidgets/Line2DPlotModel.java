/**
 * 
 */
package org.csstudio.channel.opiwidgets;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

/**
 * @author shroffk
 *
 */
public class Line2DPlotModel extends AbstractChannelWidgetModel {

	public final String ID = "org.csstudio.channel.opiwidgets.Line2DPlot"; //$NON-NLS-1$
	
	public static final String CONFIGURABLE = "configurable"; //$NON-NLS-1$	
	
	/* (non-Javadoc)
	 * @see org.csstudio.opibuilder.model.AbstractWidgetModel#configureProperties()
	 */
	@Override
	protected void configureProperties() {
		addProperty(new BooleanProperty(CONFIGURABLE, "Configurable", WidgetPropertyCategory.Behavior, false));
	}

	/* (non-Javadoc)
	 * @see org.csstudio.opibuilder.model.AbstractWidgetModel#getTypeID()
	 */
	@Override
	public String getTypeID() {
		return ID;
	}

	public boolean isConfigurable() {
		return getCastedPropertyValue(CONFIGURABLE);
	}
}
