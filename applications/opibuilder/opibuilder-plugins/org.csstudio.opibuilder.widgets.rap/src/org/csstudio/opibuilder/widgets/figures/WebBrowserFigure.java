/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.figures;


import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

/**Figure for a web browser widget.
 * @author Xihui Chen
 *
 */
public class WebBrowserFigure extends AbstractWebBrowserFigure<Browser> {

    private Browser browser;


    public WebBrowserFigure(AbstractBaseEditPart editPart) {
        super(editPart, SWT.None);
    }

    @Override
    public void setUrl(String url){
        if(runmode && url.trim().length() > 0){
            if(!url.startsWith("http") && !url.contains("://")) //$NON-NLS-1$ //$NON-NLS-2$
                url = "http://" + url; //$NON-NLS-1$
            browser.setUrl(url);
        }
    }

    @Override
    protected Browser createSWTWidget(Composite parent, int style) {
        browser = new Browser(parent, SWT.None);
        return browser;
    }

    @Override
    public Browser getBrowser() {
        return browser;
    }

}
