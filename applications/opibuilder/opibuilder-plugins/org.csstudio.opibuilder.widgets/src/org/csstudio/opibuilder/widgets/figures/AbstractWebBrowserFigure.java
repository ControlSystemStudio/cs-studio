/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.figures;


import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Control;

/**Figure for a web browser widget.
 * @author Xihui Chen
 *
 */
public abstract class AbstractWebBrowserFigure <T extends Control> extends AbstractSWTWidgetFigure<T> {	
	

	public AbstractWebBrowserFigure(AbstractBaseEditPart editpart, int style) {
		super(editpart, style);
	}

	public abstract void setUrl(String url);
	
	public abstract Browser getBrowser() ;
	
}
