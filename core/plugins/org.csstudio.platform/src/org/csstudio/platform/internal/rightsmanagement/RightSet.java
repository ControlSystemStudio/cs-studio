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

import java.util.LinkedList;


/**
 * This class defines a rightset with a name and a list of IRights.
 * @author Kai Meyer & Torsten Witte & Alexander Will & Sven Wende
 */
public class RightSet {
	
	/**
	 * The name of this rightset.
	 */
	private final String _name;
	
	/**
	 * The list of IRights of this rightset.
	 */
	private final LinkedList<IRight> _rights = new LinkedList<IRight>();
	
	/**
	 * Constructor.
	 * @param name The name of this RightSet
	 */
	public RightSet(final String name) {
		_name = name;
	}
	
	/**
	 * Adds a right to this RightSet.
	 * @param right  The right to add
	 */
	public final void addRight(final IRight right) {
		if (right != null) {
			_rights.add(right);
		}
	}
	
	/**
	 * Deletes a right from this RightSet.
	 * @param right  The right to delete
	 */
	public final void deleteRight(final IRight right) {
		_rights.remove(right);
	}
	
	/**
	 * Delivers all rights of this RightSet.
	 * @return  All rights of this RightSet
	 */
	public final IRight[] getRights() {
		return _rights.toArray(new IRight[0]);
	}
	
	/**
	 * Delivers the name of this RightSet.
	 * @return  The name of this RightSet
	 */
	public final String getName() {
		return _name;
	}
	
	/**
	 * Checks if this RightSet contains the given right.
	 * @param right  The right to check
	 * @return  True if this RightSet contains the right; false otherwise
	 */
	public final boolean containsRight(final IRight right) {
		return _rights.contains(right);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 * @return a string representation of this rightset.
	 */
	@Override
	public final String toString() {
		String text = _name + " ";
		for (int i = 0; i < _rights.size(); i++) {
			text = text + _rights.get(i).toString() + " ";
		}
		return text;
	}

}

