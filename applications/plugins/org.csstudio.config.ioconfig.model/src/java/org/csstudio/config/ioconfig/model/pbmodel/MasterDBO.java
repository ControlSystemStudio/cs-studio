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
 * $Id: Master.java,v 1.8 2010/08/20 13:33:08 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.AbstractNodeSharedImpl;
import org.csstudio.config.ioconfig.model.GSDFileTypes;
import org.csstudio.config.ioconfig.model.INodeVisitor;
import org.csstudio.config.ioconfig.model.NodeType;
import org.csstudio.config.ioconfig.model.PersistenceException;

/*******************************************************************************
 * Data model for Profibus-DP Master<br>
 * <br>
 * Created by: Torsten Boeckmann<br> *
 * Date: 07. Dezember 2005 * ********************************************************** last
 * changed: ................... * Grounds of changed: ................... * ................... *
 * Revision: * Status: untested * Precondition: * Postcondition: * *
 ******************************************************************************/

@Entity
@Table(name = "ddb_Profibus_Master")
public class MasterDBO extends AbstractNodeSharedImpl<ProfibusSubnetDBO, SlaveDBO> {

    private static final long serialVersionUID = 1L;

    /**
     * The highest accept station address.
     */
    @Transient
    private static final int MAX_STATION_ADDRESS = 126;

    // ********************
    // * Database Fields. *
    // ********************
    /**
     * Profibus-DP Master ident.
     */
    private long _profibusdpmasterId;
    /**
     * Profibus-DP Master Bezeichner.
     */
    private String _profibusdpmasterBez;
    /**
     * Vendor name of master.
     */
    private String _vendorName;
    /**
     * Model name of master.
     */
    private String _modelName;
    /**
     * Station address.
     */
    private short _fdlAddress;
    /**
     * Profibus ident given by Profibus Nutzer Organisation.
     */
    private int _profibusPnoId;
    /**
     * Min. slave interval.
     */
    private int _minSlaveInt;
    /**
     * Poll time.
     */
    private int _pollTime;
    /**
     * Data control time.
     */
    private int _dataControlTime;
    /**
     * Autoclear at detect error.
     */
    private boolean _autoclear;
    /**
     * Master user data.
     */
    private String _masterUserData;

    /**
     * The GSD File.
     */
    private GSDFileDBO _gsdFile;

    // *******************
    // * Transient data. *
    // *******************
    private int _maxNrSlave;

    private int _maxSlaveOutputLen;

    private int _maxSlaveInputLen;

    private int _maxSlaveDiagEntries;

    private int _maxBusParaLen;

    private int _maxSlaveParaLen;

    private int _maxSlaveDiagLen;

    /**
     * This Constructor is only used by Hibernate. To create an new {@link MasterDBO}
     * {@link #Master(ProfibusSubnetDBO)}
     */
    public MasterDBO() {
        // Constructor only for Hibernate
    }

    //  Data Base (Hybernate) Getter&Setter.

    /**
     * The default Constructor.
     * @throws PersistenceException
     */
    public MasterDBO(@Nonnull final ProfibusSubnetDBO profibusSubnet) throws PersistenceException {
        super(profibusSubnet);
    }

    @Override
    @Nonnull
    @Transient
    public ProfibusSubnetDBO getParent() {
        return super.getParent();
    }

