package org.csstudio.nams.configurator.treeviewer.model;

import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationBean;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationGroup;

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
		extends AbstractObservableBean implements IConfigurationBean, Comparable<T>{

	public int compareTo(T o) {
		return this.getDisplayName().compareTo(o.getDisplayName());
	}

	private IConfigurationGroup parent;

	public abstract T getClone();

	public abstract void updateState(T bean);

	public abstract int getID();

	public IConfigurationGroup getParent() {
		return parent;
	}

	public void setParent(IConfigurationGroup parent) {
		this.parent = parent;
	}
}
