package org.csstudio.nams.configurator.treeviewer;

import org.csstudio.nams.configurator.treeviewer.model.ConfigurationBean;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationNode;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.SortgroupNode;
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

		if (element instanceof SortgroupNode) {
			SortgroupNode groupNode = (SortgroupNode) element;
			return groupNode.getDisplayName();
		}

		if (element instanceof ConfigurationBean) {
			ConfigurationBean bean = (ConfigurationBean) element;
			return bean.getDisplayName();
		}
		return super.getText(element);
	}

}
