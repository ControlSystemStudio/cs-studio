package org.csstudio.dct.ui.editor.outline.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IFolderMember;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.commands.RemoveFolderCommand;
import org.csstudio.dct.model.commands.RemoveInstanceCommand;
import org.csstudio.dct.model.commands.RemovePrototypeCommand;
import org.csstudio.dct.model.commands.RemoveRecordCommand;
import org.csstudio.dct.model.visitors.SearchInstancesVisitor;
import org.csstudio.dct.util.CompareUtil;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

/**
 * Popup menu action for the outline view that removes elements from the model.
 *
 * @author Sven Wende
 *
 */
public final class RemoveAction extends AbstractOutlineAction {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command createCommand(List<IElement> selection) {
        assert selection != null;

        boolean delete = false;

        // .. delete confirmation
        if (CompareUtil.containsOnly(IFolder.class, selection)) {
            if (confirm("All subfolders and their content will be deleted. Continue?")) {
                delete = true;
            }
        } else if (CompareUtil.containsOnly(IPrototype.class, selection)) {
            if (confirm("All prototypes and derived instances will be deleted. Continue?")) {
                delete = true;
            }
        } else {
            delete = true;
        }

        // .. chain necessary commands for deletion
        CompoundCommand command = null;
        if (delete) {
            command = new CompoundCommand();
            command.add(new SelectInOutlineCommand(getOutlineView(), getProject()));

            if (CompareUtil.containsOnly(IFolder.class, selection)) {
                List<IFolder> folders = CompareUtil.convert(selection);
                chainDeleteFolders(command, folders);
            } else if (CompareUtil.containsOnly(IPrototype.class, selection)) {
                List<IPrototype> prototypes = CompareUtil.convert(selection);
                chainDeletePrototypes(command, prototypes);
            } else if (CompareUtil.containsOnly(IInstance.class, selection)) {
                List<IInstance> instances = CompareUtil.convert(selection);
                chainDeleteInstances(command, new HashSet<IInstance>(instances));
            } else if (CompareUtil.containsOnly(IRecord.class, selection)) {
                List<IRecord> records = CompareUtil.convert(selection);
                chainDeleteRecords(command, records);
            }
        }

        return command;
    }

    private boolean confirm(String message) {
        MessageDialog d = new MessageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Confirmation", null, message,
                MessageDialog.WARNING, new String[] { "Ok", "Cancel" }, 0);

        return d.open() == 0;
    }

    private void chainDeleteFolders(CompoundCommand cmd, List<IFolder> folders) {
        List<IFolder> folders2delete = new ArrayList<IFolder>();
        Set<IInstance> instances2delete = new HashSet<IInstance>();
        Set<IPrototype> prototypes2delete = new HashSet<IPrototype>();

        // .. recursively collect all elements that have to be removed
        for (IFolder f : folders) {
            collectElementsToDelete(f, folders2delete, prototypes2delete, instances2delete);
        }

        // .. chain instance deletion commands
        chainDeleteInstances(cmd, instances2delete);

        // .. chain prototype deletion commands
        for (IPrototype p : prototypes2delete) {
            cmd.add(new RemovePrototypeCommand(p));
        }

        // .. chain folder deletion commands
        for (IFolder f : folders2delete) {
            cmd.add(new RemoveFolderCommand(f));
        }
    }

    private void collectElementsToDelete(IFolder folder, List<IFolder> folders, Set<IPrototype> prototypes, Set<IInstance> instances) {
        for (IFolderMember m : folder.getMembers()) {
            if (m instanceof IFolder) {
                collectElementsToDelete((IFolder) m, folders, prototypes, instances);
            } else if (m instanceof IPrototype) {
                IPrototype prototype = (IPrototype) m;
                prototypes.add(prototype);

                // .. find dependent instances
                for (IContainer c : prototype.getDependentContainers()) {
                    if (c instanceof IInstance) {
                        instances.add((IInstance) c);
                    }
                }
            } else if (m instanceof IInstance) {
                instances.add((IInstance) m);
            }
        }

        // .. add the folder itself as last, to ensure the right deletion order
        folders.add(folder);

    }

    private void chainDeletePrototypes(CompoundCommand cmd, List<IPrototype> prototypes) {
        Set<IInstance> instances = new HashSet<IInstance>();

        for (IPrototype p : prototypes) {
            // .. search and delete instances
            List<IInstance> tmp = new SearchInstancesVisitor().search(getProject(), p.getId());
            instances.addAll(tmp);
        }

        chainDeleteInstances(cmd, instances);

        for (IPrototype p : prototypes) {
            // .. delete prototype
            cmd.add(new RemovePrototypeCommand(p));
        }
    }

    private void chainDeleteInstances(CompoundCommand cmd, Set<IInstance> instances) {
        List<IInstance> dependent = new ArrayList<IInstance>();

        for (IInstance i : instances) {
            dependent.addAll(recursivelyGetDependentInstances(i));
        }

        instances.removeAll(dependent);

        for (IInstance i : dependent) {
            cmd.add(new RemoveInstanceCommand(i));
        }

        for (IInstance i : instances) {
            cmd.add(new RemoveInstanceCommand(i));
        }

    }

    private List<IInstance> recursivelyGetDependentInstances(IInstance instance) {
        List<IInstance> result = new ArrayList<IInstance>();

        for (IContainer c : instance.getDependentContainers()) {
            assert c instanceof IInstance;
            IInstance di = (IInstance) c;
            List<IInstance> dependent = recursivelyGetDependentInstances(di);

            for(IInstance i : dependent) {
                if(result.contains(i)) {
                    throw new IllegalArgumentException("TODO");
                }
            }

            result.addAll(dependent);
            result.add(di);
        }

        return result;

    }

    private void chainDeleteRecords(CompoundCommand cmd, List<IRecord> records) {
        for (IRecord r : records) {
            cmd.add(new RemoveRecordCommand(r));
        }
    }

    @Override
    protected void afterSelectionChanged(List<IElement> selection, IAction action) {
        super.afterSelectionChanged(selection, action);

        // .. only elements of the same type can be deleted in a single step
        boolean onlyRecords = CompareUtil.containsOnly(IRecord.class, selection);

        boolean enabled = CompareUtil.containsOnly(IPrototype.class, selection) || CompareUtil.containsOnly(IInstance.class, selection)
                || CompareUtil.containsOnly(IFolder.class, selection) || onlyRecords;

        // .. only records that are not inherited can be deleted
        if (onlyRecords) {
            for (IElement e : selection) {
                IRecord r = (IRecord) e;
                enabled &= !r.isInherited();
            }
        }

        action.setEnabled(enabled);

    }
}
