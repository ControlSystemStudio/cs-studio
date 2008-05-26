package org.csstudio.nams.configurator.treeviewer.model;

import java.util.ArrayList;
import java.util.Collection;

import org.csstudio.nams.configurator.treeviewer.model.treecomponents.ConfigurationType;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationBean;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationGroup;
import org.eclipse.core.runtime.Assert;

/**
 * Die {@link SortGroupBean} enthält Kider, die im TreeViewer des Configurators
 * dargestellt werden. Da eine Gruppe "global" verwendet wird, kann (muss aber
 * nicht) eine Gruppe verschiedene Kinder beinhalten (siehe die privaten
 * Collections).
 * 
 * @author Eugen Reiswich
 * 
 */
public class SortGroupBean {

	private String groupName;

	private int groupID;

	private Collection<AlarmbearbeiterBean> alarmbearbeiterBeans;

	private Collection<AlarmtopicBean> alarmtopicBeans;

	private Collection<AlarmbearbeitergruppenBean> alarmbearbeitergruppenBeans;

	private Collection<FilterbedingungBean> filterbedingungBeans;

	private Collection<FilterBean> filterBeans;

	/**
	 * Constructor
	 * 
	 * @param ID
	 *            Die Gruppen ID
	 * @param name
	 *            Der Gruppenname
	 */
	public SortGroupBean(int ID, String name) {
		this.groupID = ID;
		this.groupName = name;

		// init collections
		this.alarmbearbeiterBeans = new ArrayList<AlarmbearbeiterBean>();
		this.alarmtopicBeans = new ArrayList<AlarmtopicBean>();
		this.alarmbearbeitergruppenBeans = new ArrayList<AlarmbearbeitergruppenBean>();
		this.filterbedingungBeans = new ArrayList<FilterbedingungBean>();
		this.filterBeans = new ArrayList<FilterBean>();
	}

	public Collection<AlarmbearbeiterBean> getAlarmbearbeiterBeans() {
		return alarmbearbeiterBeans;
	}

	public Collection<AlarmtopicBean> getAlarmtopicBeans() {
		return alarmtopicBeans;
	}

	public Collection<AlarmbearbeitergruppenBean> getAlarmbearbeitergruppenBeans() {
		return alarmbearbeitergruppenBeans;
	}

	public Collection<FilterbedingungBean> getFilterbedingungBeans() {
		return filterbedingungBeans;
	}

	public Collection<FilterBean> getFilterBeans() {
		return filterBeans;
	}

	public String getGroupName() {
		return groupName;
	}

	public int getGroupID() {
		return groupID;
	}

	public String getDisplayName() {
		return getGroupName();
	}

