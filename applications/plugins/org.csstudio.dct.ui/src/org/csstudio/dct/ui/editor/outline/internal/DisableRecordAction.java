package org.csstudio.dct.ui.editor.outline.internal;

import java.util.List;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.commands.ChangeBeanPropertyCommand;
import org.eclipse.gef.commands.Command;

/**
 * Popup menu action for the outline view that disabled a record.
 *
 * @author Sven Wende
 *
 */
public final class DisableRecordAction extends AbstractOutlineAction {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command createCommand(List<IElement> selection) {
        assert selection != null;
        assert selection.size() == 1;
        assert selection.get(0) instanceof IRecord;

        IRecord record = (IRecord) selection.get(0);
        Command command = new ChangeBeanPropertyCommand(record, "disabled", true);

        return command;
    }

}
