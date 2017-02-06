/*******************************************************************************
 * Copyright (c) 2017 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import org.csstudio.diag.epics.pvtree.jfx.FXTreeUI;
import org.csstudio.diag.epics.pvtree.model.TreeModel;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** JavaFX Application demo
 *
 *  <p>When started from Eclipse on Mac OS X,
 *  remember to de-select "start X on first thread" option
 *  in launch configuration.
 *
 *  @author Kay Kasemir
 */
public class JFX_PVTreeDemo extends Application
{
    @Override
    public void start(final Stage stage) throws Exception
    {
        final TreeModel model = new TreeModel();
        final FXTreeUI tree = new FXTreeUI(model);

        final Scene scene = new Scene(tree, 400, 600);
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
