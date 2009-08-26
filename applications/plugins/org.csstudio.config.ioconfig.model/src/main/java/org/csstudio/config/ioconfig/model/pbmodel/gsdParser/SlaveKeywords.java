package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import org.csstudio.config.ioconfig.model.Keywords;

/*******************************************************************************
 * Data model for GSD Syntax Slave-related Keywords *
 * ********************************************************** Created by:
 * Torsten Boeckmann * Date: 08. Dezember 2005 *
 * ********************************************************** This class
 * contains the master related GSD definitions. * The definitions are given by
 * the Profibus Nutzer * Organisation. *
 * ********************************************************** * last changed:
 * 16. February 2006 * Grounds of changed: Bug in data format User_Prm_ *
 * Data_Len * Revision: * Status: untested * Precondition: * Postcondition: * *
 ******************************************************************************/
public class SlaveKeywords implements Keywords {

    /**
     * The DP device supports the Freeze mode. DP slaves that support Freeze
     * mode have to guarantee that in the next data cycle after the Freeze
     * control command, the value of the inputs that were frozen last are
     * transferred to the bus. Type: Boolean (1: TRUE)
     */
    private boolean _freezeModeSupp;

    /**
     * The DP device supports the Sync mode. Type: Boolean (1: TRUE)
     */
    private boolean _syncModeSupp;

    /**
     * The DP device supports automatic baudrate recognition. Type: Boolean (1:
     * TRUE)
     */
    private boolean _autoBaudSupp;

    /**
     * The DP device supports the function Set_Slave_Add. Type: Boolean (1:
     * TRUE)
     */
    private boolean _setSlaveAddSupp;

    /**
     * Here, the length of _userPrmData is specified. Type: Unsigned8
     */
    private byte _userPrmDataLen;

    /**
     * Manufactures-specific field. Specifies the default valuefor _userPrmData
     * (refer to Section 2.3.6 Examples of GSD File Entries). If this parameter
     * is used, its length has to agree with the _userPrmDataLen. Type:
     * Octet-String
     */
    private String _userPrmData;

    /**
     * This time specifies the minimum interval between two slave list cycles
     * for the DP device. Time Base: _bit Time Type: Unsigned16
     */
    private short _minSlaveIntervall;

    /**
     * Here it is specified whether the DP device is a modular Station. 0:
     * compact device 1: modular device
     */
    private boolean _modularStation;

    /**
     * Here, the maximum number of modules of a modular station. Type: Unsigned8
     */
    private byte _maxModule;

    /**
     * Here, the maximum length of the inpput data of modular station specified
     * in bytes. Type: Unsigned8
     */
    private byte _maxmInputLen;

    /**
     * Here, the maximum length of the output data of modular station specified
     * in bytes. Type: Unsigned8
     */
    private byte _maxOutputLen;

    /**
     * Here, the largest sum of the length of the output and input data of a
     * modular station is specified in bytes. If this key word is not provided,
     * the maximum length is the sum of all input and output data. Type:
     * Unsigned16
     */
    private short _maxDataLen;

    /**
     * 
     * @author tboeckmanm
     * @author $Author$
     * @version $Revision$
     * @since 08.12.2005
     * 
     * In order to display manufacturer-specific status- and error messages of a
     * DP slave centrally, it is possible to assign to a bit a text (_diagText)
     * in the device-related diagnostic field (refer to Section 2.3.6 Examples
     * of GSD File Entries).
     */
    public class UnitDiagBit {
        /**
         * Bit position in device-related diagostic field (LSB in first byte is
         * Bit 0). Type: Unsigned16
         */
        private short _bit;

        /**
         * Diagnostic text. Type: Visible-String(32)
         */
        private String _diagText;

        // Methoden of class UnitDiagBit

        /**
         * @return the Bit position in device-related diagostic field (LSB in
         *         first byte is Bit 0). Type: Unsigned16
         */
        public final short getBit() {
            return _bit;
        }

        /**
         * @param bit
         *            Set the Bit position in device-related diagostic field
         *            (LSB in first byte is Bit 0). Type: Unsigned16
         */
        public final void setBit(final short bit) {
            _bit = bit;
        }

