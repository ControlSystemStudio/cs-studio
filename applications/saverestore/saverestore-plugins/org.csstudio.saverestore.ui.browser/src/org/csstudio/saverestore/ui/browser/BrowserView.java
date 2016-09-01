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

import static org.csstudio.ui.fx.util.FXUtilities.setGridConstraints;

import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BooleanSupplier;

import org.csstudio.saverestore.DataProviderWrapper;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.SaveSet;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.ui.Activator;
import org.csstudio.saverestore.ui.Selector;
import org.csstudio.saverestore.ui.util.SnapshotDataFormat;
import org.csstudio.ui.fx.util.FXMessageDialog;
import org.csstudio.ui.fx.util.FXTaggingDialog;
import org.csstudio.ui.fx.util.UnfocusableButton;
import org.csstudio.ui.fx.util.UnfocusableToggleButton;
import org.eclipse.fx.ui.workbench3.FXViewPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 *
 * <code>BrowserView</code> is the view that provides the browsing facilities for save and restore. The view consists of
 * an accordion panel, composed of three parts: isotopes, save sets and snapshots selector.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class BrowserView extends FXViewPart implements ISelectionProvider, IShellProvider {

    private static final String SETTINGS_SELECTED_BRANCH = "selectedBranch";
    private static final String SETTINGS_SELECTED_DATA_PROVIDER = "selectedDataProvider";
    private static final String SETTINGS_DEFAULT_BASE_LEVEL_BROWSER = "defaultBaseLevelBrowser";
    private static final String SETTINGS_BASE_LEVEL_FILTER_NOT_SELECTED = "baseLevelFilterNotSelected";

    private static final Image SAVE_SET_IMAGE = new Image(BrowserView.class.getResourceAsStream("/icons/txt.png"));

    private static class SaveSetWrapper {

        final SaveSet set;
        final String[] path;

        SaveSetWrapper(SaveSet set, String name) {
            this.set = set;
            this.path = name == null ? set.getPath() : name.split("\\/");
        }

        boolean isFolder() {
            return set == null;
        }

        @Override
        public String toString() {
            String p = path[path.length - 1];
            if (p.toLowerCase(Locale.UK).endsWith(".bms") && p.length() > 4) {
                return p.substring(0, p.length() - 4);
            } else {
                return p;
            }
        }

    }

    private static class SaveSetTreeItem extends TreeItem<SaveSetWrapper> {
        SaveSetTreeItem(SaveSetWrapper set) {
            super(set, new ImageView(SAVE_SET_IMAGE));
        }

        SaveSetTreeItem(String name) {
            super(new SaveSetWrapper(null, name));
        }
    }

    private DefaultBaseLevelBrowser defaultBaseLevelBrowser;
    private BaseLevelBrowser<BaseLevel> baseLevelBrowser;
    private TreeView<SaveSetWrapper> saveSetsTree;
    private ListView<Snapshot> snapshotsList;
    private TitledPane snapshotsPane;
    private TitledPane baseLevelPane;
    private TitledPane saveSetsPane;
    private Button importButton;
    private Button newButton;
    private VBox mainPane;
    private VBox dataPane;

    private final Selector selector = new Selector(this);
    private final BrowserActionManager actionManager = new BrowserActionManager(selector, this);

    private boolean searchMode;

    private Menu contextMenu;
    private Action deleteTagAction;

    private final List<ISelectionChangedListener> selectionChangedListener = new CopyOnWriteArrayList<>();

    private PropertyChangeListener dpl = e -> updateForDataProviderChange();

    /**
     * @return the selector bound to this view
     */
    public Selector getSelector() {
        return selector;
    }

    /**
     * @return the action manager bound to this view
     */
    public BrowserActionManager getActionManager() {
        return actionManager;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite)
     */
    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        getSite().setSelectionProvider(this);
    }

    @Override
    public void dispose() {
        SaveRestoreService.getInstance().removePropertyChangeListener(SaveRestoreService.SELECTED_DATA_PROVIDER, dpl);
        super.dispose();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.fx.ui.workbench3.FXViewPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        MenuManager menu = new MenuManager();
        deleteTagAction = new Action("Remove Tag") {
            @Override
            public void run() {
                DataProviderWrapper wrapper = SaveRestoreService.getInstance().getSelectedDataProvider();
                if (canExecute("Tag Snapshot", wrapper.getName() + " data provider does not support tagging.",
                    wrapper.getProvider()::isTaggingSupported)) {
                    Snapshot item = snapshotsList.getSelectionModel().getSelectedItem();
                    if (FXMessageDialog.openQuestion(getSite().getShell(), "Remove Tag",
                        "Are you sure you want to remove the tag '" + item.getTagName().get() + "' from snapshot '"
                            + item.getDate() + "'?")) {
                        actionManager.deleteTag(item);
                    }
                }
            }
        };
        menu.add(deleteTagAction);
        contextMenu = menu.createContextMenu(parent);
        parent.setMenu(contextMenu);
        getSite().registerContextMenu(menu, this);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, "org.csstudio.saverestore.ui.help.browser");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.fx.ui.workbench3.FXViewPart#createFxScene()
     */
    @Override
    protected Scene createFxScene() {
        BorderPane main = new BorderPane();
        Scene scene = new Scene(main);

        Node saveSets = createSaveSetsPane(scene);
        Node snapshots = createSnapshotsPane(scene);
        mainPane = new VBox();
        dataPane = new VBox();
        VBox.setVgrow(saveSets, Priority.ALWAYS);
        VBox.setVgrow(snapshots, Priority.ALWAYS);
        dataPane.getChildren().addAll(saveSets, snapshotsPane);
        VBox.setVgrow(dataPane, Priority.ALWAYS);
        saveSetsPane.setExpanded(true);
        snapshotsPane.setExpanded(true);
        Optional<BaseLevelBrowser<BaseLevel>> browser = new BaseLevelBrowserProvider().getBaseLevelBrowser();
        Node elements = createBaseLevelsPane(browser.orElse(null), scene);
        VBox.setVgrow(baseLevelPane, Priority.NEVER);
        mainPane.getChildren().setAll(elements, dataPane);
        baseLevelPane.setExpanded(true);

        main.setCenter(mainPane);
        init();
        return scene;
    }

    private Node createBaseLevelsPane(BaseLevelBrowser<BaseLevel> browser, Scene scene) {
        BorderPane content = new BorderPane();
        baseLevelBrowser = browser;
        defaultBaseLevelBrowser = new DefaultBaseLevelBrowser(this.getSite());

        IDialogSettings settings = Activator.getDefault().getDialogSettings();
        boolean useSpecialBrowser = !settings.getBoolean(SETTINGS_DEFAULT_BASE_LEVEL_BROWSER);
        if (baseLevelBrowser == null || !useSpecialBrowser) {
            content.setCenter(defaultBaseLevelBrowser.getFXContent());
        } else {
            content.setCenter(baseLevelBrowser.getFXContent());
        }
        if (browser == null) {
            browser = defaultBaseLevelBrowser;
        }
        baseLevelPane = new TitledPane(browser.getTitleFor(Optional.empty(), Optional.empty()), content);
        baseLevelPane.setMaxHeight(Double.MAX_VALUE);

        GridPane titleBox = new GridPane();
        titleBox.setHgap(5);
        Label titleText = new Label(browser.getTitleFor(Optional.empty(), Optional.empty()));
        titleText.textProperty().bind(baseLevelPane.textProperty());
        ToggleButton filterButton = new UnfocusableToggleButton("",
            new ImageView(new Image(BrowserView.class.getResourceAsStream("/icons/filter_ps.png"))));
        filterButton.setTooltip(new Tooltip("Disable non-existing"));
        filterButton.selectedProperty().addListener((a, o, n) -> {
            Activator.getDefault().getDialogSettings().put(SETTINGS_BASE_LEVEL_FILTER_NOT_SELECTED, !n);
            defaultBaseLevelBrowser.setShowOnlyAvailable(n);
            if (baseLevelBrowser != null) {
                baseLevelBrowser.setShowOnlyAvailable(n);
            }
        });

        setUpTitlePaneNode(titleText, true);
        setUpTitlePaneNode(filterButton, false);
        if (baseLevelBrowser == null) {
            titleBox.addRow(0, titleText, filterButton);
        } else {
            ToggleButton baseLevelPanelFilterButton = new UnfocusableToggleButton("",
                new ImageView(new Image(BrowserView.class.getResourceAsStream("/icons/Bookshelf16.gif"))));
            baseLevelPanelFilterButton
                .setTooltip(new Tooltip("Toggle between custom browser (" + baseLevelBrowser.getReadableName()
                    + ") and default browser (" + defaultBaseLevelBrowser.getReadableName() + ")"));
            baseLevelPanelFilterButton.selectedProperty().addListener((a, o, n) -> {
                Activator.getDefault().getDialogSettings().put(SETTINGS_DEFAULT_BASE_LEVEL_BROWSER, !n);
                if (n) {
                    content.setCenter(baseLevelBrowser.getFXContent());
                } else {
                    content.setCenter(defaultBaseLevelBrowser.getFXContent());
                }
            });
            setUpTitlePaneNode(baseLevelPanelFilterButton, false);
            titleBox.addRow(0, titleText, baseLevelPanelFilterButton, filterButton);
            baseLevelPanelFilterButton.selectedProperty().set(useSpecialBrowser);
        }
        titleBox.setMaxWidth(Double.MAX_VALUE);
        titleText.setMaxWidth(Double.MAX_VALUE);
        baseLevelPane.setGraphic(titleBox);
        baseLevelPane.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        titleBox.prefWidthProperty().bind(scene.widthProperty().subtract(34));

        boolean selected = !settings.getBoolean(SETTINGS_BASE_LEVEL_FILTER_NOT_SELECTED);
        filterButton.setSelected(selected);
        defaultBaseLevelBrowser.setShowOnlyAvailable(selected);
        if (baseLevelBrowser != null) {
            baseLevelBrowser.setShowOnlyAvailable(selected);
        }

        return baseLevelPane;
    }

    private Node createSaveSetsPane(Scene scene) {
        GridPane content = new GridPane();

        saveSetsTree = new TreeView<>();
        saveSetsTree.getSelectionModel().selectedItemProperty().addListener((a, o, n) -> selector
            .selectedSaveSetProperty().set(n == null || n.getValue().isFolder() ? null : n.getValue().set));
        saveSetsTree.setShowRoot(false);

        final ContextMenu popup = new ContextMenu();
        MenuItem deleteSetItem = new MenuItem("Delete...");
        deleteSetItem.setOnAction(e -> {
            popup.hide();
            DataProviderWrapper wrapper = SaveRestoreService.getInstance().getSelectedDataProvider();
            if (canExecute("Delete Save Set",
                wrapper.getName() + " data provider does not support deleting of save sets.",
                wrapper.getProvider()::isSaveSetSavingSupported)) {

                SaveSetTreeItem item = (SaveSetTreeItem) saveSetsTree.getSelectionModel().getSelectedItem();
                if (FXMessageDialog.openQuestion(getSite().getShell(), "Delete Save Set",
                    "Are you sure you want to delete save set '" + item.getValue().set.getPathAsString() + "'?")) {
                    actionManager.deleteSaveSet(item.getValue().set);
                }
            }
        });
        popup.getItems().add(deleteSetItem);
        saveSetsTree.setContextMenu(popup);
        saveSetsTree.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                SaveSetTreeItem item = (SaveSetTreeItem) saveSetsTree.getSelectionModel().getSelectedItem();
                if (item.getValue().set == null) {
                    popup.hide();
                } else {
                    popup.show(saveSetsTree, e.getScreenX(), e.getScreenY());
                }
            }
        });
        setGridConstraints(saveSetsTree, true, true, Priority.ALWAYS, Priority.ALWAYS);
        content.add(saveSetsTree, 0, 0);

        saveSetsPane = new TitledPane("Save Sets", content);
        saveSetsPane.setMaxHeight(Double.MAX_VALUE);

        GridPane titleBox = new GridPane();
        titleBox.setHgap(5);
        Label titleText = new Label("Save Sets");
        titleText.textProperty().bind(saveSetsPane.textProperty());
        newButton = new UnfocusableButton("New");
        newButton.setTooltip(new Tooltip("Create a new Save Set"));
        Button editButton = new UnfocusableButton("Edit");
        editButton.setTooltip(new Tooltip("Edit selected Save Set"));
        importButton = new UnfocusableButton("Import");
        importButton.setTooltip(new Tooltip("Import Save Sets from another location"));
        Button openButton = new UnfocusableButton("Open");
        openButton.setTooltip(new Tooltip("Open selected Save Set in Snapshot Viewer"));
        openButton.disableProperty()
            .bind(selector.selectedSaveSetProperty().isNull().or(saveSetsPane.expandedProperty().not()));
        openButton.setOnAction(e -> actionManager.openSaveSet(selector.selectedSaveSetProperty().get()));
        editButton.disableProperty()
            .bind(selector.selectedSaveSetProperty().isNull().or(saveSetsPane.expandedProperty().not()));
        editButton.setOnAction(e -> {
            DataProviderWrapper wrapper = SaveRestoreService.getInstance().getSelectedDataProvider();
            if (canExecute("Edit Save Set",
                wrapper.getName() + " data provider does not support editing of save sets.",
                wrapper.getProvider()::isSaveSetSavingSupported)) {
                actionManager.editSaveSet(selector.selectedSaveSetProperty().get());
            }
        });

        newButton.setOnAction(e -> {
            DataProviderWrapper wrapper = SaveRestoreService.getInstance().getSelectedDataProvider();
            if (canExecute("New Save Set",
                wrapper.getName() + " data provider does not support creation of new save sets.",
                wrapper.getProvider()::isSaveSetSavingSupported)) {
                actionManager.newSaveSet();
            }
        });
        importButton.disableProperty()
            .bind(selector.selectedBaseLevelProperty().isNull().or(saveSetsPane.expandedProperty().not()));
        importButton.setOnAction(e -> {
            DataProviderWrapper wrapper = SaveRestoreService.getInstance().getSelectedDataProvider();
            if (canExecute("Import Data", wrapper.getName() + " data provider does not support data importing.",
                wrapper.getProvider()::isImportSupported)) {
                new ImportDataDialog(BrowserView.this).openAndWait().ifPresent(actionManager::importFrom);
            }
        });

        setUpSetButtons(newButton, importButton, SaveRestoreService.getInstance().getSelectedDataProvider());

        setUpTitlePaneNode(titleText, true);
        setUpTitlePaneNode(newButton, false);
        setUpTitlePaneNode(importButton, false);
        setUpTitlePaneNode(editButton, false);
        setUpTitlePaneNode(openButton, false);
        titleBox.addRow(0, titleText, importButton, newButton, editButton, openButton);
        titleBox.setMaxWidth(Double.MAX_VALUE);
        titleText.setMaxWidth(Double.MAX_VALUE);
        saveSetsPane.setGraphic(titleBox);
        saveSetsPane.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        titleBox.prefWidthProperty().bind(scene.widthProperty().subtract(34));

        saveSetsPane.expandedProperty()
            .addListener((a, o, n) -> VBox.setVgrow(saveSetsPane, n ? Priority.ALWAYS : Priority.NEVER));
        return saveSetsPane;
    }

    private void setUpSetButtons(Button newButton, Button importButton, DataProviderWrapper dpw) {
        newButton.disableProperty().unbind();
        if (dpw == null) {
            importButton.setVisible(false);
            newButton.setDisable(true);
        } else {
            if (dpw.getProvider().areBaseLevelsSupported()) {
                importButton.setVisible(true);
                newButton.disableProperty()
                    .bind(selector.selectedBaseLevelProperty().isNull().or(saveSetsPane.expandedProperty().not()));
            } else {
                importButton.setVisible(false);
                newButton.disableProperty().bind(saveSetsPane.expandedProperty().not());
            }
        }
    }

    private boolean canExecute(String title, String message, BooleanSupplier testingFunction) {
        if (!testingFunction.getAsBoolean()) {
            FXMessageDialog.openInformation(getSite().getShell(), title, message);
            return false;
        }
        return true;
    }

    private Node createSnapshotsPane(Scene scene) {
        BorderPane content = new BorderPane();
        snapshotsList = new ListView<>();
        snapshotsList.setCellFactory(e -> new ListCell<Snapshot>() {
            public void updateItem(Snapshot item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().remove("tagged-cell");
                if (item == null || empty) {
                    setTooltip(null);
                    setText(null);
                } else {
                    StringBuilder sb = new StringBuilder(300);
                    sb.append(item.getComment());
                    String message = item.getTagMessage().orElse(null);
                    String tag = item.getTagName().orElse(null);
                    if (tag != null) {
                        sb.append("\n\n").append(tag).append('\n').append(message);
                        getStyleClass().add("tagged-cell");
                    }
                    setTooltip(new Tooltip(sb.toString()));
                    if (searchMode) {
                        StringBuilder text = new StringBuilder(300);
                        item.getSaveSet().getBaseLevel()
                            .ifPresent(e -> text.append('[').append(e).append(']').append(' '));
                        text.append(item.getSaveSet().getPathAsString()).append('\n');
                        text.append(item.toString());
                        setText(text.toString());
                    } else {
                        setText(item.toString());
                    }
                }
            }
        });
        snapshotsList.getStylesheets().add(BrowserView.class.getResource("taggedCell.css").toExternalForm());

        snapshotsList.setOnDragDetected(e -> {
            Dragboard db = snapshotsList.startDragAndDrop(TransferMode.ANY);
            ClipboardContent cc = new ClipboardContent();
            Snapshot snapshot = snapshotsList.getSelectionModel().getSelectedItem();
            snapshot.getSaveSet().updateBaseLevel();
            cc.put(SnapshotDataFormat.INSTANCE, snapshot);
            db.setContent(cc);
            e.consume();
        });
        snapshotsList.setOnMouseReleased(e -> {
            Snapshot snapshot = snapshotsList.getSelectionModel().getSelectedItem();
            deleteTagAction.setEnabled(snapshot != null && snapshot.getTagName().isPresent());
            contextMenu.setVisible(e.getButton() == MouseButton.SECONDARY);
        });
        snapshotsList.setOnMouseClicked(e -> {
            Snapshot snapshot = snapshotsList.getSelectionModel().getSelectedItem();
            if (e.getClickCount() == 2 && snapshot != null) {
                actionManager.openSnapshot(snapshot);
            }
        });
        snapshotsList.selectionModelProperty().get().selectedItemProperty().addListener((a, o, n) -> {
            final SelectionChangedEvent e = new SelectionChangedEvent(BrowserView.this, getSelection());
            selectionChangedListener.forEach(l -> l.selectionChanged(e));
        });
        content.setCenter(snapshotsList);
        snapshotsPane = new TitledPane("Snapshots", content);
        snapshotsPane.setMaxHeight(Double.MAX_VALUE);

        GridPane titleBox = new GridPane();
        titleBox.setHgap(5);
        Label titleText = new Label("Snapshots");
        titleText.textProperty().bind(snapshotsPane.textProperty());
        Button tagButton = new UnfocusableButton("Tag");
        tagButton.setTooltip(new Tooltip("Tag selected snapshot"));
        tagButton.disableProperty().bind(snapshotsList.selectionModelProperty().get().selectedItemProperty().isNull()
            .or(snapshotsPane.expandedProperty().not()));
        tagButton.setOnAction(e -> {
            DataProviderWrapper wrapper = SaveRestoreService.getInstance().getSelectedDataProvider();
            if (canExecute("Tag Snapshot", wrapper.getName() + " data provider does not support tagging.",
                wrapper.getProvider()::isTaggingSupported)) {
                final Snapshot snapshot = snapshotsList.getSelectionModel().getSelectedItem();
                final FXTaggingDialog dialog = new FXTaggingDialog(getSite().getShell());
                dialog.openAndWait().ifPresent(a -> actionManager.tagSnapshot(snapshot, a, dialog.getMessage()));
            }
        });
        Button openButton = new UnfocusableButton("Open");
        openButton.setTooltip(new Tooltip("Open selected snapshot in a new Snapshot Viewer"));
        openButton.disableProperty().bind(snapshotsList.selectionModelProperty().get().selectedItemProperty().isNull()
            .or(snapshotsPane.expandedProperty().not()));
        openButton.setOnAction(e -> actionManager.openSnapshot(snapshotsList.getSelectionModel().getSelectedItem()));

        Button compareButton = new UnfocusableButton("Compare");
        compareButton.setTooltip(new Tooltip("Open selected snapshot the active Snapshot Viewer"));
        compareButton.disableProperty().bind(snapshotsList.selectionModelProperty().get().selectedItemProperty()
            .isNull().or(snapshotsPane.expandedProperty().not()));
        compareButton
            .setOnAction(e -> actionManager.compareSnapshot(snapshotsList.getSelectionModel().getSelectedItem()));

        setUpTitlePaneNode(titleText, true);
        setUpTitlePaneNode(tagButton, false);
        setUpTitlePaneNode(openButton, false);
        setUpTitlePaneNode(compareButton, false);

        SaveRestoreService.getInstance().getPreferences().addPropertyChangeListener(e -> {
            if (SaveRestoreService.PREF_NUMBER_OF_SNAPSHOTS.equals(e.getProperty())) {
                setUpFetchButtons(titleBox, titleText, openButton, compareButton, tagButton);
            }
        });
        setUpFetchButtons(titleBox, titleText, openButton, compareButton, tagButton);

        titleBox.setMaxWidth(Double.MAX_VALUE);
        titleText.setMaxWidth(Double.MAX_VALUE);
        snapshotsPane.setGraphic(titleBox);
        snapshotsPane.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        titleBox.prefWidthProperty().bind(scene.widthProperty().subtract(34));
        snapshotsPane.expandedProperty()
            .addListener((a, o, n) -> VBox.setVgrow(snapshotsPane, n ? Priority.ALWAYS : Priority.NEVER));
        return snapshotsPane;
    }

    private void setUpFetchButtons(GridPane titleBox, Label titleText, Button openButton, Button compareButton,
        Button tagButton) {
        int num = SaveRestoreService.getInstance().getNumberOfSnapshots();
        titleBox.getChildren().clear();
        if (num > 0) {
            Button nextButton = new UnfocusableButton("",
                new ImageView(new Image(BrowserView.class.getResourceAsStream("/icons/1rightarrow.png"))));
            nextButton.setTooltip(new Tooltip("Load Next Batch"));
            nextButton.setOnAction(e -> selector.readSnapshots(false, false));
            nextButton.disableProperty()
                .bind(selector.selectedSaveSetProperty().isNull().or(selector.allSnapshotsLoadedProperty()));

            Button nextAllButton = new UnfocusableButton("",
                new ImageView(new Image(BrowserView.class.getResourceAsStream("/icons/2rightarrow.png"))));
            nextAllButton.setTooltip(new Tooltip("Load All"));
            nextAllButton.setOnAction(e -> selector.readSnapshots(false, true));
            nextAllButton.disableProperty()
                .bind(selector.selectedSaveSetProperty().isNull().or(selector.allSnapshotsLoadedProperty()));

            setUpTitlePaneNode(nextButton, false);
            setUpTitlePaneNode(nextAllButton, false);
            titleBox.addRow(0, titleText, nextButton, nextAllButton, openButton, compareButton, tagButton);
        } else {
            titleBox.addRow(0, titleText, openButton, compareButton, tagButton);
        }
    }

    private void setUpElementsPaneTitle() {
        BaseLevelBrowser<BaseLevel> br = defaultBaseLevelBrowser;
        if (baseLevelBrowser != null && baseLevelBrowser.getFXContent().getParent() != null) {
            br = baseLevelBrowser;
        }
        BaseLevel bl = selector.selectedBaseLevelProperty().get();
        if (bl == null) {
            baseLevelPane.setText(br.getTitleFor(Optional.empty(), Optional.empty()));
        } else {
            if (selector.isDefaultBranch()) {
                baseLevelPane.setText(br.getTitleFor(Optional.of(bl), Optional.empty()));
            } else {
                Branch branch = selector.selectedBranchProperty().get();
                baseLevelPane.setText(br.getTitleFor(Optional.of(bl), Optional.of(branch.getShortName())));
            }
        }
    }

    private void init() {
        selector.selectedBranchProperty().addListener((a, o, n) -> {
            if (selector.isDefaultBranch()) {
                Activator.getDefault().getDialogSettings().put(SETTINGS_SELECTED_BRANCH, (String) null);
            } else {
                Activator.getDefault().getDialogSettings().put(SETTINGS_SELECTED_BRANCH, n.getShortName());
            }
        });
        selector.selectedBaseLevelProperty().addListener((a, o, n) -> {
            setUpElementsPaneTitle();
            if (n == null) {
                saveSetsPane.setText("Save Sets");
            } else {
                saveSetsPane.setText("Save Sets for " + n.getPresentationName());
            }
        });
        if (baseLevelBrowser != null) {
            baseLevelBrowser.selectedBaseLevelProperty().addListener((a, o, n) -> {
                if (baseLevelBrowser.getFXContent().getParent() != null) {
                    selector.selectedBaseLevelProperty().setValue(n);
                }
            });
        }
        defaultBaseLevelBrowser.selectedBaseLevelProperty().addListener((a, o, n) -> {
            if (defaultBaseLevelBrowser.getFXContent().getParent() != null) {
                selector.selectedBaseLevelProperty().setValue(n);
            }
        });
        selector.baseLevelsProperty().addListener((a, o, n) -> {
            try {
                defaultBaseLevelBrowser.availableBaseLevelsProperty().set(defaultBaseLevelBrowser.transform(n));
                defaultBaseLevelBrowser.selectedBaseLevelProperty()
                    .setValue(selector.selectedBaseLevelProperty().get());
                if (baseLevelBrowser != null) {
                    baseLevelBrowser.availableBaseLevelsProperty().set(baseLevelBrowser.transform(n));
                    BaseLevel base = selector.selectedBaseLevelProperty().get();
                    if (base == null) {
                        baseLevelBrowser.selectedBaseLevelProperty().setValue(null);
                    } else {
                        List<BaseLevel> bl = baseLevelBrowser.transform(Arrays.asList(base));
                        baseLevelBrowser.selectedBaseLevelProperty().setValue(bl.isEmpty() ? null : bl.get(0));
                    }
                }
            } catch (RuntimeException e) {
                FXMessageDialog.openError(getSite().getShell(), "Base Level Error", e.getMessage());
            }
        });
        selector.selectedBranchProperty().addListener((a, o, n) -> setUpElementsPaneTitle());
        selector.selectedSaveSetProperty().addListener((a, o, n) -> {
            if (n == null) {
                snapshotsPane.setText("Snapshots");
            } else {
                snapshotsPane.setText("Snapshots of " + n.getName());
            }
        });
        selector.saveSetsProperty().addListener((a, o, saveSets) -> {
            TreeItem<SaveSetWrapper> root = new SaveSetTreeItem("Root");
            Map<String, TreeItem<SaveSetWrapper>> items = new HashMap<>();
            items.put("", root);
            for (SaveSet set : saveSets) {
                String folder = set.getFolder();
                TreeItem<SaveSetWrapper> parent = items.get(folder);
                if (parent == null) {
                    parent = new SaveSetTreeItem(folder);
                    items.put(folder, parent);

                    String[] path = parent.getValue().path;
                    TreeItem<SaveSetWrapper> currentChild = parent;
                    for (int i = path.length - 1; i > -1; i--) {
                        String m = makeStringFromParts(path, 0, i);
                        TreeItem<SaveSetWrapper> ti = items.get(m);
                        if (ti == null) {
                            ti = new SaveSetTreeItem(m);
                            items.put(m, ti);
                            ti.getChildren().add(currentChild);
                            currentChild = ti;
                        } else {
                            ti.getChildren().add(currentChild);
                            break;
                        }
                    }
                }
                parent.getChildren().add(new SaveSetTreeItem(new SaveSetWrapper(set, null)));
            }
            saveSetsTree.setRoot(root);
            root.setExpanded(true);
        });
        selector.snapshotsProperty().addListener((a, o, n) -> {
            searchMode = false;
            snapshotsList.setItems(FXCollections.observableArrayList(n));
        });

        List<DataProviderWrapper> dpws = SaveRestoreService.getInstance().getDataProviders();
        if (!dpws.isEmpty()) {
            IDialogSettings settings = Activator.getDefault().getDialogSettings();
            String selectedDataProvider = settings.get(SETTINGS_SELECTED_DATA_PROVIDER);
            if (selectedDataProvider != null) {
                selector.setFirstTimeBranch(settings.get(SETTINGS_SELECTED_BRANCH));
            }
            DataProviderWrapper dpw = dpws.get(0);
            for (DataProviderWrapper w : dpws) {
                if (w.getId().equals(selectedDataProvider)) {
                    dpw = w;
                    break;
                }
            }
            SaveRestoreService.getInstance().setSelectedDataProvider(dpw);
        }

        SaveRestoreService.getInstance().addPropertyChangeListener(SaveRestoreService.SELECTED_DATA_PROVIDER, dpl);
        updateForDataProviderChange();
    }

    private void updateForDataProviderChange() {
        DataProviderWrapper wrapper = SaveRestoreService.getInstance().getSelectedDataProvider();
        if (wrapper != null) {
            IDialogSettings settings = Activator.getDefault().getDialogSettings();
            settings.put(SETTINGS_SELECTED_DATA_PROVIDER, wrapper.getId());
            mainPane.getChildren().clear();
            if (wrapper.getProvider().areBaseLevelsSupported() && baseLevelPane != null) {
                mainPane.getChildren().addAll(baseLevelPane, dataPane);
            } else {
                mainPane.getChildren().addAll(dataPane);
            }
        }
        setUpSetButtons(newButton, importButton, wrapper);
    }

    private static String makeStringFromParts(String[] parts, int from, int to) {
        if (from == to)
            return "";
        StringBuilder sb = new StringBuilder(100);
        sb.append(parts[from]);
        for (int i = from + 1; i < to; i++) {
            sb.append('/').append(parts[i]);
        }
        return sb.toString();
    }

    private static void setUpTitlePaneNode(Region node, boolean isTitleText) {
        if (isTitleText) {
            setGridConstraints(node, true, false, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
        } else {
            setGridConstraints(node, false, false, HPos.RIGHT, VPos.CENTER, Priority.NEVER, Priority.NEVER);
            node.setPadding(new Insets(3, 5, 3, 5));
        }
    }

    /**
     * Sets the snapshot search results. The list of snapshots replaces the current list of snapshots and the expression
     * is visible in the title of the snapshots pane. The base level and save sets panes are collapsed.
     *
     * @param expression the expression used for searching
     * @param snapshots the results
     */
    void setSearchResults(String expression, List<Snapshot> snapshots) {
        if (baseLevelPane != null) {
            baseLevelPane.expandedProperty().set(false);
        }
        saveSetsPane.expandedProperty().set(false);
        snapshotsPane.expandedProperty().set(true);
        snapshotsPane.setText("Search results for '" + expression + "'");
        searchMode = true;
        snapshotsList.setItems(FXCollections.observableArrayList(snapshots));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.fx.ui.workbench3.FXViewPart#setFxFocus()
     */
    @Override
    protected void setFxFocus() {
        // no focus
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.
     * ISelectionChangedListener)
     */
    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        selectionChangedListener.add(listener);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.
     * ISelectionChangedListener)
     */
    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        selectionChangedListener.remove(listener);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void setSelection(ISelection selection) {
        // nothing to select
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
     */
    @Override
    public ISelection getSelection() {
        Snapshot selectedSnapshot = snapshotsList.selectionModelProperty().get().getSelectedItem();
        return selectedSnapshot == null ? new LazySnapshotStructuredSelection(null, null)
            : new LazySnapshotStructuredSelection(selectedSnapshot, SaveRestoreService.getInstance()
                .getDataProvider(selectedSnapshot.getSaveSet().getDataProviderId()).getProvider());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.window.IShellProvider#getShell()
     */
    @Override
    public Shell getShell() {
        return getSite().getShell();
    }
}
