package org.csstudio.config.ioconfig.model.pbmodel;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.NodeType;
import org.csstudio.config.ioconfig.model.PersistenceException;

/***********************************************************
 * Data model for Profibus-DP Subnet *
 ***********************************************************
 * Created by: Torsten Boeckmann * Date: 07. Dezember 2005 *
 ***********************************************************
 * last changed: @author $Author: hrickens $ * Grounds of changed: ................... *
 * ................... * Revision: @version $Revision: 1.2 $ * Status: untested * Precondition: *
 * Postcondition: * *
 ***********************************************************/

@Entity
//@Table(name = "ddb_ProfibusSubnet")
@Table(name = "ddb_Profibus_Subnet")
public class ProfibusSubnetDBO extends AbstractNodeDBO<IocDBO, MasterDBO> {

    /**
     * Subnet baud rate.
     */
    private String _baudRate;
    /**
     * Slot time.
     */
    private int _slotTime;

    /**
     * Min. Station delay time.
     */
    private int _minTsdr;
    /** Max. Station delay time. */
    private int _maxTsdr;
    /** Quite time. */
    private short _tqui;
    /** Set time. */
    private short _tset;
    /** Target rotation time. */
    private long _ttr;
    /** gap. */
    private short _gap;
    /** Highest station address. */
    private short _hsa;
    /** Watchdog. */
    private int _watchdog;
    /** Regard configuration. */
    private boolean _option;
    /** Numbers of repeater. */
    private short _repeaterNumber;
    /** Length of cu line. */
    private float _cuLineLength;
    /** Numbers of opticle link modules. */
    private short _olmNumber;
    /** Length of lwl. */
    private float _lwlLength;
    /** Number of Master at project. */
    private short _masterNumber;
    /** Number of slaves at project. */
    private short _slaveNumber;
    /** Number of subcriber at project. */
    private short _subscriber;
    /**
     * Quota of FDL, FMS or S7 communication at project.
     */
    private String _quotaFdlFmsS7com;
    /**
     * Communication profil: DP, Standard, Universal (DP,FMS),User defined.
     */
    private String _profil; //

    /**
     * This Constructor is only used by Hibernate. To create an new {@link ProfibusSubnetDBO}
     * {@link #ProfibusSubnet(IocDBO)}
     */
    public ProfibusSubnetDBO() {
        // Constructor for Hibernate
    }

    /**
     * The default Constructor.
     * @throws PersistenceException 
     */
    public ProfibusSubnetDBO(@Nonnull final IocDBO ioc) throws PersistenceException {
        setParent(ioc);
        ioc.addChild(this);
    }

    /**
     *
     * @return get the Baudrate
     */
    public String getBaudRate() {
        return _baudRate;
    }

    /**
     *
     * @param baudRate
     *            Set the Baudrate for the PBNet
     */
    public void setBaudRate(final String baudRate) {
        _baudRate = baudRate;
    }

    /**
     *
     * @return the length of the CU-Line.
     */
    public float getCuLineLength() {
        return _cuLineLength;
    }

    /**
     *
     * @param cuLineLength
     *            Set the length of CU-Line.
     */
    public void setCuLineLength(final float cuLineLength) {
        this._cuLineLength = cuLineLength;
    }

    /**
     *
     * @return the Gap.
     */
    public short getGap() {
        return _gap;
    }

    /**
     *
     * @param gap
     *            set the Gap.
     */
    public void setGap(final int gap) {
        this._gap = (short)gap;
    }

    /**
     *
     * @return the HSA (Highest Server Adress).
     */
    public short getHsa() {
        return _hsa;
    }

    /**
     *
     * @param hsa
     *            set the HSA (Highest Server Adress).
     */
    public void setHsa(final int hsa) {
        this._hsa = (short)hsa;
    }

    /**
     *
     * @return the length of the LWL.
     */
    public float getLwlLength() {
        return _lwlLength;
    }

    /**
     *
     * @param lwlLength
     *            set the length of the LWL.
     */
    public void setLwlLength(final float lwlLength) {
        this._lwlLength = lwlLength;
    }

    /**
     *
     * @return the number of Profibus Master
     */
    public short getMasterNumber() {
        return _masterNumber;
    }

    /**
     *
     * @param masterNumber
     *            set the number of Profibus Master
     */
    public void setMasterNumber(final short masterNumber) {
        this._masterNumber = masterNumber;
    }

    /**
     *
     * @return the max Tsdr
     */
    public int getMaxTsdr() {
        return _maxTsdr;
    }

    /**
     *
     * @param maxTsdr
     *            set the max Tsdr
     */
    public void setMaxTsdr(final int maxTsdr) {
        this._maxTsdr = maxTsdr;
    }

    /**
     *
     * @return the min Tsdr.
     */
    public int getMinTsdr() {
        return _minTsdr;
    }

    /**
     *
     * @param minTsdr
     *            set the min Tsdr.
     */
    public void setMinTsdr(final int minTsdr) {
        this._minTsdr = minTsdr;
    }

    /**
     *
     * @return The olm Number.
     */
    public short getOlmNumber() {
        return _olmNumber;
    }

    /**
     *
     * @param olmNumber
     *            get the olm number.
     */
    public void setOlmNumber(final short olmNumber) {
        this._olmNumber = olmNumber;
    }

