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
 * $Id$
 */
package org.csstudio.config.ioconfig.model;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnet;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 12.03.2008
 */
@Entity
@Table(name = "ddb_Ioc")
public class Ioc extends Node {

    /**
     * Default Constructor needed by Hibernate.
     */
    public Ioc() {
    }
    
    /**
     * Create a new Ioc with parent Facility.
     * @param facility the parent Facility.
     */
    public Ioc(Facility facility) {
        this(facility, DEFAULT_MAX_STATION_ADDRESS);

    }

    /**
     * Create a new Ioc with parent Facility.
     * @param facility the parent Facility.
     * @param maxStationAddress the highest possible Station Address.  
     */
    public Ioc(Facility facility, int maxStationAddress) {
        setParent(facility);
        facility.addChild(this);
    }

    /**
     * 
     * @return the parent Facility of this IOC.
     */
    @Transient
    public Facility getFacility() {
        return (Facility) getParent();
    }

    /**
     * @return the ProfibusSubnets children.  
     */
    @Transient
    @SuppressWarnings("unchecked")
    public Set<ProfibusSubnet> getProfibusSubnets() {
        return (Set<ProfibusSubnet>) getChildren();
    }

    /**
     * 
     * @param facility
     *            set the parent Facility of this IOC.
     */
    public void setFacility(final Facility facility) {
        this.setParent(facility);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node copyParameter(NamedDBClass parentNode) {
        if (parentNode instanceof Facility) {
            Facility facility = (Facility) parentNode;
            Ioc copy = new Ioc(facility);
            copy.setDescription(getDescription());
            copy.setDocuments(getDocuments());
            return copy;
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Node copyThisTo(Node parentNode) {
        Node copy = super.copyThisTo(parentNode);
        for (Node node : getChildren()) {
            node.copyThisTo(copy);
        }
        return copy;
    }
    
}
