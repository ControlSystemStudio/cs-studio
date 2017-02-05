package org.csstudio.diag.epics.pvtree;

import org.csstudio.diag.epics.pvtree.jfx.TreeUI;
import org.csstudio.diag.epics.pvtree.model.TreeModel;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 *
 *  <p>When started from Eclipse on Mac OS X,
 *  remember to de-select "start X on first thread" option
 *  in launch configuration.
 *
 *  @author Kay Kasemir
 */
public class JFX_PVTreeApplication extends Application
{
    @Override
    public void start(final Stage stage) throws Exception
    {
        final TreeModel model = new TreeModel();
        final TreeUI tree = new TreeUI(model);

        final ToggleButton latch = new ToggleButton(null, new ImageView(new Image("file:../org.csstudio.diag.epics.pvtree/icons/run.png")));
        latch.setOnAction(event ->
        {
            model.latchOnAlarm(latch.isSelected());
            if (latch.isSelected())
                latch.setGraphic(new ImageView(new Image("file:../org.csstudio.diag.epics.pvtree/icons/pause_on_alarm.png")));
            else
                latch.setGraphic(new ImageView(new Image("file:../org.csstudio.diag.epics.pvtree/icons/run.png")));
        });

        final Button expand = new Button(null, new ImageView(new Image("file:../org.csstudio.diag.epics.pvtree/icons/pvtree.png")));
        expand.setOnAction(event -> tree.expandAll(true));

        final Button alarms = new Button(null, new ImageView(new Image("file:../org.csstudio.diag.epics.pvtree/icons/alarmtree.png")));
        alarms.setOnAction(event -> tree.expandAlarms());

        final Button collapse = new Button(null, new ImageView(new Image("file:../org.csstudio.diag.epics.pvtree/icons/collapse.gif")));
        collapse.setOnAction(event -> tree.expandAll(false));
        final HBox buttons = new HBox(10, latch, expand, alarms, collapse);

        final BorderPane layout = new BorderPane();
        layout.setTop(buttons);
        layout.setCenter(tree);

        final Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(event ->
        {
            model.dispose();
            TestHelper.checkShutdown();
        });
    }

    public static void main(String[] args)
    {
        // TestHelper.setupLogging();
        TestHelper.setupPVFactory();
        launch(args);
    }
}
