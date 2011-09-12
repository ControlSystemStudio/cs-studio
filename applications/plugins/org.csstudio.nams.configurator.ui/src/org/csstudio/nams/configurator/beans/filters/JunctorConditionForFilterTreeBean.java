
package org.csstudio.nams.configurator.beans.filters;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;

public class JunctorConditionForFilterTreeBean extends FilterbedingungBean {

	private JunctorConditionType junctorConditionType;
	private final Set<FilterbedingungBean> filterbedingungBeans = new TreeSet<FilterbedingungBean>(
			new JunctorConditionForFilterTreeBeanComparator());

	public JunctorConditionForFilterTreeBean() {
		this.setFilterSpecificBean(null);
	}

	public boolean addOperand(final FilterbedingungBean bean) {
		Contract.require(this != bean, "this != bean"); //$NON-NLS-1$
		return this.filterbedingungBeans.add(bean);
	}

	@Override
	public int compareTo(final FilterbedingungBean o) {
		if (o instanceof JunctorConditionForFilterTreeBean) {
			return this.compareTo((JunctorConditionForFilterTreeBean) o);
		}
		return -1;
	}

	public int compareTo(final JunctorConditionForFilterTreeBean o) {
		int result = -1;
		if (o != null) {
			result = this.junctorConditionType
					.compareTo(o.junctorConditionType);
			if (result == 0) {
				result = this.filterbedingungBeans.hashCode()
						- o.filterbedingungBeans.hashCode();
			}
		}
		return result;
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
		final JunctorConditionForFilterTreeBean other = (JunctorConditionForFilterTreeBean) obj;
		if (this.filterbedingungBeans == null) {
			if (other.filterbedingungBeans != null) {
				return false;
			}
		} else if (!this.filterbedingungBeans
				.equals(other.filterbedingungBeans)) {
			return false;
		}
		if (this.junctorConditionType == null) {
			if (other.junctorConditionType != null) {
				return false;
			}
		} else if (!this.junctorConditionType
				.equals(other.junctorConditionType)) {
			return false;
		}
		return true;
	}

	@Override
	public String getDisplayName() {
		return this.junctorConditionType.name();
	}

	@Override
	public AbstractConfigurationBean<?> getFilterSpecificBean() {
		return null;
	}

	public JunctorConditionType getJunctorConditionType() {
		return this.junctorConditionType;
	}

	public Set<FilterbedingungBean> getOperands() {
		return Collections.unmodifiableSet(this.filterbedingungBeans);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((this.filterbedingungBeans == null) ? 0
						: this.filterbedingungBeans.hashCode());
		result = prime
				* result
				+ ((this.junctorConditionType == null) ? 0
						: this.junctorConditionType.hashCode());
		return result;
	}

	public boolean hasOperands() {
		return !this.filterbedingungBeans.isEmpty();
	}

	public void removeOperand(final FilterbedingungBean bean) {
		this.filterbedingungBeans.remove(bean);
	}

	@Override
	public void setFilterSpecificBean(
			FilterConditionAddOnBean filterSpecificBean) {
		filterSpecificBean = null;
	}

	public void setJunctorConditionType(
			final JunctorConditionType junctorConditionType) {
		this.junctorConditionType = junctorConditionType;
	}

	@Override
	protected void doUpdateState(final FilterbedingungBean bean) {
		if (bean instanceof JunctorConditionForFilterTreeBean) {
			super.doUpdateState(bean);
			this.filterbedingungBeans.clear();
			final JunctorConditionForFilterTreeBean junctorBean = (JunctorConditionForFilterTreeBean) bean;
			final Set<FilterbedingungBean> operands = junctorBean.getOperands();
			for (final FilterbedingungBean filterbedingungBean : operands) {
				if (filterbedingungBean instanceof JunctorConditionForFilterTreeBean
						|| filterbedingungBean instanceof NotConditionForFilterTreeBean) {
					this.filterbedingungBeans.add(filterbedingungBean
							.getClone());
				} else {
					this.filterbedingungBeans.add(filterbedingungBean);
				}
			}
			this.junctorConditionType = junctorBean.junctorConditionType;
		}
	}

	private static class JunctorConditionForFilterTreeBeanComparator implements
			Comparator<FilterbedingungBean> {

		@Override
        public int compare(FilterbedingungBean o1, FilterbedingungBean o2) {
			if (o1 instanceof NotConditionForFilterTreeBean) {
				if (o2 instanceof NotConditionForFilterTreeBean) {
					return o1.compareTo(o2);
				} else {
					return -1;
				}
			}
			if (o2 instanceof NotConditionForFilterTreeBean) {
				return 1;
			}			
			if (o1 instanceof JunctorConditionForFilterTreeBean) {
				if (o2 instanceof JunctorConditionForFilterTreeBean) {
					return o1.compareTo(o2);
				} else if (o2 instanceof NotConditionForFilterTreeBean) {
					return 1;
				} else {
					return -1;
				}
			}
			if (o2 instanceof JunctorConditionForFilterTreeBean) {
				return 1;
			}
				
			return o1.compareTo(o2);
		}
	}
}
