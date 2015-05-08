package org.csstudio.sds.eventhandling;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.commands.HidePropertyCommand;
import org.csstudio.sds.model.commands.ShowPropertyCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * Convinience class for the implementation of {@link AbstractWidgetPropertyPostProcessor}s.
 * The command shows or hides the specified properties ({@link AbstractEnsureInvariantsCommand#getPropertyIds()}) depending
 * on the specified condition {@link AbstractEnsureInvariantsCommand#shouldHideProperties(AbstractWidgetModel, String)}.
 * @author Kai Meyer (C1 WPS)
 *
 * @param <M>
 */
public abstract class AbstractEnsureInvariantsCommand<M extends AbstractWidgetModel> extends Command {
    private String _propertyId;
    private CompoundCommand chain;
    private M _widget;

    public AbstractEnsureInvariantsCommand(M widget,
            String propertyId) {
        _widget = widget;
        _propertyId = propertyId;
    }

    @Override
    public void execute() {
        if (chain == null) {
            chain = new CompoundCommand();

            String[] propertyIds = getPropertyIds();

            if (shouldHideProperties(_widget, _propertyId)) {
                for (String propertyId : propertyIds) {
                    chain.add(new HidePropertyCommand(_widget, propertyId, _propertyId));
                }
            } else {
                for (String propertyId : propertyIds) {
                    chain.add(new ShowPropertyCommand(_widget, propertyId, _propertyId));
                }
            }
        }

        chain.execute();
    }

    @Override
    public void undo() {
        chain.undo();
    }

    protected abstract String[] getPropertyIds();
    protected abstract boolean shouldHideProperties(M widget, String propertyId);
}
