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
 package org.csstudio.sds.ui.internal.editor;

import org.csstudio.sds.ui.internal.actions.CopyWidgetsAction;
import org.csstudio.sds.ui.internal.actions.CreateGroupAction;
import org.csstudio.sds.ui.internal.actions.CutWidgetsAction;
import org.csstudio.sds.ui.internal.actions.MoveToBackAction;
import org.csstudio.sds.ui.internal.actions.MoveToFrontAction;
import org.csstudio.sds.ui.internal.actions.PasteWidgetsAction;
import org.csstudio.sds.ui.internal.actions.RemoveGroupAction;
import org.csstudio.sds.ui.internal.actions.StepBackAction;
import org.csstudio.sds.ui.internal.actions.StepFrontAction;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;

/**
 * ContextMenuProvider implementation for the display editor.
 *
 * @author Sven Wende
 * @version $Revision: 1.16 $
 *
 */
public final class DisplayContextMenuProvider extends ContextMenuProvider {
    /**
     * The action registry.
     */
    private ActionRegistry _actionRegistry;

    /**
     * Constructor.
     *
     * @param viewer
     *            the graphical viewer
     * @param actionRegistry
     *            the action registry
     */
    public DisplayContextMenuProvider(final EditPartViewer viewer,
            final ActionRegistry actionRegistry) {
        super(viewer);
        assert actionRegistry != null : "actionRegistry!=null"; //$NON-NLS-1$
        _actionRegistry = actionRegistry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildContextMenu(final IMenuManager menu) {
        GEFActionConstants.addStandardActionGroups(menu);

        // add Grouping Actions
        menu.appendToGroup(GEFActionConstants.GROUP_ADD, _actionRegistry.getAction(RemoveGroupAction.ID));
        menu.appendToGroup(GEFActionConstants.GROUP_ADD, _actionRegistry.getAction(CreateGroupAction.ID));

        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, _actionRegistry
                .getAction(CutWidgetsAction.ID));
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, _actionRegistry
                .getAction(CopyWidgetsAction.ID));

        PasteWidgetsAction action = (PasteWidgetsAction) _actionRegistry
                .getAction(PasteWidgetsAction.ID);
        // remember the current mouse pointer location, so that widgets will
        // be pasted where the user right-clicked
        action.fetchCurrentCursorLocation();
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, _actionRegistry
                .getAction(ActionFactory.UNDO.getId()));

        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, _actionRegistry
                .getAction(ActionFactory.REDO.getId()));

        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, _actionRegistry
                .getAction(ActionFactory.DELETE.getId()));
        //menu.add(new Separator("ChangeOrder"));
        MenuManager orderMenu = new MenuManager("Order");
        orderMenu.add(new Separator("order"));
        orderMenu.appendToGroup("order", _actionRegistry
                .getAction(MoveToFrontAction.ID));
        orderMenu.appendToGroup("order", _actionRegistry
                .getAction(StepFrontAction.ID));
        orderMenu.appendToGroup("order", _actionRegistry
                .getAction(StepBackAction.ID));
        orderMenu.appendToGroup("order", _actionRegistry
                .getAction(MoveToBackAction.ID));
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT, orderMenu);

        MenuManager cssMenu = new MenuManager("CSS", "css");
        cssMenu.add(new Separator("additions"));
        menu.add(cssMenu);

    }
}
