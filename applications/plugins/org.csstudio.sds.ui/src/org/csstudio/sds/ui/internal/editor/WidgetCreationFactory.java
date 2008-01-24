package org.csstudio.sds.ui.internal.editor;

import java.util.Map;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetModelFactoryService;
import org.csstudio.sds.model.initializers.ModelInitializationService;
import org.csstudio.sds.preferences.PreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

/**
 * The CreationFactory for the DropDownListener.
 * 
 * @author Kai Meyer
 */
public final class WidgetCreationFactory implements CreationFactory {

	/**
	 * The type of the widget.
	 */
	private String _widgetType = null;

	/**
	 * Constructor.
	 */
	public WidgetCreationFactory() {
		this(null);
	}

	/**
	 * Constructor.
	 * 
	 * @param widgetType
	 *            The type of the widget
	 */
	public WidgetCreationFactory(final String widgetType) {
		_widgetType = widgetType;
	}

	/**
	 * Sets the type of the widget.
	 * 
	 * @param widgetType
	 *            The type of the widget (not null)
	 */
	public void setWidgetType(final String widgetType) {
		assert widgetType != null;
		_widgetType = widgetType;
	}

	/**
	 * Creates and returns a new WidgetModel and sets the initial aliases.   
	 * @param initialPv The PV used for the aliases
	 * @return The created Object
	 */
	public Object getNewObject(final IProcessVariableAddress initialPv) {
		AbstractWidgetModel model = WidgetModelFactoryService.getInstance()
				.getWidgetModelFactory(_widgetType).createWidgetModel();

		runInitializers(model);

		setupInitialAliases(model, initialPv);

		return model;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getNewObject() {
		return getNewObject(null);
	}

	/**
	 * Calls the chosen initializer.
	 * @param model The {@link AbstractWidgetModel} to initialize
	 */
	private void runInitializers(final AbstractWidgetModel model) {
		String selectedSchemaId = Platform.getPreferencesService().getString(
				SdsPlugin.PLUGIN_ID, PreferenceConstants.PROP_SCHEMA,
				null, null);

		ModelInitializationService.getInstance().initialize(model,
				selectedSchemaId);

	}

	/**
	 * Sets the initial aliases on the given {@link AbstractWidgetModel} with the given PV.
	 * @param model The model, which aliases should be initialized
	 * @param initialPv The {@link IProcessVariableAddress} to use for the aliases
	 * @return The created alias-map
	 */
	private Map<String, String> setupInitialAliases(final AbstractWidgetModel model, final IProcessVariableAddress initialPv) {
		Map<String, String> result = null;

		Map<String, String> aliases = model.getAliases();
		
		if(initialPv!=null) {
			if(aliases.isEmpty()) {
				aliases.put("--", initialPv.getFullName());
			} else {
				aliases.put(aliases.keySet().toArray()[0].toString(), initialPv.getFullName());
			}
		}
		if (!model.getAliases().isEmpty()) {
			// pop up the alias dialog
			AliasInitializationDialog dialog = new AliasInitializationDialog(
					Display.getCurrent().getActiveShell(), model.getAliases());

			if (dialog.open() == Window.OK) {
				Map<String, String> finalAliases = dialog.getAliasDescriptors();
				model.setAliases(finalAliases);
			}
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getObjectType() {
		return WidgetModelFactoryService.getInstance().getWidgetModelFactory(
				_widgetType).getWidgetModelType();
	}

}
