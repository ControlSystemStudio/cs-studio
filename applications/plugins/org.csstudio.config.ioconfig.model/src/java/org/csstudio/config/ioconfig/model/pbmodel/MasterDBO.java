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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.GSDFileTypes;
import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.NodeType;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdMasterModel;

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
public class MasterDBO extends AbstractNodeDBO {

    /**
     * The highest accept station address.
     */
    @Transient
    public static final int MAX_STATION_ADDRESS = 126;

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
    /**
     * The master GSD File Keywords for this Master from selected GSD file.
     */
    private GsdMasterModel _gsdMasterModel;

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

    /**
     * The default Constructor.
     * @throws PersistenceException 
     */
    public MasterDBO(final ProfibusSubnetDBO profibusSubnet) throws PersistenceException {
        this(profibusSubnet, DEFAULT_MAX_STATION_ADDRESS);
    }

    private MasterDBO(final ProfibusSubnetDBO profibusSubnet, final int maxStationAddress) throws PersistenceException {
        setParent(profibusSubnet);
        profibusSubnet.addChild(this);
    }

    // ****************************************
    // * Data Base (Hybernate) Getter&Setter. *
    // ****************************************

    public boolean isAutoclear() {
        return _autoclear;
    }

    public void setAutoclear(final boolean autoclear) {
        this._autoclear = autoclear;
    }

    public int getDataControlTime() {
        return _dataControlTime;
    }

