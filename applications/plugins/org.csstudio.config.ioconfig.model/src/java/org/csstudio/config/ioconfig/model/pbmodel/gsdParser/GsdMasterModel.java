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
 * $Id: GsdMasterModel.java,v 1.2 2010/08/20 13:33:07 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import org.csstudio.config.ioconfig.model.GSDFileTypes;

/**
 * Data model for GSD Syntax Master-related Keywords
 *
 * contains the master related GSD definitions.<br>
 * The definitions are given by the Profibus Nutzer<br>
 *
 * @author Torsten Boeckmann Date: 08. Dezember 2005
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 18.07.2008
 */
//TODO (hrickens) [03.05.2011]: Raus damit!
public class GsdMasterModel extends GsdGeneralModel {
    /**
     * The DP device supports the function download, Start_seq and End_seq.<br>
     * Type: Boolean (1: TRUE)
     */
    private boolean _downloadSupp;

    /**
     * The DP device supports the function upload, Start_seq and End_seq.<br>
     * Type: Boolean (1: TRUE)<br>
     */
    private boolean _uploadSupp;

    /**
     * The DP device supports the function ActParaBrct.<br>
     * Type: Boolean (1: TRUE)
     */
    private boolean _actParamBrctSupp;

    /**
     * The DP device supports the function ActParam.<br>
     * Type: Boolean (1: TRUE)
     */
    private boolean _actParamSupp;

    /**
     * Maximum memory size (in bytes) that a DP device makes<br>
     * available for storing the master parameter set.<br>
     * Type: Unsigned32<br>
     *
     */
    private long _maxMpsLength;

    /**
     * Here, the maximum L_sdu length for all master-slave<br>
     * communication relations is specified.<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _maxLsduMS;

    /**
     * Here, the maximum L_sdu length for all master-master<br>
     * communication relations is specified.<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _maxLsduMM;

    /**
     * This value indicates how long DP master (Class 1) needs<br>
     * as a maximum for processing a master-master function.<br>
     * Time Base: 10 ms<br>
     * Type: Unsigned16<br>
     *
     */
    private short _minPollTimeout;

