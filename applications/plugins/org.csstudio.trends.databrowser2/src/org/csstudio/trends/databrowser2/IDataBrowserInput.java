package org.csstudio.trends.databrowser2;

import java.io.InputStream;

import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;

public interface IDataBrowserInput extends IPathEditorInput, IPersistableElement {

	public InputStream getInputStream() throws Exception;
	
}
