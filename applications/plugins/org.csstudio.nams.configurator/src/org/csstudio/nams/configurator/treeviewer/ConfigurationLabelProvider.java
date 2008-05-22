package org.csstudio.nams.configurator.treeviewer;

import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationNode;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;

public class ConfigurationLabelProvider extends LabelProvider implements
		IBaseLabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof IConfigurationNode) {
			IConfigurationNode node = (IConfigurationNode) element;
			return node.getName();
		}
		return super.getText(element);
	}

}
