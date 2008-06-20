package org.csstudio.nams.configurator.treeviewer.model;

import java.util.ArrayList;
import java.util.Collection;

import org.csstudio.nams.configurator.treeviewer.model.AlarmbearbeiterBean.PreferedAlarmType;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.ConfigurationNode;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.ConfigurationType;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationBean;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationGroup;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationRoot;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.SortgroupNode;
import org.eclipse.core.runtime.Assert;

/**
 * Diese Klasse ist das Model für den TreeViewer des AMS-Configurators.
 * 
 * Beim DESY besteht die Anforderung, dass Gruppen global verwendet werden
 * sollen, d.h. auf eine Gruppe - angelegt z.B. im Alarmbearbeiter-Editor -
 * können auch weitere Beans wie z.B. der Alarmtopic zugreifen. Diese
 * Anforderung ist mit dem JFace TreeViewer nicht umsetzbar, daher gehen wir wie
 * folgt vor:
 * 
 * 1. Wir definieren ein Modell, dass die DESY-Anforderungen erfüllt (Collection<SortGroupBean>
 * groupBeans)
 * 
 * 2. Basierend auf diesem Modell erstellen wir ein JFace TreeViewer geeignetes
 * Modell in der Methode initBeans();
 * 
 * 3. Beim Speichern von Änderungen wird zunächst das ursprüngliche DESY-Modell
 * angepasst. Anschließend erfolgt wieder ein Aufbau des Modells für den JFace
 * TreeViewer.
 * 
 * Das Arbeiten mit zwei Modellen ist jedoch sehr fehleranfällig und führt zu
 * vielen Problemen...
 * 
 * @author Eugen Reiswich
 * 
 */
