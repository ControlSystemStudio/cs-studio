package org.remotercp.provisioning.install;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.update.configuration.IConfiguredSite;
import org.eclipse.update.configuration.IInstallConfiguration;
import org.eclipse.update.core.IFeature;
import org.eclipse.update.core.IFeatureReference;
import org.eclipse.update.core.ISite;
import org.eclipse.update.core.ISiteFeatureReference;
import org.eclipse.update.core.SiteManager;
import org.eclipse.update.internal.operations.IUnconfigureAndUninstallFeatureOperation;
import org.eclipse.update.internal.operations.OperationFactory;
import org.eclipse.update.operations.IInstallFeatureOperation;
import org.eclipse.update.operations.IOperation;
import org.eclipse.update.operations.OperationsManager;
import org.remotercp.common.provisioning.IInstallFeaturesService;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.common.status.SerializableStatus;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.provisioning.UpdateActivator;
import org.remotercp.provisioning.dialogs.AcceptUpdateDialog;
import org.remotercp.util.authorization.AuthorizationUtil;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;
import org.remotercp.util.status.StatusUtil;

/**
 * This class will install, uninstall and update features on the users local RCP
 * application and return a collection with status objects in order to inform
 * about the success/flop of the according operation.
 *
 * TODO: As Eclipse does not remove uninstalled features from the disk think
 * about an additional method which will do this step.
 *
 * @author Eugen Reiswich
 * @date 28.07.2008
 *
 */
public class InstallFeaturesServiceImpl implements IInstallFeaturesService {

	private static final Logger LOG = Logger.getLogger(InstallFeaturesServiceImpl.class.getName());

	private final ReentrantLock _lock = new ReentrantLock(true);

	/**
	 * This method prepares the {@link IInstallFeatureOperation} and calls the
	 * execution of the installation process.
	 *
	 * @param features
	 *            Features to install
	 * @return The result of the installation process collected in a List of
	 *         {@link IStatus} objects.
	 */
	private List<IStatus> installFeatures(final IFeature[] features) {
		final List<IStatus> statusCollector = new ArrayList<IStatus>();
		final List<IInstallFeatureOperation> installOperations = new ArrayList<IInstallFeatureOperation>();

		if (!_lock.isLocked()) {
			try {
				_lock.lock();

				/*
				 * create install operations first.
				 */
				for (final IFeature feature : features) {

					/*
					 * TODO: pay attention to the last three parameters and
					 * implement the necessary methods
					 */
					final IConfiguredSite configuredSite = getLocalConfiguredSiteForFeature(feature
							.getVersionedIdentifier().getIdentifier());

					final IInstallFeatureOperation installOperation = OperationsManager
							.getOperationFactory().createInstallOperation(
									configuredSite, feature, null, null, null);
					installOperations.add(installOperation);

				}

				this.executeInstallation(statusCollector, installOperations);

			} catch (final CoreException e) {
				final IStatus status = createStatus(IStatus.ERROR,
						"Unable to retrieve the IConfigureSite", e);
				statusCollector.add(status);
			} finally {
				_lock.unlock();
			}

		} else {
			final IStatus status = createStatus(
					IStatus.ERROR,
					"Concurrent operation occured while trying to install features",
					null);
			statusCollector.add(status);

		}
		LOG.info("Install operation finished");
		return statusCollector;
	}