    /**
     * This value indicates how fast a DP master (Class 1),<br>
     * at a baudrate of 9.6 kBaud, is ready to receive again<br>
     * after sending a request message (refer to EN 50170<br>
     * Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _trdy9k6;

    /**
     * This value indicates how fast a DP master (Class 1),<br>
     * at a baudrate of 19.2 kBaud, is ready to receive again<br>
     * after sending a request message (refer to EN 50170<br>
     * Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _trdy19k2;

    /**
     * This value indicates how fast a DP master (Class 1),<br>
     * at a baudrate of 31.25 kBaud, is ready to receive again<br>
     * after sending a request message (refer to EN 50170<br>
     * Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _trdy31k25;

    /**
     * This value indicates how fast a DP master (Class 1),<br>
     * at a baudrate of 45.45 kBaud, is ready to receive again<br>
     * after sending a request message (refer to EN 50170<br>
     * Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _trdy45k45;

    /**
     * This value indicates how fast a DP master (Class 1),<br>
     * at a baudrate of 93.75 kBaud, is ready to receive again<br>
     * after sending a request message (refer to EN 50170<br>
     * Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _trdy93k75;

    /**
     * This value indicates how fast a DP master (Class 1),<br>
     * at a baudrate of 187.5 kBaud, is ready to receive again<br>
     * after sending a request message (refer to EN 50170<br>
     * Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _trdy187k5;

    /**
     * This value indicates how fast a DP master (Class 1),<br>
     * at a baudrate of 500 kBaud, is ready to receive again<br>
     * after sending a request message (refer to EN 50170<br>
     * Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _trdy500;

    /**
     * This value indicates how fast a DP master (Class 1),<br>
     * at a baudrate of 1.5 MBaud, is ready to receive again<br>
     * after sending a request message (refer to EN 50170<br>
     * Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _trdy1M5;

    /**
     * This value indicates how fast a DP master (Class 1),<br>
     * at a baudrate of 3 MBaud, is ready to receive again<br>
     * after sending a request message (refer to EN 50170<br>
     * Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _trdy3M;

    /**
     * This value indicates how fast a DP master (Class 1),<br>
     * at a baudrate of 6 MBaud, is ready to receive again<br>
     * after sending a request message (refer to EN 50170<br>
     * Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _trdy6M;

    /**
     * This value indicates how fast a DP master (Class 1),<br>
     * at a baudrate of 12 MBaud, is ready to receive again<br>
     * after sending a request message (refer to EN 50170<br>
     * Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _trdy12M;

    /**
     * This value specifies the modulator fading time (TQUI),<br>
     * (refer to EN 50170 Part 8-2 Section 8.8) at a baudrate<br>
     * 9.6 kBaud.<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _tqui9k6;

    /**
     * This value specifies the modulator fading time (TQUI),<br>
     * (refer to EN 50170 Part 8-2 Section 8.8) at a baudrate<br>
     * 19.2 kBaud.<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _tqui19k2;

    /**
     * This value specifies the modulator fading time (TQUI),<br>
     * (refer to EN 50170 Part 8-2 Section 8.8) at a baudrate<br>
     * 31.25 kBaud.<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _tqui31k25;

    /**
     * This value specifies the modulator fading time (TQUI),<br>
     * (refer to EN 50170 Part 8-2 Section 8.8) at a baudrate<br>
     * 45.45 kBaud.<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _tqui45k45;

    /**
     * This value specifies the modulator fading time (TQUI),<br>
     * (refer to EN 50170 Part 8-2 Section 8.8) at a baudrate<br>
     * 93.75 kBaud.<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _tqui93k75;

    /**
     * This value specifies the modulator fading time (TQUI),<br>
     * (refer to EN 50170 Part 8-2 Section 8.8) at a baudrate<br>
     * 187.5 kBaud.<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _tqui187k5;

    /**
     * This value specifies the modulator fading time (TQUI),<br>
     * (refer to EN 50170 Part 8-2 Section 8.8) at a baudrate<br>
     * 500 kBaud.<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _tqui500;

    /**
     * This value specifies the modulator fading time (TQUI),<br>
     * (refer to EN 50170 Part 8-2 Section 8.8) at a baudrate<br>
     * 1.5 MBaud.<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _tqui1M5;

    /**
     * This value specifies the modulator fading time (TQUI),<br>
     * (refer to EN 50170 Part 8-2 Section 8.8) at a baudrate<br>
     * 3 MBaud.<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _tqui3M;

    /**
     * This value specifies the modulator fading time (TQUI),<br>
     * (refer to EN 50170 Part 8-2 Section 8.8) at a baudrate<br>
     * 6 MBaud.<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _tqui6M;

    /**
     * This value specifies the modulator fading time (TQUI),<br>
     * (refer to EN 50170 Part 8-2 Section 8.8) at a baudrate<br>
     * 12 MBaud.<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _tqui12M;

    /**
     * This value speccifies the trigger time, at the baudrate<br>
     * 9.6 kBaud, in reference to layer2 (setup time) from the<br>
     * arrival of an event until the corresponding response (refer to EN 50170<br>
     * Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _tset9k6;

    /**
     * This value speccifies the trigger time, at the baudrate<br>
     * 19.2 kBaud, in reference to layer2 (setup time) from the<br>
     * arrival of an event until the corresponding response (refer to EN 50170<br>
     * Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _tset19k2;

    /**
     * This value speccifies the trigger time, at the baudrate<br>
     * 31.25 kBaud, in reference to layer2 (setup time) from the<br>
     * arrival of an event until the corresponding response (refer to EN 50170<br>
     * Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _tset31k25;

    /**
     * This value speccifies the trigger time, at the baudrate<br>
     * 45k45 kBaud, in reference to layer2 (setup time) from the<br>
     * arrival of an event until the corresponding response (refer to EN 50170<br>
     * Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _tset45k45;

    /**
     * This value speccifies the trigger time, at the baudrate<br>
     * 93.75 kBaud, in reference to layer2 (setup time) from the<br>
     * arrival of an event until the corresponding response (refer to EN 50170<br>
     * Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _tset93k75;

    /**
     * This value speccifies the trigger time, at the baudrate<br>
     * 9.6 kBaud, in reference to layer2 (setup time) from the<br>
     * arrival of an event until the corresponding response (refer to EN 50170<br>
     * Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _tset187k5;

    /**
     * This value speccifies the trigger time, at the baudrate<br>
     * 500 kBaud, in reference to layer2 (setup time) from the<br>
     * arrival of an event until the corresponding response (refer to EN 50170<br>
     * Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _tset500;

    /**
     * This value speccifies the trigger time, at the baudrate<br>
     * 15 MBaud, in reference to layer2 (setup time) from the<br>
     * arrival of an event until the corresponding response (refer to EN 50170<br>
     * Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _tset1M5;

    /**
     * This value speccifies the trigger time, at the baudrate<br>
     * 3 MBaud, in reference to layer2 (setup time) from the<br>
     * arrival of an event until the corresponding response (refer to EN 50170<br>
     * Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _tset3M;

    /**
     * This value speccifies the trigger time, at the baudrate<br>
     * 6 MBaud, in reference to layer2 (setup time) from the<br>
     * arrival of an event until the corresponding response (refer to EN 50170<br>
     * Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _tset6M;

    /**
     * This value speccifies the trigger time, at the baudrate<br>
     * 12 MBaud, in reference to layer2 (setup time) from the<br>
     * arrival of an event until the corresponding response (refer to EN 50170<br>
     * Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _tset12M;

    /**
     * This value indicates how many entries the device in question can<br>
     * can manage in the list of active stations (LAS).<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _lasLen;

    /**
     * This value specifies the station delay time (Tsdi) of the<br>
     * indicator (refer to EN 50170 Part 8-2 Section 8.8) at a<br>
     * baudrate of 9.6 kBaud.<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private short _tsdi9k6;

    /**
     * This value specifies the station delay time (Tsdi) of the<br>
     * indicator (refer to EN 50170 Part 8-2 Section 8.8) at a<br>
     * baudrate of 19.2 kBaud.<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private short _tsdi19k2;

    /**
     * This value specifies the station delay time (Tsdi) of the<br>
     * indicator (refer to EN 50170 Part 8-2 Section 8.8) at a<br>
     * baudrate of 31.25 kBaud.<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private short _tsdi31k25;

    /**
     * This value specifies the station delay time (Tsdi) of the<br>
     * indicator (refer to EN 50170 Part 8-2 Section 8.8) at a<br>
     * baudrate of 45.45 kBaud.<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private short _tsdi45k45;

    /**
     * This value specifies the station delay time (Tsdi) of the<br>
     * indicator (refer to EN 50170 Part 8-2 Section 8.8) at a<br>
     * baudrate of 93k75 kBaud.<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private short _tsdi93k75;

    /**
     * This value specifies the station delay time (Tsdi) of the<br>
     * indicator (refer to EN 50170 Part 8-2 Section 8.8) at a<br>
     * baudrate of 187.5 kBaud.<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private short _tsdi187k5;

    /**
     * This value specifies the station delay time (Tsdi) of the<br>
     * indicator (refer to EN 50170 Part 8-2 Section 8.8) at a<br>
     * baudrate of 500 kBaud.<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private short _tsdi500;

    /**
     * This value specifies the station delay time (Tsdi) of the<br>
     * indicator (refer to EN 50170 Part 8-2 Section 8.8) at a<br>
     * baudrate of 1.5 MBaud.<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private short _tsdi1M5;

    /**
     * This value specifies the station delay time (Tsdi) of the<br>
     * indicator (refer to EN 50170 Part 8-2 Section 8.8) at a<br>
     * baudrate of 3 MBaud.<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private short _tsdi3M;

    /**
     * This value specifies the station delay time (Tsdi) of the<br>
     * indicator (refer to EN 50170 Part 8-2 Section 8.8) at a<br>
     * baudrate of 6 MBaud.<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private short _tsdi6M;

    /**
     * This value specifies the station delay time (Tsdi) of the<br>
     * indicator (refer to EN 50170 Part 8-2 Section 8.8) at a<br>
     * baudrate of 12 MBaud.<br>
     * Time Base: Bit Time<br>
     * Type: Unsigned8<br>
     *
     */
    private short _tsdi12M;

