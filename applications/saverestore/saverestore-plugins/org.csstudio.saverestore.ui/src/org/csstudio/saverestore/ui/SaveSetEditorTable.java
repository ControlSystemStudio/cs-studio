package org.csstudio.saverestore.ui;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

/**
 *
 * @author Kunal Shroff
 *
 */
public class SaveSetEditorTable extends TableView<ObservableSaveSetEntry> implements ISelectionProvider {

    private final List<ISelectionChangedListener> selectionChangedListener = new CopyOnWriteArrayList<>();

    public SaveSetEditorTable() {
        createTable();
    }

    private void createTable() {
        TableColumn<ObservableSaveSetEntry, String> pvName = new TableColumn<>("PV Name");
        pvName.setCellValueFactory(new PropertyValueFactory<>("pvname"));
        pvName.setCellFactory(TextFieldTableCell.forTableColumn());
        pvName.setOnEditCommit((CellEditEvent<ObservableSaveSetEntry, String> t) -> {
            ((ObservableSaveSetEntry) t.getTableView().getItems().get(t.getTablePosition().getRow()))
                    .setPvname(t.getNewValue());
        });
        pvName.prefWidthProperty().bind(this.widthProperty().multiply(0.25));

        TableColumn<ObservableSaveSetEntry, String> readback = new TableColumn<>("Readback");
        readback.setCellValueFactory(new PropertyValueFactory<>("readback"));
        readback.setCellFactory(TextFieldTableCell.forTableColumn());
        readback.setOnEditCommit((CellEditEvent<ObservableSaveSetEntry, String> t) -> {
            ((ObservableSaveSetEntry) t.getTableView().getItems().get(t.getTablePosition().getRow()))
                    .setReadback(t.getNewValue());
        });
        readback.prefWidthProperty().bind(this.widthProperty().multiply(0.25));

        TableColumn<ObservableSaveSetEntry, String> delta = new TableColumn<>("Delta");
        delta.setCellValueFactory(new PropertyValueFactory<>("delta"));
        delta.setCellFactory(TextFieldTableCell.forTableColumn());
        delta.setOnEditCommit((CellEditEvent<ObservableSaveSetEntry, String> t) -> {
            ((ObservableSaveSetEntry) t.getTableView().getItems().get(t.getTablePosition().getRow()))
                    .setDelta(t.getNewValue());
        });
        delta.prefWidthProperty().bind(this.widthProperty().multiply(0.25));

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
        readonly.prefWidthProperty().bind(this.widthProperty().multiply(0.25));

        this.getColumns().addAll(pvName, readback, delta, readonly);
        this.setEditable(true);
    }

    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        selectionChangedListener.add(listener);
    }

    @Override
    public ISelection getSelection() {
        ObservableList<ObservableSaveSetEntry> s = this.getSelectionModel().getSelectedItems();
        return new StructuredSelection(s.toArray(new ObservableSaveSetEntry[s.size()]));
    }

    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        selectionChangedListener.remove(listener);
    }

    @Override
    public void setSelection(ISelection selection) {
        // TODO Auto-generated method stub

    }

}
