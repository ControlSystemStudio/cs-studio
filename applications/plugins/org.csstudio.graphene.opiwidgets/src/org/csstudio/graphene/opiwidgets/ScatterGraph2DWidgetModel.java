/**
 * 
 */
package org.csstudio.graphene.opiwidgets;



/**
 * @author shroffk
 * 
 */
public class ScatterGraph2DWidgetModel extends AbstractPointDatasetGraph2DWidgetModel {

	public ScatterGraph2DWidgetModel() {
		super(true);
	}

	public final String ID = "org.csstudio.graphene.opiwidgets.ScatterGraph2D"; //$NON-NLS-1$

	@Override
	public String getTypeID() {
		return ID;
	}

}
