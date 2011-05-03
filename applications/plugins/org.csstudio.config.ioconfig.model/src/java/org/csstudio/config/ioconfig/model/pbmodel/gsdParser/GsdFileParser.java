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
 * $Id: GsdFileParser.java,v 1.3 2010/09/03 07:13:20 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.platform.logging.CentralLogger;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.3 $
 * @since 18.07.2008
 */
public final class GsdFileParser {
    
    private static final Logger LOG = CentralLogger.getInstance().getLogger(GsdFileParser.class);
    
//    /**
//     * The actual line of gsd file.
//     */
//    private static String _LINE;
//    private static GsdSlaveModel _SLAVE;
    
    private static List<String> _WARNING_LIST = new ArrayList<String>();
    
    private Integer _moduleNo = 0;
    
    public static void addValues2IntList(@Nonnull String value, @Nonnull List<Integer> valueList) {
        String[] values = value.split(",");
        for (String val : values) {
            valueList.add(GsdFileParser.gsdValue2Int(val));
        }
    }
    
//    /**
//     * @param br
//     * @throws IOException
//     */
//    private static void createExtUserPrmData(final BufferedReader br) throws IOException {
        //        // 0 | 1 | 2 | 3 | 4
        //        // ExtUserPrmData = 4 "Flow Sensor Error" ; Byte 2, bit 0
        //        // definition.
        //        String[] lineParts = _LINE.split("[=\";]");
        //        if (lineParts.length < 3) {
        //            CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
        //                                              "The ExtUserPrmData line wrong: " + _LINE);
        //            return;
        //        }
        //        ExtUserPrmData extUserPrmData = new ExtUserPrmData(_SLAVE,gsdValue2Int(lineParts[1]),
        //                                                           lineParts[2].trim());
        //        while (lineIsNotEndBlock(br, "EndExtUserPrmData")) {
        //            // Bit(0) 0 0-1
        //            // BitArea(3-4) 0 0-2
        //            // BitArea(0-6) 6 0,6,10
        //            // Unsigned16 0 0-65535
        //            if (_LINE.startsWith("Prm_Text_Ref")) {
        //                lineParts = _LINE.split("[=;]");
        //                if (lineParts.length < 2) {
        //                    CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
        //                                                      "The ExtUserPrmData line wrong: " + _LINE);
        //                    return;
        //                }
        //                extUserPrmData.setPrmTextRef(lineParts[1].trim());
        //            } else {
        //                lineParts = _LINE.split(";")[0].replaceAll("\\)", "").trim()
        //                        .split("( \\()*[ \\(]+");
        //                if (lineParts.length < 2) {
        //                    CentralLogger.getInstance().error(GsdFileParser.class,
        //                                                      "The ExtUserPrmData line wrong: " + _LINE);
        //                    return;
        //                } else if (lineParts.length == 2) {
        //                    String[] minMax = lineParts[1].split("-");
        //                    if (minMax.length != 2) {
        //                        CentralLogger.getInstance()
        //                                .error(GsdFileParser.class,
        //                                       "The ExtUserPrmData line wrong: " + _LINE);
        //                        return;
        //                    }
        //                    extUserPrmData.setDataType(lineParts[0]);
        //                    extUserPrmData.setMinBit(minMax[0]);
        //                    extUserPrmData.setMaxBit(minMax[1]);
        //                } else {
        //                    // Set DataType.
        //                    extUserPrmData.setDataType(lineParts[0]);
        //                    // Handle DataTypes Bit and BitArea.
        //                    if (lineParts[0].startsWith("Bit") || lineParts[0].startsWith("Unsigned")) {
        //                        // get Bit or Bit Range
        //                        String range = lineParts[1];
        //                        
        //                        switch (lineParts.length) {
        //                            case 3:
        //                                if (lineParts[0].endsWith("8")) {
        //                                    range = "0-7";
        //                                } else if (lineParts[0].endsWith("16")) {
        //                                    range = "0-15";
        //                                }
        //                            case 4:
        //                                try {
        //                                    if (range.contains("-")) {
        //                                        String[] minMax = range.split("-");
        //                                        if (minMax.length != 2) {
        //                                            CentralLogger.getInstance()
        //                                                    .error(GsdFileParser.class.getSimpleName(),
        //                                                           "The ExtUserPrmData line wrong: "
        //                                                                   + _LINE);
        //                                            return;
        //                                        }
        //                                        extUserPrmData.setMinBit(minMax[0]);
        //                                        extUserPrmData.setMaxBit(minMax[1]);
        //                                    } else {
        //                                        extUserPrmData.setMinBit(range);
        //                                        extUserPrmData.setMaxBit(range);
        //                                    }
        //                                } catch (ArrayIndexOutOfBoundsException e) {
        //                                    e.printStackTrace();
        //                                }
        //                                break;
        //                            default:
        //                                CentralLogger.getInstance()
        //                                        .error(GsdFileParser.class.getSimpleName(),
        //                                               "The ExtUserPrmData line wrong: " + _LINE);
        //                                return;
        //                        }
        //                        if (lineParts[0].startsWith("Bit")) {
        //                            extUserPrmData.setDefault(lineParts[2]);
        //                        } else {
        //                            extUserPrmData.setDefault(lineParts[1]);
        //                        }
        //                        if (lineParts[lineParts.length - 1].contains("-")) {
        //                            String[] minMax = lineParts[lineParts.length - 1].split("-");
        //                            if (minMax.length != 2) {
        //                                CentralLogger.getInstance()
        //                                        .error(GsdFileParser.class.getSimpleName(),
        //                                               "The ExtUserPrmData line wrong2: " + _LINE);
        //                                return;
        //                            }
        //                            extUserPrmData.setValueRange(minMax[0], minMax[1]);
        //                        } else if (lineParts[lineParts.length - 1].contains(",")) {
        //                            extUserPrmData.setValues(lineParts[lineParts.length - 1].split(","));
        //                        }
        //                        // Handle Unsigned DataTypes
        //                    } else {
        //                        CentralLogger.getInstance().warn(GsdFileParser.class.getSimpleName(),
        //                                                         "Unknown: " + _LINE);
        //                    }
        //                }
        //            }
        //        }
        //        _SLAVE.addExtUserPrmData(extUserPrmData.getIndex(), extUserPrmData);
//    }
    
