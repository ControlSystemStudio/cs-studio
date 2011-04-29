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
package org.csstudio.utility.ldap.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.naming.directory.SearchResult;

import org.csstudio.utility.ldap.service.ILdapSearchParams;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.namespace.utility.ControlSystemItem;
import org.csstudio.utility.namespace.utility.NameSpaceSearchResult;

/**
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 08.04.2010
 */
public class LdapSearchResult extends NameSpaceSearchResult implements ILdapSearchResult {

    private Set<SearchResult> _answerSet = Collections.emptySet();

    private ILdapSearchParams _searchParams;

    private List<ControlSystemItem> _csiResult;

    /**
     * Constructor.
     */
    public LdapSearchResult() {
        // Empty
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public List<ControlSystemItem> getCSIResultList() {
        // FIXME (bknerr) : obsolete probably
        //return Collections.emptyList();
        return _csiResult;
    }


    @Override
    @Nonnull
    public NameSpaceSearchResult getNew() {
        return new LdapSearchResult();
    }

    @Override
    public void notifyView() {
        setChanged();
        notifyObservers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResult(@Nonnull final ILdapSearchParams searchParams,
                          @Nonnull final Set<SearchResult> answerSet) {
        _searchParams = searchParams;
        _answerSet = answerSet;

        // FIXME (bknerr) : probably not necessary anymore
        notifyView();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Set<SearchResult> getAnswerSet() {
        return _answerSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public ILdapSearchParams getSearchParams() {
        return _searchParams;
    }

    /**
     * Copies the list of {@link ControlSystemItem}.
     * {@inheritDoc}
     */
    @Override
    public final void setCSIResultList(@Nonnull final List<ControlSystemItem> resultList) {
        _csiResult = new ArrayList<ControlSystemItem>(resultList);
    }
}
