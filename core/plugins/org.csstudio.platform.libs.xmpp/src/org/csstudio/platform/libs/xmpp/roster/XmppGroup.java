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
 package org.csstudio.platform.libs.xmpp.roster;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.libs.dcf.directory.ContactElement;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;

public class XmppGroup extends ContactElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1943811138372864696L;
	private transient RosterGroup baseGroup;
	private List<ContactElement> _cachedChildren =
		new ArrayList<ContactElement>();
	
	public XmppGroup(RosterGroup group, Roster roster) {
		baseGroup = group;
		// create the cache now
		for(RosterEntry entry : baseGroup.getEntries()) {
			_cachedChildren.add(new XmppUser(entry, roster, this));
		}
	}

	@Override
	public List<ContactElement> getChildren() {
		return _cachedChildren;
	}
	
	@Override
	public String toString() {
		return baseGroup.getName();
	}

	@Override
	public ContactElement getParent() {
		return null;
	}

	@Override
	public boolean isAvaiable() {
		return true;
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public boolean isManageable() {
		return false;
	}
}
