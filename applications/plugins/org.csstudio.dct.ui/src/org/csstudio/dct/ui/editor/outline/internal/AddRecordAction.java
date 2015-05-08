package org.csstudio.dct.ui.editor.outline.internal;

import java.util.List;
import java.util.UUID;

import org.csstudio.dct.metamodel.IRecordDefinition;
import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IRecordContainer;
import org.csstudio.dct.model.commands.AddRecordCommand;
import org.csstudio.dct.model.internal.RecordFactory;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

/**
 * Popup menu action for the outline view that creates a new record.
 *
 * @author Sven Wende
 *
 */
public final class AddRecordAction extends AbstractOutlineAction {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command createCommand(List<IElement> selection) {
        assert selection != null;
        assert selection.size() == 1;
        assert selection.get(0) instanceof IRecordContainer;

        Command command = null;

        RecordDialog rsd = new RecordDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), getProject()
                .getDatabaseDefinition().getRecordDefinitions());

        if (rsd.open() == Window.OK) {
            IRecordDefinition rd = rsd.getSelection();

            if (rd != null) {
                command = new AddRecordCommand((IContainer) selection.get(0), RecordFactory.createRecord(getProject(), rd.getType(),
                        "new record", UUID.randomUUID()));
            }
        }

        return command;
    }

}
