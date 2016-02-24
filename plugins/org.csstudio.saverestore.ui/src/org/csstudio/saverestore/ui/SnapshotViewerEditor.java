package org.csstudio.saverestore.ui;

import static org.csstudio.ui.fx.util.FXUtilities.setGridConstraints;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.csstudio.saverestore.DataProvider;
import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.Utilities;
import org.csstudio.saverestore.data.SaveSet;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VSnapshot;
import org.csstudio.saverestore.ui.util.SnapshotDataFormat;
import org.csstudio.saverestore.ui.util.VTypeNamePair;
import org.csstudio.ui.fx.util.FXComboInputDialog;
import org.csstudio.ui.fx.util.FXEditorPart;
import org.csstudio.ui.fx.util.FXMessageDialog;
import org.csstudio.ui.fx.util.FXSaveAsDialog;
import org.csstudio.ui.fx.util.FXTextAreaInputDialog;
import org.csstudio.ui.fx.util.FXTextInputDialog;
import org.csstudio.ui.fx.util.FXUtilities;
import org.csstudio.ui.fx.util.StaticTextArea;
import org.csstudio.ui.fx.util.StaticTextField;
import org.csstudio.ui.fx.util.UnfocusableButton;
import org.csstudio.ui.fx.util.UnfocusableToggleButton;
import org.diirt.vtype.Array;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;