    /**
     * @param line
     * @param lineCounter
     * @param br
     * @return
     * @throws IOException
     */
    @Nonnull
    private static KeyValuePair extractKeyValue(@Nonnull final String line,
                                                @Nonnull LineCounter lineCounter,
                                                @Nonnull BufferedReader br) throws IOException {
        try {
            int keyEnd = line.indexOf('=');
            String key = line.substring(0, keyEnd).trim();
            String value = line.substring(keyEnd + 1).trim();
            value = getValue(value, lineCounter, br);
            KeyValuePair keyValuePair = new KeyValuePair(key, value);
            return keyValuePair;
        } catch (IndexOutOfBoundsException e) {
            String warning = String.format("Can't corret handle, at line %s, the Property: %s",
                                           lineCounter,
                                           line);
            LOG.warn(warning);
            _WARNING_LIST.add(warning);
            throw e;
        }
    }
    
    @Nonnull
    public static List<String> getAndClearWarnings() {
        ArrayList<String> arrayList = new ArrayList<String>(_WARNING_LIST);
        _WARNING_LIST.clear();
        return arrayList;
    }
    
//    /**
//     * @param line
//     * @return
//     */
//    private static boolean getBooleanValue(final String line) {
//        int commendIndex = line.indexOf(';');
//        if (commendIndex < 0) {
//            commendIndex = line.length();
//        }
//        int valueStartIndex = line.indexOf('=');
//        if (valueStartIndex > commendIndex) {
//            CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
//                                              "GSD File is wrong in thius line: " + line);
//            return false;
//        }
//        return line.substring(valueStartIndex, commendIndex).trim().equals("1");
//        
//    }
    
