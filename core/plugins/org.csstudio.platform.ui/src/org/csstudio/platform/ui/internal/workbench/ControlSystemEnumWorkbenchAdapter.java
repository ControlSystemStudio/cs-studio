package org.csstudio.platform.ui.internal.workbench;

import org.csstudio.platform.model.rfc.ControlSystemEnum;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class ControlSystemEnumWorkbenchAdapter implements IWorkbenchAdapter{

	public Object[] getChildren(Object o) {
		return new Object[0];
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return null;
	}

	public String getLabel(Object o) {
		return ((ControlSystemEnum)o).toString();
	}

	public Object getParent(Object o) {
		return null;
	}

}
