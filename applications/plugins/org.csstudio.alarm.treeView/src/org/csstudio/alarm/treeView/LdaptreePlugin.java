package org.csstudio.alarm.treeView;

import org.csstudio.alarm.treeView.images.LdapImageCache;
import org.csstudio.alarm.treeView.preferences.PreferenceConstants;
import org.csstudio.alarm.treeView.views.models.AlarmConnection;
import org.csstudio.alarm.treeView.views.models.ISimpleTreeParent;
import org.csstudio.alarm.treeView.views.models.LdapConnection;
import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.eclipse.ui.plugin.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The activator class of the LdapTree-Plug-In. This manages the plug-in's
 * lifecycle.
 */
public class LdaptreePlugin extends AbstractCssUiPlugin {

	/**
	 * The plug-in id.
	 */
	private static final String PLUGIN_ID = "org.csstudio.alarm.treeView";
	
	/**
	 * Shared instance of this class.
	 */
	private static LdaptreePlugin plugin;
	
	//this remains from my previous philosophy that you can have many LDAP and JMS connections
	private List<ISimpleTreeParent> connections;
	
	private static LdapImageCache lic;
	
	/**
	 * Returns the shared instance.
	 */
	public static LdaptreePlugin getDefault() {
		return plugin;
	}

	public static LdapImageCache getDefaultImageCache(){
		if (lic == null){
			lic = new LdapImageCache();
		}
		return lic;
	}
		
	/**
	 * The constructor.
	 */
	public LdaptreePlugin() {
		connections = new ArrayList<ISimpleTreeParent>();
		plugin = this;
	}
	
	/**
	 * This method initializes alarm and ldap connection and put the LDAP connection on the viewer
	 */
	public void initalizeConnections(){
		AlarmConnection acc = new AlarmConnection();
		acc.setUrl(getPluginPreferences().getString(PreferenceConstants.JMSURL));
		acc.setTopicName(getPluginPreferences().getString(PreferenceConstants.JMSTOPIC));
		acc.startListening();
		LdapConnection lcc = new LdapConnection(getPluginPreferences().getString(PreferenceConstants.URL),
				getPluginPreferences().getString(PreferenceConstants.USER),getPluginPreferences().getString(PreferenceConstants.PASSWORD),getPluginPreferences().getString(PreferenceConstants.NODE));
		connections.add(lcc);
		lcc.initializeCaching();
		acc.mapHierarchy(lcc);
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	protected void doStart(BundleContext context) throws Exception {
		// do nothing
	}

	/**
	 * This method is called when the plug-in is stopped. We need to stop
	 * listening (JMS needs to be stopped).
	 */
	@Override
	protected void doStop(BundleContext context) throws Exception {
		for (Iterator iter = connections.iterator();iter.hasNext();){
			AlarmConnection acc = (AlarmConnection) iter.next();
			acc.stopListening();
		}
		plugin = null;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.csstudio.alarm.treeView", path);
	}
	
	public List getConnections() {
		// TODO Auto-generated method stub
		return connections;
	}

	/**
	 * Return this plug-in's id.
	 */
	@Override
	public String getPluginId() {
		return PLUGIN_ID;
	}
	
}
