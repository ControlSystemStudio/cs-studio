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
package org.csstudio.auth.security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * A <code>RightSet</code> consists of an amount of <code>Rights</code> and
 * a name.
 * 
 * @author Kai Meyer & Torsten Witte & Alexander Will & Sven Wende
 */
public class RightSet implements Iterable<IRight> {

	/**
	 * The name of this <code>RightSet</code>.
	 */
	private final String _name;

	/**
	 * The <code>Rights</code> that belong to this <code>RightSet</code>.
	 */
	private List<IRight> _rights = new ArrayList<IRight>();

	/**
	 * Standard cnstructor.
	 * 
	 * @param name
	 *            The name of this <code>RightSet</code>
	 */
	public RightSet(final String name) {
		_name = name;
	}

	/**
	 * Add a <code>Right</code> to this <code>RightSet</code>.
	 * 
	 * @param right
	 *            The <code>Right</code> to add.
	 */
	public final void addRight(final IRight right) {
		if (right != null) {
			_rights.add(right);
		}
	}

	/**
	 * Delete a <code>Right</code> from this <code>RightSet</code>.
	 * 
	 * @param right
	 *            The <code>Right</code> to delete.
	 */
	public final void deleteRight(final IRight right) {
		_rights.remove(right);
	}

	/**
	 * Return all <code>Rights</code> of this <code>RightSet</code>.
	 * 
	 * @return All <code>Rights</code> of this <code>RightSet</code>
	 */
	public final List<IRight> getRights() {
		return new ArrayList<IRight>(_rights);
	}

	/**
	 * Return the name of this <code>RightSet</code>.
	 * 
	 * @return The name of this <code>RightSet</code>.
	 */
	public final String getName() {
		return _name;
	}

	/**
	 * Checks if this <code>RightSet</code> contains the given
	 * <code>Right</code>.
	 * 
	 * @param right
	 *            The <code>Right</code> to check.
	 * @return True, if this <code>RightSet</code> contains the
	 *         <code>Right</code>.
	 */
	public final boolean hasRight(final IRight right) {
		return _rights.contains(right);
	}
	
	/**
	 * Returns true if this <code>RightSet</code> is empty <code>false</code>
	 * otherwise.
	 */
	public boolean isEmpty() {
		return _rights.isEmpty();
	}

	/**
	 * @see java.lang.Object#toString()
	 * @return a string representation of this <code>RightSet</code>
	 */
	@SuppressWarnings("nls")
    @Override
	public final String toString() {
	    final StringBuilder text = new StringBuilder(_name);
	    if (_rights.size() <= 0)
	        text.append(" <empty>");
	    else
	        for (int i = 0; i < _rights.size(); i++)
	            text.append(" ").append(_rights.get(i).toString());
	    return text.toString();
	}

	public Iterator<IRight> iterator() {
		return _rights.iterator();
	}
}
