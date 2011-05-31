/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook;

/** Interface to something that can create logbooks
 *  @author Delphy Nypaver Armstrong
 *  @author Kay Kasemir
 */
public interface ILogbookFactory
{
    /** ID of the extension point for providing an ILogbookFactory */
    final public static String EXTENSION_ID =
        "org.csstudio.logbook.logbookfactory"; //$NON-NLS-1$

    /** Some logbook systems may support more than one logbook.
     *  In that case, this method lists them.
     *  @return List of logbook names. Maybe empty, but not <code>null</code>.
     *  @throws Exception on error
     */
    public String[] getLogbooks() throws Exception;
    
    /** In case there are multiple logbooks, this would
     *  provide the suggested default.
     *  @return Name of default logbook. Maybe "", but not <code>null</code>.
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
