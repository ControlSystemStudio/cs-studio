/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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
import java.util.Collection;
import java.util.Collections;


/**
 * Information about an authorization ID which is used for actions which can
 * only be used by authorized users. Instances of this class provide information
 * about authorization IDs that are registered via the {@code authorizationId}
 * extension point. Instances of this class are immutable.
 * 
 * @author Joerg Rathlev
 */
public final class RegisteredAuthorizationId {

	private final String _id;
	private final String _description;
	private final String _contributor;
	private final ArrayList<AuthorizationIdUsage> _usage;

	/**
	 * Creates a new authorization ID information.
	 * 
	 * @param id
	 *            the authorization ID.
	 * @param description
	 *            the description of the authorization ID.
	 * @param usage
	 *            a collection of descriptions of places where the authorization
	 *            ID is used.
	 */
	public RegisteredAuthorizationId(String id, String description, String contributor,
			Collection<AuthorizationIdUsage> usage) {
		_id = id;
		_description = description;
		_contributor = contributor;
		// Create a copy of the collection to keep this object immutable
		_usage = new ArrayList<AuthorizationIdUsage>(usage);
	}
	
	/**
	 * Returns the authorization ID described by this object.
	 * 
	 * @return the authorization ID described by this object.
	 */
	public String getId() {
		return _id;
	}
	
	/**
	 * Returns the description of the authorization ID.
	 * 
	 * @return the description of the authorization ID.
	 */
	public String getDescription() {
		return _description;
	}
	
	/**
	 * Returns the name of the contributor. This is usually the id of the plugin, where the authorization id has been registered.
	 * 
	 * @return the name of the contributor
	 */
	public String getContributor() {
        return _contributor;
    }

	/**
	 * Returns an unmodifiable collection of descriptions of the places where
	 * the authorization ID described by this object is used.
	 * 
	 * @return an unmodifiable collection of descriptions of the places where
	 *         the authorization ID is used.
	 */
	public Collection<AuthorizationIdUsage> getUsage() {
		return Collections.unmodifiableCollection(_usage);
	}
}
