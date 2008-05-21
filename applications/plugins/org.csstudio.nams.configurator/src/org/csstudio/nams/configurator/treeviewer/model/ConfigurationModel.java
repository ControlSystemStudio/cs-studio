package org.csstudio.nams.configurator.treeviewer.model;

import java.util.Collection;

import org.csstudio.nams.configurator.treeviewer.model.treecomponents.SortGroupBean;

/**
 * Diese Klasse ist das Model für den TreeViewer des AMS-Configurators.
 * 
 * @author eugrei
 * 
 */
public class ConfigurationModel {

	private Collection<SortGroupBean> sortgroupBeans;

	public Collection<SortGroupBean> getSortgroupBeans() {
		return sortgroupBeans;
	}

}
