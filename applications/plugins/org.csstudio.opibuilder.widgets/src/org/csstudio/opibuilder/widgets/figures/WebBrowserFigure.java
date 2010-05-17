package org.csstudio.opibuilder.widgets.figures;


import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.util.UIBundlingThread;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

/**Figure for a web browser widget.
 * @author Xihui Chen
 *
 */
public class WebBrowserFigure extends AbstractSWTWidgetFigure {

	
	private final static Color WHITE_COLOR = 
		CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_WHITE);
	

	private Browser browser;
	
	public WebBrowserFigure(Composite composite, AbstractContainerModel parentModel, boolean runmode) {
		super(composite, parentModel);
		this.runmode = runmode;
		if(runmode){
			browser = new Browser(composite, SWT.NONE);
			browser.setVisible(false);	
		}
	}

	public void setUrl(String url){
		if(runmode && url.trim().length() > 0)
			browser.setUrl(url);
	}

	@Override
	protected void paintClientArea(Graphics graphics) {			
		//draw this so that it can be seen in the outline view
		if(!runmode){
			graphics.setBackgroundColor(WHITE_COLOR);
			graphics.fillRectangle(getClientArea());
		}
		super.paintClientArea(graphics);	
	}
	
	@Override
	public Browser getSWTWidget() {
		return browser;
	}	
	
	@Override
	public void dispose() {
		if(runmode){
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
	
}
