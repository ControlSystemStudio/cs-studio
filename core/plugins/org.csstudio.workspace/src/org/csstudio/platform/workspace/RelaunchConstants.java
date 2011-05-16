package org.csstudio.platform.workspace;

/** Constants used for the workbench relaunch.
 *  <p>
 *  Best documentation of this mess might be in Eclipse Bugzilla Bug 61809:
 *  <p>
 *  <code>IWorkbench.restart()</code> performs a normal restart,
 *  except when setting the exit code property to
 *  <code>IPlatformRunnable.EXIT_RELAUNCH</code>,
 *  in which case the exit data property is supposed to contain the command
 *  line for the relaunch.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public interface RelaunchConstants
{
    /** Property used to communicate back to eclipse that we want a restart
     *  via a magic IPlatformRunnable.EXIT_RELAUNCH code.
     */
    final public static String PROP_EXIT_CODE = "eclipse.exitcode";

    /** Property that passes the new command line to eclipse.
     *  <p>
     *  Bugzilla 175140:
     *  It's "eclipse.exitdata".
     *  javadoc for IPlatformRunnable.EXIT_RELAUNCH is wrong.
     *  
     *  @see #PROP_EXIT_CODE
     */
    final public static String PROP_EXIT_DATA = "eclipse.exitdata";
    
    /** Property that holds all commands passed on command line. */
    final public static String PROP_COMMANDS  = "eclipse.commands";
    
    /** Property for VM, copied from the "-vm /usr/bin/java" command-line
     *  arg that the product magically defines.
     */
    final public static String PROP_VM        = "eclipse.vm";
 
    /** Property that contains the VM -"-vmargs ..." arguments */
    final public static String PROP_VMARGS    = "eclipse.vmargs";
}
