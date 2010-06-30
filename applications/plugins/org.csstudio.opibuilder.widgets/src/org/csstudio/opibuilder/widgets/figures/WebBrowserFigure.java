package org.csstudio.opibuilder.widgets.figures;


import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.util.UIBundlingThread;
import org.csstudio.platform.ui.util.CustomMediaFactory;
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
			browserViewer = new BrowserViewer(composite, BrowserViewer.BUTTON_BAR | BrowserViewer.LOCATION_BAR);
			browser = browserViewer.getBrowser();
//			toolbarArmedBrowser = new Composite(composite, SWT.None);
//			toolbarArmedBrowser.setLayout(new GridLayout(3, false));
//			ToolBar toolbar = new ToolBar(toolbarArmedBrowser, SWT.NONE);
//			ToolItem itemBack = new ToolItem(toolbar, SWT.PUSH);
//			itemBack.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(
//					Activator.PLUGIN_ID, "icons/nav_backward.gif"));
//			ToolItem itemForward = new ToolItem(toolbar, SWT.PUSH);
//			itemForward.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(
//					Activator.PLUGIN_ID, "icons/nav_forward.gif"));
//			ToolItem itemStop = new ToolItem(toolbar, SWT.PUSH);
//			itemStop.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(
//					Activator.PLUGIN_ID, "icons/nav_stop.gif"));
//			ToolItem itemRefresh = new ToolItem(toolbar, SWT.PUSH);
//			itemRefresh.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(
//					Activator.PLUGIN_ID, "icons/nav_refresh.gif"));
//		
//			
//			GridData data = new GridData();
//			toolbar.setLayoutData(data);
//			
//			final Text location = new Text(toolbarArmedBrowser, SWT.BORDER);
//			data = new GridData();
//			data.horizontalAlignment = GridData.FILL;
//			data.grabExcessHorizontalSpace = true;
//			location.setLayoutData(data);
//			
//			Button goButton = new Button(toolbarArmedBrowser, SWT.PUSH);
//			goButton.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(
//					Activator.PLUGIN_ID, "icons/nav_go.gif"));
//
//			browser = new Browser(toolbarArmedBrowser, SWT.NONE);
//			browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
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
