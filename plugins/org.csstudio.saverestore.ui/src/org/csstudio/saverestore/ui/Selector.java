package org.csstudio.saverestore.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.csstudio.saverestore.CompletionNotifier;
import org.csstudio.saverestore.DataProvider;
import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.DataProviderWrapper;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.BeamlineSetData;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;
import org.eclipse.ui.IWorkbenchPart;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * <code>Selector</code> provides the selection logic and model for browsing the data provider. It contains the
 * available and selected items for each individual section (branch, base level, beamline set and snapshot).
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class Selector {

    private static final Branch DEFAULT_BRANCH = new Branch();

    private ObjectProperty<Branch> selectedBranch;
    private ObjectProperty<BaseLevel> selectedBaseLevel;
    private ObjectProperty<BeamlineSet> selectedBeamlineSet;
    private ObjectProperty<List<BaseLevel>> baseLevels;
    private ObjectProperty<List<Branch>> branches;
    private ObjectProperty<List<BeamlineSet>> beamlineSets;
    private ObjectProperty<List<Snapshot>> snapshots;
    private BooleanProperty allSnapshotsLoaded = new SimpleBooleanProperty(false);
    private Snapshot lastSnapshot;
    private CompletionNotifier notifier = new CompletionNotifier() {
        @Override
        public void synchronised() {
            runOnGUIThread(() -> {
                ((ObjectProperty<BaseLevel>) selectedBaseLevelProperty()).set(null);
                ((ObjectProperty<List<BaseLevel>>) baseLevelsProperty())
                    .set(Collections.unmodifiableList(new ArrayList<>(0)));
                ((ObjectProperty<List<BeamlineSet>>) beamlineSetsProperty())
                    .set(Collections.unmodifiableList(new ArrayList<>(0)));
                ((ObjectProperty<List<Snapshot>>) snapshotsProperty())
                    .set(Collections.unmodifiableList(new ArrayList<>(0)));
                allSnapshotsLoaded.set(false);
                SaveRestoreService.getInstance().execute("Synchronise", () -> {
                    readBranches(selectedBranchProperty().get());
                });
            });
        }

        @Override
        public void snapshotTagged(Snapshot snapshot) {
            List<Snapshot> snp = snapshotsProperty().get();
            final List<Snapshot> newV = snp.stream().map(e -> e.almostEquals(snapshot) ? snapshot : e)
                .collect(Collectors.toList());
            runOnGUIThread(() -> ((SimpleObjectProperty<List<Snapshot>>) snapshotsProperty())
                .set(Collections.unmodifiableList(newV)));
        }

        @Override
        public void snapshotSaved(VSnapshot snapshot) {
            List<Snapshot> snp = snapshotsProperty().get();
            final List<Snapshot> newV = new ArrayList<>(snp.size() + 1);
            newV.add(snapshot.getSnapshot().get());
            newV.addAll(snp);
            Collections.sort(newV);
            runOnGUIThread(() -> ((SimpleObjectProperty<List<Snapshot>>) snapshotsProperty())
                .set(Collections.unmodifiableList(newV)));
        }

        @Override
        public void branchCreated(Branch newBranch) {
            SaveRestoreService.getInstance().execute("Load branches", () -> readBranches(newBranch));
        }

        @Override
        public void beamlineSetSaved(BeamlineSetData set) {
            final BaseLevel base = selectedBaseLevelProperty().get();
            final Branch branch = selectedBranchProperty().get();
            if (!set.getDescriptor().getBranch().equals(branch)) {
                return;
            }
            Optional<BaseLevel> bl = set.getDescriptor().getBaseLevel();
            if (bl.isPresent() && !bl.get().getStorageName().equals(base.getStorageName())) {
                return;
            }
            SaveRestoreService.getInstance().execute("Load beamline sets", () -> reloadBeamlineSets(base, branch));
        }

        @Override
        public void beamlineSetDeleted(BeamlineSet set) {
            List<BeamlineSet> sets = beamlineSetsProperty().get();
            final List<BeamlineSet> newSets = sets.stream().filter(e -> !e.equals(set)).collect(Collectors.toList());
            runOnGUIThread(() -> ((SimpleObjectProperty<List<BeamlineSet>>) beamlineSetsProperty())
                .set(Collections.unmodifiableList(newSets)));
        }

        @Override
        public void dataImported(BeamlineSet source, Branch toBranch, final Optional<BaseLevel> toBase) {
            Branch selected = selectedBranchProperty().get();
            if (selected.equals(toBranch)) {
                BaseLevel base = source.getBaseLevel().orElse(null);
                if (base != null && toBase.isPresent() && base.equals(toBase.get())) {
                    SaveRestoreService.getInstance().execute("Load beamline sets",
                        () -> reloadBeamlineSets(base, toBranch));
                } else if (base == null && !toBase.isPresent()) {
                    SaveRestoreService.getInstance().execute("Load beamline sets",
                        () -> reloadBeamlineSets(null, toBranch));
                } else {
                    SaveRestoreService.getInstance().execute("Load base levels", () -> readBaseLevels(true));
                }
            }
            // if branches are different, do nothing
        }
    };
    private final IWorkbenchPart owner;

    /**
     * Creates a new selector for the workbench part.
     *
     * @param owner the part that owns this selector
     */
    public Selector(IWorkbenchPart owner) {
        this.owner = owner;
        SaveRestoreService.getInstance().addPropertyChangeListener(SaveRestoreService.SELECTED_DATA_PROVIDER, (e) -> {
            DataProviderWrapper oldValue = (DataProviderWrapper) e.getOldValue();
            DataProviderWrapper newValue = (DataProviderWrapper) e.getNewValue();
            if (oldValue != null) {
                oldValue.provider.removeCompletionNotifier(notifier);
            }
            if (newValue != null) {
                newValue.provider.addCompletionNotifier(notifier);
            }
            ((ObjectProperty<List<BaseLevel>>) baseLevelsProperty())
                .set(Collections.unmodifiableList(new ArrayList<>(0)));
            ((ObjectProperty<List<BeamlineSet>>) beamlineSetsProperty())
                .set(Collections.unmodifiableList(new ArrayList<>(0)));
            ((ObjectProperty<List<Snapshot>>) snapshotsProperty())
                .set(Collections.unmodifiableList(new ArrayList<>(0)));
            allSnapshotsLoaded.set(false);
            readBranches(DEFAULT_BRANCH);
        });
    }

    private void reloadBeamlineSets(BaseLevel baseLevel, Branch branch) {
        try {
            final BeamlineSet[] beamlineSets = SaveRestoreService.getInstance().getSelectedDataProvider().provider
                .getBeamlineSets(Optional.ofNullable(baseLevel), branch);
            runOnGUIThread(() -> ((ObjectProperty<List<BeamlineSet>>) beamlineSetsProperty())
                .set(Collections.unmodifiableList(Arrays.asList(beamlineSets))));
        } catch (DataProviderException e) {
            ActionManager.reportException(e, owner.getSite().getShell());
        }
    }

    private void readBranches(final Branch branchToSelect) {
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().provider;
        if (provider.areBranchesSupported()) {
            SaveRestoreService.getInstance().execute("Load branches", () -> {
                try {
                    final Branch[] branches = provider.getBranches();
                    runOnGUIThread(() -> {
                        ((ObjectProperty<List<Branch>>) branchesProperty())
                            .set(Collections.unmodifiableList(Arrays.asList(branches)));
                        selectedBranchProperty().set(branchToSelect);
                    });
                } catch (DataProviderException e) {
                    ActionManager.reportException(e, owner.getSite().getShell());
                }
            });
        } else {
            ((ObjectProperty<List<Branch>>) branchesProperty())
                .set(Collections.unmodifiableList(Arrays.asList(DEFAULT_BRANCH)));
            selectedBranchProperty().set(branchToSelect);
        }
    }

    /**
     * @return the property containing the list of all available branches
     */
    public ReadOnlyObjectProperty<List<Branch>> branchesProperty() {
        if (branches == null) {
            branches = new SimpleObjectProperty<>(Collections.unmodifiableList(new ArrayList<>()));
        }
        return branches;
    }

    /**
     * @return the property that contains the selected branch
     */
    public ObjectProperty<Branch> selectedBranchProperty() {
        if (selectedBranch == null) {
            selectedBranch = new SimpleObjectProperty<Branch>() {
                @Override
                public void set(Branch newValue) {
                    if (newValue == null) {
                        newValue = DEFAULT_BRANCH;
                    }
                    Branch oldValue = get();
                    if (oldValue != null && oldValue.equals(newValue)) {
                        readBaseLevels(false);
                        return;
                    }
                    super.set(newValue);

                }
            };
            selectedBranch.addListener((a, o, n) -> {
                readBaseLevels(false);
            });
        }
        return selectedBranch;
    }

    private void readBaseLevels(boolean reloadBeamlineSets) {
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().provider;
        if (provider.areBaseLevelsSupported()) {
            final Branch branch = selectedBranchProperty().get();
            SaveRestoreService.getInstance().execute("Load base levels", () -> {
                try {
                    final BaseLevel[] baseLevels = provider.getBaseLevels(branch);
                    runOnGUIThread(() -> ((ObjectProperty<List<BaseLevel>>) baseLevelsProperty())
                        .set(Collections.unmodifiableList(Arrays.asList(baseLevels))));
                    if (reloadBeamlineSets) {
                        readBeamlineSets();
                    }
                } catch (DataProviderException e) {
                    ActionManager.reportException(e, owner.getSite().getShell());
                }
            });
        } else {
            readBeamlineSets();
        }
    }

    /**
     * @return property containing all available base levels for the current branch
     */
    public ReadOnlyObjectProperty<List<BaseLevel>> baseLevelsProperty() {
        if (baseLevels == null) {
            baseLevels = new SimpleObjectProperty<>(Collections.unmodifiableList(new ArrayList<>()));
        }
        return baseLevels;
    }

    /**
     * @return property containing the selected base level
     */
    public ObjectProperty<BaseLevel> selectedBaseLevelProperty() {
        if (selectedBaseLevel == null) {
            selectedBaseLevel = new SimpleObjectProperty<>();
            selectedBaseLevel.addListener((a, o, n) -> {
                if (n == null
                    && SaveRestoreService.getInstance().getSelectedDataProvider().provider.areBaseLevelsSupported()) {
                    return;
                }
                readBeamlineSets();
            });
        }
        return selectedBaseLevel;
    }

    private void readBeamlineSets() {
        selectedBeamlineSetProperty().set(null);
        final BaseLevel baseLevel = selectedBaseLevelProperty().get();
        final Branch branch = selectedBranchProperty().get();
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().provider;
        SaveRestoreService.getInstance().execute("Load beamline sets", () -> {
            try {
                final BeamlineSet[] beamlineSets = provider.getBeamlineSets(Optional.ofNullable(baseLevel), branch);
                runOnGUIThread(() -> ((ObjectProperty<List<BeamlineSet>>) beamlineSetsProperty())
                    .set(Collections.unmodifiableList(Arrays.asList(beamlineSets))));
            } catch (DataProviderException e) {
                ActionManager.reportException(e, owner.getSite().getShell());
            }
        });
    }

    /**
     * @return property containing all available beamline sets for the selected branch and base level
     */
    public ReadOnlyObjectProperty<List<BeamlineSet>> beamlineSetsProperty() {
        if (beamlineSets == null) {
            beamlineSets = new SimpleObjectProperty<>(Collections.unmodifiableList(new ArrayList<>()));
        }
        return beamlineSets;
    }

    /**
     * @return property containing the selected beamline set
     */
    public ObjectProperty<BeamlineSet> selectedBeamlineSetProperty() {
        if (selectedBeamlineSet == null) {
            selectedBeamlineSet = new SimpleObjectProperty<BeamlineSet>();
            selectedBeamlineSet.addListener((a, o, n) -> {
                allSnapshotsLoaded.set(false);
                ((ObjectProperty<List<Snapshot>>) snapshotsProperty())
                    .set(Collections.unmodifiableList(new ArrayList<>(0)));
                lastSnapshot = null;
                if (n != null) {
                    readSnapshots(true, false);
                }
            });
        }
        return selectedBeamlineSet;
    }

    /**
     * Loads the snapshots from the repository. Either all snapshots will be loaded or only the number specified by the
     * {@link SaveRestoreService#getNumberOfSnapshots()}. The snapshots will also be loaded from the head of the
     * repository (the latest snapshot) or from the last snapshot that is already part of this selector.
     *
     * @param fromHead true if the snapshots should be loaded from the head backward or false if snapshots should be
     *            loaded from the last received snapshot backwards
     * @param all true if all snapshots should be loaded or false if only a subset specified by the preferences
     *
     * @see SaveRestoreService#getNumberOfSnapshots()
     */
    public void readSnapshots(boolean fromHead, final boolean all) {
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().provider;
        final BeamlineSet set = selectedBeamlineSetProperty().get();
        final Snapshot snap = fromHead ? null : lastSnapshot;
        SaveRestoreService.getInstance().execute("Load snapshots", () -> {
            try {
                final Snapshot[] snapshots = provider.getSnapshots(set, all, Optional.ofNullable(snap));
                final List<Snapshot> allSnapshots = new ArrayList<>(snapshotsProperty().get());
                for (Snapshot s : snapshots) {
                    allSnapshots.add(s);
                }
                lastSnapshot = allSnapshots.isEmpty() ? null : allSnapshots.get(allSnapshots.size() - 1);
                Collections.sort(allSnapshots);
                runOnGUIThread(() -> {
                    allSnapshotsLoaded.set(snapshots.length == 0);
                    ((ObjectProperty<List<Snapshot>>) snapshotsProperty())
                        .set(Collections.unmodifiableList(allSnapshots));
                });
            } catch (DataProviderException e) {
                ActionManager.reportException(e, owner.getSite().getShell());
            }
        });
    }

    /**
     * @return the property that specifies if all snapshots have already been loaded for the selected beamline set or
     *         not
     */
    public ReadOnlyBooleanProperty allSnapshotsLoadedProperty() {
        return allSnapshotsLoaded;
    }

    /**
     * @return property containing all available snapshots for the selected branch, base level, and beamline set
     */
    public ReadOnlyObjectProperty<List<Snapshot>> snapshotsProperty() {
        if (snapshots == null) {
            snapshots = new SimpleObjectProperty<>(Collections.unmodifiableList(new ArrayList<>()));
        }
        return snapshots;
    }

    /**
     * @return true if the selected branch is the default branch or false otherwise
     */
    public boolean isDefaultBranch() {
        return DEFAULT_BRANCH.equals(selectedBranchProperty().get());
    }

    /**
     * Returns null if the name is an acceptable storage name for a base level. If it is not acceptable a string
     * explaining the problem is returned.
     *
     * @param storageName the proposed name
     * @return null if proposed name is OK, or string describing the problem if not OK
     */
    public static String validateBaseLevelName(String storageName) {
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().provider;
        if (provider.areBaseLevelsSupported()) {
            return ExtensionPointLoader.getInstance().getBaseLevelValidator().map((e) -> e.validate(storageName))
                .orElse(null);
        } else {
            return null;
        }
    }

    private void runOnGUIThread(Runnable r) {
        Platform.runLater(r);
    }
}
