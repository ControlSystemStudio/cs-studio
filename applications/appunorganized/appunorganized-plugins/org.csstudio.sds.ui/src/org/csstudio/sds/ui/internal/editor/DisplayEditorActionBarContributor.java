/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.internal.actions.ArrangeAction;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.AlignmentRetargetAction;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.MatchHeightRetargetAction;
import org.eclipse.gef.ui.actions.MatchWidthRetargetAction;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * ActionBarContributor implementation for the display editor.
 *
 * @author Sven Wende & Alexander Will
 * @version $Revision: 1.12 $
 *
 */
public class DisplayEditorActionBarContributor extends ActionBarContributor {

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void buildActions() {
        addRetargetAction(new UndoRetargetAction());
        addRetargetAction(new RedoRetargetAction());
        addRetargetAction(new DeleteRetargetAction());

        addRetargetAction(new ZoomInRetargetAction());
        addRetargetAction(new ZoomOutRetargetAction());

        addRetargetAction(new MatchWidthRetargetAction());
        addRetargetAction(new MatchHeightRetargetAction());

        addRetargetAction(new AlignmentRetargetAction(PositionConstants.TOP));
        addRetargetAction(new AlignmentRetargetAction(PositionConstants.MIDDLE));
        addRetargetAction(new AlignmentRetargetAction(PositionConstants.BOTTOM));
        addRetargetAction(new AlignmentRetargetAction(PositionConstants.LEFT));
        addRetargetAction(new AlignmentRetargetAction(PositionConstants.CENTER));
        addRetargetAction(new AlignmentRetargetAction(PositionConstants.RIGHT));

        RetargetAction a = new RetargetAction(
                GEFActionConstants.TOGGLE_GRID_VISIBILITY,
                "Toggle Grid Visibility", IAction.AS_CHECK_BOX);
        a.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
                SdsUiPlugin.PLUGIN_ID, "icons/grid.png")); //$NON-NLS-1$
        addRetargetAction(a);

        a = new RetargetAction(GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY,
                "Toggle Snap To Geometry", IAction.AS_CHECK_BOX);
        a.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
                SdsUiPlugin.PLUGIN_ID, "icons/snap2geometry.png"));
        addRetargetAction(a);

        a = new RetargetAction(GEFActionConstants.TOGGLE_RULER_VISIBILITY,
                "Toggle Ruler Visibility", IAction.AS_CHECK_BOX);
        a.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
                SdsUiPlugin.PLUGIN_ID, "icons/ruler.png"));
        addRetargetAction(a);

        a = new RetargetAction(ArrangeAction.HORIZONTAL, "Arrange Horizontal");
        a.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID, "icons/arrange_hor.png"));
        addRetargetAction(a);

        a = new RetargetAction(ArrangeAction.VERTICAL, "Arrange Vertical");
        a.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID, "icons/arrange_ver.png"));
        addRetargetAction(a);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void contributeToToolBar(final IToolBarManager tbm) {
        tbm.add(getAction(ActionFactory.UNDO.getId()));
        tbm.add(getAction(ActionFactory.REDO.getId()));
        tbm.add(getAction(ActionFactory.DELETE.getId()));

        tbm.add(new Separator());
        tbm.add(getAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY));
        tbm.add(getAction(GEFActionConstants.TOGGLE_RULER_VISIBILITY));
        tbm.add(getAction(GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY));

        tbm.add(new Separator());
        tbm.add(getAction(GEFActionConstants.ALIGN_LEFT));
        tbm.add(getAction(GEFActionConstants.ALIGN_CENTER));
        tbm.add(getAction(GEFActionConstants.ALIGN_RIGHT));
        tbm.add(new Separator());
        tbm.add(getAction(GEFActionConstants.ALIGN_TOP));
        tbm.add(getAction(GEFActionConstants.ALIGN_MIDDLE));
        tbm.add(getAction(GEFActionConstants.ALIGN_BOTTOM));

        tbm.add(new Separator());
        tbm.add(getAction(ArrangeAction.HORIZONTAL));
        tbm.add(getAction(ArrangeAction.VERTICAL));

        tbm.add(new Separator());
        tbm.add(getAction(GEFActionConstants.MATCH_WIDTH));
        tbm.add(getAction(GEFActionConstants.MATCH_HEIGHT));

        tbm.add(new Separator());
        tbm.add(getAction(GEFActionConstants.ZOOM_IN));
        tbm.add(getAction(GEFActionConstants.ZOOM_OUT));
        tbm.add(new ZoomComboContributionItem(getPage()));
        tbm.add(new GridSpacingContributionItem());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void declareGlobalActionKeys() {
        addGlobalActionKey(ActionFactory.PRINT.getId());
        addGlobalActionKey(ActionFactory.SELECT_ALL.getId());
        addGlobalActionKey(ActionFactory.PASTE.getId());
        addGlobalActionKey(ActionFactory.DELETE.getId());
    }

}
