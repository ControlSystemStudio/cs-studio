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

	/*
	 * Die Collection configurationNodes enthält alle Elemente des
	 * Configuration-TreeViewer
	 */
	private Collection<IConfigurationNode> configurationNodes;

	private Collection<SortGroupBean> sortgroupBeans;

	public ConfigurationModel(Collection<SortGroupBean> sortgroupBeans) {
		this.sortgroupBeans = sortgroupBeans;

		// XXX Test only
		this.sortgroupBeans = getTestconfigurationNodes();

		this.initBeans(this.sortgroupBeans);
	}

	/**
	 * Initialisiert die GroupBeans-Map, indem als key der Gruppenname verwendet
	 * wird und als value die Gruppe an sich
	 * 
	 * @param groupBeans
	 */
	private void initBeans(Collection<SortGroupBean> groupBeans) {

		this.configurationNodes = new ArrayList<IConfigurationNode>();

		// root node alarmbearbeiter
		Collection<SortgroupNode> alarmBearbeitergroupNodes = new ArrayList<SortgroupNode>();
		// root node alarmbearbeitergruppen
		Collection<SortgroupNode> alarmBearbeiterGruppengroupNodes = new ArrayList<SortgroupNode>();
		// root node alarmtopics
		Collection<SortgroupNode> alarmTopicgroupNodes = new ArrayList<SortgroupNode>();
		// root node filterbedingungen
		Collection<SortgroupNode> filterBedingunggroupNodes = new ArrayList<SortgroupNode>();
		// root node filter
		Collection<SortgroupNode> filtergroupNodes = new ArrayList<SortgroupNode>();
		// root node empty groups
		Collection<SortgroupNode> emptygroupNodes = new ArrayList<SortgroupNode>();

		for (SortGroupBean bean : groupBeans) {

			// ALARMBEATERBEITER
			if (!bean.getAlarmbearbeiterBeans().isEmpty()) {
				SortgroupNode groupNodeAlarmbearbeiter = new SortgroupNode(bean
						.getDisplayName(), bean.getAlarmbearbeiterBeans(),
						ConfigurationType.ALARMBEATERBEITER);
				alarmBearbeitergroupNodes.add(groupNodeAlarmbearbeiter);
			}

			// else {
			// SortgroupNode emptyNode = new SortgroupNode(bean
			// .getDisplayName(), null,
			// ConfigurationType.ALARMBEATERBEITER);
			// emptygroupNodes.add(emptyNode);
			// }

			// ALARMBEATERBEITERGRUPPE
			if (!bean.getAlarmbearbeitergruppenBeans().isEmpty()) {
				SortgroupNode groupNodeAlarmbearbeitergruppe = new SortgroupNode(
						bean.getDisplayName(), bean
								.getAlarmbearbeitergruppenBeans(),
						ConfigurationType.ALARMBEATERBEITERGRUPPE);

				alarmBearbeiterGruppengroupNodes
						.add(groupNodeAlarmbearbeitergruppe);
			}

			// else {
			// SortgroupNode emptyNode = new SortgroupNode(bean
			// .getDisplayName(), null,
			// ConfigurationType.ALARMBEATERBEITERGRUPPE);
			// emptygroupNodes.add(emptyNode);
			// }

			// ALARMTOPIC
			if (!bean.getAlarmtopicBeans().isEmpty()) {
				SortgroupNode groupNodeAlarmtopic = new SortgroupNode(bean
						.getDisplayName(), bean.getAlarmtopicBeans(),
						ConfigurationType.ALARMTOPIC);
				alarmTopicgroupNodes.add(groupNodeAlarmtopic);
			}

			// else {
			// SortgroupNode emptyNode = new SortgroupNode(bean
			// .getDisplayName(), null, ConfigurationType.ALARMTOPIC);
			// emptygroupNodes.add(emptyNode);
			// }

			// FILTERBEDINGUNG
			if (!bean.getFilterbedingungBeans().isEmpty()) {
				SortgroupNode groupNodeFilterbedingung = new SortgroupNode(bean
						.getDisplayName(), bean.getFilterbedingungBeans(),
						ConfigurationType.FILTERBEDINGUNG);
				filterBedingunggroupNodes.add(groupNodeFilterbedingung);
			}

			// else {
			// SortgroupNode emptyNode = new SortgroupNode(bean
			// .getDisplayName(), null,
			// ConfigurationType.FILTERBEDINGUNG);
			// emptygroupNodes.add(emptyNode);
			// }

			// FILTER
			if (!bean.getFilterBeans().isEmpty()) {
				SortgroupNode groupNodeFilter = new SortgroupNode(bean
						.getDisplayName(), bean.getFilterBeans(),
						ConfigurationType.FILTER);
				filtergroupNodes.add(groupNodeFilter);
			}
			// else {
			// SortgroupNode emptyNode = new SortgroupNode(bean
			// .getDisplayName(), null, ConfigurationType.FILTER);
			// emptygroupNodes.add(emptyNode);
			// }

			this.checkEmptyGroup(bean, emptygroupNodes);
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
		this.configurationNodes.add(new ConfigurationNode(emptygroupNodes,
				ConfigurationType.EMPTYGROUPS));
	}

	/*
	 * Prüfe, ob eine Gruppe keine Kinder enthält und füge sie in dem Fall in
	 * den RootNode EmptyGroups. Dadurch werden leere Gruppen im TreeViewer
	 * sichtbar gemacht und können vom User bei Bedarf gelöscht werden.
	 * 
	 * @param groupBean @param emptygroupNodes
	 */
	private void checkEmptyGroup(SortGroupBean groupBean,
			Collection<SortgroupNode> emptygroupNodes) {

		if (groupBean.getAlarmbearbeiterBeans().isEmpty()
				&& groupBean.getAlarmbearbeitergruppenBeans().isEmpty()
				&& groupBean.getAlarmtopicBeans().isEmpty()
				&& groupBean.getFilterbedingungBeans().isEmpty()
				&& groupBean.getFilterBeans().isEmpty()) {
			SortgroupNode emptyNode = new SortgroupNode(groupBean
					.getDisplayName(), null, ConfigurationType.EMPTYGROUPS);

			emptygroupNodes.add(emptyNode);
		}
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
		SortGroupBean emptyGroup = new SortGroupBean(0, "Strasse 2");

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
		initialData.add(emptyGroup);

		return initialData;
	}
}
