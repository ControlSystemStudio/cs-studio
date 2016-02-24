package org.csstudio.saverestore.ui;

import static org.diirt.datasource.ExpressionLanguage.channel;

import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.csstudio.saverestore.DataProvider;
import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.DataProviderWrapper;
import org.csstudio.saverestore.FileUtilities;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.SnapshotContent;
import org.csstudio.saverestore.UnsupportedActionException;
import org.csstudio.saverestore.Utilities;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.SaveSet;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.Threshold;
import org.csstudio.saverestore.data.VDisconnectedData;
import org.csstudio.saverestore.data.VSnapshot;
import org.csstudio.saverestore.ui.util.GUIUpdateThrottle;
import org.csstudio.saverestore.ui.util.VTypePair;
import org.csstudio.ui.fx.util.FXMessageDialog;
import org.diirt.datasource.PVManager;
import org.diirt.datasource.PVReader;
import org.diirt.datasource.PVWriter;
import org.diirt.util.time.TimeDuration;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.Alarm;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Time;
import org.diirt.vtype.VTable;
import org.diirt.vtype.VType;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

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
    /** Single snapshot file extension */
    public static final String FEXT_SNP = ".snp";
    /** Multiple snapshots (what you see) file extension */
    public static final String FEXT_CSV = ".csv";

    // for testing purposes this should not be final
    private static Executor UI_EXECUTOR = Platform::runLater;

    private static final String EMPTY_STRING = "";

    private class PV {
        final PVReader<VType> reader;
        final PVWriter<Object> writer;
        PVReader<VType> readback;
        VType value = VDisconnectedData.INSTANCE;
        VType readbackValue = VDisconnectedData.INSTANCE;

        PV(PVReader<VType> reader, PVWriter<Object> writer, PVReader<VType> readback) {
            this.reader = reader;
            this.writer = writer;
            this.reader.addPVReaderListener(e -> {
                synchronized (SnapshotViewerController.this) {
                    if (suspend.get() > 0) {
                        return;
                    }
                }
                if (e.isExceptionChanged()) {
                    SaveRestoreService.LOGGER.log(Level.WARNING, "DIIRT Connection Error.",
                        e.getPvReader().lastException());
                }
                value = e.getPvReader().isConnected() ? e.getPvReader().getValue() : VDisconnectedData.INSTANCE;
                throttle.trigger();
            });
            this.value = reader.getValue();
            setReadbackReader(readback);
        }

        void setReadbackReader(PVReader<VType> readbackReader) {
            this.readback = readbackReader;
            if (this.readback != null) {
                this.readback.addPVReaderListener(e -> {
                    if (suspend.get() > 0) {
                        return;
                    }
                    readbackValue = e.getPvReader().isConnected() ? e.getPvReader().getValue()
                        : VDisconnectedData.INSTANCE;
                    if (showReadbacks) {
                        throttle.trigger();
                    }
                });
                this.readbackValue = readback.getValue();
            }
        }

        void resume() {
            this.value = reader.getValue();
            if (readback != null) {
                readbackValue = readback.getValue();
            }
        }

        void dispose() {
            if (!reader.isClosed()) {
                reader.close();
            }
            if (!writer.isClosed()) {
                writer.close();
            }
            if (readback != null && !readback.isClosed()) {
                readback.close();
            }
        }
    }

    private final BooleanProperty snapshotSaveableProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty snapshotRestorableProperty = new SimpleBooleanProperty(false);
    private final ObjectProperty<VSnapshot> baseSnapshotProperty = new SimpleObjectProperty<>(null);

    private final List<VSnapshot> snapshots = new ArrayList<>(10);
    private final Map<String, TableEntry> items = new LinkedHashMap<>();
    private final Map<String, String> readbacks = new HashMap<>();
    private final Map<TableEntry, PV> pvs = new HashMap<>();
    private final Map<String, PV> pvsForDisposal = new HashMap<>();
    private List<TableEntry> filteredList = new ArrayList<>(0);
    private final GUIUpdateThrottle throttle = new GUIUpdateThrottle(20, TABLE_UPDATE_RATE) {
        @Override
        protected void fire() {
            UI_EXECUTOR.execute(() -> {
                if (suspend.get() > 0) {
                    return;
                }
                pvs.forEach((k, v) -> {
                    k.setLiveValue(v.value);
                    k.setReadbackValue(v.readbackValue);
                });
            });
        }
    };
    private final AtomicInteger suspend = new AtomicInteger(0);
    private final ISnapshotReceiver receiver;

    private boolean showReadbacks;
    private boolean showStoredReadbacks;
    private boolean hideEqualItems;
    private String filter;

    private PropertyChangeListener busyListener = e -> snapshotSaveableProperty
        .set(!getSnapshots(true).isEmpty() && !SaveRestoreService.getInstance().isBusy());

    /**
     * Constructs a new controller for the given editor.
     *
     * @param receiver the receiver of created snapshots and shell provider for the parent shell of all popup dialogs
     */
    public SnapshotViewerController(ISnapshotReceiver receiver) {
        this.receiver = receiver;
        start();
        SaveRestoreService.getInstance().addPropertyChangeListener(SaveRestoreService.BUSY, busyListener);
    }

    /**
     * Start the gui throttle.
     */
    protected void start() {
        throttle.start();
    }

    /**
     * Returns the snapshot receiver.
     *
     * @return the snapshot receiver
     */
    public ISnapshotReceiver getSnapshotReceiver() {
        return receiver;
    }

    /**
     * Dispose of all allocated resources.
     */
    public void dispose() {
        dispose(true);
        SaveRestoreService.getInstance().removePropertyChangeListener(SaveRestoreService.BUSY, busyListener);
    }

    /**
     * Dispose of all allocated resources, except PVs. If <code>closePVs</code> is true the pvs are disposed of,
     * otherwise they are only marked for disposal. It is expected that the caller to this method later checks the PVs
     * and disposes of those that have not been unmarked.
     *
     * @param closePVs true if the PVs should be closed and map cleared or false if they should only be marked for
     *            disposal
     */
    private void dispose(boolean closePVs) {
        synchronized (snapshots) {
            // synchronise, because this method can be called from the UI thread by Eclipse, when the editor is closing
            if (closePVs) {
                pvsForDisposal.values().forEach(e -> e.dispose());
                pvsForDisposal.clear();
                pvs.values().forEach(e -> e.dispose());
                pvs.clear();
            } else {
                pvs.forEach((e, p) -> pvsForDisposal.put(e.pvNameProperty().get(), p));
                pvs.clear();
            }
            items.clear();
            snapshots.clear();
        }
    }

    private void connectPVs() {
        suspend();
        try {
            items.values().forEach(e -> {
                PV pv = pvs.get(e);
                if (pv == null) {
                    pv = pvsForDisposal.remove(e.pvNameProperty().get());
                    if (pv != null) {
                        pvs.put(e, pv);
                    }
                }
                if (pv == null) {
                    String name = e.pvNameProperty().get();
                    PVReader<VType> reader = PVManager.read(channel(name, VType.class, VType.class))
                        .maxRate(TimeDuration.ofMillis(100));
                    PVWriter<Object> writer = PVManager.write(channel(name)).timeout(TimeDuration.ofMillis(1000))
                        .async();
                    String readback = e.readbackNameProperty().get();
                    PVReader<VType> readbackReader = null;
                    if (readback != null && !readback.isEmpty()) {
                        readbackReader = PVManager.read(channel(readback, VType.class, VType.class))
                            .maxRate(TimeDuration.ofMillis(100));
                    }
                    pvs.put(e, new PV(reader, writer, readbackReader));
                } else {
                    if (pv.readback == null) {
                        String readback = e.readbackNameProperty().get();
                        if (readback != null && !readback.isEmpty()) {
                            PVReader<VType> readbackReader = PVManager.read(channel(readback, VType.class, VType.class))
                                .maxRate(TimeDuration.ofMillis(100));
                            pv.setReadbackReader(readbackReader);
                        }
                    }
                }
            });
        } finally {
            resume();
        }
    }

    /**
     * Returns the java fx property that provides the selected base snapshot.
     *
     * @return the selected base snapshot property
     */
    public ReadOnlyObjectProperty<VSnapshot> baseSnapshotProperty() {
        return baseSnapshotProperty;
    }

    /**
     * Set the snapshot as the primary snapshot for this editor. All existing snapshots are cleared. The method returns
     * the list of all table entries that should be displayed in the viewer.
     *
     * @param data the snapshot to set
     * @return a list of table entries that should be shown in the table
     */
    public List<TableEntry> setSnapshot(VSnapshot data) {
        dispose(false);
        List<TableEntry> ret = setSnapshotInternal(data);
        pvsForDisposal.values().forEach(p -> p.dispose());
        pvsForDisposal.clear();
        return ret;
    }

    private List<TableEntry> setSnapshotInternal(VSnapshot data) {
        List<String> names = data.getNames();
        List<VType> values = data.getValues();
        List<Boolean> selected = data.getSelected();
        List<String> rbs = data.getReadbackNames();
        List<VType> rbValues = data.getReadbackValues();
        synchronized (snapshots) {
            snapshots.add(data);
        }
        snapshotRestorableProperty.set(data.getSnapshot().isPresent());
        String name;
        TableEntry e;
        for (int i = 0; i < names.size(); i++) {
            e = new TableEntry();
            name = names.get(i);
            e.idProperty().setValue(i + 1);
            e.pvNameProperty().setValue(name);
            e.selectedProperty().setValue(selected.get(i));
            e.setSnapshotValue(values.get(i), 0);
            if (rbValues.size() > i) {
                e.setStoredReadbackValue(rbValues.get(i), 0);
            }
            items.put(name, e);
            String s = readbacks.get(name);
            if (rbs.size() > i && (s == null || s.isEmpty())) {
                readbacks.put(name, rbs.get(i));
                e.readbackNameProperty().set(rbs.get(i));
            }
        }
        connectPVs();
        snapshotSaveableProperty.set(data.isSaveable() && !SaveRestoreService.getInstance().isBusy());
        updateThresholds();
        UI_EXECUTOR.execute(() -> baseSnapshotProperty.set(data));
        return filter(items.values(), filter);
    }

    /**
     * Add a snapshot and compare it to the base one. If no base snapshot is set, the provided snapshot becomes the base
     * one. Method returns the list of all entries to be shown in the viewer.
     *
     * @param data the snapshot to add
     * @return a list of entries to display in the viewer
     */
    public List<TableEntry> addSnapshot(VSnapshot data) {
        int numberOfSnapshots = getNumberOfSnapshots();
        if (numberOfSnapshots == 0) {
            return setSnapshotInternal(data); // do not dispose of anything
        } else if (numberOfSnapshots == 1 && !getSnapshot(0).isSaveable() && !getSnapshot(0).isSaved()) {
            return setSnapshot(data);
        } else {
            List<String> names = data.getNames();
            List<VType> values = data.getValues();
            List<String> rbs = data.getReadbackNames();
            List<VType> rbValues = data.getReadbackValues();
            boolean update = false;
            String n;
            TableEntry e;
            List<TableEntry> withoutValue = new ArrayList<>(items.values());
            for (int i = 0; i < names.size(); i++) {
                n = names.get(i);
                e = items.get(n);
                if (e == null) {
                    e = new TableEntry();
                    e.idProperty().setValue(items.size() + i + 1);
                    e.pvNameProperty().setValue(n);
                    items.put(n, e);
                    String s = readbacks.get(n);
                    if (rbs.size() > i && (s == null || s.isEmpty())) {
                        readbacks.put(n, rbs.get(i));
                        e.readbackNameProperty().set(rbs.get(i));
                    }
                    update = true;
                }
                e.setSnapshotValue(values.get(i), numberOfSnapshots);
                if (rbValues.size() > i) {
                    e.setStoredReadbackValue(rbValues.get(i), numberOfSnapshots);
                }
                withoutValue.remove(e);
            }
            for (TableEntry te : withoutValue) {
                te.setSnapshotValue(VDisconnectedData.INSTANCE, numberOfSnapshots);
            }
            synchronized (snapshots) {
                snapshots.add(data);
            }
            connectPVs();
            if (!snapshotSaveableProperty.get()) {
                snapshotSaveableProperty.set(data.isSaveable() && !SaveRestoreService.getInstance().isBusy());
            }
            snapshotRestorableProperty.set(true);
            if (update) {
                updateThresholds();
            }
            return filter(items.values(), filter);
        }
    }

    @SuppressWarnings("rawtypes")
    private void updateThresholds() {
        Optional<ParametersProvider> provider = ExtensionPointLoader.getInstance().getParametersProvider();
        if (provider.isPresent()) {
            final List<String> pvNames = new ArrayList<>(items.size());
            final List<VType> values = new ArrayList<>(items.size());
            items.values().forEach(i -> {
                pvNames.add(i.pvNameProperty().get());
                values.add(i.valueProperty().get().value);
            });
            Map<String, Threshold> thresholds = provider.get().getThresholds(pvNames, values,
                getSnapshot(0).getSaveSet().getBaseLevel());
            items.forEach((k, v) -> v.setThreshold(Optional.ofNullable(thresholds.get(k))));
        } else {
            final Map<String, Threshold> thresholds = new HashMap<>(items.size());
            items.values().forEach(i -> {
                String pv = i.pvNameProperty().get();
                for (VSnapshot s : getAllSnapshots()) {
                    Threshold d = s.getThreshold(pv);
                    if (d != null) {
                        thresholds.put(pv, d);
                        break;
                    }
                }
            });
            items.forEach((k, v) -> v.setThreshold(Optional.ofNullable(thresholds.get(k))));
        }
    }

    /**
     * Move the snapshot under the given index to a new editor. After the action is completed the current editor no
     * longer contains this snapshot.
     *
     * @param idx the index of the snapshot to move
     * @return the list of table entries for this editor after removal of the current snapshot
     */
    public List<TableEntry> moveSnapshotToNewEditor(int idx) {
        return redoSnapshots(idx, () -> {
            final VSnapshot s = getSnapshot(idx);
            // this happens in a new thread, so it is OK
            new ActionManager(receiver).openSnapshot(s);
            synchronized (snapshots) {
                return snapshots.stream().filter(e -> e != s).collect(Collectors.toList());
            }
        });
    }

    /**
     * Removes the snapshot under the given index and recreates the table entries. The base snapshot is not allowed to
     * be removed.
     *
     * @param idx the index of the snapshot to remove
     * @return the list of new table entries
     */
    public List<TableEntry> removeSnapshot(int idx) {
        return redoSnapshots(idx, () -> {
            synchronized (snapshots) {
                final VSnapshot s = getSnapshot(idx);
                return snapshots.stream().filter(e -> e != s).collect(Collectors.toList());
            }
        });
    }

    /**
     * Set the snapshot under the given index as the base snapshot for this editor. The current base snapshot is set at
     * the given index.
     *
     * @param idx the index of the snapshot to set as base
     * @return the list of entries
     */
    public List<TableEntry> setAsBase(int idx) {
        return redoSnapshots(idx, () -> {
            List<VSnapshot> newSnapshots;
            synchronized (snapshots) {
                newSnapshots = new ArrayList<>(snapshots);
                VSnapshot oldBase = newSnapshots.get(0);
                VSnapshot newBase = newSnapshots.get(idx);
                newSnapshots.set(0, newBase);
                newSnapshots.set(idx, oldBase);
            }
            return newSnapshots;
        });
    }

    private List<TableEntry> redoSnapshots(int idx, Supplier<List<VSnapshot>> supplier) {
        if (idx == 0) {
            throw new IllegalArgumentException("Base snapshot cannot be moved/removed.");
        } else if (idx > getNumberOfSnapshots()) {
            throw new IllegalArgumentException("The index is greater than the number of snapshots.");
        }
        suspend();
        try {
            List<VSnapshot> newSnapshots = supplier.get();
            dispose(false);
            newSnapshots.forEach(this::addSnapshot);
            pvsForDisposal.values().forEach(p -> p.dispose());
            pvsForDisposal.clear();
            return filter(items.values(), filter);
        } finally {
            resume();
        }
    }

    /**
     * Returns the number of all snapshots currently visible in the viewer (including the base snapshot).
     *
     * @return the number of all snapshots
     */
    public int getNumberOfSnapshots() {
        synchronized (snapshots) {
            return snapshots.size();
        }
    }

    /**
     * Suspend all live updates from the PVs
     */
    public void suspend() {
        suspend.incrementAndGet();
    }

    /**
     * Resume live updates from pvs
     */
    public void resume() {
        if (suspend.decrementAndGet() == 0) {
            pvs.values().forEach(e -> e.resume());
            this.throttle.trigger();
        }
    }

    /**
     * Set the regular expression filter to use for filtering the list of PVs. Only those items where the partial PV
     * name matches the regular expression, will be included. The filtered list is returned.
     *
     * @param filter the filter to apply
     * @return the list of entries that match the filter
     */
    public List<TableEntry> setFilter(String filter) {
        this.filter = filter;
        return filter(items.values(), filter);
    }

    /**
     * Stores the flag whether to show or hide the items where the current and snapshot values are equal.
     *
     * @param hideEqualItems true to hide items with equal values or false otherwise
     * @return the list of filtered entries
     */
    public List<TableEntry> setHideEqualItems(boolean hideEqualItems) {
        this.hideEqualItems = hideEqualItems;
        return filter(items.values(), filter);
    }

    /**
     * Returns true if items with equal live and snapshot values are currently hidden or false is shown.
     *
     * @return true if equal items are hidden or false otherwise.
     */
    public boolean isHideEqualItems() {
        return hideEqualItems;
    }

    /**
     * Read the live snapshot value and create a new snapshot. The snapshot is added to the viewer for comparison. This
     * method should not be called from the UI thread.
     */
    public void takeSnapshot() {
        suspend();
        try {
            SaveSet set = getSnapshot(0).getSaveSet();
            DataProviderWrapper provider = SaveRestoreService.getInstance().getDataProvider(set.getDataProviderId());
            VSnapshot taken = null;
            if (provider.getProvider().isTakingSnapshotsSupported()) {
                try {
                    taken = provider.getProvider().takeSnapshot(set);
                } catch (UnsupportedActionException e) {
                    SaveRestoreService.LOGGER.log(Level.SEVERE, e, () -> "The provider " + provider.getName()
                        + " claims that it can take snapshots, but does not implement the action.");
                } catch (DataProviderException e) {
                    if (SaveRestoreService.getInstance().isCurrentJobCancelled()) {
                        // if cancelled display the message and return
                        ActionManager.reportException(e, receiver.getShell());
                        return;
                    } else {
                        // notify the user about the exception and continue taking the snapshot normally
                        ActionManager.reportException(e,
                            "Snapshot will be taken locally, but it might not be possible to save it.",
                            receiver.getShell());
                    }
                }
            }
            if (taken == null) {
                List<String> names = new ArrayList<>(items.size());
                List<VType> values = new ArrayList<>(items.size());
                List<Boolean> selected = new ArrayList<>(items.size());
                List<String> readbackNames = new ArrayList<>(items.size());
                List<VType> readbackValues = new ArrayList<>(items.size());
                List<String> deltas = new ArrayList<>(items.size());
                PV pv;
                String name;
                String delta = null;
                for (TableEntry t : items.values()) {
                    name = t.pvNameProperty().get();
                    names.add(name);
                    pv = pvs.get(t);
                    values.add(pv == null || pv.value == null ? VDisconnectedData.INSTANCE : pv.value);
                    selected.add(t.selectedProperty().get());
                    String readback = readbacks.get(name);
                    readbackNames.add(readback == null ? EMPTY_STRING : readback);
                    readbackValues
                        .add(pv == null || pv.readbackValue == null ? VDisconnectedData.INSTANCE : pv.readbackValue);
                    for (VSnapshot s : getAllSnapshots()) {
                        delta = s.getDelta(name);
                        if (delta != null) {
                            break;
                        }
                    }
                    if (delta == null) {
                        delta = EMPTY_STRING;
                    }
                    deltas.add(delta);
                }
                // taken snapshots always belong to the save set of the master snapshot
                taken = new VSnapshot(new Snapshot(set), names, selected, values, readbackNames, readbackValues, deltas,
                    Timestamp.now());
            }
            if (SaveRestoreService.getInstance().isOpenNewSnapshotsInCompareView()) {
                receiver.addSnapshot(taken, false);
            } else if (getNumberOfSnapshots() == 1 && !getSnapshot(0).isSaveable() && !getSnapshot(0).isSaved()) {
                // if there is only one snapshot which was actually opened as a save set, add it to the same editor
                receiver.addSnapshot(taken, false);
            } else {
                new ActionManager(receiver).openSnapshot(taken);
            }
            SaveRestoreService.LOGGER.log(Level.FINE, "Snapshot taken for {0}.",
                new Object[] { set.getFullyQualifiedName() });
        } finally {
            resume();
        }
    }

    /**
     * Returns the property describing whether there is at least one saveable snapshot available.
     *
     * @return the property describing whether there is at least one saveable snapshot available
     */
    public ReadOnlyBooleanProperty snapshotSaveableProperty() {
        return snapshotSaveableProperty;
    }

    /**
     * Returns the property describing whether there is at least one restorable snapshot available.
     *
     * @return the property describing whether there is at least one restorable snapshot available
     */
    public ReadOnlyBooleanProperty snapshotRestorableProperty() {
        return snapshotRestorableProperty;
    }

    /**
     * Export a single snapshot file (in the standard snapshot file format) to the given file on the file system.
     *
     * @param snapshot the snapshot to export
     * @param file the destination file
     * @param markAsSaved true if the snapshot should be marked as saved when completed or false if left intact
     */
    public void exportSingleSnapshotToFile(VSnapshot snapshot, File file, boolean markAsSaved) {
        try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
            String contents = FileUtilities.generateSnapshotFileContent(snapshot);
            pw.println(contents);
            if (markAsSaved) {
                snapshot.markNotDirty();
            }
            snapshotSaveableProperty.set(!getSnapshots(true).isEmpty());
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            ActionManager.reportException(ex, receiver.getShell());
        }
    }

    /**
     * Export all snapshots to the given file in csv format. The exported file will contain the stored values, the live
     * values and if requested also the live and/or stored readback values.
     *
     * @param file the destination file
     * @param showLiveReadback true if the live readback values should be included or false if not
     */
    public void exportToFile(File file, boolean showLiveReadback) {
        if (file == null) {
            return;
        }
        suspend();
        List<VSnapshot> snaps = new ArrayList<>();
        synchronized (snapshots) {
            snaps.addAll(snapshots);
        }
        try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
            StringBuilder header = new StringBuilder(200);
            header.append("Setpoint PV Name,");
            header.append(Utilities.timestampToBigEndianString(snaps.get(0).getTimestamp().toDate(), true)).append(',');
            for (int i = 1; i < snaps.size(); i++) {
                header.append(Utilities.timestampToBigEndianString(snaps.get(i).getTimestamp().toDate(), true))
                    .append(',');
            }
            header.append("Live Setpoint Value,Live Setpoint Timestamp");
            if (showLiveReadback) {
                header.append(",Readback PV,Live Readback Value,Live Readback Timestamp");
            }
            pw.println(header.toString());
            items.values().forEach(e -> {
                StringBuilder sb = new StringBuilder(200);
                sb.append(e.pvNameProperty().get()).append(',');
                VTypePair pair = e.valueProperty().get();
                sb.append('"').append(Utilities.valueToString(pair.value)).append('"').append(',');
                for (int i = 1; i < snaps.size(); i++) {
                    pair = e.compareValueProperty(i).get();
                    sb.append('"').append(Utilities.valueToString(((VTypePair) pair).value)).append('"').append(',');
                }
                VType v = e.liveValueProperty().get();
                sb.append('"').append(Utilities.valueToString(v)).append('"');
                sb.append(',');
                if (v instanceof Time) {
//                    sb.append(Utilities.timestampToLittleEndianString(((Time) v).getTimestamp(),true));
                    sb.append(((Time) v).getTimestamp());
                }
                if (showLiveReadback) {
                    sb.append(',').append(e.readbackNameProperty().get());
                    pair = e.readbackProperty().get();
                    sb.append(',').append('"').append(Utilities.valueToString(((VTypePair) pair).value)).append('"')
                        .append(',');
                    if (pair.value instanceof Time) {
//                        sb.append(Utilities.timestampToLittleEndianString(((Time) pair.value).getTimestamp(),true));
                        sb.append(((Time) pair.value).getTimestamp());
                    }
                }
                pw.println(sb.toString());
            });
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            ActionManager.reportException(e, receiver.getShell());
        } finally {
            resume();
        }
    }

    /**
     * Save the snapshot to the given file.
     *
     * @param file the destination file
     * @param snapshot the snapshot to save
     * @param markAsSaved true to mark the snapshot as not dirty after it has been saved or false to not do anything
     * @return the saved snapshot if successful or null if not successful
     */
    public VSnapshot saveToFile(IFile file, VSnapshot snapshot, boolean markAsSaved) {
        try {
            if (!file.exists()) {
                file.create(null, true, null);
            }
            String contents = FileUtilities.generateSnapshotFileContent(snapshot);
            InputStream stream = new ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8.name()));
            file.setContents(stream, IFile.FORCE, new NullProgressMonitor());
            if (markAsSaved) {
                snapshot.markNotDirty();
            }
            snapshotSaveableProperty.set(!getSnapshots(true).isEmpty());
            return snapshot;
        } catch (Exception e) {
            ActionManager.reportException(e, receiver.getShell());
            return null;
        }
    }

    /**
     * Open the snapshot from the given file.
     *
     * @param file the source file
     * @return the snapshot as read from file
     */
    public Optional<VSnapshot> openFromFile(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            String p = file.getAbsolutePath().replace('\\', '/');
            int idx = p.indexOf('/');
            if (idx > -1) {
                p = p.substring(idx + 1);
            }
            SnapshotContent sc = FileUtilities.readFromSnapshot(fis);
            Timestamp snapshotTime = Timestamp.of(sc.getDate());
            SaveSet set = new SaveSet(new Branch(), Optional.empty(), p.split("\\/"), null);
            Snapshot descriptor = new Snapshot(set, sc.getDate(),
                "No Comment\nLoaded from file " + file.getAbsolutePath(), "OS");
            return Optional.of(new VSnapshot((Snapshot) descriptor, sc.getNames(), sc.getSelected(), sc.getData(),
                sc.getReadbacks(), sc.getReadbackData(), sc.getDeltas(), snapshotTime));
        } catch (IOException | RuntimeException | ParseException e) {
            ActionManager.reportException(e, receiver.getShell());
            return Optional.empty();
        }
    }

    /**
     * Save the snapshot by forwarding it to the {@link DataProvider}. This method should never called on the UI thread.
     *
     * @param snapshot the snapshot to save
     */
    public VSnapshot saveSnapshot(String comment, VSnapshot snapshot) {
        try {
            suspend();
            VSnapshot s = null;
            try {
                DataProviderWrapper dpw = SaveRestoreService.getInstance()
                    .getDataProvider(snapshot.getSaveSet().getDataProviderId());
                s = dpw.getProvider().saveSnapshot(snapshot, comment);
                if (s != null) {
                    synchronized (snapshots) {
                        for (int i = 0; i < snapshots.size(); i++) {
                            if (snapshots.get(i).equalsExceptSnapshot(s)) {
                                snapshots.set(i, s);
                                if (i == 0) {
                                    final VSnapshot n = s;
                                    UI_EXECUTOR.execute(() -> baseSnapshotProperty.set(n));
                                }
                                break;
                            }
                        }
                    }
                }
                SaveRestoreService.LOGGER.log(Level.FINE, "Successfully saved Snapshot {0}: {1}.", new Object[] {
                    snapshot.getSaveSet().getFullyQualifiedName(), snapshot.getSnapshot().get().getDate() });
            } catch (DataProviderException ex) {
                ActionManager.reportException(ex, receiver.getShell());
            }
            return s;
        } finally {
            resume();
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
            suspend();
            if (s.isSaved()) {
                List<String> names = s.getNames();
                List<VType> values = s.getValues();
                for (int i = 0; i < names.size(); i++) {
                    TableEntry e = items.get(names.get(i));
                    // only restore the value if the entry is in the filtered list as well
                    if (filteredList.contains(e) && e.selectedProperty().get()) {
                        pvs.get(e).writer.write(Utilities.toRawValue(values.get(i)));
                    }
                }
                SaveRestoreService.LOGGER.log(Level.FINE, "Restored snapshot {0}: {1}.",
                    new Object[] { s.getSaveSet().getFullyQualifiedName(), s.getSnapshot().get() });
            } else {
                throw new IllegalArgumentException(
                    "Snapshot " + s + " has not been saved yet. Only saved snapshots can be used for restoring.");
            }
        } finally {
            resume();
        }
    }

    /**
     * Returns all snapshots.
     *
     * @return the list of all snapshots
     */
    public List<VSnapshot> getAllSnapshots() {
        synchronized (snapshots) {
            return new ArrayList<>(snapshots);
        }
    }

    /**
     * Returns the snapshot stored under the given index.
     *
     * @param index the index of the snapshot to return
     * @return the snapshot under the given index (0 for the base snapshot and 1 or more for the compared ones)
     */
    public VSnapshot getSnapshot(int index) {
        synchronized (snapshots) {
            return snapshots.isEmpty() ? null
                : index >= snapshots.size() ? snapshots.get(snapshots.size() - 1)
                    : index < 0 ? snapshots.get(0) : snapshots.get(index);
        }
    }

    /**
     * Returns all saveable or restorable snapshots. If <code>saveable</code> is true, saveable snapshots are returned;
     * if <code>saveable</code> is false, restorable snapshots are returned.
     *
     * @param saveable true for saveable and false for restorable snapshots
     * @return saveable snapshots or restorable snapshots
     */
    public List<VSnapshot> getSnapshots(boolean saveable) {
        synchronized (snapshots) {
            return snapshots.stream().filter(e -> (saveable && e.isSaveable()) || (!saveable && e.isSaved()))
                .collect(Collectors.toList());
        }
    }

    /**
     * Toggles the visibility of the stored readback values.
     *
     * @param show true to show the stored readback values or false to hide
     * @param consumer the consumer that is notified when the value is updated
     */
    public void showStoredReadbacks(boolean show, final Consumer<List<TableEntry>> consumer) {
        this.showStoredReadbacks = show;
        SaveRestoreService.getInstance().execute("Show stored readbacks", () -> consumer.accept(filteredList));
    }

    /**
     * Returns whether the stored readbacks are displayed in the table or not.
     *
     * @return true if displayed, or false if not displayed
     */
    public boolean isShowStoredReadbacks() {
        return showStoredReadbacks;
    }

    /**
     * Show or hide the readbacks column. If requested to show, the method search for the PVs that do not have a
     * readback assigned yet and asks the readback provider for the readback names. Readbacks that are known are then
     * assigned to table entries and connected to PVs.
     *
     * @param show true to show the readbacks or false to hide them
     * @param consumer consumer that is notified upon completion of the request (always called on non ui thread)
     */
    public void showReadbacks(boolean show, final Consumer<List<TableEntry>> consumer) {
        this.showReadbacks = show;
        final Optional<ParametersProvider> provider = ExtensionPointLoader.getInstance().getParametersProvider();
        SaveRestoreService.getInstance().execute("Load readback names", () -> {
            if (show) {
                if (provider.isPresent()) {
                    List<String> reads = items.keySet().stream().filter(k -> !readbacks.containsKey(k))
                        .collect(Collectors.toList());
                    if (!reads.isEmpty()) {
                        Map<String, String> rbs = provider.get().getReadbackNames(reads);
                        for (String r : reads) {
                            readbacks.put(r, rbs.get(r));
                        }
                    }
                }
                items.values().forEach(t -> t.readbackNameProperty().set(readbacks.get(t.pvNameProperty().get())));
                connectPVs();
            }
            consumer.accept(filter(items.values(), filter));
        });
    }

    /**
     * Returns true if the readbacks are shown in the table or false if they are not shown.
     *
     * @return true if readbacks should be shown or false otherwise
     */
    public boolean isShowReadbacks() {
        return showReadbacks;
    }

    /**
     * Import values using the given value importer. This method should not be called on the UI thread. Upon successful
     * import the consumer is called on the UI thread.
     *
     * @param importer the importer to use
     * @param consumer the consumer which is notified if import was successful
     */
    public void importValues(final ValueImporterWrapper importer, final Consumer<VSnapshot> consumer) {
        List<String> names = new ArrayList<>(items.keySet());
        if (names.isEmpty()) {
            return;
        }
        try {
            Timestamp timestamp = getSnapshot(0).getTimestamp();
            if (timestamp == null) {
                timestamp = Timestamp.now();
            }
            Map<String, VType> values = importer.importer.getValuesForPVs(names, timestamp);
            if (values != null && !values.isEmpty()) {
                SaveSet bs = new SaveSet(null, Optional.empty(), new String[] { importer.name }, importer.name);
                Snapshot desc = new Snapshot(bs, timestamp.toDate(), "Imported from " + importer.name, importer.name);
                final List<VType> vals = names.stream().map(values::get)
                    .map(v -> v == null ? VDisconnectedData.INSTANCE : v).collect(Collectors.toList());
                final VSnapshot snapshot = new VSnapshot(desc, names, vals, timestamp, importer.name);
                UI_EXECUTOR.execute(() -> consumer.accept(snapshot));
            }
        } catch (Exception ex) {
            ActionManager.reportException(ex, receiver.getShell());
        }
    }

    /**
     * Adds a pv from the archive to all snapshots. The value of the PV at the timestamp of each snapshot is loaded and
     * is set as the stored value of that PV for each snapshot. The entry for the pv is added and consumer is notified
     * on the UI thread. The value from the archive is loaded asynchronously and will set on the item in the background.
     * This method must be called on the save restore thread.
     *
     * @param pvName the name of the PV to add
     * @param consumer the consumer that is notified when the loading completes and receives the created table entry
     *            (consumer is always notified on the UI thread)
     */
    @SuppressWarnings("unchecked")
    public void addPVFromArchive(final String pvName, final Consumer<TableEntry> consumer) {
        if (items.containsKey(pvName)) {
            FXMessageDialog.openInformation(receiver.getShell(), "Add Archived PV",
                "The PV '" + pvName + "' is already in the list.");
            return;
        }
        try {
            final List<VSnapshot> snaps = new ArrayList<>();
            synchronized (snapshots) {
                snaps.addAll(snapshots);
            }

            final TableEntry entry = new TableEntry();
            entry.idProperty().set(items.size() + 1);
            entry.pvNameProperty().setValue(pvName);
            entry.selectedProperty().set(false);

            // Hard reference is required, otherwise diirt might flush the reader, before the value even arrives.
            final ArrayList<PVReader<VTable>> archiveReaders = new ArrayList<>();
            for (int i = 0; i < snaps.size(); i++) {
                final int index = i;
                Timestamp start = snaps.get(i).getTimestamp();
                if (start == null) {
                    continue;
                }
                String name = "archive://" + pvName + "?time=" + start.toString();
                PVReader<VTable> reader = PVManager.read(channel(name, VTable.class, VType.class)).readListener(x -> {
                    boolean handled = false;
                    if (x.isValueChanged()) {
                        VTable value = x.getPvReader().getValue();
                        List<VType> archiveValues = (List<VType>) ((VTable) value).getColumnData(0);
                        if (!archiveValues.isEmpty()) {
                            VType v = archiveValues.get(0);
                            snaps.get(index).addOrSetPV(pvName, false, v);
                            entry.setSnapshotValue(v, index);
                        }
                        handled = true;
                    } else if (x.isExceptionChanged()) {
                        entry.statusProperty().set(x.getPvReader().lastException().getMessage());
                        entry.severityProperty().set(AlarmSeverity.INVALID);
                        handled = true;
                    }
                    if (handled) {
                        x.getPvReader().close();
                        synchronized (archiveReaders) {
                            archiveReaders.remove(x.getPvReader());
                            archiveReaders.notifyAll();
                        }
                    }
                }).timeout(TimeDuration.ofMillis(30000)).notifyOn(UI_EXECUTOR).maxRate(TimeDuration.ofMillis(100));
                synchronized (archiveReaders) {
                    archiveReaders.add(reader);
                }
            }

            try {
                long time = System.currentTimeMillis();
                while (System.currentTimeMillis() - time < 30000
                    && !SaveRestoreService.getInstance().isCurrentJobCancelled()) {
                    synchronized (archiveReaders) {
                        if (archiveReaders.isEmpty()) {
                            break;
                        } else {
                            archiveReaders.wait(100);
                        }
                    }
                }
                if (SaveRestoreService.getInstance().isCurrentJobCancelled()) {
                    archiveReaders.forEach(e -> e.close());
                    archiveReaders.clear();
                }
            } catch (InterruptedException e) {
                // ignore
            }
            items.put(pvName, entry);
            PVReader<VType> reader = PVManager.read(channel(pvName, VType.class, VType.class))
                .readListener(x -> throttle.trigger()).maxRate(TimeDuration.ofMillis(100));
            PVWriter<Object> writer = PVManager.write(channel(pvName)).timeout(TimeDuration.ofMillis(1000)).async();
            pvs.put(entry, new PV(reader, writer, null));
            UI_EXECUTOR.execute(() -> consumer.accept(entry));
        } catch (RuntimeException e) {
            ActionManager.reportException(e, receiver.getShell());
        }
    }

    public void removePV(TableEntry entry, Consumer<TableEntry> consumer) {
        if (entry == null) {
            return;
        }
        try {
            String pvName = entry.pvNameProperty().get();
            TableEntry e = items.get(pvName);
            if (e == entry) {
                items.remove(pvName);
            } else {
                return;
            }
            filteredList.remove(entry);
            PV pv = pvs.remove(entry);
            if (pv != null) {
                pv.dispose();
            }
            final List<VSnapshot> snaps = new ArrayList<>();
            synchronized (snapshots) {
                snaps.addAll(snapshots);
            }
            for (VSnapshot s : snaps) {
                s.removePV(pvName);
            }
            UI_EXECUTOR.execute(() -> consumer.accept(entry));
        } catch (RuntimeException e) {
            ActionManager.reportException(e, receiver.getShell());
        }
    }

    /**
     * Filters the values and adds only those where the values are identical to the live values if hide is true or
     * returns all entries if hide is false.
     *
     * @param allEntries the entries to filter
     * @param filter the filter string used for filtering (can be null)
     * @return filtered list
     */
    private List<TableEntry> filter(Collection<TableEntry> allEntries, String filter) {
        List<TableEntry> entries;
        if (filter == null) {
            entries = new ArrayList<>(allEntries);
        } else {
            final Pattern pattern = Pattern.compile(".*" + filter + ".*");
            entries = allEntries.stream().filter(t -> pattern.matcher(t.pvNameProperty().get()).matches())
                .collect(Collectors.toList());
        }
        filteredList = entries;
        return entries;
    }

    /**
     * Update the snapshot under the given index according to the data in the given entry. Only the setpoint value can
     * be changed.
     *
     * @param index the index of the snapshot to update
     * @param entry the table entry providing the new data
     */
    public void updateSnapshot(int index, TableEntry entry) {
        VType value;
        String name = entry.pvNameProperty().get();
        boolean selected = entry.selectedProperty().get();
        if (index == 0) {
            value = entry.valueProperty().get().value;
            if (value instanceof Alarm) {
                entry.statusProperty().set(((Alarm) value).getAlarmName());
                entry.severityProperty().set(((Alarm) value).getAlarmSeverity());
            }
        } else {
            value = entry.compareValueProperty(index).get().value;
        }
        VSnapshot snapshot = getSnapshot(index);
        snapshot.addOrSetPV(name, selected, value);
        snapshotSaveableProperty.set(true);
        receiver.checkDirty();
    }

    /**
     * Update the given snapshot. The value of the provided PV name is changed to the given value, but only if the
     * snapshot is still present in this controller.
     *
     * @param snapshot the snapshot to update
     * @param pvNname the PV name
     * @param value the new value
     */
    public void updateSnapshot(VSnapshot snapshot, String pvName, VType value) {
        int index = snapshots.indexOf(snapshot);
        if (index < 0) {
            return;
        }
        TableEntry entry = null;
        for (TableEntry e : items.values()) {
            if (pvName.equals(e.pvNameProperty().get())) {
                entry = e;
                break;
            }
        }
        if (entry != null) {
            entry.setSnapshotValue(value, index);
            snapshot.addOrSetPV(pvName, entry.selectedProperty().get(), value);
            snapshotSaveableProperty.set(true);
            receiver.checkDirty();
        }
    }
}
