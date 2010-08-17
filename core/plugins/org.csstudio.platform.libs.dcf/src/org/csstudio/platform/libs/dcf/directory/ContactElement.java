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
 /**
 * 
 */
package org.csstudio.platform.libs.dcf.directory;

import java.io.Serializable;
import java.util.List;

/**
 * This abstract class provides the sub-classes with a
 * common interface and implementation to represent the
 * directory with.
 * 
 * @author avodovnik
 *
 */
public abstract class ContactElement implements Serializable {
	/**
	 * Holds the serial version UID.
	 */
	private static final long serialVersionUID = 1334675226399020868L;
	
	/**
	 * Holds the human readable name of the element.
	 */
	private String _name;
	/**
	 * Holds the id of the element.
	 */
	private String _id;
	
	/** 
	 * Returns a human readable value representing the
	 * name of the contact.
	 * 
	 * @return Returns a string containing a human readable
	 * representation of the contact.
	 */
	public String getName() {
		return _name;
	}
	
	/**
	 * Returns an identifier used by the protocol implementation
	 * and UI to keep track of the directory entries (if required).
	 * 
	 * @return Returns a string containing the identifier
	 * for the contact.
	 */
	public String getId() {
		return _id;
	}
	
	/**
	 * The implementation of this method must return all the
	 * children elements belonging to this one.
	 * 
	 * @return Returns a list containing ContactElement elements
	 * which can contain protocol specific implementation
	 * representing specific parts of the directory system.
	 */
	public abstract List<ContactElement> getChildren();
	
	public abstract boolean hasChildren();
	
	/**
	 * Returns whether this object is directly manageable.
	 * 
	 * @return Returns true if request can be issued directly to this
	 * implementation, or false if to its children (if there are any).
	 */
	public abstract boolean isManageable();
	
	/**
	 * Retrieves the parent object of this instance.
	 * @return Returns the parent of this instance.
	 */
	public abstract ContactElement getParent();
	
	/**
	 * Checks to see if the given resource is avaiable.
	 * @return Returns true if the resource can accept requests
	 * and action requests and false otherwise.
	 */
	public abstract boolean isAvaiable();
}
