package org.csstudio.utility.product;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;

public interface IWorkbenchWindowAdvisorExtPoint
{
	public static final String ID = "org.csstudio.utility.product.module";
	/** The name of this extension point element */
	public static final String NAME = "class";
	
    /**
     * Performs arbitrary actions before the window is opened.
     * <p>
     * This method is called before the window's controls have been created.
     * Clients must not call this method directly (although super calls are okay).
     * The default implementation does nothing. Subclasses may override.
     * Typical clients will use the window configurer to tweak the
     * workbench window in an application-specific way; however, filling the
     * window's menu bar, tool bar, and status line must be done in 
     * {@link ActionBarAdvisor#fillActionBars}, which is called immediately
     * after this method is called.
     * </p>
     */
    void preWindowOpen();

    /**
     * Performs arbitrary actions as the window's shell is being closed
     * directly, and possibly veto the close.
     * <p>
     * This method is called from a ShellListener associated with the window,
     * for example when the user clicks the window's close button. It is not
     * called when the window is being closed for other reasons, such as if the
     * user exits the workbench via the {@link ActionFactory#QUIT} action.
     * Clients must not call this method directly (although super calls are
     * okay). If this method returns <code>false</code>, then the user's
     * request to close the shell is ignored. This gives the workbench advisor
     * an opportunity to query the user and/or veto the closing of a window
     * under some circumstances.
     * </p>
     * 
     * @return <code>true</code> to allow the window to close, and
     *         <code>false</code> to prevent the window from closing
     * @see org.eclipse.ui.IWorkbenchWindow#close
     * @see WorkbenchAdvisor#preShutdown()
     */
    public boolean preWindowShellClose();

    /**
     * Performs arbitrary actions after the window has been restored, 
     * but before it is opened.
     * <p>
     * This method is called after a previously-saved window has been
     * recreated. This method is not called when a new window is created from
     * scratch. This method is never called when a workbench is started for the
     * very first time, or when workbench state is not saved or restored.
     * Clients must not call this method directly (although super calls are okay).
     * The default implementation does nothing. Subclasses may override.
     * It is okay to call <code>IWorkbench.close()</code> from this method.
     * </p>
     * 
     * @exception WorkbenchException thrown if there are any errors to report
     *   from post-restoration of the window
     */
    void postWindowRestore() throws WorkbenchException;

    /**
     * Performs arbitrary actions after the window has been created (possibly 
     * after being restored), but has not yet been opened.
     * <p>
     * This method is called after the window has been created from scratch, 
     * or when it has been restored from a previously-saved window.  In the latter case,
     * this method is called after <code>postWindowRestore</code>.
     * Clients must not call this method directly (although super calls are okay).
     * The default implementation does nothing. Subclasses may override.
     * </p>
     */
    void postWindowCreate();

    /**
     * Performs arbitrary actions after the window has been opened (possibly 
     * after being restored).
     * <p>
     * This method is called after the window has been opened. This method is 
     * called after the window has been created from scratch, or when
     * it has been restored from a previously-saved window.
     * Clients must not call this method directly (although super calls are okay).
     * The default implementation does nothing. Subclasses may override.
     * </p>
     */
    void postWindowOpen();

    /**
     * Performs arbitrary actions after the window is closed.
     * <p>
     * This method is called after the window's controls have been disposed.
     * Clients must not call this method directly (although super calls are
     * okay). The default implementation does nothing. Subclasses may override.
     * </p>
     */
    void postWindowClose();
    
    /**
     * Saves arbitrary application specific state information.
     * 
     * @param memento
     * @return a status object indicating whether the save was successful
     */
	public IStatus saveState(IMemento memento);

	/**
	* Restores arbitrary application specific state information.
	* 
	* @param memento
	* @return a status object indicating whether the restore was successful
	*/
	public IStatus restoreState(IMemento memento);
}
