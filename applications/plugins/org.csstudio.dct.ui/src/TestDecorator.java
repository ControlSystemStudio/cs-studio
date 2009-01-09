import java.util.UUID;

import org.csstudio.dct.model.IElement;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;


public class TestDecorator implements ILabelDecorator {

	public Image decorateImage(Image image, Object element) {
		return null;
	}

	public String decorateText(String text, Object element) {
		UUID id = ((IElement) element).getId();
		assert id != null;
		
		int count = 0;
		try {
			IMarker markers[] = ResourcesPlugin.getWorkspace().getRoot().findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
			
			for(IMarker marker : markers) {
				if(id.toString().equals(marker.getAttribute(IMarker.LOCATION))) {
					count++;
				}
			}
		} catch (CoreException e1) {
			count = 0;
		}

		return text + "["+count+" errors]";
	}

	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

}
