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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.csstudio.saverestore.DataProviderWrapper;
import org.csstudio.saverestore.FileUtilities;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.data.SaveSet;
import org.csstudio.saverestore.data.SaveSetData;
import org.csstudio.saverestore.data.SaveSetEntry;
import org.csstudio.saverestore.ui.util.RunnableWithID;
import org.csstudio.ui.fx.util.FXEditorPart;
import org.csstudio.ui.fx.util.FXMessageDialog;
import org.csstudio.ui.fx.util.StaticTextField;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;

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

    // UI components
    private TextArea descriptionArea;
    private SaveSetEditorTable contentTable;
    private Menu contextMenu;

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

        MenuManager menu = new MenuManager();
        final Action openChartTableAction = new Action("Add Pv's") {
            @Override
            public void run() {
                // Launch a dialog with the previous text field and format
                final AddPvDialog dialog = new AddPvDialog(getShell(), "Add SaveSet Entries",
                        "Add MASAR entires including PV name, readbacks, deltas, and readonly", null, null);
                dialog.openAndWait().ifPresent(expr -> {
                    contentTable.getItems()
                            .addAll(expr.stream().map(ObservableSaveSetEntry::new).collect(Collectors.toList()));
                });
            }
        };
        menu.add(openChartTableAction);
        openChartTableAction.setEnabled(true);
        final Action removePV = new Action("Remove Pv's") {
            @Override
            public void run() {
                ISelection selection = contentTable.getSelection();
                if (selection instanceof IStructuredSelection) {
                    @SuppressWarnings("unchecked")
                    List<ObservableSaveSetEntry> selectedEntries = ((IStructuredSelection) contentTable.getSelection()).toList();
                    observableList.removeAll(selectedEntries);
                }
            }
        };
        menu.add(removePV);
        menu.add(new org.eclipse.jface.action.Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        menu.add(new org.eclipse.jface.action.Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        contextMenu = menu.createContextMenu(parent);
        parent.setMenu(contextMenu);
        getSite().registerContextMenu(menu, contentTable);

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
        TextField titleArea = new StaticTextField();
        titleArea.setText(FileUtilities.SAVE_SET_HEADER);

        GridPane contentPanel = new GridPane();
        contentPanel.setVgap(-1);
        setGridConstraints(titleArea, true, false, Priority.ALWAYS, Priority.NEVER);
        contentPanel.add(titleArea, 0, 0);

        contentTable = new SaveSetEditorTable();
        contentTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        contentTable.getProperties().addListener(new MapChangeListener<Object, Object>() {

            @Override
            public void onChanged(
                    javafx.collections.MapChangeListener.Change<? extends Object, ? extends Object> change) {
                setDirty(true);
                Activator.getDefault().getBackgroundWorker().execute(new RunnableWithID() {
                    @Override
                    public void run() {
                    }

                    @Override
                    public int getID() {
                        return SaveSetEditor.this.hashCode();
                    }
                });
            }

        });

        contentTable.setOnMouseReleased(e -> contextMenu.setVisible(e.getButton() == MouseButton.SECONDARY));
        contentTable.setOnKeyPressed(keyEvent -> {

            KeyCodeCombination copyKeyCodeCompination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);
            KeyCodeCombination pasteKeyCodeCompination = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY);

            if (copyKeyCodeCompination.match(keyEvent)) {
                try {
                    Map<DataFormat, Object> map = new HashMap<DataFormat, Object>();
                    ISelection selection = contentTable.getSelection();
                    if (selection instanceof IStructuredSelection) {
                        @SuppressWarnings("unchecked")
                        List<ObservableSaveSetEntry> selectedEntries = ((IStructuredSelection) contentTable
                                .getSelection()).toList();
                        map.put(SaveSetEntryFormatt, selectedEntries.stream().map(ObservableSaveSetEntry::getSaveString)
                                .collect(Collectors.joining(eol)));
                        map.put(DataFormat.PLAIN_TEXT, selectedEntries.stream().map(ObservableSaveSetEntry::getPvname)
                                .collect(Collectors.joining(",")));
                        Clipboard.getSystemClipboard().setContent(map);
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                keyEvent.consume();
            } else if (pasteKeyCodeCompination.match(keyEvent)) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                if (clipboard.hasContent(SaveSetEntryFormatt)) {
                    List<SaveSetEntry> list = parseClipboardString((String) clipboard.getContent(SaveSetEntryFormatt));
                    contentTable.getItems()
                            .addAll(list.stream().map(ObservableSaveSetEntry::new).collect(Collectors.toList()));
                } else if (clipboard.hasContent(DataFormat.PLAIN_TEXT)) {
                    List<SaveSetEntry> list = parseClipboardString(
                            ((String) clipboard.getContent(DataFormat.PLAIN_TEXT)).replace(",", eol));
                    contentTable.getItems()
                            .addAll(list.stream().map(ObservableSaveSetEntry::new).collect(Collectors.toList()));
                }
            }
        });

        setGridConstraints(descriptionLabel, true, true, HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER);
        setGridConstraints(contentLabel, true, true, HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER);
        setGridConstraints(descriptionArea, true, true, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
        setGridConstraints(contentTable, true, true, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

        grid.add(descriptionLabel, 0, 0);
        grid.add(descriptionArea, 0, 1);
        grid.add(contentLabel, 0, 2);
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

    @Override
    public Shell getShell() {
        return getSite().getShell();
    }

    /**
     * A dataformat for identifying save set entries in the clipboard
     */
    public static final DataFormat SaveSetEntryFormatt = new DataFormat("text/savesetentry");
    public static final String eol = System.getProperty("line.separator");

    /**
     * A utility method for parsing SaveSetEntries
     * @param text string to be parsed
     * @return parse out a list of {@link SaveSetEntry}s from a string
     */
    public static List<SaveSetEntry> parseClipboardString(String text) {

        String[] content = text.split(eol);
        if (content.length == 0) {
            return null;
        }
        String[] d = FileUtilities.split(content[0]);
        if (d == null) {
            return null;
        }
        int length = d.length;
        List<SaveSetEntry> entries = new ArrayList<>(content.length);
        for (String s : content) {
            s = s.trim();
            if (s.isEmpty()) {
                continue;
            }
            d = FileUtilities.split(s);
            if (d == null || d.length != length) {
                return null;
            }
            String name = d[0].trim();
            String readback = null, delta = null;
            boolean readOnly = false;
            if (d.length > 1) {
                readback = d[1].trim();
            }
            if (d.length > 2) {
                delta = d[2].trim();
            }
            if (d.length > 3) {
                readOnly = Boolean.valueOf(d[3].trim());
            }
            entries.add(new SaveSetEntry(name, readback, delta, readOnly));
        }
        return entries;
    }
}
