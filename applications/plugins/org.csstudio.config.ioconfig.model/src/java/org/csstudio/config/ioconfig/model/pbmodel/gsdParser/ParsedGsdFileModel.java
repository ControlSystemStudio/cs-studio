/**
 * 
 */
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
    }

    /**
     * @return
     */
    @Nonnull 
    public String getName() {
        return _name;
    }

    /**
     * TODO 
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

    private void setIntegerValue(@Nonnull KeyValuePair keyValuePair) {
        Integer inValue = GsdFileParser.gsdValue2Int(keyValuePair.getValue());
        _intergerValueMap.put(keyValuePair.getKey(), inValue);
    }

    private void setIntArrayValue(@Nonnull KeyValuePair keyValuePair) {
        
        List<Integer> valueList = new ArrayList<Integer>();
        String[] values = keyValuePair.getValue().split(",");
        for (String val : values) {
            valueList.add(GsdFileParser.gsdValue2Int(val));
        }
        _intArrayValueMap.put(keyValuePair.getKey(), valueList);
    }

    /**
     * @param key
     * @param value
     */
    private void setStringValue(@Nonnull KeyValuePair keyValuePair) {
        _stringValueMap.put(keyValuePair.getKey(), keyValuePair.getValue());
    }

    /**
     * @param prmText
     */
    public void putPrmText(@Nonnull PrmText prmText) {
        _prmTextMap.put(prmText.getIndex(), prmText);
    }
    
}
