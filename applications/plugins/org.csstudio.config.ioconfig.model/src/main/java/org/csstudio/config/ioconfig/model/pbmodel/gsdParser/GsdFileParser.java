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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.HashMap;

import org.csstudio.config.ioconfig.model.pbmodel.GSDFile;
import org.csstudio.platform.logging.CentralLogger;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 18.07.2008
 */
public final class GsdFileParser {

    /**
     * The actual line of gsd file.
     */
    private static String _line;
    private static GsdSlaveModel _model;

    /**
     * Default Constructor.
     */
    private GsdFileParser() {

    }

    public static GsdSlaveModel parseSlave(GSDFile gsdFile, GsdSlaveModel model) {
        _model = model;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new StringReader(gsdFile.getGSDFile()));
            while ((_line = br.readLine()) != null) {
                _line = _line.trim();
                slave(br);
            }
        } catch (FileNotFoundException e1) {
            CentralLogger.getInstance().error(
                    GsdFileParser.class.getSimpleName(),
                    "The DDB for the GSD File " + gsdFile.getName() + "(ID: " + gsdFile.getId()
                            + ") no fiel entry.", e1);
        } catch (IOException e) {
            CentralLogger.getInstance().error(
                    GsdFileParser.class.getSimpleName(),
                    "The DDB for the GSD File " + gsdFile.getName() + "(ID: " + gsdFile.getId()
                            + ") wrong fiel entry.", e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                    br = null;
                }
            } catch (IOException e) {
                CentralLogger.getInstance().error(
                        GsdFileParser.class.getSimpleName(),
                        "The DDB for the GSD File " + gsdFile.getName() + "(ID: " + gsdFile.getId()
                                + ") wrong fiel entry.", e);
            }
        }
        return _model;
    }

    /**
     * 
     * @param br
     * @throws IOException
     */
    private static void slave(BufferedReader br) throws IOException {
        if (_line.startsWith(";")) {
            return;
        }
        if (_line.startsWith("Station_Type")) {
            _model.setStationType(getByteValue(_line));
        } else if (_line.startsWith("Freeze_Mode_supp")) {
            _model.setFreezeModeSupp(getBooleanValue(_line));
        } else if (_line.startsWith("Sync_Mode_supp")) {
            _model.setSyncModeSupp(getBooleanValue(_line));
        } else if (_line.startsWith("Auto_Baud_supp")) {
            _model.setAutoBaudSupp(getBooleanValue(_line));
        } else if (_line.startsWith("Set_Slave_Add_supp")) {
            _model.setSetSlaveAddSupp(getBooleanValue(_line));
        } else if (_line.startsWith("User_Prm_Data_Len")) {
            _model.setUserPrmDataLen(getByteValue(_line));
        } else if (_line.startsWith("Module")) {

            while (lineIsNotEndBlock(br, "EndModule")) {

            }
        } else if (_line.startsWith("PrmText")) {
            String[] lineParts = _line.split("[=;]");
            String index;
            if (lineParts.length < 1) {
                CentralLogger.getInstance().error(GsdFileParser.class,
                        "The PrmText line wrong: " + _line);
                return;
            }
            index = lineParts[1].trim();
            HashMap<Integer, PrmText> prmText = new HashMap<Integer, PrmText>();
            while (lineIsNotEndBlock(br, "EndPrmText")) {
                // 0 |1|2| 3
                // Text(0) = "POWER ON RESET"
                String[] prmTextParts = _line.split("[=()]");
                Integer value = Integer.parseInt(prmTextParts[1].trim());
                prmText.put(value, new PrmText(prmTextParts[3].replaceAll("\"", "").trim(), value));
            }
            _model.addPrmText(index, prmText);
        } else if (_line.startsWith("ExtUserPrmData")) {
            // 0 | 1 | 2 | 3 | 4
            // ExtUserPrmData = 4 "Flow Sensor Error" ; Byte 2, bit 0
            // definition.
            String[] lineParts = _line.split("[=\";]");
            if (lineParts.length < 3) {
                CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
                        "The ExtUserPrmData line wrong: " + _line);
                return;
            }
            ExtUserPrmData extUserPrmData = new ExtUserPrmData(_model, lineParts[1].trim(),
                    lineParts[2].trim());
            while (lineIsNotEndBlock(br, "EndExtUserPrmData")) {
                // Bit(0) 0 0-1
                // BitArea(3-4) 0 0-2
                // BitArea(0-6) 6 0,6,10
                // Unsigned16 0 0-65535
                if (_line.startsWith("Prm_Text_Ref")) {
                    lineParts = _line.split("[=;]");
                    if (lineParts.length < 2) {
                        CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
                                "The ExtUserPrmData line wrong: " + _line);
                        return;
                    }
                    extUserPrmData.setPrmTextRef(lineParts[1].trim());
                } else {
                    lineParts = _line.split(";")[0].replaceAll("\\)", "").trim().split(
                            "( \\()*[ \\(]+");
                    if (lineParts.length < 2) {
                        CentralLogger.getInstance().error(GsdFileParser.class,
                                "The ExtUserPrmData line wrong: " + _line);
                        return;
                    } else if (lineParts.length == 2) {
                        String[] minMax = lineParts[1].split("-");
                        if (minMax.length != 2) {
                            CentralLogger.getInstance().error(GsdFileParser.class,
                                    "The ExtUserPrmData line wrong: " + _line);
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
                                                CentralLogger.getInstance().error(
                                                        GsdFileParser.class.getSimpleName(),
                                                        "The ExtUserPrmData line wrong: " + _line);
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
                                    CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
                                            "The ExtUserPrmData line wrong: " + _line);
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
                                    CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
                                            "The ExtUserPrmData line wrong2: " + _line);
                                    return;
                                }
                                extUserPrmData.setValueRange(minMax[0], minMax[1]);
                            } else if (lineParts[lineParts.length - 1].contains(",")) {
                                extUserPrmData
                                        .setValues(lineParts[lineParts.length - 1].split(","));
                            }
                            // Handle Unsigned DataTypes
                        } else {
                            CentralLogger.getInstance().warn(GsdFileParser.class.getSimpleName(),
                                    "Unknown: " + _line);
                        }
                    }
                }
            }
            _model.addExtUserPrmData(extUserPrmData.getIndex(), extUserPrmData);
        } else if (_line.startsWith("Ext_User_Prm_Data_Const")) {
            // 0 |1 |2| 3 | 4
            // Ext_User_Prm_Data_Const(13) = 0 ; Reserved
            String[] lineParts = _line.split("[()=;]");
            if (lineParts.length < 4) {
                CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
                        "The Ext_User_Prm_Data_Const line wrong: " + _line);
                return;
            }
            String value = lineParts[3].trim();
            while (value.endsWith("\\")) {
                value = value.substring(0, value.length() - 1).trim().concat(
                        br.readLine().split(";")[0].trim());
            }

            ExtUserPrmDataConst extUserPrmDataConst = new ExtUserPrmDataConst(lineParts[1].trim(),
                    value);
            _model.addExtUserPrmDataConst(extUserPrmDataConst.getIndex(), extUserPrmDataConst);

        } else if (_line.startsWith("Ext_User_Prm_Data_Ref")) {
            // 0 |1| 2 | 3 | 4
            // Ext_User_Prm_Data_Ref(5) = 1 ;
            String[] lineParts = _line.split("[()=;]");
            if (lineParts.length < 4) {
                CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
                        "The Ext_User_Prm_Data_Ref line wrong: " + _line);
                return;
            }
            ExtUserPrmDataRef extUserPrmDataRef = new ExtUserPrmDataRef(lineParts[1].trim(),
                    lineParts[3].trim());

            _model.addExtUserPrmDataRef(extUserPrmDataRef.getIndex(), extUserPrmDataRef);

        } else if (_line.startsWith("Unit_Diag_Bit")) {
            // 0 | 1 |2|3| 4 | 5 | 6
            // Unit_Diag_Bit(0001) = "Invalid VOR Cmd" ;
            String[] lineParts = _line.split("[()=\";]");
            if (lineParts.length < 5) {
                CentralLogger.getInstance().error(GsdFileParser.class.getSimpleName(),
                        "The Unit_Diag_Bit line wrong: " + _line);
                return;
            }
            UnitDiagBit unitDiagBit = new UnitDiagBit(lineParts[1].trim(), lineParts[4].trim());
            _model.addUnitDiagBit(unitDiagBit.getIndex(), unitDiagBit);

        }
    }

    /**
     * @param br
     * @param endBlockSting
     * @return
     * @throws IOException
     */
    private static boolean lineIsNotEndBlock(BufferedReader br, String endBlockSting)
            throws IOException {
        _line = br.readLine();
        return (_line != null && !_line.trim().startsWith(endBlockSting));
    }

    /**
     * @param line
     * @return
     * @throws ParseException
     */
    private static boolean getBooleanValue(String line) {
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
    private static Byte getByteValue(String line) {
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

}
