/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.openfile;

/**Open display from external program, such as browser, alarm GUI...
 * @author Xihui Chen
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public interface IOpenDisplayAction
{
    /** Extension point that uses this interface */
    final public static String EXTENSION_POINT_ID = "org.csstudio.openfile.openDisplay";

    /**Open display
	 * @param path the path of the display file, format defined by implementation
	 * @param data Additional data used by the display,
	 *             for example "macro1=value1, macro2=value2",
	 *             but details left to implementation.
	 *             May be <code>null</code>.
	 * @throws Exception on error
	 */
	public void openDisplay(String path, String data) throws Exception;
}
