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
 * $Id: GsdGeneralModel.java,v 1.1 2009/08/26 07:08:42 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import org.csstudio.config.ioconfig.model.Keywords;

/**
 * Data model for GSD Syntax General Keywords
 *  
 * This class contains the general GSD definitions. The definitions are given
 * by the Profibus Nutzer. 
 * 
 * 
 * @author Torsten Boeckmann
 * Date: 08. Dezember 2005
 * @author $Author: hrickens $
 * @version $Revision: 1.1 $
 * @since 18.07.2008
 */
// TODO (hrickens) [03.05.2011]: Raus damit!
abstract class GsdGeneralModel implements Keywords{

    /**
     * Version ID of the GSD file format.<br>
     * Type: Unsigned8.
     */
    private byte _gsdRevision;

    /**
     * Manufactures's name.<br>
     * Type: Visible-String(32)
     */
    private String _vendorName;

    /**
     * Manufactuer's designation (controller type) of DP device.<br>
     * Type: Visible-String(32);
     */
    private String _modelName;

    /**
     * Revision version of DP device.<br>
     * Type: Visible-String(32)
     */
    private String _revision;

    /**
     * Version ID of DP device. The value of the RevisionNumber has to agree
     * with the value of the RevisionNumber in slave-specific diagnosis.<br>
     * Type: Unsigned8(1-63)
     */
    private byte _revisionNumber;

    /**
     * Device type of the DP device.<br>
     * Type: Unsigned16
     */
    private int _identNumber;

    /**
     * Protocol ID od the DP device.<br>
     * 0: Profibus DP 16 to 255 Manufacturer-specific<br>
     * Type: Unsigned8
     */
    private byte _protocolIdent;

    /**
     * DP device type.<br>
     * 0: DP-Slave<br>
     * 1: DP-Master (class 1)<br>
     * Type: Unsigned8
     */
    private byte _stationType;

    /**
     * This device is a FMS/DP mixed device.<br>
     * Type: Boolean (1: TRUE)
     */
    private boolean _fmsSupp;

    /**
     * Hardware release of the DP device.<br>
     * Type: Visible-String(32)
     */
    private String _hardwareRelease;

    /**
     * Software release of the DP device.<br>
     * Type: Visible-String(32)
     */
    private String _softwareRelease;

    /**
     * The DP device support the baudrate 9.6 kBaud.<br>
     * Type: Boolean (1: True)
     */
    private boolean _supp9k6;

    /**
     * The DP device support the baudrate 19.2 kBaud.<br>
     * Type: Boolean (1: True)
     */
    private boolean _supp19k2;

    /**
     * The DP device support the baudrate 31.25 kBaud.<br>
     * Type: Boolean (1: True)
     */
    private boolean _supp31k25;

    /**
     * The DP device support the baudrate 45.45 kBaud.<br>
     * Type: Boolean (1: True)
     */
    private boolean _supp45k45;

    /**
     * The DP device support the baudrate 93.75 kBaud.<br>
     * Type: Boolean (1: True)
     */
    private boolean _supp93k75;

    /**
     * The DP device support the baudrate 187.5 kBaud.<br>
     * Type: Boolean (1: True)
     */
    private boolean _supp187k5;

    /**
     * The DP device support the baudrate 500 kBaud.<br>
     * Type: Boolean (1: True)
     */
    private boolean _supp500;

    /**
     * The DP device support the baudrate 1.5 MBaud.<br>
     * Type: Boolean (1: True)
     */
    private boolean _supp1M5;

    /**
     * The DP device support the baudrate 3 MBaud.<br>
     * Type: Boolean (1: True)
     */
    private boolean _supp3M;

    /**
     * The DP device support the baudrate 6 MBaud.<br>
     * Type: Boolean (1: True)
     */
    private boolean _supp6M;

    /**
     * The DP device support the baudrate 12 MBaud.<br>
     * Type: Boolean (1: True)
     */
    private boolean _supp12M;

    /**
     * This is the time a responder needs as maximum at at a baudrate of 9.6
     * kBaud to respond to a request message (refer to EN50170 Part 8-2 Section
     * 8.8).<br>
     * Time Base: Bit Time.<br>
     * Type: Unsigned16.<br>
     */
    private short _maxTsdr9k6;

    /**
     * This is the time a responder needs as maximum at a baudrate of 19.2 kBaud
     * to respond to a request message (refer to EN50170 Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time.<br>
     * Type: Unsigned16.<br>
     */
    private short _maxTsdr19k2;

