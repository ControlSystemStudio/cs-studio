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
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import java.util.HashMap;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 18.07.2008
 */
public class GsdSlaveModel extends GsdGeneralModel {
    
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
    private short _maxModule;

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
    
    /**
     * The map Contain all PrmText entries. The Key is the Index. 
     */
    private HashMap<String, HashMap<Integer,PrmText>> _prmTextMap;

    private UserPrmDataModel _userPrmDataModel;

    private HashMap<String, ExtUserPrmData> _extUserPrmDataMap;

    private HashMap<String, ExtUserPrmDataConst> _extUserPrmDataConstMap;

    private HashMap<String, ExtUserPrmDataRef> _extUserPrmDataRefMap;

    private HashMap<String, UnitDiagBit> _unitDiagBit;

    private HashMap<Integer,GsdModuleModel> _gsdModuleMap;


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

    public void setMaxDataLen(short maxDataLen) {
        _maxDataLen = maxDataLen;
    }

    public byte getMax_Diag_Data_Len() {
        return _maxDiagDataLen;
    }

    public void setMax_Diag_Data_Len(byte maxDiagDataLen) {
        _maxDiagDataLen = maxDiagDataLen;
    }

    public byte getMaxmInputLen() {
        return _maxmInputLen;
    }

    public void setMaxmInputLen(byte maxInputLen) {
        _maxmInputLen = maxInputLen;
    }

    public short getMaxModule() {
        return _maxModule;
    }

    public void setMaxModule(short maxModule) {
        _maxModule = maxModule;
    }

    public byte getMaxOutputLen() {
        return _maxOutputLen;
    }

    public void setMaxOutputLen(byte maxOutputLen) {
        _maxOutputLen = maxOutputLen;
    }

    public byte getMax_User_Prm_Len() {
        return _maxUserPrmLen;
    }

    public void setMax_User_Prm_Len(byte maxUserPrmLen) {
        _maxUserPrmLen = maxUserPrmLen;
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

    public void setMinSlaveIntervall(short minSlaveIntervall) {
        _minSlaveIntervall = minSlaveIntervall;
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

    public void setModularStation(boolean modularStation) {
        _modularStation = modularStation;
    }

    public boolean isSetSlaveAddSupp() {
        return _setSlaveAddSupp;
    }

    public void setSetSlaveAddSupp(boolean setSlaveAddSupp) {
        _setSlaveAddSupp = setSlaveAddSupp;
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
    
    public UserPrmDataModel getUserPrmDataModel(){
        return _userPrmDataModel;
    }

    public void setUserPrmDataModel(UserPrmDataModel userPrmDataModel){
        _userPrmDataModel = userPrmDataModel;
    }

    public final HashMap<String, HashMap<Integer,PrmText>> getPrmTextMap() {
        return _prmTextMap;
    }

    public final void setPrmTextMap(HashMap<String, HashMap<Integer,PrmText>> prmTextMap) {
        _prmTextMap = prmTextMap;
    }
    
    public void addPrmText(String index, HashMap<Integer,PrmText> prmText){
        if(_prmTextMap==null){
            _prmTextMap = new HashMap<String, HashMap<Integer,PrmText>>();
        }
        _prmTextMap.put(index, prmText);
    }
    
    public HashMap<Integer,PrmText> getPrmText(String index){
        return _prmTextMap.get(index);
    }

    /**
     * @param index
     * @param extUserPrmData
     */
    public void addExtUserPrmData(String index, ExtUserPrmData extUserPrmData) {
        if(_extUserPrmDataMap==null){
            _extUserPrmDataMap = new HashMap<String, ExtUserPrmData>();
        }
        _extUserPrmDataMap.put(index, extUserPrmData);
    }
    
    public ExtUserPrmData getExtUserPrmData(String index) {
        if(_extUserPrmDataMap==null){
            _extUserPrmDataMap = new HashMap<String, ExtUserPrmData>();
        }
        return _extUserPrmDataMap.get(index);
    }

    public HashMap<String,ExtUserPrmData> getExtUserPrmDataMap() {
        if(_extUserPrmDataMap==null){
            _extUserPrmDataMap = new HashMap<String, ExtUserPrmData>();
        }
        return _extUserPrmDataMap;
    }


    /**
     * @param index
     * @param extUserPrmDataConst
     */
    public void addExtUserPrmDataConst(String index, ExtUserPrmDataConst extUserPrmDataConst) {
        if(_extUserPrmDataConstMap==null){
            _extUserPrmDataConstMap = new HashMap<String, ExtUserPrmDataConst>();
        }
        _extUserPrmDataConstMap.put(index, extUserPrmDataConst);
        
    }

    /**
     * 
     * @param index
     * @return
     */
    public ExtUserPrmDataConst getExtUserPrmDataConst(String index) {
        if(_extUserPrmDataConstMap==null){
            return null;
        }
        return _extUserPrmDataConstMap.get(index);
        
    }
    
    public HashMap<String,ExtUserPrmDataConst> getExtUserPrmDataConst() {
        if(_extUserPrmDataConstMap==null){
            _extUserPrmDataConstMap = new HashMap<String, ExtUserPrmDataConst>();
        }
        return _extUserPrmDataConstMap;
        
    }


    /**
     * @param index
     * @param extUserPrmDataRef
     */
    public void addExtUserPrmDataRef(String index, ExtUserPrmDataRef extUserPrmDataRef) {
        if(_extUserPrmDataRefMap==null){
            _extUserPrmDataRefMap = new HashMap<String, ExtUserPrmDataRef>();
        }
        _extUserPrmDataRefMap.put(index, extUserPrmDataRef);
    }

    /**
     * @param index
     * @param unitDiagBit
     */
    public void addUnitDiagBit(String index, UnitDiagBit unitDiagBit) {
        if(_unitDiagBit==null){
            _unitDiagBit = new HashMap<String, UnitDiagBit>();
        }
        _unitDiagBit.put(index, unitDiagBit);        
    }
    
    /**
     * 
     * @return the map of all {@link GsdModuleModel} from this model. 
     */
    public HashMap<Integer, GsdModuleModel> getGsdModuleList() {
        return _gsdModuleMap;
    }
 
    
    /**
     * Set all GsdModuleModel for this Slave model.
     * @param moduleMap a Map of all {@link GsdModuleModel} 
     */
    public final void setGsdModuleList(HashMap<Integer, GsdModuleModel> moduleMap) {
        _gsdModuleMap = new HashMap<Integer, GsdModuleModel>(moduleMap);
    }

}
