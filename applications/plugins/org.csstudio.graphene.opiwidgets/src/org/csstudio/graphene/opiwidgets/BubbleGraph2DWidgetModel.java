/**
 * 
 */
package org.csstudio.graphene.opiwidgets;



/**
 * @author shroffk
 * 
 */
public class BubbleGraph2DWidgetModel extends AbstractPointDatasetGraph2DWidgetModel {
	
	public BubbleGraph2DWidgetModel() {
		super(true);
	}

	public final String ID = "org.csstudio.graphene.opiwidgets.BubbleGraph2D"; //$NON-NLS-1$

	@Override
	public String getTypeID() {
		return ID;
	}

}