    /**
     * This is the time a responder needs as maximum at at a baudrate of 31.25
     * kBaud to respond to a request message (refer to EN50170 Part 8-2 Section
     * 8.8).<br>
     * Time Base: Bit Time.<br>
     * Type: Unsigned16.<br>
     */
    private short _maxTsdr31k25;

    /**
     * This is the time a responder needs as maximum at a baudrate of 45.45
     * kBaud to respond to a request message (refer to EN50170 Part 8-2 Section
     * 8.8).<br>
     * Time Base: Bit Time.<br>
     * Type: Unsigned16.<br>
     */
    private short _maxTsdr45k45;

    /**
     * This is the time a responder needs as maximum at a baudrate of 93.75
     * kBaud to respond to a request message (refer to EN50170 Part 8-2 Section
     * 8.8).<br>
     * Time Base: Bit Time.<br>
     * Type: Unsigned16.<br>
     */
    private short _maxTsdr93k75;

    /**
     * This is the time a responder needs as maximum at a baudrate of 187.5
     * kBaud to respond to a request message (refer to EN50170 Part 8-2 Section
     * 8.8).<br>
     * Time Base: Bit Time.<br>
     * Type: Unsigned16.<br>
     */
    private short _maxTsdr187k5;

    /**
     * This is the time a responder needs as maximum at a baudrate of 500 kBaud
     * to respond to a request message (refer to EN50170 Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time.<br>
     * Type: Unsigned16.<br>
     */
    private short _maxTsdr500;

    /**
     * This is the time a responder needs as maximum at a baudrate of 1.5 MBaud
     * to respond to a request message (refer to EN50170 Part 8-2 Section 8.8.).<br>
     * Time Base: Bit Time.<br>
     * Type: Unsigned16.<br>
     * 
     */
    private short _maxTsdr1M5;

    /**
     * This is the time a responder needs as maximum at at a baudrate of 3 MBaud
     * to respond to a request message (refer to EN50170 Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time.<br>
     * Type: Unsigned16.<br>
     */
    private short _maxTsdr3M;

    /**
     * This is the time a responder needs as maximum at at a baudrate of 6 MBaud
     * to respond to a request message (refer to EN50170 Part 8-2 Section 8.8).<br>
     * Time Base: Bit Time.<br>
     * Type: Unsigned16.<br>
     */
    private short _maxTsdr6M;

    /**
     * This is the time a responder needs as maximum at at a baudrate of 12
     * MBaud to respond to a request message (refer to EN50170 Part 8-2 Section
     * 8.8).<br>
     * Time Base: Bit Time.<br>
     * Type: Unsigned16.<br>
     */
    private short _maxTsdr12M;

    /**
     * This value specifies whether a device support redundant transmission
     * engineering.<br>
     * 0: No<br>
     * 1: Redundancy is supported<br>
     * Type: Boolean.<br>
     */
    private boolean _redundancy;

    /**
     * Here, the level of the connector signal CNTR-P is specified.<br>
     * 0: Not connected<br>
     * 1: RS485<br>
     * 2: TTL<br>
     * Type: Unsigned16<br>
     */
    private byte _repeaterCtrlSig;

    /**
     * Here, the meaning of the connector signal M24V and P24V is specified.<br>
     * 0: Not connected<br>
     * 1: Input<br>
     * 2: Output<br>
     * Type: Unsigned8<br>
     */
    private byte _pins24V;

    /**
     * Here, a description is provide which standard implementation is used in
     * the DP slave; for example, Standard Software, Controller or ASIC
     * solution. The manufacturer ot standard solution provides the name.<br>
     * Type: Visible-String(32)<br>
     */
    private String _implementationType;

    /**
     * Here, the file name (*DIB) of the bit map file is specified in the
     * DIB-Format (70*40 pixel (width * height) 16 color) that contains the
     * symbolic representation of the device for standard cases.<br>
     * Type: Visible-String<br>
     */
    private String _bitmapDevice;

    /**
     * Here, the file name (*DIB) of the bit map file is specified in the
     * DIB-Format (70*40 pixel (width * height) 16 color) that contains the
     * symbolic representation of the device for diagnostic cases.<br>
     * Type: Visible-String<br>
     */
    private String _bitmapDiag;

