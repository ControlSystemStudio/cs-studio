package org.csstudio.saverestore.ui;

import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;

import org.csstudio.saverestore.DataProvider;
import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.BeamlineSetData;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;
import org.csstudio.ui.fx.util.FXMessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

/**
 *
 * <code>ActionManager</code> executes various actions related to the editors, such as open snapshot or beamline set.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ActionManager {

    protected final IWorkbenchPart owner;

    /**
     * Constructs a new manager.
     *
     * @param selector the selector that provides the data
     * @param owner the owner view
     */
    public ActionManager(IWorkbenchPart owner) {
        this.owner = owner;
    }

    /**
     * Load and open the snapshot in a new editor.
     *
     * @param snapshot the snapshot to open
     */
    public void openSnapshot(Snapshot snapshot) {
        openSnapshotInternal(snapshot, true, this::loadSnapshot);
    }

    /**
     * Load the snapshot data and add it to the currently active snapshot viewer editor.
     *
     * @param snapshot the snapshot to open in comparison viewer
     */
    public void compareSnapshot(Snapshot snapshot) {
        openSnapshotInternal(snapshot, false, this::loadSnapshot);
    }

    /**
     * Opens the given snapshot in a separate editor. No data loading is performed, it is assumed that the given
     * snapshot already contains everything.
     *
     * @param snapshot the snapshot to open
     */
    public void openSnapshot(final VSnapshot snapshot) {
        openSnapshotInternal(snapshot.getSnapshot().orElse(null), true, e -> Optional.of(snapshot));
    }

    private Optional<VSnapshot> loadSnapshot(final Snapshot descriptor) {
        if (descriptor == null) {
            throw new IllegalArgumentException("Snapshot not provided.");
        }
        final DataProvider provider = SaveRestoreService.getInstance()
            .getDataProvider(descriptor.getBeamlineSet().getDataProviderId()).getProvider();
        try {
            return Optional.ofNullable(provider.getSnapshotContent(descriptor));
        } catch (DataProviderException e) {
            reportException(e, owner.getSite().getShell());
        }
        return Optional.empty();
    }

    private void openSnapshotInternal(final Snapshot snapshot, final boolean newEditor,
        final Function<Snapshot, Optional<VSnapshot>> snapshotProvider) {
        if (snapshot == null) {
            throw new IllegalArgumentException("Snapshot not provided.");
        }
        SaveRestoreService.getInstance().execute("Load snapshot data", () -> snapshotProvider.apply(snapshot)
            .ifPresent(s -> owner.getSite().getShell().getDisplay().asyncExec(() -> openSnapshotEditor(s, newEditor))));
    }

    private void openSnapshotEditor(VSnapshot s, boolean newEditor) {
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
                    owner.getSite().getPage().openEditor(new SnapshotEditorInput(s), SnapshotViewerEditor.ID);
                }
            }
        } catch (PartInitException e) {
            SaveRestoreService.LOGGER.log(Level.SEVERE, "Could not find or instantiate a new snapshot editor.", e);
        }
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
        final DataProvider provider = SaveRestoreService.getInstance()
            .getDataProvider(set.getDataProviderId()).getProvider();
        if (!provider.isBeamlineSetSavingSupported()) {
            return;
        }
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
                reportException(e, owner.getSite().getShell());
            }
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
        final DataProvider provider = SaveRestoreService.getInstance()
            .getDataProvider(set.getDataProviderId()).getProvider();
        SaveRestoreService.getInstance().execute("Open beamline set", () -> {
            try {
                BeamlineSetData data = provider.getBeamlineSetContent(set);
                final VSnapshot s = new VSnapshot(set, data.getPVList(), data.getReadbackList(), data.getDeltaList());
                owner.getSite().getShell().getDisplay().asyncExec(() -> {
                    try {
                        owner.getSite().getPage().openEditor(new SnapshotEditorInput(s), SnapshotViewerEditor.ID);
                    } catch (PartInitException e) {
                        SaveRestoreService.LOGGER.log(Level.SEVERE,
                            "Could not find or instantiate a new snapshot editor.", e);
                    }
                });
            } catch (DataProviderException e) {
                reportException(e, owner.getSite().getShell());
            }
        });
    }

    /**
     * Report exception that happened during the selector action. The exception is logged and a message is displayed.
     * The call can be made by any thread. The thread will be blocked until the message dialog is closed.
     *
     * @param e the exception to report
     * @param shell the shell to use for the message dialog parent (cannot be null)
     */
    public static void reportException(Exception e, Shell shell) {
        reportException(e, null, shell);
    }

    /**
     * Report exception that happened during the selector action. The exception is logged and a message is displayed.
     * The call can be made by any thread. The thread will be blocked until the message dialog is closed.
     *
     * @param e the exception to report
     * @param additionalMessage the message which will be appended to the dialog message
     * @param shell the shell to use for the message dialog parent (cannot be null)
     */
    public static void reportException(Exception e, String additionalMessage, Shell shell) {
        SaveRestoreService.LOGGER.log(Level.FINE, "Error accessing data storage", e);
        shell.getDisplay().syncExec(() -> {
            StringBuilder sb = new StringBuilder();
            sb.append(e.getMessage());
            if (e.getCause() != null) {
                sb.append('\n').append(e.getCause().getMessage());
            }
            if (additionalMessage != null) {
                sb.append('\n').append('\n').append(additionalMessage);
            }
            FXMessageDialog.openError(shell, "Error accessing data storage", sb.toString());
        });
    }
}
