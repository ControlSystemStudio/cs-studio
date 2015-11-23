package org.csstudio.saverestore.ui;

import static org.diirt.datasource.ExpressionLanguage.channel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.csstudio.saverestore.DataProvider;
import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.DataProviderWrapper;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.Utilities;
import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VNoData;
import org.csstudio.saverestore.data.VSnapshot;
import org.csstudio.saverestore.ui.util.GUIUpdateThrottle;
import org.csstudio.ui.fx.util.FXTextAreaInputDialog;
import org.diirt.datasource.PVManager;
import org.diirt.datasource.PVReader;
import org.diirt.datasource.PVReaderEvent;
import org.diirt.datasource.PVReaderListener;
import org.diirt.datasource.PVWriter;
import org.diirt.util.time.TimeDuration;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.VType;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * <code>SnapshotViewerController</code> is the controller for the snapshot viewer editor. It provides the logic for
 * adding and removing snapshots, as well as for taking, saving and restoring the snapshots.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SnapshotViewerController {

    /** The rate at which the table is updated */
    public static final long TABLE_UPDATE_RATE = 500;

    private class PV {
        final PVReader<VType> reader;
        final PVWriter<Object> writer;
        VType value;

        PV(PVReader<VType> reader, PVWriter<Object> writer) {
            this.reader = reader;
            this.writer = writer;
            reader.addPVReaderListener(new PVReaderListener<VType>() {
                @Override
                public void pvChanged(PVReaderEvent<VType> event) {
                    synchronized (SnapshotViewerController.this) {
                        if (suspend.get() > 0)
                            return;
                    }
                    value = event.getPvReader().getValue();
                }
            });
        }
    }

    private BooleanProperty snapshotSaveableProperty = new SimpleBooleanProperty(false);
    private BooleanProperty snapshotRestorableProperty = new SimpleBooleanProperty(false);

    private int numberOfSnapshots = 0;
    private final ArrayList<VSnapshot> snapshots = new ArrayList<>(10);
    private final Map<String, TableEntry> items = new LinkedHashMap<>();
    private final Map<TableEntry, PV> pvs = new LinkedHashMap<>();
    private final GUIUpdateThrottle throttle = new GUIUpdateThrottle(20, TABLE_UPDATE_RATE) {
        @Override
        protected void fire() {
            Platform.runLater(() -> {
                synchronized (SnapshotViewerController.this) {
                    if (suspend.get() > 0)
                        return;
                }
                pvs.forEach((k, v) -> k.liveValueProperty().set(v.value));
            });
        }
    };
    private final AtomicInteger suspend = new AtomicInteger(0);
    private final SnapshotViewerEditor owner;

    /**
     * Constructs a new controller for the given editor.
     *
     * @param owner the editor
     */
    public SnapshotViewerController(SnapshotViewerEditor owner) {
        this.owner = owner;
        throttle.start();
        SaveRestoreService.getInstance().addPropertyChangeListener(SaveRestoreService.BUSY,
                e -> snapshotSaveableProperty
                        .set(!getSnapshots(true).isEmpty() && !SaveRestoreService.getInstance().isBusy()));
    }

    /**
     * Dispose of all allocated resources.
     */
    public void dispose() {
        pvs.values().forEach((e) -> {
            e.reader.close();
            e.writer.close();
        });
        pvs.clear();
        items.clear();
        synchronized (snapshots) {
            snapshots.clear();
        }
        numberOfSnapshots = 0;
    }

    private void connectPVs() {
        items.values().forEach((e) -> {
            if (!pvs.containsKey(e)) {
                String name = e.pvNameProperty().get();
                PVReader<VType> reader = PVManager.read(channel(name, VType.class, VType.class))
                        .readListener(new PVReaderListener<VType>() {
                    @Override
                    public void pvChanged(PVReaderEvent<VType> event) {
                        throttle.trigger();
                    }
                }).maxRate(TimeDuration.ofMillis(100));
                PVWriter<Object> writer = PVManager.write(channel(name)).timeout(TimeDuration.ofMillis(1000)).async();
                pvs.put(e, new PV(reader, writer));
            }
        });
    }

    /**
     * Set the snapshot as the primary snapshot for this editor. All existing snapshots are cleared. The method returns
     * the list of all table entries that should be displayed in the viewer.
     *
     * @param data the snapshot to set
     * @return a list of table entries that should be shown in the table
     */
    public List<TableEntry> setSnapshot(VSnapshot data) {
        dispose();
        List<String> names = data.getNames();
        List<VType> values = data.getValues();
        List<Boolean> selected = data.getSelected();
        synchronized (snapshots) {
            snapshots.add(data);
        }
        snapshotRestorableProperty.set(data.isSaved());
        String name;
        TableEntry e;
        for (int i = 0; i < names.size(); i++) {
            e = new TableEntry();
            name = names.get(i);
            e.idProperty().setValue(i + 1);
            e.pvNameProperty().setValue(name);
            e.selectedProperty().setValue(selected.get(i));
            e.setSnapshotValue(values.get(i), numberOfSnapshots);
            items.put(name, e);
        }
        numberOfSnapshots = 1;
        connectPVs();
        snapshotSaveableProperty.set(data.isSaveable() && !SaveRestoreService.getInstance().isBusy());
        return new ArrayList<>(items.values());
    }

    /**
     * Add a snapshot and compare it to the base one. If no base snapshot is set, the provided snapshot becomes the base
     * one. Method returns the list of all entries to be shown in the viewer.
     *
     * @param data the snapshot to add
     * @return a list of entries to display in the viewer
     */
    public List<TableEntry> addSnapshot(VSnapshot data) {
        if (numberOfSnapshots == 1 && !getSnapshot(0).isSaveable() && !getSnapshot(0).isSaved()) {
            return setSnapshot(data);
        } else if (numberOfSnapshots == 0) {
            return setSnapshot(data);
        } else {
            List<String> names = data.getNames();
            List<VType> values = data.getValues();
            String n;
            TableEntry e;
            for (int i = 0; i < names.size(); i++) {
                n = names.get(i);
                e = items.get(n);
                if (e == null) {
                    e = new TableEntry();
                    e.idProperty().setValue(items.size() + i + 1);
                    e.pvNameProperty().setValue(n);
                    items.put(n, e);
                }
                e.setSnapshotValue(values.get(i), numberOfSnapshots);
            }
            numberOfSnapshots++;
            synchronized (snapshots) {
                snapshots.add(data);
            }
            connectPVs();
            if (!snapshotSaveableProperty.get()) {
                snapshotSaveableProperty.set(data.isSaveable() && !SaveRestoreService.getInstance().isBusy());
            }
            return new ArrayList<>(items.values());
        }
    }

    /**
     * @return the number of all snapshots in the viewer (including the base one)
     */
    public int getNumberOfSnapshots() {
        return numberOfSnapshots;
    }

    private void lock() {
        synchronized (this) {
            suspend.incrementAndGet();
        }
    }

    private void unlock() {
        synchronized (this) {
            if (suspend.decrementAndGet() == 0) {
                this.throttle.trigger();
            }
        }
    }

    /**
     * Read the live snapshot value and create a new snapshot. The snapshot is added to the viewer for comparison. This
     * method should not be called from the UI thread.
     */
    public void takeSnapshot() {
        lock();
        try {
            List<String> names = new ArrayList<>(items.size());
            List<VType> values = new ArrayList<>(items.size());
            List<Boolean> selected = new ArrayList<>(items.size());
            for (TableEntry t : items.values()) {
                names.add(t.pvNameProperty().get());
                VType val = pvs.get(t).value;// t.liveValueProperty().get();
                values.add(val == null ? VNoData.INSTANCE : val);
                selected.add(t.selectedProperty().get());
            }
            // taken snapshots always belong to the beamline set of the master snapshot
            BeamlineSet set = getSnapshot(0).getBeamlineSet();
            Snapshot snapshot = new Snapshot(set);
            VSnapshot taken = new VSnapshot(snapshot, names, selected, values, Timestamp.now());
            owner.addSnapshot(taken);
            SaveRestoreService.LOGGER.log(Level.FINE, "Snapshot taken for '" + set.getFullyQualifiedName() + "'.");
        } finally {
            unlock();
        }
    }

    /**
     * @return the property describing whether there is at least one saveable snapshot available
     */
    public ReadOnlyBooleanProperty snapshotSaveableProperty() {
        return snapshotSaveableProperty;
    }

    /**
     * @return the property describing whether there is at least one restorable snapshot available
     */
    public ReadOnlyBooleanProperty snapshotRestorableProperty() {
        return snapshotRestorableProperty;
    }

    /**
     * Save the snapshot by forwarding it to the {@link DataProvider}. Only the snapshots that belong to this viewer can
     * be saved. This method should never called on the UI thread.
     *
     * @param snapshot the snapshot to save
     */
    public VSnapshot saveSnapshot(VSnapshot snapshot) {
        try {
            lock();
            if (snapshot.getSnapshot().isPresent()) {
                Optional<String> comment = FXTextAreaInputDialog.get(owner.getSite().getShell(), "Snapshot Comment",
                        "Provide a short comment for the snapshot " + snapshot, "",
                        e -> (e == null || e.trim().length() < 10) ? "Comment should be at least 10 characters long."
                                : null);
                return comment.map(e -> {
                    VSnapshot s = null;
                    try {
                        DataProviderWrapper dpw = SaveRestoreService.getInstance()
                                .getDataProvider(snapshot.getBeamlineSet().getDataProviderId());
                        s = dpw.provider.saveSnapshot(snapshot, e);
                        if (s != null) {
                            synchronized (snapshots) {
                                for (int i = 0; i < snapshots.size(); i++) {
                                    if (snapshots.get(i).equals(s)) {
                                        snapshots.set(i, s);
                                        break;
                                    }
                                }
                            }
                        }
                        SaveRestoreService.LOGGER.log(Level.FINE,
                                "Successfully saved Snapshot '" + snapshot.getBeamlineSet().getFullyQualifiedName()
                                        + ": " + snapshot.getSnapshot().get().getDate() + "'.");
                    } catch (DataProviderException ex) {
                        Selector.reportException(ex, owner.getSite().getShell());
                    }
                    return s;
                }).orElse(null);
            } else {
                // should never happen at all
                throw new IllegalArgumentException("Snapshot " + snapshot + " is invalid.");
            }
        } finally {
            unlock();
        }
    }

    /**
     * Restore the values from the snapshot and set them on the PVs. Only the snapshot that belongs to this viewer can
     * be restored. This method should not be called from the UI thread.
     *
     * @param s the snapshot
     */
    public void restoreSnapshot(VSnapshot s) {
        try {
            lock();
            if (s.isSaved()) {
                List<String> names = s.getNames();
                List<VType> values = s.getValues();
                for (int i = 0; i < names.size(); i++) {
                    TableEntry e = items.get(names.get(i));
                    if (e.selectedProperty().get()) {
                        pvs.get(e).writer.write(Utilities.toRawValue(values.get(i)));
                    }
                }
                SaveRestoreService.LOGGER.log(Level.FINE, "Restored snapshot '"
                        + s.getBeamlineSet().getFullyQualifiedName() + ": " + s.getSnapshot().get() + "'.");
            } else {
                throw new IllegalArgumentException(
                        "Snapshot " + s + " has not been saved yet. Only saved snapshots can be used for restoring.");
            }
        } finally {
            unlock();
        }
    }

    /**
     * @param index the index of the snapshot to return
     * @return the snapshot under the given index (0 for the base snapshot and 1 or more for the compared ones)
     */
    public VSnapshot getSnapshot(int index) {
        synchronized (snapshots) {
            return snapshots.get(index);
        }
    }

    /**
     * @param saveable true for saveable and false for restorable snapshots
     * @return saveable snapshots or restorable snapshots
     */
    public List<VSnapshot> getSnapshots(boolean saveable) {
        List<VSnapshot> snaps = new ArrayList<>();
        synchronized (snapshots) {
            for (VSnapshot v : snapshots) {
                if (saveable && v.isSaveable()) {
                    snaps.add(v);
                } else if (!saveable && v.isSaved()) {
                    snaps.add(v);
                }
            }
        }
        return Collections.unmodifiableList(snaps);
    }
}