    // CHECKSTYLE OFF: StrictDuplicateCode
    @Override
    public void accept(@Nonnull final INodeVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     * @throws PersistenceException
     */
    @Override
    @Nonnull
    public MasterDBO copyParameter(@Nonnull final ProfibusSubnetDBO parentNode) throws PersistenceException {
        final ProfibusSubnetDBO subnet = parentNode;

        final MasterDBO copy = new MasterDBO(subnet);
        copy.setAutoclear(isAutoclear());
        copy.setDataControlTime(getDataControlTime());
        copy.setRedundant(getRedundant());
        copy.setGSDFile(getGSDFile());
        copy.setMasterUserData(getMasterUserData());
        copy.setMinSlaveInt(getMinSlaveInt());
        copy.setModelName(getModelName());
        copy.setPollTime(getPollTime());
        copy.setProfibusdpmasterBez(getProfibusdpmasterBez());
        copy.setProfibusDPMasterId(getProfibusDPMasterId());
        copy.setProfibusPnoId(getProfibusPnoId());
        copy.setVendorName(getVendorName());
        return copy;
    }

    @Override
    @Nonnull
    public MasterDBO copyThisTo(@Nonnull final ProfibusSubnetDBO parentNode, @CheckForNull final String namePrefix) throws PersistenceException {
        final MasterDBO copy = (MasterDBO) super.copyThisTo(parentNode, namePrefix);
        for (final SlaveDBO node : getChildren()) {
            final AbstractNodeSharedImpl<MasterDBO, ModuleDBO> childrenCopy = node.copyThisTo(copy, "Copy of");
            childrenCopy.setSortIndexNonHibernate(node.getSortIndex());
        }
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public SlaveDBO createChild() throws PersistenceException {
        return new SlaveDBO(this);
    }

    @Override
    public boolean equals(@CheckForNull final Object obj) {
        return super.equals(obj);
    }

    public int getDataControlTime() {
        return _dataControlTime;
    }

    // ******************
    // * Helper Methods *
    // ******************
    /**
     *
     * @return
     */
    @Transient
    @CheckForNull
    public String getEpicsAdressString() {
        return getProfibusSubnet().getEpicsAddressString();
    }

    @Override
    @Transient
    public int getfirstFreeStationAddress() throws PersistenceException {
        return getFreeStationAddress().first();
    }

    @Transient
    @CheckForNull
    public SortedSet<Short> getFreeMStationAddress(final boolean redunant) {
        final TreeSet<Short> freeAddressList = new TreeSet<Short>();
        for (short i = 0; i < getMaxStationAddress(); i++) {
            freeAddressList.add(i);
        }
        final Set<Short> keySet = getChildrenAsMap().keySet();
        if(redunant) {
            for (final Short key : keySet) {
                freeAddressList.remove((short)(key-1));
                freeAddressList.remove(key);
            }
        }else {
            freeAddressList.removeAll(keySet);
        }
        return freeAddressList;
    }

    @Transient
    @Nonnull
    public SortedSet<Short> getFreeStationAddress() {
        final TreeSet<Short> freeAddressList = new TreeSet<Short>();
        for (short i = 0; i < getMaxStationAddress(); i++) {
            freeAddressList.add(i);
        }
        freeAddressList.remove(getSortIndex());
        freeAddressList.remove(getRedundant());

        final Set<Short> keySet = getChildrenAsMap().keySet();
        freeAddressList.removeAll(keySet);

        return freeAddressList;
    }

    /**
     * @return the GSDFile.
     */
    @ManyToOne
    @CheckForNull
    public GSDFileDBO getGSDFile() {
        return _gsdFile;
    }

    @CheckForNull
    public String getMasterUserData() {
        return _masterUserData;
    }

    public int getMaxBusParaLen() {
        return _maxBusParaLen;
    }

    public int getMaxNrSlave() {
        return _maxNrSlave;
    }

    public int getMaxSlaveDiagEntries() {
        return _maxSlaveDiagEntries;
    }

    public int getMaxSlaveDiagLen() {
        return _maxSlaveDiagLen;
    }

    public int getMaxSlaveInputLen() {
        return _maxSlaveInputLen;
    }

    public int getMaxSlaveOutputLen() {
        return _maxSlaveOutputLen;
    }

    public int getMaxSlaveParaLen() {
        return _maxSlaveParaLen;
    }

    public int getMinSlaveInt() {
        return _minSlaveInt;
    }

    @CheckForNull
    public String getModelName() {
        return _modelName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    @Nonnull
    public NodeType getNodeType() {
        return NodeType.MASTER;
    }

    public int getPollTime() {
        return _pollTime;
    }

    @CheckForNull
    public String getProfibusdpmasterBez() {
        return _profibusdpmasterBez;
    }

    public long getProfibusDPMasterId() {
        return _profibusdpmasterId;
    }

    public int getProfibusPnoId() {
        return _profibusPnoId;
    }

    @ManyToOne
    @Nonnull
    public ProfibusSubnetDBO getProfibusSubnet() {
        return getParent();
    }

    /**
     * Get the Redundant Station Address.
     * If the Station Address < 0 the ICO have no redundant.
     * @return the redundant Station Address.
     */
    @Column(name = "FDLADDRESS")
    public short getRedundant() {
        return _fdlAddress;
    }


    @CheckForNull
    public String getVendorName() {
        return _vendorName;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
    // CHECKSTYLE ON: StrictDuplicateCode

    public boolean isAutoclear() {
        return _autoclear;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public GSDFileTypes needGSDFile() {
        return GSDFileTypes.Master;
    }

    public void setAutoclear(final boolean autoclear) {
        this._autoclear = autoclear;
    }

    public void setDataControlTime(final int dataControlTime) {
        this._dataControlTime = dataControlTime;
    }

    /**
     * @param gsdFile
     *            set the GSDFile.
     */
    public void setGSDFile(@Nullable final GSDFileDBO gsdFile) {
        _gsdFile = gsdFile;
    }

    public void setMasterUserData(@Nullable final String masterUserData) {
        this._masterUserData = masterUserData;
    }

    public void setMaxBusParaLen(final int maxBusParaLen) {
        _maxBusParaLen = maxBusParaLen;
    }

    public void setMaxNrSlave(final int maxNrSlave) {
        _maxNrSlave = maxNrSlave;
    }

    public void setMaxSlaveDiagEntries(final int maxSlaveDiagEntries) {
        _maxSlaveDiagEntries = maxSlaveDiagEntries;
    }

    public void setMaxSlaveDiagLen(final int maxSlaveDiagLen) {
        _maxSlaveDiagLen = maxSlaveDiagLen;

    }

    public void setMaxSlaveInputLen(final int maxSlaveInputLen) {
        _maxSlaveInputLen = maxSlaveInputLen;
    }

    public void setMaxSlaveOutputLen(final int maxSlaveOutputLen) {
        _maxSlaveOutputLen = maxSlaveOutputLen;
    }

    public void setMaxSlaveParaLen(final int maxSlaveParaLen) {
        _maxSlaveParaLen = maxSlaveParaLen;
    }

    public void setMinSlaveInt(final int minSlaveInt) {
        this._minSlaveInt = minSlaveInt;
    }

    public void setModelName(@Nullable final String modelName) {
        this._modelName = modelName;
    }

    public void setPollTime(final int pollTime) {
        this._pollTime = pollTime;
    }

    public void setProfibusdpmasterBez(@Nullable final String profibusdpmasterBez) {
        this._profibusdpmasterBez = profibusdpmasterBez;
    }

    public void setProfibusDPMasterId(final long profibusDPMasterId) {
        this._profibusdpmasterId = profibusDPMasterId;
    }


    public void setProfibusPnoId(final int profibusPnoId) {
        this._profibusPnoId = profibusPnoId;
    }

    public void setProfibusSubnet(@Nonnull final ProfibusSubnetDBO profibusSubnet) {
        this.setParent(profibusSubnet);
    }

    /**
     * Set the redundant Address.
     * < 0 is not redundant.
     * >= is redundant.
     * @param fdlAddress
     */
    public void setRedundant(final int fdlAddress) {
        this._fdlAddress = (short)fdlAddress;
    }

    public void setVendorName(@Nullable final String vendorName) {
        this._vendorName = vendorName;
    }

    public static int getMaxStationAddress() {
        return MAX_STATION_ADDRESS;
    }
}
