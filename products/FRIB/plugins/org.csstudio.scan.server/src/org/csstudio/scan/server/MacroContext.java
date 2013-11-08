/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.server;

/** Context for macros
 *  @author Kay Kasemir
 */
public interface MacroContext
{
    /** @param text Text that may contain "$(macro)"
     *  @return Text where macros have been replaced by their values
     *  @throws Exception on error in macros
     */
	public String resolveMacros(String text) throws Exception;

    /** @param names_and_values Macros in the form "name=value, name=value" to add to the context
     *  @throws Exception on error in macros
     *  @see #popMacros()
     */
    public void pushMacros(String names_and_values) throws Exception;

    /** Restore macros to the state before last push
     *  @see #pushMacros()
     */
    public void popMacros();

}
