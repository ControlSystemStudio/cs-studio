package org.csstudio.sds.ui.internal.editor;

import java.util.List;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.eventhandling.EventType;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetModelFactoryService;
import org.csstudio.sds.model.initializers.WidgetInitializationService;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

public class WidgetCreationUtil {

	/**
	 * Opens a dialog that allows for pre-configuring a new widget.
	 * 
	 * @param widgetType
	 *            optional - if null, the dialog will offer a widget type
	 *            selection
	 * @param processVariables
	 *            process variables, may be null
	 * @return a preconfigured widget model
	 */
	public static AbstractWidgetModel createAndPreconfigureWidget(String widgetType, List<IProcessVariableAddress> processVariables) {
		final WidgetCreationDialog dialog = new WidgetCreationDialog(new Shell(), processVariables, widgetType);

		if (dialog.open() == Window.OK) {
			String selectedWidgetType = widgetType != null ? widgetType : dialog.getSelectedWidgetType();

			if (selectedWidgetType != null) {
				AbstractWidgetModel widgetModel = createWidgetHeadlessly(selectedWidgetType, true);

				String processVariableName = dialog.getSelectedProcessVariable();
				if (processVariableName != null) {
					widgetModel.setAliasValue("channel", processVariableName);
					widgetModel.setPrimarPv("$channel$");
				}

				widgetModel.setBehavior(dialog.getSelectedBehaviourId());
				return widgetModel;
			}
		}

		return null;
	}

	/**
	 * Creates a preconfigured widget, without popping up a configuration
	 * dialog.
	 * 
	 * @param widgetType
	 *            the widget type, mandatory
	 * @param runInitializers
	 *            if true, widget initializers will be run
	 * 
	 * @return the preconfigured widget
	 */
	public static AbstractWidgetModel createWidgetHeadlessly(String widgetType, boolean runInitializers) {
		AbstractWidgetModel widgetModel = WidgetModelFactoryService.getInstance().getWidgetModel(widgetType);
		SdsPlugin.getDefault().getWidgetPropertyPostProcessingService().applyForAllProperties(widgetModel, EventType.ON_MANUAL_CHANGE);

		if (runInitializers) {
			WidgetInitializationService.getInstance().initialize(widgetModel);
		}
		return widgetModel;
	}

}
