/**
 * 
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
 * @author hrickens
 *
 */
public class ParsedGsdFileModel extends AbstractGsdPropertyModel {

    private final String _name;
    private final Map<Integer, PrmText> _prmTextMap;
    private final Map<Integer, GsdModuleModel2> _gsdModuleModelMap;
    private final Map<Integer, ExtUserPrmData> _gsdExtUserPrmData;
    private final SortedMap<Integer, Integer> _gsdExtUserPrmDataConst;
    

    /**
     * Constructor.
     * @param name
     */
    public ParsedGsdFileModel(@Nonnull String name) {
        _name = name;
        _prmTextMap = new TreeMap<Integer, PrmText>();
        _gsdModuleModelMap = new TreeMap<Integer, GsdModuleModel2>();
        _gsdExtUserPrmData = new HashMap<Integer, ExtUserPrmData>();
        _gsdExtUserPrmDataConst = new TreeMap<Integer, Integer>();
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

    /**
     * @param gsdModuleModel
     */
    public void setModule(@Nonnull GsdModuleModel2 gsdModuleModel) {
        GsdModuleModel2 put = _gsdModuleModelMap.put(gsdModuleModel.getModuleNumber(), gsdModuleModel);
        if(put!=null) {
            throw new IllegalArgumentException();
        }
    }

    public void setExtUserPrmData(@Nonnull ExtUserPrmData extUserPrmData) {
        _gsdExtUserPrmData.put(extUserPrmData.getIndex(), extUserPrmData);
    }

    public void setExtUserPrmDataConst(@Nonnull KeyValuePair keyValuePair) {
        String stringValue = keyValuePair.getValue();
        Integer index = keyValuePair.getIndex();
        if(stringValue.contains(",")) {
            List<Integer> valueList = new ArrayList<Integer>();
            GsdFileParser.addValues2IntList(keyValuePair.getValue(), valueList);
            for (Integer value : valueList) {
                _gsdExtUserPrmDataConst.put(index++, value);
            }
        } else {
            _gsdExtUserPrmDataConst.put(index, keyValuePair.getIntValue());
        }
    }
    
    public List<Integer> getExtUserPrmDataConst() {
        List<Integer> valueList = new ArrayList<Integer>();
        for (int i = 0; i < _gsdExtUserPrmDataConst.lastKey(); i++) {
            Integer value = _gsdExtUserPrmDataConst.get(i);
            if (value == null) {
                value=0;
            }
            valueList.add(value);
        }
        return valueList; 
    }
    
    @CheckForNull
    public List<Integer> getUserPrmData(){
        List<Integer> l = getIntListValue("User_Prm_Data");
        return l; 
    }

    @CheckForNull
    public GsdModuleModel2 getModule(@Nonnull Integer moduleNumber) {
        return _gsdModuleModelMap.get(moduleNumber);
    }
    
    
    
}
