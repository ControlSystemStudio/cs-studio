package org.csstudio.apputil.plugin;

//import org.apache.log4j.Logger;
//import org.csstudio.platform.logging.CentralLogger;

/** Copy/paste logging routines for the plugin class.
 *  <p>
 *  I happen to like to log like this:
 *  Use Log4j, directly calling its log routines,
 *  so that one can get source file & line info
 *  into the log.
 *  Use CSS CentralLogger only to configure the logger.
 *  
 *  @author Kay Kasemir
 */
public interface PluginLogRoutines
{
//  /** Lazily initialized Log4j Logger */
//  private static Logger log = null;
//
//  /** @return Log4j Logger */
//  public static Logger getLogger()
//  {
//      if (log == null) // Also works with plugin==null during unit tests
//          log = CentralLogger.getInstance().getLogger(plugin);
//      return log;
//  }
//
//  Then log like this:
//     Plugin.getLogger().info(...);
//
//  For performance, sometimes consider isDebugEnabled()... as per Log4j docs.
    
//    Older alternative, using only the Eclipse plugin log:
//    /** Add info message to the plugin log. */
//    public static void logInfo(String message)
//    {
//        log(IStatus.INFO, message, null);
//    }
//    
//    /** Add error message to the plugin log. */
//    public static void logError(String message)
//    {
//        log(IStatus.ERROR, message, null);
//    }
//
//    /** Add an exception to the plugin log. */
//    public static void logException(String message, Throwable ex)
//    {
//        log(IStatus.ERROR, message, ex);
//    }
//
//    /** Add a message to the log.
//     *  @param type
//     *  @param message
//     *  @param e Exception or <code>null</code>
//     */
//    private static void log(int type, String message, Throwable ex)
//    {
//        if (plugin == null)
//            System.out.println(message);
//        else
//            plugin.getLog().log(new Status(type, ID, IStatus.OK, message, ex));
//        if (ex != null)
//            ex.printStackTrace();
//    }
}
