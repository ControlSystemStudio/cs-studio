package org.csstudio.opibuilder.editor;
import org.csstudio.opibuilder.OPIBuilderPlugin;
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


public class OPIEditorActionBarContributor extends ActionBarContributor {

	public OPIEditorActionBarContributor() {
	}

	@Override
	protected void buildActions() {
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
				OPIBuilderPlugin.PLUGIN_ID, "icons/grid.png")); //$NON-NLS-1$
		addRetargetAction(a);

		a = new RetargetAction(GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY,
				"Toggle Snap To Geometry", IAction.AS_CHECK_BOX);
		a.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				OPIBuilderPlugin.PLUGIN_ID, "icons/snap2geometry.png"));
		addRetargetAction(a);
		
		a = new RetargetAction(GEFActionConstants.TOGGLE_RULER_VISIBILITY,
				"Toggle Ruler Visibility", IAction.AS_CHECK_BOX);
		a.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				OPIBuilderPlugin.PLUGIN_ID, "icons/ruler.png"));
		addRetargetAction(a);
	}

	@Override
	public void contributeToToolBar(IToolBarManager tbm) {
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
		tbm.add(getAction(GEFActionConstants.MATCH_WIDTH));
		tbm.add(getAction(GEFActionConstants.MATCH_HEIGHT));

		tbm.add(new Separator());
		tbm.add(getAction(GEFActionConstants.ZOOM_IN));
		tbm.add(getAction(GEFActionConstants.ZOOM_OUT));
		tbm.add(new ZoomComboContributionItem(getPage()));
		
		tbm.add(new Separator());
		tbm.add(new RunOPIAction());
		
		
	}
	
	@Override
	protected void declareGlobalActionKeys() {
		addGlobalActionKey(ActionFactory.PRINT.getId());
		addGlobalActionKey(ActionFactory.SELECT_ALL.getId());
		addGlobalActionKey(ActionFactory.PASTE.getId());
		addGlobalActionKey(ActionFactory.DELETE.getId());
		addGlobalActionKey(ActionFactory.UNDO.getId());
		addGlobalActionKey(ActionFactory.REDO.getId());
	}

}
