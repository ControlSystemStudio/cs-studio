package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.csstudio.nams.configurator.treeviewer.model.ObservableBean;

public class AlarmtopicNode implements IConfigurationNode {

	private String name = "Alarmtopics";
	private Map<String, SortGroupBean> groupBeans;

	public AlarmtopicNode(Map<String, SortGroupBean> map) {
		this.groupBeans = map;
	}

	public String getName() {
		return name;
	}

	public Collection<ObservableBean> getChildren() {
		Collection<ObservableBean> alarmtopicBeans = new ArrayList<ObservableBean>();

		for (SortGroupBean bean : groupBeans.values()) {
			if (!bean.getAlarmtopicBeans().isEmpty()) {
				alarmtopicBeans.addAll(bean.getAlarmtopicBeans());
			}
		}

		return alarmtopicBeans;
	}

}
