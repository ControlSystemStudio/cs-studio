package org.csstudio.opibuilder.widgets.model;


/**A polygon widget model
 * @author Sven Wende, Alexander Will (class of same name in SDS)
 * @author Xihui Chen
 *
 */
public class PolygonModel extends AbstractPolyModel {

	
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.polygon"; //$NON-NLS-1$	
	
	
	@Override
	public String getTypeID() {
		return ID;
	}

}
