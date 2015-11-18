package org.csstudio.saverestore.ui.browser;

import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;

import org.csstudio.saverestore.DataProvider;
import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.BeamlineSetData;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;
import org.csstudio.saverestore.ui.BeamlineSetEditor;
import org.csstudio.saverestore.ui.BeamlineSetEditorInput;
import org.csstudio.saverestore.ui.Selector;
import org.csstudio.saverestore.ui.SnapshotEditorInput;
import org.csstudio.saverestore.ui.SnapshotViewerEditor;
import org.csstudio.ui.fx.util.FXTextAreaInputDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

/**
 *
 * <code>ActionManager</code> executes the actions that a user can trigger through the browser UI.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ActionManager {

    private final Selector selector;
    private final IWorkbenchPart owner;

    /**
     * Constructs a new manager.
     *
     * @param selector the selector that provides the data
     * @param owner the owner view
     */
    public ActionManager(Selector selector, IWorkbenchPart owner) {
        this.selector = selector;
        this.owner = owner;
    }

    /**
     * Tag the snapshot with a specific tag name and tag message.
     *
     * @param snapshot the snapshot to tag
     * @param tagName the name of the tag
     * @param tagMessage the tag message
     */
    public void tagSnapshot(final Snapshot snapshot, final String tagName, final String tagMessage) {
        if (snapshot == null) {
            throw new IllegalArgumentException("Snapshot is not selected.");
        } else if (tagName == null) {
            throw new IllegalArgumentException("Tag name not provided.");
        }
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().provider;
        SaveRestoreService.getInstance().execute("Tag Snapshot", () -> {
            try {
                provider.tagSnapshot(snapshot, tagName, tagMessage);
            } catch (DataProviderException e) {
                Selector.reportException(e, owner.getSite().getShell());
            }
        });
    }

    /**
     * Load and open the snapshot in a new editor.
     *
     * @param snapshot the snapshot to open
     */
    public void openSnapshot(Snapshot snapshot) {
        openSnapshotInternal(snapshot, true);
    }

    /**
     * Load the snapshot data and add it to the currently active snapshot viewer editor.
     *
     * @param snapshot the snapshot to open in comparison viewer
     */
    public void compareSnapshot(Snapshot snapshot) {
        openSnapshotInternal(snapshot, false);
    }

    private void openSnapshotInternal(final Snapshot snapshot, final boolean newEditor) {
        if (snapshot == null) {
            throw new IllegalArgumentException("Snapshot is not selected");
        }
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().provider;
        SaveRestoreService.getInstance().execute("Load snapshot data", () -> {
            try {
                final VSnapshot s = provider.getSnapshotContent(snapshot);

                owner.getSite().getShell().getDisplay().asyncExec(() -> {
                    try {
                        if (newEditor) {
                            owner.getSite().getPage().openEditor(new SnapshotEditorInput(s), SnapshotViewerEditor.ID);
                        } else {
                            IEditorPart part = owner.getSite().getPage().getActiveEditor();
                            if (!(part instanceof SnapshotViewerEditor)) {
                                IEditorReference[] parts = owner.getSite().getPage().getEditorReferences();
                                for (IEditorReference e : parts) {
                                    if (SnapshotViewerEditor.ID.equals(e.getId())) {
                                        part = e.getEditor(true);
                                        break;
                                    }
                                }
                            }
                            if (part instanceof SnapshotViewerEditor) {
                                ((SnapshotViewerEditor) part).addSnapshot(s);
                            } else {
                                owner.getSite().getPage().openEditor(new SnapshotEditorInput(s),
                                        SnapshotViewerEditor.ID);
                            }
                        }
                    } catch (PartInitException e) {
                        SaveRestoreService.LOGGER.log(Level.SEVERE,
                                "Could not find or instantiate a new snapshot editor.", e);
                    }
                });
            } catch (DataProviderException e) {
                Selector.reportException(e, owner.getSite().getShell());
            }
        });
    }

    /**
     * Load the beamline set data and open it in the beamline set editor.
     *
     * @param set the beamline set to open for editing
     */
    public void editBeamlineSet(final BeamlineSet set) {
        if (set == null) {
            throw new IllegalArgumentException("Beamline set is not selected.");
        }
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().provider;
        SaveRestoreService.getInstance().execute("Load beamline set data", () -> {
            try {
                BeamlineSetData data = provider.getBeamlineSetContent(set);
                owner.getSite().getShell().getDisplay().asyncExec(() -> {
                    try {
                        owner.getSite().getPage().openEditor(new BeamlineSetEditorInput(data), BeamlineSetEditor.ID);
                    } catch (PartInitException e) {
                        SaveRestoreService.LOGGER.log(Level.SEVERE,
                                "Could not find or instantiate a new beamline set editor.", e);
                    }
                });
            } catch (DataProviderException e) {
                Selector.reportException(e, owner.getSite().getShell());
            }
        });
    }

    /**
     * Opens an empty beamline set editor.
     */
    public void newBeamlineSet() {
        final Branch branch = selector.selectedBranchProperty().get();
        final BaseLevel base = selector.selectedBaseLevelProperty().get();
        SaveRestoreService.getInstance().execute("Load beamline set data", () -> {
            BeamlineSet set = new BeamlineSet(branch, Optional.ofNullable(base), new String[] { "BeamlineSet" });
            BeamlineSetData data = new BeamlineSetData(set, new ArrayList<>(0), "");
            owner.getSite().getShell().getDisplay().asyncExec(() -> {
                try {
                    owner.getSite().getPage().openEditor(new BeamlineSetEditorInput(data), BeamlineSetEditor.ID);
                } catch (PartInitException e) {
                    SaveRestoreService.LOGGER.log(Level.SEVERE,
                            "Could not find or instantiate a new beamline set editor.", e);
                }
            });
        });
    }

    /**
     * Create an editor for a new beamline set.
     */
    public void newBeamlineSet(final String[] suggestedPath) {
        final Branch branch = selector.selectedBranchProperty().get();
        final BaseLevel level = selector.selectedBaseLevelProperty().get();
        SaveRestoreService.getInstance().execute("Open beamline set", () -> {
            BeamlineSet set = new BeamlineSet(branch, Optional.ofNullable(level), suggestedPath);
            BeamlineSetData data = new BeamlineSetData(set, new ArrayList<>(), "");
            owner.getSite().getShell().getDisplay().asyncExec(() -> {
                try {
                    owner.getSite().getPage().openEditor(new BeamlineSetEditorInput(data), BeamlineSetEditor.ID);
                } catch (PartInitException e) {
                    SaveRestoreService.LOGGER.log(Level.SEVERE,
                            "Could not find or instantiate a new beamline set editor.", e);
                }
            });
        });
    }

    /**
     * Load the beamline set data and open it in the snapshot viewer editor.
     *
     * @param set the beamline set to open
     */
    public void openBeamlineSet(final BeamlineSet set) {
        if (set == null) {
            throw new IllegalArgumentException("Beamline set is not selected.");
        }
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().provider;
        SaveRestoreService.getInstance().execute("Open beamline set", () -> {
            try {
                BeamlineSetData data = provider.getBeamlineSetContent(set);
                final VSnapshot s = new VSnapshot(set, data.getPVList());
                owner.getSite().getShell().getDisplay().asyncExec(() -> {
                    try {
                        owner.getSite().getPage().openEditor(new SnapshotEditorInput(s), SnapshotViewerEditor.ID);
                    } catch (PartInitException e) {
                        SaveRestoreService.LOGGER.log(Level.SEVERE,
                                "Could not find or instantiate a new snapshot editor.", e);
                    }
                });
            } catch (DataProviderException e) {
                Selector.reportException(e, owner.getSite().getShell());
            }
        });
    }

    /**
     * Delete the selected beamline set.
     *
     * @param set the set to delete
     */
    public void deleteBeamlineSet(final BeamlineSet set) {
        if (set == null) {
            throw new IllegalArgumentException("Beamline set is not selected.");
        }
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().provider;
        SaveRestoreService.getInstance().execute("Delete beamline set", () -> {
            try {
                Optional<String> comment = FXTextAreaInputDialog.get(owner.getSite().getShell(), "Delete Comment",
                        "Provide a short comment why the set '" + set.getPathAsString() + "' is being deleted", "",
                        e -> (e == null || e.trim().length() < 10) ?
                                "Comment should be at least 10 characters long." : null);
                if (comment.isPresent()) {
                    provider.deleteBeamlineSet(set,comment.get());
                }
            } catch (DataProviderException e) {
                Selector.reportException(e, owner.getSite().getShell());
            }
        });
    }

    /**
     * Create a new branch.
     *
     * @param branchName the name of the branch to create
     */
    public void createNewBranch(final String branchName) {
        if (branchName == null || branchName.trim().isEmpty()) {
            throw new IllegalArgumentException("Branch name cannot be null or empty.");
        }
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().provider;
        if (!provider.areBranchesSupported()) {
            return;
        }
        final Branch orgBranch = selector.selectedBranchProperty().get();
        SaveRestoreService.getInstance().execute("Create new branch", () -> {
            try {
                provider.createNewBranch(orgBranch, branchName);
            } catch (DataProviderException e) {
                Selector.reportException(e, owner.getSite().getShell());
            }

        });
    }

    /**
     * Synchronise the local repository with the remote one.
     */
    public void synchronise() {
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().provider;
        SaveRestoreService.getInstance().execute("Synchronise repository", () -> {
            try {
                provider.synchronise();
            } catch (DataProviderException e) {
                Selector.reportException(e, owner.getSite().getShell());
            }
        });
    }

    public void resetRepository() {
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().provider;
        if (!provider.isReinitSupported()) {
            return;
        }
        SaveRestoreService.getInstance().execute("Reset repository", () -> {
            try {
                provider.reinitialise();
            } catch (DataProviderException e) {
                Selector.reportException(e, owner.getSite().getShell());
            }
        });
    }

}
