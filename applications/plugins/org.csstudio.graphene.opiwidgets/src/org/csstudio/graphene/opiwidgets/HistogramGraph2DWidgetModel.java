/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.HistogramGraph2DWidget;
import org.csstudio.opibuilder.widgets.extra.AbstractSelectionWidgetModelDescription;



/**
 * @author shroffk
 * 
 */
public class HistogramGraph2DWidgetModel extends AbstractPointDatasetGraph2DWidgetModel {

	public HistogramGraph2DWidgetModel() {
		super(AbstractSelectionWidgetModelDescription.newModelFrom(HistogramGraph2DWidget.class));
	}

	public final String ID = "org.csstudio.graphene.opiwidgets.HistogramGraph2D"; //$NON-NLS-1$

	@Override
	public String getTypeID() {
		return ID;
	}
	
	@Override
	protected String getDataType() {
		return "VNumberArray";
	}

}
