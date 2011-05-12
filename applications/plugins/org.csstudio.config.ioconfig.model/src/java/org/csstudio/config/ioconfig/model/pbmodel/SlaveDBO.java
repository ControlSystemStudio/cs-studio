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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.GSDFileTypes;
import org.csstudio.config.ioconfig.model.NodeType;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdFileParser;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.ParsedGsdFileModel;
import org.csstudio.platform.logging.CentralLogger;
import org.hibernate.annotations.BatchSize;

/**
 * @author gerke
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 26.03.2007
 */

@Entity
@BatchSize(size = 32)
@Table(name = "ddb_Profibus_Slave")
public class SlaveDBO extends AbstractNodeDBO<MasterDBO, ModuleDBO> {
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
    private List<Integer> _prmUserDataList = new ArrayList<Integer>();
    
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
     * Date from GSD File. The slot index number.
     */
    @Transient
    private String _iDNo;
    
    /**
     * This Constructor is only used by Hibernate. To create an new {@link SlaveDBO}
     * {@link #Slave(MasterDBO)}
     */
    public SlaveDBO() {
        // only for Hibernate
    }
    
    public SlaveDBO(@Nonnull final MasterDBO master) throws PersistenceException {
        this(master, -1);
    }
    
    private SlaveDBO(@Nonnull final MasterDBO master, final int stationAddress) throws PersistenceException {
        setParent(master);
        master.addChild(this);
        moveSortIndex(stationAddress);
    }
    
    @ManyToOne
    public MasterDBO getProfibusDPMaster() {
        return (MasterDBO) getParent();
    }
    
    public void setProfibusDPMaster(@Nonnull final MasterDBO profibusDPMaster) {
        this.setParent(profibusDPMaster);
    }
    
    @Transient
    @SuppressWarnings("unchecked")
    public Set<ModuleDBO> getModules() {
        return (Set<ModuleDBO>) getChildren();
    }
    
    /** @return the GSDFile. */
    
    @ManyToOne(fetch = FetchType.EAGER)
    @CheckForNull
    public GSDFileDBO getGSDFile() {
        return _gsdFile;
    }
    