    /**
     * This value indicates how many DP slave stations a<br>
     * DP master (Class1) can handle.<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _maxSlavesSupp;

    /**
     * here, the maximum length of input data per DP slave<br>
     * is specified that the DP master supports.<br>
     * Type: Unsigned8<br>
     *
     */
    private byte _maxMasterInputLen;

    /**
     *      here, the maximum length of output data per DP slave<br>
     is specified that the DP master supports.<br>
     Type: Unsigned8<br>

     */
    private byte _maxMasterOutputLen;

    /**
     *      Here, the largest sum of the length of the output and<br>
     input data per DP slave is specified that the DP master<br>
     supports.If this keyword is not provided, the maximum<br>
     length will be the sum of the input and output data.<br>
     Type: Unsigned16<br>

     */
    private short _maxMasterDataLen;


    // Methoden of class MASTER_KEYWORDS<br>
    public boolean isActParamBrctSupp() {
        return _actParamBrctSupp;
    }

    public void setActParamBrctSupp(final boolean actParamBrctSupp) {
        _actParamBrctSupp = actParamBrctSupp;
    }

    public boolean isActParamSupp() {
        return _actParamSupp;
    }

    public void setActParamSupp(final boolean actParamsupp) {
        _actParamSupp = actParamsupp;
    }

