/**
 * 
 */
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * @author hrickens
 *
 */
public class ParsedGsdFileModel {

    private final String _name;
    private final Map<String, String> _stringValueMap;
    private final Map<String, Integer> _intergerValueMap;
    private final Map<String, List<Integer>> _intArrayValueMap;
    private final Map<Integer, PrmText> _prmTextMap;
    private final Map<Integer, GsdModuleModel2> _gsdModuleModelMap;
    private final Map<Integer, ExtUserPrmData> _gsdExtUserPrmData;
    

    /**
     * Constructor.
     * @param name
     */
    public ParsedGsdFileModel(@Nonnull String name) {
        _name = name;
        _stringValueMap = new HashMap<String, String>();
        _intergerValueMap = new HashMap<String, Integer>();
        _intArrayValueMap = new HashMap<String, List<Integer>>();
        _prmTextMap = new TreeMap<Integer, PrmText>();
        _gsdModuleModelMap = new TreeMap<Integer, GsdModuleModel2>();
        _gsdExtUserPrmData = new HashMap<Integer, ExtUserPrmData>();
    }

    @Nonnull 
    public String getName() {
        return _name;
    }

    @Nonnull
    public Map<Integer, PrmText> getPrmTextMap() {
        return _prmTextMap;
    }

    /**
     * @param prmText
     */
    public void putPrmText(@Nonnull PrmText prmText) {
        getPrmTextMap().put(prmText.getIndex(), prmText);
    }

    private void setIntArrayValue(@Nonnull KeyValuePair keyValuePair) {
        List<Integer> valueList = new ArrayList<Integer>();
        GsdFileParser.addValues2IntList(keyValuePair.getValue(), valueList);
        _intArrayValueMap.put(keyValuePair.getKey(), valueList);
    }

    private void setIntegerValue(@Nonnull KeyValuePair keyValuePair) {
        Integer inValue = GsdFileParser.gsdValue2Int(keyValuePair.getValue());
        _intergerValueMap.put(keyValuePair.getKey(), inValue);
    }

    /**
     * @param gsdModuleModel
     */
    public void setModule(@Nonnull GsdModuleModel2 gsdModuleModel) {
        GsdModuleModel2 put = _gsdModuleModelMap.put(gsdModuleModel.getModuleNumber(), gsdModuleModel);
        if(put!=null) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Sets the property according to their type. (type-safe)
     */
    public void setProperty(@Nonnull KeyValuePair keyValuePair) {
        String value = keyValuePair.getValue();
        if(value.startsWith("\"")) {
            setStringValue(keyValuePair);
        } else if(value.contains(",")) {
            setIntArrayValue(keyValuePair);
        } else {
            setIntegerValue(keyValuePair);
        }
    }

    private void setStringValue(@Nonnull KeyValuePair keyValuePair) {
        _stringValueMap.put(keyValuePair.getKey(), keyValuePair.getValue());
    }

    public void setExtUserPrmData(@Nonnull ExtUserPrmData extUserPrmData) {
        _gsdExtUserPrmData.put(extUserPrmData.getIndex(), extUserPrmData);
    }
    
    @CheckForNull
    public List<Integer> getUserPrmData(){
        List<Integer> l = getIntListValue("User_Prm_Data");
        return l; 
    }

    @CheckForNull
    List<Integer> getIntListValue(@Nonnull String propertty) {
        return _intArrayValueMap.get(propertty);
    }

    @CheckForNull
    String getStringValue(@Nonnull String propertty) {
        return _stringValueMap.get(propertty);
    }

    @CheckForNull
    Integer getIntValue(@Nonnull String propertty) {
        return _intergerValueMap.get(propertty);
    }

    @CheckForNull
    public GsdModuleModel2 getModule(@Nonnull Integer moduleNumber) {
        return _gsdModuleModelMap.get(moduleNumber);
    }
    
}
