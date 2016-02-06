package org.csstudio.ui.fx.util;

import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * <code>Utilities</code> provides a set of utility methods for various java FX features.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class FXUtilities {

    private FXUtilities() {
    }

    /**
     * Constructs a new bridge between the parent and the scene provided by the sceneSupplier.
     *
     * @param parent the parent of the fx canvas
     * @param sceneSupplier the function that receives a parent and returns the scene to display by this bridge
     */
    public static void createFXBridge(Composite parent, Function<Composite, Scene> sceneSupplier) {
        SWT2FXBridge.createFXBridge(parent, sceneSupplier);
    }

    /**
     * Transforms the SWT colour to a hex string representing that colour.
     *
     * @param rgb the original colour to transform
     * @return hex representation of the colour
     */
    public static String toHexColor(RGB rgb) {
        return Integer.toHexString(rgb.red) + Integer.toHexString(rgb.green) + Integer.toHexString(rgb.blue);
    }

    /**
     * Transforms the SWT colour to a hex string representing that colour and returns a java FX CSS style for the node
     * background using that colour.
     *
     * @param colour the original colour to transform
     * @return CSS style for a node background
     */
    public static String toBackgroundColorStyle(Color colour) {
        return "-fx-background-color: #" + FXUtilities.toHexColor(colour.getRGB()) + ";";
    }

    /**
     * Transforms the SWT colour to a hex string representing that colour and returns a java FX CSS style for the node
     * background using that colour.
     *
     * @param colour the original colour to transform
     * @return CSS style for a node background
     */
    public static String toBackgroundColorStyle(RGB rgb) {
        return "-fx-background-color: #" + FXUtilities.toHexColor(rgb) + ";";
    }

    /**
     * Measures the width of the string when displayed with the given font.
     *
     * @param text the text to measure the width of
     * @param font the font to use for measurement
     * @return the width of the text in pixels
     */
    public static int measureStringWidth(String text, Font font) {
        Text mText = new Text(text);
        if (font != null) {
            mText.setFont(font);
        }
        return (int) mText.getLayoutBounds().getWidth();
    }

    /**
     * Set the GridPane constraints for the given component.
     *
     * @param component the component on which the constraints should be set
     * @param fillWidth {@link GridPane#setFillWidth(Node, Boolean)}
     * @param fillHeight {@link GridPane#setFillHeight(Node, Boolean)}
     * @param hgrow {@link GridPane#setHgrow(Node, Priority)}
     * @param vgrow {@link GridPane#setVgrow(Node, Priority)}
     */
    public static void setGridConstraints(Node component, boolean fillWidth, boolean fillHeight, Priority hgrow,
        Priority vgrow) {
        GridPane.setFillWidth(component, fillWidth);
        GridPane.setFillHeight(component, fillHeight);
        if (vgrow != null) {
            GridPane.setVgrow(component, vgrow);
        }
        if (hgrow != null) {
            GridPane.setHgrow(component, hgrow);
        }
    }

    /**
     * Set the GridPane constraints for the given component.
     *
     * @param component the component on which the constraints should be set
     * @param fillWidth {@link GridPane#setFillWidth(Node, Boolean)}
     * @param fillHeight {@link GridPane#setFillHeight(Node, Boolean)}
     * @param halignment {@link GridPane#setHalignment(Node, HPos)}
     * @param valignment {@link GridPane#setValignment(Node, VPos)}
     * @param hgrow {@link GridPane#setHgrow(Node, Priority)}
     * @param vgrow {@link GridPane#setVgrow(Node, Priority)}
     */
    public static void setGridConstraints(Node component, boolean fillWidth, boolean fillHeight, HPos halignment,
        VPos valignment, Priority hgrow, Priority vgrow) {
        if (valignment != null) {
            GridPane.setValignment(component, valignment);
        }
        if (halignment != null) {
            GridPane.setHalignment(component, halignment);
        }
        setGridConstraints(component, fillWidth, fillHeight, hgrow, vgrow);
    }

    /**
     * Creates a scene which contains buttons OK and Cancel. On action the buttons call the provided consumer. The
     * method returns a reference to the OK button. To obtain the scene, invoke {@link Button#getScene()}.
     *
     * @param action the action executed on button pressed (consumer is invoked using {@link IDialogConstants#OK_ID} or
     *            {@link IDialogConstants#CANCEL_ID}
     * @return reference to the OK button
     */
    public static Button createButtonBarWithOKandCancel(Consumer<Integer> action) {
        Button okButton = new Button(IDialogConstants.OK_LABEL);
        okButton.setOnAction(e -> action.accept(IDialogConstants.OK_ID));
        Button cancelButton = new Button(IDialogConstants.CANCEL_LABEL);
        cancelButton.setOnAction(e -> action.accept(IDialogConstants.CANCEL_ID));
        int size = FXUtilities.measureStringWidth(IDialogConstants.CANCEL_LABEL, cancelButton.getFont()) + 25;
        okButton.setPrefWidth(size);
        cancelButton.setPrefWidth(size);
        GridPane pane = new GridPane();
        pane.setHgap(10);
        setGridConstraints(okButton, false, false, HPos.RIGHT, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
        pane.add(okButton, 0, 0);
        pane.add(cancelButton, 1, 0);
        new Scene(pane);
        return okButton;
    }
}
