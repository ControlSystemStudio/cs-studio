package org.csstudio.sds.components.model.eventing;

import org.csstudio.sds.components.model.BargraphModel;
import org.csstudio.sds.eventhandling.AbstractWidgetPropertyPostProcessor;
import org.csstudio.sds.model.commands.HidePropertyCommand;
import org.csstudio.sds.model.commands.ShowPropertyCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * Post Processor that hides properties for
 * {@link BargraphModel#PROP_SCALE_SECTION_COUNT} and
 * {@link BargraphModel#PROP_SHOW_VALUES} depending on property
 * {@link BargraphModel#PROP_SHOW_SCALE}
 *
 * @author Sven Wende
 *
 */
public class BargraphShowScalePostProcessor extends AbstractWidgetPropertyPostProcessor<BargraphModel> {

    @Override
    protected Command doCreateCommand(BargraphModel widget) {
        assert widget != null : "widget != null";
        return new EnsureInvariantsCommand(widget, BargraphModel.PROP_SHOW_SCALE);
    }

    private static final class EnsureInvariantsCommand extends Command {
        private final BargraphModel widget;
        private final String propertyId;
        private CompoundCommand chain;

        private EnsureInvariantsCommand(BargraphModel widget, String propertyId) {
            this.widget = widget;
            this.propertyId = propertyId;
        }

        @Override
        public void execute() {
            if (chain == null) {
                chain = new CompoundCommand();

                int optionProperty = widget.getArrayOptionProperty(propertyId);

                String[] propertyIds = new String[] { BargraphModel.PROP_SCALE_SECTION_COUNT, BargraphModel.PROP_SHOW_VALUES };

                String selectedString = BargraphModel.SHOW_LABELS[optionProperty];
                if ("None".equals(selectedString)) {
                    for (String propertyId : propertyIds) {
                        chain.add(new HidePropertyCommand(widget, propertyId, this.propertyId));
                    }
                } else {
                    for (String propertyId : propertyIds) {
                        chain.add(new ShowPropertyCommand(widget, propertyId, this.propertyId));
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
