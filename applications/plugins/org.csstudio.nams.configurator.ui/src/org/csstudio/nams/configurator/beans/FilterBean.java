
package org.csstudio.nams.configurator.beans;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.csstudio.nams.configurator.Messages;

public class FilterBean extends AbstractConfigurationBean<FilterBean> {

	public static enum PropertyNames {
		filterID, name, defaultMessage, conditions

	}

	private int filterID;// PRIMARY KEY
	private String name;
	private String defaultMessage;
	private List<FilterbedingungBean> conditions = new LinkedList<FilterbedingungBean>();
	private List<FilterAction> filterActions = new LinkedList<FilterAction>();

	public FilterBean() {
		this.filterID = -1;
	}

	public void addFilterAction(final FilterAction action) {
		this.filterActions.add(action);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final FilterBean other = (FilterBean) obj;
		if (this.conditions == null) {
			if (other.conditions != null) {
				return false;
			}
		} else if (!this.conditions.equals(other.conditions)) {
			return false;
		}
		if (this.defaultMessage == null) {
			if (other.defaultMessage != null) {
				return false;
			}
		} else if (!this.defaultMessage.equals(other.defaultMessage)) {
			return false;
		}
		if (this.filterActions == null) {
			if (other.filterActions != null) {
				return false;
			}
		} else if (!this.filterActions.equals(other.filterActions)) {
			return false;
		}
		if (this.filterID != other.filterID) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	public List<FilterAction> getActions() {

		// //FIXME remove
		// if (filterActions.size() == 0) {
		// AlarmbearbeiterFilterAction alarmbearbeiterFilterAction = new
		// AlarmbearbeiterFilterAction();
		// AlarmbearbeiterBean alarmbearbeiterBean = new AlarmbearbeiterBean();
		// alarmbearbeiterBean.setName("Der Bubu");
		// alarmbearbeiterFilterAction.setReceiver(alarmbearbeiterBean);
		// alarmbearbeiterFilterAction.setType(alarmbearbeiterFilterAction.getFilterActionTypeValues()[0]);
		// filterActions.add(alarmbearbeiterFilterAction);
		// }
		return this.filterActions;
	}

	/**
	 * returns a list of an and combined {@link FilterbedingungBean} list. this
	 * is done for backwards compatibility
	 * 
	 * @return
	 */
	public List<FilterbedingungBean> getConditions() {
		return new LinkedList<FilterbedingungBean>(this.conditions);
	}

	public String getDefaultMessage() {
		return this.defaultMessage;
	}

	@Override
    public String getDisplayName() {
		return this.getName() != null ? this.getName() : Messages.FilterBean_without_name;
	}

	public int getFilterID() {
		return this.filterID;
	}

	@Override
    public int getID() {
		return this.getFilterID();
	}

	public String getName() {
		return this.name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((this.conditions == null) ? 0 : this.conditions.hashCode());
		result = prime
				* result
				+ ((this.defaultMessage == null) ? 0 : this.defaultMessage
						.hashCode());
		result = prime
				* result
				+ ((this.filterActions == null) ? 0 : this.filterActions
						.hashCode());
		result = prime * result + this.filterID;
		result = prime * result
				+ ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	public void setConditions(final List<FilterbedingungBean> conditions) {
		final List<FilterbedingungBean> oldValue = this.conditions;
		this.conditions = conditions;
		Collections.sort(this.conditions);
		this.pcs.firePropertyChange(PropertyNames.conditions.name(), oldValue,
				conditions);
	}

	public void setDefaultMessage(final String defaultMessage) {
		final String oldValue = this.getDefaultMessage();
		this.defaultMessage = defaultMessage;
		this.pcs.firePropertyChange(PropertyNames.defaultMessage.name(),
				oldValue, this.getDefaultMessage());
	}

	public void setFilterID(final int filterID) {
		final int oldValue = this.getFilterID();
		this.filterID = filterID;
		this.pcs.firePropertyChange(PropertyNames.filterID.name(), oldValue,
				this.getFilterID());
	}

	@Override
    public void setID(final int id) {
		this.setFilterID(id);
	}

	public void setName(final String name) {
		final String oldValue = this.getName();
		this.name = name;
		this.pcs.firePropertyChange(PropertyNames.name.name(), oldValue, this
				.getName());
	}

	@Override
	public String toString() {
		return this.getDisplayName();
	}

	@Override
	protected void doUpdateState(final FilterBean bean) {
		this.setDefaultMessage(bean.getDefaultMessage());
		this.setName(bean.getName());
		this.setFilterID(bean.getFilterID());

		final List<FilterbedingungBean> cloneList = new LinkedList<FilterbedingungBean>();
		final List<FilterbedingungBean> list = bean.getConditions();
		for (final FilterbedingungBean filterbedingungBean : list) {
			cloneList.add(filterbedingungBean.getClone());
		}
		this.setConditions(cloneList);

		final LinkedList<FilterAction> cloneActions = new LinkedList<FilterAction>();
		final List<FilterAction> actions = bean.getActions();
		for (final FilterAction filterAction : actions) {
			try {
				cloneActions.add((FilterAction) filterAction.clone());
			} catch (final CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		this.filterActions = cloneActions;
	}

	public void removeAction(FilterAction action) {
		this.filterActions.remove(action);
	}

	public void moveUpAction(FilterAction action) {
		int indexOf = this.filterActions.indexOf(action);
		if (indexOf > 0) {
			Collections.swap(this.filterActions, indexOf, indexOf-1);
		}
	}

	public void moveDownAction(FilterAction action) {
		int indexOf = this.filterActions.indexOf(action);
		if (indexOf < this.filterActions.size()-1) {
			Collections.swap(this.filterActions, indexOf, indexOf+1);
		}
	}

	@Override
    public void setDisplayName(String name) {
		this.setName(name);
	}
}
