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
package org.csstudio.config.ioconfig.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * A light version of a Facility without the Children IOC. For a fast TreeView
 * display.
 * 
 * 
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 30.04.2009
 */
@Entity
@Table(name = "ddb_view_facility_light")
public class FacilityLight extends NamedDBClass {

    private int _count = 0;

    private Facility _facility = null;

    /**
     * The Constructor needed by Hibernate. To generate a new
     * {@link FacilityLight} use {@link FacilityLight#FacilityLight(Facility)}
     */
    public FacilityLight() {

    }

    /**
     * The default Constructor to generate a new {@link FacilityLight}.
     * 
     * @param newFacility
     *            the equvalent {@link Facility}.
     */
    public FacilityLight(Facility newFacility) {
        _facility = newFacility;
        setCount(newFacility.getChildren().size());
        setCreatedBy(newFacility.getCreatedBy());
        setCreatedOn(newFacility.getCreatedOn());
        setDirty(newFacility.isDirty());
        setId(newFacility.getId());
        setName(newFacility.getName());
        setSortIndex(newFacility.getSortIndex());
        setUpdatedBy(newFacility.getUpdatedBy());
        setUpdatedOn(newFacility.getUpdatedOn());
    }

    /**
     * 
     * @return the "full" Facility.
     */
    @Transient
    public Facility getFacility() {
        if (_facility == null) {
            Date date = new Date();
            System.out.println("Start um "+date);
            Facility facility = Repository.load(Facility.class, getId());
            _facility = facility;
            Date endDate = new Date();
            System.out.println("Start um "+endDate);
            System.out.println("differenc: "+(endDate.getTime()-date.getTime()));
            _facility.setFacilityLigth(this);
        }
        return _facility;
    }

    @Transient
    public void setFacility(Facility facility) {
        _facility = facility;
    }
    
    /**
     * Set the number of Children.
     * 
     * @param count
     *            number of children.
     */
    public void setCount(int count) {
        this._count = count;
    }

    /**
     * 
     * @return the number of children.
     */
    public int getCount() {
        return _count;
    }

    /**
     * 
     * @return only true when the Facility has one or more Children.
     */
    public boolean hasChildren() {
        return this._count > 0;
    }

    @Transient
    public boolean isLoaded() {
        return _facility != null;
    }

}