    /**
     * @param line
     * @return
     */
    //    private static Byte getByteValue(final String line) {
    //        int commendIndex = line.indexOf(';');
    //        if (commendIndex < 0) {
    //            commendIndex = line.length();
    //        }
    //        int valueStartIndex = line.indexOf('=') + 1;
    //        if (valueStartIndex > commendIndex) {
    //            CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
    //                                              "GSD File is wrong in thius line: " + line);
    //            return -1;
    //            
    //        }
    //        return Byte.valueOf(line.substring(valueStartIndex, commendIndex).trim());
    //    }
    
    /**
     * @param line
     * @param lineCounter
     * @param br
     * @return
     * @throws IOException 
     */
    @Nonnull
    private static String getValue(@Nonnull final String startValue,
                                   @Nonnull LineCounter lineCounter,
                                   @Nonnull final BufferedReader br) throws IOException {
        String value = startValue.trim();
        value = removeComment(value);
        while (value.endsWith("\\")) {
            lineCounter.count();
            value = value.substring(0, value.length() - 1).trim()
                    .concat(br.readLine().split(";")[0].trim());
        }
        return value;
    }
    
    /**
     * @param val
     * @return
     */
    @Nonnull
    public static Integer gsdValue2Int(@Nonnull String value) {
        String tmpValue = value.toLowerCase().trim();
        if(tmpValue.isEmpty()){
            return 0;
        }
        Integer val;
        int radix = 10;
        if (tmpValue.startsWith("0x")) {
            tmpValue = tmpValue.substring(2);
            radix = 16;
        }
        val = Integer.parseInt(tmpValue, radix);
        return val;
    }
    
    //    /**
    //     * @param br
    //     * @param endBlockSting
    //     * @return
    //     * @throws IOException
    //     */
    //    private static boolean lineIsNotEndBlock(final BufferedReader br, final String endBlockSting) throws IOException {
    //        _LINE = br.readLine();
    //        return ((_LINE != null) && !_LINE.trim().startsWith(endBlockSting));
    //    }
    
    //    public static GsdSlaveModel parseSlave(@Nonnull final GSDFileDBO gsdFile,
    //                                           @Nonnull final GsdSlaveModel model) {
    //        _SLAVE = model;
    //        BufferedReader br = null;
    //        try {
    //            br = new BufferedReader(new StringReader(gsdFile.getGSDFile()));
    //            while ((_LINE = br.readLine()) != null) {
    //                _LINE = _LINE.trim();
    //                slave(br);
    //            }
    //        } catch (FileNotFoundException e1) {
    //            CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
    //                                              "The DDB for the GSD File " + gsdFile.getName()
    //                                                      + "(ID: " + gsdFile.getId()
    //                                                      + ") no fiel entry.",
    //                                              e1);
    //        } catch (IOException e) {
    //            CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
    //                                              "The DDB for the GSD File " + gsdFile.getName()
    //                                                      + "(ID: " + gsdFile.getId()
    //                                                      + ") wrong fiel entry.",
    //                                              e);
    //        } finally {
    //            try {
    //                if (br != null) {
    //                    br.close();
    //                    br = null;
    //                }
    //            } catch (IOException e) {
    //                CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
    //                                                  "The DDB for the GSD File " + gsdFile.getName()
    //                                                          + "(ID: " + gsdFile.getId()
    //                                                          + ") wrong fiel entry.",
    //                                                  e);
    //            }
    //        }
    //        return _SLAVE;
    //    }
    
    /**
     * @param value
     * @return
     */
    @Nonnull
    private static String removeComment(@Nonnull String value) {
        String tmpValue = value;
        if (tmpValue.contains(";")) {
            if (tmpValue.startsWith("\"")) {
                int stringEnd = tmpValue.indexOf('"', 1);
                int commentStart = tmpValue.indexOf(';', stringEnd);
                if (commentStart > 0) {
                    tmpValue = tmpValue.substring(0, commentStart);
                }
            } else {
                tmpValue = tmpValue.split(";")[0].trim();
            }
        }
        return tmpValue;
    }
    
