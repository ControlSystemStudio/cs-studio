package org.csstudio.saverestore.ui;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
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
import org.eclipse.jface.window.IShellProvider;

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
public class Selector implements CompletionNotifier {

    private static final Branch DEFAULT_BRANCH = new Branch();
    // these two are not final for testing purposes
    private static Executor UI_EXECUTOR = Platform::runLater;
    private static BiConsumer<String, Runnable> SERVICE_EXECUTOR = SaveRestoreService.getInstance()::execute;

    private Branch firstTimeBranch;

    private ObjectProperty<Branch> selectedBranch;
    private ObjectProperty<BaseLevel> selectedBaseLevel;
    private ObjectProperty<BeamlineSet> selectedBeamlineSet;
    private final ObjectProperty<List<BaseLevel>> baseLevels = new SimpleObjectProperty<>(
        Collections.unmodifiableList(new ArrayList<>(0)));
    private final ObjectProperty<List<Branch>> branches = new SimpleObjectProperty<>(
        Collections.unmodifiableList(new ArrayList<>(0)));
    private final ObjectProperty<List<BeamlineSet>> beamlineSets = new SimpleObjectProperty<>(
        Collections.unmodifiableList(new ArrayList<>(0)));
    private final ObjectProperty<List<Snapshot>> snapshots = new SimpleObjectProperty<>(
        Collections.unmodifiableList(new ArrayList<>(0)));
    private final BooleanProperty allSnapshotsLoaded = new SimpleBooleanProperty(false);
    private Snapshot lastSnapshot;
    private final IShellProvider shellProvider;

    private final PropertyChangeListener pcl = e -> {
        DataProviderWrapper oldValue = (DataProviderWrapper) e.getOldValue();
        DataProviderWrapper newValue = (DataProviderWrapper) e.getNewValue();
        if (oldValue != null) {
            oldValue.getProvider().removeCompletionNotifier(this);
        }
        init(newValue);
    };

    /**
     * Creates a new selector for the workbench part.
     *
     * @param shellProvider provides the shell which is used as a parent of all dialogs
     */
    public Selector(IShellProvider shellProvider) {
        this.shellProvider = shellProvider;
        SaveRestoreService.getInstance().addPropertyChangeListener(SaveRestoreService.SELECTED_DATA_PROVIDER, pcl);
        init(SaveRestoreService.getInstance().getSelectedDataProvider());
    }

    /**
     * Dispose of this selector. Once disposed, it can no longer be used.
     */
    public void dispose() {
        SaveRestoreService.getInstance().removePropertyChangeListener(SaveRestoreService.SELECTED_DATA_PROVIDER, pcl);
    }

    private void init(DataProviderWrapper wrapper) {
        if (wrapper != null) {
            wrapper.getProvider().addCompletionNotifier(this);
        }
        ((ObjectProperty<List<BaseLevel>>) baseLevelsProperty()).set(Collections.unmodifiableList(new ArrayList<>(0)));
        ((ObjectProperty<List<BeamlineSet>>) beamlineSetsProperty())
            .set(Collections.unmodifiableList(new ArrayList<>(0)));
        ((ObjectProperty<List<Snapshot>>) snapshotsProperty()).set(Collections.unmodifiableList(new ArrayList<>(0)));
        if (wrapper != null) {
            allSnapshotsLoaded.set(false);
            if (firstTimeBranch == null) {
                readBranches(DEFAULT_BRANCH);
            } else {
                Branch branch = firstTimeBranch;
                firstTimeBranch = null;
                readBranches(branch);
            }
        }
    }

    /**
     * Set the branch that is selected at startup.
     *
     * @param branch the default branch
     */
    public void setFirstTimeBranch(String branch) {
        if (branch == null || branch.isEmpty()) {
            return;
        }
        this.firstTimeBranch = new Branch(branch, branch);
    }

    private void reloadBeamlineSets(BaseLevel baseLevel, Branch branch) {
        try {
            final BeamlineSet[] theBeamlineSets = SaveRestoreService.getInstance().getSelectedDataProvider()
                .getProvider().getBeamlineSets(Optional.ofNullable(baseLevel), branch);
            UI_EXECUTOR.execute(() -> ((ObjectProperty<List<BeamlineSet>>) beamlineSetsProperty()).set(Collections
                .unmodifiableList(theBeamlineSets == null ? new ArrayList<>(0) : Arrays.asList(theBeamlineSets))));
        } catch (DataProviderException e) {
            ActionManager.reportException(e, shellProvider.getShell());
        }
    }