	public void copyStateOf(SortGroupBean otherBean) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not implemented yet.");
	}

	public void setAlarmbearbeiterBeans(
			Collection<AlarmbearbeiterBean> alarmbearbeiterBeans) {
		this.alarmbearbeiterBeans = alarmbearbeiterBeans;
	}

	public void setAlarmtopicBeans(Collection<AlarmtopicBean> alarmtopicBeans) {
		this.alarmtopicBeans = alarmtopicBeans;
	}

	public void setAlarmbearbeitergruppenBeans(
			Collection<AlarmbearbeitergruppenBean> alarmbearbeitergruppenBeans) {
		this.alarmbearbeitergruppenBeans = alarmbearbeitergruppenBeans;
	}

	public void setFilterbedingungBeans(
			Collection<FilterbedingungBean> filterbedingungBeans) {
		this.filterbedingungBeans = filterbedingungBeans;
	}

	public void setFilterBeans(Collection<FilterBean> filterBeans) {
		this.filterBeans = filterBeans;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public boolean isEmpty() {
		boolean empty = false;
		if (this.alarmbearbeiterBeans.isEmpty()
				&& this.alarmbearbeitergruppenBeans.isEmpty()
				&& this.alarmtopicBeans.isEmpty() && this.filterBeans.isEmpty()
				&& this.filterbedingungBeans.isEmpty()) {
			empty = true;
		}

		return empty;
	}

	public IConfigurationBean findBean(IConfigurationBean bean) {
		/*
		 * Falls parent null ist, dann wurde die Bean neu angelegt
		 */
		if (bean.getParent() == null) {
			return null;
		} else {

			ConfigurationType type = bean.getParent().getConfigurationType();

			switch (type) {
			case ALARMBEATERBEITER:
				for (AlarmbearbeiterBean alarmbearbeiterBean : getAlarmbearbeiterBeans()) {
					if (alarmbearbeiterBean.getUserID() == bean.getID()) {
						return alarmbearbeiterBean;
					}
				}
			case ALARMBEATERBEITERGRUPPE:
				for (AlarmbearbeitergruppenBean alarmbearbeiterGruppenBean : getAlarmbearbeitergruppenBeans()) {
					if (alarmbearbeiterGruppenBean.getGroupID() == bean.getID()) {
						return alarmbearbeiterGruppenBean;
					}
				}
			case ALARMTOPIC:
				for (AlarmtopicBean alarmtopicBean : getAlarmtopicBeans()) {
					if (alarmtopicBean.getTopicID() == bean.getID()) {
						return alarmtopicBean;
					}
				}
			case FILTER:
				for (FilterBean filterBean : getFilterBeans()) {
					if (filterBean.getFilterID() == bean.getID()) {
						return filterBean;
					}
				}
			case FILTERBEDINGUNG:
				for (FilterbedingungBean filterbedingungBean : getFilterbedingungBeans()) {
					if (filterbedingungBean.getFilterbedinungID() == bean
							.getID()) {
						return filterbedingungBean;
					}
				}
			}
		}

		return null;
	}

	public void removeBean(IConfigurationBean bean) {
		IConfigurationGroup parent = bean.getParent();
		Assert.isNotNull(parent);
		ConfigurationType beanType = parent.getConfigurationType();
		switch (beanType) {
		case ALARMBEATERBEITER:
			for (AlarmbearbeiterBean alarmbearbeiter : this
					.getAlarmbearbeiterBeans()) {
				if (alarmbearbeiter.getUserID() == bean.getID()) {
					this.getAlarmbearbeiterBeans().remove(alarmbearbeiter);
					break;
				}
			}
			break;
		case ALARMBEATERBEITERGRUPPE:
			for (AlarmbearbeitergruppenBean alarmbearbeitergruppe : this
					.getAlarmbearbeitergruppenBeans()) {
				if (alarmbearbeitergruppe.getGroupID() == bean.getID()) {
					this.getAlarmbearbeitergruppenBeans().remove(
							alarmbearbeitergruppe);
					break;
				}
			}
			break;
		case ALARMTOPIC:
			for (AlarmtopicBean alarmtopic : this.getAlarmtopicBeans()) {
				if (alarmtopic.getTopicID() == bean.getID()) {
					this.getAlarmtopicBeans().remove(alarmtopic);
					break;
				}
			}
			break;
		case FILTER:
			for (FilterBean filter : this.getFilterBeans()) {
				if (filter.getFilterID() == bean.getID()) {
					this.getFilterBeans().remove(filter);
					break;
				}
			}
			break;
		case FILTERBEDINGUNG:
			for (FilterbedingungBean filterbedingung : this
					.getFilterbedingungBeans()) {
				if (filterbedingung.getFilterbedinungID() == bean.getID()) {
					this.getFilterbedingungBeans().remove(filterbedingung);
					break;
				}
			}
			break;
		}
	}

	public void addConfigurationItem(IConfigurationBean bean) {

		/*
		 * Da die übergebene Bean neue angelegt sein kann und daher kein Parent
		 * hat, muss hier statt bean.getParent().getConfigurationType() mit
		 * instanceof gearbeitet werden.
		 */
		ConfigurationType beanType = null;
		if (bean instanceof AlarmbearbeiterBean) {
			beanType = ConfigurationType.ALARMBEATERBEITER;
		} else if (bean instanceof AlarmbearbeitergruppenBean) {
			beanType = ConfigurationType.ALARMBEATERBEITERGRUPPE;
		} else if (bean instanceof AlarmtopicBean) {
			beanType = ConfigurationType.ALARMTOPIC;
		} else if (bean instanceof FilterBean) {
			beanType = ConfigurationType.FILTER;
		} else if (bean instanceof FilterbedingungBean) {
			beanType = ConfigurationType.FILTERBEDINGUNG;
		}

		switch (beanType) {
		case ALARMBEATERBEITER:
			this.getAlarmbearbeiterBeans().add((AlarmbearbeiterBean) bean);
			break;
		case ALARMBEATERBEITERGRUPPE:
			this.getAlarmbearbeitergruppenBeans().add(
					(AlarmbearbeitergruppenBean) bean);
			break;
		case ALARMTOPIC:
			this.getAlarmtopicBeans().add((AlarmtopicBean) bean);
			break;
		case FILTER:
			this.getFilterBeans().add((FilterBean) bean);
			break;
		case FILTERBEDINGUNG:
			this.getFilterbedingungBeans().add((FilterbedingungBean) bean);
			break;
		}
	}

}