    /**
     * @param line
     * @param parsedGsdFileModel
     * @param br
     * @throws IOException 
     */
    private static void setProperty(@Nonnull String line,
                                    @Nonnull LineCounter lineCounter,
                                    @Nonnull ParsedGsdFileModel parsedGsdFileModel,
                                    @Nonnull BufferedReader br) throws IOException {
        try {
            KeyValuePair keyValuePair = extractKeyValue(line, lineCounter, br);
            parsedGsdFileModel.setProperty(keyValuePair);
        } catch (NumberFormatException e) {
            String warning = String
                    .format("Can't corret handle from GSD File %s, at line %s, the Property: %s",
                            parsedGsdFileModel.getName(),
                            lineCounter,
                            line);
            LOG.warn(warning, e);
            _WARNING_LIST.add(warning);
        } catch (IndexOutOfBoundsException e) {
            String warning = String
                    .format("Can't corret handle from GSD File %s, at line %s, the Property: %s",
                            parsedGsdFileModel.getName(),
                            lineCounter,
                            line);
            LOG.warn(warning, e);
            _WARNING_LIST.add(warning);
        }
        
    }
    
    /**
     *
     * @param br
     * @throws IOException
     */
    private static void slave(final BufferedReader br) throws IOException {
        //        if (_LINE.startsWith(";")) {
        //            return;
        //        }
        //        if (_LINE.startsWith("Station_Type")) {
        //            _SLAVE.setStationType(getByteValue(_LINE));
        //        } else if (_LINE.startsWith("Freeze_Mode_supp")) {
        //            _SLAVE.setFreezeModeSupp(getBooleanValue(_LINE));
        //        } else if (_LINE.startsWith("Sync_Mode_supp")) {
        //            _SLAVE.setSyncModeSupp(getBooleanValue(_LINE));
        //        } else if (_LINE.startsWith("Auto_Baud_supp")) {
        //            _SLAVE.setAutoBaudSupp(getBooleanValue(_LINE));
        //        } else if (_LINE.startsWith("Set_Slave_Add_supp")) {
        //            _SLAVE.setSetSlaveAddSupp(getBooleanValue(_LINE));
        //        } else if (_LINE.startsWith("User_Prm_Data_Len")) {
        //            _SLAVE.setUserPrmDataLen(getByteValue(_LINE));
        //        } else if (_LINE.startsWith("Module")) {
        //            while (lineIsNotEndBlock(br, "EndModule")) {
        //                // skip Module lines
        //            }
        //        } else if (_LINE.startsWith("PrmText")) {
        //            String[] lineParts = _LINE.split("[=;]");
        //            String index;
        //            if (lineParts.length < 1) {
        //                CentralLogger.getInstance().error(GsdFileParser.class,
        //                                                  "The PrmText line wrong: " + _LINE);
        //                return;
        //            }
        //            index = lineParts[1].trim();
        //            HashMap<Integer, PrmTextItem> prmText = new HashMap<Integer, PrmTextItem>();
        //            while (lineIsNotEndBlock(br, "EndPrmText")) {
        //                // 0 |1|2| 3
        //                // Text(0) = "POWER ON RESET"
        //                String[] prmTextParts = _LINE.split("[=()]");
        //                Integer value = Integer.parseInt(prmTextParts[1].trim());
        //                prmText.put(value, new PrmTextItem(prmTextParts[3].replaceAll("\"", "").trim(),
        //                                                   value));
        //            }
        //            _SLAVE.addPrmText(index, prmText);
        //        } else if (_LINE.startsWith("ExtUserPrmData")) {
        //            createExtUserPrmData(br);
        //        } else if (_LINE.startsWith("Ext_User_Prm_Data_Const")) {
        //            // 0 |1 |2| 3 | 4
        //            // Ext_User_Prm_Data_Const(13) = 0 ; Reserved
        //            String[] lineParts = _LINE.split("[()=;]");
        //            if (lineParts.length < 4) {
        //                CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
        //                                                  "The Ext_User_Prm_Data_Const line wrong: "
        //                                                          + _LINE);
        //                return;
        //            }
        //            String value = lineParts[3].trim();
        //            while (value.endsWith("\\")) {
        //                value = value.substring(0, value.length() - 1).trim()
        //                        .concat(br.readLine().split(";")[0].trim());
        //            }
        //            
        //            ExtUserPrmDataConst extUserPrmDataConst = new ExtUserPrmDataConst(lineParts[1].trim(),
        //                                                                              value);
        //            _SLAVE.addExtUserPrmDataConst(extUserPrmDataConst.getIndex(), extUserPrmDataConst);
        //            
        //        } else if (_LINE.startsWith("Ext_User_Prm_Data_Ref")) {
        //            // 0 |1| 2 | 3 | 4
        //            // Ext_User_Prm_Data_Ref(5) = 1 ;
        //            String[] lineParts = _LINE.split("[()=;]");
        //            if (lineParts.length < 4) {
        //                CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
        //                                                  "The Ext_User_Prm_Data_Ref line wrong: " + _LINE);
        //                return;
        //            }
        //            ExtUserPrmDataRef extUserPrmDataRef = new ExtUserPrmDataRef(lineParts[1].trim(),
        //                                                                        lineParts[3].trim());
        //            
        //            _SLAVE.addExtUserPrmDataRef(extUserPrmDataRef.getIndex(), extUserPrmDataRef);
        //            
        //        } else if (_LINE.startsWith("Unit_Diag_Bit")) {
        //            // 0 | 1 |2|3| 4 | 5 | 6
        //            // Unit_Diag_Bit(0001) = "Invalid VOR Cmd" ;
        //            String[] lineParts = _LINE.split("[()=\";]");
        //            if (lineParts.length < 5) {
        //                CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
        //                                                  "The Unit_Diag_Bit line wrong: " + _LINE);
        //                return;
        //            }
        //            UnitDiagBit unitDiagBit = new UnitDiagBit(lineParts[1].trim(), lineParts[4].trim());
        //            _SLAVE.addUnitDiagBit(unitDiagBit.getIndex(), unitDiagBit);
        //            
        //        }
    }
    
