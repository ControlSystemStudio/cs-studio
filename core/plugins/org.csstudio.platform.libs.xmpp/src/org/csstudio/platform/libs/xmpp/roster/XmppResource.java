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
import org.jivesoftware.smack.packet.Presence;

public class XmppResource extends ContactElement {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3088808614044246268L;
	private transient Presence _presence;
	private ContactElement _parent;
	private final String _userName;
	public XmppResource(XmppUser parent, Presence presence) {
		_presence = presence;
		_parent = parent;
		_userName = _presence.getFrom();
	}
	
	public XmppResource(String userName) {
		_userName = userName;
	}

	@Override
	public List<ContactElement> getChildren() {
		// does not have children
		return new ArrayList<ContactElement>();
	}

	@Override
	public ContactElement getParent() {
		return _parent;
	}

	@Override
	public boolean isAvaiable() {
		return true;
	}
	
	@Override
	public String getName() {
		return _userName;
	}
	
	@Override
	public String toString() {
		return _userName;
	}

	public Presence getPresence() {
		return _presence;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public boolean isManageable() {
		return true;
	}
	
}