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
import java.util.Iterator;
import java.util.List;

import org.csstudio.platform.libs.dcf.directory.ContactElement;
import org.csstudio.platform.libs.xmpp.XmppConnectionManager;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;

public class XmppUser extends ContactElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5794959041232153350L;
	
	private transient RosterEntry _entry;
	private ContactElement _parent;
	private transient Roster _roster;
	private String _userName;
	
	public XmppUser(RosterEntry entry, Roster roster, ContactElement parent) {
		 _entry = entry;
		 _parent = parent;
		 _roster = roster;
		 _userName = _entry.getUser();
	}

	@Override
	public List<ContactElement> getChildren() {
		if(_roster != null) {
			List<ContactElement> elements = new ArrayList<ContactElement>();
			Iterator<Presence> i = _roster.getPresences(_entry.getUser());
			while(i.hasNext()) {
				elements.add(new XmppResource(this, i.next()));
			}
			return elements;
		}
		return null;
	}
	
	@Override
	public String toString() {
		if(_userName == "")
			return "user";
		
		return _userName;
	}

	@Override
	public ContactElement getParent() {
		return _parent;
	}

	public Presence getPresence() {
		try {
		return getRoster().getPresence(this._userName);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Roster getRoster() {
		try {
		if(_roster == null)
			_roster = ((XmppConnectionManager)XmppConnectionManager.getDefault()).getRoster();
		
		return _roster;
		
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean isAvaiable() {
		return getPresence().isAvailable();
	}

	@Override
	public boolean hasChildren() {
		// is true, if it's avaiable
		return this.isAvaiable();
	}

	@Override
	public boolean isManageable() {
		return false;
	}

}