package org.remotercp.common.provisioning;

import java.util.Collection;

import org.eclipse.ecf.core.identity.ID;
import org.remotercp.common.servicelauncher.IRemoteServiceLauncher;

public interface IInstalledFeaturesService extends IRemoteServiceLauncher{

	/**
	 * Returns an array with installed bundles in the users rcp application.
	 * 
	 * @return
	 */
	public Collection<SerializedBundleWrapper> getInstalledBundles();

	/**
	 * Returns a list with all installed features on the users rcp application.
	 */
	public Collection<SerializedFeatureWrapper> getInstalledFeatures();

	public String getUserInfo();

	/**
	 * Returns the remote user {@link ID}
	 * 
	 * @return
	 */
	public ID getUserID();

}
