package org.csstudio.sds.ui.internal.editparts;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

/**
 * Access service for contributions to the org.csstudio.sds.editParts.
 * 
 * @author Sven Wende & Stefan Hofer
 * 
 */
public final class EditPartService {
	/**
	 * The singleton instance.
	 */
	private static EditPartService _instance = null;

	/**
	 * Descriptors for all contributions.
	 */
	private Map<String, ElementEditPartDescriptor> _descriptors;

	/**
	 * Private constructor.
	 */
	private EditPartService() {
		lookup();
	}

	/**
	 * Gets the singleton instance.
	 * 
	 * @return the singleton instance
	 */
	public static EditPartService getInstance() {
		if (_instance == null) {
			_instance = new EditPartService();
		}

		return _instance;
	}

	/**
	 * Determines, whether EditParts for model objects of the specified can be
	 * created via this service or not.
	 * 
	 * @param typeId
	 *            the type identification
	 * @return true, if such EditParts can be created or false, which probably
	 *         means, that no EditPart contribution was registered for this type
	 *         of model objects
	 */
	public boolean canCreateEditPart(final String typeId) {
		return _descriptors.containsKey(typeId);
	}

	/**
	 * Creates an EditPart of the specified type.
	 * 
	 * @param typeId
	 *            the type identification
	 * @return an EditPart object
	 */
	public AbstractGraphicalEditPart createEditPart(final String typeId) {
		assert canCreateEditPart(typeId) : "Precondition violated: hasEditPart(typeId)"; //$NON-NLS-1$
		ElementEditPartDescriptor descriptor = _descriptors.get(typeId);
		return descriptor.createEditPart();
	}

	/**
	 * Looks up all extensions from the extension registry and creates the
	 * corresponding descriptor objects.
	 */
	private void lookup() {
		_descriptors = new HashMap<String, ElementEditPartDescriptor>();

		IExtensionRegistry extReg = Platform.getExtensionRegistry();
		String id = SdsUiPlugin.EXTPOINT_WIDGET_EDITPARTS;
		IConfigurationElement[] confElements = extReg
				.getConfigurationElementsFor(id);

		for (IConfigurationElement element : confElements) {
			String typeId = element.getAttribute("typeId"); //$NON-NLS-1$

			if (_descriptors.containsKey(typeId)) {
				throw new IllegalArgumentException(
						"Only one edit part for the type >>" + typeId //$NON-NLS-1$
								+ "<< should be registered."); //$NON-NLS-1$
			}

			if (typeId != null) {
				_descriptors.put(typeId, new ElementEditPartDescriptor(element));
			}
		}
	}

	/**
	 * This descriptor for {@link AbstractWidgetEditPart}s serves as a delegate to
	 * enable lazy loading of extension point implementations.
	 * 
	 * @author Stefan Hofer
	 */
	class ElementEditPartDescriptor {
		/**
		 * The configuration element.
		 */
		private final IConfigurationElement _configurationElement;

		/**
		 * Constructor.
		 * 
		 * @param configurationElement
		 *            required
		 */
		public ElementEditPartDescriptor(final IConfigurationElement configurationElement) {
			assert configurationElement != null;
			_configurationElement = configurationElement;
		}

		/**
		 * Returns a new instance of the EditPart.
		 * 
		 * @return a new edit part
		 */
		public AbstractGraphicalEditPart createEditPart() {
			AbstractGraphicalEditPart editPart = null;
			try {
				editPart = (AbstractGraphicalEditPart) _configurationElement
						.createExecutableExtension("class"); //$NON-NLS-1$
			} catch (CoreException e) {
				CentralLogger.getInstance().error(this, e);
			}
			return editPart;
		}
	}

}
