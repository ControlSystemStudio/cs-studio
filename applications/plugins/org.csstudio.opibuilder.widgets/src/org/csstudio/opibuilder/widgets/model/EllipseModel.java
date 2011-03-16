/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;


/**The widget model of ellipse widget.
 * 
 * @author Sven Wende, Alexander Will (class of same name in SDS)
 * @author Xihui Chen
 *
 */
public class EllipseModel extends AbstractShapeModel {
	
	
	public final String ID = "org.csstudio.opibuilder.widgets.Ellipse";
	

	@Override
	public String getTypeID() {
		return ID;
	}
	
	
}
