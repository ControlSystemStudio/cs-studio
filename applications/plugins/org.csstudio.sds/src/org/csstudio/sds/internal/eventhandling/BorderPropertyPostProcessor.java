package org.csstudio.sds.internal.eventhandling;

import org.csstudio.sds.eventhandling.AbstractWidgetPropertyPostProcessor;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.BorderStyleEnum;
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
public class BorderPropertyPostProcessor extends AbstractWidgetPropertyPostProcessor<AbstractWidgetModel> {

    /**
     *{@inheritDoc}
     */
    @Override
    protected Command doCreateCommand(AbstractWidgetModel widget) {
        return new HideBorderStuffCommand(widget);
    }

    private static final class HideBorderStuffCommand extends Command {
        private final AbstractWidgetModel widget;
        private CompoundCommand chain;

        private HideBorderStuffCommand(AbstractWidgetModel widget) {
            this.widget = widget;
        }

        @Override
        public void execute() {
            if (chain == null) {
                chain = new CompoundCommand();

                // .. determine the selected border style
                int optionIndex = widget.getArrayOptionProperty(AbstractWidgetModel.PROP_BORDER_STYLE);

                if (BorderStyleEnum.NONE.getIndex() == optionIndex ||
                        BorderStyleEnum.RAISED.getIndex() == optionIndex ||
                        BorderStyleEnum.LOWERED.getIndex() == optionIndex) {
                    // .. hide color and width properties, when no border style
                    // is set
                    chain.add(new HidePropertyCommand(widget, AbstractWidgetModel.PROP_BORDER_WIDTH, AbstractWidgetModel.PROP_BORDER_STYLE));
                    chain.add(new HidePropertyCommand(widget, AbstractWidgetModel.PROP_BORDER_COLOR, AbstractWidgetModel.PROP_BORDER_STYLE));
                } else {
                    // .. show color and width properties, when a border style
                    // is set
                    chain.add(new ShowPropertyCommand(widget, AbstractWidgetModel.PROP_BORDER_WIDTH, AbstractWidgetModel.PROP_BORDER_STYLE));
                    chain.add(new ShowPropertyCommand(widget, AbstractWidgetModel.PROP_BORDER_COLOR, AbstractWidgetModel.PROP_BORDER_STYLE));
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
