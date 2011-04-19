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
 * $Id: GsdSlaveModel.java,v 1.3 2010/09/03 07:13:20 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.GSDFileTypes;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.3 $
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
    private HashMap<String, HashMap<Integer,PrmTextItem>> _prmTextMap;

    private UserPrmDataModel _userPrmDataModel;

    private HashMap<String, ExtUserPrmData> _extUserPrmDataMap;

//    private TreeMap<String, ExtUserPrmDataConst> _extUserPrmDataConstMap;

    //FIXME: Müssen die anderen HashMap's auch TreeMaps sein? Nein Was hat diese Map für einen Sinn sie wird nie Ausgelese!
//    private List<ExtUserPrmDataRef> _extUserPrmDataRefMap;

    /**
     * A Map with all "ext User Prm Data" modification.
     */
    private final HashMap<Integer, Integer[]> _modifications = new HashMap<Integer, Integer[]>();

    private HashMap<String, UnitDiagBit> _unitDiagBit;

    private HashMap<Integer,GsdModuleModel> _gsdModuleMap;

    private List<String> _userPrmDataList;


    public boolean isAutoBaudSupp() {
        return _autoBaudSupp;
    }

    public void setAutoBaudSupp(final boolean autoBaudSupp) {
        _autoBaudSupp = autoBaudSupp;
    }

    public boolean isFailSafe() {
        return _failSafe;
    }

    public void setFailSafe(final boolean failSafe) {
        _failSafe = failSafe;
    }

    public boolean isFreezeModeSupp() {
        return _freezeModeSupp;
    }

    public void setFreezeModeSupp(final boolean freezeModeSupp) {
        _freezeModeSupp = freezeModeSupp;
    }

    public short getMaxDataLen() {
        return _maxDataLen;
    }

    public void setMaxDataLen(final short maxDataLen) {
        _maxDataLen = maxDataLen;
    }

    public byte getMaxDiagDataLen() {
        return _maxDiagDataLen;
    }

    public void setMaxDiagDataLen(final byte maxDiagDataLen) {
        _maxDiagDataLen = maxDiagDataLen;
    }

    public byte getMaxmInputLen() {
        return _maxmInputLen;
    }

    public void setMaxmInputLen(final byte maxInputLen) {
        _maxmInputLen = maxInputLen;
    }

    public short getMaxModule() {
        return _maxModule;
    }

    public void setMaxModule(final short maxModule) {
        _maxModule = maxModule;
    }

    public byte getMaxOutputLen() {
        return _maxOutputLen;
    }

    public void setMaxOutputLen(final byte maxOutputLen) {
        _maxOutputLen = maxOutputLen;
    }

    public byte getMaxUserPrmLen() {
        return _maxUserPrmLen;
    }

    public void setMaxUserPrmLen(final byte maxUserPrmLen) {
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

    public void setMinSlaveIntervall(final short minSlaveIntervall) {
        _minSlaveIntervall = minSlaveIntervall;
    }

    public byte getModulOffset() {
        return _modulOffset;
    }

    public void setModulOffset(final byte modulOffset) {
        _modulOffset = modulOffset;
    }

    public boolean isModularStation() {
        return _modularStation;
    }

    public void setModularStation(final boolean modularStation) {
        _modularStation = modularStation;
    }

    public boolean isSetSlaveAddSupp() {
        return _setSlaveAddSupp;
    }

    public void setSetSlaveAddSupp(final boolean setSlaveAddSupp) {
        _setSlaveAddSupp = setSlaveAddSupp;
    }

    public byte getSlaveFamily() {
        return _slaveFamily;
    }

    public void setSlaveFamily(final byte slaveFamily) {
        _slaveFamily = slaveFamily;
    }

    public boolean isSyncModeSupp() {
        return _syncModeSupp;
    }

    public void setSyncModeSupp(final boolean syncModeSupp) {
        _syncModeSupp = syncModeSupp;
    }

//    public String getUserPrmData() {
//        return _userPrmData;
//    }

    public void setUserPrmData(final String userPrmData) {
        _userPrmData = userPrmData;
        _userPrmDataList  = Arrays.asList(_userPrmData.split(","));
    }

    /**
     * @return
     *
     */
    @Transient
    public List<String> getUserPrmDataList() {
        return _userPrmDataList;
    }


    public byte getUserPrmDataLen() {
        return _userPrmDataLen;
    }

    public void setUserPrmDataLen(final byte userPrmDataLen) {
        _userPrmDataLen = userPrmDataLen;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.config.ioconfig.model.Keywords#getType()
     */
    public GSDFileTypes getType() {
        return GSDFileTypes.Slave;
    }

    public UserPrmDataModel getUserPrmDataModel(){
        return _userPrmDataModel;
    }

    public void setUserPrmDataModel(final UserPrmDataModel userPrmDataModel){
        _userPrmDataModel = userPrmDataModel;
    }

    public final HashMap<String, HashMap<Integer,PrmTextItem>> getPrmTextMap() {
        return _prmTextMap;
    }

    public final void setPrmTextMap(final HashMap<String, HashMap<Integer,PrmTextItem>> prmTextMap) {
        _prmTextMap = prmTextMap;
    }

    public void addPrmText(final String index, final HashMap<Integer,PrmTextItem> prmText){
        if(_prmTextMap==null){
            _prmTextMap = new HashMap<String, HashMap<Integer,PrmTextItem>>();
        }
        _prmTextMap.put(index, prmText);
    }

    public HashMap<Integer,PrmTextItem> getPrmText(final String index){
        return _prmTextMap.get(index);
    }

    /**
     * @param index
     * @param extUserPrmData
     */
    public void addExtUserPrmData(final String index, final ExtUserPrmData extUserPrmData) {
        if(_extUserPrmDataMap==null){
            _extUserPrmDataMap = new HashMap<String, ExtUserPrmData>();
        }
        _extUserPrmDataMap.put(index, extUserPrmData);
    }

    public ExtUserPrmData getExtUserPrmData(final String index) {
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


//    /**
//     * @param index
//     * @param extUserPrmDataConst
//     */
//    public void addExtUserPrmDataConst(final String index, final ExtUserPrmDataConst extUserPrmDataConst) {
//        if(_extUserPrmDataConstMap==null){
//            _extUserPrmDataConstMap = new TreeMap<String, ExtUserPrmDataConst>();
//        }
//        _extUserPrmDataConstMap.put(index, extUserPrmDataConst);
//
//    }

//    /**
//     *
//     * @param index the selection Index.
//     * @return the Extend User Parameter Data Constant.
//     */
//    public ExtUserPrmDataConst getExtUserPrmDataConst(final String index) {
//        if(_extUserPrmDataConstMap==null){
//            return null;
//        }
//        return _extUserPrmDataConstMap.get(index);
//
//    }
//
//    public TreeMap<String,ExtUserPrmDataConst> getExtUserPrmDataConst() {
//        if(_extUserPrmDataConstMap==null){
//            _extUserPrmDataConstMap = new TreeMap<String, ExtUserPrmDataConst>();
//        }
//        return _extUserPrmDataConstMap;
//
//    }

    /**
     * Add a modification for the ExtUserPrmDataConst.
     * @param bytePos The byte which a modified.
     * @param bitMin  The lowest bits of the byte that are modified.
     * @param bitMax  The highest bits of the byte that are modified.
     * @param value the new value of the bits that are modified.
     */
    public final void addModify(final int bytePos, final int bitMin, final int bitMax,
            final int value) {
        int startBit = bytePos * 8 + bitMin;
        _modifications.put(startBit, new Integer[] { bytePos, bitMin, bitMax, value });
    }

//    @Nonnull
//    public String getModiExtUserPrmDataConst() {
//        if ((getExtUserPrmDataConst() != null) && (_extUserPrmDataRefMap !=null)) {
//            ArrayList<String> split = new ArrayList<String>();
//            Set<String> keySet = getExtUserPrmDataConst().keySet();
//            for (String key : keySet) {
//                ExtUserPrmDataConst extUserPrmDataConst = getExtUserPrmDataConst().get(key);
//                String[] sp = extUserPrmDataConst.toString().split(",");
//                split.addAll(Arrays.asList(sp));
//            }
//
//            for (ExtUserPrmDataRef extUserPrmDataRef : _extUserPrmDataRefMap) {
//                ExtUserPrmData eupd = getExtUserPrmData(extUserPrmDataRef.getValue());
//
////                PrmText prmText = (PrmText) ((StructuredSelection) prmTextCV.getSelection())
////                .getFirstElement();
////                ExtUserPrmData extUserPrmData = (ExtUserPrmData) prmTextCV.getInput();
////                String index = extUserPrmData.getIndex();
//
//                int bytePos = Integer.parseInt(extUserPrmDataRef.getIndex());
//                int bitMin = eupd.getMinBit();
//                int bitMax = eupd.getMaxBit();
//
//                int val = Integer.parseInt(extUserPrmDataRef.getValue());
//                addModify(bytePos, bitMin, bitMax, val);
//            }
//            for (int bytePosAbs : _modifications.keySet()) {
//                Integer[] modis = _modifications.get(bytePosAbs);
//                int value = modis[3];
//                int bytePos = modis[0];
//                int low = modis[1];
//                int high = modis[2];
//                int mask = ~((int)Math.pow(2, high+1) - (int)Math.pow(2, low));
//                int radix = 10;
//                if((high>8)&&(high<16)){
//                    if (bytePos+1 < split.size()) {
//                        String byteValue = split.get(bytePos+1);
//                        byteValue = byteValue.concat(split.get(bytePos));
//                        if (byteValue.startsWith("0x")) {
//                            byteValue = byteValue.replaceAll("0x", "");
//                            radix = 16;
//                        }
//                        int parseInt = Integer.parseInt(byteValue, radix);
//                        value = value << (low);
//                        int result = (parseInt & mask) | (value);
//                        String tmp = dec2Hex(result);
//                        split.set(bytePos+1,"0x"+tmp.substring(0,1));
//                        split.set(bytePos,"0x"+tmp.substring(2,3));
//                    }
//                }else{
//                    if (bytePos < split.size()) {
//                        String byteValue = split.get(bytePos);
//                        if (byteValue.startsWith("0x")) {
//                            byteValue = byteValue.substring(2);
//                            radix = 16;
//                        }
//                        int parseInt = Integer.parseInt(byteValue, radix);
//                        value = value << (low);
//                        int result = (parseInt & mask) | (value);
//                        split.set(bytePos, dec2Hex(result));
//                    }
//                }
//            }
//            String string = split.toString();
//            string = string.substring(1, string.length() - 1);
//            return string;
//        } else if(_userPrmData != null) {
//            return _userPrmData;
//        }
//        return "";
//    }



    /**
     * @param result
     * @return
     */
    private String dec2Hex(final int result) {
        Formatter f = new Formatter();
        f.format("%#04x", result);
        return f.toString();
    }

//    /**
//     * @param index
//     * @param extUserPrmDataRef
//     */
//    public void addExtUserPrmDataRef(final String index, final ExtUserPrmDataRef extUserPrmDataRef) {
//        if(_extUserPrmDataRefMap==null){
//            _extUserPrmDataRefMap = new ArrayList<ExtUserPrmDataRef>();
//        }
//        _extUserPrmDataRefMap.add(extUserPrmDataRef);
//    }
//
//    public List<ExtUserPrmDataRef> getExtUserPrmDataRefMap() {
//        if(_extUserPrmDataRefMap==null){
//            _extUserPrmDataRefMap = new ArrayList<ExtUserPrmDataRef>();
//        }
//        return _extUserPrmDataRefMap;
//    }
    /**
     * @param index
     * @param unitDiagBit
     */
    public void addUnitDiagBit(final String index, final UnitDiagBit unitDiagBit) {
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
    public final void setGsdModuleList(final HashMap<Integer, GsdModuleModel> moduleMap) {
        _gsdModuleMap = new HashMap<Integer, GsdModuleModel>(moduleMap);
    }
}
