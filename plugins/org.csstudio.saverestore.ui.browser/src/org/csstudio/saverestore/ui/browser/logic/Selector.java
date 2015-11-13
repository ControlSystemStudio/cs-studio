package org.csstudio.saverestore.ui.browser.logic;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.csstudio.saverestore.BaseLevel;
import org.csstudio.saverestore.BeamlineSet;
import org.csstudio.saverestore.DataProvider;
import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.Engine;
import org.csstudio.saverestore.Snapshot;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * <code>Selector</code> provides the selection logic and model for the browser view. It contains the available
 * and selected items for each individual section.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class Selector {

    public static final String MASTER_BRANCH = "master";

    private StringProperty selectedBranch;
    private ObjectProperty<BaseLevel> selectedBaseLevel;
    private ObjectProperty<BeamlineSet> selectedBeamlineSet;
    private ObjectProperty<List<BaseLevel>> baseLevels;
    private ObjectProperty<List<String>> branches;
    private ObjectProperty<List<BeamlineSet>> beamlineSets;
    private ObjectProperty<List<Snapshot>> snapshots;

    public Selector() {
        Engine.getInstance().addPropertyChangeListener(Engine.SELECTED_DATA_PROVIDER, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                readBranches(MASTER_BRANCH);
            }
        });
    }

    void reloadBeamlineSets(BaseLevel baseLevel, String branch) {
        try {
            final BeamlineSet[] beamlineSets = Engine.getInstance().getSelectedDataProvider().provider
                    .getBeamlineSets(baseLevel, branch);
            executeFX(() -> ((ObjectProperty<List<BeamlineSet>>) beamlineSetsProperty())
                    .set(Collections.unmodifiableList(Arrays.asList(beamlineSets))));
        } catch (DataProviderException e) {
            e.printStackTrace();
            //TODO
        }
    }

    public void readBranches(final String branchToSelect) {
        final DataProvider provider = Engine.getInstance().getSelectedDataProvider().provider;
        if (provider.areBranchesSupported()) {
            Engine.getInstance().execute(() -> {
                try {
                    final String[] branches = provider.getBranches();
                    executeFX(() -> {
                        ((ObjectProperty<List<String>>) branchesProperty())
                                .set(Collections.unmodifiableList(Arrays.asList(branches)));
                        selectedBranchProperty().set(branchToSelect);
                    });
                } catch (DataProviderException e) {
                    e.printStackTrace();
                    //TODO
                }
            });
        } else {
            ((ObjectProperty<List<String>>) branchesProperty())
                    .set(Collections.unmodifiableList(Arrays.asList(MASTER_BRANCH)));
            selectedBranchProperty().set(branchToSelect);
        }
    }

    public ReadOnlyObjectProperty<List<String>> branchesProperty() {
        if (branches == null) {
            branches = new SimpleObjectProperty<>(Collections.unmodifiableList(new ArrayList<>()));
        }
        return branches;
    }

    public StringProperty selectedBranchProperty() {
        if (selectedBranch == null) {
            selectedBranch = new SimpleStringProperty() {
                @Override
                public void set(String newValue) {
                    if (newValue == null) {
                        newValue = MASTER_BRANCH;
                    }
                    String oldValue = get();
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
        final DataProvider provider = Engine.getInstance().getSelectedDataProvider().provider;
        if (provider.areBaseLevelsSupported()) {
            final String branch = selectedBranchProperty().get();
            Engine.getInstance().execute(() -> {
                try {
                    final BaseLevel[] baseLevels = provider.getBaseLevels(branch);
                    executeFX(() -> ((ObjectProperty<List<BaseLevel>>) baseLevelsProperty())
                            .set(Collections.unmodifiableList(Arrays.asList(baseLevels))));
                } catch (DataProviderException e) {
                    e.printStackTrace();
                    //TODO
                }
            });
        } else {
            readBeamlineSets();
        }
    }

    public ReadOnlyObjectProperty<List<BaseLevel>> baseLevelsProperty() {
        if (baseLevels == null) {
            baseLevels = new SimpleObjectProperty<>(Collections.unmodifiableList(new ArrayList<>()));
        }
        return baseLevels;
    }

    public ObjectProperty<BaseLevel> selectedBaseLevelProperty() {
        if (selectedBaseLevel == null) {
            selectedBaseLevel = new SimpleObjectProperty<>();
            selectedBaseLevel.addListener((a, o, n) -> {
                readBeamlineSets();
            });
        }
        return selectedBaseLevel;
    }

    private void readBeamlineSets() {
        selectedBeamlineSetProperty().set(null);
        final BaseLevel baseLevel = selectedBaseLevelProperty().get();
        final String branch = selectedBranchProperty().get();
        final DataProvider provider = Engine.getInstance().getSelectedDataProvider().provider;
        Engine.getInstance().execute(() -> {
            try {
                final BeamlineSet[] beamlineSets = provider.getBeamlineSets(baseLevel,branch);
                executeFX(() -> ((ObjectProperty<List<BeamlineSet>>) beamlineSetsProperty())
                        .set(Collections.unmodifiableList(Arrays.asList(beamlineSets))));
            } catch (DataProviderException e) {
                e.printStackTrace();
                //TODO
            }
        });
    }

    public ReadOnlyObjectProperty<List<BeamlineSet>> beamlineSetsProperty() {
        if (beamlineSets == null) {
            beamlineSets = new SimpleObjectProperty<>(Collections.unmodifiableList(new ArrayList<>()));
        }
        return beamlineSets;
    }

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
        final DataProvider provider = Engine.getInstance().getSelectedDataProvider().provider;
        Engine.getInstance().execute(() -> {
            try {
                final Snapshot[] snapshots = provider.getSnapshots(selectedBeamlineSetProperty().get());
                executeFX(() -> ((ObjectProperty<List<Snapshot>>) snapshotsProperty())
                        .set(Collections.unmodifiableList(Arrays.asList(snapshots))));
            } catch (DataProviderException e) {
                e.printStackTrace();
                //TODO
            }
        });
    }

    public ReadOnlyObjectProperty<List<Snapshot>> snapshotsProperty() {
        if (snapshots == null) {
            snapshots = new SimpleObjectProperty<>(Collections.unmodifiableList(new ArrayList<>()));
        }
        return snapshots;
    }

    public boolean isMainBranch() {
        return MASTER_BRANCH.equals(selectedBranch.get());
    }

    private void executeFX(Runnable r) {
        Platform.runLater(r);
    }
}
