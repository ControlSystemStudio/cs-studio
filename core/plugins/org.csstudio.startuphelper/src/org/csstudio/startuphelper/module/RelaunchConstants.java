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
 package org.csstudio.startuphelper.module;

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
