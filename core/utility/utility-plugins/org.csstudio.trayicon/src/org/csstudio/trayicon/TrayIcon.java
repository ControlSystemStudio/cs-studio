package org.csstudio.trayicon;

import org.csstudio.utility.product.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class TrayIcon {

    private static final String IMAGE_FILE = "icons/css.ico";
    private static final Image IMAGE = Activator.getImageDescriptor(IMAGE_FILE).createImage();
    private TrayItem trayItem;
    private IWorkbenchWindow[] windows;

    private boolean minimized;
    public boolean isMinimized() {
        return minimized;
    }

    /**
     * Minimize the application to a tray icon.
     *  - left-click will reopen the window(s)
     *  - right-click popup menu to open or exit.
     *
     */
    public void minimize() {
        windows = PlatformUI.getWorkbench().getWorkbenchWindows();
        for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
            window.getShell().setVisible(false);
        }

        trayItem = createTrayItem();
        minimized = true;
    }

    /**
     * Restore application window(s) from the toolbar.
     */
    public void unminimize() {
        for (IWorkbenchWindow window : windows) {
            raiseWindow(window.getShell());
        }
        removeFromTray();
        minimized = false;
    }

    /**
     * Cleanup the trayItem
     */
    private void removeFromTray() {
        trayItem.dispose();
    }

    private void raiseWindow(Shell shell) {
        shell.setVisible(true);
        shell.setActive();
        shell.setFocus();
        shell.setMinimized(false);
        shell.layout();
    }

    /**
     * Create a Tray widget for CS-Studio with wrapped popup menu
     */
    private TrayItem createTrayItem() {

        Menu menu = createPopupMenu();

        TrayItem item = new TrayItem(Display.getCurrent().getSystemTray(), SWT.NONE);
        item.setImage(IMAGE);
        item.setToolTipText(Messages.TrayIcon_tooltip);
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                unminimize();
            }
        });
        item.addListener(SWT.MenuDetect, new Listener() {
            @Override
            public void handleEvent(Event event) {
                menu.setVisible(true);
            }
        });
        // Clean-up the popup menu when the trayItem is disposed. The child
        // menuItems are disposed when their menu is disposed.
        item.addListener(SWT.Dispose, new Listener() {
            @Override
            public void handleEvent(Event event) {
                menu.dispose();
            }
        });

        return item;
    }

    private Menu createPopupMenu() {
        // Create a Menu
        Menu popupMenu = new Menu(windows[0].getShell(), SWT.POP_UP);

        // Create the open menu item.
        MenuItem openMenuItem = new MenuItem(popupMenu, SWT.PUSH);
        openMenuItem.setText(Messages.TrayIcon_open);
        openMenuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                unminimize();
            }
        });

        // Create the exit menu item.
        MenuItem exitMenuItem = new MenuItem(popupMenu, SWT.PUSH);
        exitMenuItem.setText(Messages.TrayIcon_exit);
        exitMenuItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                removeFromTray();
                PlatformUI.getWorkbench().close();
            }
        });

        return popupMenu;
    }

}
