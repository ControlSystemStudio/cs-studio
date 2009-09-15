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
package org.csstudio.config.ioconfig.model.pbmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.Node;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdMasterModel;

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

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
public class Master extends Node {

    /**
     * The highest accept station address.
     */
    @Transient
    static final public int MAX_STATION_ADDRESS = 128;

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
    private GSDFile _gsdFile;

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
     * This Constructor is only used by Hibernate. To create an new {@link Master}
     * {@link #Master(ProfibusSubnet)}
     */
    public Master() {
    }

    /**
     * The default Constructor.
     */
    public Master(ProfibusSubnet profibusSubnet) {
        this(profibusSubnet, DEFAULT_MAX_STATION_ADDRESS);
    }

    public Master(ProfibusSubnet profibusSubnet, int maxStationAddress) {
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

    public short getFdlAddress() {
        return _fdlAddress;
    }

    public void setFdlAddress(final short fdlAddress) {
        this._fdlAddress = fdlAddress;
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
    public ProfibusSubnet getProfibusSubnet() {
        return (ProfibusSubnet) getParent();
    }

    public void setProfibusSubnet(final ProfibusSubnet profibusSubnet) {
        this.setParent(profibusSubnet);
    }

    @SuppressWarnings("unchecked")
    @Transient
    public Set<Slave> getSlaves() {
        return (Set<Slave>) getChildren();
    }

    /**
     * @return the GSDFile.
     */
    @ManyToOne
    public GSDFile getGSDFile() {
        return _gsdFile;
    }

    /**
     * @param gsdFile
     *            set the GSDFile.
     */
    public void setGSDFile(final GSDFile gsdFile) {
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
    public SortedSet<Short> getFreeStationAddress(){
//        ArrayList<Short> freeAddressList = new ArrayList<Short>();
        TreeSet<Short> freeAddressList = new TreeSet<Short>();
        for (short i = 0; i < DEFAULT_MAX_STATION_ADDRESS; i++) {
            freeAddressList.add(i);
        }
        freeAddressList.remove(getSortIndex());
        Set<Short> keySet = getChildrenAsMap().keySet();
        freeAddressList.removeAll(keySet);
        return freeAddressList;
    }
    
    @Transient
    public short getfirstFreeStationAddress(int maxStationAddress) {
        return getFreeStationAddress().first();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node copyParameter(NamedDBClass parentNode) {
        if (parentNode instanceof ProfibusSubnet) {
            ProfibusSubnet subnet = (ProfibusSubnet) parentNode;

            Master copy = new Master(subnet);
            copy.setDocuments(getDocuments());
            copy.setAutoclear(isAutoclear());
            copy.setDataControlTime(getDataControlTime());
            copy.setFdlAddress(getFdlAddress());
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
    public Node copyThisTo(Node parentNode) {
        Node copy = super.copyThisTo(parentNode);
        for (Node node : getChildren()) {
            node.copyThisTo(copy);
        }
        return copy;    }

    public void setMaxNrSlave(int maxNrSlave) {
        _maxNrSlave = maxNrSlave;
    }

    public int getMaxNrSlave() {
        return _maxNrSlave;
    }
    
    public void setMaxSlaveOutputLen(int maxSlaveOutputLen) {
        _maxSlaveOutputLen = maxSlaveOutputLen;
    }
    
    public int getMaxSlaveOutputLen() {
        return _maxSlaveOutputLen;
    }

    public void setMaxSlaveInputLen(int maxSlaveInputLen) {
        _maxSlaveInputLen = maxSlaveInputLen;
    }
    
    public int getMaxSlaveInputLen() {
        return _maxSlaveInputLen;
    }

    public void setMaxSlaveDiagEntries(int maxSlaveDiagEntries) {
        _maxSlaveDiagEntries = maxSlaveDiagEntries;
    }
    
    public int getMaxSlaveDiagEntries() {
        return _maxSlaveDiagEntries;
    }

    public void setMaxSlaveDiagLen(int maxSlaveDiagLen) {
        _maxSlaveDiagLen = maxSlaveDiagLen;
        
    }
    
    public int getMaxSlaveDiagLen() {
        return _maxSlaveDiagLen;
    }
    
    public void setMaxBusParaLen(int maxBusParaLen) {
        _maxBusParaLen = maxBusParaLen;
    }
    
    public int getMaxBusParaLen() {
        return _maxBusParaLen;
    }

    public void setMaxSlaveParaLen(int maxSlaveParaLen) {
        _maxSlaveParaLen = maxSlaveParaLen;
    }

    public int getMaxSlaveParaLen() {
        return _maxSlaveParaLen;
    }

}