    /**
     * Default Constructor.
     */
    public GsdFileParser() {
        // Constructor
    }
    
    /**
     * @param line
     * @param parsedGsdFileModel
     * @param br 
     * @throws IOException 
     */
    private void buildExtUserPrmData(@Nonnull String line,
                                     @Nonnull LineCounter lineCounter,
                                     @Nonnull ParsedGsdFileModel parsedGsdFileModel,
                                     @Nonnull BufferedReader br) throws IOException {
        // TODO (hrickens) [28.03.2011]: buildExtUserPrmData
        String[] lineParts = line.split("[\"]");
        assert lineParts.length > 2;
        Integer index = GsdFileParser.gsdValue2Int(lineParts[0].split("=")[1]);
        String text = lineParts[1].split(";")[0].trim();
        
        ExtUserPrmData extUserPrmData = new ExtUserPrmData(parsedGsdFileModel, index, text);
        String tmpLine = br.readLine();
        while (!isLineParameter(tmpLine, "EndExtUserPrmData")) {
            lineCounter.count();
            tmpLine = tmpLine.trim();
            if (isLineParameter(tmpLine, "Prm_Text_Ref")) {
                KeyValuePair extractKeyValue = extractKeyValue(tmpLine, lineCounter, br);
                extUserPrmData.setPrmTextRef(extractKeyValue.getIntValue());
            } else {
                extUserPrmData.buildDataTypeParameter(tmpLine);
            }
            tmpLine = br.readLine();
        }
        parsedGsdFileModel.setExtUserPrmData(extUserPrmData);
    }
    
