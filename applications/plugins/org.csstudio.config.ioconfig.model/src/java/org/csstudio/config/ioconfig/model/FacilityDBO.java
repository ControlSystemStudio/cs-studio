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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
public class FacilityDBO extends AbstractNodeSharedImpl<VirtualRoot, IocDBO> {

    private static final long serialVersionUID = 1L;

    /**
     * Default Constructor needed by Hibernate.
     */
    public FacilityDBO() {
        // Empty.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public FacilityDBO copyParameter(@Nullable final VirtualRoot parent) {
        final FacilityDBO copy = new FacilityDBO();
        copy.setDescription(getDescription());
        return copy;
    }

    @Transient
    @Override
    @CheckForNull
    public VirtualRoot getParent() {
        return super.getParent();
    }

    /**
     * {@inheritDoc}
     * @throws PersistenceException
     */
    @Override
    @Nonnull
    public FacilityDBO copyThisTo(@Nonnull final VirtualRoot parentNode, @CheckForNull final String namePrefix) throws PersistenceException {
        final FacilityDBO copy = (FacilityDBO) super.copyThisTo(parentNode, namePrefix);
        for (final IocDBO node : getChildren()) {
            final IocDBO childrenCopy = node.copyThisTo(copy, "Copy of ");
            childrenCopy.setSortIndexNonHibernate(node.getSortIndex());
        }
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    @Nonnull
    public NodeType getNodeType() {
        return NodeType.FACILITY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public IocDBO createChild() throws PersistenceException {
        return new IocDBO(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(@Nonnull final INodeVisitor visitor) {
        visitor.visit(this);
    }

}
