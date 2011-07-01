
package org.csstudio.nams.configurator.beans;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Da einige Beans mehr als den PropertyChangeSupport benötigen, wird die
 * abstrakte Klasse {@link AbstractConfigurationBean} eingeführt.
 * 
 * Diese bietet durch die Vererbung von {@link AbstractObservableBean} einerseit
 * den PropertyChangeSupport, andererseit implementiert sie das Interface
 * {@link IConfigurationBean}, wodurch erbende Klassen im TreeViewer nach ihrem
 * HumanReadableName gefragt werden können.
 * 
 * Von {@link AbstractConfigurationBean} erben hauptsächlich unsere Tree-Beans,
 * die alle einen Clone zurückliefern müsen sowie eine update-Funktion zur
 * Verfügung stellen müssen
 * 
 * @author eugrei
 * 
 * @param <T>
 */
public abstract class AbstractConfigurationBean<T extends AbstractConfigurationBean<T> & IConfigurationBean>
		implements IConfigurationBean, Comparable<T> {

	public static enum AbstractPropertyNames {
		rubrikName
	}

	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private String rubrikName = ""; //$NON-NLS-1$

	@Override
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Propertychange suppoert for JFace Databinding
	 */
	public void addPropertyChangeListener(final String propertyName,
			final PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(propertyName, listener);
	}

	public void clearPropertyChangeListeners() {
		this.pcs = new PropertyChangeSupport(this);
	}

	@Override
    public int compareTo(final T o) {
		return this.getDisplayName().compareTo(o.getDisplayName());
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final AbstractConfigurationBean<?> other = (AbstractConfigurationBean<?>) obj;
		if (this.rubrikName == null) {
			if (other.rubrikName != null) {
				return false;
			}
		} else if (!this.rubrikName.equals(other.rubrikName)) {
			return false;
		}
		return true;
	}

	@Override
    @SuppressWarnings("unchecked") //$NON-NLS-1$
	public T getClone() {
		T cloneBean = null;
		try {
			cloneBean = (T) this.getClass().newInstance();
		} catch (final InstantiationException e) {
			e.printStackTrace();
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
		}
		cloneBean.updateState((T) this);
		return cloneBean;
	}

	@Override
    public PropertyChangeSupport getPropertyChangeSupport() {
		return this.pcs;
	}

	@Override
    public String getRubrikName() {
		return this.rubrikName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.rubrikName == null) ? 0 : this.rubrikName.hashCode());
		return result;
	}

	@Override
    public void removePropertyChangeListener(
			final PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(final String propertyName,
			final PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(propertyName, listener);
	}

	public void setRubrikName(final String groupName) {
		final String oldValue = this.rubrikName;
		this.rubrikName = groupName;
		this.pcs.firePropertyChange("rubrikName", oldValue, groupName); //$NON-NLS-1$
	}

	public void updateState(final T bean) {
		this.setRubrikName(bean.getRubrikName());
		this.doUpdateState(bean);
	}

	protected abstract void doUpdateState(T bean);
}