    /**
     * @param gsdFile
     *            set the GSDFile.
     * @throws IOException 
     */
    public void setGSDFile(@Nonnull final GSDFileDBO gsdFile) throws IOException {
        if(gsdFile == null) {
            _gsdFile = gsdFile;
        } else if(!gsdFile.equals(_gsdFile)) {
            GSDFileDBO oldGDS = _gsdFile;
            _gsdFile = gsdFile;
            if(!fill()) {
                _gsdFile = oldGDS;
            }
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
    public void setVendorName(@Nonnull final String vendorName) {
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
    public void setModelName(@Nonnull final String modelName) {
        _modelName = modelName;
    }
    
    public String getRevision() {
        return _revision;
    }
    
    public void setRevision(@Nonnull final String revision) {
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
    
    @Column(scale = 5)
    public void setFdlAddress(final int fdlAddress) {
        _fdlAddress = (short) fdlAddress;
    }
    
    public int getSlaveFlag() {
        return _slaveFlag;
    }
    
    @Column(scale = 5)
    public void setSlaveFlag(final int slaveFlag) {
        _slaveFlag = (short) slaveFlag;
    }
    
    public int getSlaveType() {
        return _slaveType;
    }
    
    @Column(scale = 5)
    public void setSlaveType(final int slaveType) {
        _slaveType = (short) slaveType;
    }
    
    public int getStationStatus() {
        return _stationStatus;
    }
    
    @Column(scale = 5)
    public void setStationStatus(final int stationStatus) {
        _stationStatus = (short) stationStatus;
    }
    
    public int getWdFact1() {
        return _wdFact1;
    }
    
    @Column(scale = 5)
    public void setWdFact1(final int wdFact1) {
        _wdFact1 = (short) wdFact1;
    }
    
    public int getWdFact2() {
        return _wdFact2;
    }
    
    @Column(scale = 5)
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
    public void setMinTsdr(@Nonnull final Integer minTsdr) {
        SortedSet<Integer> minTsdrSet = new TreeSet<Integer>();
        minTsdrSet.add(minTsdr);
        Integer minSlaveIntervall;
        try {
            GSDFileDBO gsdFile = getGSDFile();
            if(gsdFile != null) {
                ParsedGsdFileModel parsedGsdFileModel = gsdFile.getParsedGsdFileModel();
                if(parsedGsdFileModel != null) {
                    minSlaveIntervall = parsedGsdFileModel.getIntProperty("Min_Slave_Intervall");
                    if(minSlaveIntervall != null) {
                        minTsdrSet.add(minSlaveIntervall);
                    }
                }
            }
        } catch (IOException e) {
            // no min Slave Intervall!
        }
        _minTsdr = minTsdrSet.last().shortValue();
    }
    
    public int getGroupIdent() {
        return _groupIdent;
    }
    
    public void setGroupIdent(final int groupIdent) {
        _groupIdent = (short) groupIdent;
    }
    
    @Nonnull
    public String getPrmUserData() {
        return GsdFileParser.intList2HexString(_prmUserDataList);
    }
    
    @Transient
    public void setPrmUserData(@Nonnull final List<Integer> prmUserDataList) {
        _prmUserDataList = prmUserDataList;
    }
    
    public void setPrmUserData(@Nonnull final String prmUserData) {
        // System.out.println("prmUserData in:  " + prmUserData);
        if(prmUserData != null) {
            String[] split = prmUserData.split(",");
            _prmUserDataList = new ArrayList<Integer>();
            for (String value : split) {
                _prmUserDataList.add(GsdFileParser.gsdValue2Int(value));
            }
        }
    }
    
    /**
     * @param index
     * @param newValue
     */
    @Transient
    public void setPrmUserDataByte(final int index, @Nonnull final Integer newValue) {
        _prmUserDataList.set(index, newValue);
    }
    
    @Transient
    public List<Integer> getPrmUserDataList() {
        return _prmUserDataList;
    }
    
    @Transient
    private boolean fill() throws IOException {
        /*
         * Read GSD-File
         */
        GSDFileDBO gsdFile = getGSDFile();
        if(gsdFile == null) {
            return false;
        }
        
        //        GsdSlaveModel slaveModel = GsdFactory.makeGsdSlave(gsdFile);
        ParsedGsdFileModel parsedGsdFileModel = gsdFile.getParsedGsdFileModel();
        
        /*
         * Head
         */
        if(parsedGsdFileModel != null) {
            setVersion(parsedGsdFileModel.getGsdRevision());
            
            /*
             * Basic - Slave Discription (read only)
             */
            setVendorName(parsedGsdFileModel.getVendorName());
            setModelName(parsedGsdFileModel.getModelName());
            //            _iDNo = String.format("0x%04X", slaveModel.getIdentNumber());
            _iDNo = String.format("0x%04X", parsedGsdFileModel.getIdentNumber());
            setRevision(parsedGsdFileModel.getRevision());
            
            /*
             * Basic - Inputs / Outputs (read only)
             */
            /*
             * Set all GSD-File Data to Slave.
             */
            setModelName(parsedGsdFileModel.getModelName());
            //            setPrmUserData(slaveModel.getUserPrmData());
            setPrmUserData(parsedGsdFileModel.getExtUserPrmDataConst());
            //            setProfibusPNoID(slaveModel.getIdentNumber());
            setProfibusPNoID(parsedGsdFileModel.getIdentNumber());
            setRevision(parsedGsdFileModel.getRevision());
            
            /*
             * Basic - DP / FDL Access
             */
            //            /*
            //             * Modules
            //             */
            //            if (!parsedGsdFileModel.hasModule()) {
            //                parsedGsdFileModel.setGsdModuleList(GSD2Module.parse(gsdFile, parsedGsdFileModel));
            //            }
            
            _maxSize = parsedGsdFileModel.getMaxModule().shortValue();
            if(_maxSize < 1) {
                return false;
            }
            
            /*
             * Settings - Operation Mode
             */
            /*
             * Settings - Groups
             */

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
    public final String getIDNo() {
        return _iDNo;
    }
    
    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    @CheckForNull
    public SlaveDBO copyParameter(@Nullable final MasterDBO parentNode) throws PersistenceException {
        MasterDBO master = parentNode;
        SlaveDBO copy = new SlaveDBO(master);
        copy.setFdlAddress(getFdlAddress());
        copy.setGroupIdent(getGroupIdent());
        try {
            copy.setGSDFile(getGSDFile());
        } catch (IOException e) {
            throw new PersistenceException(e);
        }
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
        for (AbstractNodeDBO node : getChildren()) {
            AbstractNodeDBO childrenCopy = node.copyThisTo(copy);
            childrenCopy.setSortIndexNonHibernate(node.getSortIndex());
        }
        return copy;
    }
    
    /**
     * Swap the SortIndex of two nodes. Is the given SortIndex in use the other node became the old
     * SortIndex of this node.
     *
     * @param index
     *            the new sortIndex for this node.
     * @throws PersistenceException 
     */
    @Override
    public void moveSortIndex(final int toIndex) throws PersistenceException {
        short index = (short) toIndex;
        if(index == getSortIndex()) {
            // no new Address don't move
            return;
        }
        if(getParent() == null) {
            // Have no Parent
            setSortIndexNonHibernate(index);
            CentralLogger.getInstance().warn(this, "Slave has no Parent!");
            return;
        }
        if(index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        // Move a exist Node
        AbstractNodeDBO moveNode = getParent().getChildrenAsMap().get(index);
        if(moveNode != null) {
            moveNode.moveSortIndex( (index + 1));
        }
        setSortIndexNonHibernate(index);
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