    /**
     * Here, the file name (*DIB) of the bit map file is specified in the
     * DIB-Format (70*40 pixel (width * height) 16 color) that contains the
     * symbolic representation of the device in special operating modes. The
     * meaning is manufactures-specific.<br>
     * Type: Visible-String<br>
     */
    private String _bitmapSF;

    // Methoden of class GsdGeneralModel

    /**
     * 
     * @return if only true is 1.5 MBaud support available. 
     */
    public final boolean isSupp1M5() {
        return _supp1M5;
    }

    /**
     * The DP device support the baudrate 1.5 MBaud.<br>
     * Type: Boolean (1: True)
     * @param supp1M5 set is 1.5 MBaud support available  
     */
    public final void setSupp1M5(final boolean supp1M5) {
        _supp1M5 = supp1M5;
    }

    /**
     * 
     * @return if only true is 12 MBaud support available. 
     */
    public final boolean isSupp12M() {
        return _supp12M;
    }

    /**
     * The DP device support the baudrate 12 MBaud.<br>
     * Type: Boolean (1: True)
     * @param supp12M set is 12 MBaud support available  
     */
    public final void setSupp12M(final boolean supp12M) {
        _supp12M = supp12M;
    }

     /**
     * @return If true the DP device support the baudrate 187,5 kBaud.<br>
     */
     public final boolean isSupp187k5() {
        return _supp187k5;
    }

    /**
     * The DP device support the baudrate 187,5 kBaud.<br>
     * Type: Boolean (1: True)
     * @param supp187k5 set is 187,5 kBaud support available  
     */
    public final void setSupp187k5(final boolean supp187k5) {
        this._supp187k5 = supp187k5;
    }
	
	/**
     * @return If true the DP device support the baudrate 19,2 kBaud.<br>
     */
    public final boolean isSupp19k2() {
        return _supp19k2;
    }

    /**
     * The DP device support the baudrate 19,2 kBaud.<br>
     * Type: Boolean (1: True)
     * @param supp19k2 set is 19,2 kBaud support available  
     */
    public final void setSupp19k2(final boolean supp19k2) {
        this._supp19k2 = supp19k2;
    }

	/**
     * Here, the meaning of the connector signal M24V and P24V is specified.<br>
     * 0: Not connected<br>
     * 1: Input<br>
     * 2: Output<br>
     * Type: Unsigned8<br>
     * @return The connector Pin24V specified.
     */
    public final byte getPins24V() {
        return _pins24V;
    }
	
	/**
     * Here, the meaning of the connector signal M24V and P24V is specified.<br>
     * 0: Not connected<br>
     * 1: Input<br>
     * 2: Output<br>
     * Type: Unsigned8<br>
     * @param pins Set the connector Pin24V specified.
     */
    public final void setPins24V(final byte pins) {
        _pins24V = pins;
    }

	/**
     * @return If true the DP device support the baudrate 31.25 kBaud.<br>
     */
    public final boolean isSupp31k25() {
        return _supp31k25;
    }

    /**
     * The DP device support the baudrate 31.25 kBaud.<br>
     * Type: Boolean (1: True)
     * @param supp31k25 set is 31.25 kBaud support available  
     */
    public final void setSupp31k25(final boolean supp31k25) {
        _supp31k25 = supp31k25;
    }

	/**
     * @return If true the DP device support the baudrate 3 MBaud.<br>
     */
    public final boolean isSupp3M() {
        return _supp3M;
    }
	
	/**
     * @param supp3M set true then the DP device support the baudrate 3 MBaud.<br>
     */
    public final void setSupp3M(final boolean supp3M) {
        _supp3M = supp3M;
    }

    /**
     * @return If true the DP device support the baudrate 45.45 kBaud.<br>
     */
    public final boolean isSupp45k45() {
        return _supp45k45;
    }
    /**
     * The DP device support the baudrate 45.45 kBaud.<br>
     * Type: Boolean (1: True)
     * @param supp45k45 set is 45.45 kBaud support available  
     */
    public final void setSupp45k45(final boolean supp45k45) {
        _supp45k45 = supp45k45;
    }
	
	/**
     * @return If true the DP device support the baudrate 500 Baud.<br>
     */
    public final boolean isSupp500() {
        return _supp500;
    }

    /**
     * @param supp500 set true then the DP device support the baudrate 500 Baud.<br>
     */
    public final void setSupp500(final boolean supp500) {
        this._supp500 = supp500;
    }

    /**
     * @return If true the DP device support the baudrate 6 MBaud.<br>
     */
     public final boolean isSupp6M() {
        return _supp6M;
    }