        /**
         * @return the Diagnostic text.
         */
        public final String getDiagText() {
            return _diagText;
        }

        /**
         * @param diagText
         *            set the Diagnostic text.
         */
        public final void setDiagText(final String diagText) {
            _diagText = diagText;
        }
    }

    /**
     * 
     * @author tboeckmann
     * @author $Author$
     * @version $Revision$
     * @since 05.12.2005
     * 
     * Between the key words UnitDiagArea and Unit_Diag_Area_End the assignment
     * of values in a bit field in the device-related diagnostic field to texts
     * (_diagText) is specified (refer to Section 0).
     */
    public class UnitDiagArea {

        /**
         * First bit position of the bit field (LSB in the first byte is Bit 0).
         * Type: Unsigned16
         */
        private short _firstBit;

        /**
         * Last bit position of the bit field. The bit field may be 16 bit wide
         * maximum. Type: Unsigned16
         */
        private short _lastBit;

        /**
         * _value in bit field. Type: Unsigned16 _diagText: Type:
         * Visible-String(32)
         */
        private String _value;

        // Methoden of class UnitDiagArea
        /**
         * First bit position of the bit field (LSB in the first byte is Bit 0).
         * Type: Unsigned16
         * 
         * @return the first bit
         */
        public final short getFirstBit() {
            return _firstBit;
        }

        /**
         * First bit position of the bit field (LSB in the first byte is Bit 0).
         * Type: Unsigned16
         * 
         * @param firstBit
         *            set the first byte
         */
        public final void setFirstBit(final short firstBit) {
            _firstBit = firstBit;
        }

        /**
         * Last bit position of the bit field. The bit field may be 16 bit wide
         * maximum. Type: Unsigned16
         * 
         * @return the last Bit
         */
        public final short getLastBit() {
            return _lastBit;
        }

        /**
         * Last bit position of the bit field. The bit field may be 16 bit wide
         * maximum. Type: Unsigned16
         * 
         * @param lastBit
         *            set the last bit
         */
        public final void setLastBit(final short lastBit) {
            _lastBit = lastBit;
        }

        /**
         * The Value in bit field. Type: Unsigned16 _diagText: Type:
         * Visible-String(32)
         * 
         * @return the Value
         */
        public final String getValue() {
            return _value;
        }

        /**
         * Value in bit field. Type: Unsigned16 _diagText: Type:
         * Visible-String(32)
         * 
         * @param value
         *            set the Value
         */
        public final void setValue(final String value) {
            _value = value;
        }
    }

    /**
     * 
     * @author tboeckmann
     * @author $Author$
     * @version $Revision$
     * @since 08.12.2005
     * 
     * Between the key words Module and EndModule, the IDs of a DP compact
     * device or the IDs of all possible modules of a modular slave are
     * specified, manufacturer-specific error types are specified in the
     * channel-related diagnostic field, and the _userPrmData is described. If,
     * in the case of modular slaves, empty slots are to be defined as empty
     * module (ID/s 0x00), the empty module has to be defined. Otherwise, empty
     * slots would not appear in the configuration data. If the key word
     * Channel_Diag is used outside the key words Module and Endmodule, the same
     * manufacturer-specific error type is specified in the channel- related
     * diagnostic field for all remaining modules (refer to Section 0). If the
     * key words Ext_User_Prm_Data_Ref or Ext_User_Prm_Data_Const are used
     * outside the key words Module and EndModule, the associated _userPrmData
     * area refers to the entire device, and the data in the parameter offset to
     * the entire _userPrmData. This _userPrmData area has to be at the start of
     * the _userPrmData. The module-specific _userPrmData is directly attached
     * to the device-specific _userPrmData in the sequence in which the
     * associated modules were configured. If the keywords Ext_User_Prm_Data_Ref
     * or Ext_User_Prm_Data_Const are used within the key words Module and
     * Endmodule, the data in the parameter offset refers only to the start of
     * the _userPrmData area that is assigned to this module.
     */
    public class Module {
        /**
         * Module name of a module used in a modular DP station, or device name
         * of a compact DP slave. Type: Visible-String(32)
         */
        private String _modName;

