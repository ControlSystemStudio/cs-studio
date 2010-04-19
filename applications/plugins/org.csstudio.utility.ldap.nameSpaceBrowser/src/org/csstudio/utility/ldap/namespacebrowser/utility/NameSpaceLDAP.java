/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id$
 */
package org.csstudio.utility.ldap.namespacebrowser.utility;

import javax.naming.directory.SearchControls;

import org.csstudio.utility.ldap.namespacebrowser.Activator;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.csstudio.utility.ldap.service.LdapService;
import org.csstudio.utility.ldap.service.impl.LdapServiceImpl;
import org.csstudio.utility.nameSpaceBrowser.utility.NameSpace;
import org.csstudio.utility.namespace.utility.NameSpaceSearchResult;


/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 07.05.2007
 */
public class NameSpaceLDAP extends NameSpace {

    private final LdapService _service = LdapServiceImpl.getInstance();

	/* (non-Javadoc)
	 * @see org.csstudio.utility.nameSpaceBrowser.utility.NameSpace#start()
	 */
	@Override
	public void start() {
		try{
            final NameSpaceSearchResult nameSpaceResultList = getNameSpaceResultList();
            if (nameSpaceResultList instanceof LdapSearchResult) {

                LdapSearchResult result;
                if(getSelection().endsWith("=*,")) {
                    result = _service.retrieveSearchResult(getName(),
                                                           getFilter(),
                                                           SearchControls.SUBTREE_SCOPE);
                } else {
                    result = _service.retrieveSearchResult(getName(),
                                                           getFilter(),
                                                           SearchControls.ONELEVEL_SCOPE);
                }
                updateResultList(result.getCSIResultList());

            } else{
                // TODO: Was soll gemacht werden wenn das 'getNameSpaceResultList() instanceof LdapSearchResult' nicht stimmt.
                Activator.logError(Messages.getString("CSSView.exp.IAE.2")); //$NON-NLS-1$
            }
        }catch (final IllegalArgumentException e) {
            Activator.logException(Messages.getString("CSSView.exp.IAE.1"), e); //$NON-NLS-1$
        }
	}
}
