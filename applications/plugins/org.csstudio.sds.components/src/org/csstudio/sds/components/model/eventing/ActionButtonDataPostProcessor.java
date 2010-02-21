package org.csstudio.sds.components.model.eventing;

import org.csstudio.sds.components.model.ActionButtonModel;
import org.csstudio.sds.eventhandling.AbstractWidgetPropertyPostProcessor;
import org.csstudio.sds.internal.model.ActionDataProperty;
import org.csstudio.sds.model.ActionData;
import org.csstudio.sds.model.commands.HidePropertyCommand;
import org.csstudio.sds.model.commands.ShowPropertyCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

public class ActionButtonDataPostProcessor extends
		AbstractWidgetPropertyPostProcessor<ActionButtonModel, ActionDataProperty> {

	@Override
	protected Command doCreateCommand(ActionButtonModel widget,
			ActionDataProperty property) {
		assert widget != null : "widget != null";
		assert property != null : "property != null";
		return new EnsureInvariantsCommand(widget, property);
	}

	private static final class EnsureInvariantsCommand extends Command {
		private ActionButtonModel widget;
		private ActionDataProperty property;
		private CompoundCommand chain;

		private EnsureInvariantsCommand(ActionButtonModel widget,
				ActionDataProperty property) {
			this.widget = widget;
			this.property = property;
		}

		@Override
		public void execute() {
			if (chain == null) {
				chain = new CompoundCommand();

				ActionData data = property.getPropertyValue();
				int size = data.getWidgetActions().size();

				String[] propertyIds = new String[] {
						ActionButtonModel.PROP_ACTION_PRESSED_INDEX,
						ActionButtonModel.PROP_ACTION_RELEASED_INDEX };

				if (size == 0) {
					for (String propertyId : propertyIds) {
						chain.add(new HidePropertyCommand(widget, propertyId, property.getId()));
					}
				} else {
					for (String propertyId : propertyIds) {
						chain.add(new ShowPropertyCommand(widget, propertyId, property.getId()));
					}
				}
			}

			chain.execute();
		}

		@Override
		public void undo() {
			chain.undo();
		}
	}

}