	/**
	 * This method performs the installation for the given install commands and
	 * records errors occurred during the install operations.
	 *
	 * In addition to that this method calls the uninstall operation, if an old
	 * feature exists for a given new feature to install.
	 *
	 * @param statusCollector
	 *            List to collect status information e.g. errors or ok status.
	 *            The {@link MultiStatus} object is not used as it is not
	 *            Serializable.
	 * @param installOperations
	 *            List of install operations to execute
	 */
	protected void executeInstallation(final List<IStatus> statusCollector,
			final List<IInstallFeatureOperation> installOperations) {

		/*
		 * TODO: replace this with OperationsManager.getOperationFactory(
		 * ).createBatchInstallOperation[]
		 */
		for (final IInstallFeatureOperation installOperation : installOperations) {
			final IFeature feature = installOperation.getFeature();

			LOG.info("Installing: " + feature.getLabel());
			try {
				final boolean success = installOperation.execute(null, null);
				if (success) {
					final IStatus installOk = createStatus(IStatus.OK, "Feature "
							+ feature.getLabel()
							+ " was successfully installed", null);
					statusCollector.add(installOk);

					final IFeature oldFeature = installOperation.getOldFeature();
					if (oldFeature != null) {
						final IFeature[] uninstallFeature = new IFeature[1];
						uninstallFeature[0] = oldFeature;

						final List<IStatus> uninstallStatus = uninstallFeatures(uninstallFeature);

						final int checkStatus = StatusUtil
								.checkStatus(uninstallStatus);

						if (checkStatus == IStatus.OK) {
							final IStatus statusOK = createStatus(IStatus.OK,
									"Old feature: " + oldFeature.getLabel()
											+ " was succesfully uninstalled",
									null);
							statusCollector.add(statusOK);
						}
					}
				} else {
					final IStatus statusOK = createStatus(IStatus.OK, "Feature: "
							+ feature.getLabel() + " was installed", null);
					statusCollector.add(statusOK);
				}

			} catch (final CoreException e) {
				final IStatus error = createStatus(IStatus.ERROR,
						"Unable to install feature: " + feature.getLabel(), e);
				statusCollector.add(error);
			} catch (final InvocationTargetException e) {
				final IStatus error = createStatus(IStatus.ERROR,
						"Unable to install feature: " + feature.getLabel(), e);
				statusCollector.add(error);
			}
		}

	}

	/*
	 * This method performs the uninstall operation for the given features.
	 */
	@SuppressWarnings("restriction")
	private List<IStatus> uninstallFeatures(final IFeature[] features) {
		/*
		 * if uninstall was successfull this list will contain only one OK
		 * status, else errors are stored here
		 */
		final List<IStatus> statusCollector = new ArrayList<IStatus>();
		final List<IOperation> unconfigAndUninstallOperations = new ArrayList<IOperation>();
		final Map<IConfiguredSite, IFeature> featuresToDeleteFromDisk = new HashMap<IConfiguredSite, IFeature>();
		if (!_lock.isLocked()) {
			try {
				_lock.lock();

				// create first uninstall operations
				for (final IFeature feature : features) {
					try {
						final IConfiguredSite configuredSite = getLocalConfiguredSiteForFeature(feature
								.getVersionedIdentifier().getIdentifier());

						featuresToDeleteFromDisk.put(configuredSite, feature);

						final IOperation unconfigAndUninstallOperation = ((OperationFactory) OperationsManager
								.getOperationFactory())
								.createUnconfigureAndUninstallFeatureOperation(
										configuredSite, feature);
						unconfigAndUninstallOperations
								.add(unconfigAndUninstallOperation);

					} catch (final CoreException e) {
						final IStatus error = createStatus(IStatus.ERROR,
								"Failed to retrieve configured site for feature: "
										+ feature.getLabel(), e);
						statusCollector.add(error);
					}
				}

				// execute uninstall operations
				for (final IOperation unconfigAndUninstallOperation : unconfigAndUninstallOperations) {
					final IFeature feature = ((IUnconfigureAndUninstallFeatureOperation) unconfigAndUninstallOperation)
							.getFeature();

					try {
						unconfigAndUninstallOperation.execute(null, null);
					} catch (final CoreException e) {
						final IStatus error = createStatus(IStatus.ERROR,
								"Failed to uninstall feature: "
										+ feature.getLabel(), e);
						statusCollector.add(error);
						e.printStackTrace();
					} catch (final InvocationTargetException e) {
						final IStatus error = createStatus(IStatus.ERROR,
								"Failed to uninstall feature: "
										+ feature.getLabel(), e);
						statusCollector.add(error);
						e.printStackTrace();
					}
				}

				// remove files from disk
				new DeleteFeaturesOperation(featuresToDeleteFromDisk).run();

				final int uninstallOK = StatusUtil.checkStatus(statusCollector);
				if (uninstallOK == IStatus.OK) {
					final IStatus okStatus = createStatus(IStatus.OK,
							"Features have been successfully uninstalled", null);
					statusCollector.add(okStatus);
				}

			} finally {
				_lock.unlock();
			}

		} else {
			final IStatus concurrentError = createStatus(
					IStatus.ERROR,
					"Concurrent operation error occured while trying to uninstall features",
					null);
			statusCollector.add(concurrentError);
		}
		return statusCollector;
	}

