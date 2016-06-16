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
package org.csstudio.ui.fx.util;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.util.Geometry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 *
 * <code>FXDetailsDialog</code> is a simple dialog, which displays a message and provides a button to expand the dialog
 * displaying a more details message.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class FXDetailsDialog extends IconAndMessageDialog {

    private static final String SHOW_DETAILS_LABEL = IDialogConstants.SHOW_DETAILS_LABEL.replace("&", "");
    private static final String HIDE_DETAILS_LABEL = IDialogConstants.HIDE_DETAILS_LABEL.replace("&", "");

    /**
     * Convenience method to open the dialog
     *
     * @param parent the parent shell of the dialog, or <code>null</code> if none
     * @param title the dialog's title, or <code>null</code> if none
     * @param message the message
     * @param details the detailed message
     */
    public static void open(final Shell parent, final String title, final String message, final String details) {
        parent.getDisplay().syncExec(() -> {
            FXDetailsDialog dialog = new FXDetailsDialog(parent, title, message, details);
            dialog.open();
        });
    }

    private final String title;
    private final String details;
    private boolean showingDetails = false;
    private Button detailsButton;
    private Composite sceneComposite;

    /**
     * Creates an dialog with OK and Details buttons. Note that the dialog will have no visual representation (no
     * widgets) until it is told to open.
     *
     * @param parentShell the parent shell, or <code>null</code> to create a top-level shell
     * @param dialogTitle the dialog title, or <code>null</code> if none
     * @param dialogMessage the dialog message, or <code>null</code> if none
     */
    public FXDetailsDialog(Shell parentShell, String dialogTitle, String dialogMessage, String details) {
        super(parentShell);
        this.title = dialogTitle;
        this.message = dialogMessage;
        this.details = details;
        setBlockOnOpen(true);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (title != null) {
            shell.setText(title);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        ((GridLayout) parent.getLayout()).numColumns++;
        FXUtilities.createFXBridge(parent, this::createFXButtonBar);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     */
    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.DETAILS_ID) {
            if (showingDetails) {
                hideDetailsPanel();
            } else {
                showDetailsPanel();
            }
        } else {
            super.buttonPressed(buttonId);
        }
    }

    private void showDetailsPanel() {
        showingDetails = true;
        detailsButton.setText(HIDE_DETAILS_LABEL);
        sceneComposite.setVisible(true);
        getShell().setSize(getShell().getSize().x, getShell().getSize().y * 2);

    }

    private void hideDetailsPanel() {
        showingDetails = false;
        detailsButton.setText(SHOW_DETAILS_LABEL);
        sceneComposite.setVisible(false);
        getShell().setSize(getShell().getSize().x, getShell().getSize().y / 2);
    }

    private Scene createFXButtonBar(Composite parent) {
        detailsButton = new Button(SHOW_DETAILS_LABEL);
        detailsButton.setOnAction(e -> buttonPressed(IDialogConstants.DETAILS_ID));
        Button okButton = new Button(IDialogConstants.OK_LABEL);
        okButton.setOnAction(e -> buttonPressed(IDialogConstants.OK_ID));
        int size = FXUtilities.measureStringWidth(SHOW_DETAILS_LABEL, detailsButton.getFont()) + 25;
        okButton.setPrefWidth(size);
        detailsButton.setPrefWidth(size);
        GridPane pane = new GridPane();
        pane.setHgap(10);
        FXUtilities.setGridConstraints(okButton, false, false, HPos.RIGHT, VPos.CENTER, Priority.ALWAYS,
            Priority.NEVER);
        pane.add(okButton, 0, 0);
        pane.add(detailsButton, 1, 0);
        return new Scene(pane);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        createMessageArea(parent);
        sceneComposite = new Composite(parent, SWT.NONE);
        GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
            | GridData.VERTICAL_ALIGN_FILL);
        data.horizontalSpan = 2;
        sceneComposite.setLayoutData(data);
        sceneComposite.setLayout(new GridLayout(1, true));
        FXUtilities.createFXBridge(sceneComposite, this::getScene);
        sceneComposite.setVisible(false);
        applyDialogFont(sceneComposite);
        return sceneComposite;
    }

    private Scene getScene(Composite parent) {
        StaticTextArea textArea = new StaticTextArea();
        textArea.setText(details);
        return new Scene(textArea);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.dialogs.Dialog#getInitialLocation(org.eclipse.swt.graphics.Point)
     */
    @Override
    protected Point getInitialLocation(Point initialSize) {
        Composite theParent = getShell().getParent();

        Monitor monitor = getShell().getDisplay().getPrimaryMonitor();
        if (theParent != null) {
            monitor = theParent.getMonitor();
        }

        Rectangle monitorBounds = monitor.getClientArea();
        Point centerPoint;
        if (theParent == null) {
            centerPoint = Geometry.centerPoint(monitorBounds);
        } else {
            centerPoint = Geometry.centerPoint(theParent.getBounds());
        }

        return new Point(centerPoint.x - (initialSize.x / 2), Math.max(monitorBounds.y,
            Math.min(centerPoint.y - (initialSize.y / 2), monitorBounds.y + monitorBounds.height - initialSize.y)));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.dialogs.Dialog#getInitialSize()
     */
    @Override
    protected Point getInitialSize() {
        Point p = super.getInitialSize();
        p.y = p.y / 2;
        return p;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.dialogs.IconAndMessageDialog#getImage()
     */
    @Override
    protected Image getImage() {
        return getErrorImage();
    }

}
