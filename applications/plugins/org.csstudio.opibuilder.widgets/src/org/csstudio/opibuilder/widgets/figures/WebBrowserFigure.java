package org.csstudio.opibuilder.widgets.figures;


import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.platform.ui.util.UIBundlingThread;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.internal.browser.BrowserViewer;

/**Figure for a web browser widget.
 * @author Xihui Chen
 *
 */
@SuppressWarnings("restriction")
public class WebBrowserFigure extends AbstractSWTWidgetFigure {

	
	private final static Color WHITE_COLOR = 
		CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_WHITE);
	
	private BrowserViewer browserViewer;
	private Browser browser;
	
	
	public WebBrowserFigure(Composite composite, AbstractContainerModel parentModel, boolean runmode) {
		super(composite, parentModel);
		this.runmode = runmode;
		if(runmode){
			browserViewer = new BrowserViewer(
					composite, BrowserViewer.BUTTON_BAR | BrowserViewer.LOCATION_BAR){
				@Override
				public void setBounds(int x, int y, int width,
						int height) {
					super.setBounds(x, y, width, height);
					layout();
				}
			};
			browser = browserViewer.getBrowser();
		}
	}

	public void setUrl(String url){
		if(runmode && url.trim().length() > 0)
			browserViewer.setURL(url);
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
	public Composite getSWTWidget() {
		return browserViewer;
	}	
	
	@Override
	public void dispose() {
		if(runmode){
			super.dispose();	
			//the browser must be disposed in the queue of UI thread, 
			//so that multiple browsers can be properly disposed.
			UIBundlingThread.getInstance().addRunnable(new Runnable() {			
				public void run() {				
					browserViewer.dispose();
					browserViewer = null;
					browser = null;
				}
			});
		}
		
	}

	public Browser getBrowser() {
		return browser;
	}
	
}
