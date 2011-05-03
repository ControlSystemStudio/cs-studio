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
 * $Id: GsdModuleModel.java,v 1.2 2009/10/22 06:58:59 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import java.util.HashMap;

/**
 * The Model for a Profibus Module created from a GSD File.
 * 
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 22.08.2008
 */
public class GsdModuleModel {

    /**
     * The Name of the Module.
     */
    private String _name;

    /**
     * The SLAVE_CFG_DATA value.
     */
    private final String _value = "";

    /**
     * The UserPrmDataConst Value.<br>
     * Ext_User_Prm_Data_Const(0) = 0x20,0xD8,0x00,0x00<br>
     */
    private String _extUserPrmDataConst;

    /**
     * The number of the Module.
     */
    private final int _moduleNumber = -1;

    /**
     * The parent gsd Slave Model.
     */
//    private final GsdSlaveModel _gsdSlaveModel;
    
    /**
     * The map of all ext User Prm Data Ref.<br>
     * The key is the reference and the value is the byte position.
     */
    private final HashMap<String, String> _extUserPrmDataRefMap = new HashMap<String, String>();

    /**
     * A Map with all "ext User Prm Data" modification.
     */
    private final HashMap<Integer, Integer[]> _modifications = new HashMap<Integer, Integer[]>();

    /**
     * Example of a GSD Module.<br>
     * 
     * Module = "750-654 Data Exchange Module RA" 0xF2  ; contain Name and Value<br>
     * 394                                              ; index<br>
     * Ext_Module_Prm_Data_Len = 4<br>                  ; Ext_Module_Prm_Data length  
     * Ext_User_Prm_Data_Const(0) = 0x20,0xD8,0x00,0x00 ; extUserPrmDataConst<br>
     * Ext_User_Prm_Data_Ref(0) = 11                    ; Ext_User_Prm_Data reference. Can have more.<br> 
     * EndModule<br>
     */

//    /**
//     * Default Constructor.
//     * Set the Name and Value from the line.
//     * 
//     * @param line
//     *            the first gsd file line for a module, contain the name and value. 
//     * @param slaveModel the parent {@link GsdSlaveModel}.
//     */
//    public GsdModuleModel(final String line, final GsdSlaveModel slaveModel) {
//        _gsdSlaveModel = slaveModel;
////        String[] lineParts = line.split("[\";]");
////        if (lineParts.length > 2) {
////            setName(lineParts[1].trim());
////            setValue(lineParts[2]);
////        }
//
//    }

//    /**
//     * 
//     * @return the Name of this Module.
//     */
//    public final String getName() {
//        return _name;
//    }
    
//    /**
//     * @param name The name of the Module.
//     */
//    public final void setName(final String name) {
//        _name = name;
//    }

//    /**
//     * @return the Value for the SLAVE_CFG_DATA.
//     */
//    public final String getValue() {
//        return _value;
//    }
    
//    /**
//     * @param value set the Value for the SLAVE_CFG_DATA.
//     */
//    public final void setValue(final String value) {
//        _value = value.replaceAll("\\\\", "").trim();
//    }
//
//    /**
//     * 
//     * @param value the Value for the SLAVE_CFG_DATA.
//     */
//    public final void addValue(final String value) {
//        _value = _value.concat(value.replaceAll("\\\\", "").trim());
//    }
//
//    /**
//     * 
//     * @return the number of this Module.
//     */
//    public final int getModuleNumber() {
//        return _moduleNumber;
//    }
//    
//    /**
//     * @param moduleNumber
//     *            the number of this Module.
//     */
//    public final void setModuleNumber(final int moduleNumber) {
//        _moduleNumber = moduleNumber;
//    }
//    
//    public final void setModuleNumber(final String moduleNumber) {
//        try {
//            _moduleNumber = Integer.parseInt(moduleNumber);
//        } catch (NumberFormatException nfe) {
//        }
//
//    }

//    /**
//     * 
//     * @return the ExtUserPrmDataConst exclusive the modification.
//     */
//    public final String getExtUserPrmDataConst() {
//        if(_extUserPrmDataConst==null) {
//            return "";
//        }
//        return _extUserPrmDataConst;
//    }

//    /**
//     * @return the ExtUserPrmDataConst inclusive the modification.
//     */
//    public final String getModiExtUserPrmDataConst() {
//        if (getExtUserPrmDataConst() != null) {
//            String[] split = getExtUserPrmDataConst().split(",");
//            for (int bytePosAbs : _modifications.keySet()) {
//                Integer[] modis = _modifications.get(bytePosAbs);
//                int value = modis[3];
//                int bytePos = modis[0];
//                int low = modis[1];
//                int high = modis[2];
//                int mask = ~((int)Math.pow(2, high+1) - (int)Math.pow(2, low));
//                int radix = 10;
//                if(high>8&&high<16){
//                    if (bytePos+1 < split.length) {
//                        String byteValue = split[bytePos+1];
//                        byteValue = byteValue.concat(split[bytePos]);
//                        if (byteValue.startsWith("0x")) {
//                            byteValue = byteValue.replaceAll("0x", "");
//                            radix = 16;
//                        }
//                        int parseInt = Integer.parseInt(byteValue, radix);
//                        value = value << (low);
//                        int result = (parseInt & mask) | (value);
//                        Formatter f = new Formatter();
//                        f.format("%04x", result);
//                        String tmp = f.toString();
//                        split[bytePos+1] = "0x"+tmp.substring(0,1);
//                        split[bytePos]   = "0x"+tmp.substring(2,3);
//                    }
//                }else{
//                    if (bytePos < split.length) {
//                        String byteValue = split[bytePos];
//                        if (byteValue.startsWith("0x")) {
//                            byteValue = byteValue.substring(2);
//                            radix = 16;
//                        }
//                        int parseInt = Integer.parseInt(byteValue, radix);
//                        value = value << (low);
//                        int result = (parseInt & mask) | (value);
//                        Formatter f = new Formatter();
//                        f.format("%#04x", result);
//                        split[bytePos] = f.toString();
//                    }
//                }
//            }
//            String string = Arrays.toString(split);
//            string = string.substring(1, string.length() - 1);
//            return string;
//        }
//        return "";
//    }
    
//    /**
//     * @param extUserPrmDataConst
//     *            the ExtUserPrmDataConst value.
//     */
//    public final void setExtUserPrmDataConst(final String extUserPrmDataConst) {
//        _extUserPrmDataConst = extUserPrmDataConst.replaceAll("\\\\", "").trim();
//    }
//
//    /**
//     * Extend the extUserPrmDataConst.
//     * @param extUserPrmDataConst
//     *            the attachment ExtUserPrmDataConst value.
//     */
//    public final void addtExtUserPrmDataConst(final String extUserPrmDataConst) {
//        _extUserPrmDataConst = _extUserPrmDataConst.concat(extUserPrmDataConst.replaceAll("\\\\",
//                "").trim());
//    }

//    public ExtUserPrmData getExtUserPrmData(String value) {
//        return _gsdSlaveModel.getExtUserPrmData(value);
//    }

