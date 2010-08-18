/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.ui.internal.editor;

import java.util.List;
import java.util.Map;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.eventhandling.EventType;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetModelFactoryService;
import org.csstudio.sds.model.commands.SetPropertyCommand;
import org.csstudio.sds.model.initializers.WidgetInitializationService;
import org.eclipse.gef.commands.CompoundCommand;
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
	private final KeyListenerAdapter _keyAdapter;

	/**
	 * Constructor.
	 *
	 * @param widgetType
	 *            The type of the widget
	 */
	public WidgetCreationFactory(final String widgetType) {
		this(widgetType, null);
	}

	public WidgetCreationFactory(final String widgetType,
			final KeyListenerAdapter keyAdapter) {
		_widgetType = widgetType;
		_keyAdapter = keyAdapter;
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
	 *
	 * @param initialPv
	 *            The PV used for the aliases
	 * @return The created Object
	 */
	public Object getNewObject(final IProcessVariableAddress initialPv) {
		AbstractWidgetModel model = WidgetModelFactoryService.getInstance()
				.getWidgetModel(_widgetType);

		// .. update model to ensure invariants that have been declared by {@link SdsPlugin#EXTPOINT_WIDGET_PROPERTY_POSTPROCESSORS}
		SdsPlugin.getDefault().getWidgetPropertyPostProcessingService().applyForAllProperties(model, EventType.ON_MANUAL_CHANGE);


		if (_keyAdapter != null) {
			List<Integer> pressedKeys = _keyAdapter.getPressedKeys();
			if ((pressedKeys.size() != 1) || (pressedKeys.get(0) != 'b')) {
				runInitializers(model);
				setupInitialAliases(model, initialPv);
			}
		} else {
			runInitializers(model);
			setupInitialAliases(model, initialPv);
		}


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
	 *
	 * @param model
	 *            The {@link AbstractWidgetModel} to initialize
	 */
	private void runInitializers(final AbstractWidgetModel model) {
		WidgetInitializationService.getInstance().initialize(model);
	}

	/**
	 * Sets the initial aliases on the given {@link AbstractWidgetModel} with
	 * the given PV.
	 *
	 * @param model
	 *            The model, which aliases should be initialized
	 * @param initialPv
	 *            The {@link IProcessVariableAddress} to use for the aliases
	 * @return The created alias-map
	 */
	private Map<String, String> setupInitialAliases(
			final AbstractWidgetModel model,
			final IProcessVariableAddress initialPv) {
		Map<String, String> result = null;

		Map<String, String> aliases = model.getAliases();

		String pvFullName = "";
		if (initialPv != null) {
		    pvFullName = initialPv.getFullName();
			if (aliases.isEmpty()) {
				aliases.put("--", initialPv.getFullName());
			} else {
				aliases.put(aliases.keySet().toArray()[0].toString(), initialPv
						.getFullName());
			}
		}

		InitializationDialog dialog = new InitializationDialog(Display
				.getCurrent().getActiveShell(), model.getTypeID(), pvFullName);

		if (dialog.open() == Window.OK) {
			String behaviorId = dialog.getBehaviorId();
			String pvName = dialog.getPvName();

			CompoundCommand mainCommand = new CompoundCommand();
			mainCommand.add(new SetPropertyCommand(model,
					AbstractWidgetModel.PROP_PRIMARY_PV, pvName));
			mainCommand.add(new SetPropertyCommand(model,
					AbstractWidgetModel.PROP_BEHAVIOR, behaviorId));
			if (!model.getAliases().isEmpty()) {
				Map<String, String> finalAliases = model.getAliases();
				String key = finalAliases.keySet().iterator().next();
				finalAliases.put(key, pvName);
				mainCommand.add(new SetPropertyCommand(model,
						AbstractWidgetModel.PROP_ALIASES, finalAliases));
			}
			mainCommand.execute();
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getObjectType() {
		return WidgetModelFactoryService.getInstance().getWidgetModelType(
				_widgetType);
	}

}
