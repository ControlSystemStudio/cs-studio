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
package org.csstudio.config.ioconfig.model.pbmodel;

import java.util.Arrays;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.Node;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GSD2Module;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdFactory;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdSlaveModel;
import org.csstudio.platform.logging.CentralLogger;

/**
 * @author gerke
 * @author $Author$
 * @version $Revision$
 * @since 26.03.2007
 */

@Entity
@Table(name = "ddb_Profibus_Slave")
public class Slave extends Node {
    /**
     * Vendor name of slave.
     */
    private String _vendorName;
    /**
     * Model name of slave.
     */
    private String _modelName;
    /**
     * Revision of GSD-file.
     */
    private String _revision;
    /**
     * Profibus ident given by Profibus Nutzerorganisation.
     */
    private int _profibusPNoID;

    /**
     * Station address.
     */
    private short _fdlAddress;

    /**
     * Slave flag.
     */
    private short _slaveFlag;
    /**
     * Slave Type.
     */
    private short _slaveType;
    /**
     * Station Status.
     */
    private short _stationStatus;
    /**
     * Watchdog factor 1.
     */
    private short _wdFact1;
    /**
     * Watchdog factor 2.
     */
    private short _wdFact2;
    /**
     * Min. station delay time.
     */
    private short _minTsdr;
    /**
     * Group ident.
     */
    private short _groupIdent;
    /**
     * Parameter user data.
     */
    private String _prmUserData;

    /**
     * The GSD file for this Slave.
     */
    private GSDFile _gsdFile;

    /**
     * Date from GSD File. The max size of module slots.
     */
    @Transient
    private short _maxSize = 1;

    /**
     * Date from GSD File. The user prm data.
     */
    @Transient
    private String[] _userPrmData;

    /**
     * Date from GSD File. The slot index number.
     */
    @Transient
    private String _iDNo;

    /**
     * The GSD Slave Model.
     */
    @Transient
    private GsdSlaveModel _gsdSlaveModel;

    /**
     * This Constructor is only used by Hibernate. To create an new {@link Slave}
     * {@link #Slave(Master)}
     */
    public Slave() {
    }

    public Slave(Master master) {
        this(master, (short) -1);
    }

    public Slave(Master master, short stationAddress) {
        setParent(master);
        master.addChild(this);
        moveSortIndex(stationAddress);
    }

    @ManyToOne
    public Master getProfibusDPMaster() {
        return (Master) getParent();
    }

    public void setProfibusDPMaster(final Master profibusDPMaster) {
        this.setParent(profibusDPMaster);
    }

    @Transient
    @SuppressWarnings("unchecked")
    public Set<Module> getModules() {
        return (Set<Module>) getChildren();
    }

    /** @return the GSDFile. */

    @ManyToOne(fetch = FetchType.EAGER)
    public GSDFile getGSDFile() {
        return _gsdFile;
    }

    /**
     * @param gsdFile
     *            set the GSDFile.
     */
    public void setGSDFile(final GSDFile gsdFile) {
        GSDFile oldGDS = _gsdFile;
        _gsdFile = gsdFile;
        if (!fill()) {
            _gsdFile = oldGDS;
        }
    }

    /**
     * 
     * @return The Vendor name of this slave.
     */
    public String getVendorName() {
        return _vendorName;
    }

    /**
     * 
     * @param vendorName
     *            Set the Vendor name of this slave.
     */
    public void setVendorName(final String vendorName) {
        _vendorName = vendorName;
    }

    /**
     * 
     * @return get the Model Name of Slave.
     */
    public String getModelName() {
        return _modelName;
    }

    /**
     * 
     * @param modelName
     *            Set the Model Name of Slave.
     */
    public void setModelName(final String modelName) {
        _modelName = modelName;
    }

    public String getRevision() {
        return _revision;
    }

    public void setRevision(final String revision) {
        _revision = revision;
    }

    public int getProfibusPNoID() {
        return _profibusPNoID;
    }

    public void setProfibusPNoID(final int profibusPNoID) {
        _profibusPNoID = profibusPNoID;
    }

    public short getFdlAddress() {
        return _fdlAddress;
    }

    public void setFdlAddress(final short fdlAddress) {
        _fdlAddress = fdlAddress;
    }

    public short getSlaveFlag() {
        return _slaveFlag;
    }

    public void setSlaveFlag(final short slaveFlag) {
        _slaveFlag = slaveFlag;
    }

    public short getSlaveType() {
        return _slaveType;
    }

    public void setSlaveType(final short slaveType) {
        _slaveType = slaveType;
    }

    public short getStationStatus() {
        return _stationStatus;
    }

    public void setStationStatus(final short stationStatus) {
        _stationStatus = stationStatus;
    }

    public short getWdFact1() {
        return _wdFact1;
    }

    public void setWdFact1(final short wdFact1) {
        _wdFact1 = wdFact1;
    }

    public short getWdFact2() {
        return _wdFact2;
    }

    public void setWdFact2(final short wdFact2) {
        _wdFact2 = wdFact2;
    }

    /**
     * 
     * @return Min. station delay time.
     */
    public short getMinTsdr() {
        return _minTsdr;
    }

