/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook;

/** Interface to a Logbook
 *  @author nypaver
 *  @author Kay Kasemir
 */
public interface ILogbook
{
    /** Add new entry to the logbook.
     *  @param title Title
     *  @param text Text of the entry. Plain ASCII.
     *  @param file_names Names of files to attach or <code>null</code>.
     *         Has to contain the full path to the file including file ending.
     *         Exact path format depends on the operating system.
     *         File types that the logbook support depend on
     *         implementation but should include
     *           *.gif, *.jpg:  File will be attached as image
     *           *.html, *.htm: File will be attached as web page
     *           *.txt:         File will be attached as plain ASCII file
     *  @throws Exception on error
     */
	void createEntry(String title, String text, String ... file_names)
        throws Exception;

   /** Add new entry to the logbook.
    *  @param title Title
    *  @param text Text of the entry. Plain ASCII.
    *  @param file_names Names of files to attach or <code>null</code>.
    *         Has to contain the full path to the file including file ending.
    *         Exact path format depends on the operating system.
    *         File types that the logbook support depend on
    *         implementation but should include
    *           *.gif, *.jpg:  File will be attached as image
    *           *.html, *.htm: File will be attached as web page
    *           *.txt:         File will be attached as plain ASCII file
    *   @param captions Captions for the file names
    *   @throws Exception on error
    */
	void createEntry(String title, String text, String[] filenames, String[] captions)
       throws Exception;
   
    /** Close the logbook. Should be called when done to reclaim resources. */
    void close();
}
