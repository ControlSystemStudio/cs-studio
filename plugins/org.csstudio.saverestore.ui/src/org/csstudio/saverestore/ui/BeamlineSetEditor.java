package org.csstudio.saverestore.ui;

import java.util.List;

import org.csstudio.saverestore.BeamlineSetData;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import javafx.application.Platform;
import javafx.embed.swt.FXCanvas;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;

/**
 *
 * <code>BeamlineSetEditor</code> is an implementation of the {@link EditorPart} which allows editing the
 * beamline set. User is allowed to change the description and the list of pvs.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class BeamlineSetEditor extends EditorPart {

    public static final String ID = "org.csstudio.saverestore.ui.editor.beamlineseteditor";

    private Scene scene;
    private BorderPane contentPane;
    private TextArea descriptionArea;
    private TextArea contentArea;

    private BeamlineSetData data;

    private boolean dirty = false;

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave(IProgressMonitor monitor) {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    @Override
    public void doSaveAs() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#isDirty()
     */
    @Override
    public boolean isDirty() {
        return dirty;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);
        setPartName(input.getName());
        BeamlineSetData data = input.getAdapter(BeamlineSetData.class);
        if (data != null) {
            setBeamlineSet(data);
        }
    }

    private void setBeamlineSet(final BeamlineSetData data) {
        this.data = data;
        Platform.runLater(() -> {
            String description = data.getDescription();
            List<String> list = data.getPVList();
            descriptionArea.setText(description);
            final StringBuilder sb = new StringBuilder(list.size()*40);
            list.forEach(e -> sb.append(e).append('\n'));
            contentArea.setText(sb.toString());
        });
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(Composite parent) {
        FXCanvas canvas = new FXCanvas(parent, SWT.NONE);
        canvas.setScene(createFxScene());
    }

    private Scene createFxScene() {
        contentPane = new BorderPane();
        contentPane.setCenter(createCenterPane());
//        GridPane topPane = new GridPane();
//        Node top = createTopPane();
//        Node bottom = createButtonPane();
//        GridPane.setHgrow(top, Priority.ALWAYS);
//        GridPane.setHgrow(bottom, Priority.NEVER);
//        GridPane.setFillHeight(bottom, true);
//        topPane.add(top,0,0);
//        topPane.add(bottom,1,0);
//
//        contentPane.setTop(topPane);
        scene = new Scene(contentPane);
        return scene;
    }

    private Node createCenterPane() {
        GridPane grid = new GridPane();
        grid.setVgap(3);
        grid.setPadding(new Insets(5,5,5,5));
        Label descriptionLabel = new Label("Description:");
        descriptionLabel.setFont(Font.font(15));
        descriptionArea = new TextArea();
        descriptionArea.setEditable(true);
        descriptionArea.setTooltip(new Tooltip("Brief description of this beamline set"));
        descriptionArea.setMaxWidth(Double.MAX_VALUE);
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefRowCount(4);
        descriptionArea.setOnKeyReleased(e -> {
            dirty = true;
            firePropertyChange(PROP_DIRTY);
        });

        Label contentLabel = new Label("PV List:");
        contentLabel.setFont(Font.font(15));
        contentArea = new TextArea();
        contentArea.setEditable(true);
        contentArea.setTooltip(new Tooltip("The list of PVs in this beamline set"));
        contentArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        contentArea.setWrapText(false);
        contentArea.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                contentArea.appendText("\n");
            }
            dirty = true;
            firePropertyChange(PROP_DIRTY);
        });

        GridPane.setHalignment(descriptionLabel, HPos.LEFT);
        GridPane.setHalignment(contentLabel, HPos.LEFT);
        GridPane.setHalignment(descriptionArea, HPos.LEFT);
        GridPane.setHalignment(contentArea, HPos.LEFT);
        GridPane.setFillHeight(descriptionLabel, true);
        GridPane.setFillHeight(contentLabel, true);
        GridPane.setFillHeight(descriptionArea, true);
        GridPane.setFillHeight(contentArea, true);
        GridPane.setFillWidth(descriptionLabel, true);
        GridPane.setFillWidth(contentLabel, true);
        GridPane.setFillWidth(descriptionArea, true);
        GridPane.setFillWidth(contentArea, true);

        GridPane.setVgrow(descriptionLabel, Priority.NEVER);
        GridPane.setVgrow(contentLabel,  Priority.NEVER);
        GridPane.setVgrow(descriptionArea,  Priority.NEVER);
        GridPane.setVgrow(contentArea,  Priority.ALWAYS);
        GridPane.setHgrow(descriptionLabel, Priority.NEVER);
        GridPane.setHgrow(contentLabel,  Priority.NEVER);
        GridPane.setHgrow(descriptionArea,  Priority.NEVER);
        GridPane.setHgrow(contentArea,  Priority.ALWAYS);

        grid.add(descriptionLabel, 0, 0);
        grid.add(descriptionArea, 0, 1);
        grid.add(contentLabel, 0, 2);
        grid.add(contentArea, 0, 3);

        return grid;
    }


    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
    }

}
