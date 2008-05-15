package org.csstudio.nams.configurator.editor;

import org.csstudio.ams.configurationStoreService.util.TObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class ConfigurationEditorInput implements IEditorInput {
	
	private TObject _input;

	public ConfigurationEditorInput(TObject input) {
		_input = input;
	}

	public TObject getInput() {
		return _input;
	}

	public boolean exists() {
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return "ConfigurationEditorInput";
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return "Configuration Editor" ;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof ConfigurationEditorInput); 
	}

}
