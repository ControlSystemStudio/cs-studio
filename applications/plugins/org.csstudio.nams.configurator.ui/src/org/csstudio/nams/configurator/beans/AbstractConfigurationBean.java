package org.csstudio.nams.configurator.beans;

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
public abstract class AbstractConfigurationBean<T extends IConfigurationBean>
		extends AbstractObservableBean implements Comparable<T> {

	private String rubrikName;

	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcs;
	}

	public int compareTo(T o) {
		return this.getDisplayName().compareTo(o.getDisplayName());
	}

	public T getClone() {
		T cloneBean = null;
		try {
			cloneBean = (T) this.getClass().newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		((AbstractConfigurationBean) cloneBean).updateState(this);
		return cloneBean;
	}

	public void updateState(T bean) {
		setRubrikName(bean.getRubrikName());
		doUpdateState(bean);
	}

	protected abstract void doUpdateState(T bean);

	public String getRubrikName() {
		return rubrikName;
	}

	public void setRubrikName(String groupName) {
		String oldValue = this.rubrikName;
		this.rubrikName = groupName;
		pcs.firePropertyChange("rubrikName", oldValue, groupName);
	}
}