	/**
	 * As the IFeature object is not serializable the Admin will get a
	 * SerializedFeatureWrapper Object which only contains the featureId,
	 * version, label and URL to perform uninstall operations. In order to allow
	 * admin to directly uninstall Features one has first to find the
	 * corresponding locally installed IFeature objects.
	 *
	 * @param Feature
	 *            Ids retrieved from
	 *            Feature.getVersionedIdentifier().getIdentifier();
	 * @return A list of {@link IStatus} objects which contains the result of
	 *         the uninstall operation e.g. errors, warnings and ok status
	 */
	public List<IStatus> uninstallFeatures(final String[] featuresIds, final ID fromId) {
		// is user fromId allowed to execute this operation?
		final boolean canExecute = AuthorizationUtil.checkAuthorization(fromId,
				"uninstallFeatures");

		final List<IStatus> statusCollector = new ArrayList<IStatus>();
		final Set<IFeature> correspondingFeatures = new HashSet<IFeature>();
		if (canExecute) {

			final List<String> featureStringIds = Arrays.asList(featuresIds);

			/* get local installed features */
			try {
				final IFeatureReference[] featureReferences = getFeatureReferences();

				for (final IFeatureReference ref : featureReferences) {
					final IFeature feature = ref.getFeature(null);
					final String featureId = feature.getVersionedIdentifier()
							.getIdentifier();

					// features found?
					if (featureStringIds.contains(featureId)) {
						correspondingFeatures.add(feature);
					}
				}

				// perform update
				if (!correspondingFeatures.isEmpty()) {
					final IFeature[] featuresToUninstall = correspondingFeatures
							.toArray(new IFeature[correspondingFeatures.size()]);
					final List<IStatus> updateStatus = uninstallFeatures(featuresToUninstall);

					final int uninstallOK = StatusUtil.checkStatus(updateStatus);
					if (uninstallOK == IStatus.OK) {
						final IStatus okStatus = createStatus(IStatus.OK,
								"Features have been uninstalled", null);
						statusCollector.add(okStatus);
					}
				}

			} catch (final CoreException e) {
				final IStatus error = createStatus(IStatus.ERROR,
						"Unable to retrieve the ISite configuration", e);
				statusCollector.add(error);
			}

		} else {
			final IStatus authorizationFailed = createStatus(
					IStatus.ERROR,
					"Authorization failed for user: "
							+ fromId.getName()
							+ ". Only administrators are allowed to perform uninstall operations.",
					null);
			statusCollector.add(authorizationFailed);
		}

		return statusCollector;
	}

	/**
	 * Returns feature references for all local configured sites.
	 *
	 * @return
	 * @throws CoreException
	 */
	private IFeatureReference[] getFeatureReferences() throws CoreException {
		final List<IFeatureReference> featureReferences = new ArrayList<IFeatureReference>();
		final IConfiguredSite[] configuredSites = getConfiguration()
				.getConfiguredSites();

		for (final IConfiguredSite site : configuredSites) {
			final IFeatureReference[] featureRef = site.getFeatureReferences();
			if (featureRef != null) {
				for (final IFeatureReference ref : featureRef) {
					featureReferences.add(ref);
				}
			}
		}

		return featureReferences
				.toArray(new IFeatureReference[featureReferences.size()]);
	}

