/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id$
 */
package org.csstudio.utility.ldap.model.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.csstudio.utility.ldap.service.ILdapContentModelBuilder;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.util.LdapNameUtils;
import org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.INodeComponent;
import org.csstudio.utility.treemodel.ISubtreeNodeComponent;
import org.csstudio.utility.treemodel.ITreeNodeConfiguration;
import org.csstudio.utility.treemodel.TreeNodeComponent;
import org.csstudio.utility.treemodel.builder.AbstractContentModelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builds a content model from LDAP.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 21.05.2010
 * @param <T> the object class type for which a tree shall be created
 */
public final class LdapContentModelBuilder<T extends Enum<T> & ITreeNodeConfiguration<T>> extends AbstractContentModelBuilder<T>
        implements ILdapContentModelBuilder<T> {

    private static final Logger LOG = LoggerFactory.getLogger(LdapContentModelBuilder.class);

    private ILdapSearchResult _searchResult;
    private final T _objectClassRoot;
    private final NameParser _parser;

    /**
     * Constructor.
     * @param searchResult the search result to build the model from
     * @param objectClassRoot the model type
     * @param nameParser
     */
    public LdapContentModelBuilder(final T objectClassRoot,
                                   final ILdapSearchResult searchResult,
                                   final NameParser parser) {
        _searchResult = searchResult;
        _objectClassRoot = objectClassRoot;
        _parser = parser;
    }

    /**
     * Constructor for builder that enriches an already existing model.
     * @param model the already filled model
     * @param nameParser
     */
    public LdapContentModelBuilder(final ContentModel<T> model,
                                   final NameParser parser) {
        _objectClassRoot = model.getVirtualRoot().getType();
        setModel(model);
        _parser = parser;
    }

    @Override
    public void setSearchResult(final ILdapSearchResult result) {
        _searchResult = result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ContentModel<T> createContentModel() throws CreateContentModelException {

        // Generate new model only if there isn't any model set
        final ContentModel<T> model = getModel();

            return addSearchResult(model == null ? new ContentModel<T>(_objectClassRoot)
                                                 : model,
                                   _searchResult,
                                   _parser);
    }

    /**
     * Adds a given search result to the current LDAP content model.
     *
     * @param searchResult the search result .
     * @param parser
     * @return the enriched model
     */
    private ContentModel<T> addSearchResult(final ContentModel<T> model,
                                            final ILdapSearchResult searchResult,
                                            final NameParser parser) {

        if (searchResult != null) {
            final ISubtreeNodeComponent<T> root = model.getVirtualRoot();

            try {
                // sort the answer set for the length of rdns so the shortest come first
                // this way the nodes higher up in the tree will be created first
                // this is important when giving them their attributes
                List<SearchResult> answerList = new ArrayList<SearchResult>(searchResult.getAnswerSet());
                Comparator<? super SearchResult> comparator = createSearchResultComparator(parser);
                Collections.sort(answerList, comparator);

                for (final SearchResult row : answerList) {
                    final Attributes attributes = row.getAttributes();
                    final LdapName parsedName = (LdapName) parser.parse(row.getNameInNamespace());
                    final LdapName nameWithoutRoot = LdapNameUtils.removeRdns(parsedName,
                                                                              LdapFieldsAndAttributes.LDAP_ROOT.getRdns());
                    createLdapComponent(model,
                                        attributes == null ? new BasicAttributes() : attributes,
                                        nameWithoutRoot,
                                        root);
                }
            } catch (final IndexOutOfBoundsException iooe) {
                LOG.error("Tried to remove a name component with index out of bounds.", iooe);
            } catch (final InvalidNameException ie) {
                LOG.error("Search result row could not be parsed by NameParser or removal of name component violates the syntax rules.", ie);
            } catch (final NamingException e) {
                LOG.error("NameParser could not be obtained for LDAP Engine and CompositeName.", e);
            }
        }

        return model;
    }

    private Comparator<SearchResult> createSearchResultComparator(final NameParser parser) {
        return new Comparator<SearchResult>() {
            @SuppressWarnings("synthetic-access")
            @Override
            public int compare(final SearchResult sr1, final SearchResult sr2) {
                int result = 0;
                try {
                    final LdapName parsedName1 = (LdapName) parser.parse(sr1.getNameInNamespace());
                    final LdapName parsedName2 = (LdapName) parser.parse(sr2.getNameInNamespace());
                    result = parsedName1.getRdns().size() - parsedName2.getRdns().size();
                } catch (NamingException e) {
                    LOG.error("Cannot parse name in ldap search result comparator", e);
                    // Tunneling of root cause
                    throw new RuntimeException("Cannot parse name", e);
                }
                return result;
            }
        };
    }

    private void createLdapComponent(final ContentModel<T> model,
                                     final Attributes attributes,
                                     final LdapName fullName,
                                     final ISubtreeNodeComponent<T> root) throws InvalidNameException {
        ISubtreeNodeComponent<T> parent = root;

        final LdapName currentPartialName = new LdapName("");


        for (int i = 0; i < fullName.size(); i++) {

            final Rdn rdn = fullName.getRdn(i);
            currentPartialName.add(rdn);

            // Check whether this component exists already
            final INodeComponent<T> childByLdapName = model.getChildByLdapName(currentPartialName.toString());
            if (childByLdapName != null) {
                if (i < fullName.size() - 1) { // another name component follows => has children
                    parent = (ISubtreeNodeComponent<T>) childByLdapName;
                }
                continue; // YES
            }
            // NO

            final T oc = _objectClassRoot.getNodeTypeByNodeTypeName(rdn.getType());

            final String simpleName = (String) rdn.getValue();
            final ISubtreeNodeComponent<T> newChild =
                new TreeNodeComponent<T>(simpleName,
                                        oc,
                                        parent,
                                        attributes,
                                        currentPartialName);
            model.addChild(parent, newChild);

            parent = newChild;
        }
    }

}
