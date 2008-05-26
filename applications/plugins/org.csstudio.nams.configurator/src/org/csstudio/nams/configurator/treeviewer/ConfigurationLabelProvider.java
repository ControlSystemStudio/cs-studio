package org.csstudio.nams.configurator.treeviewer;

import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationBean;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationGroup;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationRoot;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;

public class ConfigurationLabelProvider extends LabelProvider implements
		IBaseLabelProvider {

	@Override
	public String getText(Object element) {
		// Root Node
		if (element instanceof IConfigurationRoot) {
			IConfigurationRoot node = (IConfigurationRoot) element;
			return node.getDisplayName();
		}

		// Group Node
		if (element instanceof IConfigurationGroup) {
			IConfigurationGroup groupNode = (IConfigurationGroup) element;
			return groupNode.getDisplayName();
		}

		// Beans
		if (element instanceof IConfigurationBean) {
			IConfigurationBean bean = (IConfigurationBean) element;
			return bean.getDisplayName();
		}
		return super.getText(element);
	}

}
