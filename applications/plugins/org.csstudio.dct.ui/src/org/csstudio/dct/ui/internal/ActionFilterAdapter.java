package org.csstudio.dct.ui.internal;

import java.util.UUID;

import org.csstudio.dct.model.IElement;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IActionFilter;

/**
 * Implementation of a {@link IActionFilter} adapter for {@link IElement}
 * objects.
 * 
 * @author Sven Wende
 * 
 */
public class ActionFilterAdapter implements IActionFilter {
	private static final String ATTR_ERROR = "error";

	public boolean testAttribute(Object target, String name, String value) {
		if (ATTR_ERROR.equals(name) && target instanceof IElement) {
			UUID id = ((IElement) target).getId();
			assert id != null;

			String errors = "false";
			try {
				IMarker markers[] = ResourcesPlugin.getWorkspace().getRoot().findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);

				for (IMarker marker : markers) {
					if (id.toString().equals(marker.getAttribute(IMarker.LOCATION))) {
						errors = "true";
					}
				}
			} catch (CoreException e1) {
				errors = "unknown";
			}

			return errors.equals(value);
		}

		return false;
	}

}
