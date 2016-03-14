/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.utility.screenshot.view;

import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;

import org.csstudio.utility.screenshot.IImageWorker;
import org.csstudio.utility.screenshot.ScreenshotPlugin;
import org.csstudio.utility.screenshot.ScreenshotWorker;
import org.csstudio.utility.screenshot.destination.MailImageWorker;
import org.csstudio.utility.screenshot.internal.localization.ScreenshotMessages;
import org.csstudio.utility.screenshot.menu.action.EditClearAction;
import org.csstudio.utility.screenshot.menu.action.EditCopyAction;
import org.csstudio.utility.screenshot.menu.action.EditPasteAction;
import org.csstudio.utility.screenshot.menu.action.FilePrintImageAction;
import org.csstudio.utility.screenshot.menu.action.FileSaveImageAsAction;
import org.csstudio.utility.screenshot.menu.action.FileSendImageAction;
import org.csstudio.utility.screenshot.menu.action.FitImageAction;
import org.csstudio.utility.screenshot.menu.action.HelpDocumentationAction;
import org.csstudio.utility.screenshot.menu.action.RestartBeepAction;
import org.csstudio.utility.screenshot.menu.action.RestartFiveAction;
import org.csstudio.utility.screenshot.menu.action.RestartSevenAction;
import org.csstudio.utility.screenshot.menu.action.RestartThreeAction;
import org.csstudio.utility.screenshot.menu.action.SelectionScreenAction;
import org.csstudio.utility.screenshot.menu.action.SelectionSectionAction;
import org.csstudio.utility.screenshot.menu.action.SelectionWindowAction;
import org.csstudio.utility.screenshot.menu.action.ShowOriginalAction;
import org.csstudio.utility.screenshot.menu.action.ZoomInAction;
import org.csstudio.utility.screenshot.menu.action.ZoomOutAction;
import org.csstudio.utility.screenshot.util.ClipboardHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus Moeller
 *
 */
public class ScreenshotView extends ViewPart implements FlavorListener {
    private static final Logger LOG = LoggerFactory.getLogger(ScreenshotView.class);

    private ScreenshotWorker worker;
    private IImageWorker[] imageWorker;
    private Action toolBarPasteAction;

    /**  */
    public ScreenshotView() {
        // Can be empty
    }

