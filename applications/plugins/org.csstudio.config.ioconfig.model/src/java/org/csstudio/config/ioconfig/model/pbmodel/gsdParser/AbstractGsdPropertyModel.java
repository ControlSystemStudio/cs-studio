/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * ModelClass that use GSD Files Properties.
 *
 * @author hrickens
 * @author $Author: bknerr $
 * @version $Revision: 1.7 $
 * @since 30.03.2011
 */
public abstract class AbstractGsdPropertyModel {

    private final SortedMap<Integer, Integer> _gsdExtUserPrmDataConstMap;
    private final SortedMap<Integer, KeyValuePair> _gsdExtUserPrmDataRefMap;
    private final Map<String, List<Integer>> _intArrayValueMap;
    private final Map<String, Integer> _intergerValueMap;
    private final Map<String, String> _stringValueMap;

    /**
     * Constructor.
     */
    public AbstractGsdPropertyModel() {
        _stringValueMap = new HashMap<String, String>();
        _intergerValueMap = new HashMap<String, Integer>();
        _intArrayValueMap = new HashMap<String, List<Integer>>();
        _gsdExtUserPrmDataConstMap = new TreeMap<Integer, Integer>();
        _gsdExtUserPrmDataRefMap = new TreeMap<Integer, KeyValuePair>();
    }

    /**
     * @param intValue
     * @return
     */
    @CheckForNull
    public abstract ExtUserPrmData getExtUserPrmData(@Nonnull Integer intValue);

    @Nonnull
    public List<Integer> getExtUserPrmDataConst() {
        final List<Integer> valueList = new ArrayList<Integer>();
        if (!_gsdExtUserPrmDataConstMap.isEmpty()) {
            for (int i = 0; i <= _gsdExtUserPrmDataConstMap.lastKey(); i++) {
                final Integer value = _gsdExtUserPrmDataConstMap.get(i);
                if (value != null) {
                    valueList.add(value);
                }
            }
        }
        return valueList;
    }

    @Nonnull
    public SortedMap<Integer, KeyValuePair> getExtUserPrmDataRefMap() {
        return _gsdExtUserPrmDataRefMap;
    }

    @Nonnull
    public Integer getGsdRevision() {
        Integer gsdRevision = getIntProperty("GSD_Revision");
        if(gsdRevision==null) {
            gsdRevision=-1;
        }
        return gsdRevision;
    }

    @CheckForNull
    public List<Integer> getIntArrayProperty(@Nonnull final String key) {
        return _intArrayValueMap.get(key);
    }

    @CheckForNull
    protected List<Integer> getIntListValue(@Nonnull final String propertty) {
        return _intArrayValueMap.get(propertty);
    }

    @CheckForNull
    public Integer getIntProperty(@Nonnull final String key) {
        return _intergerValueMap.get(key);
    }

    @CheckForNull
    protected Integer getIntValue(@Nonnull final String propertty) {
        return _intergerValueMap.get(propertty);
    }

    @Nonnull
    public String getModelName() {
        String modelName = getStringProperty("Model_Name");
        if(modelName==null) {
            modelName="Unkwon";
        }
        return modelName;
    }

    @Nonnull
    public String getRevision() {
        String revision = getStringProperty("Revision");
        if(revision==null) {
            revision="Unkwon";
        }
        return revision;
    }

    @CheckForNull
    public String getStringProperty(@Nonnull final String key) {
        return _stringValueMap.get(key);
    }

    @CheckForNull
    protected String getStringValue(@Nonnull final String propertty) {
        return _stringValueMap.get(propertty);
    }

    @Nonnull
    public String getVendorName() {
        String vendorName = getStringProperty("Vendor_Name");
        if(vendorName==null) {
            vendorName="Unkwon";
        }
        return vendorName;
    }