        /**
         * Here, the ID or IDs of the module of a modular DP slave or of a
         * compact DP device are specified. Type: Octet-String(17) Type:
         * Octet-String(244) (Starting with GSD_Revision 1)
         */
        private String _config;

        /**
         * Here, the reference of the module description is specified. This
         * reference has to be unique for a device (same Ident_Number). This
         * referencing is useful in order to make language-independent
         * configuring possible in a language-dependent system, or to recognize
         * modules. Type: Unsigned8
         */
        private short _moduleReference;

        // Methoden of class Module
        /**
         * Here, the ID or IDs of the module of a modular DP slave or of a
         * compact DP device are specified. Type: Octet-String(17) Type:
         * Octet-String(244) (Starting with GSD_Revision 1)
         * 
         * @return the ID.
         */
        public final String getConfig() {
            return _config;
        }

        /**
         * Here, the ID or IDs of the module of a modular DP slave or of a
         * compact DP device are specified. Type: Octet-String(17) Type:
         * Octet-String(244) (Starting with GSD_Revision 1)
         * 
         * @param config
         *            return the ID.
         */
        public final void setConfig(final String config) {
            _config = config;
        }

        /**
         * Module name of a module used in a modular DP station, or device name
         * of a compact DP slave.
         * 
         * @return the name
         */
        public final String getModName() {
            return _modName;
        }

        /**
         * Module name of a module used in a modular DP station, or device name
         * of a compact DP slave.
         * 
         * @param modName
         *            set the Name
         */
        public final void setModName(final String modName) {
            _modName = modName;
        }

        /**
         * Here, the reference of the module description is specified. This
         * reference has to be unique for a device (same Ident_Number). This
         * referencing is useful in order to make language-independent
         * configuring possible in a language-dependent system, or to recognize
         * modules. Type: Unsigned8
         * 
         * @return the reference
         */
        public final short getModuleReference() {
            return _moduleReference;
        }

        /**
         * Here, the reference of the module description is specified. This
         * reference has to be unique for a device (same Ident_Number). This
         * referencing is useful in order to make language-independent
         * configuring possible in a language-dependent system, or to recognize
         * modules. Type: Unsigned8
         * 
         * @param moduleReference
         *            set the reference
         */
        public final void setModuleReference(final short moduleReference) {
            _moduleReference = moduleReference;
        }

    }

    /**
     * With the key word Channel_Diag, the assigment of manufacturer-specific
     * error types (Error_Type)in the channel-related diagnostic field to texts
     * (_diagText) is specified (refer to Section 0).
     * 
     * @author tboeckmann
     * @author $Author$
     * @version $Revision$
     * @since 02.04.2008
     */
    public class ChannelDiag {
        /**
         * Type: Unsigned8 (16 <= Error_Type <= 31).
         */
        private byte _errorType;

        /**
         * Diagnostic text.<br>
         * Type: Visible-String32
         */
        private String _diagText;

        // Methode of class Channel_Diag
        public String getDiagText() {
            return _diagText;
        }

        public void setDiagText(String diagText) {
            _diagText = diagText;
        }

        public byte getErrorType() {
            return _errorType;
        }

        public void setErrorType(byte errorType) {
            _errorType = errorType;
        }

    }

    /**
     * Here is is specified whether the DP slave accepts a data message without
     * data instead of data message with data = 0 in the CLEAR mode of DP master
     * (Class1).<br>
     * Type: Boolean (1: TRUE)
     * 
     */
    private boolean _failSafe;

    /**
     * Here, the maximum length of the diagnostic information (Diag_Data) is
     * specified.<br>
     * Type: Unsigned8 (6-244)
     */
    private byte _maxDiagDataLen;

    /**
     * Here, the slot number is specified that is to appear in the configuration
     * tool as the first slot number at configuring (is used for improved
     * representation).<br>
     * Type: Unsigned8
     */
    private byte _modulOffset;

