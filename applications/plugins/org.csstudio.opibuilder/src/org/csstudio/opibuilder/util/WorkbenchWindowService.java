package org.csstudio.opibuilder.util;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.opibuilder.actions.CompactModeAction;
import org.csstudio.opibuilder.actions.FullScreenAction;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.WorkbenchWindow;

/**A service for storing related resources for each workbench window.
 * @author Xihui Chen
 *
 */
@SuppressWarnings("restriction")
public final class WorkbenchWindowService {
	
	
	private static WorkbenchWindowService instance;
	
	private Map<IWorkbenchWindow, CompactModeAction> compactModeRegistry;
	
	private Map<IWorkbenchWindow, FullScreenAction> fullScreenRegistry;
	
	private static boolean inCompactMode;
	
	public WorkbenchWindowService() {
		compactModeRegistry = new HashMap<IWorkbenchWindow, CompactModeAction>();
		fullScreenRegistry = new HashMap<IWorkbenchWindow, FullScreenAction>();
	}
	
	public synchronized static final WorkbenchWindowService getInstance() {
		if(instance == null)
			instance = new WorkbenchWindowService();
		return instance;
	}
	
	public void registerCompactModeAction(CompactModeAction action, IWorkbenchWindow window){
		compactModeRegistry.put(window, action);
	}
	
	public void unregisterCompactModeAction(IWorkbenchWindow window){
		compactModeRegistry.remove(window);
	}
	
	public void registerFullScreenAction(FullScreenAction action, IWorkbenchWindow window){
		fullScreenRegistry.put(window, action);
	}
	
	public void unregisterFullScreenAction(IWorkbenchWindow window){
		fullScreenRegistry.remove(window);
	}
	
	
	public CompactModeAction getCompactModeAction(IWorkbenchWindow window){
		return compactModeRegistry.get(window);
	}
	
	public FullScreenAction getFullScreenAction(IWorkbenchWindow window){
		return fullScreenRegistry.get(window);
	}
	
	public static void setInCompactMode(boolean inCompactMode) {
		WorkbenchWindowService.inCompactMode = inCompactMode;
	}
	
	public static boolean isInCompactMode() {
		return inCompactMode | PreferencesHelper.isStartWindowInCompactMode();
	}

	public static void setToolbarVisibility(final WorkbenchWindow window, final boolean visible){
		window.setCoolBarVisible(visible);
		window.setPerspectiveBarVisible(visible);

		//All these don't work
		// window.setStatusLineVisible(false);
		// window.getActionBars().getStatusLineManager().getItems()[0].setVisible(visible);
		// window.getStatusLineManager().getItems()[0].setVisible(visible);
		// window.getStatusLineManager().getControl().setVisible(visible);

		//A hack to set status line invisible.
		for (Control child : window.getShell().getChildren()) {
			if (child.isDisposed())
				continue;
			if (child.getClass().equals(Canvas.class))
				continue;
			if (child.getClass().equals(Composite.class))
				continue;
			child.setVisible(visible);

		}
		window.getShell().layout();
		
	}
	
}