    public void setExtUserPrmDataConst(@Nonnull final KeyValuePair extUserPrmDataConst) {
        final String stringValue = extUserPrmDataConst.getValue();
        Integer index = extUserPrmDataConst.getIndex();
        if (stringValue.contains(",")) {
            final List<Integer> valueList = new ArrayList<Integer>();
            GsdFileParser.addValues2IntList(extUserPrmDataConst.getValue(), valueList);
            if(index==null) {
                index = 0;
            }
            for (final Integer value : valueList) {
                _gsdExtUserPrmDataConstMap.put(index++, value);
            }
        } else {
            _gsdExtUserPrmDataConstMap.put(index, extUserPrmDataConst.getIntValue());
        }
    }

    public void setExtUserPrmDataDefault(@Nonnull final ExtUserPrmData extUserPrmData, final int bytePos) {
        setExtUserPrmDataValue(extUserPrmData, bytePos, extUserPrmData.getDefault());
    }

    public void setExtUserPrmDataRef(@Nonnull final KeyValuePair extUserPrmDataRef) {
        _gsdExtUserPrmDataRefMap.put(extUserPrmDataRef.getIntValue(), extUserPrmDataRef);
    }

    private void setExtUserPrmDataValue(@Nonnull final ExtUserPrmData extUserPrmData,
                                        final int byteIndex,
                                        final int bitValue) {
        // TODO (hrickens) [21.04.2011]: Muss refactort werde da der gleiche code auch in AbstractGsdNodeEditor#setValue2BitMask verwendent wird.
        int val = bitValue;
        final int minBit = extUserPrmData.getMinBit();
        final int maxBit = extUserPrmData.getMaxBit();




        final int mask = ~((int) Math.pow(2, maxBit + 1) - (int) Math.pow(2, minBit));
        if (maxBit > 7 && maxBit < 16) {
            int modifyByteHigh = 0;
            int modifyByteLow = 0;
            if (_gsdExtUserPrmDataConstMap.containsKey(byteIndex)) {
                modifyByteHigh = _gsdExtUserPrmDataConstMap.get(byteIndex);
            }
            if (_gsdExtUserPrmDataConstMap.containsKey(byteIndex + 1)) {
                modifyByteLow = _gsdExtUserPrmDataConstMap.get(byteIndex + 1);
            }

            final int parseInt = modifyByteHigh * 256 + modifyByteLow;
            val = val << minBit;
            final int result = parseInt & mask | val;
            modifyByteLow = result % 256;
            modifyByteHigh = (result - modifyByteLow) / 256;
            _gsdExtUserPrmDataConstMap.put(byteIndex + 1, modifyByteHigh);
            _gsdExtUserPrmDataConstMap.put(byteIndex, modifyByteLow);
        } else {
            int modifyByte = 0;
            if (_gsdExtUserPrmDataConstMap.containsKey(byteIndex)) {
                modifyByte = _gsdExtUserPrmDataConstMap.get(byteIndex);
            }
            val = val << minBit;
            final int result = modifyByte & mask | val;
            _gsdExtUserPrmDataConstMap.put(byteIndex, result);
        }
    }

    private void setIntArrayValue(@Nonnull final KeyValuePair keyValuePair) {
        final List<Integer> valueList = new ArrayList<Integer>();
        GsdFileParser.addValues2IntList(keyValuePair.getValue(), valueList);
        _intArrayValueMap.put(keyValuePair.getKey(), valueList);
    }

    private void setIntegerValue(@Nonnull final KeyValuePair keyValuePair) {
        final Integer inValue = GsdFileParser.gsdValue2Int(keyValuePair.getValue());
        _intergerValueMap.put(keyValuePair.getKey(), inValue);
    }

    /**
     * Sets the property according to their type. (type-safe)
     */
    public void setProperty(@Nonnull final KeyValuePair keyValuePair) {
        final String value = keyValuePair.getValue();
        if (value.startsWith("\"")) {
            setStringValue(keyValuePair);
        } else if (value.contains(",")) {
            setIntArrayValue(keyValuePair);
        } else {
            setIntegerValue(keyValuePair);
        }
    }

    private void setStringValue(@Nonnull final KeyValuePair keyValuePair) {
        _stringValueMap.put(keyValuePair.getKey(), keyValuePair.getValue());
    }


}