    private void readBranches(final Branch branchToSelect) {
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().getProvider();
        if (provider.areBranchesSupported()) {
            SERVICE_EXECUTOR.accept("Load branches", () -> {
                try {
                    final Branch[] theBranches = provider.getBranches();
                    // Check the available branches and the branch that needs to be selected
                    // Maybe the given branch is not really available (most likely because it is the default branch)
                    // In that case select the first available branch
                    Branch newBranchToSelect = null;
                    if (theBranches.length > 0) {
                        for (Branch b : theBranches) {
                            if (b.equals(branchToSelect)) {
                                newBranchToSelect = b;
                                break;
                            }
                        }
                        if (newBranchToSelect == null || branchToSelect != null && branchToSelect.isDefault()) {
                            newBranchToSelect = theBranches[0];
                        }
                    }
                    final Branch b = newBranchToSelect;
                    UI_EXECUTOR.execute(() -> {
                        ((ObjectProperty<List<Branch>>) branchesProperty())
                            .set(Collections.unmodifiableList(Arrays.asList(theBranches)));
                        selectedBranchProperty().set(b);
                    });
                } catch (DataProviderException e) {
                    ActionManager.reportException(e, shellProvider.getShell());
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
            selectedBranch.addListener((a, o, n) -> readBaseLevels(false));
        }
        return selectedBranch;
    }

    private void readBaseLevels(boolean reloadBeamlineSets) {
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().getProvider();
        if (provider.areBaseLevelsSupported()) {
            final Branch branch = selectedBranchProperty().get();
            SERVICE_EXECUTOR.accept("Load base levels", () -> {
                try {
                    final BaseLevel[] theBaseLevels = provider.getBaseLevels(branch);
                    UI_EXECUTOR.execute(() -> ((ObjectProperty<List<BaseLevel>>) baseLevelsProperty()).set(Collections
                        .unmodifiableList(theBaseLevels == null ? new ArrayList<>(0) : Arrays.asList(theBaseLevels))));
                    if (reloadBeamlineSets) {
                        readBeamlineSets();
                    }
                } catch (DataProviderException e) {
                    ActionManager.reportException(e, shellProvider.getShell());
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
        return baseLevels;
    }

    /**
     * @return property containing the selected base level
     */
    public ObjectProperty<BaseLevel> selectedBaseLevelProperty() {
        if (selectedBaseLevel == null) {
            selectedBaseLevel = new SimpleObjectProperty<>();
            selectedBaseLevel.addListener((a, o, n) -> {
                if (n == null && SaveRestoreService.getInstance().getSelectedDataProvider().getProvider()
                    .areBaseLevelsSupported()) {
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
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().getProvider();
        SERVICE_EXECUTOR.accept("Load beamline sets", () -> {
            try {
                final BeamlineSet[] theBeamlineSets = provider.getBeamlineSets(Optional.ofNullable(baseLevel), branch);
                UI_EXECUTOR.execute(() -> ((ObjectProperty<List<BeamlineSet>>) beamlineSetsProperty()).set(Collections
                    .unmodifiableList(theBeamlineSets == null ? new ArrayList<>(0) : Arrays.asList(theBeamlineSets))));
            } catch (DataProviderException e) {
                ActionManager.reportException(e, shellProvider.getShell());
            }
        });
    }

    /**
     * @return property containing all available beamline sets for the selected branch and base level
     */
    public ReadOnlyObjectProperty<List<BeamlineSet>> beamlineSetsProperty() {
        return beamlineSets;
    }

    /**
     * @return property containing the selected beamline set
     */
    public ObjectProperty<BeamlineSet> selectedBeamlineSetProperty() {
        if (selectedBeamlineSet == null) {
            selectedBeamlineSet = new SimpleObjectProperty<>();
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
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().getProvider();
        final BeamlineSet set = selectedBeamlineSetProperty().get();
        final Snapshot snap = fromHead ? null : lastSnapshot;
        SERVICE_EXECUTOR.accept("Load snapshots", () -> {
            try {
                final Snapshot[] theSnapshots = provider.getSnapshots(set, all, Optional.ofNullable(snap));
                final List<Snapshot> allSnapshots = new ArrayList<>(snapshotsProperty().get());
                for (Snapshot s : theSnapshots) {
                    allSnapshots.add(s);
                }
                lastSnapshot = allSnapshots.isEmpty() ? null : allSnapshots.get(allSnapshots.size() - 1);
                Collections.sort(allSnapshots);
                UI_EXECUTOR.execute(() -> {
                    allSnapshotsLoaded.set(theSnapshots.length == 0);
                    ((ObjectProperty<List<Snapshot>>) snapshotsProperty())
                        .set(Collections.unmodifiableList(allSnapshots));
                });
            } catch (DataProviderException e) {
                ActionManager.reportException(e, shellProvider.getShell());
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
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().getProvider();
        if (provider.areBaseLevelsSupported()) {
            return ExtensionPointLoader.getInstance().getBaseLevelValidator().map(e -> e.validate(storageName))
                .orElse(null);
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.CompletionNotifier#synchronised()
     */
    @Override
    public void synchronised() {
        UI_EXECUTOR.execute(() -> {
            ((ObjectProperty<BaseLevel>) selectedBaseLevelProperty()).set(null);
            ((ObjectProperty<BeamlineSet>) selectedBeamlineSetProperty()).set(null);
            ((ObjectProperty<List<BaseLevel>>) baseLevelsProperty())
                .set(Collections.unmodifiableList(new ArrayList<>(0)));
            ((ObjectProperty<List<BeamlineSet>>) beamlineSetsProperty())
                .set(Collections.unmodifiableList(new ArrayList<>(0)));
            ((ObjectProperty<List<Snapshot>>) snapshotsProperty())
                .set(Collections.unmodifiableList(new ArrayList<>(0)));
            allSnapshotsLoaded.set(false);
            SERVICE_EXECUTOR.accept("Synchronise", () -> readBranches(selectedBranchProperty().get()));
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.CompletionNotifier#snapshotTagged(org.csstudio.saverestore.data.Snapshot)
     */
    @Override
    public void snapshotTagged(Snapshot snapshot) {
        List<Snapshot> snp = snapshotsProperty().get();
        final List<Snapshot> newV = snp.stream().map(e -> e.almostEquals(snapshot) ? snapshot : e)
            .collect(Collectors.toList());
        UI_EXECUTOR.execute(
            () -> ((SimpleObjectProperty<List<Snapshot>>) snapshotsProperty()).set(Collections.unmodifiableList(newV)));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.CompletionNotifier#snapshotSaved(org.csstudio.saverestore.data.VSnapshot)
     */
    @Override
    public void snapshotSaved(VSnapshot snapshot) {
        BeamlineSet set = selectedBeamlineSet.get();
        if (set != null && set.equals(snapshot.getBeamlineSet())) {
            List<Snapshot> snp = snapshotsProperty().get();
            final List<Snapshot> newV = new ArrayList<>(snp.size() + 1);
            newV.add(snapshot.getSnapshot().get());
            newV.addAll(snp);
            Collections.sort(newV);
            UI_EXECUTOR.execute(
                () -> ((SimpleObjectProperty<List<Snapshot>>) snapshotsProperty()).set(Collections.unmodifiableList(newV)));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.CompletionNotifier#branchCreated(org.csstudio.saverestore.data.Branch)
     */
    @Override
    public void branchCreated(Branch newBranch) {
        SERVICE_EXECUTOR.accept("Load branches", () -> readBranches(newBranch));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.CompletionNotifier#beamlineSetSaved(org.csstudio.saverestore.data.BeamlineSetData)
     */
    @Override
    public void beamlineSetSaved(BeamlineSetData set) {
        final Branch branch = selectedBranchProperty().get();
        if (!set.getDescriptor().getBranch().equals(branch)) {
            return;
        }
        final BaseLevel base = selectedBaseLevelProperty().get();
        Optional<BaseLevel> bl = set.getDescriptor().getBaseLevel();
        if (bl.isPresent() && base != null && !bl.get().getStorageName().equals(base.getStorageName())) {
            return;
        }
        SERVICE_EXECUTOR.accept("Load beamline sets", () -> reloadBeamlineSets(base, branch));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.CompletionNotifier#beamlineSetDeleted(org.csstudio.saverestore.data.BeamlineSet)
     */
    @Override
    public void beamlineSetDeleted(BeamlineSet set) {
        List<BeamlineSet> sets = beamlineSetsProperty().get();
        final List<BeamlineSet> newSets = sets.stream().filter(e -> !e.equals(set)).collect(Collectors.toList());
        UI_EXECUTOR.execute(() -> ((SimpleObjectProperty<List<BeamlineSet>>) beamlineSetsProperty())
            .set(Collections.unmodifiableList(newSets)));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.CompletionNotifier#dataImported(org.csstudio.saverestore.data.BeamlineSet,
     * org.csstudio.saverestore.data.Branch, java.util.Optional)
     */
    @Override
    public void dataImported(BeamlineSet source, Branch toBranch, final Optional<BaseLevel> toBase) {
        Branch selected = selectedBranchProperty().get();
        if (selected.equals(toBranch)) {
            BaseLevel base = selectedBaseLevel.get();
            if (base != null && toBase.isPresent() && base.equals(toBase.get())) {
                SERVICE_EXECUTOR.accept("Load beamline sets", () -> reloadBeamlineSets(base, toBranch));
            } else if (base == null && !toBase.isPresent()) {
                SERVICE_EXECUTOR.accept("Load beamline sets", () -> reloadBeamlineSets(null, toBranch));
            } else {
                SERVICE_EXECUTOR.accept("Load base levels", () -> readBaseLevels(true));
            }
        }
        // if branches are different, do nothing
    }
}
