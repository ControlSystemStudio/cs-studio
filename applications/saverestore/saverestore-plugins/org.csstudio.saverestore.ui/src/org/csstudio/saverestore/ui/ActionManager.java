/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.saverestore.ui;

import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;

import org.csstudio.saverestore.DataProvider;
import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.data.SaveSet;
import org.csstudio.saverestore.data.SaveSetData;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;
import org.csstudio.ui.fx.util.FXMessageDialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

/**
 *
 * <code>ActionManager</code> executes various actions related to the editors, such as open snapshot or save set.
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
            .getDataProvider(descriptor.getSaveSet().getDataProviderId()).getProvider();
        try {
            return Optional.ofNullable(provider.getSnapshotContent(descriptor));
        } catch (DataProviderException e) {
            reportException(e, owner.getSite());
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
                    ((SnapshotViewerEditor) part).addSnapshot(s, true);
                } else {
                    owner.getSite().getPage().openEditor(new SnapshotEditorInput(s), SnapshotViewerEditor.ID);
                }
            }
        } catch (PartInitException e) {
            SaveRestoreService.LOGGER.log(Level.SEVERE, "Could not find or instantiate a new snapshot editor.", e);
        }
    }

    /**
     * Load the save set data and open it in the save set editor.
     *
     * @param set the save set to open for editing
     */
    public void editSaveSet(final SaveSet set) {
        if (set == null) {
            throw new IllegalArgumentException("Save set is not selected.");
        }
        final DataProvider provider = SaveRestoreService.getInstance()
            .getDataProvider(set.getDataProviderId()).getProvider();
        if (!provider.isSaveSetSavingSupported()) {
            return;
        }
        SaveRestoreService.getInstance().execute("Load save set data", () -> {
            try {
                SaveSetData data = provider.getSaveSetContent(set);
                owner.getSite().getShell().getDisplay().asyncExec(() -> {
                    try {
                        owner.getSite().getPage().openEditor(new SaveSetEditorInput(data), SaveSetEditor.ID);
                    } catch (PartInitException e) {
                        SaveRestoreService.LOGGER.log(Level.SEVERE,
                            "Could not find or instantiate a new save set editor.", e);
                    }
                });
            } catch (DataProviderException e) {
                reportException(e, owner.getSite());
            }
        });
    }

    /**
     * Load the save set data and open it in the snapshot viewer editor.
     *
     * @param set the save set to open
     */
    public void openSaveSet(final SaveSet set) {
        if (set == null) {
            throw new IllegalArgumentException("Save set is not selected.");
        }
        final DataProvider provider = SaveRestoreService.getInstance()
            .getDataProvider(set.getDataProviderId()).getProvider();
        SaveRestoreService.getInstance().execute("Open save set", () -> {
            try {
                SaveSetData data = provider.getSaveSetContent(set);
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
                reportException(e, owner.getSite());
            }
        });
    }

    /**
     * Report exception that happened during the selector action. The exception is logged and a message is displayed.
     * The call can be made by any thread. The thread will be blocked until the message dialog is closed.
     *
     * @param e the exception to report
     * @param shellProvider provides the shell to use for the message dialog parent (cannot be null)
     */
    public static void reportException(Exception e, IShellProvider shellProvider) {
        reportException(e, null, shellProvider);
    }

    /**
     * Report exception that happened during the selector action. The exception is logged and a message is displayed.
     * The call can be made by any thread. The thread will be blocked until the message dialog is closed.
     *
     * @param e the exception to report
     * @param additionalMessage the message which will be appended to the dialog message
     * @param shellProvider the shellProvider provides the shell to use for the message dialog parent (cannot be null)
     */
    public static void reportException(Exception e, String additionalMessage, IShellProvider shellProvider) {
        SaveRestoreService.LOGGER.log(Level.FINE, "Error accessing data storage", e);
        Display.getDefault().syncExec(() -> {
            StringBuilder sb = new StringBuilder();
            sb.append(e.getMessage());
            if (e.getCause() != null) {
                sb.append('\n').append(e.getCause().getMessage());
            }
            if (additionalMessage != null) {
                sb.append('\n').append('\n').append(additionalMessage);
            }
            FXMessageDialog.openError(shellProvider.getShell(), "Error accessing data storage", sb.toString());
        });
    }
}