    public boolean isDownloadSupp() {
        return _downloadSupp;
    }

    public void setDownloadSupp(final boolean downloadSupp) {
        _downloadSupp = downloadSupp;
    }

    public byte getLasLen() {
        return _lasLen;
    }

    public void setLasLen(final byte len) {
        _lasLen = len;
    }

    public byte getMaxLsduMM() {
        return _maxLsduMM;
    }

    public void setMaxLsduMM(final byte maxLsduMM) {
        _maxLsduMM = maxLsduMM;
    }

    public byte getMaxLsduMS() {
        return _maxLsduMS;
    }

    public void setMaxLsduMS(final byte maxLsduMS) {
        _maxLsduMS = maxLsduMS;
    }

    public short getMaxMasterDataLen() {
        return _maxMasterDataLen;
    }

    public void setMaxMasterDataLen(final short maxMasterDataLen) {
        _maxMasterDataLen = maxMasterDataLen;
    }

    public byte getMaxMasterInputLen() {
        return _maxMasterInputLen;
    }

    public void setMaxMasterInputLen(final byte maxMasterInputLen) {
        _maxMasterInputLen = maxMasterInputLen;
    }

    public byte getMaxMasterOutputLen() {
        return _maxMasterOutputLen;
    }

    public void setMaxMasterOutputLen(final byte maxMasterOutputLen) {
        _maxMasterOutputLen = maxMasterOutputLen;
    }

    public long getMaxMpsLength() {
        return _maxMpsLength;
    }

    public void setMaxMpsLength(final long maxMpsLength) {
        _maxMpsLength = maxMpsLength;
    }

    public byte getMaxSlavesSupp() {
        return _maxSlavesSupp;
    }

    public void setMaxSlavesSupp(final byte maxSlavesSupp) {
        _maxSlavesSupp = maxSlavesSupp;
    }

    public short getMinPollTimeout() {
        return _minPollTimeout;
    }

    public void setMinPollTimeout(final short minPollTimeout) {
        _minPollTimeout = minPollTimeout;
    }

    public byte getTqui1M5() {
        return _tqui1M5;
    }

