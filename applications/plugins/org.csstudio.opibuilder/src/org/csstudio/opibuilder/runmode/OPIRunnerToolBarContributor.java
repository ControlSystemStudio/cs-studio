package org.csstudio.opibuilder.runmode;
import org.csstudio.opibuilder.actions.AutoNavigateAction;
import org.csstudio.opibuilder.actions.NavigateOPIsAction;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorActionBarContributor;


/**The toolbar contributor for OPI runner
 * @author Xihui Chen
 *
 */
public class OPIRunnerToolBarContributor extends EditorActionBarContributor {

	private NavigateOPIsAction backwardAction, forwardAction;
	private ZoomInRetargetAction zoomInAction;
	private ZoomOutRetargetAction zoomOutAction;
	//private AutoNavigateAction autoNavigateAction;
	
	public OPIRunnerToolBarContributor() {
		backwardAction = new NavigateOPIsAction(false);
		forwardAction = new NavigateOPIsAction(true);
		zoomInAction = new ZoomInRetargetAction();
		zoomOutAction = new ZoomOutRetargetAction();
		//autoNavigateAction = new AutoNavigateAction();
	}
	
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {		
		toolBarManager.add(zoomInAction);
		getPage().addPartListener(zoomInAction);
		toolBarManager.add(zoomOutAction);
		getPage().addPartListener(zoomOutAction);
		toolBarManager.add(new ZoomComboContributionItem(getPage()));
		toolBarManager.add(backwardAction);
		toolBarManager.add(forwardAction);
		//toolBarManager.add(autoNavigateAction);
	}
	
	@Override
	public void setActiveEditor(IEditorPart targetEditor) {
		DisplayOpenManager manager = 
			(DisplayOpenManager)targetEditor.getAdapter(DisplayOpenManager.class);
		backwardAction.setDisplayOpenManager(manager);
		forwardAction.setDisplayOpenManager(manager);
		IActionBars bars = getActionBars();		
		bars.setGlobalActionHandler(backwardAction.getId(), backwardAction);	
		bars.setGlobalActionHandler(forwardAction.getId(), forwardAction);
	
		ActionRegistry actionRegistry =
			(ActionRegistry) targetEditor.getAdapter(ActionRegistry.class);
		bars.setGlobalActionHandler(ActionFactory.PRINT.getId(), 
				actionRegistry.getAction(ActionFactory.PRINT.getId()));
		bars.setGlobalActionHandler(zoomInAction.getId(),
				actionRegistry.getAction(zoomInAction.getId()));
		bars.setGlobalActionHandler(zoomOutAction.getId(),
				actionRegistry.getAction(zoomOutAction.getId()));
		
		//autoNavigateAction.setPart(targetEditor);
		
		
		
	}
	
}
