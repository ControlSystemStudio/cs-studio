package org.csstudio.sds.components.model.eventing;

import org.csstudio.sds.components.model.BargraphModel;
import org.csstudio.sds.eventhandling.AbstractWidgetPropertyPostProcessor;
import org.csstudio.sds.model.commands.SetPropertyCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * Post Processor that switches {@link BargraphModel#PROP_ORIENTATION} depending
 * on widget size.
 *
 * @author Sven Wende
 *
 */
public class BargraphSizePostProcessor extends AbstractWidgetPropertyPostProcessor<BargraphModel> {

    @Override
    protected Command doCreateCommand(BargraphModel widget) {
        assert widget != null : "widget != null";
        return new EnsureInvariantsCommand(widget);
    }

    private static final class EnsureInvariantsCommand extends Command {
        private final BargraphModel widget;
        private CompoundCommand chain;

        private EnsureInvariantsCommand(BargraphModel widget) {
            this.widget = widget;
        }

        @Override
        public void execute() {
            if (chain == null) {
                chain = new CompoundCommand();
                chain.add(new SetPropertyCommand(widget, BargraphModel.PROP_ORIENTATION, widget.getWidth()>=widget.getHeight()));
            }

            chain.execute();
        }

        @Override
        public void undo() {
            chain.undo();
        }
    }

}