    public void setTqui1M5(final byte tqui1M5) {
        _tqui1M5 = tqui1M5;
    }

    public byte getTqui12M() {
        return _tqui12M;
    }

    public void setTqui12M(final byte tqui12M) {
        _tqui12M = tqui12M;
    }

    public byte getTqui187k5() {
        return _tqui187k5;
    }

    public void setTqui187k5(final byte tqui187k5) {
        _tqui187k5 = tqui187k5;
    }

    public byte getTqui19k2() {
        return _tqui19k2;
    }

    public void setTqui19k2(final byte tqui19k2) {
        _tqui19k2 = tqui19k2;
    }

    public byte getTqui31k25() {
        return _tqui31k25;
    }

    public void setTqui31k25(final byte tqui31k25) {
        _tqui31k25 = tqui31k25;
    }

    public byte getTqui3M() {
        return _tqui3M;
    }

    public void setTqui3M(final byte tqui3M) {
        _tqui3M = tqui3M;
    }

    public byte getTqui45k45() {
        return _tqui45k45;
    }

    public void setTqui45k45(final byte tqui45k45) {
        _tqui45k45 = tqui45k45;
    }

    public byte getTqui500() {
        return _tqui500;
    }

    public void setTqui500(final byte tqui500) {
        _tqui500 = tqui500;
    }

    public byte getTqui6M() {
        return _tqui6M;
    }

    public void setTqui6M(final byte tqui6M) {
        _tqui6M = tqui6M;
    }

    public byte getTqui9k6() {
        return _tqui9k6;
    }

    public void setTqui9k6(final byte tqui9k6) {
        _tqui9k6 = tqui9k6;
    }

    public byte getTqui93k75() {
        return _tqui93k75;
    }

    public void setTqui93k75(final byte tqui93k75) {
        _tqui93k75 = tqui93k75;
    }

    public byte getTrdy1M5() {
        return _trdy1M5;
    }

    public void setTrdy1M5(final byte trdy1M5) {
        _trdy1M5 = trdy1M5;
    }

    public byte getTrdy12M() {
        return _trdy12M;
    }

    public void setTrdy12M(final byte trdy12M) {
        _trdy12M = trdy12M;
    }

    public byte getTrdy187k5() {
        return _trdy187k5;
    }

    public void setTrdy187k5(final byte trdy187k5) {
        _trdy187k5 = trdy187k5;
    }

    public byte getTrdy19k2() {
        return _trdy19k2;
    }

    public void setTrdy19k2(final byte trdy19k2) {
        _trdy19k2 = trdy19k2;
    }

    public byte getTrdy31k25() {
        return _trdy31k25;
    }

    public void setTrdy31k25(final byte trdy31k25) {
        _trdy31k25 = trdy31k25;
    }

    public byte getTrdy3M() {
        return _trdy3M;
    }

    public void setTrdy3M(final byte trdy3M) {
        _trdy3M = trdy3M;
    }

    public byte getTrdy45k45() {
        return _trdy45k45;
    }

    public void setTrdy45k45(final byte trdy45k45) {
        _trdy45k45 = trdy45k45;
    }

    public byte getTrdy500() {
        return _trdy500;
    }

    public void setTrdy500(final byte trdy500) {
        _trdy500 = trdy500;
    }

    public byte getTrdy6M() {
        return _trdy6M;
    }

    public void setTrdy6M(final byte trdy6M) {
        _trdy6M = trdy6M;
    }

    public byte getTrdy9k6() {
        return _trdy9k6;
    }

    public void setTrdy9k6(final byte trdy9k6) {
        _trdy9k6 = trdy9k6;
    }

    public byte getTrdy93k75() {
        return _trdy93k75;
    }

    public void setTrdy93k75(final byte trdy93k75) {
        _trdy93k75 = trdy93k75;
    }

    public short getTsdi1M5() {
        return _tsdi1M5;
    }

    public void setTsdi1M5(final short tsdi1M5) {
        _tsdi1M5 = tsdi1M5;
    }

