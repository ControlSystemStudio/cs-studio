package org.csstudio.saverestore.ui;

import static org.diirt.datasource.ExpressionLanguage.channel;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Level;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveRepository;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.saverestore.DataProvider;
import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.DataProviderWrapper;
import org.csstudio.saverestore.FileUtilities;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.Utilities;
import org.csstudio.saverestore.Utilities.VTypeComparison;
import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VNoData;
import org.csstudio.saverestore.data.VSnapshot;
import org.csstudio.saverestore.ui.util.GUIUpdateThrottle;
import org.csstudio.saverestore.ui.util.VTypePair;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;
import org.csstudio.trends.databrowser2.preferences.Preferences;
import org.csstudio.ui.fx.util.FXMessageDialog;
import org.csstudio.ui.fx.util.FXTextInputDialog;
import org.csstudio.ui.fx.util.FXTextListInputDialog;
import org.diirt.datasource.PVManager;
import org.diirt.datasource.PVReader;
import org.diirt.datasource.PVReaderEvent;
import org.diirt.datasource.PVReaderListener;
import org.diirt.datasource.PVWriter;
import org.diirt.util.time.TimeDuration;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.Time;
import org.diirt.vtype.VType;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;

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
    /** Single snapshot file extension */
    public static final String FEXT_SNP = ".snp";
    /** Multiple snapshots (what you see) file extension */
    public static final String FEXT_CSV = ".csv";

    private class PV {
        final PVReader<VType> reader;
        final PVWriter<Object> writer;
        PVReader<VType> readback;
        VType value = VNoData.INSTANCE;
        VType readbackValue = VNoData.INSTANCE;

        PV(PVReader<VType> reader, PVWriter<Object> writer, PVReader<VType> readback) {
            this.reader = reader;
            this.writer = writer;
            this.reader.addPVReaderListener(e -> {
                synchronized (SnapshotViewerController.this) {
                    if (suspend.get() > 0)
                        return;
                }
                value = e.getPvReader().getValue();
                throttle.trigger();
            });
            setReadbackReader(readback);
        }

        void setReadbackReader(PVReader<VType> readbackReader) {
            this.readback = readbackReader;
            if (this.readback != null) {
                this.readback.addPVReaderListener(e -> {
                    synchronized (SnapshotViewerController.this) {
                        if (suspend.get() > 0)
                            return;
                    }
                    readbackValue = e.getPvReader().getValue();
                    if (showReadbacks) {
                        throttle.trigger();
                    }
                });
            }
        }
    }

    private BooleanProperty snapshotSaveableProperty = new SimpleBooleanProperty(false);
    private BooleanProperty snapshotRestorableProperty = new SimpleBooleanProperty(false);

    private int numberOfSnapshots = 0;
    private final ArrayList<VSnapshot> snapshots = new ArrayList<>(10);
    private final Map<String, TableEntry> items = new LinkedHashMap<>();
    private final Map<String, String> readbacks = new HashMap<>();
    private final Map<TableEntry, PV> pvs = new LinkedHashMap<>();
    private final GUIUpdateThrottle throttle = new GUIUpdateThrottle(20, TABLE_UPDATE_RATE) {
        @Override
        protected void fire() {
            Platform.runLater(() -> {
                synchronized (SnapshotViewerController.this) {
                    if (suspend.get() > 0)
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
    private final SnapshotViewerEditor owner;

    private boolean showReadbacks = false;

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
            if (e.readback != null) {
                e.readback.close();
            }
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
            PV pv = pvs.get(e);
            if (pv == null) {
                String name = e.pvNameProperty().get();
                PVReader<VType> reader = PVManager.read(channel(name, VType.class, VType.class))
                    .maxRate(TimeDuration.ofMillis(100));
                PVWriter<Object> writer = PVManager.write(channel(name)).timeout(TimeDuration.ofMillis(1000)).async();
                String readback = e.readbackNameProperty().get();
                PVReader<VType> readbackReader = null;
                if (readback != null && !readback.isEmpty()) {
                    readbackReader = PVManager.read(channel(readback, VType.class, VType.class))
                        .maxRate(TimeDuration.ofMillis(100));
                }
                pvs.put(e, new PV(reader, writer, readbackReader));
            } else {
                if (pv.readback == null && showReadbacks) {
                    PVReader<VType> readbackReader = null;
                    String readback = e.readbackNameProperty().get();
                    if (readback != null && !readback.isEmpty()) {
                        readbackReader = PVManager.read(channel(readback, VType.class, VType.class))
                            .maxRate(TimeDuration.ofMillis(100));
                    }
                    pv.setReadbackReader(readbackReader);
                }
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
            List<TableEntry> withoutValue = new ArrayList<>(items.values());
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
                withoutValue.remove(e);
            }
            for (TableEntry te : withoutValue) {
                te.setSnapshotValue(VNoData.INSTANCE, numberOfSnapshots);
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
     * Removes the snapshot under the given index and recreates the table entries. The base snapshot is not allowed to
     * be removed.
     *
     * @param idx the index of the snapshot to remove
     * @return the list of new table entries
     */
    public List<TableEntry> removeSnapshot(int idx) {
        if (idx == 0) {
            throw new IllegalArgumentException("Cannot remove the base snapshot.");
        } else if (idx > numberOfSnapshots) {
            throw new IllegalArgumentException("The index is greater than the number of snapshots.");
        }
        pvs.values().forEach((e) -> {
            e.reader.close();
            e.writer.close();
        });
        pvs.clear();
        items.clear();
        List<VSnapshot> newSnapshots = new ArrayList<>();
        synchronized (snapshots) {
            snapshots.remove(idx);
            numberOfSnapshots = 0;
            newSnapshots.addAll(snapshots);
        }
        newSnapshots.forEach(e -> addSnapshot(e));
        return new ArrayList<>(items.values());
    }

    /**
     * Returns the number of all snapshots currently visible in the viewer (including the base snapshot).
     *
     * @return the number of all snapshots
     */
    public int getNumberOfSnapshots() {
        return numberOfSnapshots;
    }

    /**
     * Returns true if the readbacks are shown in the table or false if they are not shown.
     *
     * @return true if readbacks should be shown or false otherwise
     */
    public boolean isShowReadbacks() {
        return showReadbacks;
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
     */
    public void exportSingleSnapshotToFile(VSnapshot snapshot, File file) {
        try (final PrintWriter pw = new PrintWriter(new FileOutputStream(file))) {
            String contents = FileUtilities.generateSnapshotFileContent(snapshot);
            pw.println(contents);
        } catch (FileNotFoundException ex) {
            Selector.reportException(ex, owner.getSite().getShell());
        }
    }

    /**
     * Export all snapshots to the given file.
     *
     * @param file the destination file
     */
    public void exportToFile(File file) {
        if (file == null) {
            return;
        }
        lock();
        try (final PrintWriter pw = new PrintWriter(new FileOutputStream(file))) {
            StringBuilder header = new StringBuilder(200);
            header.append("Setpoint PV,");
            header.append(Utilities.timestampToBigEndianString(snapshots.get(0).getTimestamp().toDate(), true))
                .append(',');
            String delta = " (\u0394 First Snapshot),";
            for (int i = 1; i < snapshots.size(); i++) {
                header.append(Utilities.timestampToBigEndianString(snapshots.get(i).getTimestamp().toDate(), true))
                    .append(delta);
            }
            header.append("Live Value,Live Timestamp");
            if (showReadbacks) {
                header.append(",Readback PV,Readback Value (\u0394 Live),Readback Timestamp");
            }
            pw.println(header.toString());
            items.values().forEach(e -> {
                StringBuilder sb = new StringBuilder(200);
                sb.append(e.pvNameProperty().get()).append(',');
                sb.append('"').append(Utilities.valueToString(e.valueProperty().get())).append('"').append(',');
                for (int i = 1; i < snapshots.size(); i++) {
                    VTypePair pair = e.compareValueProperty(i).get();
                    VTypeComparison vtc = Utilities.valueToCompareString(((VTypePair) pair).value,
                        ((VTypePair) pair).base);
                    sb.append('"').append(vtc.string).append('"').append(',');
                }
                VType v = e.liveValueProperty().get();
                sb.append('"').append(Utilities.valueToString(v)).append('"');
                sb.append(',');
                if (v instanceof Time) {
                    sb.append(((Time) v).getTimestamp());
                }
                if (showReadbacks) {
                    sb.append(',').append(e.readbackNameProperty().get());
                    VTypePair pair = e.readbackProperty().get();
                    VTypeComparison vtc = Utilities.valueToCompareString(((VTypePair) pair).value,
                        ((VTypePair) pair).base);
                    sb.append(',').append('"').append(vtc.string).append('"').append(',');
                    if (pair.value instanceof Time) {
                        sb.append(((Time) pair.value).getTimestamp());
                    }
                }
                pw.println(sb.toString());
            });
        } catch (FileNotFoundException e) {
            Selector.reportException(e, owner.getSite().getShell());
        } finally {
            unlock();
        }
    }

    /**
     * Save the snapshot to the given file.
     *
     * @param file the destination file
     * @param snapshot the snapshot to save
     * @return the saved snapshot if successful or null if not successful
     */
    public VSnapshot saveToFile(IFile file, VSnapshot snapshot) {
        try {
            if (!file.exists()) {
                file.create(null, true, null);
            }
            String contents = FileUtilities.generateSnapshotFileContent(snapshot);
            InputStream stream = new ByteArrayInputStream(contents.getBytes("UTF-8"));
            file.setContents(stream, IFile.FORCE, new NullProgressMonitor());
            return snapshot;
        } catch (Exception e) {
            Selector.reportException(e, owner.getSite().getShell());
            return null;
        }
    }

    /**
     * Save the snapshot by forwarding it to the {@link DataProvider}. This method should never called on the UI thread.
     *
     * @param snapshot the snapshot to save
     */
    public VSnapshot saveSnapshot(String comment, VSnapshot snapshot) {
        try {
            lock();
            VSnapshot s = null;
            try {
                DataProviderWrapper dpw = SaveRestoreService.getInstance()
                    .getDataProvider(snapshot.getBeamlineSet().getDataProviderId());
                s = dpw.provider.saveSnapshot(snapshot, comment);
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
                    "Successfully saved Snapshot '" + snapshot.getBeamlineSet().getFullyQualifiedName() + ": "
                        + snapshot.getSnapshot().get().getDate() + "'.");
            } catch (DataProviderException ex) {
                Selector.reportException(ex, owner.getSite().getShell());
            }
            return s;
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
            return snapshots.get(index);
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
        return snaps;
    }

    /**
     * Show or hide the readbacks column. If requested to show, the method search for the PVs that do not have a
     * readback assigned yet and asks the readback provider for the readback names. Readbacks that are known are then
     * assigned to table entries and connected to PVs.
     *
     */
    public void showReadbacks(boolean show, final Consumer<List<TableEntry>> consumer) {
        this.showReadbacks = show;
        if (show) {
            ExtensionPointLoader.getInstance().getReadbackProvider()
                .ifPresent(e -> SaveRestoreService.getInstance().execute("Load readback names", () -> {
                    List<String> reads = new ArrayList<>();
                    items.keySet().forEach(k -> {
                        if (!readbacks.containsKey(k)) {
                            reads.add(k);
                        }
                    });
                    if (!reads.isEmpty()) {
                        Map<String, String> rbs = e.getReadbacks(reads);
                        for (String r : reads) {
                            readbacks.put(r, rbs.get(r));
                        }
                    }
                    items.values().forEach(t -> t.readbackNameProperty().set(readbacks.get(t.pvNameProperty().get())));
                    connectPVs();
                    consumer.accept(new ArrayList<>(items.values()));
                }));
        } else {
            SaveRestoreService.getInstance().execute("Load readback names",
                () -> consumer.accept(new ArrayList<>(items.values())));
        }
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
        if (names.size() == 0) {
            return;
        }
        try {
            Timestamp timestamp = getSnapshot(0).getTimestamp();
            Map<String, VType> values = importer.importer.getValuesForPVs(names, timestamp);
            if (values != null && !values.isEmpty()) {
                BeamlineSet bs = new BeamlineSet(null, Optional.empty(), new String[] { importer.name }, importer.name);
                Snapshot desc = new Snapshot(bs, timestamp.toDate(), "Imported from " + importer.name, importer.name);
                final List<VType> vals = new ArrayList<>(names.size());
                names.forEach(n -> {
                    VType v = values.get(n);
                    vals.add(v == null ? VNoData.INSTANCE : v);
                });
                final VSnapshot snapshot = new VSnapshot(desc, names, vals, timestamp);
                Platform.runLater(() -> consumer.accept(snapshot));
            }
        } catch (Exception ex) {
            Selector.reportException(ex, owner.getSite().getShell());
        }
    }

    /**
     * Shows the dialog to enter the name of the PV and to select the archive source. After the pv has been successfully
     * added the consumer is notified on the UI thread.
     *
     * @param consumer the consumer that is notifier when the loading completes and receives the created table entry
     */
    public void addPVFromArchive(final Consumer<TableEntry> consumer) {
        // TODO this ie temporary - it will be handled by diirt when archive source is ready
        ArchiveDataSource[] sources = Preferences.getArchives();
        if (sources.length == 0) {
            FXMessageDialog.openWarning(owner.getSite().getShell(), "Add Archived PV",
                "No archived sources specified.");
            return;
        } else if (sources.length == 1) {
            FXTextInputDialog dialog = new FXTextInputDialog(owner.getSite().getShell(), "Add Archived PV",
                "Enter the name of the PV from the archiving system that you wish to add.", "",
                e -> e == null || e.isEmpty() ? "The PV name cannot be empty" : null);
            dialog.openAndWait().ifPresent(e -> addPVFromArchive(e, sources[0], consumer));
        } else {
            FXTextListInputDialog<ArchiveDataSource> dialog = new FXTextListInputDialog<>(owner.getSite().getShell(),
                "Add Archived PV",
                "Enter the name of the PV from the archiving system that you wish to add and select the archive source.",
                e -> e == null || e.isEmpty() ? "The PV name cannot be empty" : null, Arrays.asList(sources));
            dialog.openAndWait().ifPresent(e -> addPVFromArchive(e, dialog.getSelectedOption(), consumer));
        }
    }

    private void addPVFromArchive(final String pvName, final ArchiveDataSource source,
        final Consumer<TableEntry> consumer) {
        if (items.containsKey(pvName)) {
            FXMessageDialog.openInformation(owner.getSite().getShell(), "Add Archived PV",
                "The PV '" + pvName + "' is already in the list.");
            return;
        }
        SaveRestoreService.getInstance().execute("Add archived PV", () -> {
            try {
                ArchiveReader archive = ArchiveRepository.getInstance().getArchiveReader(source.getUrl());
                List<VType> values = new ArrayList<>();
                List<VSnapshot> snaps = new ArrayList<>();
                synchronized (snapshots) {
                    snaps.addAll(snapshots);
                }
                for (VSnapshot s : snaps) {
                    Timestamp start = s.getTimestamp();
                    Timestamp end = Timestamp.of(new Date(start.toDate().getTime() + 1));
                    ValueIterator iterator = archive.getRawValues(source.getKey(), pvName, start, end);
                    VType temp = null;
                    VType value = null;
                    while (iterator.hasNext()) {
                        // find the first value that has a timestamp greater than start
                        temp = iterator.next();
                        if (temp instanceof Time) {
                            if (((Time) temp).getTimestamp().compareTo(start) > 0) {
                                break;
                            }
                        }
                        value = temp;
                    }

                    if (value == null) {
                        value = VNoData.INSTANCE;
                    }
                    values.add(value);
                }

                final TableEntry e = new TableEntry();
                e.idProperty().set(items.size() + 1);
                items.put(pvName, e);
                e.pvNameProperty().setValue(pvName);
                for (int i = 0; i < values.size(); i++) {
                    snaps.get(i).addPV(pvName, false, values.get(i));
                    e.setSnapshotValue(values.get(i), i);
                }
                e.selectedProperty().set(false);

                PVReader<VType> reader = PVManager.read(channel(pvName, VType.class, VType.class))
                    .readListener(new PVReaderListener<VType>() {
                    @Override
                    public void pvChanged(PVReaderEvent<VType> event) {
                        throttle.trigger();
                    }
                }).maxRate(TimeDuration.ofMillis(100));
                PVWriter<Object> writer = PVManager.write(channel(pvName)).timeout(TimeDuration.ofMillis(1000)).async();
                pvs.put(e, new PV(reader, writer, null));
                Platform.runLater(() -> consumer.accept(e));
            } catch (Exception e) {
                Selector.reportException(e, owner.getSite().getShell());
            }
        });
    }
}
