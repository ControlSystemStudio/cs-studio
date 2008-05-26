package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Diese Klasse repräsentiert eine Gruppe im TreeViewer. Die
 * {@link SortgroupNode} ist immer einem bestimmten Root-Element und damit einem
 * bestimmten {@link ConfigurationType} zugeordnet.
 * 
 * @author Eugen Reiswich
 * 
 */
public class SortgroupNode extends AbstractConfigurationNode implements
		IConfigurationGroup {

	private Collection<? extends IConfigurationBean> beans;

	public SortgroupNode(String name, IConfigurationRoot parent) {
		super(name, parent);
	}

	/**
	 * Liefert die Mitglieder einer Gruppe abhängig vom GroupType
	 * 
	 * @return
	 */
	public Collection<? extends IConfigurationBean> getChildren() {
		/*
		 * falls Kinder null sind, erzeuge eine leere Liste. Andernfalls wird
		 * eine NullpointerException im TreeViewer in der Methode hasChildren
		 * geworfen
		 */
		return beans == null ? new ArrayList<IConfigurationBean>() : beans;
	}

	/**
	 * Setze die Kinder einer Gruppe. Hier wird auch gleichzeitig die
	 * Parent-Gruppe gesetzt
	 */
	public void setChildren(Collection<? extends IConfigurationBean> children) {
		Collection<IConfigurationBean> items = new ArrayList<IConfigurationBean>();
		for (IConfigurationBean bean : children) {
			bean.setParent(this);
			items.add(bean);
		}
		this.beans = items;
	}

	/**
	 * Liefert den {@link ConfigurationType} der Gruppe.
	 */
	public ConfigurationType getConfigurationType() {
		return ((IConfigurationRoot) getParent()).getConfigurationType();
	}
}