    public short getTsdi12M() {
        return _tsdi12M;
    }

    public void setTsdi12M(final short tsdi12M) {
        _tsdi12M = tsdi12M;
    }

    public short getTsdi187k5() {
        return _tsdi187k5;
    }

    public void setTsdi187k5(final short tsdi187k5) {
        _tsdi187k5 = tsdi187k5;
    }

    public short getTsdi19k2() {
        return _tsdi19k2;
    }

    public void setTsdi19k2(final short tsdi19k2) {
        _tsdi19k2 = tsdi19k2;
    }

    public short getTsdi31k25() {
        return _tsdi31k25;
    }

    public void setTsdi31k25(final short tsdi31k25) {
        _tsdi31k25 = tsdi31k25;
    }

    public short getTsdi3M() {
        return _tsdi3M;
    }

    public void setTsdi3M(final short tsdi3M) {
        _tsdi3M = tsdi3M;
    }

    public short getTsdi45k45() {
        return _tsdi45k45;
    }

    public void setTsdi45k45(final short tsdi45k45) {
        _tsdi45k45 = tsdi45k45;
    }

    public short getTsdi500() {
        return _tsdi500;
    }

    public void setTsdi500(final short tsdi500) {
        _tsdi500 = tsdi500;
    }

    public short getTsdi6M() {
        return _tsdi6M;
    }

    public void setTsdi6M(final short tsdi6M) {
        _tsdi6M = tsdi6M;
    }

    public short getTsdi9k6() {
        return _tsdi9k6;
    }

    public void setTsdi9k6(final short tsdi9k6) {
        _tsdi9k6 = tsdi9k6;
    }

    public short getTsdi93k75() {
        return _tsdi93k75;
    }

    public void setTsdi93k75(final short tsdi93k75) {
        _tsdi93k75 = tsdi93k75;
    }

    public byte getTset1M5() {
        return _tset1M5;
    }

    public void setTset1M5(final byte tset1M5) {
        _tset1M5 = tset1M5;
    }

    public byte getTset12M() {
        return _tset12M;
    }

    public void setTset12M(final byte tset12M) {
        _tset12M = tset12M;
    }

    public byte getTset187k5() {
        return _tset187k5;
    }

    public void setTset187k5(final byte tset187k5) {
        _tset187k5 = tset187k5;
    }

    public byte getTset19k2() {
        return _tset19k2;
    }

    public void setTset19k2(final byte tset19k2) {
        _tset19k2 = tset19k2;
    }

    public byte getTset31k25() {
        return _tset31k25;
    }

    public void setTset31k25(final byte tset31k25) {
        _tset31k25 = tset31k25;
    }

    public byte getTset3M() {
        return _tset3M;
    }

    public void setTset3M(final byte tset3M) {
        _tset3M = tset3M;
    }

    public byte getTset45k45() {
        return _tset45k45;
    }

    public void setTset45k45(final byte tset45k45) {
        _tset45k45 = tset45k45;
    }

    public byte getTset500() {
        return _tset500;
    }

    public void setTset500(final byte tset500) {
        _tset500 = tset500;
    }

    public byte getTset6M() {
        return _tset6M;
    }

    public void setTset6M(final byte tset6M) {
        _tset6M = tset6M;
    }

    public byte getTset9k6() {
        return _tset9k6;
    }

    public void setTset9k6(final byte tset9k6) {
        _tset9k6 = tset9k6;
    }

    public byte getTset93k75() {
        return _tset93k75;
    }

    public void setTset93k75(final byte tset93k75) {
        _tset93k75 = tset93k75;
    }

    public boolean isUploadSupp() {
        return _uploadSupp;
    }

    public void setUploadSupp(final boolean uploadSupp) {
        _uploadSupp = uploadSupp;
    }

    /* (non-Javadoc)
     * @see org.csstudio.config.ioconfig.model.Keywords#getType()
     */
    public GSDFileTypes getType() {
        return GSDFileTypes.Master;
    }
}
