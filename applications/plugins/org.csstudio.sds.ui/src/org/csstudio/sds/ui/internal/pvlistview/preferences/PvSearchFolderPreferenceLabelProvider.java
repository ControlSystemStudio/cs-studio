package org.csstudio.sds.ui.internal.pvlistview.preferences;

import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class PvSearchFolderPreferenceLabelProvider implements ILabelProvider, ICheckStateProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	@Override
	public Image getImage(Object element) {
		return null;
	}

	@Override
	public String getText(Object element) {
		PvSearchFolderPreferenceItem item = (PvSearchFolderPreferenceItem)element;
		return item.getFolderPath();
	}

	@Override
	public boolean isChecked(Object element) {
		PvSearchFolderPreferenceItem item = (PvSearchFolderPreferenceItem)element;
		return item.isChecked();
	}

	@Override
	public boolean isGrayed(Object element) {
		return false;
	}


}
