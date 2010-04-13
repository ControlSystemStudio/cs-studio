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
package org.csstudio.utility.ldap.reader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.naming.directory.SearchResult;

import org.csstudio.utility.ldap.Activator;
import org.csstudio.utility.ldap.LdapUtils;
import org.csstudio.utility.ldap.Messages;
import org.csstudio.utility.namespace.utility.ControlSystemItem;
import org.csstudio.utility.namespace.utility.NameSpaceSearchResult;
import org.csstudio.utility.namespace.utility.ProcessVariable;

/**
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 08.04.2010
 */
public class LdapSearchResult extends NameSpaceSearchResult {

    private Set<SearchResult> _answerSet = Collections.emptySet();

    private String _searchroot;

    private String _filter;

    private List<ControlSystemItem> _csiResult;

    /**
     * Constructor.
     */
    public LdapSearchResult() {
        // Empty
    }

    /* (non-Javadoc)
     * @see org.csstudio.utility.nameSpaceBrowser.utility.NameSpaceResultList#getResultList()
     */
    @Override
    public List<ControlSystemItem> getCSIResultList() {
        if ((_csiResult != null) && !_csiResult.isEmpty()) {
            return _csiResult;
        }

        final List<ControlSystemItem> tmpList = new ArrayList<ControlSystemItem>();
        if(_answerSet == null) {
            return null;
        }

        for (final SearchResult row : _answerSet) { // TODO (hrickens) : encapsulate LDAP answer parsing !
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
                    Activator.logError(Messages.getString("CSSView.Error1")+row+"'");//$NON-NLS-1$ //$NON-NLS-2$
                }
                break;

            }

            if (cleanList.startsWith(LdapUtils.EREN_FIELD_NAME)) {
                tmpList.add(new ProcessVariable(token[1], cleanList));
            } else {
                tmpList.add(new ControlSystemItem(token[1], cleanList));
            }

        }
        return tmpList;
    }

    /* (non-Javadoc)
     * @see org.csstudio.utility.nameSpaceBrowser.utility.NameSpaceResultList#getNew()
     */
    @Override
    public NameSpaceSearchResult getNew() {
        return new LdapSearchResult();
    }

    @Override
    public void notifyView() {
        setChanged();
        notifyObservers();
    }

    /**
     * Sets the current result contents for these parameters
     * @param root the search root
     * @param filter the search filter
     * @param answerSet the corresponding result set
     */
    public void setResult(@Nonnull final String root,
                          @Nonnull final String filter,
                          @Nonnull final Set<SearchResult> answerSet) {
        _searchroot = root;
        _filter = filter;
        _answerSet = answerSet;

        // FIXME (bknerr) : probably not necessary anymore
        notifyView();
    }

    /**
     * Getter.
     * @return the result set
     */
    @Nonnull
    public Set<SearchResult> getAnswerSet() {
        return _answerSet;
    }

    /**
     * Search root of the current result
     * @return the current search root, may be null
     */
    @CheckForNull
    public String getSearchRoot() {
        return _searchroot;
    }

    /**
     * Filter of the current result.
     * @return the filter
     */
    @CheckForNull
    public String getFilter() {
        return _filter;
    }

    /**
     * Copies the list of {@link ControlSystemItem}.
     * {@inheritDoc}
     */
    @Override
    public final void setCSIResultList(@Nonnull final List<ControlSystemItem> resultList) {
        // FIXME (bknerr) : Deprecated structure for css view
        _csiResult = new ArrayList<ControlSystemItem>(resultList);
        notifyView();
    }
}
