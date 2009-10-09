package org.csstudio.opibuilder.runmode;
import org.csstudio.opibuilder.actions.NavigateOPIsAction;
import org.eclipse.gef.ui.actions.ActionRegistry;
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
	

	public OPIRunnerToolBarContributor() {
		backwardAction = new NavigateOPIsAction(false);
		forwardAction = new NavigateOPIsAction(true);
	}
	
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		toolBarManager.add(backwardAction);
		toolBarManager.add(forwardAction);
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
	}
	
}
