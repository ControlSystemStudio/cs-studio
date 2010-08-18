package org.remotercp.provisioning.features;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.update.configuration.IConfiguredSite;
import org.eclipse.update.configuration.ILocalSite;
import org.eclipse.update.core.IFeature;
import org.eclipse.update.core.IFeatureReference;
import org.eclipse.update.core.SiteManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.remotercp.common.provisioning.IInstalledFeaturesService;
import org.remotercp.common.provisioning.SerializedBundleWrapper;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.provisioning.UpdateActivator;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;

public class InstalledFeaturesServiceImpl implements IInstalledFeaturesService {

	private ID userID;

	private final static Logger LOG = Logger.getLogger(InstalledFeaturesServiceImpl.class.getName());

	public InstalledFeaturesServiceImpl() {

		this.init();
	}

	private void init() {
		final ISessionService sessionService = OsgiServiceLocatorUtil.getOSGiService(
				UpdateActivator.getBundleContext(), ISessionService.class);
		userID = sessionService.getRoster().getUser().getID();
	}

	/**
	 * Returns a list with all installed features on the users rcp application.
	 */
	public synchronized Collection<SerializedFeatureWrapper> getInstalledFeatures() {
		LOG.info("getInstalledFeature method performed");
		final Collection<SerializedFeatureWrapper> features = loadFeatures();
		return features;
	}

	/**
	 * Returns an array with installed bundles in the users rcp application.
	 *
	 * @return
	 */
	@Deprecated
	public synchronized Collection<SerializedBundleWrapper> getInstalledBundles() {
		final Collection<SerializedBundleWrapper> bundleWrapper = new ArrayList<SerializedBundleWrapper>();
		final BundleContext bundleContext = UpdateActivator.getBundleContext();
		if (bundleContext != null) {
			final Bundle[] bundles = bundleContext.getBundles();

			for (final Bundle bundle : bundles) {
				final SerializedBundleWrapper serializedBundleWrapper = new SerializedBundleWrapper();
				serializedBundleWrapper.setBundleId(bundle.getBundleId());
				serializedBundleWrapper.setState(bundle.getState());
				serializedBundleWrapper.setLabel(bundle.getSymbolicName());

				final Dictionary<?, ?> headers = bundle.getHeaders();
				final String bundleVersion = (String) headers.get("Bundle-Version");
				serializedBundleWrapper.setVersion(bundleVersion);

				bundleWrapper.add(serializedBundleWrapper);
			}
		}
		return bundleWrapper;
	}

	/*
	 * This method could be deprecated since Equinox p2 uses a new way to get
	 * locally installed features? Is this true?
	 */
	private Collection<SerializedFeatureWrapper> loadFeatures() {

		// final List<IFeature> installedFeatures = new ArrayList<IFeature>();
		final Collection<SerializedFeatureWrapper> installedFeaturesWrapper = new ArrayList<SerializedFeatureWrapper>();
		try {
			// Set<String> installedFeaktures = new HashSet<String>();

			// Map<URL, IConfiguredSite> discoverySites = new
			// HashMap<URL,
			// IConfiguredSite>();

			final ILocalSite localSite = SiteManager.getLocalSite();

			final IConfiguredSite[] sites = localSite.getCurrentConfiguration()
					.getConfiguredSites();

			for (final IConfiguredSite site : sites) {
				for (final IFeatureReference featureRef : site
						.getConfiguredFeatures()) {

					final IFeature feature = featureRef.getFeature(null);
					final SerializedFeatureWrapper featureWrapper = SerializedFeatureWrapper
							.createFeatureWrapper(feature);
					installedFeaturesWrapper.add(featureWrapper);
				}
			}

		} catch (final CoreException e) {
			e.printStackTrace();
		}

		return installedFeaturesWrapper;

	}

	public String getUserInfo() {

		return "Remote Service performed on user: "
				+ this.userID.toExternalForm();
	}

	public ID getUserID() {
		return this.userID;
	}

	public void startServices() {
		LOG.info("******* Starting service: "
				+ InstalledFeaturesServiceImpl.class.getName() + " ********");

		final ISessionService sessionService = OsgiServiceLocatorUtil.getOSGiService(
				UpdateActivator.getBundleContext(), ISessionService.class);
		sessionService.registerRemoteService(IInstalledFeaturesService.class.getName(), new InstalledFeaturesServiceImpl());

	}

}
