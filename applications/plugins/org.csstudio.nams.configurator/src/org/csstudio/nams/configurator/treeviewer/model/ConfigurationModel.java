package org.csstudio.nams.configurator.treeviewer.model;

import java.util.ArrayList;
import java.util.Collection;

import org.csstudio.nams.configurator.treeviewer.model.treecomponents.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.AlarmtopicBean;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.ConfigurationNode;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.ConfigurationType;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationNode;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.SortGroupBean;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.SortgroupNode;

/**
 * Diese Klasse ist das Model für den TreeViewer des AMS-Configurators.
 * 
 * @author eugrei
 * 
 */
public class ConfigurationModel {

	private Collection<IConfigurationNode> configurationNodes;
	private final Collection<SortGroupBean> sortgroupBeans;

	public ConfigurationModel(Collection<SortGroupBean> sortgroupBeans) {
		this.sortgroupBeans = sortgroupBeans;

		this.initBeans(getTestconfigurationNodes());
	}

	/**
	 * Initialisiert die GroupBeans-Map, indem als key der Gruppenname verwendet
	 * wird und als value die Gruppe an sich
	 * 
	 * @param groupBeans
	 */
	private void initBeans(Collection<SortGroupBean> groupBeans) {

		this.configurationNodes = new ArrayList<IConfigurationNode>();

		Collection<SortgroupNode> alarmBearbeitergroupNodes = new ArrayList<SortgroupNode>();
		Collection<SortgroupNode> alarmBearbeiterGruppengroupNodes = new ArrayList<SortgroupNode>();
		Collection<SortgroupNode> alarmTopicgroupNodes = new ArrayList<SortgroupNode>();
		Collection<SortgroupNode> filterBedingunggroupNodes = new ArrayList<SortgroupNode>();
		Collection<SortgroupNode> filtergroupNodes = new ArrayList<SortgroupNode>();

		for (SortGroupBean bean : groupBeans) {
			if (!bean.getAlarmbearbeiterBeans().isEmpty())
				alarmBearbeitergroupNodes.add(new SortgroupNode(bean
						.getDisplayName(), bean.getAlarmbearbeiterBeans(),
						ConfigurationType.ALARMBEATERBEITER));

			if (!bean.getAlarmbearbeitergruppenBeans().isEmpty())
				alarmBearbeiterGruppengroupNodes.add(new SortgroupNode(bean
						.getDisplayName(), bean
						.getAlarmbearbeitergruppenBeans(),
						ConfigurationType.ALARMBEATERBEITERGRUPPE));

			if (!bean.getAlarmtopicBeans().isEmpty())
				alarmTopicgroupNodes.add(new SortgroupNode(bean
						.getDisplayName(), bean.getAlarmtopicBeans(),
						ConfigurationType.ALARMTOPIC));

			if (!bean.getFilterbedingungBeans().isEmpty())
				filterBedingunggroupNodes.add(new SortgroupNode(bean
						.getDisplayName(), bean.getFilterbedingungBeans(),
						ConfigurationType.FILTERBEDINGUNG));

			if (!bean.getFilterBeans().isEmpty())
				filtergroupNodes.add(new SortgroupNode(bean.getDisplayName(),
						bean.getFilterBeans(), ConfigurationType.FILTER));
		}

		this.configurationNodes
				.add(new ConfigurationNode(alarmBearbeitergroupNodes,
						ConfigurationType.ALARMBEATERBEITER));
		this.configurationNodes.add(new ConfigurationNode(
				alarmBearbeiterGruppengroupNodes,
				ConfigurationType.ALARMBEATERBEITERGRUPPE));
		this.configurationNodes.add(new ConfigurationNode(alarmTopicgroupNodes,
				ConfigurationType.ALARMTOPIC));
		this.configurationNodes.add(new ConfigurationNode(
				filterBedingunggroupNodes, ConfigurationType.FILTERBEDINGUNG));
		this.configurationNodes.add(new ConfigurationNode(filtergroupNodes,
				ConfigurationType.FILTER));
	}

	/**
	 * Liefert alle Kinder des Models.
	 * 
	 * @return
	 */
	public Collection<IConfigurationNode> getChildren() {
		return configurationNodes;
	}

	public Collection<String> getSortgroupNames() {
		Collection<String> groupNames = new ArrayList<String>();

		for (SortGroupBean groupBean : sortgroupBeans) {
			groupNames.add(groupBean.getDisplayName());
		}
		return groupNames;
	}

	public void save(ConfigurationBean bean, String groupName) {
		// not implmented yet
	}

	private Collection<SortGroupBean> getTestconfigurationNodes() {

		Collection<SortGroupBean> initialData = new ArrayList<SortGroupBean>();

		SortGroupBean sortgroupBean = new SortGroupBean(0, "Strasse 1");

		Collection<AlarmbearbeiterBean> beans1 = new ArrayList<AlarmbearbeiterBean>();

		// Beans erzeugen
		AlarmbearbeiterBean testAlarmbearbeiterBean1 = new AlarmbearbeiterBean();
		testAlarmbearbeiterBean1.setName("Klaus");

		AlarmbearbeiterBean testAlarmbearbeiterBean2 = new AlarmbearbeiterBean();
		testAlarmbearbeiterBean2.setName("Susi");

		beans1.add(testAlarmbearbeiterBean1);
		beans1.add(testAlarmbearbeiterBean2);

		sortgroupBean.setAlarmbearbeiterBeans(beans1);

		Collection<AlarmtopicBean> beans2 = new ArrayList<AlarmtopicBean>();
		AlarmtopicBean topicBean1 = new AlarmtopicBean();
		topicBean1.setHumanReadableName("Topic 1");
		beans2.add(topicBean1);

		sortgroupBean.setAlarmtopicBeans(beans2);

		initialData.add(sortgroupBean);

		return initialData;
	}
}
