/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.data.values;

import java.io.Serializable;

/** Description of the 'severity' of a value.
 *  <p>
 *  The severity code is usually meant to indicate if the process variable
 *  that provided a value was 'ok', or in various states of warning or error.
 *  <p>
 *  Different implementations might have more or fewer warning and error states.
 *  To allow the implementation of generic tools, we define generic
 *  states 'OK', 'Minor', 'Major' and 'Invalid' as follows:
 *  <ul>
 *  <li>OK - The 'normal' severity, indicating that the process variable is
 *           fine.
 *  <li>Minor - A severity that indicates a minor warning. The process variable
 *              is valid, but its value might be close to some trip threshold.
 *              Some applications might indicate this by displaying the value
 *              in yellow.
 *  <li>Major - A severity that indicates a major warning. The process variable
 *              is valid, but its value might have exceeded some trip threshold.
 *              Some applications might indicate this by displaying the value
 *              in red.
 *  <li>Invalid - A severity that indicates an error. The process variable
 *              is probably invalid. The value could be a bad or old reading,
 *              Some applications might indicate this by displaying the value
 *              in grey.
 *  </ul>
 *  @author Kay Kasemir
 */
public interface ISeverity extends Serializable
{
    /** @return Returns the textual representation for this severity. */
    @Override
    public String toString();

    /** @return Returns <code>true</code> if this severity is in
     *          the 'OK' category.
     */
    public boolean isOK();

    /** @return Returns <code>true</code> if this severity is in
     *          the 'Minor' category.
     */
    public boolean isMinor();

    /** @return Returns <code>true</code> if this severity is in
     *          the 'Major' category.
     */
    public boolean isMajor();

    /** @return Returns <code>true</code> if this severity is in
     *          the 'Invalid' category.
     */
    public boolean isInvalid();

    /** @return Returns <code>true</code> if the associated value is meaningful,
     *  <code>false</code> if the value represents for example a 'disconnected'
     *  state and thus has no numeric value.
     */
    public boolean hasValue();
}