import javafx.animation.Animation.Status;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 *
 * <code>SnapshotViewerEditor</code> is an {@link IEditorPart} implementation for displaying, creating, comparing, and
 * restoring snapshots. It provides a table that displays the stored values (together with the alarm data and timestamp)
 * and live data. In comparison view only the values are displayed. At the top of the editor buttons for taking, saving
 * and restoring the snapshot are located.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SnapshotViewerEditor extends FXEditorPart implements ISnapshotReceiver {

    /** The editor ID */
    public static final String ID = "org.csstudio.saverestore.ui.editor.snapshotviewer";
    static final String STYLE = "style.css";

    private static final String ALL_ITEMS = "<ALL ITEMS>";
    // the style for the animated save button
    public static final String ANIMATED_STYLE = "-fx-background-color: #FF8080; -fx-text-fill: white; "
        + "-fx-effect: dropshadow(three-pass-box,rgba(0,0,0,0.6),5,0.0,0,1); ";

    private Table table;
    private final SnapshotViewerController controller;
    private TextArea commentField;
    private TextField dateField;
    private TextField creatorField;
    private TextArea parametersField;
    private Button saveSnapshotButton;
    private Menu contextMenu;

    /**
     * Constructs a new editor.
     */
    public SnapshotViewerEditor() {
        controller = new SnapshotViewerController(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.ui.fx.util.FXEditorPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        MenuManager menu = new MenuManager();
        final Action openChartTableAction = new Action("Show Chart/Table...") {
            @Override
            public void run() {
                VTypeNamePair type = table.getClickedItem();
                if (type != null && type.value instanceof Array) {
                    new WaveformDialog(getSite().getShell(), type, controller).open();
                }
            }
        };
        menu.add(openChartTableAction);
        openChartTableAction.setEnabled(false);
        final Action removePVAction = new Action("Remove PV") {
            @Override
            public void run() {
                VTypeNamePair type = table.getClickedItem();
                if (type != null) {
                    SaveRestoreService.getInstance().execute("Remove PV",
                        () -> controller.removePV(type.entry, table::removeItem));
                }
            }
        };
        menu.add(removePVAction);
        openChartTableAction.setEnabled(false);
        table.addSelectionChangedListener(e -> {
            VTypeNamePair type = table.getClickedItem();
            openChartTableAction.setEnabled(type != null && type.value instanceof Array);
            removePVAction.setEnabled(type != null);
        });
        menu.add(new org.eclipse.jface.action.Separator());
        if (SendToELogAction.isElogAvailable()) {
            menu.add(new SendToELogAction(getSite().getShell(), table));
        }
        menu.add(new org.eclipse.jface.action.Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        contextMenu = menu.createContextMenu(parent);
        parent.setMenu(contextMenu);
        getSite().registerContextMenu(menu, table);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, "org.csstudio.saverestore.ui.help.snapshotviewer");
    }

    private void init() {
        VSnapshot snapshot = getEditorInput().getAdapter(VSnapshot.class);
        if (snapshot == null) {
            snapshot = new VSnapshot(new SaveSet());
        }
        addOrSetSnapshot(snapshot, false, true);

        final FadeTransition animation = new FadeTransition(Duration.seconds(0.15), saveSnapshotButton);
        animation.setAutoReverse(true);
        animation.setFromValue(1.0);
        animation.setToValue(0.4);
        animation.setCycleCount(6);
        controller.snapshotSaveableProperty().addListener((a, o, n) -> {
            if (!n) {
                animation.pause();
                animation.jumpTo(Duration.seconds(0));
                animation.stop();
                saveSnapshotButton.setStyle(null);
            } else if (animation.getStatus() != Status.RUNNING) {
                saveSnapshotButton.setStyle(ANIMATED_STYLE);
                animation.play();
            }
            firePropertyChange(PROP_DIRTY);
        });
        if (controller.snapshotSaveableProperty().get()) {
            saveSnapshotButton.setStyle(ANIMATED_STYLE);
            animation.play();
        }
        getSite().setSelectionProvider(table);
        controller.baseSnapshotProperty().addListener((o, a, n) -> updateMetaInfo(n));
    }

    private void updateMetaInfo(VSnapshot snapshot) {
        if (snapshot != null && snapshot.getSnapshot().isPresent()) {
            Snapshot s = snapshot.getSnapshot().get();
            if (s.getTagName().isPresent()) {
                String tagName = s.getTagName().get();
                String tagMessage = s.getTagMessage().orElse("");
                List<String> parameters = s.getPublicParameters();
                StringBuilder sb = new StringBuilder(
                    tagName.length() + tagMessage.length() + 30 + parameters.size() * 100);
                sb.append("TAG NAME: ").append(tagName).append("\nTAG MESSAGE: ").append(tagMessage);
                for (String p : parameters) {
                    sb.append('\n').append(p.toUpperCase(Locale.UK)).append(':').append(' ')
                        .append(s.getParameters().get(p));
                }
                parametersField.setText(sb.toString());
                parametersField.setStyle("-fx-control-inner-background: #FFF8D2;");
            } else {
                List<String> parameters = s.getPublicParameters();
                if (parameters.isEmpty()) {
                    parametersField.setText("");
                } else {
                    StringBuilder sb = new StringBuilder(parameters.size() * 100);
                    for (String p : parameters) {
                        sb.append(p.toUpperCase(Locale.UK)).append(':').append(' ').append(s.getParameters().get(p))
                            .append('\n');
                    }
                    parametersField.setText(sb.substring(0, sb.length() - 1));
                }
                parametersField.setStyle(null);
            }
            commentField.setText(s.getComment());
            creatorField.setText(s.getOwner());
            dateField.setText(Utilities.timestampToBigEndianString(s.getDate(), true));
        } else {
            commentField.setText("");
            creatorField.setText("");
            dateField.setText("");
            parametersField.setText("");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave(IProgressMonitor monitor) {
        if (getEditorInput() instanceof IFileEditorInput || getEditorInput() instanceof IURIEditorInput) {
            save(monitor, false);
        } else {
            SaveRestoreService.getInstance().execute("Save Snapshot", () -> save(monitor, false));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    @Override
    public void doSaveAs() {
        if (getEditorInput() instanceof IFileEditorInput || getEditorInput() instanceof IURIEditorInput) {
            // if saving a file that was initially opened from workspace, save it back to workspacae, but let eclipse
            // to choose the thread and proper locking mechanism
            save(new NullProgressMonitor(), true);
        } else {
            // if saving to git, there is no save as, so this can never be called at all, but just in case,
            // do a normal save
            doSave(new NullProgressMonitor());
        }
    }

    private void save(final IProgressMonitor monitor, boolean saveAs) {
        // loop over all snapshots and save them one by one
        final List<VSnapshot> snapshots = saveAs ? controller.getAllSnapshots() : controller.getSnapshots(true);
        do {
            if (!save(snapshots, true, saveAs).map(snapshots::remove).isPresent()) {
                break;
            }
        } while (!snapshots.isEmpty());
        getSite().getShell().getDisplay().asyncExec(() -> {
            monitor.done();
            checkDirty();
        });
    }

    private Optional<VSnapshot> save(List<VSnapshot> snapshots, boolean promptIfOnlyOne, final boolean saveAs) {
        if (snapshots.isEmpty()) {
            return Optional.empty();
        }
        // ask the user to choose the snapshot to save if there is more than one snapshot or "promptIfOnlyOne" is true
        // then save the selected snapshot
        if (getEditorInput() instanceof IFileEditorInput || getEditorInput() instanceof IURIEditorInput) {
            if (snapshots.size() > 1) {
                return FXComboInputDialog.pick(getSite().getShell(), "Select Snapshot",
                    "Select the snapshot that you wish to save", snapshots.get(0), snapshots)
                    .map(e -> saveSnapshotToFile(e, saveAs));
            } else {
                return Optional.ofNullable(saveSnapshotToFile(snapshots.remove(0), saveAs));
            }
        } else {
            if (promptIfOnlyOne || snapshots.size() > 1) {
                return FXComboInputDialog.pick(getSite().getShell(), "Select Snapshot",
                    "Select the snapshot that you wish to save", snapshots.get(0), snapshots).map(this::saveSnapshot);
            } else {
                return Optional.ofNullable(saveSnapshot(snapshots.remove(0)));
            }
        }
    }

    private VSnapshot saveSnapshot(final VSnapshot snapshot) {
        if (snapshot.getSnapshot().isPresent()) {
            Optional<String> comment = FXTextAreaInputDialog.get(getSite().getShell(), "Snapshot Comment",
                "Provide a short comment for the snapshot " + snapshot, "",
                e -> (e == null || e.trim().length() < 10) ? "Comment should be at least 10 characters long." : null);
            return comment.map(e -> controller.saveSnapshot(e, snapshot))
                .filter(e -> table.replaceSnapshot(snapshot, e)).orElse(null);
        } else {
            // should never happen at all
            throw new IllegalArgumentException("Snapshot " + snapshot + " is invalid.");
        }
    }

    /**
     * Save the snapshot to a file in workspace. File can be either selected using the save as dialog or the file
     * specified by the input is used.
     *
     * @param snapshot the snapshot to save
     * @param input the editor input to use as a destination file or as an initial location
     * @param saveAs true if the snapshot should be saved o a new file or false if the file specified by input is
     *            overwritten
     * @return the snapshot if successful or null otherwise
     */
    private VSnapshot saveSnapshotToFile(VSnapshot snapshot, boolean saveAs) {
        if (getEditorInput() instanceof IFileEditorInput) {
            IFile file = ((IFileEditorInput) getEditorInput()).getFile();
            if (saveAs) {
                SaveAsDialog saveAsDialog = new FXSaveAsDialog(getSite().getShell());
                saveAsDialog.setOriginalFile(file);
                if (saveAsDialog.open() == Window.OK) {
                    IPath targetPath = saveAsDialog.getResult();
                    IFile targetFile = ResourcesPlugin.getWorkspace().getRoot().getFile(targetPath);
                    return controller.saveToFile(targetFile, snapshot, true);
                }
            } else if (file != null) {
                return controller.saveToFile(file, snapshot, true);
            }
        } else if (getEditorInput() instanceof IURIEditorInput) {
            URI uri = ((IURIEditorInput) getEditorInput()).getURI();
            if ("file".equalsIgnoreCase(uri.getScheme())) {
                final File file = new File(uri);
                if (saveAs) {
                    selectFile(true, Optional.of(file))
                        .ifPresent(f -> SaveRestoreService.getInstance().execute("Save snapshot file", () -> {
                            controller.exportSingleSnapshotToFile(snapshot, f, true);
                            Platform.runLater(() -> checkDirty());
                        }));
                } else {
                    SaveRestoreService.getInstance().execute("Save snapshot file", () -> {
                        controller.exportSingleSnapshotToFile(snapshot, file, true);
                        Platform.runLater(() -> checkDirty());
                    });
                }
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);
        setPartName(input.getName());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        controller.dispose();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.part.EditorPart#isDirty()
     */
    @Override
    public boolean isDirty() {
        return controller.snapshotSaveableProperty().get();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.ISnapshotReceiver#checkDirty()
     */
    @Override
    public void checkDirty() {
        table.updateTableColumnTitles();
        firePropertyChange(PROP_DIRTY);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed() {
        return getEditorInput() instanceof IFileEditorInput || getEditorInput() instanceof IURIEditorInput;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXEditorPart#createFxScene()
     */
    @Override
    protected Scene createFxScene() {
        BorderPane contentPane = new BorderPane(createTable());
        contentPane.setTop(new BorderPane(createMainControlPane(), createToolbarPane(), null, null, null));
        init();
        return new Scene(contentPane);
    }

    private Node createToolbarPane() {
        HBox leftToolbar = new HBox(5);
        Button addPVButton = new UnfocusableButton("",
            new ImageView(new Image(SnapshotViewerEditor.class.getResourceAsStream("/icons/includeMode_filter.png"))));
        addPVButton.setTooltip(new Tooltip("Add a PV from the central archiving system"));
        addPVButton.setOnAction(e -> FXTextInputDialog
            .get(getSite().getShell(), "Add Archived PV",
                "Enter the name of the PV from the archiving system that you wish to add.", "",
                i -> i == null || i.isEmpty() ? "The PV name cannot be empty" : null)
            .ifPresent(pv -> SaveRestoreService.getInstance().execute("Add PV from Archive",
                () -> controller.addPVFromArchive(pv, table::addItem))));
        Button importButton = new UnfocusableButton("",
            new ImageView(new Image(SnapshotViewerEditor.class.getResourceAsStream("/icons/import_wiz.png"))));
        importButton.setTooltip(new Tooltip("Import PV values from external source"));
        importButton.setDisable(ExtensionPointLoader.getInstance().getValueImporters().isEmpty());
        importButton.setOnAction(e -> {
            List<ValueImporterWrapper> importers = ExtensionPointLoader.getInstance().getValueImporters();
            if (importers.isEmpty()) {
                return;
            }
            FXComboInputDialog
                .pick(getSite().getShell(), "Select Value Importer",
                    "Select the value importer from which you wish to import the values", importers.get(0), importers)
                .ifPresent(imp -> SaveRestoreService.getInstance().execute("Import Values",
                    () -> controller.importValues(imp, SnapshotViewerEditor.this::addSnapshot)));
        });
        ToggleButton addReadbacksButton = new UnfocusableToggleButton("",
            new ImageView(new Image(SnapshotViewerEditor.class.getResourceAsStream("/icons/exp_deployplug.png"))));
        addReadbacksButton.setTooltip(new Tooltip("Show column with live values from the readback PVs"));
        addReadbacksButton.selectedProperty().addListener((a, o, n) -> controller.showReadbacks(n, b -> {
            final List<VSnapshot> snapshots = controller.getAllSnapshots();
            Platform.runLater(() -> table.updateTable(b, snapshots, controller.isShowReadbacks(),
                controller.isShowStoredReadbacks()));
        }));
        ToggleButton showStoredReadbacksButton = new UnfocusableToggleButton("",
            new ImageView(new Image(SnapshotViewerEditor.class.getResourceAsStream("/icons/frgmt_obj.png"))));
        showStoredReadbacksButton.setTooltip(new Tooltip("Show column with stored values from the readback PVs"));
        showStoredReadbacksButton.selectedProperty().addListener((a, o, n) -> controller.showStoredReadbacks(n, b -> {
            final List<VSnapshot> snapshots = controller.getAllSnapshots();
            Platform.runLater(() -> table.updateTable(b, snapshots, controller.isShowReadbacks(),
                controller.isShowStoredReadbacks()));
        }));
        Button openSnapshotFromFileButton = new UnfocusableButton("",
            new ImageView(new Image(SnapshotViewerEditor.class.getResourceAsStream("/icons/fldr_obj.png"))));
        openSnapshotFromFileButton.setTooltip(new Tooltip("Open snapshot from file"));
        openSnapshotFromFileButton.setOnAction(e -> {
            FileDialog dialog = new FileDialog(getSite().getShell(), SWT.OPEN);
            dialog.setFilterExtensions(new String[] { "*" + SnapshotViewerController.FEXT_SNP });
            dialog.setFilterNames(new String[] { "Single Snapshot (*.snp)" });
            String ans = dialog.open();
            if (ans != null) {
                SaveRestoreService.getInstance().execute("Open file",
                    () -> controller.openFromFile(new File(ans)).ifPresent(SnapshotViewerEditor.this::addSnapshot));
            }
        });
        Button saveSnapshotToFileButton = new UnfocusableButton("",
            new ImageView(new Image(SnapshotViewerEditor.class.getResourceAsStream("/icons/saveas_edit.png"))));
        saveSnapshotToFileButton.setTooltip(new Tooltip("Save a snapshot to file"));
        saveSnapshotToFileButton.setOnAction(e -> selectFile(true, Optional.empty()).ifPresent(f -> {
            final List<VSnapshot> snapshots = controller.getAllSnapshots();
            if (snapshots.size() == 1) {
                SaveRestoreService.getInstance().execute("Export to snp file",
                    () -> controller.exportSingleSnapshotToFile(snapshots.get(0), f, false));
            } else {
                FXComboInputDialog
                    .pick(getSite().getShell(), "Select Snapshot", "Select the snapshot that you wish to save",
                        snapshots.get(0), snapshots)
                    .ifPresent(s -> SaveRestoreService.getInstance().execute("Export to snp file",
                        () -> controller.exportSingleSnapshotToFile(s, f, false)));
            }
        }));
        Button exportButton = new UnfocusableButton("",
            new ImageView(new Image(SnapshotViewerEditor.class.getResourceAsStream("/icons/export_wiz.png"))));
        exportButton.setTooltip(new Tooltip("Export editor contents to file"));
        exportButton.setOnAction(e -> selectFile(false, Optional.empty()).ifPresent(f -> {
            final boolean includeReadback = controller.isShowReadbacks();
            SaveRestoreService.getInstance().execute("Export to csv file",
                () -> controller.exportToFile(f, includeReadback));
        }));
        Separator separator1 = new Separator(Orientation.VERTICAL);
        String style = SnapshotViewerEditor.class.getResource(STYLE).toExternalForm();
        separator1.getStylesheets().add(style);
        Separator separator2 = new Separator(Orientation.VERTICAL);
        separator2.getStylesheets().add(style);
        Separator separator3 = new Separator(Orientation.VERTICAL);
        separator3.getStylesheets().add(style);
        leftToolbar.getChildren().addAll(addPVButton, separator1, addReadbacksButton, showStoredReadbacksButton,
            separator2, importButton, separator3, openSnapshotFromFileButton, saveSnapshotToFileButton, exportButton);

        HBox rightToolbar = new HBox(5);
        ComboBox<String> filterCombo = new ComboBox<>();
        filterCombo.setTooltip(new Tooltip("Pnly PVs that fully or partially match the expression will be displayed"));
        filterCombo.getItems().add(ALL_ITEMS);
        filterCombo.getItems().addAll(Activator.getDefault().getFilters());
        filterCombo.setMinWidth(140);
        filterCombo.setEditable(true);
        filterCombo.setOnAction(new EventHandler<ActionEvent>() {
            private boolean suppressUpdate;

            @Override
            public void handle(ActionEvent event) {
                if (suppressUpdate) {
                    return;
                }
                // when a new filter is selected, add that filter to the second row in the combo and store the filters
                String filter = filterCombo.getSelectionModel().getSelectedItem();
                if (filter == null || filter.trim().isEmpty()) {
                    filter = ALL_ITEMS;
                }
                final String selectedFilter = filter;
                if (!selectedFilter.equals(ALL_ITEMS)) {
                    try {
                        Pattern.compile(".*" + filter + ".*");
                    } catch (PatternSyntaxException e) {
                        FXMessageDialog.openError(getSite().getShell(), "Invalid Filter", e.getMessage());
                        return;
                    }
                }
                List<String> items = new ArrayList<>(filterCombo.getItems());
                if (items.indexOf(selectedFilter) < 0) {
                    items.add(1, selectedFilter);
                    if (items.size() > Activator.getDefault().getMaxNumberOfFilters()) {
                        items.remove(items.size() - 1);
                    }
                } else if (!ALL_ITEMS.equals(selectedFilter)) {
                    items.remove(selectedFilter);
                    items.add(1, selectedFilter);
                }

                String[] filters = new String[items.size() - 1];
                for (int i = 0; i < filters.length; i++) {
                    filters[i] = items.get(i + 1);
                }
                Activator.getDefault().storeFilters(filters);

                Platform.runLater(() -> {
                    // combo box has to be updated after this event is fully handled
                    // the lines below might trigger a few more actions, so make sure that those are suppressed.
                    suppressUpdate = true;
                    ObservableList<String> list = FXCollections.observableArrayList();
                    list.add(ALL_ITEMS);
                    list.addAll(filters);
                    filterCombo.setItems(list);
                    filterCombo.getSelectionModel().select(selectedFilter);
                    suppressUpdate = false;
                });

                SaveRestoreService.getInstance().execute("Filter items", () -> {
                    final List<TableEntry> entries = controller
                        .setFilter(ALL_ITEMS.equals(selectedFilter) ? null : selectedFilter);
                    Platform.runLater(() -> table.updateTable(entries));
                });
            }
        });
        filterCombo.getSelectionModel().select(ALL_ITEMS);
        ToggleButton hideEqualItemsButton = new ToggleButton("",
            new ImageView(new Image(SnapshotViewerEditor.class.getResourceAsStream("/icons/filter_ps.png"))));
        hideEqualItemsButton.setTooltip(new Tooltip("Hide/Show items where snapshot value equals current value"));
        hideEqualItemsButton.selectedProperty()
            .addListener((a, o, n) -> SaveRestoreService.getInstance().execute("Filter items", () -> {
                final List<TableEntry> entries = controller.setHideEqualItems(n);
                Platform.runLater(() -> table.updateTable(entries));
            }));
        rightToolbar.setAlignment(Pos.CENTER_RIGHT);
        rightToolbar.getChildren().addAll(new Label("Filter (partial match):"), filterCombo, hideEqualItemsButton);
        HBox toolbar = new HBox(5);
        HBox.setHgrow(leftToolbar, Priority.ALWAYS);
        HBox.setHgrow(rightToolbar, Priority.NEVER);
        String background = FXUtilities.toBackgroundColorStyle(new RGB(251, 251, 251));
        leftToolbar.setStyle(background);
        rightToolbar.setStyle(background);
        toolbar.setStyle(background);
        toolbar.getChildren().addAll(leftToolbar, rightToolbar);
        return toolbar;
    }

    private Optional<File> selectFile(boolean snp, Optional<File> originalFile) {
        File f;
        FileDialog dialog = new FileDialog(getSite().getShell(), SWT.SAVE);
        originalFile.ifPresent(file -> dialog.setFileName(file.getAbsolutePath()));
        if (snp) {
            dialog.setFilterExtensions(new String[] { "*" + SnapshotViewerController.FEXT_SNP });
            dialog.setFilterNames(new String[] { "Single Snapshot (*.snp)" });
        } else {
            dialog.setFilterExtensions(new String[] { "*" + SnapshotViewerController.FEXT_CSV });
            dialog.setFilterNames(new String[] { "All snapshots (*.csv)" });
        }
        do {
            String file = dialog.open();
            if (file == null) {
                return Optional.empty();
            } else {
                f = new File(file);
                if (f.exists()) {
                    int ans = FXMessageDialog.openYesNoCancel(getSite().getShell(), "Overwrite File",
                        "The file '" + file + "' already exists. Do you want to overwrite it?");
                    if (ans == 0) {
                        return Optional.of(f); // overwrite
                    } else if (ans == 2) {
                        return Optional.empty(); // cancel
                    }
                } else {
                    return Optional.of(f);
                }
            }
        } while (true);
    }

    private Node createMainControlPane() {
        GridPane left = new GridPane();
        left.setPadding(new Insets(5, 5, 5, 5));
        left.setVgap(5);
        left.setHgap(5);
        left.setAlignment(Pos.TOP_LEFT);
        commentField = new StaticTextArea();
        commentField.setPrefWidth(200);
        commentField.setMaxWidth(Double.MAX_VALUE);
        commentField.setPrefRowCount(2);
        setGridConstraints(commentField, true, true, Priority.SOMETIMES, Priority.ALWAYS);
        GridPane.setMargin(commentField, new Insets(0, 5, 0, 0));
        creatorField = new StaticTextField();
        int width = FXUtilities.measureStringWidth("0000 MMM 00 00:00:00 ", creatorField.getFont()) + 20;
        creatorField.setPrefWidth(width);
        GridPane.setMargin(creatorField, new Insets(0, 5, 0, 0));
        dateField = new StaticTextField();
        dateField.setPrefWidth(width);
        GridPane.setMargin(dateField, new Insets(0, 5, 0, 0));
        parametersField = new StaticTextArea();
        parametersField.setPrefWidth(200);
        parametersField.setMaxWidth(Double.MAX_VALUE);
        parametersField.setPrefRowCount(2);
        setGridConstraints(parametersField, true, true, Priority.SOMETIMES, Priority.ALWAYS);
        GridPane.setMargin(parametersField, new Insets(0, 5, 0, 0));
        left.add(new Label("Comment:"), 0, 0);
        left.add(commentField, 1, 0, 1, 2);
        left.add(new Label("Creator:"), 2, 0);
        left.add(new Label("Timestamp:"), 2, 1);
        left.add(creatorField, 3, 0);
        left.add(dateField, 3, 1);
        left.add(new Label("Info:"), 4, 0);
        left.add(parametersField, 5, 0, 1, 2);
        GridPane grid = new GridPane();
        Node right = createButtonPane();
        GridPane.setHgrow(left, Priority.ALWAYS);
        GridPane.setHgrow(right, Priority.NEVER);
        GridPane.setFillHeight(right, true);
        GridPane.setFillWidth(left, true);
        grid.add(left, 0, 0);
        grid.add(right, 1, 0);
        return grid;
    }

    private Node createButtonPane() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5, 5, 5, 5));
        grid.setHgap(5);
        Button takeSnapshotButton = new Button();
        VBox box = new VBox();
        box.getChildren().addAll(new Label("Take"), new Label("Snapshot"));
        box.setAlignment(Pos.CENTER);
        takeSnapshotButton.setGraphic(box);
        takeSnapshotButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        takeSnapshotButton.setTooltip(new Tooltip("Take a new snapshot (locally store the live values)"));
        takeSnapshotButton.setOnAction(
            e -> SaveRestoreService.getInstance().execute("Take Snapshot", () -> controller.takeSnapshot()));

        saveSnapshotButton = new Button();
        box = new VBox();
        box.getChildren().addAll(new Label("Save"), new Label("Snapshot"));
        box.setAlignment(Pos.CENTER);
        saveSnapshotButton.setGraphic(box);
        saveSnapshotButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        saveSnapshotButton.setTooltip(new Tooltip("Save the snapshot values as a new revision"));
        saveSnapshotButton.setOnAction(e -> {
            // when user presses the big save button, only save one selected snapshot
            if (getEditorInput() instanceof IFileEditorInput || getEditorInput() instanceof IURIEditorInput) {
                final List<VSnapshot> snapshots = controller.getAllSnapshots();
                save(snapshots, false, snapshots.size() > 1);
                checkDirty();
            } else {
                final List<VSnapshot> snapshots = controller.getSnapshots(true);
                SaveRestoreService.getInstance().execute("Save Snapshot", () -> {
                    save(snapshots, false, false);
                    checkDirty();
                });
            }
        });
        saveSnapshotButton.disableProperty().bind(controller.snapshotSaveableProperty().not());

        Button restoreSnapshotButton = new Button("Restore");
        restoreSnapshotButton.setTooltip(new Tooltip("Set the stored values to PVs"));
        restoreSnapshotButton.setOnAction(e -> SaveRestoreService.getInstance().execute("Restore Snapshot", () -> {
            // allow to restore non saved snapshots as well
            List<VSnapshot> snapshots = controller.getAllSnapshots();
            if (snapshots.isEmpty()) {
                return;
            } else if (snapshots.size() == 1) {
                controller.restoreSnapshot(snapshots.get(0));
            } else {
                FXComboInputDialog.pick(getSite().getShell(), "Select Snapshot",
                    "Select the snapshot that you wish to restore", snapshots.get(0), snapshots)
                    .ifPresent(controller::restoreSnapshot);
            }
        }));
        restoreSnapshotButton.disableProperty().bind(controller.snapshotRestorableProperty().not());

        restoreSnapshotButton.setMaxHeight(Integer.MAX_VALUE);
        saveSnapshotButton.setMaxHeight(Integer.MAX_VALUE);
        takeSnapshotButton.setMaxHeight(Integer.MAX_VALUE);
        setGridConstraints(restoreSnapshotButton, true, true, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        setGridConstraints(takeSnapshotButton, true, true, HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        setGridConstraints(saveSnapshotButton, true, true, HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        grid.add(restoreSnapshotButton, 0, 0);
        grid.add(takeSnapshotButton, 1, 0);
        grid.add(saveSnapshotButton, 2, 0);
        return grid;
    }

    private Table createTable() {
        table = new Table(controller);
        table.setOnDragOver(e -> {
            if (e.getGestureSource() != table && (e.getDragboard().hasContent(SnapshotDataFormat.INSTANCE)
                || e.getDragboard().hasContent(DataFormat.FILES))) {
                e.acceptTransferModes(TransferMode.ANY);
            }
            e.consume();
        });
        table.setOnDragDropped(e -> {
            if (e.getDragboard().hasContent(SnapshotDataFormat.INSTANCE)) {
                Snapshot s = (Snapshot) e.getDragboard().getContent(SnapshotDataFormat.INSTANCE);
                if (s != null) {
                    final DataProvider provider = SaveRestoreService.getInstance()
                        .getDataProvider(s.getSaveSet().getDataProviderId()).getProvider();
                    SaveRestoreService.getInstance().execute("Load snapshot data", () -> {
                        try {
                            addSnapshot(provider.getSnapshotContent(s));
                        } catch (DataProviderException ex) {
                            ActionManager.reportException(ex, getSite().getShell());
                        }
                    });
                }
            } else if (e.getDragboard().hasContent(DataFormat.FILES)) {
                @SuppressWarnings("unchecked")
                final List<File> files = (List<File>) e.getDragboard().getContent(DataFormat.FILES);
                if (!files.isEmpty()) {
                    SaveRestoreService.getInstance().execute("Open file", () -> files
                        .forEach(c -> controller.openFromFile(c).ifPresent(SnapshotViewerEditor.this::addSnapshot)));
                }
            }
        });
        table.setOnMouseReleased(e -> contextMenu.setVisible(e.getButton() == MouseButton.SECONDARY));

        return table;
    }

    private void addSnapshot(VSnapshot data) {
        addOrSetSnapshot(data, true, true);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.ISnapshotReceiver#addSnapshot(org.csstudio.saverestore.data.VSnapshot, boolean)
     */
    @Override
    public void addSnapshot(VSnapshot data, boolean useBackgroundThread) {
        addOrSetSnapshot(data, true, useBackgroundThread);
    }

    private void addOrSetSnapshot(final VSnapshot data, final boolean add, final boolean useBackgroundThread) {
        Runnable r = () -> {
            final List<TableEntry> entries = add ? controller.addSnapshot(data) : controller.setSnapshot(data);
            final List<VSnapshot> snapshots = controller.getAllSnapshots();
            Platform.runLater(() -> table.updateTable(entries, snapshots, controller.isShowReadbacks(),
                controller.isShowStoredReadbacks()));
        };
        if (useBackgroundThread) {
            SaveRestoreService.getInstance().execute("Open Snapshot", r);
        } else {
            r.run();
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXEditorPart#setFxFocus()
     */
    @Override
    public void setFxFocus() {
        if (table != null) {
            table.requestFocus();
        }
    }
}
