package org.csstudio.sds.components.model.eventing;

import org.csstudio.sds.components.model.ActionButtonModel;
import org.csstudio.sds.eventhandling.AbstractWidgetPropertyPostProcessor;
import org.csstudio.sds.model.ActionData;
import org.csstudio.sds.model.commands.HidePropertyCommand;
import org.csstudio.sds.model.commands.ShowPropertyCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

public class ActionButtonDataPostProcessor extends
		AbstractWidgetPropertyPostProcessor<ActionButtonModel> {

	@Override
	protected Command doCreateCommand(ActionButtonModel widget) {
		assert widget != null : "widget != null";
		return new EnsureInvariantsCommand(widget, ActionButtonModel.PROP_ACTIONDATA);
	}

	private static final class EnsureInvariantsCommand extends Command {
		private ActionButtonModel widget;
		private String propertyId;
		private CompoundCommand chain;

		private EnsureInvariantsCommand(ActionButtonModel widget,
				String propertyId) {
			this.widget = widget;
			this.propertyId = propertyId;
		}

		@Override
		public void execute() {
			if (chain == null) {
				chain = new CompoundCommand();

				ActionData data = widget.getActionDataProperty(propertyId);
				int size = data.getWidgetActions().size();

				String[] propertyIds = new String[] {
						ActionButtonModel.PROP_ACTION_PRESSED_INDEX,
						ActionButtonModel.PROP_ACTION_RELEASED_INDEX };

				if (size == 0) {
					for (String propertyId : propertyIds) {
						chain.add(new HidePropertyCommand(widget, propertyId, propertyId));
					}
				} else {
					for (String propertyId : propertyIds) {
						chain.add(new ShowPropertyCommand(widget, propertyId, propertyId));
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
