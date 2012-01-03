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
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.3 $
 * @since 18.07.2008
 */
public final class GsdFileParser {

    private static final Logger LOG = LoggerFactory.getLogger(GsdFileParser.class);
    private static List<String> _WARNING_LIST = new ArrayList<String>();

    private Integer _moduleNo = 0;

    /**
     * Default Constructor.
     */
    public GsdFileParser() {
        // Constructor
    }

    private void buildExtUserPrmData(@Nonnull final String line,
                                     @Nonnull final LineCounter lineCounter,
                                     @Nonnull final ParsedGsdFileModel parsedGsdFileModel,
                                     @Nonnull final BufferedReader br) throws IOException {
        final String[] lineParts = line.split("[\"]");
        assert lineParts.length > 1;
        final Integer index = GsdFileParser.gsdValue2Int(lineParts[0].split("=")[1]);
        final String text = lineParts[1].split(";")[0].trim();

        final ExtUserPrmData extUserPrmData = new ExtUserPrmData(parsedGsdFileModel, index, text);
        String tmpLine = br.readLine();
        while (!isLineParameter(tmpLine, "EndExtUserPrmData")) {
            lineCounter.count();
            tmpLine = tmpLine.trim();
            if (isLineParameter(tmpLine, "Prm_Text_Ref")) {
                final KeyValuePair extractKeyValue = extractKeyValue(tmpLine, lineCounter, br);
                extUserPrmData.setPrmTextRef(extractKeyValue.getIntValue());
            } else {
                extUserPrmData.buildDataTypeParameter(tmpLine);
            }
            tmpLine = br.readLine();
        }
        parsedGsdFileModel.setExtUserPrmData(extUserPrmData);
    }

    private void buildExtUserPrmDataConst(@Nonnull final String line,
                                          @Nonnull final LineCounter lineCounter,
                                          @Nonnull final AbstractGsdPropertyModel parsedGsdFileModel,
                                          @Nonnull final BufferedReader br) throws IOException {
        final KeyValuePair keyValuePair = extractKeyValue(line, lineCounter, br);
        parsedGsdFileModel.setExtUserPrmDataConst(keyValuePair);
    }

    private void buildExtUserPrmDataRef(@Nonnull final String line,
                                        @Nonnull final LineCounter lineCounter,
                                        @Nonnull final ParsedGsdFileModel parsedGsdFileModel,
                                        @Nonnull final AbstractGsdPropertyModel abstractGsdPropertyModel,
                                        @Nonnull final BufferedReader br) throws IOException {
        final KeyValuePair keyValuePair = extractKeyValue(line, lineCounter, br);
        final ExtUserPrmData eupd = parsedGsdFileModel.getExtUserPrmData(keyValuePair.getIntValue());
        final Integer index = keyValuePair.getIndex();
        if (eupd != null && index != null) {
            abstractGsdPropertyModel.setExtUserPrmDataDefault(eupd, index);
            abstractGsdPropertyModel.setExtUserPrmDataRef(keyValuePair);
        }
    }

    private void buildExtUserPrmDataRef(@Nonnull final String line,
                                        @Nonnull final LineCounter lineCounter,
                                        @Nonnull final ParsedGsdFileModel parsedGsdFileModel,
                                        @Nonnull final BufferedReader br) throws IOException {
        final KeyValuePair keyValuePair = extractKeyValue(line, lineCounter, br);
        final ExtUserPrmData eupd = parsedGsdFileModel.getExtUserPrmData(keyValuePair.getIntValue());
        final Integer index = keyValuePair.getIndex();
        if(eupd!=null && index!=null) {
            parsedGsdFileModel.setExtUserPrmDataDefault(eupd, index);
            parsedGsdFileModel.setExtUserPrmDataRef(keyValuePair);
        }
    }

