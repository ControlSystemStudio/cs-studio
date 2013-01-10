package org.csstudio.trends.databrowser2;

import java.io.InputStream;

import org.csstudio.trends.databrowser2.util.ResourceUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/**
 * The editor input for Data Browser.
 * 
 * @author Davy Dequidt
 * 
 */
public class DataBrowserInput implements IDataBrowserInput {
	private IPath path;

	public DataBrowserInput(IPath path) {
		this.path = path;
	}

	public IPath getPath() {
		return path;
	}

	public boolean exists() {
		InputStream in = null;
		try {
			in = getInputStream();
		} catch (Exception e) {
			return false;
		}
		return in != null;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return getPath().lastSegment();
	}

	public IPersistableElement getPersistable() {
		return this;
	}

	public String getToolTipText() {
		return path.toString();
	}

	public InputStream getInputStream() throws Exception{
		return ResourceUtil.pathToInputStream(getPath(), false);
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
	{
	    // For 'Save' to work after 'SaveAs', the
	    // input must adapt to IFile.
	    // For details see DataBrowserEditor#doSave()
	    if (adapter == IFile.class)
	    {
	        final IResource resource = ResourcesPlugin.getWorkspace().getRoot()
                    .findMember(path, false);
            if (resource != null  &&  resource instanceof IFile)
                return resource;
	    }
		return null;
	}

	@Override
	public String toString() {
		return getPath().toString();
	}

	public void saveState(IMemento memento) {
		DataBrowserInputFactory.saveState(memento, this);
	}

	public String getFactoryId() {
		return DataBrowserInputFactory.getFactoryId();
	}

}
