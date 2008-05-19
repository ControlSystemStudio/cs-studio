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

	/** Connect to a logbook
	 *  @param user User name used when connecting to logbook (Oracle, ...)
	 *  @param password password that goes with the user
	 *  @return Logbook interface
	 *  @throws Exception on error
	 */
	public ILogbook connect(String user, String password) throws Exception;
}