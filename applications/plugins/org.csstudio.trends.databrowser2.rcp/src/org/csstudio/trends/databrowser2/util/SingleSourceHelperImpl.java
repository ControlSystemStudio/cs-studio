package org.csstudio.trends.databrowser2.util;

import org.csstudio.ui.util.dialogs.ResourceSelectionDialog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class SingleSourceHelperImpl extends SingleSourceHelper {

	@Override
	protected GC iGetImageGC(Image image) {
		return new GC(image);
	}

	@Override
	protected void iAddPaintListener(Control control,
			PaintListener paintListener) {
		control.addPaintListener(paintListener);

	}

	@Override
	protected void iRemovePaintListener(Control control,
			PaintListener paintListener) {
		control.removePaintListener(paintListener);

	}

	@Override
	protected IPath iRcpGetPathFromWorkspaceFileDialog(IPath startPath,
			String[] extensions) {
		ResourceSelectionDialog rsDialog = new ResourceSelectionDialog(Display
				.getCurrent().getActiveShell(), "Choose File", extensions);
		if (startPath != null)
			rsDialog.setSelectedResource(startPath);

		if (rsDialog.open() == Window.OK) {
			return rsDialog.getSelectedResource();
		}
		return null;
	}

	// ////////////////////////// RAP Related Stuff
	// ///////////////////////////////

	@Override
	protected void iRapAddDisplayDisposeListener(Display display,
			Runnable runnable) {

	}

	@Override
	protected void iRapPluginStartUp() {

	}

	@Override
	protected void iRapOpenWebPage(String hyperLink) {

	}

	@Override
	protected void iRapOpenPltInNewWindow(IPath path) {

	}

	protected String iOpenFileBrowser(final Shell shell, final int style)
	{
	    // On OS X, the JRE crashed when invoking this
	    // a second time after the first FileDialog was "cancel"ed by the user?!
	    // Fix seemed to be:
	    // Assert that the same JRE system library is used for the main DataBrowser
	    // plugin and this fragment. Best done by always using the 'workspace'
	    // JRE and not picking any specific one which then needs to be set to the
	    // same in both the main plugin and the fragment.
	    final FileDialog dlg = new FileDialog(shell, style);
	    final String result = dlg.open();
		return result;
	}
}