    /**
     * @param line
     * @param parsedGsdFileModel
     * @param br 
     * @throws IOException 
     */
    private void buildModule(@Nonnull String line,
                             @Nonnull LineCounter lineCounter,
                             @Nonnull ParsedGsdFileModel parsedGsdFileModel,
                             @Nonnull BufferedReader br) throws IOException {
        String[] lineParts = line.split("[\";]");
        assert lineParts.length > 2;
        Integer moduleNo = null;
        String name = lineParts[1].trim();
        
        ArrayList<Integer> valueList = new ArrayList<Integer>();
        String value = getValue(lineParts[2], lineCounter, br);
        GsdFileParser.addValues2IntList(value, valueList);
        
        GsdModuleModel2 gsdModuleModel = new GsdModuleModel2(name, valueList);
        String tmpLine = br.readLine();
        while (!isLineParameter(tmpLine, "endmodule")) {
            lineCounter.count();
            tmpLine = tmpLine.trim();
            if (isLineParameter(tmpLine, ";") || tmpLine.isEmpty()) {
                // do nothing. Is a empty line or a comment;
            } else if (isLineParameter(tmpLine, "Ext_Module_Prm_Data_Len")
                    || isLineParameter(tmpLine, "F_Ext_Module_Prm_Data_Len")) {
                KeyValuePair extractKeyValue = extractKeyValue(tmpLine, lineCounter, br);
                gsdModuleModel.setExtModulePrmDataLen(extractKeyValue.getIntValue());
            } else if (isLineParameter(tmpLine, "Ext_User_Prm_Data_Const")
                    || isLineParameter(tmpLine, "F_Ext_User_Prm_Data_Const")) {
                KeyValuePair extractKeyValue = extractKeyValue(tmpLine, lineCounter, br);
                gsdModuleModel.setExtUserPrmDataConst(extractKeyValue);
            } else if (isLineParameter(tmpLine, "Ext_User_Prm_Data_Ref")
                    || isLineParameter(tmpLine, "F_Ext_User_Prm_Data_Ref")) {
                buildExtUserPrmDataRef(tmpLine, lineCounter, parsedGsdFileModel, gsdModuleModel, br);
//                KeyValuePair extractKeyValue = extractKeyValue(tmpLine, lineCounter, br);
//                gsdModuleModel.setProperty(extractKeyValue);
            } else if (isLineParameter(tmpLine, "Info_Text")) {
                KeyValuePair extractKeyValue = extractKeyValue(tmpLine, lineCounter, br);
                gsdModuleModel.setProperty(extractKeyValue);
            } else if (isLineParameter(tmpLine, "F_ParamDescCRC")) {
                KeyValuePair extractKeyValue = extractKeyValue(tmpLine, lineCounter, br);
                //TODO set F_ParamDescCRC. If this information required or can it be ignored?
            } else if (isLineParameter(tmpLine, "F_IO_StructureDescCRC")) {
                KeyValuePair extractKeyValue = extractKeyValue(tmpLine, lineCounter, br);
                //TODO set F_IO_StructureDescCRC. If this information required or can it be ignored?
            } else {
                try {
                    tmpLine = tmpLine.split(";")[0].trim();
                    moduleNo = gsdValue2Int(tmpLine);
                } catch (NumberFormatException e) {
                    String warning = String
                            .format("Module parameter wrong at GSD File %s in line %s. Line: %s",
                                    parsedGsdFileModel.getName(),
                                    lineCounter,
                                    tmpLine);
                    LOG.warn(warning);
                    _WARNING_LIST.add(warning);
                }
            }
            tmpLine = br.readLine();
        }
        if (moduleNo == null) {
            moduleNo = _moduleNo++;
        } else if (_moduleNo < moduleNo) {
            _moduleNo = moduleNo+1;
        }
        gsdModuleModel.setModuleNumber(moduleNo);
        parsedGsdFileModel.setModule(gsdModuleModel);
    }
    
    /**
     * @param line
     * @param parsedGsdFileModel
     * @param br 
     * @throws IOException 
     */
    private void buildPrmText(@Nonnull String line,
                              @Nonnull LineCounter lineCounter,
                              @Nonnull ParsedGsdFileModel parsedGsdFileModel,
                              @Nonnull BufferedReader br) throws IOException {
        KeyValuePair prmTextKeyValue = extractKeyValue(line, lineCounter, br);
        Integer index = prmTextKeyValue.getIntValue();
        PrmText prmText = new PrmText(index);
        String tmpLine = br.readLine();
        while (!isLineParameter(tmpLine, "EndPrmText")) {
            lineCounter.count();
            tmpLine = tmpLine.trim();
            KeyValuePair prmItemKeyValue = extractKeyValue(tmpLine, lineCounter, br);
            prmText.setPrmTextItem(prmItemKeyValue);
            tmpLine = br.readLine();
        }
        parsedGsdFileModel.putPrmText(prmText);
    }
    
