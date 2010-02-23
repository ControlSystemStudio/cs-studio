package org.csstudio.sds.components.model.eventing;

import org.csstudio.sds.components.model.BargraphModel;
import org.csstudio.sds.eventhandling.AbstractWidgetPropertyPostProcessor;
import org.csstudio.sds.internal.model.ArrayOptionProperty;
import org.csstudio.sds.model.commands.HidePropertyCommand;
import org.csstudio.sds.model.commands.ShowPropertyCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

public class BargraphShowScalePostProcessor extends
		AbstractWidgetPropertyPostProcessor<BargraphModel, ArrayOptionProperty> {

	@Override
	protected Command doCreateCommand(BargraphModel widget,
			ArrayOptionProperty property) {
		assert widget != null : "widget != null";
		assert property != null : "property != null";
		return new EnsureInvariantsCommand(widget, property);
	}
	
	private static final class EnsureInvariantsCommand extends Command {
		private BargraphModel widget;
		private ArrayOptionProperty property;
		private CompoundCommand chain;

		private EnsureInvariantsCommand(BargraphModel widget,
				ArrayOptionProperty property) {
			this.widget = widget;
			this.property = property;
		}

		@Override
		public void execute() {
			if (chain == null) {
				chain = new CompoundCommand();

				Object selectedEntry = property.getPropertyValue();

				String[] propertyIds = new String[] {
						BargraphModel.PROP_SCALE_SECTION_COUNT };

				int index = (Integer) selectedEntry;
				String selectedString = property.getOptions()[index];
				if ("None".equals(selectedString)) {
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
