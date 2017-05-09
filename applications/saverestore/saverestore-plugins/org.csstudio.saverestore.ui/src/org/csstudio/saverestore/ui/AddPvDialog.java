package org.csstudio.saverestore.ui;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.saverestore.FileUtilities;
import org.csstudio.saverestore.data.SaveSetEntry;
import org.csstudio.saverestore.ui.util.RunnableWithID;
import org.csstudio.ui.fx.util.FXBaseDialog;
import org.csstudio.ui.fx.util.FXUtilities;
import org.csstudio.ui.fx.util.InputValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

/**
 * A simple dialog using the old content text area to allow users to add one or
 * more pv's or save set entry to the save set config being created or edited.
 *
 * @author Kunal Shroff
 *
 */
public class AddPvDialog extends FXBaseDialog<List<SaveSetEntry>> {

    private final PseudoClass alertedPseudoClass = PseudoClass.getPseudoClass("alerted");

    public AddPvDialog(Shell parentShell, String dialogTitle, String dialogMessage, List<SaveSetEntry> initialValue,
            InputValidator<List<SaveSetEntry>> validator) {
        super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
        }

    @Override
    protected List<SaveSetEntry> getValueFromComponent() {
        return validateString(contentArea.getText(), false);
    }

    @Override
    protected void setValueToComponent(List<SaveSetEntry> value) {
        // TODO add this when we want the dialog initialized from some other input (selection/clipboard)
    }

    private TextArea contentArea;

    @Override
    protected Scene getScene(Composite parent) {

        GridPane pane = new GridPane();
        contentArea = new TextArea();
        contentArea.getStylesheets()
            .add(SaveSetEditor.class.getResource(SnapshotViewerEditor.STYLE).toExternalForm());
        contentArea.setEditable(true);
        contentArea.setTooltip(new Tooltip("The list of PVs in this save set"));
        contentArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        contentArea.setWrapText(false);
        contentArea.textProperty().addListener((a, o, n) -> {
            final String content = n;
            // execute the content check in the background thread, but only if the same task is not already being
            // executed
            Activator.getDefault().getBackgroundWorker().execute(new RunnableWithID() {
                @Override
                public void run() {
                    final List<SaveSetEntry> data = validateString(content, false);
                    Platform.runLater(() -> contentArea.pseudoClassStateChanged(alertedPseudoClass, data == null));
                }
            });
        });
        pane.setMaxWidth(Double.MAX_VALUE);
        pane.setVgap(5);
        pane.add(contentArea, 0, 0);
        pane.setPrefWidth(getInitialSize().x);
        pane.setStyle(FXUtilities.toBackgroundColorStyle(parent.getBackground()));
        return new Scene(pane);
    }

    private List<SaveSetEntry> validateString(String text, boolean markError) {
        String[] content = text.split("\\n");
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
                if (markError) {
                    // if marError == true we are in the UI thread
                    int idx = text.indexOf(s);
                    if (idx > -1) {
                        contentArea.selectRange(idx, idx + s.length());
                    }
                }
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
