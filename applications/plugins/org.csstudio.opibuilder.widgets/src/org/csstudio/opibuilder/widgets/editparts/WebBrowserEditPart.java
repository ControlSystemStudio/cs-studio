/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.WebBrowserFigure;
import org.csstudio.opibuilder.widgets.model.WebBrowserModel;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

/**The editpart of web browser widget.
 * 
 * @author Xihui Chen
 * 
 */
public final class WebBrowserEditPart extends AbstractBaseEditPart {
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		final WebBrowserModel model = getWidgetModel();
		WebBrowserFigure figure = new WebBrowserFigure(
				(Composite) getViewer().getControl(), model.getParent(),
				getExecutionMode() == ExecutionMode.RUN_MODE, model.isShowToolBar());
		figure.setUrl(model.getURL());
		figure.setRunMode(getExecutionMode() == ExecutionMode.RUN_MODE);
		return figure;
	}
	
	@Override
	public WebBrowserModel getWidgetModel() {
		return (WebBrowserModel)getModel();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		
		// URL
		IWidgetPropertyChangeHandler urlHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				((WebBrowserFigure)refreshableFigure).setUrl((String)newValue);
				return false;
			}
		};
		setPropertyChangeHandler(WebBrowserModel.PROP_URL, urlHandler);
	}	
	
	@Override
	public void deactivate() {
		((WebBrowserFigure)getFigure()).dispose();		
		super.deactivate();
	}
	
	public Browser getBrowser(){
		return ((WebBrowserFigure)getFigure()).getBrowser();
	}
	
}
