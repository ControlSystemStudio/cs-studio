/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.runmode.RunModeService.DisplayMode;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.SingleSourceHelper;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * Open top OPIs in run mode.
 *
 * @author Xihui Chen
 */
public class OpenTopOPIsAction implements IWorkbenchWindowPulldownDelegate {

    private static final String TOP_OPI_POSITION_KEY = "Position"; //$NON-NLS-1$
    private static final String ALIAS_KEY = "Alias"; //$NON-NLS-1$
    private Menu opiListMenu;
    private static Image OPI_RUNTIME_IMAGE = CustomMediaFactory.getInstance()
            .getImageFromPlugin(OPIBuilderPlugin.PLUGIN_ID, "icons/OPIRunner.png"); //$NON-NLS-1$

    public Menu getMenu(Control parent) {
        dispose();
        opiListMenu = new Menu(parent);
        final Map<IPath, MacrosInput> topOPIs = loadTopOPIs();
        if (topOPIs == null)
            return null;

        fillMenu(topOPIs, opiListMenu);
        return opiListMenu;
    }

    public static void fillMenu(final Map<IPath, MacrosInput> topOPIs, Menu menu) {
        for (final IPath path : topOPIs.keySet()) {
            if (path != null) {
                MenuItem item = new MenuItem(menu, SWT.PUSH);
                String alias = topOPIs.get(path).get(ALIAS_KEY);
                if (alias != null)
                    item.setText(alias);
                else
                    item.setText(path.lastSegment());
                if(path.getFileExtension().toLowerCase().equals("opi")) //$NON-NLS-1$
                    item.setImage(OPI_RUNTIME_IMAGE);
                else{
                    final Image image = PlatformUI.getWorkbench().getEditorRegistry().
                            getImageDescriptor(path.toOSString()).createImage();
                    item.setImage(image);
                    item.addDisposeListener(new DisposeListener() {
                        @Override
                        public void widgetDisposed(DisposeEvent e) {
                            image.dispose();
                        }
                    });
                }
                item.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        if (path.getFileExtension().toLowerCase().equals("opi")) { //$NON-NLS-1$
                            runOPI(topOPIs.get(path), path);
                        } else {
                            runOther(path);
                        }
                    }
                });
            }
        }
    }

    public static void runOPI(final MacrosInput macrosInput, final IPath path)
    {
        DisplayMode mode =  DisplayMode.NEW_TAB;
        if (macrosInput != null)
        {
            final String position = macrosInput.get(TOP_OPI_POSITION_KEY);
            if (position != null)
            {
                if (position.toUpperCase().equals("NEW_SHELL"))
                {
                    mode = DisplayMode.NEW_SHELL;
                }
                else
                {
                    try
                    {
                        mode = DisplayMode.valueOf("NEW_TAB_" + position.toUpperCase());
                    }
                    catch (IllegalArgumentException ex)
                    {
                        // Ignore
                    }
                }
            }
        }
        RunModeService.openDisplay(path, Optional.ofNullable(macrosInput), mode, Optional.empty());
    }

    public static void runOther(final IPath path) {
        IWorkbenchPage page = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage();
        try {
            SingleSourceHelper.openEditor(page, path);
        } catch (Exception e) {
            String message = NLS.bind("Failed to open the editor. \n {0}", e);
            MessageDialog.openError(null, "Error in opening OPI", message);
        }
    }

    /**
     * @return the top OPIs from preference settings
     */
    public static Map<IPath, MacrosInput> loadTopOPIs() {
        final Map<IPath, MacrosInput> topOPIs;
        try {
            topOPIs = PreferencesHelper.getTopOPIs();
            if (topOPIs == null || topOPIs.keySet().size() == 0) {
                return null;
            }
        } catch (Exception e) {
            String message = NLS.bind(
                    "Failed to load top OPIs from preference settings. \n {0}", e);
            MessageDialog.openError(null, "Error in loading top OPIs", message);
            OPIBuilderPlugin.getLogger().log(Level.WARNING, message, e);
            return null;
        }
        return topOPIs;
    }

    public void dispose() {
        if (opiListMenu != null && !opiListMenu.isDisposed()) {
            for (MenuItem m : opiListMenu.getItems())
                m.dispose();
            opiListMenu.dispose();
            opiListMenu = null;
        }
    }

    public void init(IWorkbenchWindow window) {
        // NOP
    }

    public void run(IAction action) {
        Map<IPath, MacrosInput> topOPIs = loadTopOPIs();
        if (topOPIs == null || topOPIs.keySet().size() == 0) {
            MessageDialog.openWarning(null, "Warning",
                    "No top OPIs were set in preference settings.");
        } else {
            IPath path = (IPath) topOPIs.keySet().toArray()[0];
            if (path != null) {
                if (path.getFileExtension().toLowerCase().equals("opi")) {
                    runOPI(topOPIs.get(path), path);
                } else {
                    runOther(path);
                }
            }
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
        // NOP
    }
}
