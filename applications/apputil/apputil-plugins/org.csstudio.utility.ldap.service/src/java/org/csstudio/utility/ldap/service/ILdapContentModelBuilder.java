package org.csstudio.utility.ldap.service;


import org.csstudio.utility.treemodel.ITreeNodeConfiguration;
import org.csstudio.utility.treemodel.builder.IContentModelBuilder;

/**
 * The LDAP tree content model builder interface.
 *
 * @author bknerr
 * @author $Author: bknerr $
 * @version $Revision: 1.7 $
 * @since 07.09.2010
 * @param <T> the tree configuration type for this content model builder
 */
public interface ILdapContentModelBuilder<T extends Enum<T> & ITreeNodeConfiguration<T>>
    extends IContentModelBuilder<T> {

    /**
     * Sets the current search result for which a new model should be build or
     * an existing model should be enriched with.
     * @param result the LDAP search result
     */
    void setSearchResult(final ILdapSearchResult result);
}
