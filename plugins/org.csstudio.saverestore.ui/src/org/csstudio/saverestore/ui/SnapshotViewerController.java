package org.csstudio.saverestore.ui;

import static org.diirt.datasource.ExpressionLanguage.channel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.csstudio.saverestore.BeamlineSet;
import org.csstudio.saverestore.DataProvider;
import org.csstudio.saverestore.Engine;
import org.csstudio.saverestore.Snapshot;
import org.csstudio.saverestore.Utilities;
import org.csstudio.saverestore.VNoData;
import org.csstudio.saverestore.VSnapshot;
import org.csstudio.saverestore.ui.util.GUIUpdateThrottle;
import org.csstudio.saverestore.ui.util.TextAreaInputDialog;
import org.diirt.datasource.PVManager;
import org.diirt.datasource.PVReader;
import org.diirt.datasource.PVReaderEvent;
import org.diirt.datasource.PVReaderListener;
import org.diirt.datasource.PVWriter;
import org.diirt.util.time.TimeDuration;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.VType;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * <code>SnapshotViewerController</code> is the controller for the snapshot viewer editor. It provides the logic
 * for adding and removing snapshots, as well as for taking, saving and restoring the snapshots.
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
                    synchronized(SnapshotViewerController.this) {
                        if (suspend.get() > 0) return;
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
                synchronized(SnapshotViewerController.this) {
                    if (suspend.get() > 0) return;
                }
                pvs.forEach((k,v) -> k.liveValueProperty().set(v.value));
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
        snapshots.clear();
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
                PVWriter<Object> writer = PVManager.write(channel(name))
                        .timeout(TimeDuration.ofMillis(1000)).async();
                pvs.put(e, new PV(reader,writer));
            }
        });
    }

    /**
     * Set the snapshot as the primary snapshot for this editor. All existing snapshots are cleared. The
     * method returns the list of all table entries that should be displayed in the viewer.
     *
     * @param data the snapshot to set
     * @return a list of table entries that should be shown in the table
     */
    public synchronized List<TableEntry> setSnapshot(VSnapshot data) {
        dispose();
        List<String> names = data.getNames();
        List<VType> values = data.getValues();
        snapshots.add(data);
        snapshotRestorableProperty.set(data.isSaved());
        String name;
        TableEntry e;
        for (int i = 0; i < names.size(); i++) {
            e = new TableEntry();
            name = names.get(i);
            e.idProperty().setValue(i+1);
            e.pvNameProperty().setValue(name);
            e.setSnapshotValue(values.get(i), numberOfSnapshots);
            items.put(name, e);
        }
        numberOfSnapshots = 1;
        connectPVs();
        snapshotSaveableProperty.set(data.isSaveable());
        return new ArrayList<>(items.values());
    }

    /**
     * Add a snapshot and compare it to the base one. If no base snapshot is set, the provided snapshot becomes
     * the base one. Method returns the list of all entries to be shown in the viewer.
     *
     * @param data the snapshot to add
     * @return a list of entries to display in the viewer
     */
    public synchronized List<TableEntry> addSnapshot(VSnapshot data) {
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
                    e.idProperty().setValue(items.size() + i+1);
                    e.pvNameProperty().setValue(n);
                    items.put(n,e);
                }
                e.setSnapshotValue(values.get(i), numberOfSnapshots);
            }
            numberOfSnapshots++;
            snapshots.add(data);
            connectPVs();
            if (!snapshotSaveableProperty.get()) {
                snapshotSaveableProperty.set(data.isSaveable());
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
        synchronized(this) {
            suspend.incrementAndGet();
        }
    }

    private void unlock() {
        synchronized(this) {
            if (suspend.decrementAndGet() == 0) {
                this.throttle.trigger();
            }
        }
    }

    /**
     * Read the live snapshot value and create a new snapshot. The snapshot is added to the viewer for comparison.
     */
    public void takeSnapshot() {
        lock();
        try {
            List<String> names = new ArrayList<>(items.size());
            List<VType> values = new ArrayList<>(items.size());
            for (TableEntry t : items.values()) {
                names.add(t.pvNameProperty().get());
                VType val = pvs.get(t).value;//t.liveValueProperty().get();
                values.add(val == null ? VNoData.INSTANCE : val);
            }
            BeamlineSet set = snapshots.get(0).getBeamlineSet();
            Snapshot snapshot = new Snapshot(set);
            VSnapshot taken = VSnapshot.of(snapshot, names, values, Timestamp.now());
            owner.addSnapshot(taken);
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
     * Save the snapshot by forwarding it to the {@link DataProvider}. Only the snapshots that belong to this
     * viewer can be saved.
     *
     * @param snapshot the snapshot to save
     */
    public void saveSnapshot(VSnapshot snapshot) {
        try {
            lock();
            if (snapshots.contains(snapshot)) {
                if (snapshot.getSnapshot().isPresent()) {
                    TextAreaInputDialog dialog = new TextAreaInputDialog(
                            owner.getSite().getShell(), "Snapshot Comment",
                            "Provide a short comment for the snapshot " + snapshot + ":", "", new IInputValidator() {
                                @Override
                                public String isValid(String newText) {
                                    if (newText == null || newText.trim().length() < 10) {
                                        return "Comment should be at least 10 characters long.";
                                    }
                                    return null;
                                }
                            });
                    if (dialog.open() == IDialogConstants.OK_ID) {
                        final String c = dialog.getValue();
                        Engine.getInstance().execute(() -> {
                            DataProvider provider = Engine.getInstance().getSelectedDataProvider().provider;
                            try {
                                provider.saveSnapshot(snapshot, c);
                            } catch (Exception e) {
                                Engine.LOGGER.log(Level.WARNING, "Could not save snapshot " + snapshot + ".", e);
                            }
                        });
                    }

//                  TextAreaDialog dialog = new TextAreaDialog();
//                  dialog.initModality(Modality.APPLICATION_MODAL);
//                  dialog.initOwner(owner.getWindow());
//                  dialog.setTitle("Snapshot Comment");
//                  dialog.setHeaderText("Provide a short comment for the snapshot " + snapshot + ":");
//                  dialog.showAndWait();
//                  Optional<String> comment = dialog.showAndWait();
//                  comment.ifPresent(c -> {
                } else {
                    throw new IllegalArgumentException("Snapshot " + snapshot + " is invalid.");
                }
            } else {
                throw new IllegalArgumentException("Snapshot " + snapshot + " is not a part of this view.");
            }

        } finally {
            unlock();
        }
    }

    /**
     * Restore the values from the snapshot and set them on the PVs. Only the snapshot that belongs to this viewer
     * can be restored.
     *
     * @param s the snapshot
     */
    public void restoreSnapshot(VSnapshot s) {
        try {
            lock();
            if (snapshots.contains(s)) {
                if (s.isSaved()) {
                    List<String> names = s.getNames();
                    List<VType> values = s.getValues();
                    for (int i = 0; i < names.size(); i++) {
                        TableEntry e = items.get(names.get(i));
                        if (e.selectedProperty().get()) {
                            pvs.get(e).writer.write(Utilities.toRawValue(values.get(i)));
                        }
                    }
                } else {
                    throw new IllegalArgumentException("Snapshot " + s
                            + " has not been saved yet. Only saved snapshots can be used for restoring.");
                }
            } else {
                throw new IllegalArgumentException("Snapshot " + s + " is not part of this view.");
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
        return snapshots.get(index);
    }

    /**
     * @param saveable true for saveable and false for restorable snapshots
     * @return saveable snapshots or restorable snapshots
     */
    public List<VSnapshot> getSnapshots(boolean saveable) {
        List<VSnapshot> snaps = new ArrayList<>();
        for (VSnapshot v : snapshots) {
            if (saveable && v.isSaveable()) {
                snaps.add(v);
            } else if (!saveable && v.isSaved()) {
                snaps.add(v);
            }

        }
        return Collections.unmodifiableList(snaps);
    }

}