    private void buildModule(@Nonnull final String line,
                             @Nonnull final LineCounter lineCounter,
                             @Nonnull final ParsedGsdFileModel parsedGsdFileModel,
                             @Nonnull final BufferedReader br) throws IOException {
        final String[] lineParts = line.split("[\";]");
        assert lineParts.length > 2;
        Integer moduleNo = null;
        final GsdModuleModel2 gsdModuleModel = createModule(lineCounter, br, lineParts);
        String tmpLine = br.readLine();
        while (!isLineParameter(tmpLine, "endmodule")) {
            tmpLine = tmpLine.trim();
            moduleNo =
                handleModuleLine(lineCounter,
                                 parsedGsdFileModel,
                                 br,
                                 moduleNo,
                                 gsdModuleModel,
                                 tmpLine);
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

    private void buildPrmText(@Nonnull final String line,
                              @Nonnull final LineCounter lineCounter,
                              @Nonnull final ParsedGsdFileModel parsedGsdFileModel,
                              @Nonnull final BufferedReader br) throws IOException {
        final KeyValuePair prmTextKeyValue = extractKeyValue(line, lineCounter, br);
        final Integer index = prmTextKeyValue.getIntValue();
        final PrmText prmText = new PrmText(index);
        String tmpLine = br.readLine();
        while (!isLineParameter(tmpLine, "EndPrmText")) {
            lineCounter.count();
            tmpLine = tmpLine.trim();
            final KeyValuePair prmItemKeyValue = extractKeyValue(tmpLine, lineCounter, br);
            prmText.setPrmTextItem(prmItemKeyValue);
            tmpLine = br.readLine();
        }
        parsedGsdFileModel.putPrmText(prmText);
    }

    private void buildSlotDefinition(@Nonnull final String line,
                                     @Nonnull final LineCounter lineCounter,
                                     @Nonnull final BufferedReader br) throws IOException {
        // (hrickens) [28.03.2011]:  buildSlotDefinition. If this information can be ignored.
        String tmpLine = line;
        while (!isLineParameter(tmpLine, "EndSlotDefinition")) {
            lineCounter.count();
            tmpLine = tmpLine.trim();
            tmpLine = br.readLine();
        }
    }

    private void buildUnitDiagArea(@Nonnull final String line,
                                   @Nonnull final LineCounter lineCounter,
                                   @Nonnull final BufferedReader br) throws IOException {
        // (hrickens) [28.03.2011]: buildUnitDiagArea. This information can be ignored.
        String tmpLine = line;
        while (!isLineParameter(tmpLine, "Unit_Diag_Area_End")) {
            lineCounter.count();
            tmpLine = tmpLine.trim();
            tmpLine = br.readLine();
        }

    }

    private void buildUnitDiagType(@Nonnull final String line,
                                   @Nonnull final LineCounter lineCounter,
                                   @Nonnull final BufferedReader br) throws IOException {
        // (hrickens) [28.03.2011]: buildUnitDiagType. This information can be ignored.
        String tmpLine = line;
        while (!isLineParameter(tmpLine, "EndUnitDiagType")) {
            lineCounter.count();
            tmpLine = tmpLine.trim();
            tmpLine = br.readLine();
        }
    }

    /**
     * @param lineCounter
     * @param br
     * @param lineParts
     * @return
     * @throws IOException
     */
    @Nonnull
    public GsdModuleModel2 createModule(@Nonnull final LineCounter lineCounter,
                                        @Nonnull final BufferedReader br,
                                        @Nonnull final String[] lineParts) throws IOException {
        final String name = lineParts[1].trim();
        final ArrayList<Integer> valueList = new ArrayList<Integer>();
        final String value = getValue(lineParts[2], lineCounter, br);
        GsdFileParser.addValues2IntList(value, valueList);
        final GsdModuleModel2 gsdModuleModel = new GsdModuleModel2(name, valueList);
        return gsdModuleModel;
    }

    /**
     * @param lineCounter
     * @param parsedGsdFileModel
     * @param moduleNo
     * @param tmpLine
     * @return
     */
    @CheckForNull
    public Integer extractModuleNo(@Nonnull final LineCounter lineCounter,
                                   @Nonnull final ParsedGsdFileModel parsedGsdFileModel,
                                   @Nonnull final String tmpLine) {
        try {
            return gsdValue2Int(tmpLine.split(";")[0].trim());
        } catch (final NumberFormatException e) {
            addWarning("Module parameter wrong at GSD File %s in line %s. Line: %s",
                       lineCounter,
                       parsedGsdFileModel.getName(),
                       tmpLine);
        }
        return null;
    }

    /**
     * @param lineCounter
     * @param parsedGsdFileModel
     * @param br
     * @param moduleNo
     * @param gsdModuleModel
     * @param tmpLine
     * @return
     * @throws IOException
     */
    // CHECKSTYLE OFF: CyclomaticComplexity
    @CheckForNull
    public Integer handleModuleLine(@Nonnull final LineCounter lineCounter,
                                    @Nonnull final ParsedGsdFileModel parsedGsdFileModel,
                                    @Nonnull final BufferedReader br,
                                    @Nullable final Integer moduleNumber,
                                    @Nonnull final GsdModuleModel2 gsdModuleModel,
                                    @Nullable final String tmpLine) throws IOException {
        lineCounter.count();
        Integer moduleNo = moduleNumber;
        // do nothing. Is a empty line or a comment;
        if (!(isLineParameter(tmpLine, ";") || tmpLine.isEmpty())) {
            if (isLineParameter(tmpLine, "Ext_Module_Prm_Data_Len")
                    || isLineParameter(tmpLine, "F_Ext_Module_Prm_Data_Len")) {
                extractKeyValue(tmpLine, lineCounter, br);
            } else if (isLineParameter(tmpLine, "Ext_User_Prm_Data_Const")
                    || isLineParameter(tmpLine, "F_Ext_User_Prm_Data_Const")) {
                final KeyValuePair extractKeyValue = extractKeyValue(tmpLine, lineCounter, br);
                gsdModuleModel.setExtUserPrmDataConst(extractKeyValue);
            } else if (isLineParameter(tmpLine, "Ext_User_Prm_Data_Ref")
                    || isLineParameter(tmpLine, "F_Ext_User_Prm_Data_Ref")) {
                buildExtUserPrmDataRef(tmpLine, lineCounter, parsedGsdFileModel, gsdModuleModel, br);
            } else if (isLineParameter(tmpLine, "Info_Text")) {
                final KeyValuePair extractKeyValue = extractKeyValue(tmpLine, lineCounter, br);
                gsdModuleModel.setProperty(extractKeyValue);
            } else if (isLineParameter(tmpLine, "F_ParamDescCRC")) {
                extractKeyValue(tmpLine, lineCounter, br);
                //set F_ParamDescCRC. This information can be ignored.
            } else if (isLineParameter(tmpLine, "F_IO_StructureDescCRC")) {
                extractKeyValue(tmpLine, lineCounter, br);
                //set F_IO_StructureDescCRC. This information can be ignored.
            } else {
                moduleNo = extractModuleNo(lineCounter, parsedGsdFileModel, tmpLine);
            }
        }
        return moduleNo;
    }
    // CHECKSTYLE ON: CyclomaticComplexity

    private boolean isLineParameter(@CheckForNull final String line, @Nonnull final String parameter) {
        return line != null && line.toLowerCase().startsWith(parameter.toLowerCase());
    }

    /**
     * @param br
     * @param parsedGsdFileModel
     * @return
     * @throws IOException
     */
    // CHECKSTYLE OFF: CyclomaticComplexity
    @Nonnull
    private ParsedGsdFileModel parse(@Nonnull final BufferedReader br,
                                     @Nonnull final ParsedGsdFileModel parsedGsdFileModel) throws IOException {
        String line;
        final LineCounter lineCounter = new LineCounter();
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
                buildUnitDiagType(line, lineCounter, br);
            } else if (isLineParameter(line, "SlotDefinition")) {
                buildSlotDefinition(line, lineCounter, br);
            } else if (isLineParameter(line, "Unit_Diag_Area")) {
                buildUnitDiagArea(line, lineCounter, br);
            } else if (isLineParameter(line, "X_Unit_Diag_Area")) {
                buildUnitDiagArea(line, lineCounter, br);
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
    // CHECKSTYLE ON: CyclomaticComplexity

    @Nonnull
    public ParsedGsdFileModel parse(@Nonnull final GSDFileDBO gsdFileDBO) throws IOException {
        StringReader sr = new StringReader(gsdFileDBO.getGSDFile());
        BufferedReader br = new BufferedReader(sr);
        try {
            final ParsedGsdFileModel parsedGsdFileModel = new ParsedGsdFileModel(gsdFileDBO);
            return parse(br, parsedGsdFileModel);
        } finally {
            br.close();
            br = null;
            sr.close();
            sr = null;
        }
    }

    public static void addValues2IntList(@Nonnull final String value, @Nonnull final List<Integer> valueList) {
        final String[] values = value.split(",");
        for (final String val : values) {
            valueList.add(GsdFileParser.gsdValue2Int(val));
        }
    }

    private static void addWarning(@Nonnull final String message, @Nonnull final Object... parameter) {
        final String warning = String.format(message, parameter);
        LOG.warn(warning);
        _WARNING_LIST.add(warning);
    }

    @Nonnull
    private static KeyValuePair extractKeyValue(@Nonnull final String line,
                                                @Nonnull final LineCounter lineCounter,
                                                @Nonnull final BufferedReader br) throws IOException {
        try {
            final int keyEnd = line.indexOf('=');
            final String key = line.substring(0, keyEnd).trim();
            String value = line.substring(keyEnd + 1).trim();
            value = getValue(value, lineCounter, br);
            final KeyValuePair keyValuePair = new KeyValuePair(key, value);
            return keyValuePair;
        } catch (final IndexOutOfBoundsException e) {
            addWarning("Can't corret handle, at line %s, the Property: %s", lineCounter, line);
            throw e;
        }
    }

    @Nonnull
    public static List<String> getAndClearWarnings() {
        final ArrayList<String> arrayList = new ArrayList<String>(_WARNING_LIST);
        _WARNING_LIST.clear();
        return arrayList;
    }

    @Nonnull
    private static String getValue(@Nonnull final String startValue,
                                   @Nonnull final LineCounter lineCounter,
                                   @Nonnull final BufferedReader br) throws IOException {
        String value = startValue.trim();
        value = removeComment(value);
        while (value.endsWith("\\")) {
            lineCounter.count();
            final String readLine = br.readLine();
            if(readLine != null) {
                value = value.substring(0, value.length() - 1).trim()
                .concat(readLine.split(";")[0].trim());
            }
        }
        return value;
    }

    @Nonnull
    public static Integer gsdValue2Int(@Nonnull final String value) {
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

    @Nonnull
    public static String intList2HexString(@Nonnull final List<Integer> intList) {
        final StringBuilder sb = new StringBuilder();
        for (final Integer value : intList) {
            sb.append(String.format("0x%02X,", value));
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    @Nonnull
    private static String removeComment(@Nonnull final String value) {
        String tmpValue = value;
        if (tmpValue.contains(";")) {
            if (tmpValue.startsWith("\"")) {
                final int stringEnd = tmpValue.indexOf('"', 1);
                final int commentStart = tmpValue.indexOf(';', stringEnd);
                if (commentStart > 0) {
                    tmpValue = tmpValue.substring(0, commentStart);
                }
            } else {
                tmpValue = tmpValue.split(";")[0].trim();
            }
        }
        return tmpValue;
    }

    private static void setProperty(@Nonnull final String line,
                                    @Nonnull final LineCounter lineCounter,
                                    @Nonnull final ParsedGsdFileModel parsedGsdFileModel,
                                    @Nonnull final BufferedReader br) throws IOException {
        try {
            final KeyValuePair keyValuePair = extractKeyValue(line, lineCounter, br);
            parsedGsdFileModel.setProperty(keyValuePair);
        } catch (final NumberFormatException e) {
            addWarning("Can't corret handle from GSD File %s, at line %s, the Property: %s",
                       parsedGsdFileModel.getName(),
                       lineCounter,
                       line);
        } catch (final IndexOutOfBoundsException e) {
            addWarning("Can't corret handle from GSD File %s, at line %s, the Property: %s",
                       parsedGsdFileModel.getName(),
                       lineCounter,
                       line);
        }

    }
}
