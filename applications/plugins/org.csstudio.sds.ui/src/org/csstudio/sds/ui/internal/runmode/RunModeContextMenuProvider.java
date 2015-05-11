/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.sds.ui.internal.runmode;

import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.properties.actions.AbstractWidgetActionModel;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.editparts.AbstractBaseEditPart;
import org.csstudio.sds.ui.widgetactionhandler.WidgetActionHandlerService;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * ContextMenuProvider implementation for the display editor.
 *
 * @author Sven Wende, Kai Meyer
 * @version $Revision: 1.15 $
 *
 */
public final class RunModeContextMenuProvider extends ContextMenuProvider {

    /**
     * The ID for the close action.
     */
    public static final String CLOSE_ACTION_ID = "closeAction";
    /**
     * The action registry.
     */
    private ActionRegistry _actionRegistry;

    @Override
    public void addMenuListener(IMenuListener listener) {
        super.addMenuListener(listener);
    }

    /**
     * Constructor.
     *
     * @param viewer
     *            the graphical viewer
     * @param actionRegistry
     *            the action registry
     */
    public RunModeContextMenuProvider(final EditPartViewer viewer,
                                      final ActionRegistry actionRegistry) {
        super(viewer);
        assert actionRegistry != null : "actionRegistry!=null"; //$NON-NLS-1$
        _actionRegistry = actionRegistry;
    }

    public void dispose() {
        super.dispose();
        super.removeAll();
        super.setViewer(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildContextMenu(final IMenuManager menu) {
        menu.add(new Separator("actions"));
        this.addWidgetActionToMenu(menu);

        IAction closeAction = _actionRegistry.getAction(CLOSE_ACTION_ID);
        GEFActionConstants.addStandardActionGroups(menu);
        if (closeAction != null) {
            ImageDescriptor close = CustomMediaFactory.getInstance()
                    .getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID, "icons/delete.gif");
            closeAction.setImageDescriptor(close);
            menu.add(closeAction);
        }
    }

    /**
     * Adds the defined {@link AbstractWidgetActionModel}s to the given
     * {@link IMenuManager}.
     *
     * @param menu
     *            The {@link IMenuManager}
     */
    @SuppressWarnings("rawtypes")
    private void addWidgetActionToMenu(final IMenuManager menu) {
        List selectedEditParts = getViewer().getSelectedEditParts();
        if (selectedEditParts.size() == 1) {
            if (selectedEditParts.get(0) instanceof AbstractBaseEditPart) {
                AbstractBaseEditPart editPart = (AbstractBaseEditPart) selectedEditParts.get(0);
                AbstractWidgetModel widget = editPart.getWidgetModel();

                List<AbstractWidgetActionModel> widgetActions = widget.getActionData()
                        .getWidgetActions();
                if (!widgetActions.isEmpty()) {
                    if (widgetActions.size() > 3) {
                        MenuManager actionMenu = new MenuManager("Actions", "actions");
                        fillMenu(actionMenu, widget, widgetActions);
                        menu.add(actionMenu);
                    } else {
                        fillMenu(menu, widget, widgetActions);
                    }
                }
            }
        }
    }

    /**
     * @param menu
     * @param widget
     * @param widgetActions
     */
    private void fillMenu(final IMenuManager menu,
                          AbstractWidgetModel widget,
                          List<AbstractWidgetActionModel> widgetActions) {
        for (AbstractWidgetActionModel action : widgetActions) {
            menu.add(new MenuAction(widget, action));
        }
    }

    /**
     * An Action, which encapsulates a {@link AbstractWidgetActionModel}.
     *
     * @author Kai Meyer
     *
     */
    private final class MenuAction extends Action {
        /**
         * The selected widget model.
         */
        private AbstractWidgetModel _widget;

        /**
         * The {@link AbstractWidgetActionModel}.
         */
        private AbstractWidgetActionModel _widgetAction;

        /**
         * Constructor.
         *
         * @param widgetAction
         *            The encapsulated {@link AbstractWidgetActionModel}
         */
        public MenuAction(AbstractWidgetModel widget, final AbstractWidgetActionModel widgetAction) {
            assert widget != null;
            assert widgetAction != null;
            _widget = widget;
            _widgetAction = widgetAction;

            // decorate the action
            this.setText(_widgetAction.getActionLabel());
            IWorkbenchAdapter adapter = (IWorkbenchAdapter) Platform.getAdapterManager()
                    .getAdapter(widgetAction, IWorkbenchAdapter.class);
            if (adapter != null) {
                this.setImageDescriptor(adapter.getImageDescriptor(widgetAction));
            }

            // enablement of the action is dependent on the enablement of the
            // corresponding widget
            this.setEnabled(_widget.isAccesible() && widgetAction.isEnabled());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            WidgetActionHandlerService.getInstance().performAction(_widget, _widgetAction);
        }
    }

}
