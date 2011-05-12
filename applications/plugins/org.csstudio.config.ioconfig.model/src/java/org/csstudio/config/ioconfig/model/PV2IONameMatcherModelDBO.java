/*
		* Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
		* Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
		*
		* THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
		* WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT
		NOT LIMITED
		* TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE
		AND
		* NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
		BE LIABLE
		* FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
		CONTRACT,
		* TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
		SOFTWARE OR
		* THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE
		DEFECTIVE
		* IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING,
		REPAIR OR
		* CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART
		OF THIS LICENSE.
		* NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS
		DISCLAIMER.
		* DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
		ENHANCEMENTS,
		* OR MODIFICATIONS.
		* THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
		MODIFICATION,
		* USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
		DISTRIBUTION OF THIS
		* PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU
		MAY FIND A COPY
		* AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
		*/
package org.csstudio.config.ioconfig.model;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 27.07.2010
 */
@Entity
@Table(name = "dct_Records")
public class PV2IONameMatcherModelDBO {

    /**
    * Key ID.
    */
    private int _id;

    /**
     * IO Name.
     */
    private String _ioName;

    /**
     * EPICS Name.
     */
    private String _epicsName;

    /**
     * The Typew of the EPICS Record.
     */
    private String _reordType;

    /**
     *
     * @return the Node key ID.
     */
    @Id
    @GeneratedValue
    public int getId() {
        return _id;
    }

    /**
     *
     * @param id
     *            set the Node key ID.
     */
    public void setId(final int id) {
        _id = id;
    }

    /**
     * @param ioName
     *            the IO Name of this Channel.
     */
    public void setIoName(@Nonnull final String ioName) {
        _ioName = ioName;
    }

    /**
     *
     * @return the IO Name of this Channel.
     */
    @Column(name = "io_name", nullable=false)
    @Nonnull 
    public String getIoName() {
        return _ioName;
    }

    /**
     * @param epicsName the epicsName to set
     */
    public void setEpicsName(@Nonnull final String epicsName) {
        _epicsName = epicsName;
    }

    /**
     * @return the epicsName
     */
    @Nonnull 
    @Column(name = "epics_name", nullable=false)
    public String getEpicsName() {
        return _epicsName;
    }

    /**
     * @param reordType the reordType to set
     */
    public void setReordType(@Nonnull final String reordType) {
        _reordType = reordType;
    }

    /**
     * @return the reordType
     */
    @Column(name = "record_type", nullable=false)
    @Nonnull 
    public String getReordType() {
        return _reordType;
    }
}
