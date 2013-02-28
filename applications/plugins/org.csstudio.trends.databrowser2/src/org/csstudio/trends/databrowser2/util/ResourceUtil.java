package org.csstudio.trends.databrowser2.util;

import java.io.File;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

/**Utility functions for resources.
 * @author Xihui Chen, Abadie Lana, Kay Kasemir
 */
public class ResourceUtil {

	private static final ResourceUtilSSHelper IMPL;
	
	static {
		IMPL = (ResourceUtilSSHelper)ImplementationLoader.newInstance(
				ResourceUtilSSHelper.class);
	}
			
	/**Get screenshot image from GraphicalViewer
	 * @param viewer the GraphicalViewer
	 * @return the screenshot image
	 */
	public static Image getScreenshotImage(GraphicalViewer viewer){		
		return IMPL.getScreenShotImage(viewer);
	}
	
	@SuppressWarnings("nls")
    public static String getScreenshotFile(GraphicalViewer viewer) throws Exception{
		File file;
		 // Get name for snapshot file
        try
        {
            file = File.createTempFile("databrowser", ".png"); //$NON-NLS-1$ //$NON-NLS-2$
            file.deleteOnExit();
        }
        catch (Exception ex)
        {
            throw new Exception("Cannot create tmp. file:\n" + ex.getMessage());
        }

        // Create snapshot file
        try
        {
            final ImageLoader loader = new ImageLoader();

            final Image image = ResourceUtil.getScreenshotImage(viewer);
            loader.data = new ImageData[]{image.getImageData()};
            image.dispose();
            loader.save(file.getAbsolutePath(), SWT.IMAGE_PNG);
        }
        catch (Exception ex)
        {
            throw new Exception(
                    NLS.bind("Cannot create snapshot in {0}:\n{1}",
                            file.getAbsolutePath(), ex.getMessage()));
        }
        return file.getAbsolutePath();
    }
}
