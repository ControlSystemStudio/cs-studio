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
import org.eclipse.ui.internal.browser.BrowserViewer;

/**Figure for a web browser widget.
 * @author Xihui Chen
 *
 */
@SuppressWarnings("restriction")
public class WebBrowserFigure extends AbstractWebBrowserFigure {


	private BrowserViewer browserViewer;
	private Browser browser;
		
	public WebBrowserFigure(AbstractBaseEditPart editPart, boolean showToolbar) {
		super(editPart);
		browserViewer = new BrowserViewer(getParentComposite(),
				showToolbar ? BrowserViewer.BUTTON_BAR
						| BrowserViewer.LOCATION_BAR : SWT.None);
		browserViewer.setLayoutData(null);
		browser = browserViewer.getBrowser();
	}
	
	public void setUrl(String url){
		if(runmode && url.trim().length() > 0)
			browserViewer.setURL(url);
	}	
	
	@Override
	public Composite getSWTWidget() {		
		return browserViewer;
	}		

	public Browser getBrowser() {
		return browser;
	}
	
}
