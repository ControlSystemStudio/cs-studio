package org.csstudio.nams.configurator.editor;

import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class ConfigurationEditorInput implements IEditorInput {

	private final IConfigurationBean bean;

	public ConfigurationEditorInput(IConfigurationBean newBean) {
		this.bean = newBean;
	}

	public IConfigurationBean getBean() {
		return bean;
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
		return "Configuration Editor";
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof ConfigurationEditorInput)) return false;
		
		ConfigurationEditorInput editorInput = (ConfigurationEditorInput) obj;
		
		if (editorInput.getBean().getID() == -1) return false;
		
		return editorInput.getBean().getID() == this.getBean().getID();
	}

}
