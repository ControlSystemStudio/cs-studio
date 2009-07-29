package org.csstudio.opibuilder.runmode;


import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;

/**
 * ContextMenuProvider implementation for the OPI Runner.
 * 
 * @author Xihui Chen
 * 
 */
public final class OPIRunnerContextMenuProvider extends ContextMenuProvider {
	

	/**
	 * Constructor.
	 * 
	 * @param viewer
	 *            the graphical viewer
	 */
	public OPIRunnerContextMenuProvider(final EditPartViewer viewer) {
		super(viewer);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void buildContextMenu(final IMenuManager menu) {		
		GEFActionConstants.addStandardActionGroups(menu);
		MenuManager cssMenu = new MenuManager("CSS", "css");
		cssMenu.add(new Separator("additions"));
		menu.add(cssMenu);		
	}
	
	

}