public class ConfigurationModel extends AbstractObservableBean implements
		IConfigurationModel {

	/*
	 * Die Collection rootNodes enthält alle Elemente des
	 * Configuration-TreeViewer
	 */
	private Collection<IConfigurationRoot> rootNodes;

	/*
	 * Die Collection configurationGroups enthällt globale Gruppen, die von
	 * allen ConfigurationsTypen im TreeViewer verwendet werden können. Diese
	 * Collecion wird aus der Datenbank erstellt und enthällt initialdaten für
	 * den TreeViewer.
	 */
	private Collection<SortGroupBean> groupBeans;

	// private Collection<SortGroupBean> sortgroupBeans;

	public ConfigurationModel(Collection<SortGroupBean> configurationGroups) {
		this.groupBeans = configurationGroups;

		// XXX Test only
		this.groupBeans = getTestconfigurationNodes();

		this.initBeans();
	}

	/*
	 * In dieser Methode erfolgt die Erstellung des Modells für den TreeViewer.
	 * Hier werden Gruppen zu RootNodes zusamengestellt.
	 */
	private void initBeans() {
		this.rootNodes = new ArrayList<IConfigurationRoot>();

		IConfigurationRoot alarmbearbeiterRootNode = new ConfigurationNode(
				ConfigurationType.ALARMBEATERBEITER);
		IConfigurationRoot alarmbearbeiterGruppenRootNode = new ConfigurationNode(
				ConfigurationType.ALARMBEATERBEITERGRUPPE);
		IConfigurationRoot alarmtopicRootNode = new ConfigurationNode(
				ConfigurationType.ALARMTOPIC);
		IConfigurationRoot filterRootNode = new ConfigurationNode(
				ConfigurationType.FILTER);
		IConfigurationRoot filterbedingungRootNode = new ConfigurationNode(
				ConfigurationType.FILTERBEDINGUNG);
		IConfigurationRoot emptyRootNode = new ConfigurationNode(
				ConfigurationType.EMPTYGROUPS);

		this.rootNodes.add(alarmbearbeiterRootNode);
		this.rootNodes.add(alarmbearbeiterGruppenRootNode);
		this.rootNodes.add(alarmtopicRootNode);
		this.rootNodes.add(filterRootNode);
		this.rootNodes.add(filterbedingungRootNode);
		this.rootNodes.add(emptyRootNode);

		for (SortGroupBean groupBean : this.groupBeans) {

			// ALARMBEATERBEITER
			if (!groupBean.getAlarmbearbeiterBeans().isEmpty()) {
				SortgroupNode groupNodeAlarmbearbeiter = new SortgroupNode(
						groupBean.getDisplayName(), alarmbearbeiterRootNode);
				// set children
				groupNodeAlarmbearbeiter.setChildren(groupBean
						.getAlarmbearbeiterBeans());
				alarmbearbeiterRootNode.addChild(groupNodeAlarmbearbeiter);
			}

			// ALARMBEATERBEITERGRUPPE
			if (!groupBean.getAlarmbearbeitergruppenBeans().isEmpty()) {
				SortgroupNode groupNodeAlarmbearbeitergruppen = new SortgroupNode(
						groupBean.getDisplayName(),
						alarmbearbeiterGruppenRootNode);
				// set children
				groupNodeAlarmbearbeitergruppen.setChildren(groupBean
						.getAlarmbearbeitergruppenBeans());
				alarmbearbeiterGruppenRootNode
						.addChild(groupNodeAlarmbearbeitergruppen);
			}

			// ALARMTOPIC
			if (!groupBean.getAlarmtopicBeans().isEmpty()) {
				SortgroupNode groupNodeAlarmtopic = new SortgroupNode(groupBean
						.getDisplayName(), alarmtopicRootNode);
				groupNodeAlarmtopic.setChildren(groupBean.getAlarmtopicBeans());
				alarmtopicRootNode.addChild(groupNodeAlarmtopic);
			}

			// FILTER
			if (!groupBean.getFilterBeans().isEmpty()) {
				SortgroupNode groupNodeFilter = new SortgroupNode(groupBean
						.getDisplayName(), filterRootNode);
				groupNodeFilter.setChildren(groupBean.getFilterBeans());
				filterRootNode.addChild(groupNodeFilter);
			}

			// FILTERBEDINGUNG
			if (!groupBean.getFilterbedingungBeans().isEmpty()) {
				SortgroupNode groupNodeFilterbedingung = new SortgroupNode(
						groupBean.getDisplayName(), filterbedingungRootNode);
				groupNodeFilterbedingung.setChildren(groupBean
						.getFilterbedingungBeans());
				filterbedingungRootNode.addChild(groupNodeFilterbedingung);
			}

			// EMPTY GROUPS
			if (groupBean.isEmpty()) {
				SortgroupNode groupNodeEmpty = new SortgroupNode(groupBean
						.getDisplayName(), emptyRootNode);
				emptyRootNode.addChild(groupNodeEmpty);
			}
		}
	}

	/**
	 * Liefert alle Kinder des Models.
	 * 
	 * @return
	 */
	public Collection<IConfigurationRoot> getChildren() {
		return this.rootNodes;
	}

	/**
	 * Liefert alle Gruppennamen
	 */
	public Collection<String> getSortgroupNames() {
		Collection<String> groupNames = new ArrayList<String>();

		for (SortGroupBean groupBean : this.groupBeans) {
			groupNames.add(groupBean.getDisplayName());
		}
		return groupNames;
	}

	/**
	 * Speichert eine Bean im lokalen Modell. Die Speicherung in der DB wir per
	 * Synchronisation durchgeführt (Synchronize-Button in der Toolbar)
	 * 
	 * @param bean
	 *            Die Bean, die gespeichert werden soll
	 * @param groupName
	 *            Die Gruppe der Bean (kann auch eine neue Gruppe sein)
	 * @return {@link IConfigurationBean} Die übergebene Bean, jedoch mit
	 *         geändertem inneren Zustand. So hat die Bean evtl. andere
	 *         Gruppenzugehörigkeit bekommen.
	 */
	public IConfigurationBean save(IConfigurationBean bean, String groupName) {
		// wurde eine neue Gruppe angelegt ?
		SortGroupBean group = this.getGroupBeanByName(groupName);

		if (group == null) {
			this.createNewGroup(bean, groupName);
		} else {
			/*
			 * prüfe, ob Gruppenzugehörigkeit der Bean geändert wurde und
			 * verschiebe die Bean ggf. in die neue Gruppe
			 */
			if (bean.getParent() != null
					&& !bean.getParent().getDisplayName().equalsIgnoreCase(
							groupName)) {
				/*
				 * ändere Gruppenzugehörigkeit
				 */
				this.changeGroup(bean, groupName);
			} else {
				/*
				 * Gruppe wurde nicht geändert, einfach nur Bean updaten
				 */
				this.updateBeanState(bean, groupName);

			}

		}

		Collection<IConfigurationRoot> oldValue = this.rootNodes;

		// aktualisiere Model
		this.initBeans();

		/*
		 * Benachrichtige Listener, das dürfte eigentlich nur der TreeViewer
		 * sein.
		 */
		getPropertyChangeSupport().firePropertyChange("model", oldValue,
				this.rootNodes);

		IConfigurationBean updatedConfigurationItem = this
				.getUpdatedConfigurationItem(bean);

		return updatedConfigurationItem;
	}

	/*
	 * TODO: das Casten könnte hier zu Fehlern führen, überlege alternative
	 * Lösung
	 */
	private void updateBeanState(IConfigurationBean bean, String groupName) {
		boolean beanFound = false;
		/*
		 * Finde zuerst die Bean.
		 */
		for (SortGroupBean groupBean : this.groupBeans) {
			IConfigurationBean foundBean = groupBean.findBean(bean);
			if (foundBean != null) {
				beanFound = true;
				/*
				 * Bestimmte den Bean-Typ
				 */
				ConfigurationType beanType = bean.getParent()
						.getConfigurationType();
				switch (beanType) {
				case ALARMBEATERBEITER:
					((AlarmbearbeiterBean) foundBean)
							.updateState((AlarmbearbeiterBean) bean);
					break;
				case ALARMBEATERBEITERGRUPPE:
					((AlarmbearbeiterGruppenBean) foundBean)
							.updateState((AlarmbearbeiterGruppenBean) bean);
					break;
				case ALARMTOPIC:
					((AlarmtopicBean) foundBean)
							.updateState((AlarmtopicBean) bean);
					break;
				case FILTER:
					((FilterBean) foundBean).updateState((FilterBean) bean);
					break;
				case FILTERBEDINGUNG:
					((FilterbedingungBean) foundBean)
							.updateState((FilterbedingungBean) bean);
					break;
				}
			}
		}

		if (!beanFound) {
			// neue Bean wurde angelegt
			SortGroupBean group = getGroupBeanByName(groupName);
			Assert.isNotNull(group);
			group.addConfigurationItem(bean);
		}
	}

	/*
	 * Ändert die Gruppe einer Bean. Vorbedingung: Gruppe für übergebenen
	 * Gruppennamen muss existieren
	 */
	private void changeGroup(IConfigurationBean bean, String groupName) {
		/*
		 * Lösche zunächst Bean aus alter Gruppe
		 */
		for (SortGroupBean groupBean : this.groupBeans) {
			groupBean.removeBean(bean);
		}

		// füge Bean in die übergebene Gruppe
		SortGroupBean groupBean = this.getGroupBeanByName(groupName);
		groupBean.addConfigurationItem(bean);
	}

	private SortGroupBean getGroupBeanByName(String groupName) {
		for (SortGroupBean bean : this.groupBeans) {
			if (bean.getDisplayName().equalsIgnoreCase(groupName)) {
				return bean;
			}
		}
		return null;
	}

	/*
	 * Da nach dem Speichern die ursprüngliche Bean eine andere
	 * Gruppenzugehörigkeit hat, wird hier nach dieser Bean gesucht, um einen
	 * übergreifenden konsistenten Zustand zu bewahren.
	 */
	private IConfigurationBean getUpdatedConfigurationItem(
			IConfigurationBean bean) {
		IConfigurationGroup parent = bean.getParent();
		Assert.isNotNull(parent);
		ConfigurationType beanType = parent.getConfigurationType();

		for (IConfigurationRoot root : this.rootNodes) {
			if (root.getConfigurationType() == beanType) {
				/*
				 * richtigen Root-Node gefunden, suche in den Gruppen weiter
				 */
				for (IConfigurationGroup group : root.getChildren()) {
					for (IConfigurationBean configurationBean : group
							.getChildren()) {
						if (configurationBean.getID() == bean.getID()) {
							return configurationBean;
						}
					}
				}
			}
		}
		return null;
	}

	/*
	 * Erstellt eine neue Gruppe und befüllt je nach ConfigurationType die
	 * entsprechende Collection mit dem übergebenen Bean.
	 */
	private void createNewGroup(IConfigurationBean bean, String groupName) {
		SortGroupBean groupBean = new SortGroupBean(-1, groupName);

		ConfigurationType type = bean.getParent().getConfigurationType();

		switch (type) {
		case ALARMBEATERBEITER:
			groupBean.getAlarmbearbeiterBeans().add((AlarmbearbeiterBean) bean);
			break;
		case ALARMBEATERBEITERGRUPPE:
			groupBean.getAlarmbearbeitergruppenBeans().add(
					(AlarmbearbeiterGruppenBean) bean);
			break;
		case ALARMTOPIC:
			groupBean.getAlarmtopicBeans().add((AlarmtopicBean) bean);
			break;
		case FILTER:
			groupBean.getFilterBeans().add((FilterBean) bean);
			break;
		case FILTERBEDINGUNG:
			groupBean.getFilterbedingungBeans().add((FilterbedingungBean) bean);
			break;
		}
	}

	/*
	 * XXX: For Tests only!!!
	 */
	private Collection<SortGroupBean> getTestconfigurationNodes() {

		Collection<SortGroupBean> initialData = new ArrayList<SortGroupBean>();

		SortGroupBean groupBean1 = new SortGroupBean(0, "Strasse 1");
		SortGroupBean emptyGroupBean = new SortGroupBean(1, "Strasse 2");
		SortGroupBean topicGroupBean = new SortGroupBean(2, "Topic 1");

		Collection<AlarmbearbeiterBean> alarmbearbeiterBeans = new ArrayList<AlarmbearbeiterBean>();

		// AlarmbearbeiterBeans erzeugen
		AlarmbearbeiterBean alarmbearbeiterBean1 = new AlarmbearbeiterBean();
		alarmbearbeiterBean1.setName("Klaus");
		alarmbearbeiterBean1.setActive(true);
		alarmbearbeiterBean1.setConfirmCode("Confirmation Code");
		alarmbearbeiterBean1.setEmail("alarm@test.de");
		alarmbearbeiterBean1.setMobilePhone("0170/rufmichan");
		alarmbearbeiterBean1.setPhone("040/don't call");
		alarmbearbeiterBean1.setPreferedAlarmType(PreferedAlarmType.EMAIL);
		alarmbearbeiterBean1.setStatusCode("Statuc Code");
		alarmbearbeiterBean1.setUserID(0);

		AlarmbearbeiterBean alarmbearbeiterBean2 = new AlarmbearbeiterBean();
		alarmbearbeiterBean2.setName("Susi");
		alarmbearbeiterBean2.setUserID(1);

		alarmbearbeiterBeans.add(alarmbearbeiterBean1);
		alarmbearbeiterBeans.add(alarmbearbeiterBean2);

		groupBean1.setAlarmbearbeiterBeans(alarmbearbeiterBeans);

		// Alarmtopic Beans
		Collection<AlarmtopicBean> beans2 = new ArrayList<AlarmtopicBean>();
		AlarmtopicBean topicBean1 = new AlarmtopicBean();
		topicBean1.setHumanReadableName("Mein Topic");
		topicBean1.setTopicID(1);
		beans2.add(topicBean1);

		groupBean1.setAlarmtopicBeans(beans2);

		initialData.add(groupBean1);
		initialData.add(topicGroupBean);
		initialData.add(emptyGroupBean);

		return initialData;
	}
}
