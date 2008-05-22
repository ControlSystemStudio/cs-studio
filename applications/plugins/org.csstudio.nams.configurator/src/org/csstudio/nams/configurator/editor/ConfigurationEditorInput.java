package org.csstudio.nams.configurator.editor;

import java.util.Collection;

import org.csstudio.nams.configurator.treeviewer.model.AbstractConfigurationBean;
import org.csstudio.nams.configurator.treeviewer.model.ConfigurationBean;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class ConfigurationEditorInput implements IEditorInput {

	private final ConfigurationBean bean;
	private final Collection<String> sortgroupNames;

	public ConfigurationEditorInput(AbstractConfigurationBean newElement,
			Collection<String> sortgroupNames) {
		this.bean = newElement;
		this.sortgroupNames = sortgroupNames;
	}

	public ConfigurationBean getBean() {
		return bean;
	}

	public Collection<String> getSortgroupNames() {
		return sortgroupNames;
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

	/**
	 * Da nur ein ConfigurationEditor geöffnet werden woll, muss equals immer
	 * true liefern
	 */
	public boolean equals(Object obj) {
		return (obj instanceof ConfigurationEditorInput);
	}

}