	/**
     * @param supp6M set true then the DP device support the baudrate 6 MBaud.<br>
     */
    public final void setSupp6M(final boolean supp6M) {
        _supp6M = supp6M;
    }

	/**
     * @return If true the DP device support the baudrate 9.6 kBaud.<br>
     */
    public final boolean isSupp9k6() {
        return _supp9k6;
    }
	
	/**
     * @param supp9k6 set true then the DP device support the baudrate 9.6 kBaud.<br>
     */
    public final void setSupp9k6(final boolean supp9k6) {
        _supp9k6 = supp9k6;
    }

    /**
     * @return If true the DP device support the baudrate 93.75 kBaud.<br>
     */
    public final boolean isSupp93k75() {
        return _supp93k75;
    }
	
	/**
     * @param supp93k75 set true then the DP device support the baudrate 93.75 kBaud.<br>
     */
    public final void setSupp93k75(final boolean supp93k75) {
        _supp93k75 = supp93k75;
    }

    /**
     * Here, the file name (*DIB) of the bit map file is specified in the
     * DIB-Format (70*40 pixel (width * height) 16 color) that contains the
     * symbolic representation of the device for standard cases.<br>
     * @return the File Name.
     */
    public final String getBitmapDevice() {
        return _bitmapDevice;
    }

    /**
     * Here, the file name (*DIB) of the bit map file is specified in the
     * DIB-Format (70*40 pixel (width * height) 16 color) that contains the
     * symbolic representation of the device for standard cases.<br>
     * @param bitmapDevice Set the File name.
     */
    public final void setBitmapDevice(final String bitmapDevice) {
        _bitmapDevice = bitmapDevice;
    }

    /**
     * Here, the file name (*DIB) of the bit map file is specified in the
     * DIB-Format (70*40 pixel (width * height) 16 color) that contains the
     * symbolic representation of the device for diagnostic cases.<br>
     * 
     * @return The File name.
     */
    public final String getBitmapDiag() {
        return _bitmapDiag;
    }

    /**
     * Here, the file name (*DIB) of the bit map file is specified in the
     * DIB-Format (70*40 pixel (width * height) 16 color) that contains the
     * symbolic representation of the device for diagnostic cases.<br>
     * 
     * @param bitmapDiag Set the File name.
     */
    public final void setBitmapDiag(final String bitmapDiag) {
        _bitmapDiag = bitmapDiag;
    }

    /**
     * Here, the file name (*DIB) of the bit map file is specified in the
     * DIB-Format (70*40 pixel (width * height) 16 color) that contains the
     * symbolic representation of the device in special operating modes. The
     * meaning is manufactures-specific.<br>
     * Type: Visible-String<br>
     * 
     * @return The Bitmap File name.
     */
    public final String getBitmapSF() {
        return _bitmapSF;
    }

    /**
     * Here, the file name (*DIB) of the bit map file is specified in the
     * DIB-Format (70*40 pixel (width * height) 16 color) that contains the
     * symbolic representation of the device in special operating modes. The
     * meaning is manufactures-specific.<br>
     * Type: Visible-String<br>
     * 
     * @param bitmapSF Set the Bitmap File name.
     */
    public final void setBitmapSF(final String bitmapSF) {
        _bitmapSF = bitmapSF;
    }

    /**
     * @return Is true this device is a FMS/DP mixed device.<br>
     */
    public final boolean isFmsSupp() {
        return _fmsSupp;
    }

    /**
     * @param fmsSupp Set true, this device is a FMS/DP mixed device.<br>
     */
    public final void setFmsSupp(final boolean fmsSupp) {
        _fmsSupp = fmsSupp;
    }

    /**
     * 
     * @return Version ID of the GSD file format.
     */
    public final byte getGsdRevision() {
        return _gsdRevision;
    }

    /**
     * 
     * @param revision set the Version ID of the GSD file format.
     */
    public final void setGsdRevision(final byte revision) {
        _gsdRevision = revision;
    }

    /**
     * @return The Hardware release of the DP device.<br>
     */
    public final String getHardwareRelease() {
        return _hardwareRelease;
    }

    /**
     * @param hardwareRelease Set the Hardware release of the DP device.<br>
     */
    public final void setHardwareRelease(final String hardwareRelease) {
        _hardwareRelease = hardwareRelease;
    }

//    public final int getIdentNumber() {
//        return _identNumber;
//    }

