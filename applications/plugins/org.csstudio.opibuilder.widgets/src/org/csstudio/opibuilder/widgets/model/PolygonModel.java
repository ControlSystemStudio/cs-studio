/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
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
