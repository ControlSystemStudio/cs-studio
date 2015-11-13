package org.csstudio.saverestore.ui.browser.logic;

import java.util.logging.Level;

import org.csstudio.saverestore.BaseLevel;
import org.csstudio.saverestore.BeamlineSet;
import org.csstudio.saverestore.BeamlineSetData;
import org.csstudio.saverestore.DataProvider;
import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.Engine;
import org.csstudio.saverestore.Snapshot;
import org.csstudio.saverestore.VSnapshot;
import org.csstudio.saverestore.ui.BeamlineSetEditor;
import org.csstudio.saverestore.ui.BeamlineSetEditorInput;
import org.csstudio.saverestore.ui.SnapshotEditorInput;
import org.csstudio.saverestore.ui.SnapshotViewerEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
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
    private final IViewPart owner;

    /**
     * Constructs a new manager.
     *
     * @param selector the selector that provides the data
     * @param owner the owner view
     */
    public ActionManager(Selector selector, IViewPart owner) {
        this.selector = selector;
        this.owner = owner;
    }

    public void tagSnapshot(final Snapshot snapshot, final String tagName, final String tagMessage) {
        if (snapshot == null) {
            throw new IllegalArgumentException("Snapshot is not selected.");
        } else if (tagName == null) {
            throw new IllegalArgumentException("Tag name not provided.");
        }
        final DataProvider provider = Engine.getInstance().getSelectedDataProvider().provider;
        Engine.getInstance().execute(() -> {
            provider.tagSnapshot(snapshot, tagName, tagMessage);
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
        final DataProvider provider = Engine.getInstance().getSelectedDataProvider().provider;
        Engine.getInstance().execute(() -> {
            try {
                final VSnapshot s = provider.getSnapshotContent(snapshot);

                owner.getViewSite().getShell().getDisplay().asyncExec(()->{
                    try {
                        if (newEditor) {
                            owner.getViewSite().getPage().openEditor(
                                    new SnapshotEditorInput(s), SnapshotViewerEditor.ID);
                        } else {
                            IEditorPart part = owner.getViewSite().getPage().getActiveEditor();
                            if (part instanceof SnapshotViewerEditor) {
                                ((SnapshotViewerEditor) part).addSnapshot(s);
                            }
                        }
                    } catch (PartInitException e) {
                        Engine.LOGGER.log(Level.SEVERE, "Could not find or instantiate a new snapshot editor.", e);
                    }
                });
            } catch (DataProviderException e) {
                e.printStackTrace();
                //TODO
            }
        });
    }

    /**
     * Load the beamline set data and open it in the beamline set editor.
     * @param set the beamline set to open for editing
     */
    public void editBeamlineSet(final BeamlineSet set) {
        if (set == null) {
            throw new IllegalArgumentException("Beamline set is not selected.");
        }
        final DataProvider provider = Engine.getInstance().getSelectedDataProvider().provider;
        Engine.getInstance().execute(() -> {
            try {
                BeamlineSetData data = provider.getBeamlineSetContent(set);
                owner.getViewSite().getShell().getDisplay().asyncExec(() -> {
                    try {
                        owner.getViewSite().getPage().openEditor(
                                new BeamlineSetEditorInput(data), BeamlineSetEditor.ID);
                    } catch (PartInitException e) {
                        Engine.LOGGER.log(Level.SEVERE, "Could not find or instantiate a new snapshot editor.", e);
                    }
                });
            } catch (DataProviderException e) {
                e.printStackTrace();
                //TODO
            }
        });
    }

    /**
     * Load the beamline set data and open it in the snapshot viewer editor.
     * @param set the beamline set to open
     */
    public void openBeamlineSet(final BeamlineSet set) {
        if (set == null) {
            throw new IllegalArgumentException("Beamline set is not selected.");
        }
        final DataProvider provider = Engine.getInstance().getSelectedDataProvider().provider;
        Engine.getInstance().execute(() -> {
            try {
                BeamlineSetData data = provider.getBeamlineSetContent(set);
                final VSnapshot s = VSnapshot.of(set, data.getPVList());
                owner.getViewSite().getShell().getDisplay().asyncExec(() -> {
                    try {
                        owner.getViewSite().getPage().openEditor(
                                new SnapshotEditorInput(s), SnapshotViewerEditor.ID);
                    } catch (PartInitException e) {
                        Engine.LOGGER.log(Level.SEVERE, "Could not find or instantiate a new snapshot editor.", e);
                    }
                });
            } catch (DataProviderException e) {
                e.printStackTrace();
                //TODO
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
        final DataProvider provider = Engine.getInstance().getSelectedDataProvider().provider;
        final String orgBranch = selector.selectedBranchProperty().get();
        Engine.getInstance().execute(() -> {
            try {
                provider.createNewBranch(orgBranch, branchName);
                selector.readBranches(branchName);
            } catch (DataProviderException e) {
                e.printStackTrace();
                //TODO
            }

        });
    }

    /**
     * Synchronise the local repository with the remote one.
     */
    public void synchronise() {
        final BaseLevel isotope = selector.selectedBaseLevelProperty().get();
        final String branch = selector.selectedBranchProperty().get();
        final DataProvider provider = Engine.getInstance().getSelectedDataProvider().provider;
        Engine.getInstance().execute(() -> {
            try {
                provider.synchronise();
                if (isotope != null) {
                   selector.reloadBeamlineSets(isotope,branch);
                }
            } catch (DataProviderException e) {
                e.printStackTrace();
                //TODO
            }
        });
    }

}
