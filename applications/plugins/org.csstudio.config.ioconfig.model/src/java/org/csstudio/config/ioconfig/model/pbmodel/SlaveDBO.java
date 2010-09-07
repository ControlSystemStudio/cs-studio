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
 * $Id: Slave.java,v 1.7 2010/09/03 07:13:20 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.GSDFileTypes;
import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.NodeType;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GSD2Module;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdFactory;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdSlaveModel;
import org.csstudio.platform.logging.CentralLogger;
import org.hibernate.annotations.BatchSize;

/**
 * @author gerke
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 26.03.2007
 */

@Entity
@BatchSize(size=32)
@Table(name = "ddb_Profibus_Slave")
public class SlaveDBO extends AbstractNodeDBO {
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
//    private String _prmUserData;
    private List<String> _prmUserDataList = new ArrayList<String>();

    /**
     * The GSD file for this Slave.
     */
    private GSDFileDBO _gsdFile;

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
     * This Constructor is only used by Hibernate. To create an new {@link SlaveDBO}
     * {@link #Slave(MasterDBO)}
     */
    public SlaveDBO() {
    }

    public SlaveDBO(final MasterDBO master) {
        this(master, -1);
    }

    public SlaveDBO(final MasterDBO master, final int stationAddress) {
        setParent(master);
        master.addChild(this);
        moveSortIndex(stationAddress);
    }

    @ManyToOne
    public MasterDBO getProfibusDPMaster() {
        return (MasterDBO) getParent();
    }

    public void setProfibusDPMaster(final MasterDBO profibusDPMaster) {
        this.setParent(profibusDPMaster);
    }

    @Transient
    @SuppressWarnings("unchecked")
    public Set<ModuleDBO> getModules() {
        return (Set<ModuleDBO>) getChildren();
    }

    /** @return the GSDFile. */

    @ManyToOne(fetch = FetchType.EAGER)
    public GSDFileDBO getGSDFile() {
        return _gsdFile;
    }

    /**
     * @param gsdFile
     *            set the GSDFile.
     */
    public void setGSDFile(final GSDFileDBO gsdFile) {
        GSDFileDBO oldGDS = _gsdFile;
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

    public int getFdlAddress() {
        return _fdlAddress;
    }

    @Column(scale=5)
    public void setFdlAddress(final int fdlAddress) {
        _fdlAddress = (short) fdlAddress;
    }

    public int getSlaveFlag() {
        return _slaveFlag;
    }
    @Column(scale=5)
    public void setSlaveFlag(final int slaveFlag) {
        _slaveFlag = (short) slaveFlag;
    }

    public int getSlaveType() {
        return _slaveType;
    }
    @Column(scale=5)
    public void setSlaveType(final int slaveType) {
        _slaveType = (short) slaveType;
    }

    public int getStationStatus() {
        return _stationStatus;
    }
    @Column(scale=5)
    public void setStationStatus(final int stationStatus) {
        _stationStatus = (short) stationStatus;
    }

    public int getWdFact1() {
        return _wdFact1;
    }
    @Column(scale=5)
    public void setWdFact1(final int wdFact1) {
        _wdFact1 = (short) wdFact1;
    }

    public int getWdFact2() {
        return _wdFact2;
    }
    @Column(scale=5)
    public void setWdFact2(final int wdFact2) {
        _wdFact2 = (short) wdFact2;
    }

    /**
     *
     * @return Min. station delay time.
     */
    public int getMinTsdr() {
        return _minTsdr;
    }

    /**
     *
     * @param minTsdr
     *            set Min. station delay time.
     */
    public void setMinTsdr(final int minTsdr) {
        int subNetMinTsdr = -1;
        if ((getProfibusDPMaster() != null) && (getProfibusDPMaster().getProfibusSubnet() != null)) {
            subNetMinTsdr = getProfibusDPMaster().getProfibusSubnet().getMinTsdr();
        }
        if (getGSDSlaveData() == null) {
            _minTsdr = (short) minTsdr;
            return;
        }
        int[] minTsdrs = new int[] { getGSDSlaveData().getMinSlaveIntervall(), subNetMinTsdr,
                minTsdr };
        Arrays.sort(minTsdrs);

        _minTsdr = (short) minTsdrs[2];
    }

    public int getGroupIdent() {
        return _groupIdent;
    }

    public void setGroupIdent(final int groupIdent) {
        _groupIdent = (short) groupIdent;
    }

    public String getPrmUserData() {
//        return _prmUserData;
        String string = _prmUserDataList.toString();
        string = string.substring(1,string.length()-1);
        return string;
    }

    public void setPrmUserData(final String prmUserData) {
//        _prmUserData = prmUserData;
        _prmUserDataList = Arrays.asList(prmUserData.split(","));
    }

    /**
     * @param index
     * @param newValue
     */
    @Transient
    public void setPrmUserDataByte(final int index, final String newValue) {
        _prmUserDataList.set(index, newValue);
    }

    @Transient
    public List<String> getPrmUserDataList() {
        return _prmUserDataList;
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
            if((slaveModel.getGsdModuleList()==null)||slaveModel.getGsdModuleList().isEmpty()) {
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
    public final int getMaxSize() {
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
        if ((_gsdSlaveModel == null) && (getGSDFile() != null)) {
            fill();
        }
        return _gsdSlaveModel;
    }

    /**
     * @param slaveKeywords
     */
    @Transient
    public void setGSDSlaveData(final GsdSlaveModel gsdSlaveModel) {
        _gsdSlaveModel = gsdSlaveModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractNodeDBO copyParameter(final NamedDBClass parentNode) {
        if (parentNode instanceof MasterDBO) {
            MasterDBO master = (MasterDBO) parentNode;
            SlaveDBO copy = new SlaveDBO(master);
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
            for (AbstractNodeDBO n : getChildren()) {
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
    public void moveSortIndex(final int toIndex) {
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
        AbstractNodeDBO moveNode = getParent().getChildrenAsMap().get(toIndex);
        if (moveNode != null) {
            moveNode.moveSortIndex((toIndex + 1));
        }
        setSortIndexNonHibernate(toIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GSDFileTypes needGSDFile() {
        return GSDFileTypes.Slave;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public NodeType getNodeType() {
        return NodeType.SLAVE;
    }

}