	private IInstallConfiguration getConfiguration() throws CoreException {
		final IInstallConfiguration currentConfiguration = SiteManager.getLocalSite()
				.getCurrentConfiguration();
		return currentConfiguration;
	}

	/**
	 * This method can be called if all installs/updates/uninstalls have been
	 * finished in order to apply changes to the running application.
	 */
	public List<IStatus> restartApplication(final ID fromId) {
		final List<IStatus> statusCollector = new ArrayList<IStatus>();
		// is user fromId allowed to execute this operation?
		final boolean canExecute = AuthorizationUtil.checkAuthorization(fromId,
				"restartApplication");

		if (canExecute) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					PlatformUI.getWorkbench().restart();
					final IStatus authorizationFailed = createStatus(IStatus.ERROR,
							"Application has been sucessfully restarted", null);
					statusCollector.add(authorizationFailed);
				}
			});
		} else {
			final IStatus authorizationFailed = createStatus(
					IStatus.ERROR,
					"Authorization failed for user: "
							+ fromId.getName()
							+ ". Only administrators are allowed to perform restart operations.",
					null);
			statusCollector.add(authorizationFailed);
		}
		return statusCollector;
	}

	/**
	 * This method can be called to ask the client if updates can be performed
	 * now. The client will get an dialog with a count down where he/she can
	 * choose to perform updates now or cancel.
	 *
	 * @return The status whether client has accepted (Status.OK) the update or
	 *         cancelled (Status.CANCELLED)
	 */
	public IStatus acceptUpdate(final ID fromId) {
		final ISessionService sessionService = OsgiServiceLocatorUtil.getOSGiService(
				UpdateActivator.getBundleContext(), ISessionService.class);
		final String userName = sessionService.getConnectionDetails()
				.getUserName();
		/*
		 * XXX: this list is a workaround for syncExec-Operation. As we are only
		 * able to pass final parameters to the run() method there is no other
		 * way? to apply status-information dynamically.
		 */
		final List<IStatus> statusCollector = new ArrayList<IStatus>();

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				final AcceptUpdateDialog restartDialog = new AcceptUpdateDialog();
				final int result = restartDialog.open();
				if (result == SWT.OK) {
					final IStatus status = createStatus(IStatus.OK, userName
							+ " has accepted update", null);
					statusCollector.add(status);
				} else {
					final IStatus status = createStatus(IStatus.CANCEL, userName
							+ " cancelled update", null);
					statusCollector.add(status);
				}
			}
		});

		return statusCollector.get(0);
	}

	/**
	 * This method will install the provided features in user's RCP application.
	 */
	public List<IStatus> installFeatures(final SerializedFeatureWrapper[] features,
			final ID fromId) {
		final List<IStatus> statusCollector = new ArrayList<IStatus>();
		final List<IFeature> featuresToUpdate = new ArrayList<IFeature>();

		// is user fromId allowed to execute this operation?
		final boolean canExecute = AuthorizationUtil.checkAuthorization(fromId,
				"installFeatures");
		if (canExecute) {

			try {
				for (final SerializedFeatureWrapper serializedFeature : features) {
					final ISite site = SiteManager.getSite(serializedFeature
							.getUpdateUrl(), null);

					final ISiteFeatureReference[] featureReferences = site
							.getFeatureReferences();

					for (final ISiteFeatureReference featureReference : featureReferences) {
						final IFeature feature = featureReference.getFeature(null);
						final String featureId = feature.getVersionedIdentifier()
								.getIdentifier();
						final String featureVersion = feature
								.getVersionedIdentifier().getVersion()
								.toString();
						if (featureId.equals(serializedFeature.getIdentifier())) {
							// right feature for update found. now get the right
							// version
							if (featureVersion.equals(serializedFeature
									.getVersion())) {
								featuresToUpdate.add(feature);
							}
						}
					}
				}

				// now perform the update
				final IFeature[] featuresReadyForUpdate = featuresToUpdate
						.toArray(new IFeature[featuresToUpdate.size()]);

				final List<IStatus> installResult = this
						.installFeatures(featuresReadyForUpdate);
				statusCollector.addAll(installResult);
			} catch (final CoreException e) {
				final IStatus error = createStatus(
						IStatus.ERROR,
						"Unable to retrieve the ISite configuration while trying to install features.",
						e);
				statusCollector.add(error);
			}
		} else {
			final IStatus authorizationFailed = createStatus(
					IStatus.ERROR,
					"Authorization failedfor user: "
							+ fromId.getName()
							+ ". Only administrators are allowed to perform install operations.",
					null);
			statusCollector.add(authorizationFailed);
		}

		return statusCollector;
	}

	/**
	 * TODO: check if a feature can be configured in more than one site!!!
	 *
	 * Get the local installed Feature for a given featureId.
	 *
	 * @param featureId
	 *            The id of the feature which local respresentative should be
	 *            found.
	 * @return The local installed feature for the given feature id.
	 * @throws CoreException
	 */
	private IConfiguredSite getLocalConfiguredSiteForFeature(final String featureId)
			throws CoreException {
		IConfiguredSite returnSite = null;

		final IConfiguredSite[] configuredSites = getConfiguration()
				.getConfiguredSites();
		for (final IConfiguredSite site : configuredSites) {
			final IFeatureReference[] featureReferences = site.getFeatureReferences();
			for (final IFeatureReference featureReference : featureReferences) {
				final IFeature feature = featureReference.getFeature(null);
				if (feature.getVersionedIdentifier().getIdentifier().equals(
						featureId)) {
					returnSite = site;
					break;
				}
			}
		}
		if (returnSite == null) {
			// feature is not configured now == new feature
			returnSite = configuredSites[0];
		}
		return returnSite;
	}

	/**
	 * An update is a mix of uninstall old feature and install the new feature.
	 * Therefore we can call installFeatures method.
	 */
	public List<IStatus> updateFeautures(final SerializedFeatureWrapper[] features,
			final ID fromId) {
		List<IStatus> installResult = new ArrayList<IStatus>();
		// is user fromId allowed to execute this operation?
		final boolean canExecute = AuthorizationUtil.checkAuthorization(fromId,
				"updateFeatures");
		if (canExecute) {
			installResult = this.installFeatures(features, fromId);
		} else {
			final IStatus authorizationFailed = createStatus(
					IStatus.ERROR,
					"Authorization failed for user: "
							+ fromId.getName()
							+ ". Only administrators are allowed to perform update operations.",
					null);
			installResult.add(authorizationFailed);
		}

		return installResult;
	}

	/*
	 * This method will create a serializable status and log the status message.
	 */
	private IStatus createStatus(final int severity, final String message, final Exception e) {
		LOG.info(message);

		if (e == null) {
			return new SerializableStatus(severity, UpdateActivator.PLUGIN_ID,
					message);
		} else {
			return new SerializableStatus(severity, UpdateActivator.PLUGIN_ID,
					message, e);
		}

	}

	/***************************************************************************
	 * This method does register the remote InstallFeatureService as a remote
	 * operation.
	 */
	public void startServices() {
		LOG.info("******* Starting service: "
				+ InstallFeaturesServiceImpl.class.getName() + " *******");

		final ISessionService sessionService = OsgiServiceLocatorUtil.getOSGiService(
				UpdateActivator.getBundleContext(), ISessionService.class);
		sessionService.registerRemoteService(IInstallFeaturesService.class
				.getName(), new InstallFeaturesServiceImpl());
	}
}
