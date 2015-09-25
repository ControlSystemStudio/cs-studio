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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * RCP View summarising OPI shells.  Only one should be open
 * at any one time.
 */
@SuppressWarnings("nls")
public class OPIShellSummary extends FXViewPart {

    public static final String ID = "org.csstudio.opibuilder.opiShellSummary";

    private ScrollPane scrollpane;
    private GridPane grid;
    private Scene scene;

    private Set<OPIShell> cachedShells;

    public OPIShellSummary() {
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void init(final IViewSite site, final IMemento memento) throws PartInitException
    {
        super.init(site, memento);
        cachedShells = OPIShell.getAllShells();
        for (OPIShell shell : cachedShells) {
            shell.registerWithView(this);
        }
    }

    @Override
    protected javafx.scene.Scene createFxScene() {
        VBox vbox = new VBox();
        Button b = new Button("Close all OPI shells");
        b.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                OPIShell.closeAll();
                update();
            }
        });
        vbox.getChildren().add(b);
        grid = new GridPane();
        ColumnConstraints column = new ColumnConstraints();
        column.setPercentWidth(60);
        column.setHalignment(HPos.LEFT);
        grid.getColumnConstraints().add(column);
        column = new ColumnConstraints();
        column.setPercentWidth(20);
        column.setHalignment(HPos.CENTER);
        grid.getColumnConstraints().add(column);
        column = new ColumnConstraints();
        column.setPercentWidth(20);
        column.setHalignment(HPos.CENTER);
        grid.getColumnConstraints().add(column);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        grid.setPadding(new Insets(10, 10, 10, 10));
        scrollpane = new ScrollPane();
        vbox.getChildren().add(grid);
        scrollpane.setContent(vbox);
        scene = new Scene(scrollpane);
        update();
        return scene;
    }

    @Override
    protected void setFxFocus() {
    }

    public void update() {
        grid.getChildren().removeAll(grid.getChildren());
        Set<OPIShell> updatedShells = OPIShell.getAllShells();
        if (updatedShells.isEmpty()) {
            Label emptyLabel = new Label("No OPI shells open.");
            grid.add(emptyLabel, 0, 0);
        } else {
            int row = 0;
            for (OPIShell shell : updatedShells) {
                if ( ! cachedShells.contains(shell)) {
                    shell.registerWithView(this);
                }
                Button b = new Button("Close");
                Label l = new Label(shell.getTitle());
                b.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        shell.close();
                        setFxFocus();
                    }
                });
                Button bf = new Button("Show");
                bf.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        shell.raiseToTop();
                    }
                });
                grid.add(l, 0, row);
                grid.add(bf, 1, row);
                grid.add(b, 2, row);
                row += 1;
            }
        }
        cachedShells = updatedShells;
    }

    @Override
    public String toString()
    {
        return "OpiShell summary: " + getViewSite().getId();
    }

}
