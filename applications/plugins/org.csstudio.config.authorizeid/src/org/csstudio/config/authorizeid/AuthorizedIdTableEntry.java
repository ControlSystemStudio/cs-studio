/* 
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.config.authorizeid;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.auth.security.RegisteredAuthorizationId;
import org.csstudio.auth.security.SecurityFacade;
import org.eclipse.core.runtime.PlatformObject;

/**
 * Entry for Authorized-ID Table
 * @author Jörg Penning
 */
@SuppressWarnings("serial")
class AuthorizedIdTableEntry extends PlatformObject implements Serializable {

    private final String _authorizeId;
    private final String _description;

	private AuthorizedIdTableEntry(@Nonnull final String authorizeId, @Nullable final String description) {
        this._authorizeId = authorizeId;
        this._description = description;
	}

	@Nonnull
	public String getAuthorizeId() {
        return _authorizeId;
    }
	
	@CheckForNull
	public String getDescription() {
        return _description;
    }
	
	/**
	 * If a description has been given it is assumed that the authorize id has been registered at some plugin.
	 * 
	 * @return true, if a description has been given
	 */
	public boolean isRegisteredAtPlugin() {
	    return _description != null;
	}
	
	
	public static AuthorizedIdTableEntry createEntry(@Nonnull final String authorizeId) {
	    final String description = getDescription(authorizeId);
        return new AuthorizedIdTableEntry(authorizeId, description);
	}
	
    @CheckForNull
    private static RegisteredAuthorizationId getRegisteredAuthorizationId(@Nonnull final String authorizationId) {
        RegisteredAuthorizationId result = null;
        Collection<RegisteredAuthorizationId> authIds = SecurityFacade.getInstance()
                .getRegisteredAuthorizationIds();
        for (RegisteredAuthorizationId registeredAuthorizationId : authIds) {
            if (registeredAuthorizationId.getId().equals(authorizationId)) {
                result = registeredAuthorizationId;
                break;
            }
        }
        return result;
    }
    
    @CheckForNull
    private static String getDescription(@Nonnull final String authorizationId) {
        RegisteredAuthorizationId registeredAuthorizationId = getRegisteredAuthorizationId(authorizationId);
        return registeredAuthorizationId == null ? null : registeredAuthorizationId
                .getDescription();
    }

}
