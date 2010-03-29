package org.csstudio.opibuilder.widgets.figures;


import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.util.UIBundlingThread;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

/**Figure for a web browser widget.
 * @author Xihui Chen
 *
 */
public class WebBrowserFigure extends AbstractSWTWidgetFigure {

	
	private final static Color GRAY_COLOR = 
		CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_GRAY);
	private final static Color DARK_GRAY_COLOR = 
		CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_DARK_GRAY);

	private Browser browser;
	
	public WebBrowserFigure(Composite composite, AbstractContainerModel parentModel) {
		super(composite, parentModel);
		browser = new Browser(composite, SWT.NONE);
		browser.setVisible(false);		
	}

	public void setUrl(String url){
		if(url.trim().length() > 0)
			browser.setUrl(url);
	}

	@Override
	protected void paintClientArea(Graphics graphics) {			
		//draw this so that it can be seen in the outline view
		if(!runmode){
			Rectangle clientArea = getClientArea().getCopy().shrink(2, 2);
			graphics.setBackgroundColor(GRAY_COLOR);
			graphics.fillRectangle(clientArea);
			graphics.setForegroundColor(DARK_GRAY_COLOR);
			graphics.drawRectangle(
					new Rectangle(clientArea.getLocation(), clientArea.getSize().shrink(1, 1)));
		}
		super.paintClientArea(graphics);	
	}
	
	@Override
	public Browser getSWTWidget() {
		return browser;
	}	
	
	@Override
	public void dispose() {
		super.dispose();	
		//the browser must be disposed in the queue of UI thread, 
		//so that multiple browsers can be properly disposed.
		UIBundlingThread.getInstance().addRunnable(new Runnable() {			
			public void run() {				
				browser.dispose();
				browser = null;
			}
		});
	}
	
}
