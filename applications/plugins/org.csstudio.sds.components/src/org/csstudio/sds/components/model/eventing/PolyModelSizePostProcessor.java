package org.csstudio.sds.components.model.eventing;

import org.csstudio.sds.components.model.AbstractPolyModel;
import org.csstudio.sds.eventhandling.AbstractWidgetPropertyPostProcessor;
import org.eclipse.gef.commands.Command;

public class PolyModelSizePostProcessor extends
		AbstractWidgetPropertyPostProcessor<AbstractPolyModel> {

	@Override
	protected Command doCreateCommand(AbstractPolyModel widget) {
		assert widget != null : "widget != null";
		return new SetSizeCommand(widget);
	}
	
	private static class SetSizeCommand extends Command {

		private final AbstractPolyModel widget;

		public SetSizeCommand(AbstractPolyModel widget) {
			this.widget = widget;
		}
		
		@Override
		public void execute() {
			widget.setSize(widget.getWidth(),widget.getHeight());
		}
		
	}

}
