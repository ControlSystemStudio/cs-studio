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
 package org.csstudio.dal.simple;

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
public interface Severity
{
    /** @return Returns the textual representation for this severity. */
    public String getSeverityInfo();

    /** @return Returns the textual representation of the description. */
    public String descriptionToString();

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