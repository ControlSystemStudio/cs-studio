package org.csstudio.trends.databrowser2.util;

import java.io.InputStream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.graphics.Image;

/**
 * ResourceUtil Single Source helper. The IMPL should not be null.
 * 
 * @author Davy Dequidt <davy.dequidt@iter.org>
 */
public abstract class ResourceUtilSSHelper {

	/**
	 * Return the {@link InputStream} of the file that is available on the
	 * specified path.
	 * 
	 * @param path
	 *            The {@link IPath} to the file in the workspace, the local file
	 *            system, or a URL (http:, https:, ftp:, file:, platform:)
	 * @param runInUIJob
	 *            true if the task should run in UIJob, which will block UI
	 *            responsiveness with a progress bar on status line. Caller must
	 *            be in UI thread if this is true.
	 * @return The corresponding {@link InputStream}. Never <code>null</code>
	 * @throws Exception
	 */
	public abstract InputStream pathToInputStream(final IPath path,
			boolean runInUIJob) throws Exception;

	/**
	 * @param path
	 *            the file path
	 * @return true if the file path is an existing workspace file.
	 */
	public abstract boolean isExistingWorkspaceFile(IPath path);

	public abstract Image getScreenShotImage(GraphicalViewer viewer);

}
