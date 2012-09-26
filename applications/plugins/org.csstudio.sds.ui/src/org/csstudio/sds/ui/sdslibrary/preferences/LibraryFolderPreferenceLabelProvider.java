package org.csstudio.sds.ui.sdslibrary.preferences;

import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class LibraryFolderPreferenceLabelProvider implements ILabelProvider, ICheckStateProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public Image getImage(Object element) {
		return null;
	}

	@Override
	public String getText(Object element) {
		LibraryFolderPreferenceItem item = (LibraryFolderPreferenceItem)element;
		return item.getFolderPath();
	}

	@Override
	public boolean isChecked(Object element) {
		LibraryFolderPreferenceItem item = (LibraryFolderPreferenceItem)element;
		return item.isChecked();
	}

	@Override
	public boolean isGrayed(Object element) {
		return false;
	}


}
