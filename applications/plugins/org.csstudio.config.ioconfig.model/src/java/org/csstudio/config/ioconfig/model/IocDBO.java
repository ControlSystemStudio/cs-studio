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
 * $Id: Ioc.java,v 1.2 2010/08/20 13:33:05 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model;

import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 12.03.2008
 */
@Entity
@Table(name = "ddb_Ioc")
public class IocDBO extends AbstractNodeDBO {

    /**
     * Default Constructor needed by Hibernate.
     */
    public IocDBO() {
        // EMPTY
    }

    /**
     * Create a new Ioc with parent Facility.
     * @param facility the parent Facility.
     * @throws PersistenceException 
     */
    public IocDBO(@Nonnull final FacilityDBO facility) throws PersistenceException {
        this(facility, DEFAULT_MAX_STATION_ADDRESS);

    }

    /**
     * Create a new Ioc with parent Facility.
     * @param facility the parent Facility.
     * @param maxStationAddress the highest possible Station Address.
     * @throws PersistenceException 
     */
    private IocDBO(@Nonnull final FacilityDBO facility, final int maxStationAddress) throws PersistenceException {
        setParent(facility);
        facility.addChild(this);
    }

    /**
     *
     * @return the parent Facility of this IOC.
     */
    @Transient
    @Nonnull
    public FacilityDBO getFacility() {
        return (FacilityDBO) getParent();
    }

    /**
     * @return the ProfibusSubnets children.
     */
    @Transient
    @SuppressWarnings("unchecked")
    @Nonnull
    public Set<ProfibusSubnetDBO> getProfibusSubnets() {
        return (Set<ProfibusSubnetDBO>) getChildren();
    }

    /**
     *
     * @param facility
     *            set the parent Facility of this IOC.
     */
    public void setFacility(@Nonnull final FacilityDBO facility) {
        this.setParent(facility);
    }

    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    @CheckForNull
    public IocDBO copyParameter(@Nonnull final NamedDBClass parentNode) throws PersistenceException {
        if (parentNode instanceof FacilityDBO) {
            final FacilityDBO facility = (FacilityDBO) parentNode;
            final IocDBO copy = new IocDBO(facility);
            copy.setDescription(getDescription());
            return copy;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    @Nonnull
    public IocDBO copyThisTo(@Nonnull final AbstractNodeDBO parentNode) throws PersistenceException {
        final IocDBO copy = super.copyThisTo(parentNode);
        for (final AbstractNodeDBO node : getChildren()) {
            AbstractNodeDBO childrenCopy = node.copyThisTo(copy);
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
        return NodeType.IOC;
    }

}
