/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.IntensityGraph2DWidget;
import org.csstudio.opibuilder.widgets.extra.AbstractSelectionWidgetModelDescription;

/**
 * @author shroffk
 * 
 */
public class IntensityGraph2DWidgetModel extends AbstractPointDatasetGraph2DWidgetModel {
	
	public IntensityGraph2DWidgetModel() {
		super(AbstractSelectionWidgetModelDescription.newModelFrom(IntensityGraph2DWidget.class));
	}

	public final String ID = "org.csstudio.graphene.opiwidgets.IntensityGraph2D"; //$NON-NLS-1$

	@Override
	public String getTypeID() {
		return ID;
	}

	@Override
	protected void configureProperties() {
		super.configureProperties();
	}

}
