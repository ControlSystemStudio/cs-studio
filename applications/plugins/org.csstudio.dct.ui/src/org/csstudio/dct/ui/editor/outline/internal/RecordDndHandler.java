package org.csstudio.dct.ui.editor.outline.internal;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IRecordContainer;
import org.csstudio.dct.model.commands.AddRecordCommand;
import org.csstudio.dct.model.commands.RemoveRecordCommand;
import org.csstudio.dct.model.internal.Record;
import org.csstudio.dct.model.internal.RecordFactory;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;

public class RecordDndHandler extends AbstractDnDHandler<IRecord> {

    @Override
    protected Command doCreateCopyCommand(IRecord dndSource, IElement dndTarget) {
        assert dndTarget instanceof IContainer;

        CompoundCommand cmd = new CompoundCommand();

        // .. determine target container
        IContainer container = (IContainer) dndTarget;

        // .. clone record
        Record clone = RecordFactory.cloneRecord(dndSource.getContainer().getProject(), (IRecord) dndSource);

        // .. create command
        cmd.add(new AddRecordCommand(container, clone));

        return cmd;
    }

    @Override
    protected Command doCreateMoveCommand(IRecord dndSource, IElement dndTarget) {
        CompoundCommand cmd = new CompoundCommand();

        if (dndTarget instanceof IContainer) {
            IRecord record = (IRecord) dndSource;
            cmd.add(new RemoveRecordCommand(record));
            cmd.add(new AddRecordCommand((IContainer) dndTarget, record));
        } else if (dndTarget instanceof IRecord) {
            IRecord targetRecord = (IRecord) dndTarget;
            IRecord record = (IRecord) dndSource;
            cmd.add(new RemoveRecordCommand(record));

            int index = targetRecord.getContainer().getRecords().indexOf(targetRecord);
            int tmp = targetRecord.getContainer().getRecords().indexOf(record);
            if(tmp>-1 && tmp<index) {
                index--;
            }

            cmd.add(new AddRecordCommand(targetRecord.getContainer(), record, index));
        }

        return cmd;
    }

    @Override
    public int updateDragFeedback(IRecord dndSource, IElement dndTarget, DropTargetEvent event) {
        if (event.detail == DND.DROP_COPY) {
            if (dndTarget instanceof IRecordContainer) {
                event.feedback = DND.FEEDBACK_SELECT;
            } else {
                event.feedback = DND.FEEDBACK_NONE;
            }
        } else if (event.detail == DND.DROP_MOVE) {
            if (dndTarget instanceof IRecord) {
                event.feedback = DND.FEEDBACK_INSERT_BEFORE;
            } else if (dndTarget instanceof IRecordContainer) {
                event.feedback = DND.FEEDBACK_SELECT;
            } else {
                event.feedback = DND.FEEDBACK_NONE;
            }
        } else {
            event.feedback = DND.FEEDBACK_NONE;
        }

        return 0;
    }

    @Override
    public boolean supports(IRecord dndSource) {
        return !dndSource.isInherited();
    }

}
