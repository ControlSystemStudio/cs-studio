package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.csstudio.nams.configurator.treeviewer.model.ConfigurationBean;

public class SortgroupNode {

	private final GroupType groupType;
	private final Map<String, SortGroupBean> groupBeans;

	public static enum GroupType {
		ALARMBEATERBEITER, ALARMBEATERBEITERGRUPPE, ALARMTOPIC, FILTERBEDINGUNG, FILTER
	};

	public SortgroupNode(Map<String, SortGroupBean> groupBeans,
			GroupType groupType) {
		this.groupBeans = groupBeans;
		this.groupType = groupType;
	}

	public GroupType getGroupType() {
		return groupType; 
	}

	public Collection<ConfigurationBean> getChildren() {
		Collection<ConfigurationBean> filterBeans = new ArrayList<ConfigurationBean>();

		/*
		 * Filter AlarmbearbeiterBeans heraus
		 */
		for (SortGroupBean bean : groupBeans.values()) {

			/*
			 * filter leere Gruppen heraus, diese werden im TreeViewer nicht
			 * angezeigt (Konvention laut DESY)
			 */
			if (!bean.getFilterBeans().isEmpty()) {
				filterBeans.addAll(bean.getFilterBeans());
			}
		}
		return filterBeans;
	}
}
