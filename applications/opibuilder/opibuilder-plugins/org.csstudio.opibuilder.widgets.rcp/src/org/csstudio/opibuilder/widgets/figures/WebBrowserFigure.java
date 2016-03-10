/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.figures;


import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.internal.browser.BrowserViewer;

/**Figure for a web browser widget.
 * @author Xihui Chen
 *
 */
@SuppressWarnings("restriction")
public class WebBrowserFigure extends AbstractWebBrowserFigure<BrowserViewer> {


    private BrowserViewer browserViewer;
    private Browser browser;

    private String errorMassage;

    public WebBrowserFigure(AbstractBaseEditPart editPart, boolean showToolbar) {
        super(editPart, showToolbar ? BrowserViewer.BUTTON_BAR
                | BrowserViewer.LOCATION_BAR : SWT.None);
    }

    @Override
    public void setUrl(String url){
        if(runmode && url.trim().length() > 0)
            try {
                browserViewer.setURL(url);
            } catch (Exception e) {
                errorMassage = "Failed to set URL to the web browser widget.\n" +
                        "It may not available on your platform.\n" + e;
                ErrorHandlerUtil.handleError(errorMassage, e);
            }
    }

    @Override
    protected void paintOutlineFigure(Graphics graphics) {
        super.paintOutlineFigure(graphics);
        if(errorMassage != null){
            graphics.pushState();
            graphics.setBackgroundColor(ColorConstants.white);
            Rectangle clientArea = getClientArea();
            graphics.fillRectangle(clientArea);
            if(getBorder() == null){
                graphics.setForegroundColor(ColorConstants.gray);
                graphics.drawRectangle(getBounds());
            }
            graphics.setForegroundColor(ColorConstants.red);
            graphics.drawText(errorMassage, clientArea.x, clientArea.y);
            graphics.popState();
        }

    }

    @Override
    public Browser getBrowser() {
        return browser;
    }

    @Override
    protected  BrowserViewer createSWTWidget(Composite parent, int style) {
        try {
            browserViewer = new BrowserViewer(parent, style);
            browserViewer.setLayoutData(null);
            browser = browserViewer.getBrowser();
        } catch (Exception e) {
            errorMassage = "Failed to create web browser widget.\n" +
                    "It may not available on your platform.\n" + e;;
            ErrorHandlerUtil.handleError(errorMassage, e);
        }
        return browserViewer;

    }

}
