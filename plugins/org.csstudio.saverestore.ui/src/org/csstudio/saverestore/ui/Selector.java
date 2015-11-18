package org.csstudio.saverestore.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

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
import org.csstudio.ui.fx.util.FXMessageDialog;
import org.csstudio.ui.fx.util.InputValidator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
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

    public static final String BASE_LEVEL_VALIDATOR_EXT_POINT = "org.csstudio.saverestore.ui.baselevelvalidator";

    private Optional<InputValidator<String>> baseLevelValidator;

    @SuppressWarnings("unchecked")
    private Optional<InputValidator<String>> getBaseLevelValidator() {
        if (baseLevelValidator == null) {
            InputValidator<String> bb = null;
            try {
                IExtensionRegistry extReg = org.eclipse.core.runtime.Platform.getExtensionRegistry();
                IConfigurationElement[] confElements = extReg
                        .getConfigurationElementsFor(BASE_LEVEL_VALIDATOR_EXT_POINT);
                for (IConfigurationElement element : confElements) {
                    bb = (InputValidator<String>) element.createExecutableExtension("validator");
                }

            } catch (CoreException e) {
                SaveRestoreService.LOGGER.log(Level.SEVERE, "Save and restore base level browser could not be loaded.",
                        e);
                baseLevelValidator = null;
            }
            baseLevelValidator = Optional.ofNullable(bb);

        }
        return baseLevelValidator;
    }

    private static final Branch DEFAULT_BRANCH = new Branch("master", "master");

    private ObjectProperty<Branch> selectedBranch;
    private ObjectProperty<BaseLevel> selectedBaseLevel;
    private ObjectProperty<BeamlineSet> selectedBeamlineSet;
    private ObjectProperty<List<BaseLevel>> baseLevels;
    private ObjectProperty<List<Branch>> branches;
    private ObjectProperty<List<BeamlineSet>> beamlineSets;
    private ObjectProperty<List<Snapshot>> snapshots;
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
                SaveRestoreService.getInstance().execute("Synchronise", () -> {
                    readBranches(selectedBranchProperty().get());
                });
            });
        }

        @Override
        public void snapshotTagged(Snapshot snapshot) {
            List<Snapshot> snp = snapshotsProperty().get();
            final List<Snapshot> newV = new ArrayList<>(snp.size());
            snp.forEach(k -> {
                if(k.almostEquals(snapshot)) {
                    newV.add(snapshot);
                } else {
                    newV.add(k);
                }
            });
            runOnGUIThread(() -> ((SimpleObjectProperty<List<Snapshot>>) snapshotsProperty())
                    .set(Collections.unmodifiableList(newV)));
        }

        @Override
        public void snapshotSaved(VSnapshot snapshot) {
            List<Snapshot> snp = snapshotsProperty().get();
            List<Snapshot> newV = new ArrayList<>();
            newV.add(snapshot.getSnapshot().get());
            newV.addAll(snp);
            runOnGUIThread(() -> ((SimpleObjectProperty<List<Snapshot>>) snapshotsProperty())
                    .set(Collections.unmodifiableList(newV)));
        }

        @Override
        public void branchCreated(Branch newBranch) {
            SaveRestoreService.getInstance().execute("Load branches", () -> readBranches(newBranch));
        }

        @Override
        public void beamlineSaved(BeamlineSetData set) {
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
        public void beamlineDeleted(BeamlineSet set) {
            List<BeamlineSet> sets = beamlineSetsProperty().get();
            final List<BeamlineSet> newSets = new ArrayList<>(sets.size());
            sets.forEach(e -> {
                if (!e.equals(set)) {
                    newSets.add(e);
                }
            });
            runOnGUIThread(() -> ((SimpleObjectProperty<List<BeamlineSet>>) beamlineSetsProperty())
                    .set(Collections.unmodifiableList(newSets)));
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
            ((ObjectProperty<List<BaseLevel>>) baseLevelsProperty()).set(new ArrayList<>(0));
            ((ObjectProperty<List<BeamlineSet>>) beamlineSetsProperty()).set(new ArrayList<>(0));
            ((ObjectProperty<List<Snapshot>>) snapshotsProperty()).set(new ArrayList<>(0));
            readBranches(DEFAULT_BRANCH);
        });
    }

    private void reloadBeamlineSets(BaseLevel baseLevel, Branch branch) {
        try {
            final BeamlineSet[] beamlineSets = SaveRestoreService.getInstance().getSelectedDataProvider().provider
                    .getBeamlineSets(baseLevel, branch);
            runOnGUIThread(() -> ((ObjectProperty<List<BeamlineSet>>) beamlineSetsProperty())
                    .set(Collections.unmodifiableList(Arrays.asList(beamlineSets))));
        } catch (DataProviderException e) {
            reportException(e, owner.getSite().getShell());
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
                    reportException(e, owner.getSite().getShell());
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
                        readBaseLevels();
                        return;
                    }
                    super.set(newValue);

                }
            };
            selectedBranch.addListener((a, o, n) -> {
                readBaseLevels();
            });
        }
        return selectedBranch;
    }

    private void readBaseLevels() {
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().provider;
        if (provider.areBaseLevelsSupported()) {
            final Branch branch = selectedBranchProperty().get();
            SaveRestoreService.getInstance().execute("Load base levels", () -> {
                try {
                    final BaseLevel[] baseLevels = provider.getBaseLevels(branch);
                    runOnGUIThread(() -> ((ObjectProperty<List<BaseLevel>>) baseLevelsProperty())
                            .set(Collections.unmodifiableList(Arrays.asList(baseLevels))));
                } catch (DataProviderException e) {
                    reportException(e, owner.getSite().getShell());
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
                if (n == null && SaveRestoreService.getInstance().getSelectedDataProvider().provider
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
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().provider;
        SaveRestoreService.getInstance().execute("Load beamline sets", () -> {
            try {
                final BeamlineSet[] beamlineSets = provider.getBeamlineSets(baseLevel, branch);
                runOnGUIThread(() -> ((ObjectProperty<List<BeamlineSet>>) beamlineSetsProperty())
                        .set(Collections.unmodifiableList(Arrays.asList(beamlineSets))));
            } catch (DataProviderException e) {
                reportException(e, owner.getSite().getShell());
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
                if (n == null) {
                    ((ObjectProperty<List<Snapshot>>) snapshotsProperty())
                            .set(Collections.unmodifiableList(new ArrayList<>(0)));
                } else {
                    readSnapshots();
                }
            });
        }
        return selectedBeamlineSet;
    }

    private void readSnapshots() {
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().provider;
        SaveRestoreService.getInstance().execute("Load snapshots", () -> {
            try {
                final Snapshot[] snapshots = provider.getSnapshots(selectedBeamlineSetProperty().get());
                runOnGUIThread(() -> ((ObjectProperty<List<Snapshot>>) snapshotsProperty())
                        .set(Collections.unmodifiableList(Arrays.asList(snapshots))));
            } catch (DataProviderException e) {
                reportException(e, owner.getSite().getShell());
            }
        });
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
        return DEFAULT_BRANCH.equals(selectedBranch.get());
    }

    /**
     * Returns null if the name is an acceptable storage name for a base level. If it is not acceptable a string
     * explaining the problem is returned.
     *
     * @param storageName the proposed name
     * @return null if proposed name is OK, or string describing the problem if not OK
     */
    public String validateBaseLevelName(String storageName) {
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().provider;
        if (provider.areBaseLevelsSupported()) {
            return getBaseLevelValidator().map((e) -> e.validate(storageName)).orElse(null);
        } else {
            return null;
        }
    }

    private void runOnGUIThread(Runnable r) {
        Platform.runLater(r);
    }

    /**
     * Report exception that happened during the selector action. The exception is logged and a message is displayed.
     * The call can be made by any thread. The thread will be blocked until the message dialog is closed.
     *
     * @param e the exception to report
     * @param shell the shell to use for the message dialog parent (cannot be null)
     */
    public static void reportException(Exception e, Shell shell) {
        SaveRestoreService.LOGGER.log(Level.WARNING, "Error accessing data storage", e);
        shell.getDisplay().syncExec(() -> {
            StringBuilder sb = new StringBuilder();
            sb.append(e.getMessage());
            if (e.getCause() != null) {
                sb.append('\n').append(e.getCause().getMessage());
            }
            FXMessageDialog.openError(shell, "Error accessing data storage", sb.toString());
        });
    }
}