    private void buildSlotDefinition(@Nonnull String line,
                                     @Nonnull LineCounter lineCounter,
                                     @Nonnull AbstractGsdPropertyModel parsedGsdFileModel,
                                     @Nonnull BufferedReader br) throws IOException {
        // XXX (hrickens) [28.03.2011]:  buildSlotDefinition. If this information required or can it be ignored?
        String tmpLine = line;
        while (!isLineParameter(tmpLine, "EndSlotDefinition")) {
            lineCounter.count();
            tmpLine = tmpLine.trim();
            tmpLine = br.readLine();
        }
    }
    
    private void buildUnitDiagArea(@Nonnull String line,
                                   @Nonnull LineCounter lineCounter,
                                   @Nonnull AbstractGsdPropertyModel parsedGsdFileModel,
                                   @Nonnull BufferedReader br) throws IOException {
        // XXX (hrickens) [28.03.2011]: buildUnitDiagArea. If this information required or can it be ignored? 
        String tmpLine = line;
        while (!isLineParameter(tmpLine, "Unit_Diag_Area_End")) {
            lineCounter.count();
            tmpLine = tmpLine.trim();
            tmpLine = br.readLine();
        }
        
    }
    
    private void buildUnitDiagType(@Nonnull String line,
                                   @Nonnull LineCounter lineCounter,
                                   @Nonnull AbstractGsdPropertyModel parsedGsdFileModel,
                                   @Nonnull BufferedReader br) throws IOException {
        // XXX (hrickens) [28.03.2011]: buildUnitDiagType. If this information required or can it be ignored?
        String tmpLine = line;
        while (!isLineParameter(tmpLine, "EndUnitDiagType")) {
            lineCounter.count();
            tmpLine = tmpLine.trim();
            tmpLine = br.readLine();
        }
    }
    
    private boolean isLineParameter(@CheckForNull String line, @Nonnull String parameter) {
        return ((line != null) && (line.toLowerCase().startsWith(parameter.toLowerCase())));
    }
    
    /**
     * @param br
     * @param parsedGsdFileModel 
     * @return
     * @throws IOException 
     */
    @Nonnull
    private ParsedGsdFileModel parse(@Nonnull BufferedReader br,
                                     @Nonnull ParsedGsdFileModel parsedGsdFileModel) throws IOException {
        String line;
        LineCounter lineCounter = new LineCounter();
        while ((line = br.readLine()) != null) {
            lineCounter.count();
            line = line.trim();
            if (isLineParameter(line, ";") || line.isEmpty()) {
                // skip line,  is a comment or empty
                continue;
            } else if (isLineParameter(line, "#")) {
                // contain Profibus Type
                continue;
            } else if (isLineParameter(line, "PrmText")) {
                buildPrmText(line, lineCounter, parsedGsdFileModel, br);
            } else if (isLineParameter(line, "User_Prm_Data_Len")) {
                setProperty(line, lineCounter, parsedGsdFileModel, br);
            } else if (isLineParameter(line, "User_Prm_Data")) {
                buildExtUserPrmDataConst(line, lineCounter, parsedGsdFileModel, br);
            } else if (isLineParameter(line, "Ext_User_Prm_Data_Const")) {
                buildExtUserPrmDataConst(line, lineCounter, parsedGsdFileModel, br);
            } else if (isLineParameter(line, "Ext_User_Prm_Data_Ref")) {
                buildExtUserPrmDataRef(line, lineCounter, parsedGsdFileModel, br);
            } else if (isLineParameter(line, "ExtUserPrmData")) {
                buildExtUserPrmData(line, lineCounter, parsedGsdFileModel, br);
            } else if (isLineParameter(line, "Module")) {
                buildModule(line, lineCounter, parsedGsdFileModel, br);
            } else if (isLineParameter(line, "UnitDiagType")) {
                buildUnitDiagType(line, lineCounter, parsedGsdFileModel, br);
            } else if (isLineParameter(line, "SlotDefinition")) {
                buildSlotDefinition(line, lineCounter, parsedGsdFileModel, br);
            } else if (isLineParameter(line, "Unit_Diag_Area")) {
                buildUnitDiagArea(line, lineCounter, parsedGsdFileModel, br);
            } else if (isLineParameter(line, "X_Unit_Diag_Area")) {
                buildUnitDiagArea(line, lineCounter, parsedGsdFileModel, br);
            } else if (isLineParameter(line, "Slave_Family")) {
                // TODO (hrickens) [28.03.2011]: Hier könnte man den Text noch als zweite variante setzen. (Das was nach dem @ kommt)
                setProperty(line.split("@")[0], lineCounter, parsedGsdFileModel, br);
            } else if (isLineParameter(line, "End_Physical_Interface")) {
                continue; // unused property
            } else {
                setProperty(line, lineCounter, parsedGsdFileModel, br);
            }
        }
        return parsedGsdFileModel;
    }
    
