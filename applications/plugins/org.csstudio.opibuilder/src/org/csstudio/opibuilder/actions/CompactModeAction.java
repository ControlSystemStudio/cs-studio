package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.WorkbenchWindow;


/**The action to make CSS full screen.
 * @author Xihui Chen
 *
 */
@SuppressWarnings("restriction")
public class CompactModeAction extends WorkbenchPartAction {
	
	public static final String ID = "org.csstudio.opibuilder.actions.compactMode";
	
	private ActionFactory.IWorkbenchAction toggleToolbarAction;
	private Menu menuBar;
	private boolean inFullScreenMode = false;
	private Shell shell;
	private ImageDescriptor fullScreenImage = 
		CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
			OPIBuilderPlugin.PLUGIN_ID, "icons/compact_mode.png");
	private ImageDescriptor exitFullScreenImage = 
		CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
			OPIBuilderPlugin.PLUGIN_ID, "icons/exit_compact_mode.gif");
	private IWorkbenchWindow window;
	private boolean toolbarWasInvisible;
	/**
	 * Constructor.
	 * @param part The workbench part associated with this PrintAction
	 */
	public CompactModeAction(IWorkbenchPart part) {
		super(part);
		window = part.getSite().getWorkbenchWindow();
		 toggleToolbarAction = ActionFactory.TOGGLE_COOLBAR.create(window); 
		 shell = part.getSite().getWorkbenchWindow().getShell();
		 menuBar = shell.getMenuBar();
		 setImageDescriptor(fullScreenImage);
		 
	}
	
	/**
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		return true;
	}
	
	/**
	 * @see org.eclipse.gef.ui.actions.EditorPartAction#init()
	 */
	protected void init() {
		super.init();
		setText("Compact Mode");
		setId(ID);
		}
	
	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		if(inFullScreenMode){
			if(!toolbarWasInvisible)
				toggleToolbarAction.run();
			shell.setMenuBar(menuBar);		
			inFullScreenMode = false;
			setText("Compact Mode");
			setImageDescriptor(fullScreenImage);
		}else {
			if(window instanceof WorkbenchWindow && !((WorkbenchWindow) window).getCoolBarVisible()){
				toolbarWasInvisible = true;
			}else{
				toolbarWasInvisible = false;
				toggleToolbarAction.run();
			}
			shell.setMenuBar(null);		
			inFullScreenMode = true;
			setText("Exit Compact Mode");
			setImageDescriptor(exitFullScreenImage);
		}
	}

}
