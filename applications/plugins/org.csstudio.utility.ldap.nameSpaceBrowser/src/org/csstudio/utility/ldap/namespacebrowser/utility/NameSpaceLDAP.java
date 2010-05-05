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

import javax.naming.CompositeName;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.engine.Engine;
import org.csstudio.utility.ldap.namespacebrowser.Activator;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.nameSpaceBrowser.utility.NameSpace;
import org.csstudio.utility.namespace.utility.NameSpaceSearchResult;


/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 07.05.2007
 */
public class NameSpaceLDAP extends NameSpace {

    private final Logger _log = CentralLogger.getInstance().getLogger(this);

	/* (non-Javadoc)
	 * @see org.csstudio.utility.nameSpaceBrowser.utility.NameSpace#start()
	 */
	@Override
	public void start() {
		try{
            final NameSpaceSearchResult nameSpaceResultList = getNameSpaceResultList();
            if (nameSpaceResultList instanceof LdapSearchResult) {
                final ILdapService service = Activator.getDefault().getLdapService();

                final NameParser parser = Engine.getInstance().getLdapDirContext().getNameParser(new CompositeName());

                final LdapName searchRoot = (LdapName) parser.parse(getName());

                LdapSearchResult result;
                if(getSelection().endsWith("=*,")) {
                    result = service.retrieveSearchResultSynchronously(searchRoot,
                                                                       getFilter(),
                                                                       SearchControls.SUBTREE_SCOPE);
                } else {
                    result = service.retrieveSearchResultSynchronously(searchRoot,
                                                                       getFilter(),
                                                                       SearchControls.ONELEVEL_SCOPE);
                }
                updateResultList(result.getCSIResultList());

            } else{
                // TODO: Was soll gemacht werden wenn das 'getNameSpaceResultList() instanceof LdapSearchResult' nicht stimmt.
                Activator.logError(Messages.getString("CSSView.exp.IAE.2")); //$NON-NLS-1$
            }
        } catch (final IllegalArgumentException e) {
            Activator.logException(Messages.getString("CSSView.exp.IAE.1"), e); //$NON-NLS-1$
        } catch (final NamingException ne) {
            _log.error("Error while parsing search root " + getName() + " as LDAP name.", ne);
        }
	}
}
