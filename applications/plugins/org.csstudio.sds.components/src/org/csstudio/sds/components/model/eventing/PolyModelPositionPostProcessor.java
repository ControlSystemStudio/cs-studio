package org.csstudio.sds.components.model.eventing;

import org.csstudio.sds.components.model.AbstractPolyModel;
import org.csstudio.sds.eventhandling.AbstractWidgetPropertyPostProcessor;
import org.eclipse.gef.commands.Command;

public class PolyModelPositionPostProcessor extends
		AbstractWidgetPropertyPostProcessor<AbstractPolyModel> {

	@Override
	protected Command doCreateCommand(AbstractPolyModel widget) {
		assert widget != null : "widget != null";
		return new SetLocationCommand(widget);
	}
	
	private static class SetLocationCommand extends Command {

		private final AbstractPolyModel widget;

		public SetLocationCommand(AbstractPolyModel widget) {
			this.widget = widget;
		}
		
		@Override
		public void execute() {
			widget.setLocation(widget.getX(),widget.getY());
		}
		
	}

}