    public final void setIdentNumber(final int identNumber) {
        _identNumber = identNumber;
    }

    public final String getImplementationType() {
        return _implementationType;
    }

    public final void setImplementationType(final String implementationType) {
        _implementationType = implementationType;
    }

    public final short getMaxTsdr1M5() {
        return _maxTsdr1M5;
    }

    public final void setMaxTsdr1M5(final short maxTsdr1M5) {
        _maxTsdr1M5 = maxTsdr1M5;
    }

    public final short getMaxTsdr12M() {
        return _maxTsdr12M;
    }

    public final void setMaxTsdr12M(final short maxTsdr12M) {
        _maxTsdr12M = maxTsdr12M;
    }

    public final short getMaxTsdr187k5() {
        return _maxTsdr187k5;
    }

    public final void setMaxTsdr187k5(final short maxTsdr187k5) {
        _maxTsdr187k5 = maxTsdr187k5;
    }

    public final short getMaxTsdr3M() {
        return _maxTsdr3M;
    }

    public final void setMaxTsdr3M(final short maxTsdr3M) {
        _maxTsdr3M = maxTsdr3M;
    }

    public final short getMaxTsdr31k25() {
        return _maxTsdr31k25;
    }

    public final void setMaxTsdr31k25(final short maxTsdr31k25) {
        _maxTsdr31k25 = maxTsdr31k25;
    }

    public final short getMaxTsdr45k45() {
        return _maxTsdr45k45;
    }

    public final void setMaxTsdr45k45(final short maxTsdr45k45) {
        _maxTsdr45k45 = maxTsdr45k45;
    }

    public final short getMaxTsdr500() {
        return _maxTsdr500;
    }

    public final void setMaxTsdr500(final short maxTsdr500) {
        _maxTsdr500 = maxTsdr500;
    }

    public final short getMaxTsdr6M() {
        return _maxTsdr6M;
    }

    public final void setMaxTsdr6M(final short maxTsdr6M) {
        _maxTsdr6M = maxTsdr6M;
    }

    public final short getMaxTsdr9k6() {
        return _maxTsdr9k6;
    }

    public final void setMaxTsdr9k6(final short maxTsdr9k6) {
        _maxTsdr9k6 = maxTsdr9k6;
    }

    public final short getMaxTsdr19k2() {
        return _maxTsdr19k2;
    }

    public final void setMaxTsdr19k2(final short maxTsdr19k2) {
        _maxTsdr19k2 = maxTsdr19k2;
    }

    public final short getMaxTsdr93k75() {
        return _maxTsdr93k75;
    }

    public final void setMaxTsdr93k75(final short maxTsdr93k75) {
        _maxTsdr93k75 = maxTsdr93k75;
    }

    public final String getModelName() {
        return _modelName;
    }

    public final void setModelName(final String modelName) {
        _modelName = modelName;
    }

    public final byte getProtocolIdent() {
        return _protocolIdent;
    }

    public final void setProtocolIdent(final byte protocolIdent) {
        _protocolIdent = protocolIdent;
    }

    public final boolean isRedundancy() {
        return _redundancy;
    }

    public final void setRedundancy(final boolean redundancy) {
        _redundancy = redundancy;
    }

    public final byte getRepeaterCtrlSig() {
        return _repeaterCtrlSig;
    }

    public final void setRepeaterCtrlSig(final byte repeaterCtrlSig) {
        _repeaterCtrlSig = repeaterCtrlSig;
    }

    public final String getRevision() {
        return _revision;
    }

    public final void setRevision(final String revision) {
        _revision = revision;
    }

    public final byte getRevisionNumber() {
        return _revisionNumber;
    }

    public final void setRevisionNumber(final byte revisionNumber) {
        _revisionNumber = revisionNumber;
    }

    public final String getSoftwareRelease() {
        return _softwareRelease;
    }

    public final void setSoftwareRelease(final String softwareRelease) {
        _softwareRelease = softwareRelease;
    }

    public final byte getStationType() {
        return _stationType;
    }

    public final void setStationType(final byte stationType) {
        _stationType = stationType;
    }

    /**
     * 
     * @return Manufactures's name.
     */
    public final String getVendorName() {
        return _vendorName;
    }

    /**
     * 
     * @param vendorName set the Manufactures's name.
     */
    public final void setVendorName(final String vendorName) {
        _vendorName = vendorName;
    }
}
