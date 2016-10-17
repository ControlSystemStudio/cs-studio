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
package org.csstudio.saverestore.ui.browser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;

import org.csstudio.saverestore.DataProvider;
import org.csstudio.saverestore.DataProvider.ImportType;
import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.DataProviderWrapper;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.SearchCriterion;
import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.SaveSet;
import org.csstudio.saverestore.data.SaveSetData;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.ui.ActionManager;
import org.csstudio.saverestore.ui.SaveSetEditor;
import org.csstudio.saverestore.ui.SaveSetEditorInput;
import org.csstudio.saverestore.ui.Selector;
import org.csstudio.ui.fx.util.FXMessageDialog;
import org.csstudio.ui.fx.util.FXTextAreaInputDialog;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

import javafx.application.Platform;

/**
 *
 * <code>ActionManager</code> executes the actions that a user can trigger through the browser UI.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class BrowserActionManager extends ActionManager {

    private final Selector selector;

    /**
     * Constructs a new manager.
     *
     * @param selector the selector that provides the data
     * @param owner the owner view
     */
    public BrowserActionManager(Selector selector, IWorkbenchPart owner) {
        super(owner);
        this.selector = selector;
    }

    /**
     * Import the save sets and snapshots from the provided source to the current branch and base level. Before
     * initiating the import the user has the option to chose whether to import any snapshots as well.
     *
     * @param source the source of data
     */
    public void importFrom(final SaveSet source) {
        if (source == null) {
            throw new IllegalArgumentException("The source location cannot be null.");
        }
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().getProvider();
        if (!provider.isImportSupported()) {
            return;
        }
        final Branch currentBranch = selector.selectedBranchProperty().get();
        final BaseLevel baseLevel = selector.selectedBaseLevelProperty().get();
        if (baseLevel == null && source.getBaseLevel().isPresent()) {
            throw new IllegalArgumentException("Cannot import to unknown base level.");
        } else if (!source.getBaseLevel().isPresent() && baseLevel != null) {
            throw new IllegalArgumentException("Cannot import from an unknown base level.");
        } else if (currentBranch == null && source.getBranch() != null) {
            throw new IllegalArgumentException("Cannot import to unknown branch.");
        } else if (source.getBranch() == null && currentBranch != null) {
            throw new IllegalArgumentException("Cannot import from an unknown branch.");
        }
        int ans = new FXMessageDialog(owner.getSite().getShell(), "Import Snapshots", null,
            "Do you want to import any snapshots for the selected save sets?", FXMessageDialog.DialogType.QUESTION,
            new String[] { "No", "Last Only", "All", "Cancel" }, 0).open();
        if (ans == 3) {
            // cancelled
            return;
        }
        final ImportType type = ans == 0 ? ImportType.SAVE_SET
            : ans == 1 ? ImportType.LAST_SNAPSHOT : ImportType.ALL_SNAPSHOTS;
        SaveRestoreService.getInstance().execute("Import Data", () -> {
            try {
                if (provider.importData(source, currentBranch, Optional.ofNullable(baseLevel), type)) {
                    SaveRestoreService.LOGGER.log(Level.FINE, "Successfully imported data from {0}.",
                        new Object[] { source.getFullyQualifiedName() });
                } else {
                    SaveRestoreService.LOGGER.log(Level.FINE, "Failed to import data from {0}.",
                        new Object[] { source.getFullyQualifiedName() });
                }
            } catch (DataProviderException e) {
                ActionManager.reportException(e, owner.getSite());
            }
        });
    }

    /**
     * Tag the snapshot with a specific tag name and tag message.
     *
     * @param snapshot the snapshot to tag
     * @param tagName the name of the tag
     * @param tagMessage the tag message
     */
    public void tagSnapshot(final Snapshot snapshot, final String tagName, final String tagMessage) {
        if (snapshot == null) {
            throw new IllegalArgumentException("Snapshot is not selected.");
        } else if (tagName == null) {
            throw new IllegalArgumentException("Tag name not provided.");
        }
        final DataProvider provider = SaveRestoreService.getInstance()
            .getDataProvider(snapshot.getSaveSet().getDataProviderId()).getProvider();
        if (!provider.isTaggingSupported()) {
            return;
        }
        SaveRestoreService.getInstance().execute("Tag Snapshot", () -> {
            try {
                provider.tagSnapshot(snapshot, Optional.of(tagName), Optional.of(tagMessage));
                SaveRestoreService.LOGGER.log(Level.FINE, "Successfully tagged snapshot {0}: {1}.",
                    new Object[] { snapshot.getSaveSet().getFullyQualifiedName(), snapshot.getDate() });
            } catch (DataProviderException e) {
                ActionManager.reportException(e, owner.getSite());
            }
        });
    }

    /**
     * Opens an empty save set editor.
     */
    public void newSaveSet() {
        DataProviderWrapper wrapper = SaveRestoreService.getInstance().getSelectedDataProvider();
        if (!wrapper.getProvider().isSaveSetSavingSupported()) {
            return;
        }
        final Branch branch = selector.selectedBranchProperty().get();
        final BaseLevel base = selector.selectedBaseLevelProperty().get();
        final String dataProvider = wrapper.getId();
        SaveRestoreService.getInstance().execute("Load save set data", () -> {
            SaveSet set = new SaveSet(branch, Optional.ofNullable(base), new String[] { "SaveSet" },
                dataProvider);
            SaveSetData data = new SaveSetData(set, new ArrayList<>(0), new ArrayList<>(0), new ArrayList<>(0),
                "");
            owner.getSite().getShell().getDisplay().asyncExec(() -> {
                try {
                    owner.getSite().getPage().openEditor(new SaveSetEditorInput(data), SaveSetEditor.ID);
                } catch (PartInitException e) {
                    SaveRestoreService.LOGGER.log(Level.SEVERE,
                        "Could not find or instantiate a new save set editor.", e);
                }
            });
        });
    }

    /**
     * Delete the selected save set.
     *
     * @param set the set to delete
     */
    public void deleteSaveSet(final SaveSet set) {
        if (set == null) {
            throw new IllegalArgumentException("Save set is not selected.");
        }
        final DataProvider provider = SaveRestoreService.getInstance()
            .getDataProvider(set.getDataProviderId()).getProvider();
        if (!provider.isSaveSetSavingSupported()) {
            return;
        }
        SaveRestoreService.getInstance().execute("Delete save set", () -> {
            try {
                Optional<String> comment = FXTextAreaInputDialog.get(owner.getSite().getShell(), "Delete Comment",
                    "Provide a short comment why the set '" + set.getPathAsString() + "' is being deleted", "",
                    e -> (e == null || e.trim().length() < 10) ? "Comment should be at least 10 characters long."
                        : null);
                if (comment.isPresent()) {
                    if (provider.deleteSaveSet(set, comment.get())) {
                        SaveRestoreService.LOGGER.log(Level.FINE, "Successfully deleted save set {0}.",
                            new Object[] { set.getFullyQualifiedName() });
                    } else {
                        SaveRestoreService.LOGGER.log(Level.FINE, "Failed to delete save set {0}.",
                            new Object[] { set.getFullyQualifiedName() });
                    }
                }
            } catch (DataProviderException e) {
                ActionManager.reportException(e, owner.getSite());
            }
        });
    }

    /**
     * Delete the tag for the given snapshot.
     *
     * @param snapshot the snapshot to delete the tag for
     */
    public void deleteTag(final Snapshot snapshot) {
        if (snapshot == null) {
            throw new IllegalArgumentException("Snapshot not selected.");
        } else if (!snapshot.getTagName().isPresent()) {
            throw new IllegalArgumentException("Selected snapshot is not tagged.");
        }
        final DataProvider provider = SaveRestoreService.getInstance()
            .getDataProvider(snapshot.getSaveSet().getDataProviderId()).getProvider();
        if (!provider.isTaggingSupported()) {
            return;
        }
        SaveRestoreService.getInstance().execute("Remove tag", () -> {
            try {
                provider.tagSnapshot(snapshot, Optional.empty(), Optional.empty());
                SaveRestoreService.LOGGER.log(Level.FINE, "Successfully deleted the tag from {0}: {1}.",
                    new Object[] { snapshot.getSaveSet().getFullyQualifiedName(), snapshot.getDate() });
            } catch (DataProviderException e) {
                ActionManager.reportException(e, owner.getSite());
            }
        });
    }

    /**
     * Create a new branch.
     *
     * @param branchName the name of the branch to create
     */
    public void createNewBranch(final String branchName) {
        if (branchName == null || branchName.trim().isEmpty()) {
            throw new IllegalArgumentException("Branch name cannot be null or empty.");
        }
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().getProvider();
        if (!provider.areBranchesSupported()) {
            return;
        }
        final Branch orgBranch = selector.selectedBranchProperty().get();
        SaveRestoreService.getInstance().execute("Create new branch", () -> {
            try {
                provider.createNewBranch(orgBranch, branchName);
                SaveRestoreService.LOGGER.log(Level.FINE, "Successfully created branch {0}.",
                    new Object[] { branchName });
            } catch (DataProviderException e) {
                ActionManager.reportException(e, owner.getSite());
            }
        });
    }

    /**
     * Synchronise the local repository with the remote one.
     */
    public void synchronise() {
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().getProvider();
        SaveRestoreService.getInstance().execute("Synchronise repository", () -> {
            try {
                if (provider.synchronise()) {
                    SaveRestoreService.LOGGER.log(Level.FINE, "Repository synchronised.");
                } else {
                    SaveRestoreService.LOGGER.log(Level.FINE, "Failed to synchronise repository.");
                }
            } catch (DataProviderException e) {
                ActionManager.reportException(e, owner.getSite());
            }
        });
    }

    /**
     * Reset repository to the state of the central repository.
     */
    public void resetRepository() {
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().getProvider();
        if (!provider.isReinitSupported()) {
            return;
        }
        SaveRestoreService.getInstance().execute("Reset repository", () -> {
            try {
                if (provider.reinitialise()) {
                    SaveRestoreService.LOGGER.log(Level.FINE, "Repository reinitialised.");
                } else {
                    SaveRestoreService.LOGGER.log(Level.FINE, "Failed to reinitialise repository.");
                }
            } catch (DataProviderException e) {
                ActionManager.reportException(e, owner.getSite());
            }
        });
    }

    /**
     * Search for snapshots that match the given expression and criteria. Upon completion the consumer receives all
     * found snapshots (the consumer is updated on UI thread).
     *
     * @param expression the expression to search for
     * @param criteria the criteria or fields on which to perform the search
     * @param start the start date of the time window in which to search
     * @param end the end date of the time window in which to search
     * @param consumer the consumer that receives the results when search completes
     */
    public void searchForSnapshots(final String expression, final List<SearchCriterion> criteria,
        final Optional<Date> start, final Optional<Date> end, final Consumer<List<Snapshot>> consumer) {
        final DataProvider provider = SaveRestoreService.getInstance().getSelectedDataProvider().getProvider();
        if (!provider.isSearchSupported()) {
            return;
        }
        final Branch branch = selector.selectedBranchProperty().get();
        SaveRestoreService.getInstance().execute("Search for snapshots", () -> {
            try {
                Snapshot[] searchResult = provider.findSnapshots(expression, branch, criteria, start, end);
                Platform.runLater(() -> consumer.accept(Arrays.asList(searchResult)));
            } catch (DataProviderException e) {
                ActionManager.reportException(e, owner.getSite());
            }
        });
    }
}
