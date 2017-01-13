/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.eclipse.swt.graphics.RGB;

/**The model for web browser widget.
 * @author Xihui Chen
 *
 */
public class WebBrowserModel extends AbstractWidgetModel {


    public final String ID = "org.csstudio.opibuilder.widgets.webbrowser";//$NON-NLS-1$
    public static final String PROP_URL = "url";//$NON-NLS-1$
    public static final String PROP_SHOW_TOOLBAR = "show_toolbar";//$NON-NLS-1$

    public WebBrowserModel() {
        setBorderStyle(BorderStyle.LINE);
        setBorderColor(new RGB(192, 192, 192));
        setSize(450, 300);
    }

    @Override
    protected void configureProperties() {
        addProperty(new StringProperty(
                PROP_URL, "URL", WidgetPropertyCategory.Basic, "")); //$NON-NLS-2$
        addProperty(new BooleanProperty(PROP_SHOW_TOOLBAR, "Show Toolbar",
                WidgetPropertyCategory.Display, true));
        setPropertyVisible(PROP_FONT, false);
        setPropertyVisible(PROP_FONT_PIXELS, false);
    }

    public String getURL(){
        return (String)getPropertyValue(PROP_URL);
    }


    @Override
    public String getTypeID() {
        return ID;
    }

    public boolean isShowToolBar() {
        return (Boolean)getPropertyValue(PROP_SHOW_TOOLBAR);
    }

}
