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
package org.csstudio.saverestore.ui;

import static org.csstudio.ui.fx.util.FXUtilities.setGridConstraints;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.csstudio.saverestore.DataProviderWrapper;
import org.csstudio.saverestore.FileUtilities;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.data.SaveSet;
import org.csstudio.saverestore.data.SaveSetData;
import org.csstudio.saverestore.data.SaveSetEntry;
import org.csstudio.saverestore.ui.TableViewSample.ObservableSaveSetEntry;
import org.csstudio.saverestore.ui.util.RunnableWithID;
import org.csstudio.ui.fx.util.FXEditorPart;
import org.csstudio.ui.fx.util.FXMessageDialog;
import org.csstudio.ui.fx.util.StaticTextField;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.util.Callback;

/**
 *
 * <code>SaveSetEditor</code> is an implementation of the {@link EditorPart}
 * which allows editing the save sets. User is allowed to change the description
 * and the list of pvs in the set.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SaveSetEditor extends FXEditorPart implements IShellProvider {

    public static final String ID = "org.csstudio.saverestore.ui.editor.saveseteditor";

    private final PseudoClass alertedPseudoClass = PseudoClass.getPseudoClass("alerted");

    private TextArea descriptionArea;
    private TableView<ObservableSaveSetEntry> contentTable;

    private boolean dirty;
    private final SaveSetController controller;

    /**
     * Constructs a new save set editor.
     */
    public SaveSetEditor() {
        this.controller = new SaveSetController(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.csstudio.ui.fx.util.FXEditorPart#createPartControl(org.eclipse.swt.
     * widgets.Composite)
     */
    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, "org.csstudio.saverestore.ui.help.saveseteditor");
    }

    /**
     * Checks if the data provider of edited save set supports saving or editing
     * of save sets. If yes, the method returns true, otherwise it returns
     * false.
     *
     * @return true if save set can be saved or false otherwise
     */
    private boolean canExecute() {
        return controller.getSavedSaveSetData().filter(d -> {
            DataProviderWrapper wrapper = SaveRestoreService.getInstance()
                    .getDataProvider(d.getDescriptor().getDataProviderId());
            if (wrapper == null) {
                return false;
            }
            if (!wrapper.getProvider().isSaveSetSavingSupported()) {
                FXMessageDialog.openInformation(getSite().getShell(), "Save Save Set",
                        wrapper.getName() + " does not support editing or saving save sets.");
                return false;
            }
            return true;
        }).isPresent();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
     * IProgressMonitor)
     */
    @Override
    public void doSave(final IProgressMonitor monitor) {
        Optional<SaveSetData> d = controller.getSavedSaveSetData();
        if (!d.isPresent() || d.get().getStoredDate() == null) {
            doSaveAs();
        } else if (canExecute()) {
            monitor.beginTask("Save save set", 1);
            final SaveSetData data = createData();
            if (data == null) {
                MessageDialog.openError(getSite().getShell(), "Save Save Set",
                        "There is an error in the file contents.");
                return;
            }
            if (data.equalContent(d.get())) {
                MessageDialog.openInformation(getSite().getShell(), "Save Save Set",
                        "There are no changes between the saved and this save set.");
                setDirty(false);
                return;
            }

            SaveRestoreService.getInstance().execute("Save Save Set", () -> {
                final Optional<SaveSetData> ds = controller.save(data);
                getSite().getShell().getDisplay().asyncExec(() -> {
                    monitor.done();
                    ds.ifPresent(e -> setInput(new SaveSetEditorInput(e)));
                });
            });
        }
    }

    private SaveSetData createData() {
        return createData(descriptionArea.getText().trim(), contentTable.getItems(), true);
    }

    private SaveSetData createData(String description, ObservableList<ObservableSaveSetEntry> observableList, boolean markError) {
        List<SaveSetEntry> entries = observableList.stream().map(ObservableSaveSetEntry::getEntry)
                .collect(Collectors.toList());

        Optional<SaveSetData> bsd = controller.getSavedSaveSetData();
        SaveSet descriptor = bsd.isPresent() ? bsd.get().getDescriptor() : new SaveSet();
        return new SaveSetData(descriptor, entries, description);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    @Override
    public void doSaveAs() {
        if (canExecute()) {
            final SaveSetData data = createData();
            if (data == null) {
                MessageDialog.openError(getSite().getShell(), "Save Save Set",
                        "There is an error in the file contents.");
                return;
            }
            if (data.equalContent(controller.getSavedSaveSetData().orElse(null)) && MessageDialog.openQuestion(
                    getSite().getShell(), "Save Save Set As",
                    "Theare are no changes between the saved and this save set. Are you sure you want to save it as a new save set?")) {
                setDirty(false);
                return;
            }
            new RepositoryTreeBrowser(this, data.getDescriptor()).openAndWait()
                    .ifPresent(saveSet -> SaveRestoreService.getInstance().execute("Save Save Set",
                            () -> controller.save(new SaveSetData(saveSet, data.getEntries(), data.getDescription()))
                                    .ifPresent(d -> getSite().getShell().getDisplay()
                                            .asyncExec(() -> setInput(new SaveSetEditorInput(d))))));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.part.EditorPart#isDirty()
     */
    @Override
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Sets the dirt property of this editor and fires a change event.
     *
     * @param dirty
     *            true if the editor should be marked dirty or false if not
     *            dirty
     */
    private void setDirty(boolean dirty) {
        this.dirty = dirty;
        firePropertyChange(PROP_DIRTY);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite,
     * org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);
    }

    private void init() {
        IEditorInput input = getEditorInput();
        SaveSetData data = input.getAdapter(SaveSetData.class);
        if (data != null) {
            SaveRestoreService.getInstance().execute("Open save set", () -> setSaveSet(data));
        }
        firePropertyChange(PROP_TITLE);
    }

    ObservableList<ObservableSaveSetEntry> observableList;

    private void setSaveSet(final SaveSetData data) {
        if (descriptionArea != null) {
            List<SaveSetEntry> list = data.getEntries();
            observableList = FXCollections.observableArrayList();
            final StringBuilder sb = new StringBuilder(list.size() * 200);
            list.forEach((e) -> {
                sb.append(e).append('\n');
                observableList.add(new ObservableSaveSetEntry(
                        new SaveSetEntry(e.getPVName(), e.getReadback(), e.getDelta(), e.isReadOnly())));
            });
            

            Platform.runLater(() -> {
                controller.setSavedSaveSetData(data);
                descriptionArea.setText(data.getDescription());
//                contentArea.setText(sb.toString().trim());
                contentTable.setItems(observableList);
                setDirty(false);
            });
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
     */
    @Override
    protected void setInput(IEditorInput input) {
        super.setInput(input);
        setPartName(input.getName());
        firePropertyChange(PROP_INPUT);
        init();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXEditorPart#createFxScene()
     */
    @Override
    protected Scene createFxScene() {
        Scene scene = new Scene(new BorderPane(createCenterPane()));
        init();
        return scene;
    }

    private ObservableList<ObservableSaveSetEntry> data;

    private Node createCenterPane() {
        GridPane grid = new GridPane();
        grid.setVgap(3);
        grid.setPadding(new Insets(5, 5, 5, 5));
        Label descriptionLabel = new Label("Description:");
        descriptionLabel.setFont(Font.font(15));
        descriptionArea = new TextArea();
        descriptionArea.setEditable(true);
        descriptionArea.setTooltip(new Tooltip("Brief description of this save set"));
        descriptionArea.setMaxWidth(Double.MAX_VALUE);
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefRowCount(4);
        descriptionArea.textProperty().addListener((a, o, n) -> setDirty(true));

        Label contentLabel = new Label("PV List:");
        contentLabel.setFont(Font.font(15));
//
//        contentArea = new TextArea();
//        contentArea.getStylesheets().add(SaveSetEditor.class.getResource(SnapshotViewerEditor.STYLE).toExternalForm());
//        contentArea.setEditable(true);
//        contentArea.setTooltip(new Tooltip("The list of PVs in this save set"));
//        contentArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
//        contentArea.setWrapText(false);
//        contentArea.textProperty().addListener((a, o, n) -> {
//            setDirty(true);
//            final String description = descriptionArea.getText();
//            final String content = n;
//            // execute the content check in the background thread, but only if
//            // the same task is not already being
//            // executed
//            Activator.getDefault().getBackgroundWorker().execute(new RunnableWithID() {
//                @Override
//                public void run() {
//                    final SaveSetData data = createData(description, content, false);
//                    Platform.runLater(() -> contentArea.pseudoClassStateChanged(alertedPseudoClass, data == null));
//                }
//
//                @Override
//                public int getID() {
//                    return SaveSetEditor.this.hashCode();
//                }
//            });
//        });
        TextField titleArea = new StaticTextField();
        titleArea.setText(FileUtilities.SAVE_SET_HEADER);

        GridPane contentPanel = new GridPane();
        contentPanel.setVgap(-1);
        setGridConstraints(titleArea, true, false, Priority.ALWAYS, Priority.NEVER);
//        setGridConstraints(contentArea, true, true, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        contentPanel.add(titleArea, 0, 0);
//        contentPanel.add(contentArea, 0, 1);

        contentTable = new TableView<ObservableSaveSetEntry>();
        contentTable.getProperties().addListener(new MapChangeListener<Object, Object>() {

            @Override
            public void onChanged(
                    javafx.collections.MapChangeListener.Change<? extends Object, ? extends Object> change) {
                setDirty(true);
                final String description = descriptionArea.getText();
                Activator.getDefault().getBackgroundWorker().execute(new RunnableWithID() {
                    @Override
                    public void run() {
//                        final SaveSetData data = createData(description, content, false);
//                        Platform.runLater(() -> contentArea.pseudoClassStateChanged(alertedPseudoClass, data == null));
                    }

                    @Override
                    public int getID() {
                        return SaveSetEditor.this.hashCode();
                    }
                });
            }

        });

        TableColumn<ObservableSaveSetEntry, String> pvName = new TableColumn<>("PV Name");
        pvName.setCellValueFactory(new PropertyValueFactory<>("pvname"));
        pvName.setCellFactory(TextFieldTableCell.forTableColumn());
        pvName.setOnEditCommit((CellEditEvent<ObservableSaveSetEntry, String> t) -> {
            ((ObservableSaveSetEntry) t.getTableView().getItems().get(t.getTablePosition().getRow()))
                    .setPvname(t.getNewValue());
        });
        pvName.prefWidthProperty().bind(contentTable.widthProperty().multiply(0.25));

        TableColumn<ObservableSaveSetEntry, String> readback = new TableColumn<>("Readback");
        readback.setCellValueFactory(new PropertyValueFactory<>("readback"));
        readback.setCellFactory(TextFieldTableCell.forTableColumn());
        readback.setOnEditCommit((CellEditEvent<ObservableSaveSetEntry, String> t) -> {
            ((ObservableSaveSetEntry) t.getTableView().getItems().get(t.getTablePosition().getRow()))
                    .setReadback(t.getNewValue());
        });
        readback.prefWidthProperty().bind(contentTable.widthProperty().multiply(0.25));

        TableColumn<ObservableSaveSetEntry, String> delta = new TableColumn<>("Delta");
        delta.setCellValueFactory(new PropertyValueFactory<>("delta"));
        delta.setCellFactory(TextFieldTableCell.forTableColumn());
        delta.setOnEditCommit((CellEditEvent<ObservableSaveSetEntry, String> t) -> {
            ((ObservableSaveSetEntry) t.getTableView().getItems().get(t.getTablePosition().getRow()))
                    .setDelta(t.getNewValue());
        });
        delta.prefWidthProperty().bind(contentTable.widthProperty().multiply(0.25));

        TableColumn<ObservableSaveSetEntry, Boolean> readonly = new TableColumn<>("Readonly");
        readonly.setCellValueFactory(
                new Callback<CellDataFeatures<ObservableSaveSetEntry, Boolean>, ObservableValue<Boolean>>() {
                    @Override
                    public ObservableValue<Boolean> call(CellDataFeatures<ObservableSaveSetEntry, Boolean> param) {
                        return param.getValue().readonly;
                    }
                });
        readonly.setCellFactory(
                new Callback<TableColumn<ObservableSaveSetEntry, Boolean>, TableCell<ObservableSaveSetEntry, Boolean>>() {
                    public TableCell<ObservableSaveSetEntry, Boolean> call(
                            TableColumn<ObservableSaveSetEntry, Boolean> p) {
                        return new CheckBoxTableCell<ObservableSaveSetEntry, Boolean>();
                    }
                });
        readonly.setOnEditCommit((CellEditEvent<ObservableSaveSetEntry, Boolean> t) -> {
            ((ObservableSaveSetEntry) t.getTableView().getItems().get(t.getTablePosition().getRow()))
                    .setReadonly(t.getNewValue());
        });
        readonly.prefWidthProperty().bind(contentTable.widthProperty().multiply(0.25));

        contentTable.getColumns().addAll(pvName, readback, delta, readonly);
        contentTable.setItems(data);
        contentTable.setEditable(true);
        
        setGridConstraints(descriptionLabel, true, true, HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER);
        setGridConstraints(contentLabel, true, true, HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER);
        setGridConstraints(descriptionArea, true, true, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
//        setGridConstraints(contentPanel, true, true, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        setGridConstraints(contentTable, true, true, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

        grid.add(descriptionLabel, 0, 0);
        grid.add(descriptionArea, 0, 1);
        grid.add(contentLabel, 0, 2);
//        grid.add(contentPanel, 0, 3);
        grid.add(contentTable, 0, 3);

        return grid;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXEditorPart#setFxFocus()
     */
    @Override
    public void setFxFocus() {
        if (contentTable != null) {
            contentTable.requestFocus();
        }
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
