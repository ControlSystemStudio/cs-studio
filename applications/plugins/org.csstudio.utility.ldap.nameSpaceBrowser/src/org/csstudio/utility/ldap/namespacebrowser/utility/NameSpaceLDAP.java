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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.model.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldap.namespacebrowser.Activator;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.utils.LdapSearchResult;
import org.csstudio.utility.nameSpaceBrowser.utility.NameSpace;
import org.csstudio.utility.namespace.utility.ControlSystemItem;
import org.csstudio.utility.namespace.utility.NameSpaceSearchResult;
import org.csstudio.utility.namespace.utility.ProcessVariable;


/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 07.05.2007
 */
public class NameSpaceLDAP extends NameSpace {

    private static final Logger LOG = CentralLogger.getInstance().getLogger(NameSpaceLDAP.class);

    private List<ControlSystemItem> _csiResult;

	/* (non-Javadoc)
	 * @see org.csstudio.utility.nameSpaceBrowser.utility.NameSpace#start()
	 */
	@Override
	public void start() {
		try{
            final NameSpaceSearchResult nameSpaceResultList = getNameSpaceResultList();
            if (nameSpaceResultList instanceof LdapSearchResult) {
                final ILdapService service = Activator.getDefault().getLdapService();
                if (service == null) {
                    LOG.error("LDAP service unavailable.");
                    return;
                }
                final NameParser parser = service.getLdapNameParser();

                final LdapName searchRoot = (LdapName) parser.parse(getName());

                ILdapSearchResult result;
                if(getSelection().endsWith("=*,")) {
                    result = service.retrieveSearchResultSynchronously(searchRoot,
                                                                       getFilter(),
                                                                       SearchControls.SUBTREE_SCOPE);
                } else {
                    result = service.retrieveSearchResultSynchronously(searchRoot,
                                                                       getFilter(),
                                                                       SearchControls.ONELEVEL_SCOPE);
                }
                if (result != null) {
                    updateResultList(getCSIResultList(result));
                }

            } else{
                // TODO: Was soll gemacht werden wenn das 'getNameSpaceResultList() instanceof LdapSearchResult' nicht stimmt.
                LOG.error("CSSView.exp.IAE.2"); //$NON-NLS-1$
            }
        } catch (final IllegalArgumentException e) {
            LOG.error("CSSView.exp.IAE.1", e);
        } catch (final NamingException ne) {
            LOG.error("Error while parsing search root " + getName() + " as LDAP name.", ne);
        }
	}


	@Nonnull
	private List<ControlSystemItem> getCSIResultList(@Nonnull final ILdapSearchResult result) {

	    if (_csiResult != null && !_csiResult.isEmpty()) {
	        return _csiResult;
	    }

	    final List<ControlSystemItem> tmpList = new ArrayList<ControlSystemItem>();
	    final Set<SearchResult> answerSet = result.getAnswerSet();
	    if(answerSet == null) {
	        return Collections.emptyList();
	    }

	    for (final SearchResult row : answerSet) { // TODO (hrickens) : encapsulate LDAP answer parsing !
	        String cleanList = row.getName();
	        // Delete "-Chars that add from LDAP-Reader when the result contains special character
	        if(cleanList.startsWith("\"")){ //$NON-NLS-1$
	            if(cleanList.endsWith("\"")) {
	                cleanList = cleanList.substring(1,cleanList.length()-1);
	            } else {
	                cleanList = cleanList.substring(1);
	            }
	        }
	        final String[] token = cleanList.split("[,=]"); //$NON-NLS-1$
	        if(token.length<2) {
	            if(!token[0].equals("no entry found")){
	                LOG.error("CSSViewError " + row + "'");//$NON-NLS-1$ //$NON-NLS-2$
	            }
	            break;

	        }

	        if (cleanList.startsWith(LdapEpicsControlsConfiguration.RECORD.getNodeTypeName())) {
	            tmpList.add(new ProcessVariable(token[1], cleanList));
	        } else {
	            tmpList.add(new ControlSystemItem(token[1], cleanList));
	        }

	    }
	    _csiResult = tmpList;
	    return _csiResult;
	}
}
