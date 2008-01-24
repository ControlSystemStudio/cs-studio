package org.csstudio.sds.ui.internal.feedback;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.feedback.IGraphicalFeedbackFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * Access service for contributions for the extension point
 * <b>org.csstudio.sds.graphicalFeedbackFactories</b>.
 * 
 * This service will only be used internally.
 * 
 * @author Sven Wende
 * 
 */
public final class GraphicalFeedbackContributionsService {

	/**
	 * The singleton instance.
	 */
	private static GraphicalFeedbackContributionsService _instance = null;

	/**
	 * Contains descriptors for all contributions.
	 */
	private Map<String, GraphicalFeedbackContributionDescriptor> _descriptors;

	/**
	 * Private constructor.
	 */
	private GraphicalFeedbackContributionsService() {
		lookup();
	}

	/**
	 * Gets the singleton instance.
	 * 
	 * @return the singleton instance
	 */
	public static GraphicalFeedbackContributionsService getInstance() {
		if (_instance == null) {
			_instance = new GraphicalFeedbackContributionsService();
		}

		return _instance;
	}

	/**
	 * Returns a graphical feedback factory for widget models of the specified
	 * type. If no special factory was contributed for this specific type, a
	 * default implementation is returned.
	 * 
	 * @param typeId
	 *            widget model type id
	 * 
	 * @return a graphical feedback factory
	 */
	public IGraphicalFeedbackFactory getGraphicalFeedbackFactory(
			final String typeId) {
		IGraphicalFeedbackFactory factory = new DefaultFeedbackFactory();

		if (_descriptors.containsKey(typeId)) {
			factory = _descriptors.get(typeId).getGraphicalFeedbackFactory();
		}

		return factory;
	}

	/**
	 * Looks up the extension registry for contributions.
	 */
	private void lookup() {
		_descriptors = new HashMap<String, GraphicalFeedbackContributionDescriptor>();

		IExtensionRegistry extReg = Platform.getExtensionRegistry();
		String id = SdsUiPlugin.EXTPOINT_GRAPHICAL_FEEDBACK_FACTORIES;
		IConfigurationElement[] confElements = extReg
				.getConfigurationElementsFor(id);

		for (IConfigurationElement element : confElements) {
			String typeId = element.getAttribute("typeId"); //$NON-NLS-1$

			if (_descriptors.containsKey(typeId)) {
				throw new IllegalArgumentException(
						"Only one graphical feedback factory for the type >>" + typeId //$NON-NLS-1$
								+ "<< should be registered."); //$NON-NLS-1$
			}

			if (typeId != null) {
				_descriptors.put(typeId,
						new GraphicalFeedbackContributionDescriptor(element));
			}
		}
	}

	/**
	 * This descriptor serves as a delegate to enable lazy loading of extension
	 * point implementations.
	 * 
	 * @author Sven Wende
	 */
	class GraphicalFeedbackContributionDescriptor {

		/**
		 * A configuration element.
		 */
		private final IConfigurationElement _configurationElement;

		/**
		 * The grapical feedback factory. (Gets instantiated lazily and cached)!
		 */
		private IGraphicalFeedbackFactory _graphicalFeedbackFactory;

		/**
		 * Constructor.
		 * 
		 * @param configurationElement
		 *            required
		 */
		public GraphicalFeedbackContributionDescriptor(
				final IConfigurationElement configurationElement) {
			assert configurationElement != null;
			_configurationElement = configurationElement;
		}

		/**
		 * 
		 * @return a new edit part
		 */
		public IGraphicalFeedbackFactory getGraphicalFeedbackFactory() {
			if (_graphicalFeedbackFactory == null) {
				try {
					_graphicalFeedbackFactory = (IGraphicalFeedbackFactory) _configurationElement
							.createExecutableExtension("class"); //$NON-NLS-1$
				} catch (CoreException e) {
					CentralLogger.getInstance().error(this, e);
				}
			}
			return _graphicalFeedbackFactory;
		}

	}

}
