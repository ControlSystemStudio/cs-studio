package org.csstudio.trends.databrowser2.util;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.graphics.Image;

/**
 * ResourceUtil Single Source helper. The IMPL should not be null.
 * 
 * @author Davy Dequidt <davy.dequidt@iter.org>
 */
public abstract class ResourceUtilSSHelper {
	public abstract Image getScreenShotImage(GraphicalViewer viewer);
}