    /**
     * Here, the DP slave is assigned to a function class. The family name is
     * structured hierarchically. In addition to the main family, subfamilies
     * can be generated that are respectively added with "@". A maximum of three
     * subfamilies can be defined. Example: _slaveFamily=3@Digital@24V The
     * following main families are specified: 0: General (can't be assigned to
     * the categories below) 1: Drives 2: Switching devices 3: I/O 4: Values 5:
     * Controller 6: HMI (MMI) 7: Encoders 8: NC/RC 9: Gateway 10: PLCs 11:
     * Ident systems 12-255: reserved Type: Unsigned8
     */
    private byte _slaveFamily;

    /**
     * Here, the maximum length of the _userPrmData is specified. The definition
     * of key word excludes the evaluation of _userPrmDataLen.<br>
     * Type: Unsigned8
     */
    private byte _maxUserPrmLen;

    private Ext_User_Prm_Data_Const _extUserPrmDataConst;

    /**
     * Here, a reference to a _userPrmData description is specified. The
     * definition of this key word excludes the evaluation of _userPrmData . If
     * areas overlap when describing the _userPrmData, the area defined last in
     * the Device Description Block has priority.
     * 
     * @author tboeckmann
     * @author $Author$
     * @version $Revision$
     * @since 02.04.2008
     */
    public class Ext_User_Prm_Data_Ref {

        /**
         * Here, the offset within the associated part of the _userPrmData is
         * defined.<br>
         * Type: Unsigned8
         */
        private byte _referenceOffset;

        /**
         * This reference number has to be the same as the reference number that
         * is defined in the _userPrmData description.<br>
         * Type: Unsigned16
         */
        private short _referenceNumber;

        // Methoden of class Ext_User_Prm_Data_Ref

        public short getReferenceNumber() {
            return _referenceNumber;
        }

        public void setReferenceNumber(short referenceNumber) {
            _referenceNumber = referenceNumber;
        }

        public byte getReferenceOffset() {
            return _referenceOffset;
        }

        public void setReferenceOffset(byte referenceOffset) {
            _referenceOffset = referenceOffset;
        }

    }

    /**
     * Here, a constant part of the _userPrmData is specified. The definition of
     * this key word excludes the evaluation of _userPrmData . If areas overlap
     * when describing the _userPrmData, the area defined last in the Device
     * Description Data (GSD) file has priority.
     * 
     * @author tboeckmann
     * @author $Author$
     * @version $Revision$
     * @since 02.04.2008
     */
    public class Ext_User_Prm_Data_Const {

        /**
         * Here, the offset within the associated part of _userPrmData is
         * defined.<br>
         * Type: Unsigned8
         */
        private byte _constOffset;

        /**
         * Here, the constants or default selections within the _userPrmData are
         * defined.<br>
         * Type: Octet-String
         * 
         */
        private String _constPrmData = "";

        // Methoden of class Ext_User_Prm_Data_Const
        public byte getConstOffset() {
            return _constOffset;
        }

        public void setConstOffset(byte constOffset) {
            _constOffset = constOffset;
        }

        public String getConstPrmData() {
            if (_constPrmData == null) {
                _constPrmData = "";
            }
            return _constPrmData;
        }

        public void setConstPrmData(String constPrmData) {
            _constPrmData = constPrmData;
        }

    }

    /**
     * Between the key words ExtUserPrmData and EndExtUserPrmData, a parameter
     * of the _userPrmData is described. The definition of this key word
     * excludes the evaluation of _userPrmData.
     * 
     * @author tboeckmann
     * @author $Author$
     * @version $Revision$
     * @since 02.04.2008
     */
    public class ExtUserPrmData {

        /**
         * Here, the reference of the _userPrmData description is specified.<br>
         * This reference has to be unique.<br>
         * Type: Unsigned8
         */
        private byte Reference_Number;

        /**
         * Clear text description of parameter.<br>
         * Type: Visible-String
         */
        private String Ext_User_Prm_Data_Name;

