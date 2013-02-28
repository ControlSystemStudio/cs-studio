/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.util;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.graphics.Image;

/**RAP implementation of ResourceUtilHelper.
 * @author Xihui Chen
 *
 */
public class ResourceUtilSSHelperImpl extends ResourceUtilSSHelper{
	private static final String NOT_IMPLEMENTED = 
			"This method has not been implemented yet for RAP";

	@Override
	public Image getScreenShotImage(GraphicalViewer viewer) {
		throw new RuntimeException(NOT_IMPLEMENTED);
	}	
}
