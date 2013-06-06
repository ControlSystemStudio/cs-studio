/**
 * 
 */
package org.csstudio.graphene.opiwidgets;

import org.csstudio.graphene.BubbleGraph2DWidget;


/**
 * @author shroffk
 * 
 */
public class BubbleGraph2DWidgetModel extends AbstractPointDatasetGraph2DWidgetModel {
	
	public BubbleGraph2DWidgetModel() {
		super(BubbleGraph2DWidget.class);
	}

	public final String ID = "org.csstudio.graphene.opiwidgets.BubbleGraph2D"; //$NON-NLS-1$

	@Override
	public String getTypeID() {
		return ID;
	}

}
