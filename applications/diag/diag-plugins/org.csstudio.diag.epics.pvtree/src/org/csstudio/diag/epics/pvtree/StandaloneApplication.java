/*******************************************************************************
 * Copyright (c) 2017 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import static org.csstudio.diag.epics.pvtree.Plugin.logger;

import java.util.Arrays;
import java.util.logging.Level;

import org.csstudio.diag.epics.pvtree.jfx.FXTreeUI;
import org.csstudio.diag.epics.pvtree.model.TreeModel;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.fx.ui.workbench3.FXViewPart;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import javafx.scene.Scene;
import javafx.stage.Stage;

/** OSGi application for standalone PV Tree
 *
 *  May need command-line options
 *  -vmargs -Dosgi.requiredJavaVersion=1.8 -Xms256m -Xmx1024m
 *  -Dorg.osgi.framework.bundle.parent=ext -Dosgi.framework.extensions=org.eclipse.fx.osgi
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class StandaloneApplication implements IApplication
{
    private Display display;

    /** Empty FXViewPart */
    private static class FXInitializeHack extends FXViewPart
    {
        @Override
        protected Scene createFxScene()
        {
            return null;
        }

        @Override
        protected void setFxFocus()
        {
            // NOP
        }
    }

    public void usage()
    {
        System.out.println("USAGE: pv_tree [options] [PV name]");
        System.out.println("Options:");
        System.out.println(" -help                                        Display command line options");
        System.out.println(" -pluginCustomization /path/to/settings.ini   Channel Access, .. configuration");
    }

    @Override
    public Object start(IApplicationContext context) throws Exception
    {
        final String[] argv = (String[]) context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
        logger.log(Level.CONFIG, "Args: " + Arrays.toString(argv));

        String pv_name = null;
        if (argv.length > 1  ||
            (argv.length == 1   &&   argv[0].startsWith("-h")))
        {
            usage();
            return Integer.valueOf(0);
        }

        if (argv.length == 1)
            pv_name = argv[0];

        final Stage stage = initializeUI();
        final TreeModel model = new TreeModel();
        final FXTreeUI tree = new FXTreeUI(model);

        final Scene scene = new Scene(tree, 400, 600);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> stop());

        if (pv_name != null)
            tree.setPVName(pv_name);

        while (!display.isDisposed())
        {
            if (!display.readAndDispatch())
                display.sleep();
        }

        return Integer.valueOf(0);
    }

    private Stage initializeUI()
    {
        // Creating an FXCanvas results in a combined
        // SWT and JavaFX setup with common UI thread.
        // Shell that's created as a parent for the FXCanvas is never shown.
        display = Display.getDefault();
        final Shell temp_shell = new Shell(display);

        // Would like to create an FXCanvas,
        // but that requires jfxswt.jar on the classpath.
        //   new FXCanvas(temp_shell, SWT.NONE);
        // The FXViewPart creates an FXCanvas and is
        // packaged in org.eclipse.fx.ui.workbench3
        // so we can access it:
        final FXInitializeHack hack = new FXInitializeHack();
        hack.createPartControl(temp_shell);
        temp_shell.close();

        final Stage stage = new Stage();
        stage.setTitle("EPICS PV Tree");
        stage.setWidth(400);
        stage.setHeight(600);

        return stage;
    }

    @Override
    public void stop()
    {
        System.exit(0);
    }
}
