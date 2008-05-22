package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.csstudio.nams.configurator.treeviewer.model.ConfigurationBean;

public class FilterbedingungNode implements IConfigurationNode {

	private String name = "Filterbedingungen";
	private Map<String, SortGroupBean> groupBeans;

	public FilterbedingungNode(Map<String, SortGroupBean> map) {
		this.groupBeans = map;
	}

	public String getName() {
		return name;
	}

	public Collection<ConfigurationBean> getChildren() {
		Collection<ConfigurationBean> filterbedingungBeans = new ArrayList<ConfigurationBean>();

		/*
		 * Filter AlarmbearbeiterBeans heraus
		 */
		for (SortGroupBean bean : groupBeans.values()) {

			/*
			 * filter leere Gruppen heraus, diese werden im TreeViewer nicht
			 * angezeigt (Konvention laut DESY)
			 */
			if (!bean.getFilterbedingungBeans().isEmpty()) {
				filterbedingungBeans.addAll(bean.getFilterbedingungBeans());
			}
		}
		return filterbedingungBeans;
	}
}