    /**  */
    @Override
    public void createPartControl(Composite parent) {
        Action tempAction = null;
        IExtensionRegistry extReg = Platform.getExtensionRegistry();
        IConfigurationElement[] confElements = extReg
                .getConfigurationElementsFor("org.csstudio.utility.screenshot.ImageWorker");

        LOG.debug("Implementationen: {}", confElements.length);

        if(confElements.length > 0) {
            imageWorker = new IImageWorker[confElements.length];

            for (int x = 0; x < confElements.length; x++) {
                try {
                    imageWorker[x] = (IImageWorker) confElements[x]
                            .createExecutableExtension("class");
                } catch (CoreException ce) {
                    LOG.error("*** CoreException *** : ", ce);
                }
            }
        }

        worker = new ScreenshotWorker(parent);

        // Add toolbar contributions
        IActionBars actionBars = getViewSite().getActionBars();
        IToolBarManager toolbarManager = actionBars.getToolBarManager();
        IMenuManager menuBar = actionBars.getMenuManager();

        // Zoom icons
        tempAction = new ZoomInAction(worker);
        tempAction.setImageDescriptor(ScreenshotPlugin.getImageDescriptor("icons/zoomin.gif"));
        toolbarManager.add(tempAction);

        tempAction = new ZoomOutAction(worker);
        tempAction.setImageDescriptor(ScreenshotPlugin.getImageDescriptor("icons/zoomout.gif"));
        toolbarManager.add(tempAction);

        tempAction = new FitImageAction(worker);
        tempAction.setImageDescriptor(ScreenshotPlugin.getImageDescriptor("icons/fitimage.gif"));
        toolbarManager.add(tempAction);

        tempAction = new ShowOriginalAction(worker);
        tempAction.setImageDescriptor(ScreenshotPlugin.getImageDescriptor("icons/original.gif"));
        toolbarManager.add(tempAction);

        toolbarManager.add(new Separator());

        // Copy / Paste icons
        tempAction = new EditCopyAction(worker);
        tempAction.setImageDescriptor(ScreenshotPlugin.getImageDescriptor("icons/copy.gif"));
        toolbarManager.add(tempAction);

        toolBarPasteAction = new EditPasteAction(worker);
        toolBarPasteAction.setImageDescriptor(ScreenshotPlugin
                .getImageDescriptor("icons/paste.gif"));
        toolbarManager.add(toolBarPasteAction);
        ClipboardHandler.getInstance().addListener(this);

        toolbarManager.add(new Separator());

        // Create the menu
        MenuManager fileMenu = new MenuManager(ScreenshotMessages.getString("ScreenshotView.MENU_FILE"));

        if(imageWorker != null) {
            MenuManager fileSendMenu = new MenuManager(ScreenshotMessages.getString("ScreenshotView.MENU_FILE_SEND"));

            for (int i = 0; i < imageWorker.length; i++) {
                tempAction = new FileSendImageAction(worker, imageWorker[i]);
                fileSendMenu.add(tempAction);

                if(imageWorker[i] instanceof MailImageWorker) {
                    tempAction = new FileSendImageAction(worker, imageWorker[i]);
                    tempAction.setImageDescriptor(ScreenshotPlugin
                            .getImageDescriptor("icons/email.gif"));
                    toolbarManager.add(tempAction);
                } else {
                    tempAction = new FileSendImageAction(worker, imageWorker[i]);
                    tempAction.setImageDescriptor(ScreenshotPlugin
                            .getImageDescriptor("icons/send.gif"));
                    toolbarManager.add(tempAction);
                }
            }

            fileMenu.add(fileSendMenu);
            fileMenu.add(new Separator());
        } else {
            LOG.error("Sorry, no image worker available.");
        }

        toolbarManager.add(new Separator());

        fileMenu.add(new FileSaveImageAsAction(worker));
        fileMenu.add(new Separator());
        fileMenu.add(new FilePrintImageAction(worker, false));
        fileMenu.add(new FilePrintImageAction(worker, true));

        tempAction = new FileSaveImageAsAction(worker);
        tempAction.setImageDescriptor(ScreenshotPlugin.getImageDescriptor("icons/save.gif"));
        toolbarManager.add(tempAction);

        tempAction = new FilePrintImageAction(worker, false);
        tempAction.setImageDescriptor(ScreenshotPlugin.getImageDescriptor("icons/print.gif"));
        toolbarManager.add(tempAction);
        toolbarManager.add(new Separator());

        MenuManager editMenu = new MenuManager(ScreenshotMessages.getString("ScreenshotView.MENU_EDIT"));
        EditCopyAction copyAction = new EditCopyAction(worker);
        editMenu.add(copyAction);
        editMenu.addMenuListener(copyAction);

        EditPasteAction pasteAction = new EditPasteAction(worker);
        editMenu.add(pasteAction);
        editMenu.addMenuListener(pasteAction);
        editMenu.add(new Separator());
        editMenu.add(new EditClearAction(worker));

        MenuManager captureMenu = new MenuManager(ScreenshotMessages.getString("ScreenshotView.MENU_SELECTION"));
        captureMenu.add(new SelectionSectionAction(worker));
        captureMenu.add(new Separator());
        captureMenu.add(new SelectionWindowAction(worker));
        captureMenu.add(new Separator());
        captureMenu.add(new SelectionScreenAction(worker));

        tempAction = new SelectionSectionAction(worker);
        tempAction.setImageDescriptor(ScreenshotPlugin.getImageDescriptor("icons/section.gif"));
        toolbarManager.add(tempAction);
        tempAction = new SelectionWindowAction(worker);
        tempAction.setImageDescriptor(ScreenshotPlugin.getImageDescriptor("icons/window.gif"));
        toolbarManager.add(tempAction);
        tempAction = new SelectionScreenAction(worker);
        tempAction.setImageDescriptor(ScreenshotPlugin.getImageDescriptor("icons/screen.gif"));
        toolbarManager.add(tempAction);
        toolbarManager.add(new Separator());

        MenuManager restartMenu = new MenuManager(ScreenshotMessages.getString("ScreenshotView.MENU_RESTART"));
        restartMenu.add(new RestartBeepAction(worker));
        restartMenu.add(new Separator());
        restartMenu.add(new RestartThreeAction(worker));
        restartMenu.add(new RestartFiveAction(worker));
        restartMenu.add(new RestartSevenAction(worker));

        MenuManager helpMenu = new MenuManager(ScreenshotMessages.getString("ScreenshotView.MENU_HELP"));
        helpMenu.add(new HelpDocumentationAction(worker));

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(captureMenu);
        menuBar.add(restartMenu);
        menuBar.add(helpMenu);

        actionBars.updateActionBars();

        worker.setDefaults();
    }

    /**
     * Called when the View is to be disposed
     */
    @Override
    public void dispose() {

        worker.dispose();
        worker = null;

        for (int i = 0; i < imageWorker.length; i++) {
            imageWorker[i] = null;
        }

        imageWorker = null;

        super.dispose();
    }

    /**
     * Returns the Display.
     *
     * @return the display we're using
     */
    public Display getDisplay() {
        return worker.getDisplay();
    }

    /**
     * Called when we must grab focus.
     *
     * @see org.eclipse.ui.part.ViewPart#setFocus
     */
    @Override
    public void setFocus() {
        worker.setFocus();
    }

    @Override
    public void flavorsChanged(FlavorEvent flavorEvent) {

        if(ClipboardHandler.getInstance().isImageAvailable()) {
            toolBarPasteAction.setEnabled(true);
        } else {
            toolBarPasteAction.setEnabled(false);
        }
    }
}
