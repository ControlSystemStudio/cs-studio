/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.widgets.editparts.TextDirectEditPolicy;

/**
 * The model for widgets have text property,
 *  so the widget can be directly edited by installing {@link TextDirectEditPolicy}. 
 * @author Xihui Chen
 *
 */
public interface ITextModel {

	public void setText(String text);
	
	public String getText();
	
	
}
