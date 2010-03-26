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

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 12.03.2008
 */
@Entity
@Table(name = "ddb_Facility")
public class Facility extends Node {

    private FacilityLight _facilityLight;

    /**
     * Default Constructor needed by Hibernate.
     */
    public Facility() {
        // Empty.
    }

    /**
     *
     * @return the children IOC's.
     */
    @Transient
    @SuppressWarnings("unchecked")
    public Set<Ioc> getIoc() {
        return (Set<Ioc>) getChildren();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node copyParameter(final NamedDBClass parent) {
        final Facility copy = new Facility();
        copy.setDescription(getDescription());
        copy.setDocuments(getDocuments());
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node copyThisTo(final Node parentNode) {
        final Node copy = super.copyThisTo(parentNode);
        for (final Node node : getChildren()) {
            node.copyThisTo(copy);
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
    public void setParent(final Node parent) {
        // do nothing
    }

    /**
     *
     * @param facilityLight set the equivalent {@link FacilityLight}.
     */
    @Transient
    public void setFacilityLigth(final FacilityLight facilityLight) {
        _facilityLight = facilityLight;

    }

    /**
     *
     * @return the equivalent {@link FacilityLight}.
     */
    @Transient
    public FacilityLight getFacilityLigth() {
        return _facilityLight;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(final String name) {
        super.setName(name);
        if (_facilityLight != null) {
            _facilityLight.setName(name);
        }
    }



    @Override
	public <T extends Node> Node addChild(final T child) {
    	final Node addChild = super.addChild(child);
        if (_facilityLight != null) {
            _facilityLight.setCount(getChildren().size());
        }
		return addChild;
	}



	@Override
    public void removeAllChild() {
        super.removeAllChild();
        if (_facilityLight != null) {
            _facilityLight.setCount(getChildren().size());
        }
    }

    @Override
    public void removeChild(final Node child) {
        super.removeChild(child);
        if (_facilityLight != null) {
            _facilityLight.setCount(getChildren().size());
        }

    }

	/**
     * {@inheritDoc}
     */
    @Override
    public void setSortIndex(final Short sortIndex) {
        super.setSortIndex(sortIndex);
        if (_facilityLight != null) {
            _facilityLight.setSortIndex(sortIndex);
        }
    }
}
