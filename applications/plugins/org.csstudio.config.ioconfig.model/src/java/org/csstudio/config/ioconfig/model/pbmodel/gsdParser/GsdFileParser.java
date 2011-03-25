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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.HashMap;

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
    
    /**
     * The actual line of gsd file.
     */
    private static String _LINE;
    private static GsdSlaveModel _SLAVE;
    
    /**
     * Default Constructor.
     */
    private GsdFileParser() {
        
    }
    
    public static GsdSlaveModel parseSlave(final GSDFileDBO gsdFile, final GsdSlaveModel model) {
        _SLAVE = model;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new StringReader(gsdFile.getGSDFile()));
            while ((_LINE = br.readLine()) != null) {
                _LINE = _LINE.trim();
                slave(br);
            }
        } catch (FileNotFoundException e1) {
            CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
                                              "The DDB for the GSD File " + gsdFile.getName()
                                                      + "(ID: " + gsdFile.getId()
                                                      + ") no fiel entry.",
                                              e1);
        } catch (IOException e) {
            CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
                                              "The DDB for the GSD File " + gsdFile.getName()
                                                      + "(ID: " + gsdFile.getId()
                                                      + ") wrong fiel entry.",
                                              e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                    br = null;
                }
            } catch (IOException e) {
                CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
                                                  "The DDB for the GSD File " + gsdFile.getName()
                                                          + "(ID: " + gsdFile.getId()
                                                          + ") wrong fiel entry.",
                                                  e);
            }
        }
        return _SLAVE;
    }
    
    /**
     *
     * @param br
     * @throws IOException
     */
    private static void slave(final BufferedReader br) throws IOException {
        if (_LINE.startsWith(";")) {
            return;
        }
        if (_LINE.startsWith("Station_Type")) {
            _SLAVE.setStationType(getByteValue(_LINE));
        } else if (_LINE.startsWith("Freeze_Mode_supp")) {
            _SLAVE.setFreezeModeSupp(getBooleanValue(_LINE));
        } else if (_LINE.startsWith("Sync_Mode_supp")) {
            _SLAVE.setSyncModeSupp(getBooleanValue(_LINE));
        } else if (_LINE.startsWith("Auto_Baud_supp")) {
            _SLAVE.setAutoBaudSupp(getBooleanValue(_LINE));
        } else if (_LINE.startsWith("Set_Slave_Add_supp")) {
            _SLAVE.setSetSlaveAddSupp(getBooleanValue(_LINE));
        } else if (_LINE.startsWith("User_Prm_Data_Len")) {
            _SLAVE.setUserPrmDataLen(getByteValue(_LINE));
        } else if (_LINE.startsWith("Module")) {
            while (lineIsNotEndBlock(br, "EndModule")) {
                ;// skip Module lines
            }
        } else if (_LINE.startsWith("PrmText")) {
            String[] lineParts = _LINE.split("[=;]");
            String index;
            if (lineParts.length < 1) {
                CentralLogger.getInstance().error(GsdFileParser.class,
                                                  "The PrmText line wrong: " + _LINE);
                return;
            }
            index = lineParts[1].trim();
            HashMap<Integer, PrmText> prmText = new HashMap<Integer, PrmText>();
            while (lineIsNotEndBlock(br, "EndPrmText")) {
                // 0 |1|2| 3
                // Text(0) = "POWER ON RESET"
                String[] prmTextParts = _LINE.split("[=()]");
                Integer value = Integer.parseInt(prmTextParts[1].trim());
                prmText.put(value, new PrmText(prmTextParts[3].replaceAll("\"", "").trim(), value));
            }
            _SLAVE.addPrmText(index, prmText);
        } else if (_LINE.startsWith("ExtUserPrmData")) {
            createExtUserPrmData(br);
        } else if (_LINE.startsWith("Ext_User_Prm_Data_Const")) {
            // 0 |1 |2| 3 | 4
            // Ext_User_Prm_Data_Const(13) = 0 ; Reserved
            String[] lineParts = _LINE.split("[()=;]");
            if (lineParts.length < 4) {
                CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
                                                  "The Ext_User_Prm_Data_Const line wrong: "
                                                          + _LINE);
                return;
            }
            String value = lineParts[3].trim();
            while (value.endsWith("\\")) {
                value = value.substring(0, value.length() - 1).trim()
                        .concat(br.readLine().split(";")[0].trim());
            }
            
            ExtUserPrmDataConst extUserPrmDataConst = new ExtUserPrmDataConst(lineParts[1].trim(),
                                                                              value);
            _SLAVE.addExtUserPrmDataConst(extUserPrmDataConst.getIndex(), extUserPrmDataConst);
            
        } else if (_LINE.startsWith("Ext_User_Prm_Data_Ref")) {
            // 0 |1| 2 | 3 | 4
            // Ext_User_Prm_Data_Ref(5) = 1 ;
            String[] lineParts = _LINE.split("[()=;]");
            if (lineParts.length < 4) {
                CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
                                                  "The Ext_User_Prm_Data_Ref line wrong: " + _LINE);
                return;
            }
            ExtUserPrmDataRef extUserPrmDataRef = new ExtUserPrmDataRef(lineParts[1].trim(),
                                                                        lineParts[3].trim());
            
            _SLAVE.addExtUserPrmDataRef(extUserPrmDataRef.getIndex(), extUserPrmDataRef);
            
        } else if (_LINE.startsWith("Unit_Diag_Bit")) {
            // 0 | 1 |2|3| 4 | 5 | 6
            // Unit_Diag_Bit(0001) = "Invalid VOR Cmd" ;
            String[] lineParts = _LINE.split("[()=\";]");
            if (lineParts.length < 5) {
                CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
                                                  "The Unit_Diag_Bit line wrong: " + _LINE);
                return;
            }
            UnitDiagBit unitDiagBit = new UnitDiagBit(lineParts[1].trim(), lineParts[4].trim());
            _SLAVE.addUnitDiagBit(unitDiagBit.getIndex(), unitDiagBit);
            
        }
    }
    
    /**
     * @param br
     * @throws IOException
     */
    private static void createExtUserPrmData(final BufferedReader br) throws IOException {
        // 0 | 1 | 2 | 3 | 4
        // ExtUserPrmData = 4 "Flow Sensor Error" ; Byte 2, bit 0
        // definition.
        String[] lineParts = _LINE.split("[=\";]");
        if (lineParts.length < 3) {
            CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
                                              "The ExtUserPrmData line wrong: " + _LINE);
            return;
        }
        ExtUserPrmData extUserPrmData = new ExtUserPrmData(_SLAVE,
                                                           lineParts[1].trim(),
                                                           lineParts[2].trim());
        while (lineIsNotEndBlock(br, "EndExtUserPrmData")) {
            // Bit(0) 0 0-1
            // BitArea(3-4) 0 0-2
            // BitArea(0-6) 6 0,6,10
            // Unsigned16 0 0-65535
            if (_LINE.startsWith("Prm_Text_Ref")) {
                lineParts = _LINE.split("[=;]");
                if (lineParts.length < 2) {
                    CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
                                                      "The ExtUserPrmData line wrong: " + _LINE);
                    return;
                }
                extUserPrmData.setPrmTextRef(lineParts[1].trim());
            } else {
                lineParts = _LINE.split(";")[0].replaceAll("\\)", "").trim()
                        .split("( \\()*[ \\(]+");
                if (lineParts.length < 2) {
                    CentralLogger.getInstance().error(GsdFileParser.class,
                                                      "The ExtUserPrmData line wrong: " + _LINE);
                    return;
                } else if (lineParts.length == 2) {
                    String[] minMax = lineParts[1].split("-");
                    if (minMax.length != 2) {
                        CentralLogger.getInstance()
                                .error(GsdFileParser.class,
                                       "The ExtUserPrmData line wrong: " + _LINE);
                        return;
                    }
                    extUserPrmData.setDataType(lineParts[0]);
                    extUserPrmData.setMinBit(minMax[0]);
                    extUserPrmData.setMaxBit(minMax[1]);
                } else {
                    // Set DataType.
                    extUserPrmData.setDataType(lineParts[0]);
                    // Handle DataTypes Bit and BitArea.
                    if (lineParts[0].startsWith("Bit") || lineParts[0].startsWith("Unsigned")) {
                        // get Bit or Bit Range
                        String range = lineParts[1];
                        
                        switch (lineParts.length) {
                            case 3:
                                if (lineParts[0].endsWith("8")) {
                                    range = "0-7";
                                } else if (lineParts[0].endsWith("16")) {
                                    range = "0-15";
                                }
                            case 4:
                                try {
                                    if (range.contains("-")) {
                                        String[] minMax = range.split("-");
                                        if (minMax.length != 2) {
                                            CentralLogger.getInstance()
                                                    .error(GsdFileParser.class.getSimpleName(),
                                                           "The ExtUserPrmData line wrong: "
                                                                   + _LINE);
                                            return;
                                        }
                                        extUserPrmData.setMinBit(minMax[0]);
                                        extUserPrmData.setMaxBit(minMax[1]);
                                    } else {
                                        extUserPrmData.setMinBit(range);
                                        extUserPrmData.setMaxBit(range);
                                    }
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                CentralLogger.getInstance()
                                        .error(GsdFileParser.class.getSimpleName(),
                                               "The ExtUserPrmData line wrong: " + _LINE);
                                return;
                        }
                        if (lineParts[0].startsWith("Bit")) {
                            extUserPrmData.setDefault(lineParts[2]);
                        } else {
                            extUserPrmData.setDefault(lineParts[1]);
                        }
                        if (lineParts[lineParts.length - 1].contains("-")) {
                            String[] minMax = lineParts[lineParts.length - 1].split("-");
                            if (minMax.length != 2) {
                                CentralLogger.getInstance()
                                        .error(GsdFileParser.class.getSimpleName(),
                                               "The ExtUserPrmData line wrong2: " + _LINE);
                                return;
                            }
                            extUserPrmData.setValueRange(minMax[0], minMax[1]);
                        } else if (lineParts[lineParts.length - 1].contains(",")) {
                            extUserPrmData.setValues(lineParts[lineParts.length - 1].split(","));
                        }
                        // Handle Unsigned DataTypes
                    } else {
                        CentralLogger.getInstance().warn(GsdFileParser.class.getSimpleName(),
                                                         "Unknown: " + _LINE);
                    }
                }
            }
        }
        _SLAVE.addExtUserPrmData(extUserPrmData.getIndex(), extUserPrmData);
    }
    
    /**
     * @param br
     * @param endBlockSting
     * @return
     * @throws IOException
     */
    private static boolean lineIsNotEndBlock(final BufferedReader br, final String endBlockSting) throws IOException {
        _LINE = br.readLine();
        return ((_LINE != null) && !_LINE.trim().startsWith(endBlockSting));
    }
    
    /**
     * @param line
     * @return
     * @throws ParseException
     */
    private static boolean getBooleanValue(final String line) {
        int commendIndex = line.indexOf(';');
        if (commendIndex < 0) {
            commendIndex = line.length();
        }
        int valueStartIndex = line.indexOf('=');
        if (valueStartIndex > commendIndex) {
            CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
                                              "GSD File is wrong in thius line: " + line);
            return false;
        }
        return line.substring(valueStartIndex, commendIndex).trim().equals("1");
        
    }
    
    /**
     * @param line
     * @return
     * @throws ParseException
     */
    private static Byte getByteValue(final String line) {
        int commendIndex = line.indexOf(';');
        if (commendIndex < 0) {
            commendIndex = line.length();
        }
        int valueStartIndex = line.indexOf('=') + 1;
        if (valueStartIndex > commendIndex) {
            CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
                                              "GSD File is wrong in thius line: " + line);
            return -1;
            
        }
        return Byte.valueOf(line.substring(valueStartIndex, commendIndex).trim());
    }
    
    /**
     * @param fileAsString
     * @return
     * @throws IOException 
     */
    public static ParsedGsdFileModel parse(GSDFileDBO gsdFileDBO) throws IOException {
        StringReader sr = new StringReader(gsdFileDBO.getGSDFile());
        try {
            BufferedReader br = new BufferedReader(sr);
            ParsedGsdFileModel parsedGsdFileModel = new ParsedGsdFileModel(gsdFileDBO.getName());
            return parse(br, parsedGsdFileModel);
        } finally {
            if (sr != null) {
                sr.close();
                sr = null;
            }
        }
    }
    
    /**
     * @param br
     * @param parsedGsdFileModel 
     * @return
     * @throws IOException 
     */
    @Nonnull
    private static ParsedGsdFileModel parse(@Nonnull BufferedReader br,
                                            @Nonnull ParsedGsdFileModel parsedGsdFileModel) throws IOException {
        String line;
        int lineCounter = 0;
        try {
            while ((line = br.readLine()) != null) {
                lineCounter++;
                line = line.trim();
                if (line.startsWith(";") || line.isEmpty()) {
                    // skip line,  is a comment or empty
                    continue;
                } else if (line.startsWith("#")) {
                    // contain Profibus Type
                    continue;
                } else if (line.startsWith("PrmText")) {
                    buildPrmText(line, lineCounter, parsedGsdFileModel, br);
                } else if (line.startsWith("ExtUserPrmData")) {
                    buildExtUserPrmData(line, lineCounter, parsedGsdFileModel, br);
                } else if (line.startsWith("Module")) {
                    buildModule(line, lineCounter, parsedGsdFileModel, br);
                } else if (line.startsWith("UnitDiagType")) {
                    buildUnitDiagType(line, lineCounter, parsedGsdFileModel, br);
                } else if (line.startsWith("SlotDefinition")) {
                    buildSlotDefinition(line, lineCounter, parsedGsdFileModel, br);
                } else if (line.startsWith("Unit_Diag_Area")) {
                    buildUnitDiagArea(line, lineCounter, parsedGsdFileModel, br);
                } else if (line.startsWith("X_Unit_Diag_Area")) {
                    buildUnitDiagArea(line, lineCounter, parsedGsdFileModel, br);
                } else {
                    setProperty(line, lineCounter, parsedGsdFileModel, br);
                }
                
                // TODO(hrickens) [25.03.2011]: create and fill ParsedGsdFileModel
            }
        } finally {
            if (br != null) {
                br.close();
                br = null;
            }
        }
        return parsedGsdFileModel;
    }
    
    /**
     * @param line
     * @param lineCounter
     * @param parsedGsdFileModel
     * @param br
     * @throws IOException 
     */
    private static void buildUnitDiagType(@Nonnull String line,
                                          int lineCounter,
                                          @Nonnull ParsedGsdFileModel parsedGsdFileModel,
                                          @Nonnull BufferedReader br) throws IOException {
        while ((line = br.readLine()) != null && !line.startsWith("EndUnitDiagType")) {
            lineCounter++;
            line = line.trim();
        }       
    }

    /**
     * @param line
     * @param lineCounter
     * @param parsedGsdFileModel
     * @param br
     * @throws IOException 
     */
    private static void buildSlotDefinition(@Nonnull String line,
                                            int lineCounter,
                                            @Nonnull ParsedGsdFileModel parsedGsdFileModel,
                                            @Nonnull BufferedReader br) throws IOException {
        while ((line = br.readLine()) != null && !line.startsWith("EndSlotDefinition")) {
            lineCounter++;
            line = line.trim();
        }        
    }

    /**
     * @param line
     * @param lineCounter
     * @param parsedGsdFileModel
     * @param br
     * @throws IOException 
     */
    private static void buildUnitDiagArea(@Nonnull String line,
                                          int lineCounter,
                                          @Nonnull ParsedGsdFileModel parsedGsdFileModel,
                                          @Nonnull BufferedReader br) throws IOException {
        while ((line = br.readLine()) != null && !line.endsWith("Unit_Diag_Area_End")) {
            lineCounter++;
            line = line.trim();
        }
        
    }

    /**
     * @param line
     * @param parsedGsdFileModel
     * @param br
     * @throws IOException 
     */
    private static void setProperty(@Nonnull String line, int lineCounter, 
                                    @Nonnull ParsedGsdFileModel parsedGsdFileModel,
                                    @Nonnull BufferedReader br) throws IOException {
        String[] split = line.split("=");
        if(split.length==2) {
            String key = split[0].trim();
            String value = getValue(split[1], lineCounter, br);
        } else {
            LOG.error(String.format("Wrong GSD File poperty at line %d: %s",lineCounter,line));
        }
        
    }
    
    /**
     * @param line
     * @param lineCounter
     * @param br
     * @return
     * @throws IOException 
     */
    @Nonnull
    private static String getValue(@Nonnull final String startValue, int lineCounter, @Nonnull final BufferedReader br) throws IOException {
        String value = startValue;
        while (value.endsWith("\\")) {
            lineCounter++;
            value = value.substring(0, value.length() - 1).trim()
                    .concat(br.readLine().split(";")[0].trim());
        }
        return value;
    }

    /**
     * @param line
     * @param parsedGsdFileModel
     * @param br 
     * @throws IOException 
     */
    private static void buildModule(@Nonnull String line, int lineCounter,
                                    @Nonnull ParsedGsdFileModel parsedGsdFileModel,
                                    @Nonnull BufferedReader br) throws IOException {
        while ((line = br.readLine()) != null && !line.startsWith("EndModule")) {
            lineCounter++;
            line = line.trim();
        }
    }
    
    /**
     * @param line
     * @param parsedGsdFileModel
     * @param br 
     * @throws IOException 
     */
    private static void buildExtUserPrmData(@Nonnull String line, int lineCounter,
                                            @Nonnull ParsedGsdFileModel parsedGsdFileModel,
                                            @Nonnull BufferedReader br) throws IOException {
        while ((line = br.readLine()) != null && !line.startsWith("EndExtUserPrmData")) {
            lineCounter++;
            line = line.trim();
        }
    }
    
    /**
     * @param line
     * @param parsedGsdFileModel
     * @param br 
     * @throws IOException 
     */
    private static void buildPrmText(@Nonnull String line, int lineCounter,
                                     @Nonnull ParsedGsdFileModel parsedGsdFileModel,
                                     @Nonnull BufferedReader br) throws IOException {
        while ((line = br.readLine()) != null && !line.startsWith("EndPrmText")) {
            lineCounter++;
            line = line.trim();
        }
    }
    
}
