package org.csstudio.sns.mpsbypasses.ui;

import java.net.URL;

import org.csstudio.sns.mpsbypasses.Plugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/** Action to open a web page in the Workbench's viewer
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class WebPageAction extends Action
{
	final private String url;

	/** Initialize
	 *  @param label Label for action
	 *  @param url Web URL to open when invoked
	 */
	public WebPageAction(final String label, final String url)
    {
		super(label, Plugin.getImageDescription("icons/weblink.png"));
		this.url = url;
    }

	@Override
	public void run()
	{
        try
        {
            final IWorkbench workbench = PlatformUI.getWorkbench();
            final IWebBrowser browser =
                workbench.getBrowserSupport().createBrowser(
                		IWorkbenchBrowserSupport.NAVIGATION_BAR | IWorkbenchBrowserSupport.LOCATION_BAR,
                		"MPSBypass", null, null);
            browser.openURL(new URL(url));
        }
        catch (Throwable ex)
        {
            MessageDialog.openError(null, "Web Browser Error",
                NLS.bind("Cannot open web page {0}\nException: {1}", url, ex.getMessage()));
        }
	}
}
