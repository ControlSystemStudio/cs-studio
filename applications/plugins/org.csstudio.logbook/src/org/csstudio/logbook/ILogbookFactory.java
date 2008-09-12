package org.csstudio.logbook;

/** Interface to something that can create logbooks
 *  @author nypaver
 *  @author Kay Kasemir
 */
public interface ILogbookFactory
{
    /** ID of the extension point for providing an ILogbookFactory */
    final public static String EXTENSION_ID =
        "org.csstudio.logbook.logbookfactory"; //$NON-NLS-1$

    /** Some logbook systems may support more than one logbook.
     *  In that case, this method lists them.
     *  @return List of logbook names or <code>null</code>
     *  @throws Exception on error
     */
    public String[] getLoogbooks() throws Exception;
    
    /** In case there are multiple logbooks, this would
     *  provide the suggested default.
     *  @return Name of default logbook or <code>null</code>
     */
    public String getDefaultLogbook();
    
	/** Connect to a logbook
	 *  @param logbook Name of the logbook in case the system has more than one.
	 *                 Otherwise <code>null</code>.
	 *  @param user User name used when connecting to logbook (Oracle, ...)
	 *  @param password password that goes with the user
	 *  @return Logbook interface
	 *  @throws Exception on error
	 */
	public ILogbook connect(String logbook, String user, String password) throws Exception;
}