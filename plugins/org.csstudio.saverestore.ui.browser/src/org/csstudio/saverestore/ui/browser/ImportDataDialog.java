package org.csstudio.saverestore.ui.browser;

import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.ui.util.RepositoryTree;
import org.csstudio.ui.fx.util.FXBaseDialog;
import org.eclipse.ui.IWorkbenchPart;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * <code>ImportDataDialog</code> is a dialog that allows to select the location from which the data will be copied. The
 * location is returned in a form of a {@link BeamlineSet} whout it might not be an actual beamlineset. If it is just a
 * folder it will be missing the last part of the path. If it is a baselevel, it will not have the path defined at all.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ImportDataDialog extends FXBaseDialog<BeamlineSet> {

    private IWorkbenchPart owner;
    private RepositoryTree treeView;
    private TextField fullNameField;

    /**
     * Construct a new repository tree browser.
     *
     * @param owner the owner view
     */
    public ImportDataDialog(IWorkbenchPart owner) {
        super(owner.getSite().getShell(), "Select Source", "Select the location from which to copy the data", null,
            e -> e == null || e.getBranch() == null
                || e.getPath().length == 0
                    ? "Select a single item"
                    : e.getBranch() != null && !e.getBaseLevel().isPresent()
                        && (e.getPath().length == 0 || e.getPath()[0].isEmpty()) ? "Select more than just a branch"
                            : null);
        this.owner = owner;
    }

    private Node createContents() {
        treeView = new RepositoryTree(owner, false, null);

        treeView.getSelectionModel().selectedItemProperty().addListener((a, o, n) -> validateInput());

        fullNameField = new TextField() {
            public void requestFocus() {
            }
        };
        fullNameField.setMaxWidth(Double.MAX_VALUE);
        fullNameField.setEditable(false);
        fullNameField.setFocusTraversable(false);

        Label fullNameLabel = new Label("Selected Item:");

        GridPane pane = new GridPane();
        pane.setVgap(5);
        pane.setHgap(3);

        GridPane.setFillHeight(treeView, true);
        GridPane.setFillHeight(fullNameField, false);
        GridPane.setFillHeight(fullNameLabel, false);
        GridPane.setFillWidth(treeView, true);
        GridPane.setFillWidth(fullNameField, true);
        GridPane.setFillWidth(fullNameLabel, false);
        GridPane.setHgrow(treeView, Priority.ALWAYS);
        GridPane.setHgrow(fullNameField, Priority.ALWAYS);
        GridPane.setHgrow(fullNameLabel, Priority.NEVER);
        GridPane.setVgrow(treeView, Priority.ALWAYS);
        GridPane.setVgrow(fullNameField, Priority.NEVER);
        GridPane.setVgrow(fullNameLabel, Priority.NEVER);

        pane.add(treeView, 0, 0, 2, 1);
        pane.add(fullNameLabel, 0, 1, 1, 1);
        pane.add(fullNameField, 1, 1, 1, 1);

        return pane;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXBaseDialog#getValueFromComponent()
     */
    @Override
    protected BeamlineSet getValueFromComponent() {
        BeamlineSet set = treeView.getValueFromComponent();
        if (set == null) {
            fullNameField.setText("");
        } else {
            fullNameField.setText(set.getFullyQualifiedName());
        }

        return set;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXBaseDialog#setValueToComponent(java.lang.Object)
     */
    @Override
    protected void setValueToComponent(BeamlineSet value) {
        treeView.setValueToComponent(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.saverestore.ui.util.FXBaseDialog#getScene()
     */
    @Override
    protected Scene getScene() {
        BorderPane pane = new BorderPane(createContents());
        Scene scene = new Scene(pane);
        return scene;
    }
}
