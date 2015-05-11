package org.csstudio.sds.components.model.eventing;

import org.csstudio.sds.components.model.SimpleSliderModel;
import org.csstudio.sds.eventhandling.AbstractWidgetPropertyPostProcessor;
import org.csstudio.sds.model.commands.SetPropertyCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * Post Processor that switches {@link SimpleSliderModel#PROP_ORIENTATION} depending
 * on widget size.
 *
 * @author Sven Wende
 *
 */
public class SimpleSliderSizePostProcessor extends AbstractWidgetPropertyPostProcessor<SimpleSliderModel> {

    @Override
    protected Command doCreateCommand(SimpleSliderModel widget) {
        assert widget != null : "widget != null";
        return new EnsureInvariantsCommand(widget);
    }

    private static final class EnsureInvariantsCommand extends Command {
        private final SimpleSliderModel widget;
        private CompoundCommand chain;

        private EnsureInvariantsCommand(SimpleSliderModel widget) {
            this.widget = widget;
        }

        @Override
        public void execute() {
            if (chain == null) {
                chain = new CompoundCommand();
                chain.add(new SetPropertyCommand(widget, SimpleSliderModel.PROP_ORIENTATION, widget.getWidth()>=widget.getHeight()));
            }

            chain.execute();
        }

        @Override
        public void undo() {
            chain.undo();
        }
    }

}
