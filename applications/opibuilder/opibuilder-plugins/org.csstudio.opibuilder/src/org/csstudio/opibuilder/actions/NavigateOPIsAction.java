/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.runmode.DisplayOpenManager;
import org.csstudio.opibuilder.runmode.IDisplayOpenManagerListener;
import org.csstudio.opibuilder.runmode.IRunnerInput;
import org.csstudio.opibuilder.runmode.RunnerInput;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

/**Go back/forward to one of the OPIs opened before in the OPI Runner.
 * @author Xihui Chen
 *
 */
public class NavigateOPIsAction extends Action implements IDisplayOpenManagerListener {

    private static final String BACK = "Back";
    private static final String FORWARD = "Forward";
    private DisplayOpenManager manager;
    private boolean recreateMenu;
    private boolean forward;


    private class MenuCreator implements IMenuCreator {
        private Menu historyMenu;

        @Override
        public Menu getMenu(Menu parent) {
            setMenu(new Menu(parent));
            fillMenu(historyMenu);
            initMenu();
            return historyMenu;
        }

        @Override
        public Menu getMenu(Control parent) {
            setMenu(new Menu(parent));
            fillMenu(historyMenu);
            initMenu();
            return historyMenu;
        }

        private void setMenu(Menu menu) {
            dispose();
            historyMenu = menu;
        }

        private void initMenu() {
            historyMenu.addMenuListener(new MenuAdapter() {
                @Override
                public void menuShown(MenuEvent e) {
                    if (recreateMenu) {
                        Menu m = (Menu) e.widget;
                        MenuItem[] items = m.getItems();
                        for (int i = 0; i < items.length; i++) {
                            items[i].dispose();
                        }
                        fillMenu(m);
                    }
                }
            });
        }

        private void fillMenu(final Menu menu) {
            if (manager == null)
                return;
            Object[] entries = forward ?
                    manager.getForwardStackEntries() : manager.getBackStackEntries();
            RunnerInput[] runnerInputArray = new RunnerInput[entries.length];
            int i=entries.length-1;
            for(final Object o : entries){
                runnerInputArray[i--] = (RunnerInput)o;
            }

            for(final RunnerInput input : runnerInputArray){
                final MenuItem menuItem = new MenuItem(menu, SWT.None);
                menuItem.setText(input.getName());
                menuItem.addSelectionListener(new SelectionAdapter(){
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        if(forward){
                            manager.goForward(menu.indexOf(menuItem));
                        }
                        else
                            manager.goBack(menu.indexOf(menuItem));
                    }
                });
            }

            recreateMenu = false;
        }

        @Override
        public void dispose() {
            if (historyMenu != null) {
                for (int i = 0; i < historyMenu.getItemCount(); i++) {
                    MenuItem menuItem = historyMenu.getItem(i);
                    menuItem.dispose();
                }
                historyMenu.dispose();
                historyMenu = null;
            }
        }
    }



    public NavigateOPIsAction(boolean forward) {
        this.forward = forward;
        ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
        if (forward) {
            setText("&Forward");
            setToolTipText(FORWARD);
            setId(ActionFactory.FORWARD_HISTORY.getId());
            setImageDescriptor(sharedImages
                    .getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
            setDisabledImageDescriptor(sharedImages
                    .getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD_DISABLED));
            setActionDefinitionId("org.eclipse.ui.navigate.forwardHistory"); //$NON-NLS-1$
        } else {
            setText("&Back");
            setToolTipText(BACK);
            setId(ActionFactory.BACKWARD_HISTORY.getId());
            setImageDescriptor(sharedImages
                    .getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
            setDisabledImageDescriptor(sharedImages
                    .getImageDescriptor(ISharedImages.IMG_TOOL_BACK_DISABLED));
            setActionDefinitionId("org.eclipse.ui.navigate.backwardHistory"); //$NON-NLS-1$
        }
        setMenuCreator(new MenuCreator());
    }

    @Override
    public void run() {
        if(manager == null)
            return;
        if(forward)
            manager.goForward();
        else
            manager.goBack();
    }

    @Override
    public boolean isEnabled() {
        if (manager == null)
            return false;
        return forward?manager.canForward() : manager.canBackward();
    }

    public void setDisplayOpenManager(DisplayOpenManager manager) {
        if (this.manager != null) {
            this.manager.removeListener(this);
        }
        this.manager = manager;
        if (this.manager != null) {
            this.manager.addListener(this);
        }
        update();
    }

    @Override
    public void displayOpenHistoryChanged(DisplayOpenManager manager) {
        update();
    }

    public void update(){
        if (manager == null) return;
        setEnabled(isEnabled());
        recreateMenu = true;
        if(forward){
            if(manager.canForward())
                setToolTipText(FORWARD + " to " + ((IRunnerInput)(manager.getForwardStackEntries()[0])).getName());
            else
                setToolTipText(FORWARD);
        }else{
            if(manager.canBackward())
                setToolTipText(BACK + " to " + ((IRunnerInput)(manager.getBackStackEntries()[0])).getName());
            else
                setToolTipText(BACK);
        }
    }

    /**
     * Disposes of all resources allocated by this action.
     */
    public void dispose() {
        setDisplayOpenManager(null);
    }

}