    /**
     * 
     * @param minTsdr
     *            set Min. station delay time.
     */
    public void setMinTsdr(short minTsdr) {
        short subNetMinTsdr = -1;
        if (getProfibusDPMaster() != null && getProfibusDPMaster().getProfibusSubnet() != null) {
            subNetMinTsdr = (short) getProfibusDPMaster().getProfibusSubnet().getMinTsdr();
        }
        if (getGSDSlaveData() == null) {
            _minTsdr = minTsdr;
            return;
        }
        Short[] minTsdrs = new Short[] { getGSDSlaveData().getMinSlaveIntervall(), subNetMinTsdr,
                minTsdr };
        Arrays.sort(minTsdrs);

        _minTsdr = minTsdrs[2];
    }

    public short getGroupIdent() {
        return _groupIdent;
    }

    public void setGroupIdent(final short groupIdent) {
        _groupIdent = groupIdent;
    }

    public String getPrmUserData() {
        return _prmUserData;
    }

    public void setPrmUserData(final String prmUserData) {
        _prmUserData = prmUserData;
    }

    /** {@inheritDoc} */
    @Transient
    private final boolean fill() {
        /*
         * Read GSD-File
         */
        if (getGSDFile() == null) {
            return false;
        }

        GsdSlaveModel slaveModel = GsdFactory.makeGsdSlave(getGSDFile());

        /*
         * Head
         */
        if (slaveModel != null) {
            setVersion(slaveModel.getGsdRevision());

            /*
             * Basic - Slave Discription (read only)
             */
            setVendorName(slaveModel.getVendorName());
            setModelName(slaveModel.getModelName());
            _iDNo = String.format("0x%04X", slaveModel.getIdentNumber());
            setRevision(slaveModel.getRevision());

            /*
             * Basic - Inputs / Outputs (read only)
             */
            /*
             * Set all GSD-File Data to Slave.
             */
            setModelName(slaveModel.getModelName());
            setPrmUserData(slaveModel.getUserPrmData());
            setProfibusPNoID(slaveModel.getIdentNumber());
            setRevision(slaveModel.getRevision());

            /*
             * Basic - DP / FDL Access
             */
            /*
             * Modules
             */
            if(slaveModel.getGsdModuleList()==null||slaveModel.getGsdModuleList().isEmpty()) {
                slaveModel.setGsdModuleList(GSD2Module.parse(getGSDFile(), slaveModel));
            }

            _maxSize = slaveModel.getMaxModule();
            if (_maxSize < 1) {
                return false;
            }

            /*
             * Settings - Operation Mode
             */
            /*
             * Settings - Groups
             */
            /*
             * Settings - User Prm Data
             */
            _userPrmData = slaveModel.getUserPrmData().split(",");

            // setGSDData
            setGSDSlaveData(slaveModel);
        }
        return true;
    }

    @Transient
    public String getEpicsAdressString() {
        /** contribution to ioName (PV-link to EPICSORA) */
        return getProfibusDPMaster().getEpicsAdressString() + ":" + getSortIndex();
    }

    @Transient
    public final short getMaxSize() {
        return _maxSize;
    }

    @Transient
    public final String[] getUserPrmData() {
        return _userPrmData;
    }

    @Transient
    public final String getIDNo() {
        return _iDNo;
    }

    @Transient
    public GsdSlaveModel getGSDSlaveData() {
        if (_gsdSlaveModel == null && getGSDFile() != null) {
            fill();
        }
        return _gsdSlaveModel;
    }

    /**
     * @param slaveKeywords
     */
    @Transient
    public void setGSDSlaveData(GsdSlaveModel gsdSlaveModel) {
        _gsdSlaveModel = gsdSlaveModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node copyParameter(NamedDBClass parentNode) {
        if (parentNode instanceof Master) {
            Master master = (Master) parentNode;
            Slave copy = new Slave(master);
            copy.setDocuments(getDocuments());
            copy.setFdlAddress(getFdlAddress());
            copy.setGroupIdent(getGroupIdent());
            copy.setGSDFile(getGSDFile());
            copy.setGSDSlaveData(getGSDSlaveData());
            copy.setMinTsdr(getMinTsdr());
            copy.setModelName(getModelName());
            copy.setPrmUserData(getPrmUserData());
            copy.setProfibusPNoID(getProfibusPNoID());
            copy.setRevision(getRevision());
            copy.setSlaveFlag(getSlaveFlag());
            copy.setSlaveType(getSlaveType());
            copy.setStationStatus(getStationStatus());
            copy.setVendorName(getVendorName());
            copy.setWdFact1(getWdFact1());
            copy.setWdFact2(getWdFact2());
            for (Node n : getChildren()) {
                n.copyThisTo(copy);
            }
            return copy;
        }
        return null;
    }

    /**
     * Swap the SortIndex of two nodes. Is the given SortIndex in use the other node became the old
     * SortIndex of this node.
     * 
     * @param toIndex
     *            the new sortIndex for this node.
     */
    @Override
    public void moveSortIndex(short toIndex) {
        if (toIndex == getSortIndex()) {
            // no new Address don't move
            return;
        }
        if (getParent() == null) {
            // Have no Parent
            setSortIndexNonHibernate(toIndex);
            CentralLogger.getInstance().warn(this, "Slave has no Parent!");
            return;
        }
        if (toIndex < 0) {
            throw new ArrayIndexOutOfBoundsException(toIndex);
        }
        // Move a exist Node
        Node moveNode = getParent().getChildrenAsMap().get(toIndex);
        if (moveNode != null) {
            moveNode.moveSortIndex((short) (toIndex + 1));
        }
        setSortIndexNonHibernate(toIndex);
    }
}