        /**
         * The following data types are specified:<br>
         * Unsigned8<br>
         * Unsigned16<br>
         * Unsigned32<br>
         * Signed8<br>
         * Signed16<br>
         * Signed32<br>
         * Bit<br>
         * BitArea<br>
         * <br>
         * <br>
         * Name of general data type of the describled parameter.<br>
         * Type: Visible-String(32)
         */
        private String _dataTypeName;

        /**
         * Default value of the described parameter.<br>
         * Type: DataType (has to correspond to the describled parameter).
         */
        private String _defaultValue;

        /**
         * Minimum value of the described parameter.<br>
         * Type: DataType (has to correspond to the describled parameter).
         */
        private String _minValue;

        /**
         * Maximum value of the described parameter.<br>
         * Type: DataType (has to correspond to the describled parameter).
         */
        private String _maxValue;

        /**
         * Permitted value of the described parameter.<br>
         * Type: DataType (has to correspond to the Data_Type_Name).
         */
        private String _allowedValue;

        /**
         * This reference number has to be the same as the reference number that
         * is defined in the PrmText description.<br>
         * Type: Unsigned8
         */
        private byte _prmTextRef;

        /**
         * Between the key words PrmText and EndPrmText, possible values of a
         * parameter are described. Texts are also assigned to these values.
         * 
         * @author tboeckmann
         * @author $Author$
         * @version $Revision$
         * @since 08.12.2005
         */
        public class PrmText {

            /**
             * Meaning: Here, the reference of the PrmText description is
             * specified. This reference must be unique.<BR>
             * Type: Unsigned8
             */
            private byte _referenceNumber;

            /**
             * @author tboeckmann
             * @author $Author$
             * @version $Revision$
             * @since 01.10.2008
             */
            /**
             * @author hrickens
             * @author $Author$
             * @version $Revision$
             * @since 01.10.2008
             */
            /**
             * @author hrickens
             * @author $Author$
             * @version $Revision$
             * @since 01.10.2008
             */
            public class Text_Item {
                /**
                 * 
                 * Here, the value of the parameter is specified that is to be
                 * described. Type: Data_Type (has to correspond to the<br>
                 * Data_Type_Name in the parameter description).
                 */
                private String _prmDataType;

                /**
                 * Description of the parameter value.<br>
                 * Type: Visible-String
                 */
                private String _text;

                // Methoden of class Text_Item
                public String getPrm_Data_Type() {
                    return _prmDataType;
                }

                public void setPrm_Data_Type(String prm_Data_Type) {
                    _prmDataType = prm_Data_Type;
                }

                public String getText() {
                    return _text;
                }

                public void setText(String text) {
                    _text = text;
                }

            }

            // Methoden of class PrmText
            public byte getReference_Number() {
                return _referenceNumber;
            }

            public void setReference_Number(byte referenceNumber) {
                _referenceNumber = referenceNumber;
            }

        }

        // Mehoden of class ExtUserPrmData
        public String getAllowed_Value() {
            return _allowedValue;
        }

        public void setAllowed_Value(String allowedValue) {
            _allowedValue = allowedValue;
        }

        public String getData_Type_Name() {
            return _dataTypeName;
        }

        public void setData_Type_Name(String dataTypeName) {
            _dataTypeName = dataTypeName;
        }

        public String getDefault_Value() {
            return _defaultValue;
        }

        public void setDefault_Value(String defaultValue) {
            _defaultValue = defaultValue;
        }

        public String getExt_User_Prm_Data_Name() {
            return Ext_User_Prm_Data_Name;
        }

        public void setExt_User_Prm_Data_Name(String extUserPrmDataName) {
            Ext_User_Prm_Data_Name = extUserPrmDataName;
        }

        public String getMax_Value() {
            return _maxValue;
        }

        public void setMax_Value(String max_Value) {
            _maxValue = max_Value;
        }

        public String getMin_Value() {
            return _minValue;
        }

        public void setMin_Value(String min_Value) {
            _minValue = min_Value;
        }

        public byte getPrm_Text_Ref() {
            return _prmTextRef;
        }

        public void setPrm_Text_Ref(byte prm_Text_Ref) {
            _prmTextRef = prm_Text_Ref;
        }

        public byte getReference_Number() {
            return Reference_Number;
        }

