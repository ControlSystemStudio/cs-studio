package org.csstudio.dct.ui.internal;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.internal.Folder;

/**
 * UI adapter for {@link Folder}.
 * 
 * @author Sven Wende
 */
public class FolderWorkbenchAdapter extends BaseWorkbenchAdapter<IFolder> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object[] doGetChildren(IFolder folder) {
		List<Object> result = new ArrayList<Object>();
		result.addAll(folder.getMembers());
		return result.toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String doGetLabel(IFolder folder) {
		return folder.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String doGetIcon(IFolder folder) {
		return "icons/folder.png";
	}

	

}