    private void buildExtUserPrmDataRef(@Nonnull String line,
                                        @Nonnull LineCounter lineCounter,
                                        @Nonnull ParsedGsdFileModel parsedGsdFileModel,
                                        @Nonnull BufferedReader br) throws IOException {
        KeyValuePair keyValuePair = extractKeyValue(line, lineCounter, br);
        ExtUserPrmData eupd = parsedGsdFileModel.getExtUserPrmData(keyValuePair.getIntValue());
        parsedGsdFileModel.setExtUserPrmDataDefault(eupd, keyValuePair.getIndex());
        parsedGsdFileModel.setExtUserPrmDataRef(keyValuePair);
    }
    
    private void buildExtUserPrmDataRef(@Nonnull String line,
                                        @Nonnull LineCounter lineCounter,
                                        @Nonnull ParsedGsdFileModel parsedGsdFileModel,
                                        @Nonnull AbstractGsdPropertyModel abstractGsdPropertyModel,
                                        @Nonnull BufferedReader br) throws IOException {
        KeyValuePair keyValuePair = extractKeyValue(line, lineCounter, br);
        ExtUserPrmData eupd = parsedGsdFileModel.getExtUserPrmData(keyValuePair.getIntValue());
        abstractGsdPropertyModel.setExtUserPrmDataDefault(eupd, keyValuePair.getIndex());
        abstractGsdPropertyModel.setExtUserPrmDataRef(keyValuePair);
    }
    
    /**
     * @param line
     * @param lineCounter
     * @param parsedGsdFileModel
     * @param br
     * @throws IOException 
     */
    private void buildExtUserPrmDataConst(@Nonnull String line,
                                          @Nonnull LineCounter lineCounter,
                                          @Nonnull AbstractGsdPropertyModel parsedGsdFileModel,
                                          @Nonnull BufferedReader br) throws IOException {
        KeyValuePair keyValuePair = extractKeyValue(line, lineCounter, br);
        parsedGsdFileModel.setExtUserPrmDataConst(keyValuePair);
    }
    
    /**
     * @param fileAsString
     * @return
     * @throws IOException 
     */
    @Nonnull
    public ParsedGsdFileModel parse(@Nonnull GSDFileDBO gsdFileDBO) throws IOException {
        StringReader sr = new StringReader(gsdFileDBO.getGSDFile());
        BufferedReader br = new BufferedReader(sr);
        try {
            ParsedGsdFileModel parsedGsdFileModel = new ParsedGsdFileModel(gsdFileDBO.getName());
            return parse(br, parsedGsdFileModel);
        } finally {
            if (br != null) {
                br.close();
                br = null;
            }
            if (sr != null) {
                sr.close();
                sr = null;
            }
        }
    }

    @Nonnull
    public static String intList2HexString(@Nonnull List<Integer> intList) {
        StringBuilder sb = new StringBuilder();
        for (Integer value : intList) {
            sb.append(String.format("0x%02X,", value));
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
