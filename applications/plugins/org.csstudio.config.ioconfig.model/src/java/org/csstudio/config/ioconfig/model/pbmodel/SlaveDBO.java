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
package org.csstudio.config.ioconfig.model.pbmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.AbstractNodeSharedImpl;
import org.csstudio.config.ioconfig.model.GSDFileTypes;
import org.csstudio.config.ioconfig.model.INodeVisitor;
import org.csstudio.config.ioconfig.model.NodeType;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdFileParser;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel2;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.ParsedGsdFileModel;
import org.hibernate.annotations.BatchSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gerke
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 26.03.2007
 */

@Entity
@BatchSize(size = 32)
@Table(name = "ddb_Profibus_Slave")
//@SecondaryTable(name="nodeDB", pkJoinColumns = @)
public class SlaveDBO extends AbstractNodeSharedImpl<MasterDBO, ModuleDBO> {

    private static final Logger LOG = LoggerFactory.getLogger(SlaveDBO.class);

    private static final long serialVersionUID = 1L;
    private String _vendorName;
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
    private short _slaveType;
    private short _stationStatus;
    private short _wdFact1;
    private short _wdFact2;
    /**
     * Min. station delay time.
     */
    private short _minTsdr;
    private short _groupIdent;
    /**
     * Parameter user data.
     */
    private List<Integer> _prmUserDataList = new ArrayList<Integer>();
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
        super(master);
        moveSortIndex(stationAddress);
    }

    @Override
    @Nonnull
    @Transient
    public MasterDBO getParent() {
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
    @CheckForNull
    public SlaveDBO copyParameter(@Nonnull final MasterDBO parentNode) throws PersistenceException {
        final MasterDBO master = parentNode;
        final SlaveDBO copy = new SlaveDBO(master);
        copy.setFdlAddress(getFdlAddress());
        copy.setGroupIdent(getGroupIdent());
        copy.setGSDFile(getGSDFile());
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
        for (final ModuleDBO node : getChildren()) {
            final AbstractNodeSharedImpl<?, ?> childrenCopy = node.copyThisTo(copy, null);
            childrenCopy.setSortIndexNonHibernate(node.getSortIndex());
        }
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ModuleDBO createChild() throws PersistenceException {
        return new ModuleDBO(this);
    }

    @Override
    public boolean equals(@CheckForNull final Object obj) {
        return super.equals(obj);
    }

    @Transient
    private boolean fill() {
        final GSDFileDBO gsdFile = getGSDFile();
        if(gsdFile == null) {
            return false;
        }

        final ParsedGsdFileModel parsedGsdFileModel = gsdFile.getParsedGsdFileModel();

        // Head
        if(parsedGsdFileModel != null) {
            setVersion(parsedGsdFileModel.getGsdRevision());

            setVendorName(parsedGsdFileModel.getVendorName());
            setModelName(parsedGsdFileModel.getModelName());
            _iDNo = String.format("0x%04X", parsedGsdFileModel.getIdentNumber());
            setRevision(parsedGsdFileModel.getRevision());

            setModelName(parsedGsdFileModel.getModelName());
            setPrmUserData(parsedGsdFileModel.getExtUserPrmDataConst());
            setProfibusPNoID(parsedGsdFileModel.getIdentNumber());
            setRevision(parsedGsdFileModel.getRevision());
            parsedGsdFileModel.isModularStation();
            _maxSize = parsedGsdFileModel.getMaxModule().shortValue();
            if(parsedGsdFileModel.isModularStation()) {
                if(_maxSize < 1) {
                    return false;
                }
            }
        }
        return true;
    }

    @Transient
    @Nonnull
    public String getEpicsAdressString() {
        /** contribution to ioName (PV-link to EPICSORA) */
        return getProfibusDPMaster().getEpicsAdressString() + ":" + getSortIndex();
    }

    public int getFdlAddress() {
        return _fdlAddress;
    }

    public int getGroupIdent() {
        return _groupIdent;
    }

    /** @return the GSDFile. */

    @ManyToOne(fetch = FetchType.EAGER)
    @CheckForNull
    public GSDFileDBO getGSDFile() {
        return _gsdFile;
    }

    @Transient
    @CheckForNull
    public final String getIDNo() {
        return _iDNo;
    }

    @Transient
    public final int getMaxSize() {
        return _maxSize;
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
     * @return get the Model Name of Slave.
     */
    @Nonnull
    public String getModelName() {
        if(_modelName == null) {
            _modelName = "";
        }
        return _modelName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    @Nonnull
    public NodeType getNodeType() {
        return NodeType.SLAVE;
    }

    @Nonnull
    public String getPrmUserData() {
        return GsdFileParser.intList2HexString(_prmUserDataList);
    }

    @Transient
    @Nonnull
    public List<Integer> getPrmUserDataList() {
        return _prmUserDataList;
    }

    @ManyToOne
    @Nonnull
    public MasterDBO getProfibusDPMaster() {
        return getParent();
    }

    public int getProfibusPNoID() {
        return _profibusPNoID;
    }

    @Nonnull
    public String getRevision() {
        if(_revision == null) {
            _revision = "";
        }
        return _revision;
    }

    public int getSlaveFlag() {
        return _slaveFlag;
    }

    public int getSlaveType() {
        return _slaveType;
    }

    public int getStationStatus() {
        return _stationStatus;
    }

    /**
     *
     * @return The Vendor name of this slave.
     */
    @Nonnull
    public String getVendorName() {
        if(_vendorName == null) {
            _vendorName = "";
        }
        return _vendorName;
    }

    public int getWdFact1() {
        return _wdFact1;
    }

    public int getWdFact2() {
        return _wdFact2;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
    // CHECKSTYLE ON: StrictDuplicateCode

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
        final short index = (short) toIndex;
        if(index == getSortIndex()) { // no new Address don't move
            return;
        }
        if(getParent() == null) { // Have no Parent
            setSortIndexNonHibernate(index);
            LOG.warn("Slave has no Parent!");
            return;
        }
        if(index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        // Move a exist Node
        final SlaveDBO moveNode = getParent().getChildrenAsMap().get(index);
        if(moveNode != null) {
            moveNode.moveSortIndex( index + 1);
        }
        setSortIndexNonHibernate(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public GSDFileTypes needGSDFile() {
        return GSDFileTypes.Slave;
    }

    @Column(scale = 5)
    public void setFdlAddress(final int fdlAddress) {
        _fdlAddress = (short) fdlAddress;
    }

    public void setGroupIdent(final int groupIdent) {
        _groupIdent = (short) groupIdent;
    }

    /**
     * @param gsdFile
     *            set the GSDFile.
     */
    public void setGSDFile(@CheckForNull final GSDFileDBO gsdFile) {
        if(gsdFile == null) {
            _gsdFile = null;
        } else if(!gsdFile.equals(_gsdFile)) {
            final GSDFileDBO oldGDS = _gsdFile;
            _gsdFile = gsdFile;
            if(!fill()) {
                _gsdFile = oldGDS;
            }
        }
    }

    /**
     *
     * @param minTsdr
     *            set Min. station delay time.
     */
    public void setMinTsdr(@Nonnull final Integer minTsdr) {
        final SortedSet<Integer> minTsdrSet = new TreeSet<Integer>();
        minTsdrSet.add(minTsdr);
        Integer minSlaveIntervall;
        final GSDFileDBO gsdFile = getGSDFile();
        if(gsdFile != null) {
            final ParsedGsdFileModel parsedGsdFileModel = gsdFile.getParsedGsdFileModel();
            if(parsedGsdFileModel != null) {
                minSlaveIntervall = parsedGsdFileModel.getIntProperty("Min_Slave_Intervall");
                if(minSlaveIntervall != null) {
                    minTsdrSet.add(minSlaveIntervall);
                }
            }
        }
        _minTsdr = minTsdrSet.last().shortValue();
    }

    /**
     *
     * @param modelName
     *            Set the Model Name of Slave.
     */
    public void setModelName(@Nonnull final String modelName) {
        _modelName = modelName;
    }

    @Transient
    public void setPrmUserData(@Nonnull final List<Integer> prmUserDataList) {
        _prmUserDataList = prmUserDataList;
    }

    public void setPrmUserData(@Nonnull final String prmUserData) {
        _prmUserDataList = new ArrayList<Integer>();
        if(prmUserData != null&&!prmUserData.trim().isEmpty()) {
            final String[] split = prmUserData.split(",");
            for (final String value : split) {
                _prmUserDataList.add(GsdFileParser.gsdValue2Int(value));
            }
        }
    }

    @Transient
    public void setPrmUserDataByte(final int index, @Nonnull final Integer newValue) {
        _prmUserDataList.set(index, newValue);
    }

    public void setProfibusDPMaster(@Nonnull final MasterDBO profibusDPMaster) {
        this.setParent(profibusDPMaster);
    }

    public void setProfibusPNoID(final int profibusPNoID) {
        _profibusPNoID = profibusPNoID;
    }

    public void setRevision(@Nonnull final String revision) {
        _revision = revision;
    }

    @Column(scale = 5)
    public void setSlaveFlag(final int slaveFlag) {
        _slaveFlag = (short) slaveFlag;
    }

    @Column(scale = 5)
    public void setSlaveType(final int slaveType) {
        _slaveType = (short) slaveType;
    }

    @Column(scale = 5)
    public void setStationStatus(final int stationStatus) {
        _stationStatus = (short) stationStatus;
    }

    /**
     * @param vendorName Set the Vendor name of this slave.
     */
    public void setVendorName(@Nonnull final String vendorName) {
        _vendorName = vendorName;
    }

    @Column(scale = 5)
    public void setWdFact1(final int wdFact1) {
        _wdFact1 = (short) wdFact1;
    }

    @Column(scale = 5)
    public void setWdFact2(final int wdFact2) {
        _wdFact2 = (short) wdFact2;
    }

    @Transient
    @Nonnull
    public String getSlaveCfgDataString() {
        StringBuilder cfgData = new StringBuilder();
        for (final ModuleDBO module : getChildrenAsMap().values()) {
            final GsdModuleModel2 gsdModuleModel2 = module.getGsdModuleModel2();
            if(gsdModuleModel2 != null) {
                cfgData = cfgData.append(gsdModuleModel2.getValueAsString().trim()).append(",");
            }
        }
        if(cfgData.toString().endsWith(",")) {
            cfgData = cfgData.deleteCharAt(cfgData.length() - 1);
        }
        return cfgData.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void assembleEpicsAddressString() throws PersistenceException {
        setSlaveFlag(128);
        super.assembleEpicsAddressString();
    }
}
