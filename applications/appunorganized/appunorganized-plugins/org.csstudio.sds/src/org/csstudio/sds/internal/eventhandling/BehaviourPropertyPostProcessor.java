package org.csstudio.sds.internal.eventhandling;

import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.eventhandling.AbstractWidgetPropertyPostProcessor;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.BorderStyleEnum;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.commands.HidePropertyCommand;
import org.csstudio.sds.model.commands.ShowPropertyCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * Hides {@link AbstractWidgetModel#PROP_BORDER_COLOR} and
 * {@link AbstractWidgetModel#PROP_BORDER_WIDTH} when
 * {@link AbstractWidgetModel#PROP_BORDER_STYLE} is set to
 * {@link BorderStyleEnum#NONE}.
 *
 * @author Sven Wende
 *
 */
public class BehaviourPropertyPostProcessor extends AbstractWidgetPropertyPostProcessor<AbstractWidgetModel> {

    /**
     *{@inheritDoc}
     */
    @Override
    protected Command doCreateCommand(AbstractWidgetModel widget) {
        assert widget != null : "widget != null";
        return new EnsureInvariantsCommand(widget);
    }

    private static final class EnsureInvariantsCommand extends Command {
        private AbstractWidgetModel widget;
        private CompoundCommand chain;

        private EnsureInvariantsCommand(AbstractWidgetModel widget) {
            this.widget = widget;
        }

        @Override
        public void execute() {
            if (chain == null) {
                chain = new CompoundCommand();

                // .. determine the selected behavior
                String behaviorId = widget.getBehaviorProperty(AbstractWidgetModel.PROP_BEHAVIOR);

                IBehaviorService behaviourService = SdsPlugin.getDefault()
                        .getBehaviourService();
                String[] invisiblePropertyIds = behaviourService
                        .getInvisiblePropertyIds(behaviorId, widget.getTypeID());

                for (WidgetProperty property : widget.getProperties()) {
                    chain.add(new ShowPropertyCommand(widget, property.getId(), AbstractWidgetModel.PROP_BEHAVIOR));
                }
                for (String propertyId : invisiblePropertyIds) {
                    chain.add(new HidePropertyCommand(widget, propertyId, AbstractWidgetModel.PROP_BEHAVIOR));
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
