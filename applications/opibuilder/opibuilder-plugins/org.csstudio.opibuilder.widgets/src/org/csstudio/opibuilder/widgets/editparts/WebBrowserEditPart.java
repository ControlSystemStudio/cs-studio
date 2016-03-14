/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.AbstractWebBrowserFigure;
import org.csstudio.opibuilder.widgets.model.WebBrowserModel;
import org.csstudio.opibuilder.widgets.util.SingleSourceHelper;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.browser.Browser;

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
        final AbstractWebBrowserFigure<?> figure = SingleSourceHelper.createWebBrowserFigure(
                this, model.isShowToolBar());
        figure.setUrl(model.getURL());
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
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                ((AbstractWebBrowserFigure<?>)refreshableFigure).setUrl((String)newValue);
                return false;
            }
        };
        setPropertyChangeHandler(WebBrowserModel.PROP_URL, urlHandler);
    }

    public Browser getBrowser(){
        return ((AbstractWebBrowserFigure<?>)getFigure()).getBrowser();
    }

}