    /**
     * @param bytePos
     *            the Byte index.
     * @param reference
     *            the reference number to the extended user parameter data.
     */
//    public void addExtUserPrmDataRef(final String bytePos, final String reference) {
//        _extUserPrmDataRefMap.put(reference, bytePos);
//    }
//    
//    public final String getExtUserPrmDataRef(String reference) {
//        return _extUserPrmDataRefMap.get(reference);
//    }
    
//    @Nonnull
//    public ArrayList<ExtUserPrmData> getAllExtUserPrmDataRef() {
//        ArrayList<ExtUserPrmData> arrayList = new ArrayList<ExtUserPrmData>();
//        for (String key : _extUserPrmDataRefMap.keySet()) {
//            arrayList.add(_gsdSlaveModel.getExtUserPrmDataMap().get(key));
//        }
//        Collections.sort(arrayList, new Comparator<ExtUserPrmData>() {
//
//            @Override
//            public int compare(ExtUserPrmData arg0, ExtUserPrmData arg1) {
//                try {
//                return arg0.getIndex() - arg1.getIndex();
//                }catch (ArrayIndexOutOfBoundsException e) {
//                    CentralLogger.getInstance().warn(this, "arg0: +"+arg0+"\t"+arg0.getIndex());
//                    CentralLogger.getInstance().warn(this, "arg1: +"+arg1+"\t"+arg1.getIndex());
//                    e.printStackTrace();
//                    throw e;
//                }
//            }
//
//        });
//        return arrayList;
//    }
//
//    public final HashMap<Integer, PrmTextItem> getPrmText(String prmTextRef) {
//        return _gsdSlaveModel.getPrmText(prmTextRef);
//    }
//
//    /**
//     * Add a modification for the ExtUserPrmDataConst. 
//     * @param bytePos The byte which a modified. 
//     * @param bitMin  The lowest bits of the byte that are modified.
//     * @param bitMax  The highest bits of the byte that are modified.
//     * @param value the new value of the bits that are modified.
//     */
//    public final void addModify(final int bytePos, final int bitMin, final int bitMax,
//            final int value) {
//        int startBit = bytePos * 8 + bitMin;
//        _modifications.put(startBit, new Integer[] { bytePos, bitMin, bitMax, value });
//    }
    
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public final String toString() {
//        return getName();
//    }

//    /* (non-Javadoc)
//     * @see java.lang.Object#hashCode()
//     */
//    @Override
//    public int hashCode() {
//        final int prime = 31;
//        int result = 1;
//        result = prime * result + ((_gsdSlaveModel == null) ? 0 : _gsdSlaveModel.hashCode());
//        result = prime * result + _moduleNumber;
//        return result;
//    }
//
//    /* (non-Javadoc)
//     * @see java.lang.Object#equals(java.lang.Object)
//     */
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj == null) {
//            return false;
//        }
//        if (!(obj instanceof GsdModuleModel)) {
//            return false;
//        }
//        GsdModuleModel other = (GsdModuleModel) obj;
//        if (_gsdSlaveModel == null) {
//            if (other._gsdSlaveModel != null) {
//                return false;
//            }
//        } else if (!_gsdSlaveModel.equals(other._gsdSlaveModel)) {
//            return false;
//        }
//        if (_moduleNumber != other._moduleNumber) {
//            return false;
//        }
//        return true;
//    }

    
}
