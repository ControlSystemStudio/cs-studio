package org.csstudio.dct.ui.internal;

import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.internal.Project;

/**
 * UI adapter for {@link Project}.
 * 
 * @author Sven Wende
 */
public class ProjectWorkbenchAdapter extends FolderWorkbenchAdapter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String doGetIcon(IFolder folder) {
		return "icons/project.png";
	}

}
