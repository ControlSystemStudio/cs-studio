package org.csstudio.dct.ui.editor.copyandpaste;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.commands.AddRecordCommand;
import org.csstudio.dct.model.commands.ChangeBeanPropertyCommand;
import org.csstudio.dct.model.commands.ChangeFieldValueCommand;
import org.csstudio.dct.model.internal.Record;
import org.csstudio.dct.model.internal.RecordFactory;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

public class RecordCopyAndPasteStrategy implements ICopyAndPasteStrategy {

    @Override
    public Command createPasteCommand(List<IElement> copiedElements, IProject project, List<IElement> selectedElements) {
        assert copiedElements != null;
        assert project != null;
        assert selectedElements != null;

        CompoundCommand cmd = new CompoundCommand();

        for (IElement c : selectedElements) {
            assert c instanceof IContainer;

            for (IElement e : copiedElements) {
                if (e instanceof IRecord) {
                    IRecord r = (IRecord) e;
                    // TODO (hrickens) [26.08.2011]: Code duplication! Same code on BaseCopyAndPasteStrategy.chainPrototype(IPrototype prototype2Copy, CompoundCommand commandChain, Map<UUID, IPrototype> alreadyCreatedPrototypes, IProject project, IFolder targetFolder)
                    IRecord nr = RecordFactory.createRecord(project, r.getType(), r.getName(), UUID.randomUUID());

                    cmd.add(new ChangeBeanPropertyCommand(nr, "epicsName", r.getEpicsName()));
                    cmd.add(new ChangeBeanPropertyCommand(nr, "disabled", r.getDisabled()));
                    cmd.add(new ChangeBeanPropertyCommand(nr, "name", r.getName()));

                    cmd.add(new AddRecordCommand((IContainer) c, nr));

                    for (String key : r.getFields().keySet()) {
                        cmd.add(new ChangeFieldValueCommand(nr, key, r.getField(key)));
                    }
                }
            }
        }

        return cmd;
    }

    @Override
    public List<Serializable> createCopyElements(List<IElement> selectedElements) {
        List<Serializable> copies = new ArrayList<Serializable>();

        // create a temporary display model
        for (IElement e : selectedElements) {
            assert e instanceof IRecord;
            IRecord r = (IRecord) e;

            Record rcopy = new Record(r.getName(), r.getType(), UUID.randomUUID());
            rcopy.setFields(new HashMap<String, String>(r.getFields()));
            rcopy.setDisabled(r.getDisabled());
            rcopy.setEpicsName(r.getEpicsName());
            rcopy.setName(r.getName());
            copies.add(rcopy);
        }

        return copies;
    }

    @Override
    public boolean canCopy(List<IElement> selectedElements) {
        boolean result = false;

        if (!selectedElements.isEmpty()) {
            result = true;
            for (IElement e : selectedElements) {
                result &= e instanceof IRecord;
            }
        }

        return result;
    }

    @Override
    public boolean canPaste(List<IElement> selectedElements) {
        boolean result = false;

        if (!selectedElements.isEmpty()) {
            result = true;
            for (IElement e : selectedElements) {
                result &= e instanceof IContainer;
            }
        }

        return result;
    }

    @Override
    public String getContentDescription() {
        return "Records";
    }
}
