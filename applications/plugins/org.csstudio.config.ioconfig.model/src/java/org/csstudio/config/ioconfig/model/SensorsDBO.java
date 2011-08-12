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
package org.csstudio.config.ioconfig.model;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.1 $
 * @since 02.07.2009
 */
@Entity
@Table(name = "ddb_Sensors")
public class SensorsDBO {

    /**
     * Key ID.
     */
    private int _id;
    private String _ioName;
    private String _selection;
    private String _sensorID;

    @Id
    @GeneratedValue
    public int getId() {
        return _id;
    }

    public void setId(final int id) {
        this._id = id;
    }

    @Nonnull 
    public String getIoName() {
        return _ioName;
    }

    /**
     *
     * @param ioName
     *            the IO Name of this Channel.
     */
    public void setIoName(@Nonnull final String ioName) {
        this._ioName = ioName;
    }

    @Column(length=32)
    @Nonnull 
    public String getSelection() {
        return _selection;
    }

    public void setSelection(@Nonnull final String selection) {
        _selection = selection;
    }

    @Column(length=32)
    @Nonnull 
    public String getSensorID() {
        return _sensorID;
    }

    public void setSensorID(@Nonnull final String sensorID) {
        _sensorID = sensorID;
    }

    @Override
    @Nonnull 
    public String toString() {
        return String.format("%1$3s : %2$3s", getSelection(),getSensorID());
    }


}
