/*******************************************************************************
 * Copyright (c) 2017 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree.jfx;

import static org.csstudio.diag.epics.pvtree.Plugin.logger;
import static org.csstudio.diag.epics.pvtree.Plugin.openIconStream;

import java.util.logging.Level;

import org.csstudio.diag.epics.pvtree.Messages;
import org.csstudio.diag.epics.pvtree.model.TreeModel;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/** JFX UI for {@link TreeModel}
 *
 *  <p>Combines {@link FXTree} with PV name field and buttons.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class FXTreeUI extends BorderPane
{
    private final FXTree tree;
    private final TextField pv_name;

    /** @param model Model to represent */
    public FXTreeUI(final TreeModel model)
    {
        tree = new FXTree(model);

        final Label label = new Label(Messages.PV_Label);
        pv_name = new TextField();
        pv_name.setTooltip(new Tooltip(Messages.PV_TT));
        pv_name.setOnAction(event -> setPVName(pv_name.getText()));

        final ToggleButton latch = new ToggleButton(null, getImageView("run.png"));
        latch.setTooltip(new Tooltip(Messages.TreeMode_TT));
        latch.setOnAction(event ->
        {
            model.latchOnAlarm(latch.isSelected());
            if (latch.isSelected())
                latch.setGraphic(getImageView("pause_on_alarm.png"));
            else
                latch.setGraphic(getImageView("run.png"));
        });

        final Button expand = new Button(null, getImageView("pvtree.png"));
        expand.setTooltip(new Tooltip(Messages.ExpandAllTT));
        expand.setOnAction(event -> tree.expandAll(true));

        final Button alarms = new Button(null, getImageView("alarmtree.png"));
        alarms.setTooltip(new Tooltip(Messages.ExpandAlarmsTT));
        alarms.setOnAction(event -> tree.expandAlarms());

        final Button collapse = new Button(null, getImageView("collapse.gif"));
        collapse.setTooltip(new Tooltip(Messages.CollapseTT));
        collapse.setOnAction(event -> tree.expandAll(false));

        label.setMaxHeight(Double.MAX_VALUE); // center vertically
        HBox.setHgrow(pv_name, Priority.ALWAYS);
        final HBox buttons = new HBox(5, label, pv_name, latch, collapse, alarms, expand);

        setTop(buttons);
        setCenter(tree.getNode());
    }

    /** @param pv_name PV name to show in tree */
    public void setPVName(final String pv_name)
    {
        this.pv_name.setText(pv_name);
        tree.setPVName(pv_name);
    }

    private static ImageView getImageView(final String icon)
    {
        try
        {
            return new ImageView(new Image(openIconStream(icon)));
        }
        catch (Exception ex)
        {
            logger.log(Level.WARNING, "Cannot open icon", ex);
            return null;
        }
    }
}