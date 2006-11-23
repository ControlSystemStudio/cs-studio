/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.platform.internal.rightsmanagement;


/**
 * This class implements a right, dependent on a role and user group.
 * @author Kai Meyer & Torsten Witte & Alexander Will & Sven Wende
 */
public class Right implements IRight{
	
	/**
	 * The user role.
	 */
	private final String _role;
	
	
	/**
	 * The user group. 
	 */
	private final String _group;
	
	/**
	 * Constructor.
	 * @param role The role
	 * @param group The group
	 */
	public Right(final String role, final String group) {
		_role = role;
		_group = group;
	}
	
	/**
	 * Delivers the group.
	 * @return  The group
	 */
	public final String getGroup() {
		return _group;
	}
	
	/**
	 * Delivers the role.
	 * @return  The role
	 */
	public final String getRole() {
		return _role;
	}	
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @param o another Right
	 * @return true, if this right is equal to another right, false qotherwise.
	 */
	@Override
	public final boolean equals(final Object o) {
		if (o instanceof Right) {
			Right right = (Right) o;
			return _role.equals(right.getRole()) && _group.equals(right.getGroup());
		}
		return super.equals(o);
	}
	
	
	/**
	 * @see java.lang.Object#hashCode()
	 * @return the hashCode of this right
	 */
	@Override
	public final int hashCode() {
		return super.hashCode();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 * @return  a string representation of this right.
	 */
	@Override
	public final String toString() {
		return "(" + _role + "," + _group + ")";
	}

}
