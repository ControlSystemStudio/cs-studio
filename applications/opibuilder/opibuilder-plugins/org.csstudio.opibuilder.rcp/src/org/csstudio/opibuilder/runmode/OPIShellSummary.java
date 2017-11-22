/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.runmode;

import java.util.Set;

import org.eclipse.fx.ui.workbench3.FXViewPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * RCP View summarising OPI shells.  Only one should be open
 * at any one time.
 *
 * @author Will Rogers
 */
@SuppressWarnings("nls")
public class OPIShellSummary extends FXViewPart {

    public static final String ID = "org.csstudio.opibuilder.opiShellSummary";

    private static final int MIN_SHOW_HIDE_BUTTON_SIZE = 60;  //px
    private static final int MIN_SHOW_HIDE_ALL_BUTTON_SIZE = 80;  //px

    private ScrollPane scrollpane;
    private GridPane grid;
    private Scene scene;
    private Label summaryLabel;
    private Button closeAllButton;
    private Button showAllButton;
    private boolean disposed = false;

    private Set<OPIShell> cachedShells;

    public OPIShellSummary() {
    }

    @Override
    public void dispose() {
        super.dispose();
        disposed = true;
    }

    public boolean isDisposed() {
        return disposed;
    }

    @Override
    public void init(final IViewSite site, final IMemento memento) throws PartInitException {
        super.init(site, memento);
        cachedShells = OPIShell.getAllShells();
        for (OPIShell shell : cachedShells) {
            shell.registerWithView(this);
        }
    }

    @Override
    protected javafx.scene.Scene createFxScene() {

        VBox container = new VBox();
        HBox header = new HBox();
        summaryLabel = new Label("No standalone OPIs open");
        summaryLabel.setStyle("-fx-font-weight: bold");
        summaryLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(summaryLabel, Priority.ALWAYS);

        closeAllButton = new Button("Close all");
        closeAllButton.setMinWidth(MIN_SHOW_HIDE_ALL_BUTTON_SIZE);
        closeAllButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                OPIShell.closeAll();
                update();
            }
        });
        showAllButton = new Button("Show all");
        showAllButton.setMinWidth(MIN_SHOW_HIDE_ALL_BUTTON_SIZE);
        showAllButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                OPIShell.showAll();
                update();
            }
        });

        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().add(summaryLabel);
        header.getChildren().add(showAllButton);
        header.getChildren().add(closeAllButton);
        container.getChildren().add(header);

        grid = new GridPane();
        grid.setHgap(2.0);

        ColumnConstraints nameColumn = new ColumnConstraints();
        nameColumn.setHgrow(Priority.ALWAYS);
        nameColumn.setHalignment(HPos.LEFT);
        grid.getColumnConstraints().add(nameColumn);

        ColumnConstraints showButtonColumn = new ColumnConstraints();
        showButtonColumn.setHalignment(HPos.CENTER);
        grid.getColumnConstraints().add(showButtonColumn);

        ColumnConstraints closeButtonColumn = new ColumnConstraints();
        closeButtonColumn.setHalignment(HPos.CENTER);
        grid.getColumnConstraints().add(closeButtonColumn);

        container.setPadding(new Insets(10, 10, 10, 10));
        grid.setPadding(new Insets(10, 10, 10, 10));

        scrollpane = new ScrollPane();
        scrollpane.setFitToWidth(true);
        container.getChildren().add(grid);
        scrollpane.setContent(container);
        scene = new Scene(scrollpane);

        update();
        return scene;
    }

    @Override
    protected void setFxFocus() {
    }

    /**
     * Update the view with the currently open shells.
     */
    public void update() {
        grid.getChildren().removeAll(grid.getChildren());
        Set<OPIShell> updatedShells = OPIShell.getAllShells();
        if (updatedShells.isEmpty()) {
            summaryLabel.setText("No standalone OPIs open.");
            showAllButton.setDisable(true);
            closeAllButton.setDisable(true);
        } else {
            int row = 0;
            String plural = updatedShells.size() == 1 ? "" : "s";
            String isAre = updatedShells.size() == 1 ? "is" : "are";
            summaryLabel.setText("There " + isAre + " " + updatedShells.size() + " standalone OPI" + plural + " open.");
            // Register right-click with any new shells.
            for (OPIShell shell : updatedShells) {
                if ( ! cachedShells.contains(shell)) {
                    shell.registerWithView(this);
                }
                Button closeButton = new Button("Close");
                closeButton.setMinWidth(MIN_SHOW_HIDE_BUTTON_SIZE);
                Label titleLabel = new Label(shell.getTitle());
                closeButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        shell.close();
                        setFxFocus();
                    }
                });
                Button showButton = new Button("Show");
                showButton.setMinWidth(MIN_SHOW_HIDE_BUTTON_SIZE);
                showButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        shell.raiseToTop();
                    }
                });
                grid.add(titleLabel, 0, row);
                grid.add(showButton, 1, row);
                grid.add(closeButton, 2, row);
                row += 1;
            }
            showAllButton.setDisable(false);
            closeAllButton.setDisable(false);
        }
        cachedShells = updatedShells;
    }

    @Override
    public <T> T getAdapter(Class<T> adapter) {
        if (OPIShell.getActiveShell() == null) {
            return null;
        }
        return OPIShell.getActiveShell().getAdapter(adapter);
    }

    @Override
    public String toString() {
        return "OpiShell summary: " + getViewSite().getId();
    }

}
