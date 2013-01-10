package org.csstudio.trends.databrowser2.util;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class SingleSourceHelper {

	private static final SingleSourceHelper IMPL;

	static {
		IMPL = (SingleSourceHelper) ImplementationLoader
				.newInstance(SingleSourceHelper.class);
	}

	public static GC getImageGC(final Image image) {
		if (IMPL == null)
			return null;
		return IMPL.iGetImageGC(image);
	}

	protected abstract GC iGetImageGC(final Image image);

	public static void addPaintListener(final Control control,
			PaintListener paintListener) {
		if (IMPL != null)
			IMPL.iAddPaintListener(control, paintListener);
	}

	protected abstract void iAddPaintListener(Control control,
			PaintListener paintListener);

	public static void removePaintListener(final Control control,
			PaintListener paintListener) {
		if (IMPL != null)
			IMPL.iRemovePaintListener(control, paintListener);
	}

	protected abstract void iRemovePaintListener(Control control,
			PaintListener paintListener);

	public static IPath rcpGetPathFromWorkspaceFileDialog(IPath startPath,
			String[] extensions) {
		if (IMPL != null)
			return IMPL.iRcpGetPathFromWorkspaceFileDialog(startPath,
					extensions);
		return null;
	}

	protected abstract IPath iRcpGetPathFromWorkspaceFileDialog(
			IPath startPath, String[] extensions);

	public static void rapOpenPltInNewWindow(IPath path) {
		if (IMPL != null)
			IMPL.iRapOpenPltInNewWindow(path);
	}

	protected abstract void iRapOpenPltInNewWindow(IPath path);

	public static void rapAddDisplayDisposeListener(Display display,
			Runnable runnable) {
		if (IMPL != null)
			IMPL.iRapAddDisplayDisposeListener(display, runnable);
	}

	protected abstract void iRapAddDisplayDisposeListener(Display display,
			Runnable runnable);

	/**
	 * Plugin Startup code for WebOPI.
	 */
	public static void rapPluginStartUp() {
		if (IMPL != null)
			IMPL.iRapPluginStartUp();
	}

	protected abstract void iRapPluginStartUp();

	public static void rapOpenWebPage(String hyperLink) {
		if (IMPL != null)
			IMPL.iRapOpenWebPage(hyperLink);
	}

	protected abstract void iRapOpenWebPage(String hyperLink);

	public static String openFileBrowser(Shell shell, int style) {
		if (IMPL != null)
			return IMPL.iOpenFileBrowser(shell, style);
		return null;
	}

	protected abstract String iOpenFileBrowser(Shell shell, int style);
}
