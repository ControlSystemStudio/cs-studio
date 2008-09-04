package org.csstudio.nams.configurator.editor;

import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class ConfigurationEditorInput implements IEditorInput {

	private IConfigurationBean bean;

	public ConfigurationEditorInput(final IConfigurationBean newBean) {
		this.bean = newBean;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ConfigurationEditorInput)) {
			return false;
		}

		final ConfigurationEditorInput editorInput = (ConfigurationEditorInput) obj;

		return editorInput.getBean() == this.getBean();
	}

	public boolean exists() {
		return false;
	}

	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public Object getAdapter(final Class adapter) {
		return null;
	}

	public IConfigurationBean getBean() {
		return this.bean;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return "ConfigurationEditorInput"; //$NON-NLS-1$
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return "Configuration Editor"; //$NON-NLS-1$
	}

	public void setBean(final IConfigurationBean bean) {
		this.bean = bean;
	}

}