    /**
     *
     * @return the state of Option.
     */
    public boolean isOptionPar() {
        return _option;
    }

    /**
     *
     * @param optionPar
     *            set the state of option
     */
    public void setOptionPar(final boolean optionPar) {
        this._option = optionPar;
    }

    /**
     *
     * @return the Profile.
     */
    public String getProfil() {
        return _profil;
    }

    /**
     *
     * @param profil
     *            set the Profile
     */
    public void setProfil(final String profil) {
        this._profil = profil;
    }

    /**
     *
     * @return the quota Fdl Fms S7 com.
     */
    public String getQuotaFdlFmsS7com() {
        return _quotaFdlFmsS7com;
    }

    /**
     *
     * @param quotaFdlFmsS7Com
     *            set the quota Fdl Fms S7 com.
     */
    public void setQuotaFdlFmsS7com(final String quotaFdlFmsS7Com) {
        _quotaFdlFmsS7com = quotaFdlFmsS7Com;
    }

    /**
     *
     * @return the Repeater number.
     */
    public short getRepeaterNumber() {
        return _repeaterNumber;
    }

    /**
     *
     * @param repeaterNumber
     *            set the Repeater number.
     */
    public void setRepeaterNumber(final int repeaterNumber) {
        _repeaterNumber = (short) repeaterNumber;
    }

    public short getSlaveNumber() {
        return _slaveNumber;
    }

    public void setSlaveNumber(final short slaveNumber) {
        this._slaveNumber = slaveNumber;
    }

    public int getSlotTime() {
        return _slotTime;
    }

    public void setSlotTime(final int slotTime) {
        this._slotTime = slotTime;
    }

    public short getSubscriber() {
        return _subscriber;
    }

    public void setSubscriber(final short subscriber) {
        this._subscriber = subscriber;
    }

    public short getTqui() {
        return _tqui;
    }

    public void setTqui(final int tqui) {
        this._tqui = (short)tqui;
    }

    public short getTset() {
        return _tset;
    }

    public void setTset(final int tset) {
        this._tset = (short)tset;
    }

    public long getTtr() {
        return _ttr;
    }

    public void setTtr(final long ttr) {
        this._ttr = ttr;
    }

    public int getWatchdog() {
        return _watchdog;
    }

    public void setWatchdog(final int watchdog) {
        this._watchdog = watchdog;
    }

    @Transient
    @Nonnull
    public Set<MasterDBO> getProfibusDPMaster() {
        return getChildren();
    }

    /**
     *
     * @return the parent Ioc of this subnet.
     */
    @ManyToOne // TODO (hrickens) [11.05.2011]: Wieso ist hier eine ManyToOne Beziehung? Die ist doch inder der getParent Methode.
    public IocDBO getIoc() {
        return (IocDBO) getParent();
    }

    /**
     *
     * @param ioc
     *            set the parent Ioc of this subnet.
     */
    public void setIoc(final IocDBO ioc) {
        this.setParent(ioc);
    }

    /**
     * contribution to ioName (PV-link to EPICSORA).
     *
     * @return the Epics Address String
     */
    @Transient
    @Nonnull
    public String getEpicsAddressString() {
        return "@"+getName();
    }


    @Transient
    public void updateName(final String name) {
        setName(name);
//        localUpdate();
    }

    /**
     * {@inheritDoc}
     * @throws PersistenceException 
     */
    @Override
    public ProfibusSubnetDBO copyParameter(final IocDBO parent) throws PersistenceException {
            IocDBO ioc = parent;
            ProfibusSubnetDBO copy = new ProfibusSubnetDBO(ioc);
            copy.setDescription(getDescription());
            copy.setBaudRate(getBaudRate());
            copy.setCuLineLength(getCuLineLength());
            copy.setGap(getGap());
            copy.setHsa(getHsa());
            copy.setLwlLength(getLwlLength());
            copy.setMasterNumber(getMasterNumber());
            copy.setMaxTsdr(getMaxTsdr());
            copy.setMinTsdr(getMinTsdr());
            copy.setOlmNumber(getOlmNumber());
            copy.setOptionPar(isOptionPar());
            copy.setProfil(getProfil());
            copy.setQuotaFdlFmsS7com(getQuotaFdlFmsS7com());
            copy.setRepeaterNumber(getRepeaterNumber());
            copy.setSlaveNumber(getSlaveNumber());
            copy.setSlotTime(getSlotTime());
            copy.setSubscriber(getSubscriber());
            copy.setTqui(getTqui());
            copy.setTset(getTset());
            copy.setTtr(getTtr());
            copy.setWatchdog(getWatchdog());
            return copy;
    }

    @Override
    public ProfibusSubnetDBO copyThisTo(final IocDBO parentNode) throws PersistenceException {
        ProfibusSubnetDBO copy = super.copyThisTo(parentNode);
        for (MasterDBO node : getChildren()) {
            AbstractNodeDBO childrenCopy = node.copyThisTo(copy);
            childrenCopy.setSortIndexNonHibernate(node.getSortIndex());
        }
        return copy;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public NodeType getNodeType() {
        return NodeType.SUBNET;
    }

}
