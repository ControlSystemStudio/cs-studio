package org.csstudio.util.plugin;

/** Copy/paste logging routines for the plugin class.
 *  <p>
 *  I happen to like to use the Plugin log via these
 *  routines.
 *  When the plugin is based on the CSS plugin classes,
 *  those messages of course also go into the CSS log.
 *  <p>
 *  Since not everybody might like this, they're added
 *  by copy/paste to the plugin activators in my apps,
 *  without having to change the CSS plugin base classes.
 *  
 *  @author Kay Kasemir
 */
public interface PluginLogRoutines
{
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
