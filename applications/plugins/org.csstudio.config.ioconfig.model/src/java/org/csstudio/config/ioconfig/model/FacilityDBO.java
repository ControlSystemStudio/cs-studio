/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
 * $Id: Facility.java,v 1.3 2010/08/20 13:33:05 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.3 $
 * @since 12.03.2008
 */
@Entity
@Table(name = "ddb_Facility")
public class FacilityDBO extends AbstractNodeDBO {

    /**
     * Default Constructor needed by Hibernate.
     */
    public FacilityDBO() {
        // Empty.
    }

    /**
     *
     * @return the children IOC's.
     */
    @Transient
    @SuppressWarnings("unchecked")
    public Set<IocDBO> getIoc() {
        return (Set<IocDBO>) getChildren();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractNodeDBO copyParameter(final NamedDBClass parent) {
        final FacilityDBO copy = new FacilityDBO();
        copy.setDescription(getDescription());
        copy.setDocuments(new HashSet<DocumentDBO>(getDocuments()));
        return copy;
    }

    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    public AbstractNodeDBO copyThisTo(final AbstractNodeDBO parentNode) throws PersistenceException {
        final AbstractNodeDBO copy = super.copyThisTo(parentNode);
        for (final AbstractNodeDBO node : getChildren()) {
            AbstractNodeDBO childrenCopy = node.copyThisTo(copy);
            childrenCopy.setSortIndexNonHibernate(node.getSortIndex());
        }
        return copy;
    }

    /**
     * Do nothing!
     *
     * @param parent
     *            facility can't have a parent!
     */
    @Override
    public void setParent(final AbstractNodeDBO parent) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public NodeType getNodeType() {
        return NodeType.FACILITY;
    }

}