    public void setDataControlTime(final int dataControlTime) {
        this._dataControlTime = dataControlTime;
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

    /**
     * Set the redundant Address.
     * < 0 is not redundant.
     * >= is redundant.
     * @param fdlAddress
     */
    public void setRedundant(final int fdlAddress) {
        this._fdlAddress = (short)fdlAddress;
    }

    public String getMasterUserData() {
        return _masterUserData;
    }

    public void setMasterUserData(final String masterUserData) {
        this._masterUserData = masterUserData;
    }

    public int getMinSlaveInt() {
        return _minSlaveInt;
    }

    public void setMinSlaveInt(final int minSlaveInt) {
        this._minSlaveInt = minSlaveInt;
    }

    public String getModelName() {
        return _modelName;
    }

    public void setModelName(final String modelName) {
        this._modelName = modelName;
    }

    public int getPollTime() {
        return _pollTime;
    }

    public void setPollTime(final int pollTime) {
        this._pollTime = pollTime;
    }

    public int getProfibusPnoId() {
        return _profibusPnoId;
    }

    public void setProfibusPnoId(final int profibusPnoId) {
        this._profibusPnoId = profibusPnoId;
    }

    public String getProfibusdpmasterBez() {
        return _profibusdpmasterBez;
    }

    public void setProfibusdpmasterBez(final String profibusdpmasterBez) {
        this._profibusdpmasterBez = profibusdpmasterBez;
    }

    public long getProfibusDPMasterId() {
        return _profibusdpmasterId;
    }

    public void setProfibusDPMasterId(final long profibusDPMasterId) {
        this._profibusdpmasterId = profibusDPMasterId;
    }

    public String getVendorName() {
        return _vendorName;
    }

    public void setVendorName(final String vendorName) {
        this._vendorName = vendorName;
    }

    @ManyToOne
    public ProfibusSubnetDBO getProfibusSubnet() {
        return (ProfibusSubnetDBO) getParent();
    }

    public void setProfibusSubnet(final ProfibusSubnetDBO profibusSubnet) {
        this.setParent(profibusSubnet);
    }

    @SuppressWarnings("unchecked")
    @Transient
    public Set<SlaveDBO> getSlaves() {
        return (Set<SlaveDBO>) getChildren();
    }

    /**
     * @return the GSDFile.
     */
    @ManyToOne
    public GSDFileDBO getGSDFile() {
        return _gsdFile;
    }

    /**
     * @param gsdFile
     *            set the GSDFile.
     */
    public void setGSDFile(final GSDFileDBO gsdFile) {
        _gsdFile = gsdFile;
    }


    // ******************
    // * Helper Methods *
    // ******************
    /**
     *
     * @return
     */
    @Transient
    public String getEpicsAdressString() {
        return getProfibusSubnet().getEpicsAddressString();
    }

    @Transient
    public GsdMasterModel getGSDMasterData() {
        return _gsdMasterModel;
    }

    /**
     * @param masterModel
     */
    @Transient
    public void setGSDMasterData(final GsdMasterModel masterModel) {
        _gsdMasterModel = masterModel;
    }

    @Transient
    public SortedSet<Short> getFreeStationAddress() throws PersistenceException{
        TreeSet<Short> freeAddressList = new TreeSet<Short>();
        for (short i = 0; i < MAX_STATION_ADDRESS; i++) {
            freeAddressList.add(i);
        }
        freeAddressList.remove(getSortIndex());
        freeAddressList.remove(getRedundant());

        Set<Short> keySet = getChildrenAsMap().keySet();
        freeAddressList.removeAll(keySet);

        return freeAddressList;
    }

    @Transient
    public SortedSet<Short> getFreeMStationAddress(final boolean redunant) throws PersistenceException{
        TreeSet<Short> freeAddressList = new TreeSet<Short>();
        for (short i = 0; i < MAX_STATION_ADDRESS; i++) {
            freeAddressList.add(i);
        }
        Set<Short> keySet = getChildrenAsMap().keySet();
        if(redunant) {
            for (Short key : keySet) {
                freeAddressList.remove((short)(key-1));
                freeAddressList.remove(key);
            }
        }else {
            freeAddressList.removeAll(keySet);
        }
        return freeAddressList;
    }


    @Override
    @Transient
    public short getfirstFreeStationAddress(final int maxStationAddress) throws PersistenceException {
        return getFreeStationAddress().first();
    }

    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    public AbstractNodeDBO copyParameter(final NamedDBClass parentNode) throws PersistenceException {
        if (parentNode instanceof ProfibusSubnetDBO) {
            ProfibusSubnetDBO subnet = (ProfibusSubnetDBO) parentNode;

            MasterDBO copy = new MasterDBO(subnet);
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
        return null;
    }

    @Override
    public AbstractNodeDBO copyThisTo(final AbstractNodeDBO parentNode) throws PersistenceException {
        AbstractNodeDBO copy = super.copyThisTo(parentNode);
        for (AbstractNodeDBO node : getChildren()) {
            AbstractNodeDBO childrenCopy = node.copyThisTo(copy);
            childrenCopy.setSortIndexNonHibernate(node.getSortIndex());
        }
        return copy;
    }

    public void setMaxNrSlave(final int maxNrSlave) {
        _maxNrSlave = maxNrSlave;
    }

    public int getMaxNrSlave() {
        return _maxNrSlave;
    }

    public void setMaxSlaveOutputLen(final int maxSlaveOutputLen) {
        _maxSlaveOutputLen = maxSlaveOutputLen;
    }

    public int getMaxSlaveOutputLen() {
        return _maxSlaveOutputLen;
    }

    public void setMaxSlaveInputLen(final int maxSlaveInputLen) {
        _maxSlaveInputLen = maxSlaveInputLen;
    }

    public int getMaxSlaveInputLen() {
        return _maxSlaveInputLen;
    }

    public void setMaxSlaveDiagEntries(final int maxSlaveDiagEntries) {
        _maxSlaveDiagEntries = maxSlaveDiagEntries;
    }

    public int getMaxSlaveDiagEntries() {
        return _maxSlaveDiagEntries;
    }

    public void setMaxSlaveDiagLen(final int maxSlaveDiagLen) {
        _maxSlaveDiagLen = maxSlaveDiagLen;

    }

    public int getMaxSlaveDiagLen() {
        return _maxSlaveDiagLen;
    }

    public void setMaxBusParaLen(final int maxBusParaLen) {
        _maxBusParaLen = maxBusParaLen;
    }

    public int getMaxBusParaLen() {
        return _maxBusParaLen;
    }

    public void setMaxSlaveParaLen(final int maxSlaveParaLen) {
        _maxSlaveParaLen = maxSlaveParaLen;
    }

    public int getMaxSlaveParaLen() {
        return _maxSlaveParaLen;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GSDFileTypes needGSDFile() {
        return GSDFileTypes.Master;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public NodeType getNodeType() {
        return NodeType.MASTER;
    }

}