        public void setReference_Number(byte reference_Number) {
            Reference_Number = reference_Number;
        }

    }

    public boolean isAutoBaudSupp() {
        return _autoBaudSupp;
    }

    public void setAutoBaudSupp(boolean autoBaudSupp) {
        _autoBaudSupp = autoBaudSupp;
    }

    public boolean isFailSafe() {
        return _failSafe;
    }

    public void setFailSafe(boolean failSafe) {
        _failSafe = failSafe;
    }

    public boolean isFreezeModeSupp() {
        return _freezeModeSupp;
    }

    public void setFreezeModeSupp(boolean freezeModeSupp) {
        _freezeModeSupp = freezeModeSupp;
    }

    public short getMaxDataLen() {
        return _maxDataLen;
    }

    public void setMaxDataLen(short max_Data_Len) {
        _maxDataLen = max_Data_Len;
    }

    public byte getMax_Diag_Data_Len() {
        return _maxDiagDataLen;
    }

    public void setMax_Diag_Data_Len(byte max_Diag_Data_Len) {
        _maxDiagDataLen = max_Diag_Data_Len;
    }

    public byte getMaxmInputLen() {
        return _maxmInputLen;
    }

    public void setMaxmInputLen(byte max_Input_Len) {
        _maxmInputLen = max_Input_Len;
    }

    public byte getMaxModule() {
        return _maxModule;
    }

    public void setMaxModule(byte max_Module) {
        _maxModule = max_Module;
    }

    public byte getMaxOutputLen() {
        return _maxOutputLen;
    }

    public void setMaxOutputLen(byte max_Output_Len) {
        _maxOutputLen = max_Output_Len;
    }

    public byte getMax_User_Prm_Len() {
        return _maxUserPrmLen;
    }

    public void setMax_User_Prm_Len(byte max_User_Prm_Len) {
        _maxUserPrmLen = max_User_Prm_Len;
    }

    /**
     * This time specifies the minimum interval between two slave list cycles
     * for the DP device.
     * 
     * @return Minimum slave interval time
     */
    public short getMinSlaveIntervall() {
        return _minSlaveIntervall;
    }

    public void setMinSlaveIntervall(short min_Slave_Intervall) {
        _minSlaveIntervall = min_Slave_Intervall;
    }

    public byte getModul_Offset() {
        return _modulOffset;
    }

    public void setModul_Offset(byte modul_Offset) {
        _modulOffset = modul_Offset;
    }

    public boolean isModularStation() {
        return _modularStation;
    }

    public void setModularStation(boolean modular_Station) {
        _modularStation = modular_Station;
    }

    public boolean isSetSlaveAddSupp() {
        return _setSlaveAddSupp;
    }

    public void setSetSlaveAddSupp(boolean set_Slave_Add_supp) {
        _setSlaveAddSupp = set_Slave_Add_supp;
    }

    public byte getSlaveFamily() {
        return _slaveFamily;
    }

    public void setSlaveFamily(byte slave_Family) {
        _slaveFamily = slave_Family;
    }

    public boolean isSyncModeSupp() {
        return _syncModeSupp;
    }

    public void setSyncModeSupp(boolean sync_Mode_supp) {
        _syncModeSupp = sync_Mode_supp;
    }

    public String getUserPrmData() {
        return _userPrmData;
    }

    public void setUserPrmData(String user_Prm_Data) {
        _userPrmData = user_Prm_Data;
    }

    public byte getUserPrmDataLen() {
        return _userPrmDataLen;
    }

    public void setUserPrmDataLen(byte user_Prm_Data_Len) {
        _userPrmDataLen = user_Prm_Data_Len;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.csstudio.config.ioconfig.model.Keywords#getType()
     */
    public GSDFileTyp getType() {
        return GSDFileTyp.Slave;
    }

    public Ext_User_Prm_Data_Const getExtUserPrmDataConst() {
        if (_extUserPrmDataConst == null) {
            _extUserPrmDataConst = new Ext_User_Prm_Data_Const();
        }
        return _extUserPrmDataConst;
    }
}
